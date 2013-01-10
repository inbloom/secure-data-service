package org.slc.sli.datastore.mongo;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.bson.BSON;
import org.bson.types.Binary;
import org.springframework.stereotype.Component;

/**
 * Provides ID conversion for MongoDB.
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */

@Component
public class MongoIdConverter {
    
    /**
     * Converts the given String into a UUID object.
     * 
     * @param uid
     *            The object's UUID String
     * @return a Binary representation of the given UUID.
     */
    public Object toDatabaseId(String id) {
        
        UUID uuid = UUID.fromString(id);
        return uuid;
    }
    
    /**
     * 
     * Converts the given Binary into a String that represent a UUID.
     * 
     * @param id
     * @return
     */
    public static String binaryToUUIDString(Object id) {
        Binary binary = (Binary) id;
        byte[] arr = binary.getData();
        ByteBuffer buff = ByteBuffer.wrap(arr);
        
        long msb = buff.getLong(0);
        long lsb = buff.getLong(8);
        UUID uid = new UUID(msb, lsb);
        
        return uid.toString();
    }
    
    /**
     * Converts the given UUID into a Binary object that represents the underlying byte array in
     * Mongo. This is recommended by the mongo docs to store UUIDs.
     * 
     * @param uid
     *            The object's UUID
     * @return a Binary representation of the given UUID.
     */
    
    public static Binary convertUUIDtoBinary(UUID uuid) {
        
        ByteBuffer buff = ByteBuffer.allocate(16);
        buff.putLong(uuid.getMostSignificantBits());
        buff.putLong(uuid.getLeastSignificantBits());
        byte[] arr = buff.array();
        
        Binary binary = new Binary(BSON.B_UUID, arr);
        
        return binary;
    }
    
}
