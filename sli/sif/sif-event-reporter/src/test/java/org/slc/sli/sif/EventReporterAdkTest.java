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

package org.slc.sli.sif;

import java.io.File;

import openadk.library.ADK;
import openadk.library.ADKException;

import org.junit.Before;

/**
 * ADK test.
 */
public abstract class EventReporterAdkTest {

    public static final String ADK_LOG_DIR = "target" + File.separator + "logs";
    public static final String ADK_LOG_FILE = "sif-openadk.log";
    public static final String ADK_LOG_PATH = ADK_LOG_DIR + File.separator + ADK_LOG_FILE;

    @Before
    public void setup() {
        try {
            System.setProperty("adk.log.file", ADK_LOG_PATH);
            ADK.initialize();
        } catch (ADKException e) {
            e.printStackTrace();
        }
    }
}
