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

package org.slc.sli.aggregation.mapreduce.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputCollector;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.slc.sli.aggregation.mapreduce.map.key.EmittableKey;
import org.slc.sli.aggregation.mapreduce.map.key.IdFieldEmittableKey;

/**
 * IDMapperTest
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { IDMapper.class, OutputCollector.class } )
public class IDMapperTest {

    String fields[] = { "data.element.id" };

    /**
     * testMapIdFieldKey Test mapping an arbitrary field to an ID.
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testMapIdFieldKey() throws Exception {

        BSONObject elem = new BasicBSONObject("id", 3697);
        BSONObject data = new BasicBSONObject("element", elem);
        final BSONObject entity = new BasicBSONObject("data", data);
        IDMapper mapper = new IDMapper(IdFieldEmittableKey.class, fields);

        OutputCollector<EmittableKey, BSONObject> collector = Mockito.mock(OutputCollector.class);

        PowerMockito.when(collector, "collect", Matchers.any(EmittableKey.class), Matchers.any(BSONObject.class)).thenAnswer(
            new Answer<BSONObject>() {

                @Override
                public BSONObject answer(InvocationOnMock invocation) throws Throwable {

                    Object args[] = invocation.getArguments();

                    assertNotNull(args);
                    assertEquals(args.length, 2);

                    assertTrue(args[0] instanceof IdFieldEmittableKey);
                    assertTrue(args[1] instanceof BSONObject);

                    IdFieldEmittableKey id = (IdFieldEmittableKey) args[0];
                    assertEquals(id.getIdField().toString(), "data.element.id");
                    Text idValue = id.getId();
                    assertEquals(Long.parseLong(idValue.toString()), 3697);

                    BSONObject e = (BSONObject) args[1];
                    assertEquals(e, entity);

                    return null;
                }
            }
        );

        Text id = new Text("data.element.id");
        mapper.map(id, entity, collector, null);
    }

    @Test
    public void testMapTenantAndIdKey() throws IOException {
    }

    @Test
    public void testGetLeaf() throws InstantiationException, IllegalAccessException {

        // root.body.profile.name.first = George
        BSONObject root = new BasicBSONObject();
        BSONObject body = new BasicBSONObject();
        BSONObject profile = new BasicBSONObject();
        BSONObject name = new BasicBSONObject();
        BSONObject first = new BasicBSONObject();

        first.put("first", "George");
        name.put("name", first);
        profile.put("profile", name);
        body.put("body", profile);
        root.put("root", body);

        IDMapper mapper = new IDMapper(IdFieldEmittableKey.class, fields);

        assertEquals(mapper.getLeaf(root, new Text("root.body.profile.name.first")).toString(), "George");
    }

}
