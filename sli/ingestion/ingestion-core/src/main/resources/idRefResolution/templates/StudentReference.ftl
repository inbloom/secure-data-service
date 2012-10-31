<#if (Student[0])??>
<#assign student=Student>
<#elseif (StudentReference[0])??>
<#assign student=StudentReference.StudentIdentity>
</#if>

<StudentIdentity>
<StudentUniqueStateId>${student.StudentUniqueStateId}</StudentUniqueStateId >
</StudentIdentity>

