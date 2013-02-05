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


package org.slc.sli.validation.schema;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Representation of Documentation annotations.
 *
 * @author asaarela
 *
 */
public class Documentation extends Annotation {
    
    private String value;
    
    public Documentation(NodeList nodes) {
        if (nodes == null) {
            value = null;
            
        } else {
            StringBuilder builder = new StringBuilder();
            for (int docNodeIdx = 0; docNodeIdx < nodes.getLength(); ++docNodeIdx) {
                Node node = nodes.item(docNodeIdx);
                
                if (node instanceof Text) {
                    Text e = (Text) node;
                    builder.append(e.getNodeValue());
                }
            }
            value = builder.toString();
        }
    }
    
    @Override
    public Annotation.AnnotationType getType() {
        return Annotation.AnnotationType.DOCUMENTATION;
    }
    
    @Override
    public String toString() {
        return value;
    }
    
}
