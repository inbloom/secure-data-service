package org.slc.sli.scaffold;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
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

public class ResourceDocumentation {
    private static final String DEFAULT_RESOURCE_LOC = "/wadl/v1_resources.json";
    private final Document doc;
    private final DocumentManipulator manipulator;
    private final Map<String, ResourceEndPointTemplate> resources;

    public ResourceDocumentation(final Document doc) throws IOException {
        this(doc, DEFAULT_RESOURCE_LOC);
    }

    public ResourceDocumentation(final Document doc, final String resourceLoc) throws IOException {
        this.doc = doc;
        this.manipulator = new DocumentManipulator();
        final String fileAsString = IOUtils.toString(super.getClass().getResourceAsStream(resourceLoc));
        final ApiNameSpace apiNameSpace = new ObjectMapper().readValue(fileAsString, ApiNameSpace.class);
        resources = getResourceMap(apiNameSpace.getNameSpace(), apiNameSpace.getResources());
    }

    public void addDocumentation() throws IOException, XPathException {
        final NodeList topLevelResources = manipulator.getNodeList(this.doc, "//resource");

        for (int i = 0; i < topLevelResources.getLength(); i++) {
            final Node node = topLevelResources.item(i);
            addDocTag(node);
            addDeprecations(node);
        }
    }

    private void addDocTag(final Node node) {
        final ResourceEndPointTemplate resource = getResourceTemplate(node);
        if (resource != null) {
            final String doc = resource.getDoc();
            if (doc != null) {
                final Node docElem = this.doc.createElement("doc");
                docElem.setTextContent(doc);
                node.appendChild(docElem);
            }
        }
    }

    private void addDeprecations(final Node node) {
        final ResourceEndPointTemplate resource = getResourceTemplate(node);
        if (resource != null) {
            final String deprecatedReason = resource.getDeprecatedReason();
            if (deprecatedReason != null) {
                final Node deprecatedReasonNode = this.doc.createElement("deprecatedReason");
                deprecatedReasonNode.setTextContent(deprecatedReason);
                node.appendChild(deprecatedReasonNode);
            }

            final String deprecatedVersion = resource.getDeprecatedVersion();
            if (deprecatedVersion != null) {
                final Node deprecatedVersionNode = this.doc.createElement("deprecatedVersion");
                deprecatedVersionNode.setTextContent(deprecatedVersion);
                node.appendChild(deprecatedVersionNode);
            }
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