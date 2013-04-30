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
package org.slc.sli.bulk.extract;

import junit.framework.Assert;

import com.mongodb.DBObject;
import com.mongodb.ReadPreference;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.slc.sli.dal.template.MongoEntityTemplate;

/**
 * Unit Test for BulkExtractRepository class.
 * @author npandey
 *
 */
public class SecondaryReadRepositoryTest {

    SecondaryReadRepository secondaryReadRepository;
    MongoEntityTemplate template;

    /**
     * Initialization method.
     *
     */
    @Before
    public void init() {
        secondaryReadRepository = new SecondaryReadRepository();
        template = Mockito.mock(MongoEntityTemplate.class);
        secondaryReadRepository.setTemplate(template);
    }

    /**
     * Test failure on primary.
     */
    @Test
    public void testFailOnPrimary() {
        secondaryReadRepository.setFailOnPrimary(true);
        String tagSetProperty = "";
        secondaryReadRepository.setTagSet(tagSetProperty);
        secondaryReadRepository.setReadProperties();
        Mockito.verify(template).setReadPreference(ReadPreference.secondary());
    }


    /**
     * Test no failure on primary.
     */
    @Test
    public void testNoFailOnPrimary() {
        secondaryReadRepository.setFailOnPrimary(false);
        String tagSetProperty = "";
        secondaryReadRepository.setTagSet(tagSetProperty);
        secondaryReadRepository.setReadProperties();
        Mockito.verify(template).setReadPreference(ReadPreference.secondaryPreferred());
    }

    /**
     * Test read preference when first tag-set is set.
     */
    @Test
    public void testOneTagSet() {
        secondaryReadRepository.setFailOnPrimary(true);
        String tagSetProperty = "[{\"name\" : \"value\"}]";
        secondaryReadRepository.setTagSet(tagSetProperty);
        DBObject firstTagSet = secondaryReadRepository.getFirstTagSet(secondaryReadRepository.getTagSetsFromProperty());
        DBObject[] remainingTagSets = secondaryReadRepository.getRemainingTagSets(secondaryReadRepository.getTagSetsFromProperty());
        Assert.assertNull(remainingTagSets);
        secondaryReadRepository.setReadProperties();
        Mockito.verify(template).setReadPreference(ReadPreference.secondary(firstTagSet));
    }

    /**
     * Test read preference when multiple tag-set are set.
     */
    @Test
    public void testMultipleTagSets() {
        secondaryReadRepository.setFailOnPrimary(true);
        String tagSetProperty = "[{\"name\" : \"value\"}, {\"name1\" : \"value1\", \"name2\" : \"value2\"}, {\"name3\" : \"value3\"}]";
        secondaryReadRepository.setTagSet(tagSetProperty);
        DBObject firstTagSet = secondaryReadRepository.getFirstTagSet(secondaryReadRepository.getTagSetsFromProperty());
        DBObject[] remainingTagSets = secondaryReadRepository.getRemainingTagSets(secondaryReadRepository.getTagSetsFromProperty());
        secondaryReadRepository.setReadProperties();
        Mockito.verify(template).setReadPreference(ReadPreference.secondary(firstTagSet, remainingTagSets));
    }

    /**
     * Test read preference when tag-set list is empty.
     */
    @Test
    public void testEmptyTagSet() {
        secondaryReadRepository.setFailOnPrimary(true);
        String tagSetProperty = "[]";
        secondaryReadRepository.setTagSet(tagSetProperty);
        DBObject[] tags = secondaryReadRepository.getTagSetsFromProperty();
        Assert.assertNull(tags);
        secondaryReadRepository.setReadProperties();
        Mockito.verify(template).setReadPreference(ReadPreference.secondary());
    }

    /**
     * Test when an invalid tag set is set in the properties.
     *
     */
    @Test
    public void testInvalidTagSet() {
        secondaryReadRepository.setFailOnPrimary(false);
        String tagSetProperty = "[{\"name\" : \"}, {\"name1\" : \"value1\", \"name2\" : \"value2\"}, {\"name3\" : \"value3\"}]";
        secondaryReadRepository.setTagSet(tagSetProperty);
        DBObject[] tags = secondaryReadRepository.getTagSetsFromProperty();
        Assert.assertNull(tags);
        secondaryReadRepository.setReadProperties();
        Mockito.verify(template).setReadPreference(ReadPreference.secondaryPreferred());
    }

    /**
     * Test when an null is set as a tag set is set in the properties.
     *
     */
    @Test
    public void testNullTagSet() {
        secondaryReadRepository.setFailOnPrimary(false);
        String tagSetProperty = "[{\"name\" : \"}, null, {\"name1\" : \"value1\", \"name2\" : \"value2\"}]";
        secondaryReadRepository.setTagSet(tagSetProperty);
        DBObject[] tags = secondaryReadRepository.getTagSetsFromProperty();
        Assert.assertNull(tags);
        secondaryReadRepository.setReadProperties();
        Mockito.verify(template).setReadPreference(ReadPreference.secondaryPreferred());
    }

    /**
     * Test when an empty entry is specified in a tag set is set in the properties.
     *
     */
    @Test
    public void testEmptyEntry() {
        secondaryReadRepository.setFailOnPrimary(false);
        String tagSetProperty = "[{\"name\" : \"}, , {\"name1\" : \"value1\", \"name2\" : \"value2\"}]";
        secondaryReadRepository.setTagSet(tagSetProperty);
        DBObject[] tags = secondaryReadRepository.getTagSetsFromProperty();
        Assert.assertNull(tags);
        secondaryReadRepository.setReadProperties();
        Mockito.verify(template).setReadPreference(ReadPreference.secondaryPreferred());
    }

}
