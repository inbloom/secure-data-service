<div class="tabular">
    <table ><thead><tr><th></th><td class="contactInfoData"><h6>${singleContactName}</h6></td></tr></thead><tbody>
	<!-- display telephone numbers for student -->
		<#list singleContact.telephone as telephone>
		<tr>
				<th>
					${telephone.telephoneNumberType}:
				</th>
				<td class="contactInfoData">
					${telephone.telephoneNumber}
				</td>
			</tr>
		</#list>
		<#list singleContact.electronicMail as electronicMail>
			<tr>
				<th>
					<!-- show only once -->
					<#if electronicMail.emailAddressType ??>
					   <#if electronicMail.emailAddressType == "Organization">
					       School E-mail:
				       <#else>
					       ${electronicMail.emailAddressType}:
				       </#if>
				   <#else>
					   E-mail:
					</#if>
				</th>
				<td class="contactInfoData">
					${electronicMail.emailAddress}
				</td>
			</tr>
		</#list>
		<#list singleContact.address as address>
			<tr>
				<th>
				<!-- show only once -->
				<#if address.addressType ??>
					<#if address.addressType == "Home" >
	    				Address:
    				<#else>
    					${address.addressType}:
    				</#if>
				</#if>
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
