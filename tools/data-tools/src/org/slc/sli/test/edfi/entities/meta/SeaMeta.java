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


package org.slc.sli.test.edfi.entities.meta;

import java.util.HashMap;
import java.util.Map;

public class SeaMeta {

    public final String id;
    
    public String programId;
    
    public Map<String, ProgramMeta> programs = new HashMap();
    
    public Map<String, ESCMeta> escs = new HashMap<String, ESCMeta>();
    
    public SeaMeta(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "SeaMeta [id=" + id + "]";
    }

}
