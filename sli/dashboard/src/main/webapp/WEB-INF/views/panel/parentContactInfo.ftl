<div class="tabular">
    <table><thead><tr><th></th><td class="contactInfoData"><h6>${parentContactName}</h6></td></tr></thead><tbody>
	<#if parentContact.studentParentAssociation ??>
		<#list parentContact.studentParentAssociation as studentParentAssociation>
			<tr>
				<th>
					<#if studentParentAssociation.relation ??>
						${studentParentAssociation.relation}:
					</#if>
				</th>
				<td class="contactInfoData">
					<#if parentContact.name ??>
						<#if parentContact.name.firstName ??>
							${parentContact.name.firstName}
						</#if>
						<#if parentContact.name.lastSurname ??>
							${parentContact.name.lastSurname}
						</#if>
					</#if>
				</td>
			</tr>
		</#list>
	</#if>
	<#list parentContact.telephone as telephone>
		<#if telephone.telephoneNumberType ??>
			<tr>
				<th>
					${telephone.telephoneNumberType}:
				</th>
				<td class="contactInfoData">
					${telephone.telephoneNumber}
				</td>
			</tr>
		</#if>
	</#list>
	<#list parentContact.electronicMail as electronicMail>
			<tr>
				<th>
					<!-- show only once -->
					<#if electronicMail.emailAddressType ??>
					       ${electronicMail.emailAddressType}:
				   <#else>
					   E-mail:
					</#if>
				</th>
				<td class="contactInfoData">
					${electronicMail.emailAddress}
				</td>
			</tr>
		</#list>
	<#list parentContact.address as address>
			<tr>
				<th>
				<!-- show only once -->
					Address:
				</th>
				<td class="contactInfoData">
				    <div>
					${address.streetNumberName}<#if address.apartmentRoomSuiteNumber ??>, ${address.apartmentRoomSuiteNumber}</#if>
					<!-- 
					ignore apartmentRoomSuiteNumber is null.
					otherwise display on the first line separated by comma after streetNumberName
					-->
					
					
					<!--
					ignore BuildingSiteNumber if null otherwise display it on its own line
					-->
					</div>
					<#if address.buildingSiteNumber ??>
					
					<div>
					${address.buildingSiteNumber}
					</div>
					</#if>
					
					<div>
					${address.city}, ${address.stateAbbreviation} ${address.postalCode}
					</div>
					<#if address.countryCode ?? && address.countryCode != "US">
						<div>
						${address.countryCode}
						</div>
					</#if>
				</td>
			</tr>
		</#list>
	</tbody></table>
</div>
