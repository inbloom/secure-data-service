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
package org.slc.sli.bulk.extract.files.metadata;

import java.util.List;

/** ApiNameSpace class used to read namespace object from api/resource.json, mainly to retrieve api version in bulk extract.
 * @author tke
 *
 */
public class ApiNameSpace {

    private String[] nameSpace;
    private List<ResourceEndPointTemplate> resources;

    /**
     * get name space.
     * @return all the name spaces
     */
    public String[] getNameSpace() {
        String[] copy = new String[nameSpace.length];
        System.arraycopy(nameSpace, 0, copy, 0, nameSpace.length);

        return copy;
    }

    /**
     * set namespace.
     * @param nameSpace namespace to be set
     */
    public void setNameSpace(String[] nameSpace) {
        String[] copy = new String[nameSpace.length];
        System.arraycopy(nameSpace, 0, copy, 0, nameSpace.length);

        this.nameSpace = copy;
    }

    /**
     * get resources.
     * @return a list of all resources
     */
    public List<ResourceEndPointTemplate> getResources() {
        return resources;
    }

    /**
     * set resources.
     * @param resources resources to be set.
     */
    public void setResources(List<ResourceEndPointTemplate> resources) {
        this.resources = resources;
    }
}
