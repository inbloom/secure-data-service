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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.sun.jersey.core.spi.factory.ResponseImpl;

import org.apache.commons.io.FileUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Test for support BulkExtract
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class BulkExtractTest {

    private static final String INPUT_FILE_NAME = "mock.in.tar.gz";
    private static final String OUTPUT_FILE_NAME = "mock.out.tar.gz";


    private static final String EXPECTED_STRING = "Crypto sux";
    public static final String BULK_DATA = "12345";

    @Autowired
    @InjectMocks
    private BulkExtract bulkExtract;

    @Autowired
    private SecurityContextInjector injector;

    @Mock
    private Repository<Entity> mockMongoEntityRepository;

    private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw1KLTcuf8OpvbHfwMJks\n" +
            "UAXbeaoVqZiK/CRhttWDmlMEs8AubXiSgZCekXeaUqefK544BOgeuNgQmMmo0pLy\n" +
            "j/GoGhf/bSZH2tsx1uKneCUm9Oq1g+juw5HmBa14H914tslvriFpJvN0b7q53Zey\n" +
            "AOxuD06l94UMj7wnMiNypEhowIMyVMMCRR9485hC8YsRtGB+f607bB440+d5zjG8\n" +
            "HGofzWZoCWGR70gJkkOZhwtLw+njpIhmnjDyknngUsOaX1Gza5Fzuz0QtVc/iVHg\n" +
            "iSFSz068XR5+zUmTI3cns6QbGnbsajuaTNQiZUHmQ8LOCddAfZz/7incsD9D9Jfb\n" +
            "YwIDAQAB\n";
    private static final String PRIVATE_KEY = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDDUotNy5/w6m9sd/AwmSxQBdt5qhWpmIr8JGG21YOaUwSzwC5teJKBkJ6Rd5pSp58rnjgE6B642BCYyajSkvKP8agaF/9tJkfa2zHW4qd4JSb06rWD6O7DkeYFrXgf3Xi2yW+uIWkm83Rvurndl7IA7G4PTqX3hQyPvCcyI3KkSGjAgzJUwwJFH3jzmELxixG0YH5/rTtsHjjT53nOMbwcah/NZmgJYZHvSAmSQ5mHC0vD6eOkiGaeMPKSeeBSw5pfUbNrkXO7PRC1Vz+JUeCJIVLPTrxdHn7NSZMjdyezpBsaduxqO5pM1CJlQeZDws4J10B9nP/uKdywP0P0l9tjAgMBAAECggEACXTko7aZHsvq6yB/c4rm91ThRGm0tMpa6ExGotiBj6Y3UxCZ7tjolvdOhhJ5WUkeTrlRUwN+AUsMuqkA0Hkm30s+7Ux+JGW3EuSL7DB7FTkPQspeUW2kqblVnq7AYyKQ5qCoFJEviyA8YfBzcUQX7S2FQp53MJ2zdv4QE8Bdm5CEPiTjA8F0eOeA8awQfPK3W4JPPZkhVErb9ie0Tj18xARpmI8llI7s6kAU51qmFHvi51l8nqTNCbVxxfRPACT5NUr4qkD2fhaGaFqMekJz8aKvIEUBc37BBe1PmaRvQKZGc+GgpPkJc9xqVEhfihm2HHcfhsA7HvrMeYFd12tDCQKBgQDo2vwqOdYn/L8z9if9B1+qYw/ETJ6OMwXN+1yajpZm7RT+tj4uvsoqo88G/VYIb5ZzaXT7xtLwINEgl8G1PdIKFwScv9fxVOgjuZpPKdJuvplSzqJXSP7Ok/u1SUGmtN3oGbBG8r+N72dvDB1gt4daaZka6VuK8NOJn6BTHxefrQKBgQDWvIf8rWdOwbjJ22P/KB2px8gTCVDCkb+RP63uVxoARJ4tMeIZ2YRTHEZUhKqlvDWPZh+1l2bnzhjIQ/iOHwfiO4oBg6gFqceIdwWQs335z842JKV6lHhlN2vrAdIIc46uwTFs3HEfQKHalVrA4/4eAmmNv4UokWSRfL7xaoKJTwKBgQCAQQV9OIf1VGf39dAGtQYDMjbf9xep2P6MerOByaGbpV/X/4b2dk2h+MGx5t15HgUvIlm1x8gtTNYC7rNZ4WgL+Kuorp4BJbQK4VLV4YIvTznh+0A9dU4reCS+sE/Bw4MqMOP/3/qT8dX1uyV/PPcHXHxg70FloMnS1qIWxlxbrQKBgQCYYUz2p26J2rpws7iwFh2Gn3iA2blveNHCFrgsS67txcOhOqbBxTM7bvMRgts9pOM1ETkrOXcSw5OeeW1mHOsRRULXdD/FVQd89UkDt/uLTEV+8l5jL/yHht6T88TBro7vv7R9FalIjirM2/N8sc1gKkIRDnlFoncFLsqosfZTzQKBgQDhkt/sWJsnMQ4TlcIFDgzmAE3D5YePJW3oN+FBye+6ukB4OZAsF9I7OAF3ibbeVSQ8CXD7BuJFJenFazguD3zydreCsRmuEIkswg2mROsBnci3Jq3omnKsfR8V014PCTaRX39VDCvmTuKSk39zFibioWb74r+jAF6IRUVtu0A4hQ==";

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        Map<String, Object> appBody = new HashMap<String, Object>();
        appBody.put("isBulkExtract", true);
        Entity mockEntity = Mockito.mock(Entity.class);
        when(mockEntity.getBody()).thenReturn(appBody);
        when(mockMongoEntityRepository.findOne(Mockito.eq("application"), Mockito.any(NeutralQuery.class))).thenReturn(
                mockEntity);

    }

//    @SuppressWarnings("unchecked")
//    @Test
//    public void testCiphers() throws Exception {
//        Method m = BulkExtract.class.getDeclaredMethod("getCiphers", new Class<?>[] {});
//        m.setAccessible(true);
//        Pair<Cipher, SecretKey> pair = (Pair<Cipher, SecretKey>) m.invoke(this.bulkExtract, new Object[] {});
//        Assert.assertNotNull(pair);
//
//        Cipher enc = pair.getLeft();
//        byte[] bytes = enc.doFinal(EXPECTED_STRING.getBytes("UTF-8"));
//
//        SecretKeySpec key = new SecretKeySpec(pair.getRight().getEncoded(),"AES");
//        Cipher dec2 = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        dec2.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(enc.getIV()));
//        Assert.assertEquals(EXPECTED_STRING, StringUtils.newStringUtf8(dec2.doFinal(bytes)));
//    }

    @Test
    public void testGetSampleExtract() throws Exception {
        injector.setEducatorContext();

        ResponseImpl res = (ResponseImpl) bulkExtract.get(null);
        assertEquals(200, res.getStatus());
        MultivaluedMap<String, Object> headers = res.getMetadata();
        assertNotNull(headers);
        assertTrue(headers.containsKey("content-disposition"));
        assertTrue(headers.containsKey("last-modified"));
        String header = (String) headers.getFirst("content-disposition");
        assertNotNull(header);
        assertTrue(header.startsWith("attachment"));
        assertTrue(header.indexOf("sample-extract.tar") > 0);

        Object entity = res.getEntity();
        assertNotNull(entity);
        StreamingOutput out = (StreamingOutput) entity;
        File file = new File("out.zip");
        FileOutputStream os = new FileOutputStream(file);
        out.write(os);
        os.flush();
        assertTrue(file.exists());

        assertEquals(798669192L, FileUtils.checksumCRC32(file));
        FileUtils.deleteQuietly(file);
    }

    @Test
    public void testGetTenantFileError() throws Exception {
        injector.setEducatorContext();
        Entity mockEntity = Mockito.mock(Entity.class);
        Map<String, Object> mockBody = Mockito.mock(Map.class);
        Mockito.when(mockEntity.getBody()).thenReturn(mockBody);
        Mockito.when(mockBody.get("public_key")).thenReturn(PUBLIC_KEY);
        Mockito.when(mockBody.get("isBulkExtract")).thenReturn(true);
        Mockito.when(mockBody.containsKey("isBulkExtract")).thenReturn(true);
        Mockito.when(mockBody.get("path")).thenReturn("/fake/path");
        Mockito.when(mockBody.get("date")).thenReturn("2013-05-04");
        Mockito.when(mockMongoEntityRepository.findOne(Mockito.anyString(), Mockito.any(NeutralQuery.class)))
            .thenReturn(mockEntity);
        ResponseImpl res = (ResponseImpl) bulkExtract.getTenant(null);
        assertEquals(404, res.getStatus());
    }

  @Test
  public void testGet() throws Exception {
      injector.setOauthAuthenticationWithEducationRole();

      { //mock application entity
          Entity mockEntity = Mockito.mock(Entity.class);
          Map<String, Object> mockBody = Mockito.mock(Map.class);
          Mockito.when(mockEntity.getBody()).thenReturn(mockBody);

          Mockito.when(mockBody.get(eq("public_key"))).thenReturn(PUBLIC_KEY);
          Mockito.when(mockBody.get(eq("_id"))).thenReturn("abc123_id");
          Mockito.when(mockMongoEntityRepository.findOne(Mockito.eq(EntityNames.APPLICATION), Mockito.any(NeutralQuery.class)))
                  .thenReturn(mockEntity);
      }


      { //mock bulk extract entity
          Entity mockEntity = Mockito.mock(Entity.class);
          Map<String, Object> mockBody = Mockito.mock(Map.class);
          Mockito.when(mockEntity.getBody()).thenReturn(mockBody);

          File tmpDir = FileUtils.getTempDirectory();
          File inputFile = FileUtils.getFile(tmpDir, INPUT_FILE_NAME);

          FileUtils.writeStringToFile(inputFile, BULK_DATA);
          Mockito.when(mockBody.get(BulkExtract.BULK_EXTRACT_FILE_PATH)).thenReturn(inputFile.getAbsolutePath());
          Mockito.when(mockBody.get(BulkExtract.BULK_EXTRACT_DATE)).thenReturn(new Date());
          Mockito.when(mockMongoEntityRepository.findOne(Mockito.eq(BulkExtract.BULK_EXTRACT_FILES), Mockito.any(NeutralQuery.class)))
                  .thenReturn(mockEntity);
      }


      Response res = bulkExtract.getDelta(null, null);
      assertEquals(200, res.getStatus());
      MultivaluedMap<String, Object> headers = res.getMetadata();
      assertNotNull(headers);
      assertTrue(headers.containsKey("content-disposition"));
      assertTrue(headers.containsKey("last-modified"));
      String header = (String) headers.getFirst("content-disposition");
      assertNotNull(header);
      assertTrue(header.startsWith("attachment"));
      assertTrue(header.indexOf(INPUT_FILE_NAME) > 0);

      Object entity = res.getEntity();
      assertNotNull(entity);
  }

    @Test
    public void testGetDelta() throws Exception {
        injector.setEducatorContext();
        Map<String, Object> body = new HashMap<String, Object>();
        File f = File.createTempFile("bulkExtract", ".tgz");
        { //mock application entity
            Entity mockEntity = Mockito.mock(Entity.class);
            Map<String, Object> mockBody = Mockito.mock(Map.class);
            Mockito.when(mockEntity.getBody()).thenReturn(mockBody);

            Mockito.when(mockBody.get(eq("public_key"))).thenReturn(PUBLIC_KEY);
            Mockito.when(mockBody.get(eq("_id"))).thenReturn("abc123_id");
            Mockito.when(mockMongoEntityRepository.findOne(Mockito.eq(EntityNames.APPLICATION), Mockito.any(NeutralQuery.class)))
                    .thenReturn(mockEntity);
        }

        try {
            body.put(BulkExtract.BULK_EXTRACT_FILE_PATH, f.getAbsolutePath());
            body.put(BulkExtract.BULK_EXTRACT_DATE, "20130331");
            Entity e = new MongoEntity("bulkExtractEntity", body);
            when(mockMongoEntityRepository.findOne(eq(BulkExtract.BULK_EXTRACT_FILES), argThat(new BaseMatcher<NeutralQuery>() {

                @Override
                public boolean matches(Object arg0) {
                    NeutralQuery query = (NeutralQuery) arg0;
                    return query.getCriteria().contains(
                            new NeutralCriteria("date", NeutralCriteria.CRITERIA_GTE, new DateTime(2013, 3, 31, 0, 0).toDate()))
                            && query.getCriteria().contains(
                                    new NeutralCriteria("date", NeutralCriteria.CRITERIA_LT, new DateTime(2013, 4, 1,
                                            0, 0).toDate()));

                }

                @Override
                public void describeTo(Description arg0) {
                }
            }))).thenReturn(e);
            Response r = bulkExtract.getDelta(null, "20130331");
            assertEquals(200, r.getStatus());
            Response notExisting = bulkExtract.getDelta(null, "20130401");
            assertEquals(404, notExisting.getStatus());
        } finally {
            f.delete();
        }
    }

    @Test(expected = AccessDeniedException.class)
    public void testAppHasNoDefinedRestriction() throws Exception {
        injector.setEducatorContext();
        // No BE Field
        Map<String, Object> body = new HashMap<String, Object>();
        Entity mockEntity = Mockito.mock(Entity.class);
        when(mockEntity.getBody()).thenReturn(body);
        when(mockMongoEntityRepository.findOne(eq("application"), Mockito.any(NeutralQuery.class))).thenReturn(
                mockEntity);
        bulkExtract.getTenant(null);
    }

    @Test(expected = AccessDeniedException.class)
    public void testAppIsNotBeepApp() throws Exception {
        injector.setEducatorContext();
        // No BE Field
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("isBulkExtract", false);
        Entity mockEntity = Mockito.mock(Entity.class);
        when(mockEntity.getBody()).thenReturn(body);
        when(mockMongoEntityRepository.findOne(eq("application"), Mockito.any(NeutralQuery.class))).thenReturn(
                mockEntity);
        bulkExtract.getTenant(null);
    }
}
