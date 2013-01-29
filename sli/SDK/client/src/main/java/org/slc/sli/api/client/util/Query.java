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


package org.slc.sli.api.client.util;

import java.util.Map;

/**
 * Query parameters for requests to the SLI ReSTful API service. Parameters are encoded
 * and passed to the service with the request.
 *
 * @author asaarela
 */
public interface Query {

    /**
     * Get the query parameters associated with this query instance.
     *
     * @return map of query parameters.
     */
    public abstract Map<String, Object> getParameters();

}
