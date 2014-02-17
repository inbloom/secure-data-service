package org.inbloom.resources;

import org.inbloom.model.Realm;
import org.inbloom.repository.RealmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * endpoint for realms
 * @author ben morgan
 */
@Controller
public class RealmResource {

    @Autowired
    MongoRepositoryFactory operatorRepositoryFactory;

    @RequestMapping(value = "/realms", method = RequestMethod.GET, produces="application/json")
    public @ResponseBody List<Realm> get() {

        RealmRepository realmRepo = operatorRepositoryFactory.getRepository(RealmRepository.class);
        return realmRepo.findAll();
    }

}
