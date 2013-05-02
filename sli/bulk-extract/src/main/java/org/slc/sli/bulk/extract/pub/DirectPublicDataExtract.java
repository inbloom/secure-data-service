package org.slc.sli.bulk.extract.pub;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * User: ablum
 */
public class DirectPublicDataExtract implements PublicDataExtract{

    private EntityExtractor extractor;

    public DirectPublicDataExtract(EntityExtractor extractor) {
        this.extractor = extractor;
    }


    @Override
    public void extract(String edOrgid, ExtractFile file) {
         Query query = new Query((new Criteria("_id")).is(edOrgid));
         extractor.setExtractionQuery(query);

         extractor.extractEntities(file, "educationOrganization");
     }
}
