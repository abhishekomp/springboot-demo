package com.example.winttodo.utils;

import org.slf4j.Logger;

/**
 * LogUtils class
 *
 * @author : kjss920
 * @since : 2025-09-18, Thursday
 **/
public class LogUtils {
    /**
     * Logs the entry of a method with the class name and method name.
     * TLDR: Call this method at the beginning of any method to log its entry.
     * Example usage:
     * <pre>
     * public void someMethod() {
     *     LogUtils.logMethodEntry(logger, this);
     *     // method logic...
     * }
     * </pre>
     * `Thread.currentThread().getStackTrace()` returns an array representing the current call stack.
     * - `[0]` is always the `getStackTrace` method itself.
     * - `[1]` is the method where `getStackTrace()` was called.
     * - `[2]` is the caller of that method.
     * So, if you call `getStackTrace()[1].getMethodName()`, you get the name of the method where the code is running.
     * If you use `[2]`, you get the name of the method that called the current method.

     * **Use `[1]`** to log the current method's name.
     * **Use `[2]`** to log the caller's method name.
     * @param logger   the Logger instance to use for logging
     * @param instance the instance of the class where the method is located
     */
    public static void logMethodEntry(Logger logger, Object instance) {
        String className = instance.getClass().getSimpleName();
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        logger.info("Entering {}.{}", className, methodName);
    }
}
