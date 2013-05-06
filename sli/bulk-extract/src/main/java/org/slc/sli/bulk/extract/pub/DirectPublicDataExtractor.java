package org.slc.sli.bulk.extract.pub;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.util.EdOrgPathDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * User: ablum
 */
public class DirectPublicDataExtractor implements PublicDataExtractor {

    private EntityExtractor extractor;

    public DirectPublicDataExtractor(EntityExtractor extractor) {
        this.extractor = extractor;
    }


    @Override
    public void extract(String edOrgid, ExtractFile file) {

        for (EdOrgPathDefinition definition : EdOrgPathDefinition.values()) {
            Query query = new Query((new Criteria(definition.getEdOrgRefField())).is(edOrgid));
            extractor.setExtractionQuery(query);
            extractor.extractEntities(file, definition.getEntityName());
        }
     }
}
