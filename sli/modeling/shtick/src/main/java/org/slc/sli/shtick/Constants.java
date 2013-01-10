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

package org.slc.sli.shtick;

/**
 * Describe the names of fields that are assumed to exist in the resource representation.
 */
public final class Constants {
    
    public static final String ENTITY_BODY_KEY = "body";
    public static final String ENTITY_LINKS_KEY = "links";
    public static final String ENTITY_METADATA_KEY = "metaData";
    
    public static final String ENTITY_TYPE_KEY = "entityType";
    
    /** Key to locate 'links' field of the Entity. */
    public static final String LINKS_KEY = "links";
    
    /** Key to locate the Entity's id field */
    public static final String ENTITY_ID_KEY = "id";
    
    /** The resource name for the link */
    public static final String LINK_RESOURCE_KEY = "rel";
    
    /** Key to lookup the resource URI for a link */
    public static final String LINK_HREF_KEY = "href";
}
