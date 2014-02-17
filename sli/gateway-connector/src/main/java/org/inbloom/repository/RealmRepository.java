package org.inbloom.repository;

import org.inbloom.model.Realm;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RealmRepository extends MongoRepository<Realm, String> {

}
