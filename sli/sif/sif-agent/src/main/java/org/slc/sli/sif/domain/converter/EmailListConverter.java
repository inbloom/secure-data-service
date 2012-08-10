package org.slc.sli.sif.domain.converter;

import java.util.ArrayList;
import java.util.List;

import openadk.library.common.Email;
import openadk.library.common.EmailList;

import org.slc.sli.sif.domain.slientity.ElectronicMail;
import org.springframework.stereotype.Component;

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
