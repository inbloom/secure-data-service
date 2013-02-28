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

package org.slc.sli.dal.convert;

import org.slc.sli.domain.Entity;

/**
 * define superdoc converter interface that convert subdoc into and outside of the superdoc body
 * currently this is just for assessment and studentassessment superdoc transformation
 * 
 * @author Dong Liu dliu@wgen.net
 */

public interface SuperdocConverter {
    
    // convert the superdoc entity with subdoc outside the body into the superdoc entity body
    public void subdocToBodyField(Entity entity);
    
    // convert the superdoc entities with subdoc outside the body into the superdoc entity body
    public void subdocToBodyField(Iterable<Entity> entities);

    // convert the field of superdoc entity in the body to subdoc that outside of the entity body
    public void bodyFieldToSubdoc(Entity entity);
    
    // convert the field of superdoc entities in the body to subdoc that outside of the entity body
    public void bodyFieldToSubdoc(Iterable<Entity> entities);

}
