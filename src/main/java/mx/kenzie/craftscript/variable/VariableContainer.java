package mx.kenzie.craftscript.variable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class VariableContainer extends HashMap<String, Object> {

    public static final Pattern VAR_NAME = Pattern.compile("[a-zA-Z_\\-.][a-zA-Z0-9 _\\-.]*");

    public VariableContainer() {
        super();
    }

    public VariableContainer(Map<String, Object> container) {
        super(container);
    }

}
