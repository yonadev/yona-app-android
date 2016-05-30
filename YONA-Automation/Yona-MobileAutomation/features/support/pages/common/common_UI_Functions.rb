module COMMON_UI

  def COMMON_UI.checkUIKeyboard
    key_present=false

    console_answer = %x{adb shell dumpsys input_method | grep mInputShown}
    # console_answer="mShowRequested=true mShowExplicitlyRequested=false mShowForced=false mInputShown=true"
    console_answer=console_answer.split
    if ((console_answer.length>0)&&(console_answer[3]!=nil))
      console_answer=console_answer[3].split("=")
      if ((console_answer.length>0)&&(console_answer!=nil)&&(console_answer[1]== 'true'))
        # puts console_answer
        key_present=true
      else
        # puts "Could not determine value of mInputShown"
        key_present=false
      end
    else
      # puts "Invalid command output"
      # puts %x{adb shell dumpsys input_method | grep mInputShown}
      key_present=false
    end
    key_present
  end

  def COMMON_UI.appium_swipe(start_x, start_y, end_x, end_y, duration)
    action = Appium::TouchAction.new.swipe(start_x: start_x, start_y: start_y, end_x: end_x, end_y: end_y, duration: duration * 1000)
    action.perform
  end


end