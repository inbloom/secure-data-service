# Load the rails application
require File.expand_path('../application', __FILE__)

# Put this here until we fix storing OAuth object directly in session
require "oauth_helper"

# Initialize the rails application
DbRails::Application.initialize!
