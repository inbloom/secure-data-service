<StudentIdentity>
<#if (Student.StudentUniqueStateId[0])??>
<StudentUniqueStateId>${Student.StudentUniqueStateId}</StudentUniqueStateId >
</#if>
<#list Student.StudentIdentificationCode as idCode>
<StudentIdentificationCode
    <#if (idCode.@IdentificationSystem[0])??>
    IdentificationSystem="${idCode.@IdentificationSystem}"
    </#if>
    <#if (idCode.@AssigningOrganizationCode[0])??>
    AssigningOrganizationCode="${idCode.@AssigningOrganizationCode}"
    </#if>
    >
    <IdentificationCode>${idCode.IdentificationCode}</IdentificationCode>
</StudentIdentificationCode>
</#list>
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
    <#if (Student.Name.LastSurname[0])??>
    <LastSurname>${Student.Name.LastSurname}</LastSurname>
    </#if>
    <#if (Student.Name.GenerationCodeSuffix[0])??>
    <GenerationCodeSuffix>${Student.Name.GenerationCodeSuffix}</GenerationCodeSuffix>
    </#if>
    <#if (StudentRerenece.Name.MaidenName[0])??>
    <MaidenName>${StudentRerenece.Name.MaidenName}</MaidenName>
    </#if>
</Name>
</#if>

<#list Student.OtherName as otherName>
<OtherName
    <#if (otherName.@OtherNameType[0])??>
    OtherNameType="${otherName.@OtherNameType}"
    </#if>
    >
    <#if (otherName.PersonalTitlePrefix[0])??>
    <PersonalTitlePrefix>${otherName.PersonalTitlePrefix}</PersonalTitlePrefix>
    </#if>
    <#if (otherName.FirstName[0])??>
    <FirstName>${otherName.FirstName}</FirstName>
    </#if>
    <#if (otherName.MiddleName[0])??>
    <MiddleName>${otherName.MiddleName}</MiddleName>
    </#if>
    <#if (otherName.LastSurname[0])??>
    <LastSurname>${otherName.LastSurname}</LastSurname>
    </#if>
    <#if (otherName.GenerationCodeSuffix[0])??>
    <GenerationCodeSuffix>${otherName.GenerationCodeSuffix}</GenerationCodeSuffix>
    </#if>
</OtherName>
</#list>
<#if (Student.BirthData.BirthDate[0])??>
<BirthDate>${Student.BirthData.BirthDate}</BirthDate>
</#if>
<#if (Student.Sex[0])??>
<Sex>${Student.Sex}</Sex>
</#if>
<#if (Student.HispanicLatinoEthnicity[0])??>
<HispanicLatinoEthnicity>${Student.HispanicLatinoEthnicity}</HispanicLatinoEthnicity>
</#if>
<#if (Student.Race[0])??>
<Race>
    <#list Student.Race.RacialCategory as racialCategory>
    <RacialCategory>${racialCategory}</RacialCategory>
   </#list>
</Race>
</#if>
</StudentIdentity>

