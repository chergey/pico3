package com.picocontainer;

import java.util.Arrays;

import static org.junit.Assert.fail;

public class Utils {

    public static <T> void shouldThrow(Runnable action, String message, Class<?>... exceptions) {
        try {
            action.run();
        } catch (Exception e) {
            if (Arrays.stream(exceptions).anyMatch(f -> f.equals(e.getClass()))) {
                return;
            } else {
                fail(message);
            }
        }
        fail(message);
    }

    public static <T> void shouldThrow(Runnable action, String message, String exceptionMessage, Class<?>... exceptions) {
        try {
            action.run();
        } catch (Exception e) {
            if (Arrays.stream(exceptions).anyMatch(f -> f.equals(e.getClass())) && e.getMessage().equals(exceptionMessage)) {
                return;
            } else {
                fail(message);
            }
        }
        fail(message);
    }
}
