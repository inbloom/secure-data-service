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


package org.slc.sli.ingestion.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to calculate MD5 on a file.
 *
 * @author okrook
 *
 */
public class MD5 {

    private static final Logger LOG = LoggerFactory.getLogger(MD5.class);

    public static String calculate(File f) {
        String md5 = "";

        try {
            md5 = DigestUtils.md5Hex( new BufferedInputStream( new FileInputStream(f) ) );
        } catch (IOException e) {
          LOG.error( "Error opening file: " + f.getAbsolutePath(), e );
        }

        return md5;
    }
}
