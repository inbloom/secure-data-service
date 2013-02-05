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


package org.slc.sli.dal.convert;

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
public class MongoIdConverter implements IdConverter {

    /**
     * Converts the given STring into a UUID object.
     *
     * @param uid
     *            The object's UUID String
     * @return a Binary representation of the given UUID.
     */
    @Override
    public Object toDatabaseId(String id) {

        try {
//            UUID uuid = UUID.fromString(id);
//            return uuid;
            return id;
        } catch (IllegalArgumentException e) {
            return null;
        }
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
     * Mongo. This is recommended by the mongo docs
     * to store UUIDs.
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
