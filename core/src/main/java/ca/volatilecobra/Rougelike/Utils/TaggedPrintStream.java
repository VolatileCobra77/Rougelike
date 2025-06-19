package ca.volatilecobra.Rougelike.Utils;

import java.io.PrintStream;
import static ca.volatilecobra.Rougelike.Utils.ConsoleColors.*;

public class TaggedPrintStream extends PrintStream {
    private PrintStreamTypes type;
    public TaggedPrintStream(PrintStream original, PrintStreamTypes type) {
        super(original);
        this.type = type;
    }

    public void println(String x) {
        String caller = getCallerClassName();
        String color;

        switch (type) {
            case INFO -> color = GREEN;
            case WARN -> color = YELLOW;
            case ERR  -> color = RED;
            default   -> color = RESET;
        }

        super.println(color + caller + ": " + x + RESET);
    }

    private String getCallerClassName() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        // stack[0] = getStackTrace, stack[1] = getCallerClassName, stack[2] = println
        // stack[3] = the actual caller
        if (stack.length > 3) {
            return stack[3].getClassName().substring(stack[3].getClassName().lastIndexOf('.') + 1);
        }
        return "Unknown";
    }
}
