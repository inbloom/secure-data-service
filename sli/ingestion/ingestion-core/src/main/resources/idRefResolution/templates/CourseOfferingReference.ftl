
<CourseOfferingIdentity>
    <#if (CourseOffering.LocalCourseCode[0])??>
        <LocalCourseCourseCode>${CourseOffering.LocalCourseCode}</LocalCourseCourseCode>
    </#if>

    <#if (CourseOffering.CourseCode[0])??>
        <CourseCode
            <#if (CourseOffering.CourseCode.@IdentificationSystem[0])??>
                IdentificationSystem="${CourseOffering.CourseCode.@IdentificationSystem}"
            </#if>
            <#if (CourseOffering.CourseCode.@AssigningOrganizationCode[0])??>
                AssigningOrganizationCode="${CourseOffering.CourseCode.@AssigningOrganizationCode[0]}"
            </#if>
            >
            <#if (CourseOffering.CourseCode.ID[0])??>
                <ID>${CourseOffering.CourseCode.ID}</ID>
            </#if>
        </CourseCode>
    </#if>

    <#if (CourseOffering.Term[0])??>
        <Term>${CourseOffering.Term}</Term>
    </#if>

    <#if (CourseOffering.SchoolYear[0])??>
        <SchoolYear>${CourseOffering.SchoolYear}</SchoolYear>
    </#if>

    <#if (CourseOffering.StateOrganizationId[0])?? >
    <StateOrganizationId>${CourseOffering.StateOrganizationId}</StateOrganizationId>
    </#if>

    <#if (CourseOffering.CourseOfferingIdentificationCode[0])?? >
    <CourseOfferingIdentificationCode
        <#if (CourseOffering.CourseOfferingIdentificationCode.@IdentificationSystem[0])??>
        IdentificationSystem="${CourseOffering.CourseOfferingIdentificationCode.@IdentificationSystem}"
        </#if>
        >
        <#if (CourseOffering.CourseOfferingIdentificationCode.ID[0])??>
        <ID>${CourseOffering.CourseOfferingIdentificationCode.ID}</ID>
        </#if>
    </CourseOfferingIdentificationCode>
    </#if>
</CourseOfferingIdentity>