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

package org.slc.sli.api.resources.v1.view;

import org.slc.sli.api.representation.EntityBody;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

/**
 * Manipulates a list of entities to add views
 *
 * @author srupasinghe
 */

public interface View {

    public List<EntityBody> add(List<EntityBody> entities, final String resource, final MultivaluedMap<String, String> queryParams);

}
