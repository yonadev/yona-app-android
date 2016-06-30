Given(/User enters the pin and login to Yona$/)do
  on(Signup).enterPin
end

When(/^User is logged in to Yona app and on Settings tab$/)do
  # on(Signup).enterPin
end




When(/^Click on Settings tab$/)do
  sleep 1
  on(ADDCHELLENGS).selmenutab 'sett'
end


Then(/^Selects the Unsubscribe link from settigs screen$/)do
  sleep 2
  if (on(Settings).settingsScreen)
    on(Settings).clickUnsubscribe
  end
end


Then(/^clicks on Cancel button in confirmation dialog$/)do
  sleep 2
  on(Settings).clickCancel
end

And(/^User remains on Settins screen$/) do
  sleep 2
  expect(on(Settings).settingsScreen).to be_truthy
end


And(/^User is already on settings tab$/) do
  sleep 2
  on(ADDCHELLENGS).selmenutab 'sett'
end

And(/^clicks on OK button in confirmation dialog$/) do
  sleep 2
  on(Settings).clickOk

end


And(/^Navigated to Sighup Home page$/) do
  sleep 4
  expect(on(Settings).onHomepage?).to be_truthy
end


Then(/Trigger unsubscribe from device A API call$/) do
  sleep 2
  on(AddDevice).unsubscribeCall

end


Then(/Push the app in backgroun$/) do
   on(Settings).minimizeandrelaunchapp

end

Then(/Gets a error message stating user with given id not found$/) do
  sleep 4
  expect(COMMON_UI.eledisplayed?(on(Settings).unsubscribedl_element)).to be_truthy
end

Then(/User is on home page on device B$/) do
  sleep 2
  on(Profile).resetYona
  sleep 2
  on(Signup).checkWelcomeScreen


end


