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


package org.slc.sli.ingestion.util;

import java.util.Map;

import org.springframework.core.io.Resource;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;



/**
 *
 * Utility class which provides an XSD Resource from an XML IngestionFileEntry.
 *
 * @author tshewchuk
 *
 */
public class XsdSelector {

    private Map<String, Resource> xsdList;

    /**
    * Provides an XSD Resource from an XML IngestionFileEntry.

    * @param xmlFile
    *        IngestionFileEntry representing the XML file.
    *
    * @return Resource
    *         Resource representing the XSD file schema for the XML file,
    *         or null if no matching schema can be found.
    */
    public Resource provideXsdResource(IngestionFileEntry xmlFile) {
        Resource xsdResource = xsdList.get(xmlFile.getFileType().getName());
        return xsdResource;
    }

    public Map<String, Resource> getXsdList() {
        return xsdList;
    }

    public void setXsdList(Map<String, Resource> xsdList) {
        this.xsdList = xsdList;
    }

}
