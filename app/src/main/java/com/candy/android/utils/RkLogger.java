package com.candy.android.utils;

/*
 * Copyright (C) 2016 Rikkeisoft Co., Ltd.
 */

import android.util.Log;

import com.candy.android.BuildConfig;

/**
 * API for sending log output.
 * This one support to show correctly function , class name and line of code .
 * Default:
 * - Logcat: enabled
 */

public class RkLogger {
    /**
     * Priority constant for the println method; use RkLog.v.
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use RkLog.d.
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use RkLog.i.
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the println method; use RkLog.w.
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the println method; use RkLog.e.
     */
    public static final int ERROR = 6;

    /**
     * Priority constant for the println method. use RkLog.wtf.
     */
    public static final int ASSERT = 7;

    private static final String DOT = ".";


    /**
     * Send an {@link #INFO} log message.
     *
     * @param identifier Used to identify the source of a log message.
     * @param message    The message you would like logged.
     */
    public static void i(final String identifier, final String message) {
        log(INFO, identifier, message, null);
    }

    /**
     * Send a {@link #DEBUG} log message.
     *
     * @param identifier Used to identify the source of a log message.
     * @param message    The message you would like logged.
     */

    public static void d(final String identifier, final String message) {
        log(DEBUG, identifier, message, null);
    }

    /**
     * Send a {@link #WARN} log message.
     *
     * @param identifier Used to identify the source of a log message.
     * @param message    The message you would like logged.
     */
    public static void w(final String identifier, final String message) {
        log(WARN, identifier, message, null);
    }

    public static void w(final String identifier, final String message, Throwable e) {
        log(WARN, identifier, message, e);
    }

    /**
     * Send a {@link #VERBOSE} log message.
     *
     * @param identifier Used to identify the source of a log message.
     * @param message    The message you would like logged.
     */

    public static void v(final String identifier, final String message) {
        log(VERBOSE, identifier, message, null);
    }

    /**
     * Send an {@link #ERROR} log message.
     *
     * @param identifier Used to identify the source of a log message.
     * @param message    The message you would like logged.
     */
    public static void e(final String identifier, final String message) {
        e(identifier, message, null);
    }

    public static void e(final String identifier, final String message, Throwable throwable) {
        log(ERROR, identifier, message, throwable);
    }

    public static void e(final String identifier, final int message) {
        log(ERROR, identifier, message + "", null);
    }

    public static void e(final int message) {
        log(ERROR, "", message + "", null);
    }

    public static void e(final String message) {
        log(ERROR, "", message + "", null);
    }


    /**
     * Send an {@link #ASSERT} log message.
     *
     * @param identifier Used to identify the source of a log message.
     * @param message    The message you would like logged.
     */

    public static void wtf(final String identifier, final String message) {
        log(ASSERT, identifier, message, null);
    }

    private static void log(int type, final String identifier, final String message, Throwable e) {
        if (BuildConfig.DEBUG) {
            Throwable stack = new Throwable().fillInStackTrace();
            StackTraceElement[] trace = stack.getStackTrace();
            String className = trace[2].getClassName();
            className = className.substring(className.lastIndexOf(DOT) + 1);
            String methodName = trace[2].getMethodName();
            String line = trace[2].getLineNumber() + "";
            String TAG = className + "#" + methodName + "#" + line;
            switch (type) {
                case VERBOSE:
                    Log.v(TAG, identifier + ": " + message, e);
                    break;
                case DEBUG:
                    Log.d(TAG, identifier + ": " + message, e);
                    break;
                case INFO:
                    Log.i(TAG, identifier + ": " + message, e);
                    break;
                case WARN:
                    Log.w(TAG, identifier + ": " + message, e);
                    break;
                case ERROR:
                    Log.e(TAG, identifier + ": " + message, e);
                    break;
                case ASSERT:
                    Log.wtf(TAG, identifier + ": " + message, e);
                    break;
                default:
                    break;
            }
        }
    }
}
