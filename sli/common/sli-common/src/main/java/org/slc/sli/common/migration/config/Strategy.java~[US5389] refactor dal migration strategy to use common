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

package org.slc.sli.common.migration.config;

/**
 * Created with IntelliJ IDEA.
 * User: pghosh
 * Date: 12/10/12
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Strategy {
    ADD("org.slc.sli.dal.migration.strategy.impl"),
    DEFAULT("none");

    private String className;

    private Strategy(String strategyType) {
        this.className = strategyType;
    }

    public String getClassName() {
        return this.className;
    }

    public static Strategy fromString(String className) {
        if (className != null) {
            for (Strategy strategy : Strategy.values()) {
                if (className.equalsIgnoreCase(strategy.className)) {
                    return strategy;
                }
            }
        }
        return null;
    }
}
