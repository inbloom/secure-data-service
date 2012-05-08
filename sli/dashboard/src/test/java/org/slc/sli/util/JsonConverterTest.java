/**
 *  JUnit test for JsonConverter
 */
package org.slc.sli.util;

import static org.junit.Assert.*;

import org.junit.Test;

import org.slc.sli.entity.GenericEntity;

/**
 * @author Takashi Osako
 * JUnit Test
 */
public class JsonConverterTest {
    
    @Test
    public void test() {
        GenericEntity entity = new GenericEntity();
        GenericEntity element = new GenericEntity();
        element.put("tire", "Yokohama");
        entity.put("car", element);
        assertEquals("{\"car\":{\"tire\":\"Yokohama\"}}", JsonConverter.toJson(entity));
    }
}
