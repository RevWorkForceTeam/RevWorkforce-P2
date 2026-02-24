package com.rev.revworkforcep2.logging;

public final class LogConstants {

    private LogConstants() {

    }

    public static final String ENTRY =
            "Entering {}.{}() with arguments: {}";

    public static final String EXIT =
            "Exiting {}.{}() with response: {}";

    public static final String EXECUTION_TIME =
            "Execution time for {}.{}(): {} ms";

    public static final String SLOW_EXECUTION =
            "Slow execution detected in {}.{}() | {} ms";

    public static final String EXCEPTION =
            "Exception occurred in {}.{}(): {}";
}