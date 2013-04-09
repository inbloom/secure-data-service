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

package org.slc.sli.bulk.extract.treatment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.slc.sli.domain.Entity;

/**
 *JUnit test class for TreatmentApplicator class.
 * @author ablum
 *
 */
public class TreatmentApplicatorTest {

    TreatmentApplicator applicator = new TreatmentApplicator();
    Treatment treatment1 = Mockito.mock(Treatment.class);
    Treatment treatment2 = Mockito.mock(Treatment.class);

    /**
     * Runs before JUnit test and does the initiation work.
     * @throws IOException
     *          if an I/O error occurred
     */
    @Before
    public void init() throws IOException {

        List<Treatment> treatments = new ArrayList<Treatment>();
        Entity student = Mockito.mock(Entity.class);
        Mockito.when(treatment1.apply(Mockito.any(Entity.class))).thenReturn(student);
        treatments.add(treatment1);
        Mockito.when(treatment2.apply(Mockito.any(Entity.class))).thenReturn(student);
        treatments.add(treatment2);
        applicator.setTreatments(treatments);
    }

    /**
     * Junit test case to test treatement application to a student entity.
     */
    @Test
    public void testApplyAll() {
        Entity student = Mockito.mock(Entity.class);
        applicator.apply(student);

        Mockito.verify(treatment1,Mockito.atLeast(1)).apply(Mockito.any(Entity.class));
        Mockito.verify(treatment2,Mockito.atLeast(1)).apply(Mockito.any(Entity.class));
    }
}
