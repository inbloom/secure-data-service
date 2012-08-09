<@includePanelModel panelId="schoolInfo"/>
<div class="schoolProfile">
    <#if panelData.nameOfInstitution??>
        <h1>${panelData.nameOfInstitution}</h1>
    </#if>
    
    <div class="schoolInfo">
        <div class="school col1 column">

            <div class="tabular">
                <table>
                    <#list panelData.address as address>
                    <tr>
                    <th>Address:</th>
                    <td>
                        <div>${address.streetNumberName}</div>
                        <div>${address.city}, ${address.stateAbbreviation} ${address.postalCode}</div>
                    </td>
                    </tr>
                    </#list>
                    
                    <#list panelData.telephone as telephone>
                    <tr>
                    <th><#if telephone.institutionTelephoneNumberType ??>${telephone.institutionTelephoneNumberType}:</#if></th>
                    <td><#if telephone.telephoneNumber??> ${telephone.telephoneNumber}</#if></td>
                    </tr>
                    </#list>
                    
                    <tr>
                    <th>Grades offered:</th>
                    <td><#if panelData.gradesOfferedCode??>
                        <#list panelData.gradesOfferedCode as gradeOffered>${gradeOffered}<#if gradeOffered_has_next>, </#if></#list>
                        </#if>
                    </td>
                    </tr>                   
                </table>
            </div>
        </div>
    </div>
</div>
