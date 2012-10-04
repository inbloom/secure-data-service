package org.slc.sli.search.util;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import org.apache.commons.io.IOUtils;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.search.util.MongoTemplateWrapper.DBCollectionWrapper;

/**
 * Factory class for Mocking Mongo Template
 * 
 * @author tosako
 * 
 */
public class MockMongoTemplateFactory {

    private LinkedList<DBObject> jsonArray = new LinkedList<DBObject>();

    private MockMongoTemplateFactory() {
    }

    /**
     * factory method to create MongoTemplateWrapper.
     * 
     * @return
     */
    public static MongoTemplateWrapper create() {
        MongoTemplateWrapper mongotemplate = mock(MongoTemplateWrapper.class);

        // when getColleciton method is called, then read JSON file from resource by give name.
        // Finally, create DBCollectionWrapper class.
        when(mongotemplate.getCollection(anyString())).thenAnswer(new Answer<DBCollectionWrapper>() {
            public DBCollectionWrapper answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                String collection = (String) args[0];
                return (new MockMongoTemplateFactory()).createDBCollection(collection);
            }
        });
        return mongotemplate;
    }

    /**
     * Create DBCollectionWrapper.
     * Since this is mocking data, Date source comes from file in resource directory.
     * If a file does not exist, it returns null object.
     * @param collection
     * @return
     */
    private DBCollectionWrapper createDBCollection(String collection) {
        DBCollectionWrapper dbColelction = null;
        BufferedReader br = null;
        File jsonFile = new File(DBCollection.class.getClassLoader().getResource(collection + ".json").getFile());

        if (jsonFile.exists()) {
            try {
                br = new BufferedReader(new FileReader(jsonFile));
                String line;

                DBCursor cursor = mock(DBCursor.class);
                dbColelction = mock(DBCollectionWrapper.class);
                while ((line = br.readLine()) != null) {
                    DBObject bson = (DBObject) JSON.parse(line);
                    jsonArray.add(bson);
                }

                when(dbColelction.find(any(DBObject.class), any(DBObject.class))).thenReturn(cursor);

                when(cursor.hasNext()).thenAnswer(new Answer<Boolean>() {
                    public Boolean answer(InvocationOnMock invocation) throws Throwable {
                        return !jsonArray.isEmpty();
                    }
                });
                
                when(cursor.next()).thenAnswer(new Answer<DBObject>() {
                    public DBObject answer(InvocationOnMock invocation) throws Throwable {
                        return jsonArray.remove();
                    }

                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(br);
            }
        }
        return dbColelction;
    }
}
