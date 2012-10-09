package org.slc.sli.ingestion.transformation.normalization.did;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

/**
 * unit tests for DidSchemaParser
 *
 * @author jtully
 *
 */
public class DidSchemaParserTest {

    DidSchemaParser didSchemaParser;

    @Before
    public void setup() {
        didSchemaParser = new DidSchemaParser();
        didSchemaParser.setResourceLoader(new DefaultResourceLoader());
        didSchemaParser.setXsdLocation("classpath:test-schema/Ed-Fi-Core.xsd");
    }

    @Test
    public void shouldParseEdFiSchema() {
        System.out.println(didSchemaParser);

        didSchemaParser.setup();
    }
}
