/**
 * 
 */
package org.slc.sli.ingestion.processors;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import org.slc.sli.ingestion.NeutralRecord;

/**
 * @author nbrown
 * 
 */
public class EdFiAssessmentConvertorTest {
    
    @Test
    public void test() {
        EdFiAssessmentConvertor convertor = new EdFiAssessmentConvertor(null);
        NeutralRecord dibels6th = new NeutralRecord();
        dibels6th.setAttributeField("AssessmentFamilyIdentificationCode", makeId("dibels6"));
        dibels6th.setAttributeField("AssessmentFamilyTitle", "DIBELS 6th Edition");
        dibels6th.setRecordType("AssessmentFamily");
        NeutralRecord dibelsNext = new NeutralRecord();
        dibelsNext.setAttributeField("AssessmentFamilyIdentificationCode", makeId("dibelsNext"));
        dibelsNext.setAttributeField("AssessmentFamilyTitle", "DIBELS Next");
        dibelsNext.setRecordType("AssessmentFamily");
        NeutralRecord dibelsNextK = new NeutralRecord();
        dibelsNextK.setAttributeField("AssessmentFamilyIdentificationCode", makeId("dibelsNextK"));
        dibelsNextK.setAttributeField("AssessmentFamilyTitle", "DIBELS Next Kindergarten");
        dibelsNextK.setRecordType("AssessmentFamily");
        dibelsNextK.setAttributeField("parentAssessmentFamilyId", "dibelsNext");
        NeutralRecord dibelsNext1 = new NeutralRecord();
        dibelsNext1.setAttributeField("AssessmentFamilyIdentificationCode",makeId( "dibelsNext1"));
        dibelsNext1.setAttributeField("AssessmentFamilyTitle", "DIBELS Next Grade 1");
        dibelsNext1.setRecordType("AssessmentFamily");
        dibelsNext1.setAttributeField("parentAssessmentFamilyId", "dibelsNext");
        NeutralRecord dibelsNext2 = new NeutralRecord();
        dibelsNext2.setAttributeField("AssessmentFamilyIdentificationCode", makeId("dibelsNext2"));
        dibelsNext2.setAttributeField("AssessmentFamilyTitle", "DIBELS Next Grade 2");
        dibelsNext2.setRecordType("AssessmentFamily");
        dibelsNext2.setAttributeField("parentAssessmentFamilyId", "dibelsNext");
        NeutralRecord dibelsNext3 = new NeutralRecord();
        dibelsNext3.setAttributeField("AssessmentFamilyIdentificationCode", makeId("dibelsNext3"));
        dibelsNext3.setAttributeField("AssessmentFamilyTitle", "DIBELS Next Grade 3");
        dibelsNext3.setRecordType("AssessmentFamily");
        dibelsNext3.setAttributeField("parentAssessmentFamilyId", "dibelsNext");
        NeutralRecord kboy = new NeutralRecord();
        kboy.setLocalId("kboy");
        kboy.setRecordType("assessment");
        kboy.setAttributeField("assessmentTitle", "K-BOY");
        kboy.setAttributeField("parentAssessmentFamilyId", "dibelsNextK");
        NeutralRecord kmoy = new NeutralRecord();
        kmoy.setLocalId("kmoy");
        kmoy.setRecordType("assessment");
        kmoy.setAttributeField("assessmentTitle", "K-MOY");
        kmoy.setAttributeField("parentAssessmentFamilyId", "dibelsNextK");
        NeutralRecord keoy = new NeutralRecord();
        keoy.setLocalId("keoy");
        keoy.setRecordType("assessment");
        keoy.setAttributeField("assessmentTitle", "K-EOY");
        keoy.setAttributeField("parentAssessmentFamilyId", "dibelsNextK");
        Map<String, List<NeutralRecord>> records = new HashMap<String, List<NeutralRecord>>();
        records.put("assessment", Arrays.asList(kboy, kmoy, keoy));
        records.put("AssessmentFamily",
                Arrays.asList(dibels6th, dibelsNext, dibelsNext1, dibelsNext2, dibelsNext3, dibelsNextK));
        List<NeutralRecord> sliAssessments = convertor.convert(records);
        assertEquals(3, sliAssessments.size());
        for (NeutralRecord record : sliAssessments) {
            assertEquals("DIBELS Next.DIBELS Next Kindergarten",
                    record.getAttributes().get("assessmentFamilyHierarchyName"));
        }
        
    }
    
    private List<Map<String, Object>> makeId(String id) {
        List<Map<String, Object>> idList = new ArrayList<Map<String,Object>>();
        Map<String, Object> idMap = new HashMap<String, Object>();
        idMap.put("ID", id);
        idList.add(idMap);
        return idList;
    }
    
}
