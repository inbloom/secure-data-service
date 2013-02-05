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


package org.slc.sli.common.util.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Converts username into a string that Logback can use.
 *
 * @author smelody
 *
 */
public class UserConverter extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent event) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            Object oPrincipal = auth.getPrincipal();
            String principal = "";
            if (oPrincipal != null) {
                principal = oPrincipal.toString();
            }
            return principal;

        } else {
            //calling getContext when there is no context creates a new security context
            //ThreadLocal that's never cleaned up otherwise
            SecurityContextHolder.clearContext();
        }
        return "NO_USER";
    }
}
