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


package org.slc.sli.test.exportTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmbeddedElement {
    public String name;

    public String template;
    public List<String> valuePlaceholders = new ArrayList<String>();
    public List<String> embeddedPlaceholders = new ArrayList<String>();
    public Map<String, EmbeddedElement> embeddedElementMap = new HashMap<String, EmbeddedElement>();

    public String query;
    public List<String> columnNames = new ArrayList<String>();
    public List<String> joinKeys = new ArrayList<String>();
}
