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

package org.slc.sli.scaffold;

import org.apache.commons.io.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slc.sli.api.resources.generic.config.ApiNameSpace;
import org.slc.sli.api.resources.generic.config.ResourceEndPointTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles adding documentation to a resource document
 */
public class ResourceDocumentation {
    private static final String DEFAULT_RESOURCE_LOC = "/wadl/resources.json";
    private final Document doc;
    private final DocumentManipulator manipulator;
    private final Map<String, ResourceEndPointTemplate> resources = new HashMap<String, ResourceEndPointTemplate>();

    public ResourceDocumentation(final Document doc) throws IOException {
        this(doc, DEFAULT_RESOURCE_LOC);
    }

    public ResourceDocumentation(final Document doc, final String resourceLoc) throws IOException {
        this.doc = doc;
        this.manipulator = new DocumentManipulator();
        final String fileAsString = IOUtils.toString(super.getClass().getResourceAsStream(resourceLoc));
        final ApiNameSpace[] apiNameSpaces = new ObjectMapper().readValue(fileAsString, ApiNameSpace[].class);

        for (ApiNameSpace apiNameSpace : apiNameSpaces) {
            String[] versions = apiNameSpace.getNameSpace();

            for (String version : versions) {
                resources.putAll(getResourceMap(version, apiNameSpace.getResources()));
            }
        }
    }

    public void addDocumentation() throws IOException, XPathException {
        final NodeList topLevelResources = manipulator.getNodeList(this.doc, "//resource");

        for (int i = 0; i < topLevelResources.getLength(); i++) {
            final Node node = topLevelResources.item(i);
            final ResourceEndPointTemplate resourceTemplate = getResourceTemplate(node);
            if (resourceTemplate != null) {
                addTag(node, "doc", resourceTemplate.getDoc());
                addTag(node, "deprecatedVersion", resourceTemplate.getDeprecatedVersion());
                addTag(node, "deprecatedReason", resourceTemplate.getDeprecatedReason());
                addTag(node, "availableSince", resourceTemplate.getAvailableSince());
            }
        }
    }

    private void addTag(final Node node, final String key, final String value) {
        if (value != null) {
            final Node toAdd = this.doc.createElement(key);
            toAdd.setTextContent(value);
            node.appendChild(toAdd);
        }
    }

    private ResourceEndPointTemplate getResourceTemplate(final Node node) {
        return resources.get(getPath(node));
    }

    private String getPath(final Node node) {
        final Node pathNode = node.getAttributes().getNamedItem("path");
        if (pathNode != null) {
            String parentPath = getPath(node.getParentNode());
            String nodePath = pathNode.getNodeValue();
            return parentPath.isEmpty() ? nodePath : parentPath + "/" + nodePath;
        } else {
            return "";
        }
    }

    private Map<String, ResourceEndPointTemplate> getResourceMap(final String namespace,
                                                                 final List<ResourceEndPointTemplate> resources) {
        final Map<String, ResourceEndPointTemplate> resourceMap = new HashMap<String, ResourceEndPointTemplate>();

        for (final ResourceEndPointTemplate resource : resources) {
            resourceMap.put(namespace + resource.getPath(), resource);
            if (resource.getSubResources() != null && resource.getSubResources().size() > 0) {
                resourceMap.putAll(getResourceMap(namespace + resource.getPath(), resource.getSubResources()));
            }
        }

        return resourceMap;
    }
}