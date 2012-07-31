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

package org.slc.sli.ingestion.healthcheck;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.slc.sli.ingestion.IngestionHealthCheck;

/**
 * Health Check Controller.
 *
 * @author unavani
 *
 */
@Controller
@RequestMapping("/healthcheck")
public class HealthCheckController {

    Map<String, Heart> hearts = new HashMap<String, Heart>();

    @Autowired
    IngestionHealthCheck ingestionHealthCheck;

    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    @ResponseBody
    public Heart getNodeInformation(@PathVariable String name) {
        Heart heart;
        if (name.equals("Ingestion")) {
            if (hearts.containsKey(name)) {
                heart = hearts.get(name);
            } else {
                heart = new Heart(name);
                hearts.put(name, heart);
            }
            ingestionHealthCheck.updateLastActivity();
            heart.setStartTime(ingestionHealthCheck.getStartTime());
            heart.setLastActivity(ingestionHealthCheck.getLastActivity());
            heart.setLastActivityTime(ingestionHealthCheck.getLastActivityTime());
            heart.setVersion(ingestionHealthCheck.getVersion());
        } else {
            return null;
        }

        return heart;
    }
}
