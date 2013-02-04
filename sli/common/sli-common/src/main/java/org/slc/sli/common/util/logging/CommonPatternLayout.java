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

import ch.qos.logback.classic.PatternLayout;

/**
 * Provides custom patterns for including additional values in our log
 * statements.
 *
 * @author smelody
 *
 */
public class CommonPatternLayout extends PatternLayout {
    // TODO - shouldn't this be in our config files in 1 place?
    private static final String PATTERN = "%date{dd MMM yyyy HH:mm:ss.SSSZ} %-5level [%thread] %logger{10} [%user] - %msg%n";

    // Registers custom converters
    static {
        PatternLayout.defaultConverterMap.put("user", UserConverter.class.getName());
    }

    /**
     * Constructor.
     */
    public CommonPatternLayout() {
        setPattern(PATTERN);
    }

}
