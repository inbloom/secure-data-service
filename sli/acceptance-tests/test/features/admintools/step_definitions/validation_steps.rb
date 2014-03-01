Then /^I should (not )?see validation errors for:$/ do |not_see, table|
  table.raw.each do |row|
    has_validation(row.first, !not_see)
  end
end

Then /^I should (not )?see required validation errors for:$/ do |not_see, table|
  table.raw.each do |row|
    message = "#{row.first} can't be blank"
    has_validation(message, !not_see)
  end
end

Then /^I should (not )?see minimum length validation errors for:$/ do |not_see, table|
  table.rows_hash.each do |field, length|
    message = "#{field} must be at least #{length} characters long"
    has_validation(message, !not_see)
  end
end

Then /^I should (not )?see co-dependent validation errors for:$/ do |not_see, table|
  table.raw.each do |row|
    message = "#{row.first} can't be blank if #{row.last} is non-blank"
    has_validation(message, !not_see)
  end
end

def has_validation(message, present=true)
  error_selector = '#error_explanation li'
  if present
    browser.page.should have_selector(error_selector, :text => message)
  else
    browser.page.should have_no_selector(error_selector, :text => message)
  end
end

