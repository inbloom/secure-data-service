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

package org.slc.sli.dashboard.unit.entity;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.Link;
import org.slc.sli.api.client.impl.BasicLink;
import org.slc.sli.dashboard.entity.GenericEntity;

/**
 * Unit Test for GenericEntity
 * @author joechung
 *
 */
public class GenericEntityTest {

    @Test
    public void testLinkMap() throws MalformedURLException {
        Map<String, Object> resourceWithLinks = new HashMap<String, Object>();
        List<Link> links = new ArrayList<Link>();
        links.add(new BasicLink("Google", new URL("http://www.google.com")));
        resourceWithLinks.put(Entity.LINKS_KEY, links);

        Entity entity = new GenericEntity(resourceWithLinks);
        assertEquals(1, entity.getLinks().size());
        Set<String> keySet = entity.getLinkMap().keySet();
        assertEquals(1, keySet.size());
        for (String key : keySet) {
            assertEquals("Google", key);
            assertEquals("http", entity.getLinkMap().get(key).getProtocol());
            assertEquals("www.google.com", entity.getLinkMap().get(key).getHost());
        }
    }
}
