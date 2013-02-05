=begin

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


module AppsHelper
  def sortable(title, value = nil)
    value ||= title.downcase
    direction = params[:direction] == "desc" ? "asc" : "desc"
    link_to title, {:sort => value, :direction => direction}, {:class => direction}
  end

  # For client_id and client_secret, return 'Pending' if app isn't yet registered
  def client_field_value(app, field)
    if app.attributes.has_key? 'registration' and app.registration.status == 'APPROVED'
      app.attributes[field]
    else
      "Pending"
    end
  end

end
