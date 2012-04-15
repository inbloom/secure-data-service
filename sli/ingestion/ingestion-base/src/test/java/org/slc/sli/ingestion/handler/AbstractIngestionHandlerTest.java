package org.slc.sli.ingestion.handler;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.Validator;

/**
 * A Junit test to test AbstractIngestionHandler
 * @author ablum
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class AbstractIngestionHandlerTest {

    AbstractIngestionHandler<Object, Object> handler;

    @Test
    public void handleTest() {
        handler = Mockito.mock(AbstractIngestionHandler.class);
        Mockito.doCallRealMethod().when(handler).handle(Mockito.any());
        Mockito.doCallRealMethod().when(handler).handle(Mockito.any(), Mockito.any(ErrorReport.class));
        Mockito.doCallRealMethod().when(handler).handle(Mockito.any(), Mockito.any(ErrorReport.class), Mockito.any(FileProcessStatus.class));
        Mockito.doCallRealMethod().when(handler).pre(Mockito.any(), Mockito.any(ErrorReport.class));
        Mockito.doCallRealMethod().when(handler).post(Mockito.any(), Mockito.any(ErrorReport.class));
        Mockito.doCallRealMethod().when(handler).setPreValidators(Mockito.anyList());
        Mockito.doCallRealMethod().when(handler).setPostValidators(Mockito.anyList());

        Validator<Object> preValidator = Mockito.mock(Validator.class);
        Mockito.when(preValidator.isValid(Mockito.any(Object.class), Mockito.any(ErrorReport.class))).thenReturn(true);
        List<Validator<Object>> preValidators = new ArrayList<Validator<Object>>();
        preValidators.add(preValidator);
        handler.setPreValidators(preValidators);

        Validator<Object> postValidator = Mockito.mock(Validator.class);
        Mockito.when(postValidator.isValid(Mockito.any(Object.class), Mockito.any(ErrorReport.class))).thenReturn(true);
        List<Validator<Object>> postValidators = new ArrayList<Validator<Object>>();
        postValidators.add(postValidator);
        handler.setPostValidators(postValidators);

        Object ife = Mockito.mock(Object.class);
        Mockito.when(handler.doHandling(Mockito.any(Object.class), Mockito.any(ErrorReport.class), Mockito.any(FileProcessStatus.class))).thenReturn(ife);

        Object fileEntry = null;
        fileEntry = handler.handle(ife);

        Assert.assertNotNull(fileEntry);
    }

}
