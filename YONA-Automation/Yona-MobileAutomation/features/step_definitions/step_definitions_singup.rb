Given(/^I am here$/)do
  puts "Hey in here"
end

Then(/^I am there/)do
  puts "Hey in there"
  sleep 4
end


Given(/^User is on Home page and can see two options Login and Join$/) do
  sleep 2
  on(Signup).checkWelcomeScreen
  # puts @driver.manage().window().size[:height]
end


When(/^User clicks on Join button$/) do
  sleep 2
  on(Signup).join_element.click()

end

And(/^Enters first name and last name$/) do
  sleep 1
  on(Signup).enterName('fname', 'lname')
end



Then(/^Clicks on Next button$/) do
  sleep 1
  on(Signup).next('namescreen')
end


And(/^Then enters Mobile number and Nick Name on next screen$/) do
  begin
    strMob=rand(1000000000).to_s
  end while strMob.length!=9
  on(Signup).enterMobile(strMob.to_s, 'Jacky')
end



Then(/^Clicks on Next button on this screen$/) do
  sleep 2
  on(Signup).next('mobnumscreen')
  sleep 5

end



Then(/^Clicks on OK button in alert$/) do

  on(Signup).clickOK
end

Then(/^Enters pincode fetched from alert$/) do
  sleep 3
  on(Signup).enterPin
end

Then(/^Sets the pin for application login$/) do
  sleep 2
  on(Signup).enterPin

end

And(/^Confirms the pin for application login$/) do
  sleep 2
  on(Signup).enterPin
end

Then(/^User is landed on Challenges screen$/) do
  expect(on(Signup).landed_on_challenges?).to be_truthy
end

Then(/^Check for permission popup and click OK if found$/) do
  sleep 2
  on(Signup).checkPermissionPopup
end

Then (/^User has intalled Yona app and launched it$/)do
  on(Profile).resetYona
  sleep 3
end