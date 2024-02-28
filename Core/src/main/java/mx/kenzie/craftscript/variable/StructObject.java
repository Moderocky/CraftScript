package mx.kenzie.craftscript.variable;

import mx.kenzie.craftscript.kind.CustomKind;
import mx.kenzie.craftscript.kind.Kind;
import mx.kenzie.craftscript.utility.Container;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class StructObject extends VariableContainer implements Container {

    public static final Pattern TYPE_NAME = Pattern.compile("#[a-zA-Z_\\-.][a-zA-Z0-9_\\-.]*");
    private static final CustomKind KIND = new CustomKind();

    protected boolean frozen;

    public StructObject() {
        super(new HashMap<>(), Collections.emptyList());
    }

    public StructObject(Map<String, Object> object) {
        super(new HashMap<>(object), Collections.emptyList());
    }

    public StructObject(StructObject object) {
        super(new HashMap<>(object.container), Collections.emptyList());
        this.frozen = object.frozen;
    }

    public void freeze() {
        this.frozen = true;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void unfreeze() {
        this.frozen = false;
    }

    @Override
    public Object put(String key, Object value) {
        if (!frozen || this.containsKey(key)) return super.put(key, value);
        return null;
    }

    @Override
    public String toString() {
        if (this.isEmpty()) return "[]";
        final Iterator<Entry<String, Object>> iterator = this.entrySet().iterator();
        final StringBuilder builder = new StringBuilder();
        builder.append('[');
        while (iterator.hasNext()) {
            final Entry<String, Object> entry = iterator.next();
            final String key = entry.getKey();
            final Object value = entry.getValue();
            builder.append(key);
            builder.append('=');
            builder.append(Wrapper.unwrap(value) == this ? "this" : Kind.of(value).toStringTry(value));
            if (!iterator.hasNext()) return builder.append(']').toString();
            builder.append(',').append(' ');
        }
        return builder.append(']').toString();
    }

    public Kind<StructObject> getKind() {
        return KIND;
    }

}
