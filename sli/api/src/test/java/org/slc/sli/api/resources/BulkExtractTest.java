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

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.StreamingOutput;

import com.sun.jersey.core.spi.factory.ResponseImpl;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Test for support BulkExtract
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class BulkExtractTest {

    private static final String FILE_NAME = "mock.tar.gz";

    private static final String EXPECTED_STRING = "Crypto sux";

    @Autowired
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
        Pair<Cipher,SecretKey> pair = (Pair<Cipher, SecretKey>) m.invoke(this.bulkExtract, new Object[] {});
        Assert.assertNotNull(pair);

        Cipher enc = pair.getLeft();
        byte[] bytes = enc.doFinal(EXPECTED_STRING.getBytes("UTF-8"));

        Cipher dec = Cipher.getInstance("AES/CBC/PKCS5Padding");
        dec.init(Cipher.DECRYPT_MODE, pair.getRight(), new IvParameterSpec(enc.getIV()));
        Assert.assertEquals(EXPECTED_STRING, StringUtils.newStringUtf8(dec.doFinal(bytes)));
    }

    @Test
    public void testGetWithNoTenantSupport() throws Exception {
        injector.setEducatorContext();
        ResponseImpl res = (ResponseImpl) bulkExtract.get();
        assertEquals(404, res.getStatus());
    }

    @Test
    public void testGetErrorFile() throws Exception {
        injector.setEducatorContext();
        bulkExtract.setMongoEntityRepository(mockMongoEntityRepository);
        Entity mockEntity = Mockito.mock(Entity.class);
        Map<String, Object> mockBody = Mockito.mock(Map.class);
        Mockito.when(mockEntity.getBody()).thenReturn(mockBody);
        Mockito.when(mockBody.get(Mockito.anyString())).thenReturn("");
        Mockito.when(mockMongoEntityRepository.findOne(Mockito.anyString(), Mockito.any(NeutralQuery.class)))
            .thenReturn(mockEntity);
        ResponseImpl res = (ResponseImpl) bulkExtract.get();
        assertEquals(204, res.getStatus());
    }

  @Test
  public void testGet() throws Exception {
      injector.setEducatorContext();
      bulkExtract.setMongoEntityRepository(mockMongoEntityRepository);
      Entity mockEntity = Mockito.mock(Entity.class);
      Map<String, Object> mockBody = Mockito.mock(Map.class);
      Mockito.when(mockEntity.getBody()).thenReturn(mockBody);

      File tmpDir = FileUtils.getTempDirectory();
      File file = FileUtils.getFile(tmpDir, FILE_NAME);
      FileUtils.writeStringToFile(file, "12345");
      System.out.print(file.exists());
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
}
