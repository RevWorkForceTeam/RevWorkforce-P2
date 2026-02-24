package com.rev.revworkforcep2.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppLogger {

    private final Logger logger;

    private AppLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public static AppLogger getLogger(Class<?> clazz) {
        return new AppLogger(clazz);
    }

    public void info(String message, Object... params) {
        logger.info(message, params);
    }

    public void warn(String message, Object... params) {
        logger.warn(message, params);
    }

    public void error(String message, Object... params) {
        logger.error(message, params);
    }

    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    public void debug(String message, Object... params) {
        logger.debug(message, params);
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }
}