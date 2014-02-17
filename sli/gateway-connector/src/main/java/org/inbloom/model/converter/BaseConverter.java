package org.inbloom.model.converter;

import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for Converters. Provides methods for
 * extracting data from nested Mongo DBObjects
 *
 * @author ben morgan
 */
public class BaseConverter {

    //get values from subdocs
    protected Object getObject(DBObject dbObj, String key)
    {
        String[] keys = key.split("\\.");

        Object obj = null;

        for(int i=0;i<keys.length;i++)
        {
            obj = dbObj.get(keys[i]);

            if(i < keys.length-1) {
                if(obj != null && obj instanceof DBObject) {
                    dbObj = (DBObject) obj;
                }
                else {
                    obj = null;
                    break;
                }
            }
        }

        return obj;
    }

    protected List<String> getStringList(DBObject dbObj, String key)
    {
        Object obj = getObject(dbObj, key);
        if(obj instanceof List)
        {
            List list = (List) obj;
            List<String> stringList = new ArrayList<String>();

            for(Object o : list)
            {
                    stringList.add(o.toString());
            }
            return stringList;
        }
        return null;
    }

    protected String getString(DBObject dbObj, String key)
    {
          Object obj = getObject(dbObj, key);
        if(obj instanceof String)
            return (String) obj;
        else return null;
    }

    protected Boolean getBoolean(DBObject dbObj, String key)
    {
        Object obj = getObject(dbObj, key);
        if(obj instanceof Boolean)
            return (Boolean) obj;
        else return null;
    }

}
