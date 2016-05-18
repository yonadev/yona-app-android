Given(/^User is logged on to Yona app$/)do
  puts "Already Logged in"

end

Then(/^Click on add goal button$/)do
  on(ADDCHELLENGS).addgoal_element.click
  sleep 3
end

And(/^select the category on next page.$/)do
  on(ADDCHELLENGS).selectCtg 'News'
  sleep 2
end

Then(/^Click on Save goal button$/)do
  on(ADDCHELLENGS).savegoal_element.click
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
