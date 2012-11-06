/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.api.security.context.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Validates the context of a staff member to see the requested set of sections. Returns true if the
 * staff member can see ALL of the sections, and false otherwise.
 */
@Component
public class StaffToGradebookEntryValidator extends AbstractContextValidator {
    
    @Autowired
    StaffToSectionValidator sectionVal;
    
    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Override
    public boolean canValidate(String entityType, boolean through) {
        return entityType.equals(EntityNames.GRADEBOOK_ENTRY) && isStaff();
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) {
      NeutralQuery query = new NeutralQuery(0);
      query.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, ids));
      query.setIncludeFields(Arrays.asList("sectionId", "_id"));
      Iterable<Entity> ents = repo.findAll(EntityNames.GRADEBOOK_ENTRY, query);
      
      Set<String> sectionIds = new HashSet<String>();
      Set<String> gbeIds = new HashSet<String>();

      for (Entity gbe : ents) {
          String id = (String) gbe.getEntityId();
          String sectionId = (String) gbe.getBody().get("sectionId");
          gbeIds.add(id);
          sectionIds.add(sectionId);
      }
      
        // TODO can this happen?
      if (gbeIds.size() != ids.size() || ids.size() == 0) {
          return false;
      }
      

      return sectionVal.validate(EntityNames.SECTION, sectionIds);

      
    }

}
