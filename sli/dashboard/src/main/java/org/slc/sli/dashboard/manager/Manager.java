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


package org.slc.sli.dashboard.manager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A base class for all the data managers
 * @author agrebneva
 *
 */
public interface Manager {
    /**
     * Annotation to mark entity reference methods
     * @author agrebneva
     *
     */
    @Target(value = { ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface EntityMapping {
        String value();
    }
    
    /**
     * Annotation to mark entity reference methods
     * @author agrebneva
     *
     */
    @Target(value = { ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    public @interface EntityMappingManager {
    }
}
