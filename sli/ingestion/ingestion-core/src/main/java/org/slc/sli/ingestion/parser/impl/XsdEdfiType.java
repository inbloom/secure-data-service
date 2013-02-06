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
package org.slc.sli.ingestion.parser.impl;

import org.slc.sli.ingestion.parser.EdfiType;

/**
 * Basic implementation of EdfiType
 * @author dduran
 *
 */
public final class XsdEdfiType implements EdfiType {

    final String name;
    final String type;
    final boolean isList;

    public XsdEdfiType(String name, String type) {
        this.name = name;
        this.type = type;
        isList = false;
    }

    public XsdEdfiType(String name, String type, boolean isList) {
        this.name = name;
        this.type = type;
        this.isList = isList;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean isList() {
        return isList;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "<name=" + name + ", type=" + type + ", isList=" + isList + ">";
    }

}
