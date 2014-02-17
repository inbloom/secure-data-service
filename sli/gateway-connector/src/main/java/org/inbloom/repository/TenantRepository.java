package org.inbloom.repository;

import org.inbloom.model.Tenant;
import org.springframework.data.mongodb.repository.MongoRepository;



public interface TenantRepository extends MongoRepository<Tenant, String>{

}
