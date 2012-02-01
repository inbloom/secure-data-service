package org.slc.sli.ingestion.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.Validator;

/**
 * Tests for EntityPersistHandler
 *
 * @author dduran
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class EntityPersistHandlerTest {

    @Autowired
    private EntityPersistHandler entityPersistHandler;

    private EntityRepository mockedEntityRepository;

    private Validator<Entity> mockedValidator;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        mockedEntityRepository = mock(EntityRepository.class);
        entityPersistHandler.setEntityRepository(mockedEntityRepository);

        mockedValidator = mock(Validator.class);
        entityPersistHandler.setPreValidator(mockedValidator);
    }

    @Test
    public void testDoHandling() {
        Entity mockedEntity = mock(Entity.class);
        ErrorReport mockedErrorReport = mock(ErrorReport.class);

        String expectedType = "test_entity";
        when(mockedEntity.getType()).thenReturn(expectedType);

        Map<String, Object> expectedMap = new HashMap<String, Object>();
        when(mockedEntity.getBody()).thenReturn(expectedMap);

        entityPersistHandler.doHandling(mockedEntity, mockedErrorReport);

        verify(mockedEntityRepository).create(expectedType, expectedMap);
    }

    @Test
    public void testHandlePassedValidation() {
        /*
         * when validation passes for an entity, we should try to persist
         */

        Entity mockedEntity = mock(Entity.class);
        ErrorReport mockedErrorReport = mock(ErrorReport.class);

        String expectedType = "test_entity";
        when(mockedEntity.getType()).thenReturn(expectedType);

        Map<String, Object> expectedMap = new HashMap<String, Object>();
        when(mockedEntity.getBody()).thenReturn(expectedMap);

        when(mockedErrorReport.hasErrors()).thenReturn(false);

        entityPersistHandler.handle(mockedEntity, mockedErrorReport);

        verify(mockedEntityRepository).create(expectedType, expectedMap);
    }

    @Test
    public void testHandleFailedValidation() {
        /*
         * when validation fails for an entity, we should not try to persist
         */

        Entity mockedEntity = mock(Entity.class);
        ErrorReport mockedErrorReport = mock(ErrorReport.class);

        String expectedType = "test_entity";
        when(mockedEntity.getType()).thenReturn(expectedType);

        Map<String, Object> expectedMap = new HashMap<String, Object>();
        when(mockedEntity.getBody()).thenReturn(expectedMap);

        when(mockedErrorReport.hasErrors()).thenReturn(true);

        entityPersistHandler.handle(mockedEntity, mockedErrorReport);

        verify(mockedEntityRepository, never()).create(expectedType, expectedMap);
    }

}
