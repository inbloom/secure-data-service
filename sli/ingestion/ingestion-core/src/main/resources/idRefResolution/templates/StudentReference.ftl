<#if (Student[0])??>
<#assign student=Student>
<#elseif (StudentReference[0])??>
<#assign student=StudentReference.StudentIdentity>
</#if>

<StudentIdentity>
<#if (student.StudentUniqueStateId[0])??>
<StudentUniqueStateId>${student.StudentUniqueStateId}</StudentUniqueStateId >
</#if>
<#list student.StudentIdentificationCode as idCode>
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
<#if (student.Name[0])??>
<Name
    <#if (student.Name.@Verification[0])??>
    Verification="${student.Name.@Verification}"
    </#if>
    >
    <#if (student.Name.PersonalTitlePrefix[0])??>
    <PersonalTitlePrefix>${student.Name.PersonalTitlePrefix}</PersonalTitlePrefix>
    </#if>
    <#if (student.Name.FirstName[0])??>
    <FirstName>${student.Name.FirstName}</FirstName>
    </#if>
    <#if (student.Name.MiddleName[0])??>
    <MiddleName>${student.Name.MiddleName}</MiddleName>
    </#if>
    <#if (student.Name.LastSurname[0])??>
    <LastSurname>${student.Name.LastSurname}</LastSurname>
    </#if>
    <#if (student.Name.GenerationCodeSuffix[0])??>
    <GenerationCodeSuffix>${student.Name.GenerationCodeSuffix}</GenerationCodeSuffix>
    </#if>
    <#if (student.Name.MaidenName[0])??>
    <MaidenName>${student.Name.MaidenName}</MaidenName>
    </#if>
</Name>
</#if>

<#list student.OtherName as otherName>
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
<#if (student.BirthData.BirthDate[0])??>
<BirthDate>${student.BirthData.BirthDate}</BirthDate>
</#if>
<#if (student.Sex[0])??>
<Sex>${student.Sex}</Sex>
</#if>
<#if (student.HispanicLatinoEthnicity[0])??>
<HispanicLatinoEthnicity>${student.HispanicLatinoEthnicity}</HispanicLatinoEthnicity>
</#if>
<#if (student.Race[0])??>
<Race>
    <#list student.Race.RacialCategory as racialCategory>
    <RacialCategory>${racialCategory}</RacialCategory>
   </#list>
</Race>
</#if>
</StudentIdentity>

