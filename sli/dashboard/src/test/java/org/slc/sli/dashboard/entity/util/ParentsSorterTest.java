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


package org.slc.sli.dashboard.entity.util;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.util.Constants;

/**
 * test for ParentsSorter class
 *
 * @author tosako
 *
 */
public class ParentsSorterTest {

    @Test
    public void testSort() {
        List<GenericEntity> entities = new LinkedList<GenericEntity>();
        List<LinkedHashMap<String, Object>> studentParentsAssocistion = new LinkedList<LinkedHashMap<String, Object>>();
        LinkedHashMap<String, Object> obj = new LinkedHashMap<String, Object>();
        obj.put(Constants.ATTR_RELATION, "Father");
        studentParentsAssocistion.add(obj);
        GenericEntity entity = new GenericEntity();
        entity.put(Constants.ATTR_STUDENT_PARENT_ASSOCIATIONS, studentParentsAssocistion);
        entities.add(entity);

        obj = new LinkedHashMap<String, Object>();
        obj.put(Constants.ATTR_RELATION, "Mother");
        studentParentsAssocistion = new LinkedList<LinkedHashMap<String, Object>>();
        studentParentsAssocistion.add(obj);
        entity = new GenericEntity();
        entity.put(Constants.ATTR_STUDENT_PARENT_ASSOCIATIONS, studentParentsAssocistion);
        entities.add(entity);

        ParentsSorter.sort(entities);
        List<LinkedHashMap<String, Object>> objTest = (List<LinkedHashMap<String, Object>>) entities.get(0).get(
                Constants.ATTR_STUDENT_PARENT_ASSOCIATIONS);
        Assert.assertEquals("Mother", objTest.get(0).get(Constants.ATTR_RELATION));
    }
}
