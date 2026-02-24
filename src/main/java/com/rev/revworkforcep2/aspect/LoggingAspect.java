package com.rev.revworkforcep2.aspect;

import com.rev.revworkforcep2.logging.AppLogger;
import com.rev.revworkforcep2.logging.LogConstants;
import com.rev.revworkforcep2.logging.LogMessageBuilder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final AppLogger logger =
            AppLogger.getLogger(LoggingAspect.class);

    @SuppressWarnings("unused")
    @Around("within(com.rev.revworkforcep2.service..*)")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // ENTRY LOG
        if (logger.isDebugEnabled()) {
            logger.debug(
                    LogConstants.ENTRY,
                    className,
                    methodName,
                    LogMessageBuilder.buildArguments(args)
            );
        }

        try {

            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;

            logger.info(
                    LogConstants.EXIT,
                    className,
                    methodName,
                    result
            );

            // EXECUTION TIME LOG
            logger.debug(
                    LogConstants.EXECUTION_TIME,
                    className,
                    methodName,
                    executionTime
            );

            // SLOW EXECUTION WARNING
            if (executionTime > 1000) {
                logger.warn(
                        LogConstants.SLOW_EXECUTION,
                        className,
                        methodName,
                        executionTime
                );
            }

            return result;

        } catch (Exception ex) {

            // EXCEPTION LOG (prints full stack trace)
            logger.error(
                    LogConstants.EXCEPTION,
                    className,
                    methodName,
                    ex.getMessage()
            );

            logger.error("Stack trace:", ex);

            throw ex;
        }
    }
}