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
package org.slc.sli.ingestion.parser.impl;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 *
 * @author dduran
 *
 */
public class InterchangeStudentParentTest {

    @Test
    public void testStudent() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentParent.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeStudentParent/Student.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentParent/Student.expected.json");

        EntityTestHelper.parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testParent() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentParent.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeStudentParent/Parent.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentParent/Parent.expected.json");

        EntityTestHelper.parseAndVerify(schema, inputXml, expectedJson);
    }

    @Test
    public void testStudentParentAssociation() throws Throwable {

        Resource schema = new ClassPathResource("edfiXsd-SLI/SLI-Interchange-StudentParent.xsd");
        Resource inputXml = new ClassPathResource("parser/InterchangeStudentParent/StudentParentAssociation.xml");
        Resource expectedJson = new ClassPathResource("parser/InterchangeStudentParent/StudentParentAssociation.expected.json");

        EntityTestHelper.parseAndVerify(schema, inputXml, expectedJson);
    }

}
