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


package org.slc.sli.dashboard.manager;

import org.slc.sli.dashboard.entity.Config;
import org.slc.sli.dashboard.entity.Config.Data;
import org.slc.sli.dashboard.entity.EdOrgKey;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.manager.Manager.EntityMapping;
import org.slc.sli.dashboard.manager.Manager.EntityMappingManager;

/**
 * Manager for hierarchy related manipulations
 * @author agrebneva
 *
 */
@EntityMappingManager
public interface UserEdOrgManager {
    public EdOrgKey getUserEdOrg(String token);
    @EntityMapping("userEdOrg")
    public GenericEntity getUserInstHierarchy(String token, Object key, Config.Data config);
    /**
     * GetStaff info and user admin flag.
     *
     * @param token - The staff entity
     * @return staff info
     */
    GenericEntity getStaffInfo(String token);
    @EntityMapping("userCoursesSections")
    GenericEntity getUserCoursesAndSections(String token, Object schoolIdObj, Data config);
    @EntityMapping("userSectionList")
    GenericEntity getUserSectionList(String token, Object schoolIdObj, Data config);
    @EntityMapping("schoolInfo")
    GenericEntity getSchoolInfo(String token, Object schoolIdObj, Data config);
}
