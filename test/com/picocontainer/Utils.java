package com.picocontainer;

import java.util.Arrays;

import static org.junit.Assert.fail;

public class Utils {

    public static void shouldThrow(Runnable action, Class<?>... exceptions) {
      shouldThrow(action, "should have barfed!", exceptions);
    }


    public static void shouldThrow(Runnable action, String message, Class<?>... exceptions) {
        try {
            action.run();
        } catch (Throwable e) {
            if (Arrays.stream(exceptions).anyMatch(f -> f.equals(e.getClass()))) {
                return;
            } else {
                fail(message);
            }
        }
        fail(message);
    }

    public static void shouldThrow(Runnable action, String message, String exceptionMessage, Class<?>... exceptions) {
        try {
            action.run();
        } catch (Throwable e) {
            if (Arrays.stream(exceptions).anyMatch(f -> f.equals(e.getClass())) && e.getMessage().equals(exceptionMessage)) {
                return;
            } else {
                fail(message);
            }
        }
        fail(message);
    }
}
