package mx.kenzie.craftscript.variable;

import mx.kenzie.craftscript.script.Context;
import mx.kenzie.foundation.*;
import mx.kenzie.foundation.instruction.AccessField;
import mx.kenzie.foundation.instruction.CallMethod;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static mx.kenzie.foundation.instruction.Instruction.*;

public abstract class MagicVariableContainer extends VariableContainer {

    private static final AtomicInteger counter = new AtomicInteger();
    protected final Map<String, Object> backer;

    protected MagicVariableContainer(Map<String, Object> container, Collection<Object> parameters) {
        super(container, parameters);
        this.backer = container;
    }

    public MagicVariableContainer() {
        super();
        this.backer = new HashMap<>();
    }

    public MagicVariableContainer(Map<String, Object> container) {
        super();
        this.backer = container;
    }

    public static MagicVariableContainer create(Class<? extends MagicVariableContainer> type) {
        try {
            return type.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addConstructors(PreClass builder, Type parent) {
        PreMethod method = builder.add(PreMethod.constructor(Modifier.PUBLIC, Map.class, Collection.class));
        method.line(methodVisitor -> {
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 2);
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, parent.internalName(), "<init>",
                "(Ljava/util/Map;Ljava/util/Collection;)V", false);
            methodVisitor.visitInsn(Opcodes.RETURN);
        });
        method = builder.add(PreMethod.constructor(Modifier.PUBLIC));
        method.line(methodVisitor -> {
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, parent.internalName(), "<init>",
                "()V", false);
            methodVisitor.visitInsn(Opcodes.RETURN);
        });
        method = builder.add(PreMethod.constructor(Modifier.PUBLIC, Map.class));
        method.line(methodVisitor -> {
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, parent.internalName(), "<init>",
                "(Ljava/util/Map;)V", false);
            methodVisitor.visitInsn(Opcodes.RETURN);
        });
    }

    public static String identifier(String... names) {
        long result = 1;
        for (String element : names) result = 31 * result + element.hashCode();
        return "MagicBox_" + Long.toHexString(result);
    }

    @SuppressWarnings("unused")
    public static Context makeMagic(Context context, VariableContainer container) {
        final VariableContainer parent = context.variables();
        if (parent instanceof VariableFinder) return context;
        else if (parent instanceof StructObject) return context;
        else if (container.getClass().isInstance(parent)) return context;
        return new Context(context.source(), context.manager(), container, context.data());
    }

    @SuppressWarnings("unused")
    public static Context makeMagic(Context context, String... names) throws Exception {
        final VariableContainer parent = context.variables();
        if (parent instanceof VariableFinder) return context;
        else if (parent instanceof StructObject) return context;
        final Class<? extends MagicVariableContainer> superType;
        if (parent instanceof MagicVariableContainer magic) superType = magic.getClass();
        else superType = MagicVariableContainer.class;
        final String name = "script." + identifier(names);
        Class<? extends MagicVariableContainer> ours;
        try {
            ours = (Class<? extends MagicVariableContainer>) Class.forName(name, true, Loader.getDefault());
            if (ours.isInstance(parent)) return context;
        } catch (ClassNotFoundException e) {
            ours = compile(superType, Loader.DEFAULT, names);
        }
        assert ours != null;
        final MagicVariableContainer container = ours.getConstructor(Map.class).newInstance(parent);
        return new Context(context.source(), context.manager(), container, context.data());
    }

    public static CallMethod.Stub getVariable(String name) {
        return METHOD.of(true, compileInterface(name), Object.class, name);
    }

    public static CallMethod.Stub setVariable(String name) {
        return METHOD.of(true, compileInterface(name), Object.class, name, Object.class);
    }

    private static Type compileInterface(String name) {
        final PreClass builder = new PreClass("var", name);
        builder.setInterface(true);
        final PreMethod set = builder.add(
            new PreMethod(Modifier.PUBLIC, Modifier.ABSTRACT, Object.class, name, Object.class));
        final PreMethod get = builder.add(new PreMethod(Modifier.PUBLIC, Modifier.ABSTRACT, Object.class, name));
        try {
            Class.forName(builder.type().getTypeName(), true, Loader.getDefault());
            return builder.type();
        } catch (ClassNotFoundException | NoClassDefFoundError ex) {
            return Type.of(builder.compile().load(Loader.DEFAULT));
        }
    }

    public static Type[] makeInterfaces(String... names) {
        final Type[] types = new Type[names.length];
        for (int i = 0; i < names.length; i++) types[i] = compileInterface(names[i]);
        return types;
    }

    public static void prepareMagicBoxFor(String... names) {
        try {
            Class.forName("script." + identifier(names), true, Loader.getDefault());
        } catch (ClassNotFoundException | NoClassDefFoundError ex) {
            compile(MagicVariableContainer.class, Loader.DEFAULT, names);
        }
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends MagicVariableContainer> compile(Class<? extends MagicVariableContainer> superType, Loader loader, String... names) {
        final String name = identifier(names);
        final PreClass builder = new PreClass("script", name);
        builder.setParent(Type.of(superType));
        builder.addInterfaces(makeInterfaces(names));
        addConstructors(builder, Type.of(superType));
        Arrays.sort(names, Comparator.comparingInt(String::hashCode));
        final PreField[] fields = new PreField[names.length];
        for (int i = 0; i < names.length; i++) {
            fields[i] = builder.add(new PreField(Modifier.PUBLIC, Object.class, names[i]));
        }
        for (final String var : names) {
            final PreMethod set = builder.add(
                new PreMethod(Modifier.PUBLIC, Object.class, var, Object.class));
            final AccessField.Stub stub = FIELD.of(builder, var, Object.class);
            set.line(visitor -> {
                stub.set(THIS, LOAD_VAR.object(1)).write(visitor);
                visitor.visitVarInsn(Opcodes.ALOAD, 1);
                visitor.visitInsn(Opcodes.ARETURN);
            });
            final PreMethod get = builder.add(new PreMethod(Modifier.PUBLIC, Object.class, var));
            get.line(RETURN.object(stub.get(THIS)));
        }
        final PreMethod put = builder.add(
            new PreMethod(Modifier.PUBLIC, Type.OBJECT, "put", String.class, Object.class));
        final PreMethod get = builder.add(new PreMethod(Modifier.PUBLIC, Type.OBJECT, "get", Object.class));
        put.line(visitor -> {
            final Label[] labels = new Label[names.length];
            final int[] keys = new int[labels.length];
            final Label end = new Label();
            for (int i = 0; i < labels.length; i++) {
                labels[i] = new Label();
                keys[i] = names[i].hashCode();
            }
            METHOD.of(String.class, int.class, "hashCode").get(LOAD_VAR.object(1)).write(visitor);
            visitor.visitLookupSwitchInsn(end, keys, labels);
            for (int i = 0; i < names.length; i++) {
                final AccessField.Stub stub = FIELD.of(builder, names[i], Object.class);
                visitor.visitLabel(labels[i]);
                stub.set(THIS, LOAD_VAR.object(2)).write(visitor);
                visitor.visitVarInsn(Opcodes.ALOAD, 2);
                visitor.visitInsn(Opcodes.ARETURN);
            }
            visitor.visitLabel(end);
            SUPER.of(superType, Type.OBJECT, "put", String.class, Object.class)
                .get(THIS, LOAD_VAR.object(1), LOAD_VAR.object(2)).write(visitor);
            visitor.visitInsn(Opcodes.ARETURN);
        });
        get.line(STORE_VAR.object(1, METHOD
            .of(Objects.class, String.class, "toString", Object.class)
            .getStatic(LOAD_VAR.object(1))));
        get.line(visitor -> {
            final Label[] labels = new Label[names.length];
            final int[] keys = new int[labels.length];
            final Label end = new Label();
            for (int i = 0; i < labels.length; i++) {
                labels[i] = new Label();
                keys[i] = names[i].hashCode();
            }
            METHOD.of(String.class, int.class, "hashCode").get(LOAD_VAR.object(1)).write(visitor);
            visitor.visitLookupSwitchInsn(end, keys, labels);
            for (int i = 0; i < names.length; i++) {
                final AccessField.Stub stub = FIELD.of(builder, names[i], Object.class);
                visitor.visitLabel(labels[i]);
                stub.get(THIS).write(visitor);
                visitor.visitInsn(Opcodes.ARETURN);
            }
            visitor.visitLabel(end);
            SUPER.of(superType, Type.OBJECT, "get", Object.class)
                .get(THIS, LOAD_VAR.object(1)).write(visitor);
            visitor.visitInsn(Opcodes.ARETURN);
        });
        return (Class<? extends MagicVariableContainer>) builder.compile().load(loader);
    }

    @Override
    public @Nullable Object put(String key, Object value) {
        return backer.put(key, value);
    }

    @Override
    public Object get(Object key) {
        return backer.get(key);
    }

}
