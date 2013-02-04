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


package test.camel.support.stax;

import javax.xml.stream.Location;

/**
 * @author okrook
 *
 */
public class EdFiEntity {
    private final int lineNumber;
    private final int columnNumber;
    private final Object entity;

    public EdFiEntity(Location location, Object entity) {
        this.lineNumber = location.getLineNumber();
        this.columnNumber = location.getColumnNumber();
        this.entity = entity;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public Object getEntity() {
        return entity;
    }
}
