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


/**
 *
 */
package org.slc.sli.test.edfi.entities.meta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lchen
 *
 */
public class ParentMeta {

    public final String id;
    public boolean isMale;


    public ParentMeta(String id,boolean isMale) {

        this.id = id;
        this.isMale = isMale;
    }


    @Override
    public String toString() {
        return "ParentMeta [id=" + id + "]";
    }
}


