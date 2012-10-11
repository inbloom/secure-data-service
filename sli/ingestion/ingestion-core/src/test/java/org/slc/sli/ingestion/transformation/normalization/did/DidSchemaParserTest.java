package org.slc.sli.ingestion.transformation.normalization.did;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
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

    private static final String SECTION_TYPE = "section";
    private static final String EDORG_TYPE = "educationOrganization";

    @Before
    public void setup() {
        didSchemaParser = new DidSchemaParser();
        didSchemaParser.setResourceLoader(new DefaultResourceLoader());
        didSchemaParser.setXsdLocation("classpath:test-schema/Ed-Fi-Core.xsd");
        didSchemaParser.setExtensionXsdLocation("classpath:test-schema/SLI-Ed-Fi-Core.xsd");
        didSchemaParser.setup();
    }

    //TODO add exception path tests

    @Test
    public void shouldExtractCorrectRefConfigs() {
        Map<String, DidRefConfig> refConfigs = didSchemaParser.extractRefConfigs();
        Assert.assertEquals("Should extract 1 ref config for the section reference", 1, refConfigs.size());
        Assert.assertTrue(refConfigs.containsKey(SECTION_TYPE));

        DidRefConfig refConfig = refConfigs.get(SECTION_TYPE);
        Assert.assertNotNull(refConfig);
        Assert.assertEquals(refConfig.getEntityType(), SECTION_TYPE);

        Assert.assertNotNull(refConfig.getKeyFields());
        List<KeyFieldDef> keyFields = refConfig.getKeyFields();
        Assert.assertEquals("keyFields list should contain 2 keyfields", 2, keyFields.size());
        Assert.assertNotNull(keyFields.get(0));
        Assert.assertNotNull(keyFields.get(1));

        KeyFieldDef uniqSectionCode = keyFields.get(0);
        Assert.assertEquals("uniqueSectionCode", uniqSectionCode.getKeyFieldName());
        Assert.assertNull("uniqueSectionCode should not have a nested DID", uniqSectionCode.getRefConfig());
        Assert.assertEquals("SectionIdentity.UniqueSectionCode", uniqSectionCode.getValueSource());

        KeyFieldDef schoolId = keyFields.get(1);

        Assert.assertEquals("schoolId", schoolId.getKeyFieldName());
        Assert.assertNotNull("schoolId should have a nested DID", schoolId.getRefConfig());
        Assert.assertNull("schoolId should not have a value source", schoolId.getValueSource());

        DidRefConfig nestedRefConfig = schoolId.getRefConfig();
        Assert.assertEquals(EDORG_TYPE, nestedRefConfig.getEntityType());
        Assert.assertNotNull(nestedRefConfig.getKeyFields());
        Assert.assertEquals("nested schoolId ref should contain 1 key field", 1, nestedRefConfig.getKeyFields().size());

        KeyFieldDef stateOrgId = nestedRefConfig.getKeyFields().get(0);
        Assert.assertNotNull(stateOrgId);
        Assert.assertEquals("stateOrganizationId", stateOrgId.getKeyFieldName());
        Assert.assertEquals("SectionIdentity.StateOrganizationId", stateOrgId.getValueSource());
        Assert.assertNull("nested stateOrgId should not contain a nested reference", stateOrgId.getRefConfig());
    }

    @Test
    public void shouldExtractCorrectEntityConfigs() {
        Map<String, DidEntityConfig> entityConfigs = didSchemaParser.extractEntityConfigs();

        Assert.assertEquals("Should extract 1 entity config for the 1 complexType containing a sectionReference (SLC-GradebookEntry)", 1, entityConfigs.size());

        //check the entity configs extracted are for the correct types
        Assert.assertTrue(entityConfigs.containsKey("SLC-GradebookEntry"));

        //test the entityConfig for StudentSectionAssociation
        DidEntityConfig GBEConfig = entityConfigs.get("SLC-GradebookEntry");
        Assert.assertNotNull(GBEConfig);
        Assert.assertNotNull(GBEConfig.getReferenceSources());

        List<DidRefSource> refSources = GBEConfig.getReferenceSources();
        Assert.assertEquals("entity config should contain a single DidRefSource (section)", 1, refSources.size());
        DidRefSource refSource = refSources.get(0);
        Assert.assertNotNull(refSource);
        Assert.assertEquals(refSource.getEntityType(), SECTION_TYPE);
        Assert.assertEquals(refSource.getSourceRefPath(), "body.SectionReference");
    }
}
