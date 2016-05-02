Given(/^User has sign to Yona from first device$/) do
  on(AddDevice).singUpAPIs

end

When(/^Make a Add device request from settings screen$/) do
  on(AddDevice).addNewDevic
end


Then(/^Click on Login button in another device$/) do
  on(AddDevice).login_element.click()
end

And(/^Enters Mobile number and code generated above$/) do
  on(AddDevice).enterDetails
end

Then(/^Clicks on VORIGE button$/) do
  on(AddDevice).vorige_element.click()
end



And(/^User is logged in to new device$/) do
  puts "New divce added successfully"
end




