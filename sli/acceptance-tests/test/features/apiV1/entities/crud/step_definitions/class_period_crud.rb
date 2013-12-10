When /^I POST a class period$/ do
  @expected_entity = {
                       "classPeriodName" => "First Period",
                       "educationOrganizationId" => "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb"
                     }
  post_entity("classPeriods")
end

When /^I GET the class period$/ do
  get_entity
end

When /^I try the not supported PUT for the class period$/ do
  unsupported_put("classPeriods")
end

When /^I try the not supported PATCH for the class period$/ do
  unsupported_patch("classPeriods")
end

Then /^I DELETE the class period$/ do
  deleteEntity("classPeriods")
end

When /^I POST a custom class period$/ do
  post_custom_entity("classPeriods")
end

When /^I GET the custom class period$/ do
  get_custom_entity("classPeriods")
end

When /^I PUT a custom class period$/ do
  put_custom_entity("classPeriods")
end

When /^I DELETE the custom class period$/ do
  delete_custom_entity("classPeriods")
end

Then /^I GET the deleted custom class period$/ do
  get_deleted_custom_entity("classPeriods")
end

When /^I try the not supported PATCH for custom class period$/ do
  patch_custom_entity("classPeriods")
end

When /^I try the not supported POST for the class period id endpoint$/ do
  post_id("classPeriods")
end

When /^I try the not supported PUT for the class period list endpoint$/ do
  put_list("classPeriods")
end

When /^I try the not supported PATCH for the class period list endpoint$/ do
  patch_list("classPeriods")
end

When /^I try the not supported DELETE for the class period list endpoint$/ do
  delete_list("classPeriods")
end


