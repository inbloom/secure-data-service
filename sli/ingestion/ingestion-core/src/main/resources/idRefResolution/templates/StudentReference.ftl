<StudentIdentity>
<#if (Student.StudentUniqueStateId[0])??>
<StudentUniqueStateId>${Student.StudentUniqueStateId}</StudentUniqueStateId >
</#if>
<#if (Student.StudentIdentificationCode[0])??>
<StudentIdentificationCode
    <#if (Student.StudentIdentificaitonCode.@identificationSystem[0])??>
    IdentificationSystem="${Student.StudentIdentificaitonCode.@identificationSystem}"
    </#if>
    <#if (Student.StudentIdentificaitonCode.@AssigningOrganizationCode[0])??>
    AssigningOrganizationCode="${Student.StudentIdentificaitonCode.@AssigningOrganizationCode}"
    </#if>
    >
    <#if (Student.StudentIdentificationCode.IdentificationCode[0])??>
    <IdentificationCode>${Student.StudentIdentificationCode.IdentificationCode}</IdentificationCode>
    </#if>
</StudentIdentificationCode>
</#if>
<#if (Student.Name[0])??>
<Name
    <#if (Student.Name.@Verification[0])??>
    Verification="${Student.Name.@Verification}"
    </#if>
    >
    <#if (Student.Name.PersonalTitlePrefix[0])??>
    <PersonalTitlePrefix>${Student.Name.PersonalTitlePrefix}</PersonalTitlePrefix>
    </#if>
    <#if (Student.Name.FirstName[0])??>
    <FirstName>${Student.Name.FirstName}</FirstName>
    </#if>
    <#if (Student.Name.MiddleName[0])??>
    <MiddleName>${Student.Name.MiddleName}</MiddleName>
    </#if>
    <#if (Student.Name.LastName[0])??>
    <LastName>${Student.Name.LastName}</LastName>
    </#if>
    <#if (Student.Name.GenerationCodeSuffix[0])??>
    <GenerationCodeSuffix>${Student.Name.GenerationCodeSuffix}</GenerationCodeSuffix>
    </#if>
    <#if (StudentRerenece.Name.MaidenName[0])??>
    <MaidenName>${StudentRerenece.Name.MaidenName}</MaidenName>
    </#if>
</Name>
</#if>

<#if (Student.Name[0])??>
<OtherName
    <#if (Student.OtherName.@OtherNameType[0])??>
    OtherNameType="${Student.OtherName.@OtherNameType}"
    </#if>
    >
    <#if (Student.Name.PersonalTitlePrefix[0])??>
    <PersonalTitlePrefix>${Student.Name.PersonalTitlePrefix}</PersonalTitlePrefix>
    </#if>
    <#if (Student.Name.FirstName[0])??>
    <FirstName>${Student.Name.FirstName}</FirstName>
    </#if>
    <#if (Student.Name.MiddleName[0])??>
    <MiddleName>${Student.Name.MiddleName}</MiddleName>
    </#if>
    <#if (Student.Name.LastName[0])??>
    <LastName>${Student.Name.LastName}</LastName>
    </#if>
    <#if (Student.Name.GenerationCodeSuffix[0])??>
    <GenerationCodeSuffix>${Student.Name.GenerationCodeSuffix}</GenerationCodeSuffix>
    </#if>
</OtherName>
</#if>
<#if (Student.BirthDate[0])??>
<BirthDate>${Student.BirthDate}</BirthDate>
</#if>
<#if (Student.Sex[0])??>
<Sex>${Student.Sex}</Sex>
</#if>
<#if (Student.HispanicLatinoEthnicity[0])??>
<HispanicLatinoEthnicity>${Student.HispanicLatinoEthnicity}</HispanicLatinoEthnicity>
</#if>
<#if (Student.Race[0])??>
<Race>
    <#if (Student.Race.RacialCategory[0])??>
    <RacialCategory>${Student.Race.RacialCategory}</RacialCategory>
   </#if>
</Race>
</#if>
</StudentIdentity>

