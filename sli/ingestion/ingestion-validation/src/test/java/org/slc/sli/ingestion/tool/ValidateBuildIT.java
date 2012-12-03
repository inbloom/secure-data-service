/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.ingestion.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Ignore;

/**
 * @author tke
 *
 */
@Ignore
public class ValidateBuildIT extends TestCase{

    static final String TARGET = "target/";
    static final String SRC_FILE = "OfflineValidationTool-src.zip";

    public void test() throws Exception{
        File srcZip = new File(TARGET + SRC_FILE);
        Assert.assertTrue(srcZip.exists());

        ZipFile zip = new ZipFile(srcZip);

        File target = new File(TARGET);
        unzipFileIntoDirectory(zip, target);

        File dir = new File(TARGET + "OfflineValidationTool");

        Process p = Runtime.getRuntime().exec("mvn clean package", null, dir);

        int ret = p.waitFor();

        Assert.assertTrue(ret == 0);
    }

    public static void unzipFileIntoDirectory(ZipFile zipFile, File homeParentDir) {
        Enumeration files = zipFile.entries();
        File f = null;
        FileOutputStream fos = null;

        while (files.hasMoreElements()) {
          try {
            ZipEntry entry = (ZipEntry) files.nextElement();
            InputStream eis = zipFile.getInputStream(entry);
            byte[] buffer = new byte[1024];
            int bytesRead = 0;

            f = new File(homeParentDir.getAbsolutePath() + File.separator + entry.getName());

            if (entry.isDirectory()) {
              f.mkdirs();
              continue;
            } else {
              f.getParentFile().mkdirs();
              f.createNewFile();
            }

            fos = new FileOutputStream(f);

            while ((bytesRead = eis.read(buffer)) != -1) {
              fos.write(buffer, 0, bytesRead);
            }
          } catch (IOException e) {
            e.printStackTrace();
            continue;
          } finally {
            if (fos != null) {
              try {
                fos.close();
              } catch (IOException e) {
                // ignore
              }
            }
          }
        }
      }


}
