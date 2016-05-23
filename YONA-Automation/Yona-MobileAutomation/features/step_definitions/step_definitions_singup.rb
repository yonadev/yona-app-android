Given(/^User is on Home page and can see two options Login and Join$/) do
  sleep 2
  on(Signup).checkWelcomeScreen

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
  sleep 2
  on(Signup).enterPin


end

Then(/^Sets the pin for application login$/) do
  sleep 2
  on(Signup).enterPin

end

And(/^Confirms the pin for application login$/) do
  sleep 2
  on(Signup).enterPin
  sleep 4
end

Then(/^User is landed on Challenges screen$/) do
  expect(on(Signup).landed_on_challenges?).to be_truthy
end
