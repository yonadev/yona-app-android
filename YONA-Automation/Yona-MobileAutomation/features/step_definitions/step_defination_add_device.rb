Then (/^User has navigated to Login screen from Welcome screen$/) do
  on(Signup).checkWelcomeScreen
  sleep 3
end

Then(/^User has sign to Yona from first device$/) do
  sleep 2
  strPrfx="+31".to_s
  $strMobliNumb = rand(1000000000).to_s
  mNum=strPrfx+$strMobliNumb
  on(AddDevice).singUpAPIs(mNum)
end

When(/^Make a Add device request from settings screen$/) do
  sleep 2
  strPrfx="+31".to_s
  puts "Mobile=#{$strMobliNumb}"
  mNum=strPrfx+$strMobliNumb
  on(AddDevice).addNewDevic(mNum)
end


Then(/^Click on Login button in another device$/) do
  sleep 2
  puts "Mobile=#{$strMobliNumb}"
  on(AddDevice).login_element.click()
end

And(/^Enters Mobile number and code generated above$/) do
  sleep 2
  puts "Mobile=#{$strMobliNumb}"
  on(AddDevice).enterDetails($strMobliNumb)
end

Then(/^Clicks on VORIGE button$/) do
  sleep 2
  on(AddDevice).vorige_element.click()
end



And(/^User is logged in to new device$/) do
  puts "New divce added successfully"
  sleep 2
end




