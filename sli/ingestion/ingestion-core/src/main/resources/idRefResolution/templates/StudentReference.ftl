<StudentIdentity>
<#if (StudentReference.StudentUniqueStateId[0])??>
<StudentUniqueStateId>
    ${StudentReference.StudentUniqueStateId}
</StudentUniqueStateId >
</#if>
<#if (StudentReference.StudentIdentificationCode[0])??>
<StudentIdentificationCode
    <#if (StudentReference.StudentIdentificaitonCode.@identificationSystem[0])??>  
    IdentificationSystem="${StudentReference.StudentIdentificaitonCode.@identificationSystem}"
    </#if>
    <#if (StudentReference.StudentIdentificaitonCode.@AssigningOrganizationCode[0])??>
    AssigningOrganizationCode="${StudentReference.StudentIdentificaitonCode.@AssigningOrganizationCode}"
    </#if>
    >
    <#if (StudentReference.StudentIdentificationCode.IdentificationCode[0])??>
    <IdentificationCode>
        ${StudentReference.StudentIdentificationCode.IdentificationCode}
    </IdentificationCode>
    </#if>
</StudentIdentificationCode>
</#if>
<#if (StudentReference.Name[0])??>
<Name
    <#if (StudentReference.Name.@Verification[0])??> 
    Verification="${StudentReference.Name.@Verification}"
    </#if>
    >
    <#if (StudentReference.Name.PersonalTitlePrefix[0])??>
    <PersonalTitlePrefix>
        ${StudentReference.Name.PersonalTitlePrefix}
    </PersonalTitlePrefix>
    </#if>
    <#if (StudentReference.Name.FirstName[0])??>
    <FirstName>
        ${StudentReference.Name.FirstName}
    </FirstName>
    </#if>
    <#if (StudentReference.Name.MiddleName[0])??>
    <MiddleName>
        ${StudentReference.Name.MiddleName}
    </MiddleName>
    </#if>
    <#if (StudentReference.Name.LastName[0])??>
    <LastName>
        ${StudentReference.Name.LastName}
    </LastName>
    </#if>
    <#if (StudentReference.Name.GenerationCodeSuffix[0])??>
    <GenerationCodeSuffix>
        ${StudentReference.Name.GenerationCodeSuffix}
    </GenerationCodeSuffix>
    </#if>
    <#if (StudentRerenece.Name.MaidenName[0])??>
    <MaidenName>
        ${StudentRerenece.Name.MaidenName}
    </MaidenName>
    </#if>
</Name>
</#if>

<#if (StudentReference.Name[0])??>
<OtherName
    <#if (StudentReference.OtherName.@OtherNameType[0])??>
    OtherNameType="${StudentReference.OtherName.@OtherNameType}"
    </#if>
    >
    <#if (StudentReference.Name.PersonalTitlePrefix[0])??>
    <PersonalTitlePrefix>
        ${StudentReference.Name.PersonalTitlePrefix}
    </PersonalTitlePrefix>
    </#if>
    <#if (StudentReference.Name.FirstName[0])??>
    <FirstName>
        ${StudentReference.Name.FirstName}
    </FirstName>
    </#if>
    <#if (StudentReference.Name.MiddleName[0])??>
    <MiddleName>
        ${StudentReference.Name.MiddleName}
    </MiddleName>
    </#if>
    <#if (StudentReference.Name.LastName[0])??>
    <LastName>
        ${StudentReference.Name.LastName}
    </LastName>
    </#if>
    <#if (StudentReference.Name.GenerationCodeSuffix[0])??>
    <GenerationCodeSuffix>
        ${StudentReference.Name.GenerationCodeSuffix}
    </GenerationCodeSuffix>
    </#if>
</OtherName>
</#if>
<#if (StudentReference.BirthDate[0])??>
<BirthDate>
    ${StudentReference.BirthDate}
</BirthDate>
</#if>
<#if (StudentReference.Sex[0])??>
<Sex>
    ${StudentReference.Sex}
</Sex>
</#if>
<#if (StudentReference.HispanicLatinoEthnicity[0])??>
<HispanicLatinoEthnicity>
    ${StudentReference.HispanicLatinoEthnicity}
</HispanicLatinoEthnicity>
</#if>
<#if (StudentReference.Race[0])??>
<Race>
    <#if (StudentReference.Race.RacialCategory[0])??>
    <RacialCategory>
        ${StudentReference.Race.RacialCategory}
    </RacialCategory>
   </#if>
</Race>
</#if>
</StudentIdentity>
         
