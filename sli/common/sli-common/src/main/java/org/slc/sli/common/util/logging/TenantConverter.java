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

import org.slc.sli.common.util.tenantdb.TenantContext;

/**
 * Converter to provide tenant id set in current thread.
 *
 * @author dduran
 *
 */
public class TenantConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        String tenantId = TenantContext.getTenantId();
        if (tenantId != null) {
            return tenantId;
        }
        return "NO_TENANT";
    }

}
