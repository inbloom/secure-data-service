package org.inbloom.repository;

import org.inbloom.model.EducationOrganization;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EducationOrganizationRepository  extends MongoRepository<EducationOrganization, String> {

}
