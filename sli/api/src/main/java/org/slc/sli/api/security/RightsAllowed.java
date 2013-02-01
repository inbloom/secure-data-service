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
package org.slc.sli.api.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.slc.sli.domain.enums.Right;

/**
 * Use to enable right checking on jax-rs endpoints.
 * <p>
 * First an authentication check takes place, which results in a 401 if the user isn't authenticated.
 * Next the rights are checked, which results in a 403 if the user doesn't have the necessary rights.
 * </p>
 * <p>
 * Example:
 * </p>
 * Check that the user has ADMIN_ACCESS right
 * <pre>
 *   {@literal @}GET
 *   {@literal @}RightsAllowed({Right.ADMIN_ACCESS})
 *   public Response getEntities() {
 *   ...
 *   }
 * </pre>
 * 
 *  Check that the user is authenticated, but don't check a specific Right
 * <pre>
 *   {@literal @}GET
 *   {@literal @}RightsAllowed(any=true)
 *   public Response getEntities() {
 *   ...
 *   }
 * </pre>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RightsAllowed {
    
    public Right[] value() default {};
    public boolean any() default false;
}


