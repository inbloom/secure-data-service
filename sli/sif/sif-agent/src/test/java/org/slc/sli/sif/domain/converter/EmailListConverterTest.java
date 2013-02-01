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
package org.slc.sli.sif.domain.converter;

import java.util.List;

import junit.framework.Assert;
import openadk.library.common.Email;
import openadk.library.common.EmailList;
import openadk.library.common.EmailType;

import org.junit.Test;

import org.slc.sli.sif.AdkTest;
import org.slc.sli.sif.domain.slientity.ElectronicMail;

/**
 * Unit tests for EmailListConverter
 *
 * @author jtully
 *
 *         <xs:enumeration value="Home/Personal" />
 *         <xs:enumeration value="Organization" />
 *         <xs:enumeration value="Other" />
 *         <xs:enumeration value="Work" />
 *
 */

public class EmailListConverterTest extends AdkTest {

    private final EmailListConverter converter = new EmailListConverter();

    @Test
    public void testNullObject() {
        List<ElectronicMail> result = converter.convert(null);
        Assert.assertNull("Address list should be null", result);
    }

    @Test
    public void testEmptyList() {
        EmailList list = new EmailList();
        List<ElectronicMail> result = converter.convert(list);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testEmptyEmail() {
        EmailList list = new EmailList();
        list.add(new Email());
        List<ElectronicMail> result = converter.convert(list);

        Assert.assertEquals(1, result.size());

        ElectronicMail sliEmail = result.get(0);
        Assert.assertNull(sliEmail.getEmailAddressType());
        Assert.assertNull(sliEmail.getEmailAddress());
    }

    @Test
    public void testMappings() {
        EmailList emails = createEmailList();
        List<ElectronicMail> result = converter.convert(emails);
        Assert.assertEquals("Should map 5 emails", 5, result.size());

        for (int i = 0; i < 5; i++) {
            Assert.assertNotNull(result.get(i));
            Assert.assertEquals(result.get(i).getEmailAddress(), "email" + i);
            Assert.assertEquals(result.get(i).getEmailAddressType(), null);
        }
    }

    private EmailList createEmailList() {
        EmailList emails = new EmailList();
        emails.add(new Email(EmailType.PRIMARY, "email0"));
        emails.add(new Email(EmailType.ALT1, "email1"));
        emails.add(new Email(EmailType.ALT2, "email2"));
        emails.add(new Email(EmailType.ALT3, "email3"));
        emails.add(new Email(EmailType.ALT4, "email4"));
        return emails;
    }

}
