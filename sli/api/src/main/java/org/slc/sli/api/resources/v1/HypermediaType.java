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


package org.slc.sli.api.resources.v1;

import javax.ws.rs.core.MediaType;


/**
 * Class of constants representing transmission types.
 * 
 * @author kmyers
 *
 */
public class HypermediaType {
    
    /**
     * SLC supported formats.
     */
    public static final String XML = MediaType.APPLICATION_XML;
    public static final String JSON = MediaType.APPLICATION_JSON;
    public static final String VENDOR_SLC_JSON = "application/vnd.slc+json";
    public static final String VENDOR_SLC_XML = "application/vnd.slc+xml";
    public static final String VENDOR_SLC_LONG_JSON = "application/vnd.slc.full+json";
    public static final String VENDOR_SLC_LONG_XML = "application/vnd.slc.full+xml";
}
