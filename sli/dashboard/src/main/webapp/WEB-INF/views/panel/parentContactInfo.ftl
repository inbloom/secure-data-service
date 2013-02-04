<#--
  Copyright 2012-2013 inBloom, Inc. and its affiliates.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<div class="tabular">
	<table>
		<thead>
			<#list parentContact.studentParentAssociations as studentParentAssociation>
				<tr><th></th><td class="contactInfoData"><h6>
					<#if studentParentAssociation.primaryContactStatus ??>
						PRIMARY
					<#else>
						OTHER
					</#if>
				</h6></td></tr>
			</#list>
		</thead>
		<tbody>
			<#list parentContact.studentParentAssociations as studentParentAssociation>
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
			<#list parentContact.telephone as telephone>
				<tr>
					<th>
						<#if telephone.telephoneNumberType ??>
							${telephone.telephoneNumberType}:
						<#else>
							Phone:
						</#if>
					</th>
					<td class="contactInfoData">
						<#if telephone.telephoneNumber ??>
							${telephone.telephoneNumber}
						</#if>
					</td>
				</tr>
			</#list>
			<#list parentContact.electronicMail as electronicMail>
					<tr>
						<th>
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
							<#if address.addressType ??>
								${address.addressType}:
							<#else>
								Address:
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
		</tbody>
	</table>
</div>
