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

import com.sun.jersey.core.spi.factory.ResponseImpl;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

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
    private static final String PRIVATE_KEY = "MIIEpQIBAAKCAQEAw1KLTcuf8OpvbHfwMJksUAXbeaoVqZiK/CRhttWDmlMEs8Au\n" +
            "bXiSgZCekXeaUqefK544BOgeuNgQmMmo0pLyj/GoGhf/bSZH2tsx1uKneCUm9Oq1\n" +
            "g+juw5HmBa14H914tslvriFpJvN0b7q53ZeyAOxuD06l94UMj7wnMiNypEhowIMy\n" +
            "VMMCRR9485hC8YsRtGB+f607bB440+d5zjG8HGofzWZoCWGR70gJkkOZhwtLw+nj\n" +
            "pIhmnjDyknngUsOaX1Gza5Fzuz0QtVc/iVHgiSFSz068XR5+zUmTI3cns6QbGnbs\n" +
            "ajuaTNQiZUHmQ8LOCddAfZz/7incsD9D9JfbYwIDAQABAoIBAAl05KO2mR7L6usg\n" +
            "f3OK5vdU4URptLTKWuhMRqLYgY+mN1MQme7Y6Jb3ToYSeVlJHk65UVMDfgFLDLqp\n" +
            "ANB5Jt9LPu1MfiRltxLki+wwexU5D0LKXlFtpKm5VZ6uwGMikOagqBSRL4sgPGHw\n" +
            "c3FEF+0thUKedzCds3b+EBPAXZuQhD4k4wPBdHjngPGsEHzyt1uCTz2ZIVRK2/Yn\n" +
            "tE49fMQEaZiPJZSO7OpAFOdaphR74udZfJ6kzQm1ccX0TwAk+TVK+KpA9n4Whmha\n" +
            "jHpCc/GiryBFAXN+wQXtT5mkb0CmRnPhoKT5CXPcalRIX4oZthx3H4bAOx76zHmB\n" +
            "XddrQwkCgYEA6Nr8KjnWJ/y/M/Yn/QdfqmMPxEyejjMFzftcmo6WZu0U/rY+Lr7K\n" +
            "KqPPBv1WCG+Wc2l0+8bS8CDRIJfBtT3SChcEnL/X8VToI7maTynSbr6ZUs6iV0j+\n" +
            "zpP7tUlBprTd6BmwRvK/je9nbwwdYLeHWmmZGulbivDTiZ+gUx8Xn60CgYEA1ryH\n" +
            "/K1nTsG4ydtj/ygdqcfIEwlQwpG/kT+t7lcaAESeLTHiGdmEUxxGVISqpbw1j2Yf\n" +
            "tZdm584YyEP4jh8H4juKAYOoBanHiHcFkLN9+c/ONiSlepR4ZTdr6wHSCHOOrsEx\n" +
            "bNxxH0Ch2pVawOP+HgJpjb+FKJFkkXy+8WqCiU8CgYEAgEEFfTiH9VRn9/XQBrUG\n" +
            "AzI23/cXqdj+jHqzgcmhm6Vf1/+G9nZNofjBsebdeR4FLyJZtcfILUzWAu6zWeFo\n" +
            "C/irqK6eASW0CuFS1eGCL0854ftAPXVOK3gkvrBPwcODKjDj/9/6k/HV9bslfzz3\n" +
            "B1x8YO9BZaDJ0taiFsZcW60CgYEAmGFM9qduidq6cLO4sBYdhp94gNm5b3jRwha4\n" +
            "LEuu7cXDoTqmwcUzO27zEYLbPaTjNRE5Kzl3EsOTnnltZhzrEUVC13Q/xVUHfPVJ\n" +
            "A7f7i0xFfvJeYy/8h4bek/PEwa6O77+0fRWpSI4qzNvzfLHNYCpCEQ55RaJ3BS7K\n" +
            "qLH2U80CgYEA4ZLf7FibJzEOE5XCBQ4M5gBNw+WHjyVt6DfhQcnvurpAeDmQLBfS\n" +
            "OzgBd4m23lUkPAlw+wbiRSXpxWs4Lg988na3grEZrhCJLMINpkTrAZ3Ityat6Jpy\n" +
            "rH0fFdNeDwk2kV9/VQwr5k7ikpN/cxYm4qFm++K/owBeiEVFbbtAOIU=\n";

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCiphers() throws Exception {
        Method m = BulkExtract.class.getDeclaredMethod("getCiphers", new Class<?>[] {});
        m.setAccessible(true);
        Pair<Cipher, SecretKey> pair = (Pair<Cipher, SecretKey>) m.invoke(this.bulkExtract, new Object[] {});
        Assert.assertNotNull(pair);

        Cipher enc = pair.getLeft();
        byte[] bytes = enc.doFinal(EXPECTED_STRING.getBytes("UTF-8"));

        SecretKeySpec key = new SecretKeySpec(pair.getRight().getEncoded(),"AES");
        Cipher dec2 = Cipher.getInstance("AES/CBC/PKCS5Padding");
        dec2.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(enc.getIV()));
        Assert.assertEquals(EXPECTED_STRING, StringUtils.newStringUtf8(dec2.doFinal(bytes)));
    }

    @Test
    public void testGetFileError() throws Exception {
        injector.setEducatorContext();
        Entity mockEntity = Mockito.mock(Entity.class);
        Map<String, Object> mockBody = Mockito.mock(Map.class);
        Mockito.when(mockEntity.getBody()).thenReturn(mockBody);
        Mockito.when(mockBody.get(Mockito.anyString())).thenReturn(PUBLIC_KEY);
        Mockito.when(mockMongoEntityRepository.findOne(Mockito.anyString(), Mockito.any(NeutralQuery.class)))
            .thenReturn(mockEntity);
        ResponseImpl res = (ResponseImpl) bulkExtract.get();
        assertEquals(404, res.getStatus());
    }

  @Test
  public void testGet() throws Exception {
      injector.setOauthAuthenticationWithEducationRole();

      {
          Entity mockEntity = Mockito.mock(Entity.class);
          Map<String, Object> mockBody = Mockito.mock(Map.class);
          Mockito.when(mockEntity.getBody()).thenReturn(mockBody);

          Mockito.when(mockBody.get(eq("public_key"))).thenReturn(PUBLIC_KEY);
          Mockito.when(mockMongoEntityRepository.findOne(Mockito.eq(EntityNames.APPLICATION), Mockito.any(NeutralQuery.class)))
                  .thenReturn(mockEntity);
      }


      {
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


      Response res = bulkExtract.get();
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
      StreamingOutput out = (StreamingOutput) entity;

      ByteArrayOutputStream os = new ByteArrayOutputStream();

      out.write(os);
      os.flush();
      byte[] message = os.toByteArray();
      assertTrue(message.length >= 256 + 256 + BULK_DATA.length());
  }

    @Test
    public void testGetDelta() throws Exception {
        injector.setEducatorContext();
        Map<String, Object> body = new HashMap<String, Object>();
        File f = File.createTempFile("bulkExtract", ".tgz");
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
            Response r = bulkExtract.getDelta("20130331");
            assertEquals(200, r.getStatus());
            Response notExisting = bulkExtract.getDelta("20130401");
            assertEquals(404, notExisting.getStatus());
        } finally {
            f.delete();
        }
    }
}
