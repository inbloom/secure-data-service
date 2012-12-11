/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.dal.migration.config;

import org.slc.sli.dal.migration.strategy.MigrationException;
import org.slc.sli.dal.migration.strategy.TransformStrategy;
import org.slc.sli.dal.migration.strategy.impl.AddStrategy;

/**
 * A list of all supported strategies and the classes that implement those strategies.
 * 
 * @author pghosh
 * @author kmyers
 */
public enum Strategy {
    
    ADD(AddStrategy.class);

    private Class<? extends TransformStrategy> implementingClass;

    private Strategy(Class<? extends TransformStrategy> implementingClass) {
        this.implementingClass = implementingClass;
    }
    
    public static TransformStrategy getNewImplementation(String strategyName) throws MigrationException {
        if (strategyName == null) {
            return null;
        }
        
        Strategy strategy = Strategy.valueOf(strategyName);
        
        if (strategy == null) {
            return null;
        }
        
        try {
            return strategy.implementingClass.newInstance();
        } catch (InstantiationException e) {
            throw new MigrationException(e);
        } catch (IllegalAccessException e) {
            throw new MigrationException(e);
        }
    }
}
