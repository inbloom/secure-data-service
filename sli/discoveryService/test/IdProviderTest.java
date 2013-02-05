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


import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

/**
 * app/models/IdProvider unit tests
 */
public class IdProviderTest extends UnitTest {

    @Before
    public void setup() {
        Fixtures.deleteAllModels();
    }

    @Test
    public void createRetrieveAndDeleteIdP() {
        // Create a new IdProvider and save it
        new IdProvider("name", "idp.sli.wgen.net", "idp=SLI").save();

        // Retrieve the IdP with domain idp.sli.wgen.net
        IdProvider sli = IdProvider.find("byDomain", "idp.sli.wgen.net").first();

        // Test
        assertNotNull(sli);
        assertEquals("idp=SLI", sli.redirect);
        assertEquals( IdProvider.count(), 1 );

        sli.delete();
        assertEquals( IdProvider.count(), 0);
    }


}
