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


package org.slc.sli.dal.convert;

/**
 * Provides an interface to convert database IDs into Strings that the Entity instances can use.
 *
 * @author Sean Melody <smelody@wgen.net>
 *
 */

public interface IdConverter {

    /**
     * Converts the given String ID into an ID suitable for persisting in the datastore.
     *
     * @param id
     * @return
     */
    public Object toDatabaseId(String id);

}
