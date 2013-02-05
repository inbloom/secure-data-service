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

import java.util.ArrayList;
import java.util.List;

import openadk.library.common.Email;
import openadk.library.common.EmailList;

import org.springframework.stereotype.Component;

import org.slc.sli.sif.domain.slientity.ElectronicMail;

/**
 * Converter for mapping a SIF EmailList to a  List of SLI Electronic Mail objects.
 *
 * SIF EmailType is not mapped since this is not compatible with the SLI type and the SLI
 * type is optional.
 *
 * @author jtully
 *
 */
@Component
public class EmailListConverter {

    public List<ElectronicMail> convert(EmailList list) {
        if (list == null) {
            return null;
        }

        List<ElectronicMail> result =  new ArrayList<ElectronicMail>();
        for (Email sifEmail : list)  {
            ElectronicMail sliEmail = new ElectronicMail();
            sliEmail.setEmailAddress(sifEmail.getValue());
            result.add(sliEmail);
        }
        return result;
    }

}
