package org.slc.sli.bulk.extract.context.resolver.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ParentContextResolverTest {
    @InjectMocks
    private final ParentContextResolver underTest = new ParentContextResolver();
    @Mock
    private Repository<Entity> repo;
    @Mock
    private StudentContextResolver studentResolver;
 
    @Test
    public void test() {
        Entity parent = mock(Entity.class);
        Entity kid1 = mock(Entity.class);
        Entity kid2 = mock(Entity.class);
        when(parent.getEntityId()).thenReturn("parentId");
        when(repo.findEach(Mockito.matches("student"), Mockito.any(NeutralQuery.class))).thenReturn(Arrays.asList(kid1, kid2).iterator());
        //I don't know, maybe the parent has a step kid who used to live in a different district.
        when(studentResolver.findGoverningLEA(kid1)).thenReturn(new HashSet<String>(Arrays.asList("lea1", "lea2")));
        when(studentResolver.findGoverningLEA(kid2)).thenReturn(new HashSet<String>(Arrays.asList("lea1", "lea3")));
        assertEquals(new HashSet<String>(Arrays.asList("lea1", "lea2", "lea3")), underTest.findGoverningLEA(parent));
    }
    
}
