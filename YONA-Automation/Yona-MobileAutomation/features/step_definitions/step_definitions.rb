Given(/^User is on Home page and can see two options Login and Join$/) do
  sleep 2

end

When(/^User clicks on Join button$/) do
  on(Login).join_element.click()

end

And(/^Enters first name and last name$/) do
  sleep 1
  on(Login).setText('fname', 'lname')
end



Then(/^Clicks on Next button$/) do
  sleep 1
  on(Login).next('namescreen')
end


And(/^Then enters Mobile number and Nick Name on next screen$/) do
  sleep 1
  on(Login).setText('155881488', 'Jacky')
end



Then(/^Clicks on Next button on this screen$/) do
  sleep 1
  on(Login).next('mobnumscreen')
  sleep 4

end



Then(/^Clicks on OK button in alert$/) do

  on(Login).clickOK
end

Then(/^Enters pincode fetched from alert$/) do
  sleep 1
  on(Login).enterPin


end

Then(/^Sets the pin for application login$/) do
  sleep 1
  on(Login).enterPin

end

And(/^Confirms the pin for application login$/) do
  sleep 1
  on(Login).enterPin
  sleep 4
end

# Then(/^I enter user name$/) do
#   puts "User entered user name"
#   sleep 3
# end
#
# Then(/^I enter password$/) do
#   puts "User entered password"
# end
#
# Then(/^I click on login button$/) do
#   puts "User clicked on login button"
#   sleep 3
# end
#
# Then(/^I should be logged in$/) do
#   puts "User is logged in to app"
#   sleep 3
# end
