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


package org.slc.sli.dal.repository.tenancy;

import java.util.Stack;

/**
 * @author okrook
 *
 */
public final class CurrentTenantHolder {
    private static ThreadLocal<Stack<String>> tenants = new ThreadLocal<Stack<String>>();

    public static void push(String tenantId) {
        getCurrentStack().push(tenantId);
    }

    public static String pop() {
        return getCurrentStack().pop();
    }

    public static String getCurrentTenant() {
        return getCurrentStack().peek();
    }

    private static Stack<String> getCurrentStack() {
        Stack<String> tenantStack = tenants.get();

        if (tenantStack == null) {
            tenantStack = new Stack<String>();
            tenants.set(tenantStack);
        }

        return tenantStack;
    }
}
