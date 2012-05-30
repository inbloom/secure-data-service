
<CourseOfferingIdentity>
    <#if (CourseOffering.LocalCourseCode[0])??>
        <LocalCourseCode>${CourseOffering.LocalCourseCode}</LocalCourseCode>
    </#if>

    <#list CourseOffering.CourseReference.CourseIdentity.CourseCode as courseCode>
        <CourseCode
            <#if (courseCode.@IdentificationSystem[0])??>
                IdentificationSystem="${courseCode.@IdentificationSystem}"
            </#if>
            <#if (coureseCode.@AssigningOrganizationCode[0])??>
                AssigningOrganizationCode="${courseCode.@AssigningOrganizationCode[0]}"
            </#if>
            >
            <#if (courseCode.ID[0])??>
                <ID>${courseCode.ID}</ID>
            </#if>
        </CourseCode>
    </#list>

    <#if (CourseOffering.SessionReference.SessionIdentity.Term[0])??>
        <Term>${CourseOffering.SessionReference.SessionIdentity.Term}</Term>
    </#if>

    <#if (CourseOffering.SessionReference.SessionIdentity.SchoolYear[0])??>
        <SchoolYear>${CourseOffering.SessionReference.SessionIdentity.SchoolYear}</SchoolYear>
    </#if>

    <#if (CourseOffering.SchoolReference.EducationalOrgIdentity.StateOrganizationId[0])?? >
    <StateOrganizationId>${CourseOffering.SchoolReference.EducationalOrgIdentity.StateOrganizationId}</StateOrganizationId>
    </#if>

    <#list CourseOffering.SchoolReference.EducationalOrgIdentity.EducationOrgIdentificationCode as edorgID >
    <EducationOrgIdentificationCode
        <#if (edorgID.@IdentificationSystem[0])??>
        IdentificationSystem="${edorgID.@IdentificationSystem}"
        </#if>
        >
        <#if (edorgID.ID[0])??>
        <ID>${edorgID.ID}</ID>
        </#if>
    </EducationOrgIdentificationCode>
    </#list>
</CourseOfferingIdentity>