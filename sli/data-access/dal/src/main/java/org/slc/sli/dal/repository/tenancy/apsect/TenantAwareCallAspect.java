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

package org.slc.sli.dal.repository.tenancy.apsect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import org.slc.sli.dal.repository.tenancy.CurrentTenantHolder;
import org.slc.sli.dal.repository.tenancy.TenantCall;

/**
 * @author okrook
 *
 */
@Aspect
public class TenantAwareCallAspect {

    @Around("execution(@org.slc.sli.dal.repository.tenancy.TenantCall * *(..))")
    public void switchToTenantCall(ProceedingJoinPoint jp) throws Throwable {
        executeCall(jp, extractTenant(jp));
    }

    @Around("execution(@org.slc.sli.dal.repository.tenancy.SystemCall * *(..))")
    public void switchToSystemCall(ProceedingJoinPoint jp) throws Throwable {
        executeCall(jp, null);
    }

    private void executeCall(ProceedingJoinPoint jp, String tenant) throws Throwable {
        CurrentTenantHolder.push(tenant);

        try {
            jp.proceed();
        } finally {
            CurrentTenantHolder.pop();
        }
    }

    private String extractTenant(JoinPoint jp) {
        MethodSignature ms = (MethodSignature) jp.getSignature();

        String[] params = ms.getParameterNames();

        TenantCall tenantParam = ms.getMethod().getAnnotation(TenantCall.class);

        for (int i = 0; i < params.length; i++) {
            if (tenantParam.param().equals(params[i])) {
                return (String) jp.getArgs()[i];
            }
        }

        throw new IllegalStateException("Expected method parameter [" + tenantParam.param() + "] was not found");
    }
}
