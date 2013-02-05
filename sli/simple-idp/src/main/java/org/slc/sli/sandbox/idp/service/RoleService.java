/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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
 * Returns available role information
 */
@Component
public class RoleService {
    
    /**
     * Holds role information.
     * 
     */
    public static class Role {
        String name;
        
        public Role(String name) {
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
     * Returns roles available to the user
     */
    public List<Role> getAvailableRoles() {
        return Arrays.asList(new Role("IT Administrator"), new Role("Leader"), new Role("Educator"), new Role(
                "Aggregate Viewer"));
    }
}
