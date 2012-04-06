<div class="tabular">
    <table ><thead><tr><th></th><td style="width:200px"><h6>${singleContactName}</h6></td></tr></thead><tbody>
    <tr><th></th><td></td></tr>
	<!-- display telephone numbers for student -->
		<#list singleContact.telephone as telephone>
		<tr>
				<th>
					${telephone.telephoneNumberType}:
				</th>
				<td style="width:200px;<#if telephone_index == 0>font-weight:bold;</#if>">
					${telephone.telephoneNumber}
				</td>
			</tr>
		</#list>
	<tr><th></th><td></td></tr>
		<#list singleContact.electronicMail as electronicMail>
			<tr>
				<th>
					<!-- show only once -->
					<#if electronicMail_index == 0>
					E-mail:
					</#if>
				</th>
				<td style="width:200px">
					${electronicMail.emailAddress}
				</td>
			</tr>
		</#list>
	<tr><th></th><td></td></tr>
		<#list singleContact.address as address>
			<tr>
				<th>
				<!-- show only once -->
				<#if address_index == 0>
					Address:
				</#if>
				</th>
				<td style="width:200px;line-height:12px">
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
