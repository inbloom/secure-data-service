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


package org.slc.sli.api.resources.v1.entity;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.slc.sli.api.config.BasicDefinitionStore;

/**
 * Unit tests for CompetencyLevelDescriptorResource
 *
 * @author chung
 *
 */
@RunWith(JUnit4.class)
public class CompetencyLevelDescriptorResourceTest {
    CompetencyLevelDescriptorResource competencyLevelDescriptorResource = new CompetencyLevelDescriptorResource(new BasicDefinitionStore());

    @Test
    public void testReadAll() {
        Response res = competencyLevelDescriptorResource.readAll(0, 0, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }

    @Test
    public void testRead() {
        Response res = competencyLevelDescriptorResource.read(null, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }

    @Test
    public void testCreate() {
        Response res = competencyLevelDescriptorResource.create(null, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }

    @Test
    public void testDelete() {
        Response res = competencyLevelDescriptorResource.delete(null, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }

    @Test
    public void testUpdate() {
        Response res = competencyLevelDescriptorResource.update(null, null, null, null);
        assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
    }
}
