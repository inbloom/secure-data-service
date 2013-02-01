/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.dashboard.web.util;

import java.lang.annotation.Annotation;

import javax.validation.Valid;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Fix for 3.0 spring to validate @requestbody and @requestparam
 * Aspect provides validation for requestmapping annotated methods since Spring 3.0 provides modelattribute validation only
 * @author agrebneva
 *
 */
@Aspect
@Component
@Scope(value = "singleton")
public class ControllerInputValidatorAspect {

    private class DefaultStringValidatable {
        @SuppressWarnings("unused")
        @NoBadChars
        private String validatableString;

        public DefaultStringValidatable(String validatableString) {
            this.validatableString = validatableString;
        }
    }

    private Validator validator;

    /**
     * All methods annotation with RequestMapping
     */
    @Pointcut("execution(@org.springframework.web.bind.annotation.RequestMapping * *(..))")
    @SuppressWarnings("unused")
    private void controllerMethodInvocation() {
        //No Op
    }


    /**
     * Around for pointcut defined by controllerMethodInvocation
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("controllerMethodInvocation()")
    public Object aroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Annotation[][] annotations = methodSignature.getMethod().getParameterAnnotations();
        String[] paramNames = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < args.length; i++) {
            if (checkAnnotations(annotations[i])) {
                validateArg(args[i], paramNames[i]);
            }
        }
        return joinPoint.proceed(args);
    }

    /**
     * Find params to validate using annotations
     * @param paramAnnotations - annotations for method args
     * @return if marked to be validated
     */
    private boolean checkAnnotations(Annotation[] paramAnnotations) {
        boolean isValidate = false, isNonModelParam = false;
        for (Annotation annotation : paramAnnotations) {
            if (Valid.class.isInstance(annotation)) {
                isValidate = true;
            } else if (RequestParam.class.isInstance(annotation)
                       || PathVariable.class.isInstance(annotation)
                       || RequestBody.class.isInstance(annotation)) {
                isNonModelParam = true;
            }
        }
        return isValidate && isNonModelParam;
    }

    /**
     * Validate param using param specific validator and validate all strings using a blacklist validator
     * @param arg
     * @param argName
     */
    private void validateArg(Object arg, String argName) {
        BindingResult result = new BeanPropertyBindingResult(arg, argName);
        ValidationUtils.invokeValidator(getValidator(), arg, result);
        // force string validation for bad chars
        if (arg instanceof String) {
            ValidationUtils.invokeValidator(getValidator(), new DefaultStringValidatable((String) arg), result);
        }
        if (result.hasErrors()) {
            throw new HttpMessageConversionException("Invalid input parameter " + argName, new BindException(result));
        }
    }

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public Validator getValidator() {
        return validator;
    }
}
