# Add NOGO Goal

Given(/^User is logged in to Yona app and on Profile page$/)do
  # on(Signup).enterPin
  on(ADDCHELLENGS).selmenutab 'prf'
end

Then(/^click on Profile icon on top left corner$/)do
  sleep 2
  on(Profile).ptviewic_element.click
end

Then(/^Able to view details like first name,last name, nick name,mobile number and edit icon$/)do
  sleep 2
  expect(on(Profile).isflddisplayed(on(Profile).ptfname_element)).to be_truthy
  expect(on(Profile).isflddisplayed(on(Profile).ptlname_element)).to be_truthy
  sleep 1
  expect(on(Profile).isflddisplayed(on(Profile).ptnickname_element)).to be_truthy
  COMMON_UI.appium_swipe(533,1550,513,999,1)
  sleep 1
  expect(on(Profile).isflddisplayed(on(Profile).ptmobilen_element)).to be_truthy
  expect(on(Profile).isflddisplayed(on(Profile).pteditic_element)).to be_truthy

end