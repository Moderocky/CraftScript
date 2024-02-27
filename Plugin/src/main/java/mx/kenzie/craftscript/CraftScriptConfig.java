package mx.kenzie.craftscript;

import mx.kenzie.craftscript.statement.IfStatement;
import mx.kenzie.craftscript.variable.VariableContainer;
import mx.kenzie.craftscript.variable.Wrapper;

import java.util.Map;
import java.util.Objects;

public class CraftScriptConfig extends VariableContainer {

    public boolean isWebEnabled() {
        final Object web = this.get("web");
        if (!(web instanceof Map<?, ?> map)) return false;
        return IfStatement.value(Wrapper.unwrap(map.get("enabled")));
    }

    public String webContentRoot() {
        final Object web = this.get("web");
        if (!(web instanceof Map<?, ?> map)) return null;
        return Objects.toString(Wrapper.unwrap(map.get("content_root")));
    }

}
