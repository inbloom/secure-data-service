//
// Copyright 2012-2013 inBloom, Inc. and its affiliates.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//


db["application"].ensureIndex({"body.admin_visible":1});  // ?
db["application"].ensureIndex({"body.created_by":1}); 
db["application"].ensureIndex({"body.allowed_for_all_edorgs":1});  //app auth per edorg
db["application"].ensureIndex({"body.authorized_ed_orgs":1});  //app auth per edorg
db["application"].ensureIndex({"body.authorized_for_all_edorgs":1});  //app auth per edorg
db["application"].ensureIndex({"body.client_id":1,"body.client_secret":1},{unique:true});  //app auth
db["application"].ensureIndex({"metaData.tenantId":1}); 

db["customRole"].ensureIndex({"body.realmId":1});  // create custom role for a realm

db["realm"].ensureIndex({"body.idp.id":1});  //oauth login
db["realm"].ensureIndex({"body.tenantId":1,"body.edOrg":1});  //login? saml fedaration
db["realm"].ensureIndex({"body.edOrg":1});  //create custom role for a realm
db["realm"].ensureIndex({"body.uniqueIdentifier":1});  //unique identifier for a realm
db["realm"].ensureIndex({"body.name":1});

db["securityEvent"].ensureIndex({"body.targetEdOrg":1,"body.roles":1});

db["tenant"].ensureIndex({"body.landingZone.ingestionServer":1,"body.landingZone.preload.status":1});//ingestion-startup
db["tenant"].ensureIndex({"body.landingZone.path":1});  //ingestion-job start
db["tenant"].ensureIndex({"body.tenantId":1},{unique:true});  //ingestion-startup
db["tenant"].ensureIndex({"body.landingZone.preload.status":1});  //ingestion pre loading

db["userSession"].ensureIndex({"body.appSession.code.value":1});  //oauth
db["userSession"].ensureIndex({"body.appSession.samlId":1});  //saml fedaration
db["userSession"].ensureIndex({"body.expiration":1,"body.hardLogout":1,"body.appSession.token":1});  //api
db["userSession"].ensureIndex({"body.hardLogout":1,"body.expiration":1});  //api
db["userSession"].ensureIndex({"body.principal.externalId":1});  //api
db["userSession"].ensureIndex({"body.appSession.token":1});  //token
