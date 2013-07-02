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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.ReadPreference;

import org.apache.commons.lang3.ArrayUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.slc.sli.dal.repository.MongoEntityRepository;

/**
 * Bulk Extract Repository to read from secondary server.
 *
 * @author npandey
 *
 */
public class SecondaryReadRepository extends MongoEntityRepository {

    private static final Logger LOG = LoggerFactory
            .getLogger(SecondaryReadRepository.class);

    @Value("${sli.be.mongo.tagSet:}")
    private String tagSet;

    @Value("${sli.be.mongo.failOnPrimary:true}")
    private boolean failOnPrimary;

    /**
     * Method to set read preference of the mongo template.
     *
     * @param firstTag
     *            map representing the first tag
     * @param remainingTags
     *            map representing the remaining tags
     */
    private void setReadPreference(DBObject firstTag, DBObject... remainingTags) {
        ReadPreference readPreference;
        if (firstTag == null) {
            LOG.info("Setting up database preferences to read from secondary");
            readPreference = failOnPrimary ? ReadPreference.secondary()
                    : ReadPreference.secondaryPreferred();
        } else if (remainingTags == null) {
            LOG.info("Setting up database preferences to read from secondary with respect to the tag set provided");
            readPreference = failOnPrimary ? ReadPreference.secondary(firstTag)
                    : ReadPreference.secondaryPreferred(firstTag);
        } else {
            LOG.info("Setting up database preferences to read from secondary with respect to the tag sets provided");
            readPreference = failOnPrimary ? ReadPreference.secondary(firstTag,
                    remainingTags) : ReadPreference.secondaryPreferred(
                    firstTag, remainingTags);
        }
        template.setReadPreference(readPreference);
    }

    /**
     * Method to convert string property to a list of maps.
     *
     * @return array of DBObject
     */
    @SuppressWarnings("unchecked")
    protected DBObject[] getTagSetsFromProperty() {
        List<Map<String, String>> tagMapList = new ArrayList<Map<String, String>>();
        DBObject[] tags = null;
        try {
            if (!tagSet.equals("")) {
                ObjectMapper om = new ObjectMapper();
                tagMapList = om.readValue(tagSet, List.class);
            }
        } catch (JsonParseException e) {
            LOG.error("Invalid tagset property specified: " + tagSet, e);
            tagMapList = null;
        } catch (JsonMappingException e) {
            LOG.error("Invalid tagset property specified: " + tagSet, e);
            tagMapList = null;
        } catch (IOException e) {
            LOG.error("Invalid tagset property specified: " + tagSet, e);
            tagMapList = null;
        }

        tags = convertListToDBObjectArray(tagMapList);

        return tags;
    }

    /**
     * Converts a list of map to array of DBObject.
     *
     * @param list
     *            list of map
     * @return array of DBObject
     */
    protected DBObject[] convertListToDBObjectArray(
            List<Map<String, String>> list) {
        List<DBObject> objectList = new ArrayList<DBObject>();

        DBObject[] object = null;

        if (list != null && !list.isEmpty()) {
            for (Map<String, String> tagSet : list) {
                objectList.add(new BasicDBObject(tagSet));
            }
            object = objectList.toArray(new DBObject[objectList.size()]);
        }

        return object;
    }

    /**
     * Method called after bean initialization.
     */
    @PostConstruct
    protected void setReadProperties() {

        DBObject[] tagSets = getTagSetsFromProperty();
        DBObject firstTag = tagSets == null ? null : getFirstTagSet(tagSets);
        DBObject[] remainingTags = tagSets == null ? null
                : getRemainingTagSets(tagSets);

        setReadPreference(firstTag, remainingTags);
    }

    /**
     * Method to get the first tag set from an array.
     *
     * @param object
     *            array of DBObject
     * @return first tag set
     */
    protected DBObject getFirstTagSet(DBObject[] object) {
        return object[0];
    }

    /**
     * Method to get the remaining tag sets from the array.
     *
     * @param object
     *            array of DBObject
     * @return array of remaining tag sets
     */
    protected DBObject[] getRemainingTagSets(DBObject[] object) {
        DBObject[] newObject = ArrayUtils.remove(object, 0);
        if (newObject.length == 0) {
            return null;
        }
        return newObject;
    }

    /**
     * Setter method for property.
     *
     * @param failOnPrimary
     *            boolean value
     */
    public void setFailOnPrimary(boolean failOnPrimary) {
        this.failOnPrimary = failOnPrimary;
    }

    /**
     * Setter method for property.
     *
     * @param tagSet
     *            String representing tagSet
     */
    public void setTagSet(String tagSet) {
        this.tagSet = tagSet;
    }
}
