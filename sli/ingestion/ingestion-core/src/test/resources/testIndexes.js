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


db["application"].ensureIndex({"body.admin_visible":1});
db["customRole"].ensureIndex({"body.realmId":1});  // comment
db["tenant"].ensureIndex({"body.tenantId":1},{unique:true});  
//Another Comment
db["userSession"].ensureIndex({"body.expiration":1,"body.hardLogout":1,"body.appSession.token":1});
//Invalid Indexes
db["realm"].addIndex({"body.realmId:1"});
db["usersession"].ensureIndex({"body.appSession.token":1}, unique:true}); 


