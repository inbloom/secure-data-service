package org.slc.sli.ingestion.handler;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.transformation.SimpleEntity;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * @author unavani
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SchoolSessionAssociationEntityPersistHandlerTest {

    @Autowired
    SchoolSessionAssociationEntityPersistHandler handler;
    SimpleEntity mockEntity;

    @Before
    public void setUp() {
        mockEntity = Mockito.mock(SimpleEntity.class);
    }

    public SchoolSessionAssociationEntityPersistHandler getSchoolSessionAssociationEntityPersistHandler() {
        SchoolSessionAssociationEntityPersistHandler spyHandler = Mockito.spy(handler);

        Mockito.doReturn(mockEntity).when((EntityPersistHandler) spyHandler).doHandling(Mockito.any(SimpleEntity.class),
                Mockito.any(ErrorReport.class), Mockito.any(FileProcessStatus.class));
        return spyHandler;
    }

    @Test
    public void testValid() {
        FaultsReport errorReport = new FaultsReport();

        SimpleEntity entity = new SimpleEntity();
        Map<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("session", new SimpleEntity());
        entity.setBody(bodyMap);

        SchoolSessionAssociationEntityPersistHandler mockHandler = getSchoolSessionAssociationEntityPersistHandler();
        mockHandler.doHandling(entity, errorReport, null);
        Assert.assertFalse("Error report should not contain errors", errorReport.hasErrors());
    }
}
