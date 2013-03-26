package org.slc.sli.bulk.extract.treatment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slc.sli.domain.Entity;


public class TreatmentApplicatorTest {

    TreatmentApplicator applicator = new TreatmentApplicator();
    Treatment treatment1 = Mockito.mock(Treatment.class);
    Treatment treatment2 = Mockito.mock(Treatment.class);

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

    @Test
    public void testApplyAll() {
        Entity student = Mockito.mock(Entity.class);
        applicator.apply(student);

        Mockito.verify(treatment1,Mockito.atLeast(1)).apply(Mockito.any(Entity.class));
        Mockito.verify(treatment2,Mockito.atLeast(1)).apply(Mockito.any(Entity.class));
    }
}
