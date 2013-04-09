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

package org.slc.sli.api.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.slc.sli.api.security.RightsAllowed;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

/**
 * The Bulk Extract Endpoints.
 *
 * @author dkornishev
 *
 */
@Component
@Path("/bulk")
@Produces({ "application/x-tar" })
public class BulkExtract {

    private static final Logger LOG = LoggerFactory.getLogger(BulkExtract.class);

    private static final String SAMPLED_FILE_NAME = "sample-extract.tar";

    public static final String BULK_EXTRACT_FILES = "bulkExtractFiles";
    public static final String BULK_EXTRACT_FILE_PATH = "path";
    public static final String BULK_EXTRACT_DATE = "date";

    @Autowired
    private Repository<Entity> mongoEntityRepository;

    private SLIPrincipal principal;

    private void initializePrincipal() {
        this.principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * Creates a streaming response for the sample data file.
     *
     * @return
     *          A response with the sample extract file
     * @throws Exception
     *          On Error
     */
    @GET
    @Path("extract")
    @RightsAllowed({ Right.BULK_EXTRACT })
    public Response get() throws Exception {
        LOG.info("Received request to stream sample bulk extract...");

        final InputStream is = this.getClass().getResourceAsStream("/bulkExtractSampleData/" + SAMPLED_FILE_NAME);

        StreamingOutput out = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                int n;
                byte[] buffer = new byte[1024];
                while ((n = is.read(buffer)) > -1) {
                    output.write(buffer, 0, n);
                }
            }
        };

        ResponseBuilder builder = Response.ok(out);
        builder.header("content-disposition", "attachment; filename = " + SAMPLED_FILE_NAME);
        builder.header("last-modified", "Not Specified");
        return builder.build();
    }

    /**
     * Creates a streaming response for the tenant data file.
     *
     * @return
     *          A response with the actual extract file
     * @throws Exception
     *          On Error
     */
    @GET
    @Path("extract/tenant")
    @RightsAllowed({ Right.BULK_EXTRACT })
    public Response getTenant() throws Exception {
        info("Received request to stream tenant bulk extract...");
        checkApplicationAuthorization(null);

        return getExtractResponse(null);
    }

    /**
     * Stream a delta response.
     *
     * @param date
     *            the date of the delta
     * @return
     *          A response with a delta extract file.
     * @throws Exception
     *          On Error
     */
    @GET
    @Path("deltas/{date}")
    @RightsAllowed({ Right.BULK_EXTRACT })
    public Response getDelta(@PathParam("date") String date) throws Exception {
        LOG.info("Retrieving delta bulk extract");
        return getExtractResponse(date);
    }

    /**
     * Get the bulk extract response
     *
     * @param deltaDate
     *            the date of the delta, or null to get the full extract
     * @return the jax-rs response to send back.
     * @throws Exception
     */
    private Response getExtractResponse(String deltaDate) throws Exception {
        final Pair<Cipher, SecretKey> cipherSecretKeyPair = getCiphers();
        ExtractFile bulkExtractFileEntity = getBulkExtractFile(deltaDate);
        if (bulkExtractFileEntity == null) {
            // return 404 if no bulk extract support for that tenant
            LOG.info("No bulk extract support for tenant: {}", principal.getTenantId());
            return Response.status(Status.NOT_FOUND).build();
        }

        final File bulkExtractFile = bulkExtractFileEntity.getBulkExtractFile(bulkExtractFileEntity);
        if (bulkExtractFile == null || !bulkExtractFile.exists()) {
            // return 404 if the bulk extract file is missing
            LOG.info("No bulk extract file found for tenant: {}", principal.getTenantId());
            return Response.status(Status.NOT_FOUND).build();
        }

        String fileName = bulkExtractFile.getName();
        String lastModified = bulkExtractFileEntity.getLastModified();

        try {
        	final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            final InputStream is = new FileInputStream(bulkExtractFile);
            StreamingOutput out = new StreamingOutput() {
                @Override
                public void write(OutputStream output) throws IOException, WebApplicationException {
                    int n;
                    byte[] buffer = new byte[1024];

                    byte[] ivBytes = cipherSecretKeyPair.getLeft().getIV();
                    byte[] secretBytes = cipherSecretKeyPair.getRight().getEncoded();

                    PublicKey publicKey = getApplicationPublicKey(auth);
                    byte[] encryptedIV = encryptDataWithRSAPublicKey(ivBytes, publicKey);
                    byte[] encryptedSecret = encryptDataWithRSAPublicKey(secretBytes, publicKey);

                    output.write(encryptedIV);
                    output.write(encryptedSecret);

                    CipherOutputStream stream = new CipherOutputStream(output, cipherSecretKeyPair.getLeft());
                    while ((n = is.read(buffer)) > -1) {
                        stream.write(buffer, 0, n);
                    }
                    stream.close();
                }
            };
            ResponseBuilder builder = Response.ok(out);
            builder.header("content-disposition", "attachment; filename = " + fileName);
            builder.header("last-modified", lastModified);
            return builder.build();
        } catch (FileNotFoundException e) {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    private PublicKey getApplicationPublicKey(Authentication authentication) throws IOException {
        PublicKey publicKey = null;

        if (!(authentication instanceof OAuth2Authentication)) {
            throw new AccessDeniedException("Not logged in with valid oauth context");
        }
        final OAuth2Authentication oauth = (OAuth2Authentication) authentication;

        final String clientId = oauth.getClientAuthentication().getClientId();
        NeutralQuery query = new NeutralQuery(new NeutralCriteria("client_id", NeutralCriteria.OPERATOR_EQUAL,
                clientId));
        final Entity entity = mongoEntityRepository.findOne(EntityNames.APPLICATION, query);

        if(entity == null) {
        	throw new AccessDeniedException("Could not find application with client_id=" + clientId);
        } else if (entity.getBody().get("public_key") == null) {
            throw new AccessDeniedException("Missing public_key attribute on application entity. client_id=" + clientId);
        }

        String key = (String) entity.getBody().get("public_key");



        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decodeBase64(key));
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            publicKey = kf.generatePublic(spec);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Exception: NoSuchAlgorithmException {}", e);
            throw new IOException(e);
        } catch (InvalidKeySpecException e) {
            LOG.error("Exception: InvalidKeySpecException {}", e);
            throw new IOException(e);
        }

        return publicKey;
}

    /**
     * Get the bulk extract file
     *
     * @param deltaDate
     *            the date of the delta, or null to retrieve a full extract
     * @return
     */
    private ExtractFile getBulkExtractFile(String deltaDate) {
        boolean isDelta = deltaDate != null;
        initializePrincipal();
        NeutralQuery query = new NeutralQuery(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL,
                principal.getTenantId()));
        query.addCriteria(new NeutralCriteria("isDelta", NeutralCriteria.OPERATOR_EQUAL, Boolean.toString(isDelta)));
        if (isDelta) {
            DateTime d = ISODateTimeFormat.basicDate().parseDateTime(deltaDate);
            query.addCriteria(new NeutralCriteria("date", NeutralCriteria.CRITERIA_GTE, d.toDate()));
            query.addCriteria(new NeutralCriteria("date", NeutralCriteria.CRITERIA_LT, d.plusDays(1).toDate()));
        }
        Entity entity = mongoEntityRepository.findOne(BULK_EXTRACT_FILES, query);
        if (entity == null) {
            return null;
        }
        return new ExtractFile(entity.getBody().get(BULK_EXTRACT_DATE).toString(), entity.getBody()
                .get(BULK_EXTRACT_FILE_PATH).toString());
    }

    private byte[] encryptDataWithRSAPublicKey(byte[] rawData, PublicKey publicKey) {
        byte[] encryptedData = null;

        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            encryptedData = cipher.doFinal(rawData);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Exception: NoSuchAlgorithmException {}", e);
        } catch (NoSuchPaddingException e) {
            LOG.error("Exception: NoSuchPaddingException {}", e);
        } catch (InvalidKeyException e) {
            LOG.error("Exception: InvalidKeyException {}", e);
        } catch (BadPaddingException e) {
            LOG.error("Exception: BadPaddingException {}", e);
        } catch (IllegalBlockSizeException e) {
            LOG.error("Exception: IllegalBlockSizeException {}", e);
        }

        return encryptedData;
    }

    /**
     * @throws AccessDeniedException
     *             if the application is not BEEP enabled
     */
    private void checkApplicationAuthorization(Set<String> edorgsForExtract) throws AccessDeniedException {
        OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        String clientId = auth.getClientAuthentication().getClientId();
        Entity app = this.mongoEntityRepository.findOne("application", new NeutralQuery(new NeutralCriteria(
                "client_id", NeutralCriteria.OPERATOR_EQUAL, clientId)));
        Map<String, Object> body = app.getBody();
        if (!body.containsKey("isBulkExtract") || (Boolean) body.get("isBulkExtract") == false) {
            throw new AccessDeniedException("Application is not approved for bulk extract");
        }
    }

    /**
     * @return the mongoEntityRepository
     */
    public Repository<Entity> getMongoEntityRepository() {
        return mongoEntityRepository;
    }

    /**
     * @param mongoEntityRepository
     *            the mongoEntityRepository to set
     */
    public void setMongoEntityRepository(Repository<Entity> mongoEntityRepository) {
        this.mongoEntityRepository = mongoEntityRepository;
    }

    private Pair<Cipher, SecretKey> getCiphers() throws Exception {
        SecretKey secret = KeyGenerator.getInstance("AES").generateKey();

        Cipher encrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
        encrypt.init(Cipher.ENCRYPT_MODE, secret);

        return Pair.of(encrypt, secret);
    }

    /**
     * Information about the file to extract
     *
     * @author nbrown
     *
     */
    private class ExtractFile {
        private final String lastModified;
        private final String fileName;

        public ExtractFile(String lastModified, String fileName) {
            super();
            this.lastModified = lastModified;
            this.fileName = fileName;
            debug("The file is "  + fileName + " and lastModified is " + lastModified);
        }

        public String getLastModified() {
            return lastModified;
        }

        public File getBulkExtractFile(ExtractFile bulkExtractFileEntity) {
            File bulkExtractFile = new File(fileName);
            debug("Length of bulk extract file is " + bulkExtractFile.length());
            return bulkExtractFile;
        }

    }

}
