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

package org.slc.sli.sandbox.idp.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * Returns available user type information
 */
@Component
public class UserTypeService {

    /**
     * Holds userType information.
     */
    public static class UserType {
        String name;

        public UserType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return name;
        }
    }

    /**
     * Returns UserTypes available to the user
     */
    public List<UserType> getAvailableUserTypes() {
        return Arrays.asList(new UserType("student"), new UserType("staff"));
    }
}
