package org.slc.sli.util;

import com.google.gson.Gson;

/**
 * Json converter util
 * 
 * @author dwu
 *
 */
public class JsonConverter {

    public static String toJson(Object o) {
        
        Gson gson = new Gson();
        return gson.toJson(o);
    }
    
}
