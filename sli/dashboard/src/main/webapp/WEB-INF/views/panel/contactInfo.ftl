<@includePanelModel panelId="contactInfo"/>
<#assign id = getDivId(panelConfig.id)>

<#assign addressSize = panelData.address?size>

<div class="header">
	Contact Information 
</div>
<div class="studentContactInfo">
	<!-- display telephone numbers for student -->
	<div class="section">
		<#list panelData.telephone as telephone>
			<div class = "contactInfoRow">
				<div class = "contactInfoCol1">
					${telephone.telephoneNumberType}:
				</div>
				<div class = "contactInfoCol2">
					<!-- The first phone number listed should be bolded. -->
					<#if telephone_index == 0>
						<div class = "bold">
					</#if>
					${telephone.telephoneNumber}
					<#if telephone_index == 0>
						</div>
					</#if>
				</div>
			</div>
		</#list>
	</div>
	
	<div class="section">
		<#list panelData.electronicMail as electronicMail>
			<div class = "contactInfoRow">
				<div class="contactInfoCol1">
					<!-- show only once -->
					<#if electronicMail_index == 0>
					E-mail:
					</#if>
				</div>
				<div class="contactInfoCol2">
					${electronicMail.emailAddress}
				</div>
			</div>
		</#list>
	</div>
	
	<div class="section">
		<#list panelData.address as address>
			<div class = "contactInfoRow">
				<div class="contactInfoCol1">
				<!-- show only once -->
				<#if address_index == 0>
					Address:
				</#if>
				</div>
				<div class="contactInfoCol2">
					${address.streetNumberName}
					<!-- 
					ignore apartmentRoomSuiteNumber is null.
					otherwise display on the first line separated by comma after streetNumberName
					-->
					<#if address.apartmentRoomSuiteNumber ??>
					, ${address.apartmentRoomSuiteNumber}
					</#if>
					
					<!--
					ignore BuildingSiteNumber if null otherwise display it on its own line
					-->
					<#if address.buildingSiteNumber ??>
					<br>
					${address.buildingSiteNumber}
					</#if>
					<br>
					${address.city}, ${address.stateAbbreviation} ${address.postalCode}
					<#if address.countryCode ?? && address.countryCode != "US">
						<br>
						${address.countryCode}
					</#if>
				</div>
			</div>
		</#list>
	</div>
</div>
