package org.slc.sli.ingestion.processors;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.xml.sax.SAXException;

/**
 * Tests for TransformationProcessor
 *
 * @author ifaybyshev
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class TransformationProcessorTest {

    @Autowired
    private TransformationProcessor transformationProcessor;

    @Test
    public void shouldEnvokeTransformationStrategies() throws IOException, SAXException {

        transformationProcessor.performDataTransformations("1234567890");
        
        //TODO - Complete test
        assertTrue("Running Transformation Logic", true);
    }


}
