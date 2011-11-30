import play.*;
import play.jobs.*;
import play.test.*;

import models.*;

/**
 * Bootstrap runs on application start to perform initialization tasks.
 * Loads models as needed.
 */
@OnApplicationStart
public class Bootstrap extends Job {

    public void doJob() {
        // Check if the database is empty
        if(IdProvider.count() == 0) {
            Fixtures.loadModels("initial-data.yml");
        }
    }

}