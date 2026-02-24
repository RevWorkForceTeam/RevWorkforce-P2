package com.rev.revworkforcep2.logging;

import java.util.Arrays;
import java.util.stream.Collectors;

public class LogMessageBuilder {

    private static final int MAX_ARG_LENGTH = 200;

    private LogMessageBuilder() {
        // Prevent instantiation
    }

    public static String buildArguments(Object[] args) {

        if (args == null || args.length == 0) {
            return "No arguments";
        }

        return Arrays.stream(args)
                .map(LogMessageBuilder::safeToString)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private static String safeToString(Object obj) {

        if (obj == null) {
            return "null";
        }

        String value = obj.toString();

        // Limit very large log output
        if (value.length() > MAX_ARG_LENGTH) {
            return value.substring(0, MAX_ARG_LENGTH) + "...";
        }

        return value;
    }
}