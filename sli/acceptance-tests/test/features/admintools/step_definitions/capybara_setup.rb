=begin

Copyright 2012-2014 inBloom, Inc. and its affiliates.

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
require 'capybara'
require 'capybara-screenshot'
require 'capybara-screenshot/cucumber'

require_relative '../../utils/db_client.rb'
require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'

class Browser
  include Capybara::DSL
  def initialize
    Capybara.default_driver = :selenium
    Capybara.reset_session!
  end

  def reset_session!
    Capybara.reset_session!
  end

  def confirm_popup
    page.driver.browser.switch_to.alert.accept
  end

  def dismiss_popup
    page.driver.browser.switch_to.alert.dismiss
  end

  def alert_popup_message
    page.driver.browser.switch_to.alert.text
  end
end