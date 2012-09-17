package org.slc.sli.dal.lifecycle;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Define a Deterministic UUID class used to derive, create, and return UUIDs based upon entity natural keys
 * used in conjunction with a message digest algorithm.
 *
 * @author Robert Bloh <rbloh@wgen.net>
 *
 */

public class DeterministicUuid {

    // Constants
    public static final String DIGEST_ALGORITHM = "SHA-256";

    // Static Methods
    public static void generate(ModelEntity entity) throws ModelException {

        if (entity.getKeys().keySet().size() > 0) {

            // Generate Using Keys
            for (String attributeName : entity.getKeys().keySet()) {
                String uuid = getUuid(entity, attributeName).toString();
                if (attributeName.equals(ModelEntity.ID_ATTRIBUTE)) {
                    entity.setId(uuid);
                } else {
                    entity.getBody().put(attributeName, uuid);
                }
            }
        } else {

            // Generate Default Random UUID
            String uuid = getUuid(entity, ModelEntity.ID_ATTRIBUTE, true).toString();
            entity.setId(uuid);

        }
    }

    public static UUID getUuid(ModelEntity entity, String attributeName) throws ModelException {
        return getUuid(entity, attributeName, false);
    }

    public static UUID getUuid(ModelEntity entity, String attributeName, boolean allowRandom) throws ModelException {
        UUID uuid = null;

        try {

            // Setup Message Digest
            MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGORITHM);

            if ((entity.getKeys(attributeName) == null) || (entity.getKeys(attributeName).length <= 0)) {

                if (allowRandom) {

                    // If No Keys Specified Digest Random UUID
                    UUID randomUUID = java.util.UUID.randomUUID();
                    byte[] uuidBytes = randomUUID.toString().getBytes();

                    messageDigest.update(uuidBytes);

                } else {
                    throw new ModelException("Natural keys for attribute: " + attributeName + " missing or invalid.");
                }

            } else {

                // Digest Entity Keys
                for (int i = 0; i < entity.getKeys(attributeName).length; i++) {

                    String key = entity.getKeys(attributeName)[i];

                    String keyValue = "";
                    if (key.equalsIgnoreCase("type")) {
                        // Special Case: "type"
                        keyValue = entity.getType();
                    } else {
                        keyValue = entity.getBody().get(key).toString();
                    }

                    byte[] keyValueBytes = keyValue.getBytes();

                    messageDigest.update(keyValueBytes);
                }

            }

            // Generate Digest UUID String
            byte[] digestBytes = messageDigest.digest();

            // Reset Message Digest
            messageDigest.reset();

            uuid = generateUuid(digestBytes);

        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new ModelException(noSuchAlgorithmException.getMessage());
        }

        return uuid;
    }

    protected static UUID generateUuid(byte[] data) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);

        long msb = byteBuffer.getLong(0);
        long lsb = byteBuffer.getLong(8);

        UUID uuid = new UUID(msb, lsb);

        return uuid;
    }

//    public static void main(String[] args) throws Exception {
//
//        try {
//
//            LOG.info("Starting SHA Example...");
//
//            ModelEntity entity = new ModelEntity();
//
//            LOG.info("Default Random Deterministic UUID: " + DeterministicUUID.getUUID(entity, ModelEntity.ID_ATTRIBUTE, true));
//
//            String[] idKeys = {"id"};
//            entity.getBody().put("id", "1");
//
//            entity.setKeys(ModelEntity.ID_ATTRIBUTE, idKeys);
//
//            LOG.info("First Deterministic UUID: " + DeterministicUUID.getUUID(entity, ModelEntity.ID_ATTRIBUTE));
//
//            entity.getBody().put("id", "2");
//
//            LOG.info("Second Deterministic UUID: " + DeterministicUUID.getUUID(entity, ModelEntity.ID_ATTRIBUTE));
//
//            entity.getBody().put("id", "1");
//
//            LOG.info("First Deterministic UUID: " + DeterministicUUID.getUUID(entity, ModelEntity.ID_ATTRIBUTE));
//
//        } catch (Exception exception) {
//            LOG.error(exception.getMessage());
//        }
//    }
}
