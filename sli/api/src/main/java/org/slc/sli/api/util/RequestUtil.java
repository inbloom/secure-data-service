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

package org.slc.sli.api.util;

import java.util.UUID;

/**
 * Holder for request information utilities.
 *
 * @author tshewchuk
 */
public class RequestUtil {

    private static ThreadLocal<UUID> currentRequestId = new ThreadLocal<UUID>() {
        @Override
        protected UUID initialValue()
        {
            return generateRequestId();
        }
    };

    /**
     * Get current request ID for this thread.
     *
     * @return Current request ID
     */
    public static UUID getCurrentRequestId() {
        return currentRequestId.get();
    }

    /**
     * Set current request ID for this thread.
     */
    public static void setCurrentRequestId() {
        RequestUtil.currentRequestId.set(generateRequestId());
    }

    /**
     * Generate a new request ID for this thread.
     *
     * @return Generated request ID
     */
    public static UUID generateRequestId() {
        return UUID.randomUUID();
    }

}
