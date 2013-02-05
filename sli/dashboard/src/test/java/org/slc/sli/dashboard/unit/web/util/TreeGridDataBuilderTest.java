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


package org.slc.sli.dashboard.unit.web.util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.web.util.TreeGridDataBuilder;

/**
 * JUnit test class for TreeGridDataBuilder
 *
 * @author David Wu
 *
 */
public class TreeGridDataBuilderTest {

    @Test
    public void testBuild() {

        List<GenericEntity> subjects = new ArrayList<GenericEntity>();

        GenericEntity subject = new GenericEntity();
        subject.put("id", "subject1");
        subject.put("subjectArea", "Math");
        subjects.add(subject);

        GenericEntity course1 = new GenericEntity();
        course1.put("id", "course1");
        course1.put("courseTitle", "Algebra");
        GenericEntity course2 = new GenericEntity();
        course2.put("id", "course2");
        course2.put("courseTitle", "Geometry");
        List<GenericEntity> courses = new ArrayList<GenericEntity>();
        courses.add(course1);
        courses.add(course2);
        subject.put("courses", courses);

        GenericEntity section1 = new GenericEntity();
        section1.put("id", "section1");
        section1.put("sectionName", "Algebra Sec 1");
        GenericEntity section2 = new GenericEntity();
        section2.put("id", "section2");
        section2.put("sectionName", "Algebra Sec 2");

        /*
        GenericEntity section3 = new GenericEntity();
        section3.put("id", "section3");
        section3.put("sectionName", "Geometry Sec 3");
        GenericEntity section4 = new GenericEntity();
        section4.put("id", "section4");
        section4.put("sectionName", "Geometry Sec 4");
         */

        List<GenericEntity> sections1 = new ArrayList<GenericEntity>();
        sections1.add(section1);
        sections1.add(section2);
        course1.put("sections", sections1);

        /*
        List<GenericEntity> sections2 = new ArrayList<GenericEntity>();
        sections2.add(section3);
        sections2.add(section4);
        course2.put("sections", sections2);
         */

        List<String> subLevels = new ArrayList<String>();
        subLevels.add("courses");
        subLevels.add("sections");
        List<GenericEntity> treeGrid = TreeGridDataBuilder.build(subjects, subLevels);

        Assert.assertEquals(5, treeGrid.size());
        GenericEntity t0 = treeGrid.get(0);
        Assert.assertEquals("subject1", t0.getId());
        Assert.assertEquals("null", t0.get("parent"));
        Assert.assertEquals(0, t0.get("level"));
        Assert.assertEquals(false, t0.get("isLeaf"));

        GenericEntity t1 = treeGrid.get(1);
        Assert.assertEquals("course1", t1.getId());
        Assert.assertEquals("subject1", t1.get("parent"));
        Assert.assertEquals(1, t1.get("level"));
        Assert.assertEquals(false, t1.get("isLeaf"));

        GenericEntity t2 = treeGrid.get(2);
        Assert.assertEquals("section1", t2.getId());
        Assert.assertEquals("course1", t2.get("parent"));
        Assert.assertEquals(2, t2.get("level"));
        Assert.assertEquals(true, t2.get("isLeaf"));

        GenericEntity t4 = treeGrid.get(4);
        Assert.assertEquals("course2", t4.getId());
        Assert.assertEquals("subject1", t4.get("parent"));
        Assert.assertEquals(1, t4.get("level"));
        Assert.assertEquals(true, t4.get("isLeaf"));
    }

}
