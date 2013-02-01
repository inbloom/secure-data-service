=begin 
#--
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

=end 
# This is a very simplistic ActiveResource class that makes use of the
# <api>/system/session/check call which returns all kinds of useful information
# about you and your current API session as well as extending your OAuth
# session.
class Check < SessionResource
  self.site = "#{APP_CONFIG['api_base']}/system/session".gsub("v1/", "")
  self.url_type = "check"
  self.format = ActiveResource::Formats::JsonFormat
end
