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

package org.slc.sli.modeling.wadl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Documentation;
import org.slc.sli.modeling.rest.Grammars;
import org.slc.sli.modeling.rest.Include;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Option;
import org.slc.sli.modeling.rest.Param;
import org.slc.sli.modeling.rest.ParamStyle;
import org.slc.sli.modeling.rest.Representation;
import org.slc.sli.modeling.rest.Request;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.ResourceType;
import org.slc.sli.modeling.rest.Resources;
import org.slc.sli.modeling.rest.Response;
import org.slc.sli.modeling.wadl.reader.WadlReader;
import org.slc.sli.modeling.wadl.writer.WadlWriter;
import org.slc.sli.modeling.xdm.DmNode;
import org.slc.sli.modeling.xdm.DmText;

/**
 * JUnit test class for WadlWriter class.
 *
 * @author wscott
 *
 */
public class WadlWriterTest {

    private static final String GET = "GET";
    private static final String RESOURCE_ID = "resourceId";
    private static final String QUERY_TYPE = "queryType";
    private static final String RESOURCE_PATH = "resourcePath";
    private static final QName XS_STRING = new QName("xs:string");
    private static final String PARAM_ID = "paramId";
    private static final String PARAM_NAME = "paramName";
    private static final String PARAM_DEFAULT_VALUE = "paramDefaultValue";
    private static final String PARAM_OTHER_VALUE = "paramOtherValue";
    private static final String APPLICATION_JSON = "application/json";
    private static final String RESOURCES_BASE = "resourcesBase";
    private static final String INCLUDE_HREF = "includeHref";
    private static final String DOCTEXT = "doctext";
    private static final String DOCTITLE = "doctitle";

    Application app;

    @SuppressWarnings("serial")
    private static final Map<String, String> PREFIX_MAPPINGS = new HashMap<String, String>(1) {
        {
            put("xmlns", "http://www.slcedu.org/api/v1");
        }
    };

    private static final List<Documentation> EMPTY_DOCS = new ArrayList<Documentation>(0);
    private static final List<Param> EMPTY_PARAMS = new ArrayList<Param>(0);
    private static final List<Resource> EMPTY_RESOURCES = new ArrayList<Resource>(0);
    private static final List<Method> EMPTY_METHODS = new ArrayList<Method>(0);
    private static final List<Representation> EMPTY_REPRESENTATIONS = new ArrayList<Representation>(0);
    private static final List<ResourceType> EMPTY_RESOURCE_TYPES = new ArrayList<ResourceType>(0);

    @Before
    public void setupApplication() {
        // create sample documentation
        final DmNode docText = new DmText(DOCTEXT);
        // final DmNode docComment = new DmComment("doccomment");
        // final DmNode docElement = new DmElement(new QName("docelement"));
        // final DmNode docProcessingInstruction = new
        // DmProcessingInstruction("piTarget", "piData");
        @SuppressWarnings("serial")
        Documentation doc = new Documentation(DOCTITLE, null, new ArrayList<DmNode>(4) {
            {
                add(docText);
                // add(docComment);
                // add(docElement);
                // add(docProcessingInstruction);
            }
        });
        List<Documentation> docs = new ArrayList<Documentation>(1);
        docs.add(doc);

        // create a sample representation
        Representation representationJson = new Representation(null, null, APPLICATION_JSON,
                new ArrayList<String>(0), EMPTY_DOCS, EMPTY_PARAMS);
        List<Representation> representations = new ArrayList<Representation>(1);
        representations.add(representationJson);

        // create a query parameter
        Option optionDefault = new Option(PARAM_DEFAULT_VALUE, EMPTY_DOCS);
        Option optionOther = new Option(PARAM_OTHER_VALUE, EMPTY_DOCS);
        List<Option> options = new ArrayList<Option>(2);
        options.add(optionDefault);
        options.add(optionOther);

        Param param = new Param(PARAM_NAME, ParamStyle.QUERY, PARAM_ID, XS_STRING, PARAM_DEFAULT_VALUE, true, false,
                null, null, EMPTY_DOCS, options, null);
        List<Param> params = new ArrayList<Param>(1);
        params.add(param);

        // create a response
        List<String> statusCodes = new ArrayList<String>(1);
        statusCodes.add("200");

        Response response = new Response(statusCodes, EMPTY_DOCS, EMPTY_PARAMS, representations);
        List<Response> responses = new ArrayList<Response>(1);
        responses.add(response);

        //create a request
        Request request = new Request(EMPTY_DOCS, params, EMPTY_REPRESENTATIONS);

        //create a method
        Method method = new Method("methodId", GET, EMPTY_DOCS, request, responses);
        List<Method> methods = new ArrayList<Method>(1);
        methods.add(method);

        //create a resource
        Resource resource = new Resource(RESOURCE_ID, new ArrayList<String>(0), QUERY_TYPE, RESOURCE_PATH, EMPTY_DOCS,
                EMPTY_PARAMS, methods, EMPTY_RESOURCES, "MyClass");

        // create sample Resources
        List<Resource> resourceList = new ArrayList<Resource>(1);
        resourceList.add(resource);

        Resources resources = new Resources(RESOURCES_BASE, EMPTY_DOCS, resourceList);

        // create an Includes list
        List<Include> includes = new ArrayList<Include>(1);
        Include include = new Include(INCLUDE_HREF, EMPTY_DOCS);
        includes.add(include);

        // create a grammars section to hold the includes list
        Grammars grammars = new Grammars(EMPTY_DOCS, includes);

        // app = new Application(docs, grammars, resources, resourceTypes,
        // methods, representations, representations);
        app = new Application(docs, grammars, resources, EMPTY_RESOURCE_TYPES, EMPTY_METHODS, EMPTY_REPRESENTATIONS,
                EMPTY_REPRESENTATIONS);

    }

    @Test
    public void testWriteReadApplicationFile() throws FileNotFoundException {
        File file = new File("sample.wadl");
        WadlWriter.writeDocument(app, PREFIX_MAPPINGS, file);

        Application app = WadlReader.readApplication(file);

        // verify documentation
        List<Documentation> docs = app.getDocumentation();
        assertEquals(1, docs.size());
        assertEquals(1, docs.get(0).getContents().size());
        assertEquals(DOCTITLE, docs.get(0).getTitle());
        DmText docText = (DmText) docs.get(0).getContents().get(0);
        assertEquals(DOCTEXT, docText.getStringValue());

        //verify grammars include
        Grammars grammars = app.getGrammars();
        assertEquals(1, grammars.getIncludes().size());
        Include include = grammars.getIncludes().get(0);
        assertEquals(INCLUDE_HREF, include.getHref());

        // verify resoruces
        Resources resources = app.getResources();
        assertEquals(RESOURCES_BASE, resources.getBase());

        // verify resource
        assertEquals(1, app.getResources().getResources().size());
        Resource resource = app.getResources().getResources().get(0);
        assertEquals(RESOURCE_PATH, resource.getPath());

        // verify method
        assertEquals(1, resource.getMethods().size());
        Method method = resource.getMethods().get(0);
        assertEquals(GET, method.getVerb());

        // verify response
        assertEquals(1, method.getResponses().size());
        Response response = method.getResponses().get(0);
        assertEquals(1, response.getRepresentations().size());
        Representation representation = response.getRepresentations().get(0);
        assertEquals(APPLICATION_JSON, representation.getMediaType());

        // verify parameters
        Request request = method.getRequest();
        assertEquals(1, request.getParams().size());
        Param param = request.getParams().get(0);
        assertEquals(PARAM_NAME, param.getName());
        assertEquals(2, param.getOptions().size());

        file.deleteOnExit();
    }

}
