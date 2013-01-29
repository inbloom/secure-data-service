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


package org.slc.sli.api.client;

/**
 * 
 * Client representation of a URL link resource as provided by the SLI API ReSTful service. Each
 * link has a name (for example, 'getStudents') and an associated URL.
 * 
 * @author asaarela
 */
public interface Link {
    
    /** The resource name for the link */
    public static final String LINK_RESOURCE_KEY = "rel";
    
    /** Key to lookup the resource URI for a link */
    public static final String LINK_HREF_KEY = "href";
    
    /**
     * Get the resource name of this link.
     * 
     * @return String link resource name
     */
    public String getLinkName();
    
    /**
     * Get the link URL.
     * 
     * @return java.net.URL for this resource.
     */
    public java.net.URL getResourceURL();
}
