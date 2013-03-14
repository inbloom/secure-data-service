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


package org.slc.sli.ingestion.landingzone.validation;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.BaseMessageCode;
import org.slc.sli.ingestion.reporting.impl.DummyMessageReport;
import org.slc.sli.ingestion.reporting.impl.JobSource;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;

/**
 * @author okrook
 *
 */
public class FilePresenceValidatorTest {

    @Test
    public void testBase004Message() {
        IngestionFileEntry fe = new IngestionFileEntry("/", FileFormat.CONTROL_FILE, FileType.CONTROL, "subFolder/file.ctl", "");

        FilePresenceValidator v = new FilePresenceValidator();

        AbstractMessageReport mr = Mockito.spy(new DummyMessageReport());

        Assert.assertFalse(v.isValid(fe, mr, new SimpleReportStats(), new JobSource("control")));

        Mockito.verify(mr, Mockito.atLeastOnce()).error(Mockito.any(ReportStats.class),
                Mockito.any(Source.class), Mockito.eq(BaseMessageCode.BASE_0004), Mockito.any(Object[].class));
    }

    @Test
    public void testBase001Message() {
        IngestionFileEntry fe = new IngestionFileEntry("/", FileFormat.CONTROL_FILE, FileType.CONTROL, "file.ctl", "");

        FilePresenceValidator v = new FilePresenceValidator();

        AbstractMessageReport mr = Mockito.spy(new DummyMessageReport());

        Assert.assertFalse(v.isValid(fe, mr, new SimpleReportStats(), new JobSource("control")));

        Mockito.verify(mr, Mockito.atLeastOnce()).error(Mockito.any(Throwable.class), Mockito.any(ReportStats.class),
                Mockito.any(Source.class), Mockito.eq(BaseMessageCode.BASE_0001), Mockito.any(Object[].class));
    }

    @Test
    public void testHappyPath() throws IOException {
        IngestionFileEntry fe = Mockito.spy(new IngestionFileEntry("/", FileFormat.CONTROL_FILE, FileType.CONTROL, "file.ctl", ""));

        Mockito.doReturn(new ByteArrayInputStream(new byte[1024])).when(fe).getFileStream();

        FilePresenceValidator v = new FilePresenceValidator();

        AbstractMessageReport mr = Mockito.spy(new DummyMessageReport());

        Assert.assertTrue(v.isValid(fe, mr, new SimpleReportStats(), new JobSource("control")));

        Mockito.verify(mr, Mockito.never()).error(Mockito.any(ReportStats.class),
                Mockito.any(Source.class), Mockito.any(BaseMessageCode.class), Mockito.any(Object[].class));
    }

}
