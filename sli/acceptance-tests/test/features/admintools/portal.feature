@smoke @no_ingestion_hooks
Feature:
  As an administrating user
  In order to navigate using the admin application
  I want to be able see information and manage my account

Background:
  Given I have an open browser

Scenario: A user sees the basic portal header and footer
  Given I am a valid inBloom operator
   When I attempt to go to the default administration page
   Then I should see a portal header with my e-mail address
