package mx.kenzie.craftscript.variable;

import mx.kenzie.craftscript.kind.CustomKind;
import mx.kenzie.craftscript.kind.Kind;

import java.util.Iterator;
import java.util.regex.Pattern;

public class StructObject extends VariableContainer {

    public static final Pattern TYPE_NAME = Pattern.compile("#[a-zA-Z_\\-.][a-zA-Z0-9_\\-.]*");
    private static final CustomKind KIND = new CustomKind();

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
            builder.append(value == this ? "this" : value);
            if (!iterator.hasNext()) return builder.append(']').toString();
            builder.append(',').append(' ');
        }
        return builder.append(']').toString();
    }

    public Kind<StructObject> getKind() {
        return KIND;
    }

}
