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
 package org.slc.sli.api.client.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.MessageProcessingException;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.Link;
import org.slc.sli.api.client.RESTClient;
import org.slc.sli.api.client.SLIClient;
import org.slc.sli.api.client.SLIClientException;
import org.slc.sli.api.client.constants.ResourceNames;

/**
 * JUnit for BasicClient
 *
 * @author nbrown
 *
 */
public class BasicClientTest {
    private RESTClient restClient = mock(RESTClient.class);
    private SLIClient underTest = new BasicClient(restClient);
    private ObjectMapper mapper = new ObjectMapper();
    private Map<String, Object> studentData = new HashMap<String, Object>();
    private Map<String, Object> studentDataWithType = new HashMap<String, Object>();
    private Map<String, Object> resourceWithLinks = new HashMap<String, Object>();

    @Before
    public void init() throws MalformedURLException {
        when(restClient.getBaseURL()).thenReturn("http://baseURL");
        studentData.put("id", "42");
        studentData.put("name", "River Tam");

        studentDataWithType.putAll(studentData);
        studentDataWithType.put("entityType", ResourceNames.STUDENTS);

        List<Link> links = new ArrayList<Link>();
        links.add(new BasicLink("Google", new URL("http://www.google.com")));
        resourceWithLinks.put(Entity.LINKS_KEY, links);
    }

    @Test
    public void testCreate() throws IOException, URISyntaxException, SLIClientException {
        Entity e = new GenericEntity(ResourceNames.STUDENTS, studentData);
        Response r = mock(Response.class);
        when(r.getStatus()).thenReturn(201);
        when(r.getHeader("Location")).thenReturn("http://baseURL/students/42");
        when(restClient.postRequest(any(URL.class), anyString())).thenReturn(r);
        assertEquals("42", underTest.create(e));

        Entity e2 = new GenericEntity("", studentData);
        assertEquals("42", underTest.create(e2, "students"));
        assertEquals("42", underTest.create(e2, "/students"));
        assertEquals("42", underTest.create(e2, "http://baseURL/students"));

        verify(restClient, times(4)).postRequest(new URL("http://baseURL/students"),
                mapper.writeValueAsString(studentData));

        Entity e3 = new GenericEntity("", resourceWithLinks);
        assertEquals(1, e3.getLinks().size());
        Set<String> keySet = e3.getLinkMap().keySet();
        assertEquals(1, keySet.size());
        for (String key : keySet) {
            assertEquals("Google", key);
            assertEquals("http", e3.getLinkMap().get(key).getProtocol());
            assertEquals("www.google.com", e3.getLinkMap().get(key).getHost());
        }
    }

    @Test
    public void testRead() throws MessageProcessingException, URISyntaxException, IOException, SLIClientException {
        Response r = mock(Response.class);
        when(r.getStatus()).thenReturn(200);
        when(r.readEntity(String.class)).thenReturn(mapper.writeValueAsString(studentDataWithType));
        when(restClient.getRequest(new URL("http://baseURL/students/42"))).thenReturn(r);
        when(restClient.getRequest(new URL("http://baseURL/students?limit=1"))).thenReturn(r);
        assertEquals(Arrays.asList(new GenericEntity(ResourceNames.STUDENTS, studentData)),
                underTest.read("/students/42"));
        assertEquals(Arrays.asList(new GenericEntity(ResourceNames.STUDENTS, studentData)),
                underTest.read("http://baseURL/students/42"));
        List<Entity> studentList = new ArrayList<Entity>();
        underTest.read(studentList, ResourceNames.STUDENTS, BasicQuery.Builder.create().maxResults(1).build());
        assertEquals(Arrays.asList(new GenericEntity(ResourceNames.STUDENTS, studentData)), studentList);

    }

    @Test
    public void testUpdate() throws URISyntaxException, IOException, MessageProcessingException, SLIClientException {
        Entity e = new GenericEntity(ResourceNames.STUDENTS, studentData);
        Response r = mock(Response.class);
        when(r.getStatus()).thenReturn(204);
        when(restClient.putRequest(any(URL.class), anyString())).thenReturn(r);
        underTest.update(e);
        verify(restClient).putRequest(new URL("http://baseURL/students/42"), mapper.writeValueAsString(studentData));
    }

    @Test
    public void testDelete() throws MalformedURLException, URISyntaxException, SLIClientException {
        Entity e = new GenericEntity(ResourceNames.STUDENTS, studentData);
        Response r = mock(Response.class);
        when(r.getStatus()).thenReturn(204);
        when(restClient.deleteRequest(any(URL.class))).thenReturn(r);
        underTest.delete(e);
        underTest.delete(ResourceNames.STUDENTS, "42");
        verify(restClient, times(2)).deleteRequest(new URL("http://baseURL/students/42"));
    }
}
