package org.slc.sli.api.init;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * 
 *
 */
public class RealmInitializerTest {
    
    @InjectMocks
    private RealmInitializer realmInit;
    
    @Mock
    private Repository<Entity> mockRepo;
    
    @Before
    public void setUp() throws Exception {
        realmInit = new RealmInitializer();
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testRealmNotExist() throws Exception {
        
        // verify that the code attempts to insert a new realm when no existing realm is present
        Mockito.when(mockRepo.findOne(Mockito.eq("realm"), Mockito.any(NeutralQuery.class))).thenReturn(null);
        final AtomicBoolean update = new AtomicBoolean(false);
        Mockito.when(mockRepo.update(Mockito.anyString(), Mockito.any(Entity.class))).thenAnswer(new Answer<Boolean>() {
            
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                update.set(true);
                return true;
            }
            
        });
        realmInit.init();
        assertTrue("Repo was updated with new realm", update.get());
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testOutdatedRealm() throws Exception {
        
        // verify that the code attempts to insert a new realm if the existing one needs to be
        // modified
        Map body = realmInit.createAdminRealmBody();
        body.put("name", "New name");
        Entity existingRealm = new MongoEntity("realm", body);
        Mockito.when(mockRepo.findOne(Mockito.eq("realm"), Mockito.any(NeutralQuery.class))).thenReturn(existingRealm);
        final AtomicBoolean update = new AtomicBoolean(false);
        Mockito.when(mockRepo.update(Mockito.anyString(), Mockito.any(Entity.class))).thenAnswer(new Answer<Boolean>() {
            
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                update.set(true);
                return true;
            }
            
        });
        realmInit.init();
        assertTrue("Existing realm was updated", update.get());
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testRealmUnchanged() throws Exception {
        
        // verify that the code doesn't attempt to update the realm if the existing one hasn't been
        // modified
        Map body = realmInit.createAdminRealmBody();
        Entity existingRealm = new MongoEntity("realm", body);
        Mockito.when(mockRepo.findOne(Mockito.eq("realm"), Mockito.any(NeutralQuery.class))).thenReturn(existingRealm);
        final AtomicBoolean update = new AtomicBoolean(false);
        Mockito.when(mockRepo.update(Mockito.anyString(), Mockito.any(Entity.class))).thenAnswer(new Answer<Boolean>() {
            
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                update.set(true);
                return true;
            }
            
        });
        realmInit.init();
        assertFalse("Existing realm was not touched", update.get());
    }
    
}
