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
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.test.WebContextTestExecutionListener;
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

    private static final String FILE_NAME = "mock.tar.gz";

    private static final String EXPECTED_STRING = "Crypto sux";

    @Autowired
    @InjectMocks
    private BulkExtract bulkExtract;

    @Autowired
    private SecurityContextInjector injector;

    @Mock
    private Repository<Entity> mockMongoEntityRepository;

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

        Cipher dec = Cipher.getInstance("AES/CBC/PKCS5Padding");
        dec.init(Cipher.DECRYPT_MODE, pair.getRight(), new IvParameterSpec(enc.getIV()));
        Assert.assertEquals(EXPECTED_STRING, StringUtils.newStringUtf8(dec.doFinal(bytes)));
    }

    @Test
    public void testGetFileError() throws Exception {
        injector.setEducatorContext();
        Entity mockEntity = Mockito.mock(Entity.class);
        Map<String, Object> mockBody = Mockito.mock(Map.class);
        Mockito.when(mockEntity.getBody()).thenReturn(mockBody);
        Mockito.when(mockBody.get(Mockito.anyString())).thenReturn("");
        Mockito.when(mockMongoEntityRepository.findOne(Mockito.anyString(), Mockito.any(NeutralQuery.class)))
            .thenReturn(mockEntity);
        ResponseImpl res = (ResponseImpl) bulkExtract.get();
        assertEquals(404, res.getStatus());
    }

  @Test
  public void testGet() throws Exception {
      injector.setEducatorContext();
      Entity mockEntity = Mockito.mock(Entity.class);
      Map<String, Object> mockBody = Mockito.mock(Map.class);
      Mockito.when(mockEntity.getBody()).thenReturn(mockBody);

      File tmpDir = FileUtils.getTempDirectory();
      File file = FileUtils.getFile(tmpDir, FILE_NAME);
      FileUtils.writeStringToFile(file, "12345");
      Mockito.when(mockBody.get(BulkExtract.BULK_EXTRACT_FILE_PATH)).thenReturn(file.getAbsolutePath());
      Mockito.when(mockBody.get(BulkExtract.BULK_EXTRACT_DATE)).thenReturn(new Date());
      Mockito.when(mockMongoEntityRepository.findOne(Mockito.anyString(), Mockito.any(NeutralQuery.class)))
          .thenReturn(mockEntity);

      ResponseImpl res = (ResponseImpl) bulkExtract.get();
      assertEquals(200, res.getStatus());
      MultivaluedMap<String, Object> headers = res.getMetadata();
      assertNotNull(headers);
      assertTrue(headers.containsKey("content-disposition"));
      assertTrue(headers.containsKey("last-modified"));
      String header = (String) headers.getFirst("content-disposition");
      assertNotNull(header);
      assertTrue(header.startsWith("attachment"));
      assertTrue(header.indexOf(FILE_NAME) > 0);

      Object entity = res.getEntity();
      assertNotNull(entity);
      StreamingOutput out = (StreamingOutput) entity;
      FileOutputStream os = new FileOutputStream(file);
      out.write(os);
      os.flush();
      assertTrue(file.exists());
      FileUtils.deleteQuietly(file);
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
                            new NeutralCriteria("date", NeutralCriteria.CRITERIA_GTE, new DateTime(2013, 3, 31, 0, 0).getMillis()))
                            && query.getCriteria().contains(
                                    new NeutralCriteria("date", NeutralCriteria.CRITERIA_LT, new DateTime(2013, 4, 1,
                                            0, 0).getMillis()));

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
