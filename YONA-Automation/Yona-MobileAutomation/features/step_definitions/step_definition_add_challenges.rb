# Add NOGO Goal
Given(/^User is logged in to Yona app and on Challenges tab$/)do
  # on(Signup).enterPin
  on(ADDCHELLENGS).selmenutab 'chl'
end

Given(/^User is already on challenges tab$/)do
  puts "Do Nothing"
end


And(/^Click on NOGO goal$/)do
  sleep 2
  on(ADDCHELLENGS).selectgoalcat 'NOGO'

end

Then(/^Click on add goal button$/)do
  sleep 2
  lswl = on(ADDCHELLENGS).addgoal_elements
  if(lswl!=nil && lswl.length>1)
    lswl[1].click
  else
    lswl[0].click
  end
end

And(/^select the category on next page.$/)do
  sleep 2
  on(ADDCHELLENGS).selectCtg 'News'
  sleep 2
end

Then(/^Click on Save goal button$/)do
  on(ADDCHELLENGS).savegl
  sleep 2
end

And(/^Goal with selected category is added as NOGO challenge$/)do
    expect(on(ADDCHELLENGS).goal_added?).to be_truthy
    sleep 2
end

Then(/^Select the goal and delete it$/)do
  on(ADDCHELLENGS).deletegoal
end

And(/^Goal is deleted and user is navigated back to Challenges home screen$/)do
  expect(on(ADDCHELLENGS).isgoaldeleted?).to be_truthy
end

# Add Time Zone Goal

Then(/^Click on Timzone goal$/)do
  sleep 2
  on(ADDCHELLENGS).selectgoalcat 'Timezone'

end


And(/^select the category of Timezone goal on next page$/)do
  sleep 2
  on(ADDCHELLENGS).selectCtg 'News'

end


And(/^Specify the time duration and click on OK button$/)do
  sleep 1
  on(ADDCHELLENGS).timduration('3to6')

end

And(/^Edit the goal and delete time duration$/)do
  sleep 2
  on(ADDCHELLENGS).editgoal
end




# Add Credit Goal
And(/^Click on Credit goals$/)do
  sleep 2
  on(ADDCHELLENGS).selectgoalcat 'Credit'

end

And(/^Specify the time duration in minutes$/)do
   sleep 1
  on(ADDCHELLENGS).durationinminutes('min180')

end

And(/^Edit the goal and change the duration$/)do
  sleep 1
  on(ADDCHELLENGS).selectgoal
  on(ADDCHELLENGS).durationinminutes('min540')

end

