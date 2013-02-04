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


package org.slc.sli.test.generators;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.slc.sli.test.edfi.entities.GenerationCodeSuffixType;
import org.slc.sli.test.edfi.entities.OtherName;
import org.slc.sli.test.edfi.entities.OtherNameType;
import org.slc.sli.test.edfi.entities.PersonalTitlePrefixType;

public class OtherNameGenerator {
    private static final Logger log = Logger.getLogger(OtherNameGenerator.class);
    private Random rand = new Random(31);
    private String familyName_US       = "database/name/familyName_US.csv";
    private String givenName_female_US = "database/name/givenName_female_US.csv";
    private String givenName_male_US   = "database/name/givenName_male_US.csv";
    private String webmailDomain       = "database/name/webmailDomain.csv";
    private List<String> familyNames   ;
    private List<String> femaleNames   ;
    private List<String> maleNames     ;
    private List<String> webDomains    ;
    private int malePercent            = 50;

    /*
     *
     * malePercent can be 40, 50, 60, 63...etc.
     */
    public OtherNameGenerator(int malePercent) throws Exception{
        this.malePercent = malePercent;
        loadData();
    }

    public OtherNameGenerator() throws Exception{
        this(50);
    }

    private int getRand()
    {
        int num = rand.nextInt();
        return num < 0? -1 * num:num;
    }

    private List<String> loadColumn(String fileName) throws Exception
    {
        BufferedReader reader    = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        List<String> colValues   = new ArrayList<String>();
        String line = null;
        while((line = reader.readLine()) != null)
        {
            String [] lineParts = line.split(",");
            if(lineParts.length > 1)
            {
                String val = lineParts[0];
                colValues.add(val);
            }
            else
            {
                log.warn("Ignoring line [" + line + "] from [" + fileName + "].");
            }
        }
        return colValues;
    }

    private void loadData() throws Exception
    {
         familyNames = loadColumn(familyName_US);
         femaleNames = loadColumn(givenName_female_US);
         maleNames   = loadColumn(givenName_male_US);
         webDomains  = loadColumn(webmailDomain);
    }

    private OtherName getName(List<String> firstNames)
    {
        OtherName name = new OtherName();
        name.setFirstName(firstNames.get(getRand()%firstNames.size()));
        name.setMiddleName(firstNames.get(getRand()%firstNames.size()));
        name.setLastSurname(familyNames.get(getRand()%familyNames.size()));
        if(getRand()%4==0) name.setGenerationCodeSuffix(GenerationCodeSuffixType.JR);
        //name.setMaidenName();
        int r = getRand()% 4;
                  if (r == 0) name.setOtherNameType(OtherNameType.ALIAS);
             else if (r == 2) name.setOtherNameType(OtherNameType.NICKNAME);
             else if (r == 3) name.setOtherNameType(OtherNameType.OTHER_NAME);
             else if (r == 4) name.setOtherNameType(OtherNameType.PREVIOUS_LEGAL_NAME);
        return name;
    }

    public OtherName getMaleName()
    {
        OtherName name = getName(maleNames);
        name.setPersonalTitlePrefix(PersonalTitlePrefixType.MR);
        return name;
    }

    public OtherName getFemaleName()
    {
        OtherName name = getName(femaleNames);
        name.setPersonalTitlePrefix(PersonalTitlePrefixType.MS);
        return name;
    }

    public OtherName getName()
    {
        int rand = getRand()% 100;
        if(rand > malePercent)
            return getFemaleName();
        else
            return getMaleName();
    }

    private OtherName getOtherName(List<String> firstNames)
    {
        OtherName name = new OtherName();
        name.setFirstName(firstNames.get(getRand()%firstNames.size()));
        name.setMiddleName(firstNames.get(getRand()%firstNames.size()));
        name.setLastSurname(familyNames.get(getRand()%familyNames.size()));
        if(getRand()%4==0) name.setGenerationCodeSuffix(GenerationCodeSuffixType.JR);
        name.setOtherNameType(OtherNameType.ALIAS);
        return name;
    }

    public OtherName getMaleOtherName()
    {
        OtherName name = getOtherName(maleNames);
        name.setPersonalTitlePrefix(PersonalTitlePrefixType.MR);
        return name;
    }

    public OtherName getFemaleOtherName()
    {
        OtherName name = getOtherName(femaleNames);
        name.setPersonalTitlePrefix(PersonalTitlePrefixType.MS);
        return name;
    }

    public OtherName getOtherName()
    {
        int rand = getRand()% 100;
        if(rand > malePercent)
            return getFemaleOtherName();
        else
            return getMaleOtherName();
    }

    public static void main(String [] args) throws Exception
    {
        OtherNameGenerator nameFactory = new OtherNameGenerator(80);
        for(String id : "1 2 3 4 5 6 7 8 9 10".split(" "))
        {
            OtherName name = nameFactory.getOtherName();
            String nameString = "\n\n" + id + ".\n" +
                    "PersonalTitlePrefix : " + name.getPersonalTitlePrefix() + ",\n" +
                    "FirstName : " + name.getFirstName() + ",\n" +
                    "MiddleName : " + name.getMiddleName() + ",\n" +
                    "LastSurname : " + name.getLastSurname() + ",\n" +
                    "GenerationCodeSuffix : " + name.getGenerationCodeSuffix() + ",\n" +
                    "Verification : " + name.getOtherNameType();
            log.info(nameString);
            System.out.println(nameString);
        }
    }
}
