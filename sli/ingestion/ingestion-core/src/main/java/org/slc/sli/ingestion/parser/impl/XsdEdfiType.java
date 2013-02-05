/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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
 *
 * @author dduran
 *
 */
public final class XsdEdfiType implements EdfiType {

    final String name;
    final String type;
    final int numLists;

    public XsdEdfiType(String name, String type) {
        this.name = name;
        this.type = type;
        numLists = 0;
    }

    public XsdEdfiType(String name, String type, int numLists) {
        this.name = name;
        this.type = type;
        this.numLists = numLists;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int numLists() {
        return numLists;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "XsdTypeMeta [type=" + type + ", numLists=" + numLists + ", name=" + name + "]";
    }

}
