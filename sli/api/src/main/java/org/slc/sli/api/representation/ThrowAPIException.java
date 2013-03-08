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

package org.slc.sli.api.representation;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

/**
 * Wrapper for throwing exceptions ( to insert additional logging, etc. )
 *
 * @author
 */
@Component
public class ThrowAPIException {

    public static void throwAccessDeniedException(String errorMessage, Collection<String> targetEdOrgs) {

        /*
         * AccesDenied ExceptionHandler will look for the inclusion of edOrgs
         */
        StringBuilder msg = new StringBuilder(errorMessage);
        if (targetEdOrgs != null) {
            msg.append(AccessDeniedExceptionHandler.ED_ORG_START);
            msg.append(Arrays.toString(targetEdOrgs.toArray()));
            msg.append(AccessDeniedExceptionHandler.ED_ORG_END);
        }

        throwAccessDeniedException(msg.toString());

    }

    public static void throwAccessDeniedException( String errorMessage, String targetEdOrg ) {
        StringBuilder msg = new StringBuilder(errorMessage);
        if (targetEdOrg != null) {
            msg.append(AccessDeniedExceptionHandler.ED_ORG_START);
            msg.append( targetEdOrg);
            msg.append(AccessDeniedExceptionHandler.ED_ORG_END);
        }

        throwAccessDeniedException(msg.toString());

    }
    public static void throwAccessDeniedException(String errorMessage) {
        throw new AccessDeniedException(errorMessage);

    }


}
