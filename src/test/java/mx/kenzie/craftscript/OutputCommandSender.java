package mx.kenzie.craftscript;

import org.jetbrains.annotations.NotNull;

public class OutputCommandSender extends TestCommandSender {

    @Override
    public void sendMessage(@NotNull String message) {
        System.out.println(message);
    }

}
