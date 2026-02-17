package com.rev.revworkforcep2.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger =
            LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.rev.revworkforcep2.service.impl.*.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();

        String methodName = joinPoint.getSignature().toShortString();

        logger.info("Entering method: {}", methodName);

        try {
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;

            logger.info("Exiting method: {} | Execution time: {} ms",
                    methodName, executionTime);

            return result;

        } catch (Exception ex) {
            logger.error("Exception in method: {} | Message: {}",
                    methodName, ex.getMessage());
            throw ex;
        }
    }
}
