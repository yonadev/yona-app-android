# Add NOGO Goal

Given(/^User logs in to Yona app and clicks on Profile tab$/)do
  on(Signup).singnuptoYona
  sleep 1
  on(Signup).checkPermissionPopup
  sleep 1
  on(ADDCHELLENGS).selmenutab 'prf'
end

Then(/^User enters pin on login screen$/) do
  sleep 1
  on(Signup).enterPin
  sleep 1
  on(Signup).checkPermissionPopup
end

Then(/^Clicks on Profile tab$/)do
  sleep 1
  on(ADDCHELLENGS).selmenutab 'prf'
end


Then(/^Click on Profile icon on top left corner$/)do
  sleep 2
  on(Profile).ptviewic_element.click
end

Then(/^Able to view details like first name,last name, nick name,mobile number and edit icon$/)do
  sleep 2
  expect(on(Profile).isflddisplayed(on(Profile).ptfname_element)).to be_truthy
  expect(on(Profile).isflddisplayed(on(Profile).ptlname_element)).to be_truthy
  sleep 1
  on(Profile).swipe_to
  sleep 1
  expect(on(Profile).isflddisplayed(on(Profile).ptnickname_element)).to be_truthy
  expect(on(Profile).isflddisplayed(on(Profile).ptmobilen_element)).to be_truthy
  expect(on(Profile).isflddisplayed(on(Profile).pteditic_element)).to be_truthy

end

Then(/^Quit the Yona app$/)do
  sleep 2
  on(Profile).resetYona

end

Then(/^Update first name, last name, nick name and mobile number$/) do
  # on(Signup).enterName('fname_updated', 'lname_updated')

  on(Profile).editDetails

end

Then(/^Clicks on save icon on top right corner$/)do
  on(Profile).pteditic_element.click
end
