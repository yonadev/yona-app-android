
class ADDCHELLENGS<MobTest::Base

  android do
    $strGoalCat=nil

    buttons(:addgoal, id: "#{$android_package}:id/img_add_goal")
    labels(:glcategory, id: "#{$android_package}:id/goal_title")
    buttons(:savegoal, id: "#{$android_package}:id/btnChallenges")
    button(:gocount, id: "#{$android_package}:id/tab_item_count")
    button(:delgoal, id: "#{$android_package}:id/rightIcon")
    button(:acceptdelete, id: "android:id/button1")
    button(:next, id: "#{$android_package}:id/next")
    button(:cancel, id: "#{$android_package}:id/cancel")
    button(:ok, id: "#{$android_package}:id/ok")
    button(:previous, id: "#{$android_package}:id/previous")
    buttons(:menutab, id: "#{$android_package}:id/tab_image")
    labels(:catgoal, id: "#{$android_package}:id/tab_text")
    buttons(:crdtimepkbtn, id: "#{$android_package}:id/goal_item_layout")
    button(:swpdeltime, id: "#{$android_package}:id/swipe_delete_goal")
    button(:edtimedur, id: "#{$android_package}:id/txtGoalStartTime")
    button(:timedurend, id: "#{$android_package}:id/txtGoalEndTime")
    button(:timepickerdl, id: "#{$android_package}:id/time_picker_dialog")
    button(:timepicker, id: "#{$android_package}:id/time_picker")
    # button(:chllyt, id: "#{$android_package}:id/challengesLayout")
    button(:chllyt, id: "#{$android_package}:id/viewPager")
    button(:chllmcnt, id: "#{$android_package}:id/main_content")



    # Android Methods

    def selectCtg(strgl)
      lsWelemnt = glcategory_elements
      i=0
      # puts "Goal=#{lsWelemnt[i].attribute('text')} and #{strgl}"
      begin
        if(lsWelemnt!=nil && lsWelemnt.length>0)
          while i<lsWelemnt.length do
            if(lsWelemnt[i].attribute('text')==strgl)
              # $strGoalCat=lsWelemnt[i].attribute('text')
              lsWelemnt[i].click
              break
            end
            i=i+1
          end
        else
          puts "Category not found"
        end
      rescue Exception=>e
        puts e.message
        puts e.cause
      end

    end


    def goal_added?
      bool=false
      begin
        weGlcnt = gocount_element.attribute('text')
        i=0
        if(weGlcnt!=nil && weGlcnt.to_i>1)
          bool=true
        end
      rescue Exception=>e
        puts e.message
        return false
      end
     bool
    end

    def deletegoal
      sleep 1
      selectgoal
      sleep 1
      delgoal_element.click
      sleep 1
      acceptdelete_element.click
      sleep 2
    end

    def editgoal
      selectgoal
      sleep 2
      edtimedur_element.click
      timduration('3to9')
      endtime=timedurend_element
      swploc=endtime.location
      swpsize=endtime.size
      # puts "Swipeto=#{swploc[:x]},#{swploc[:y]},#{(swploc[:x]-swpsize[:width])},#{swploc[:y]}"
      COMMON_UI.appium_swipe(swploc[:x],swploc[:y],(swploc[:x]-swpsize[:width]),swploc[:y],1)
      sleep 1
      swpdeltime_element.click
      sleep 1
      acceptdelete_element.click
    end

    def isgoaldeleted?
      bool=false
      puts "Goal count=#{gocount_element.attribute('text').to_i}"
      begin
        if(gocount_element.attribute('text').to_i==1)
          bool=true
        end
      rescue Exception=>e
        puts e.message
        return false
      end
    bool
    end

    def taponbutton(tapcount, touchcount, duration, startX, startY)
      action = Appium::TouchAction.new.tap(tapcount: tapcount, touchcount: touchcount, duration: duration, x: startX, y:startY)
      action.perform
    end


    def selmenutab(lsmenu)
      lswbCat = menutab_elements
       # puts "Length=#{lswbCat.length}"
      if(lswbCat !=nil && lswbCat.length>=4)
        case lsmenu
          when 'prf'
            lswbCat[lswbCat.length-4].click
          when 'buddy'
            lswbCat[lswbCat.length-3].click
          when 'chl'
            lswbCat[lswbCat.length-2].click
          when 'sett'
            puts "click on settings"
            lswbCat[lswbCat.length-1].click
        end
     end
    end
    def selectgoalcat(strcat)
      lswbCat = catgoal_elements
      if(lswbCat!=nil && lswbCat.length==3)
        case strcat
          when 'Credit'
            lswbCat[0].click
          when 'Timezone'
            lswbCat[1].click
          when 'NOGO'
            lswbCat[2].click
        end
      end
    end
    def durationinminutes(strMins)
      lswl = crdtimepkbtn_elements
      if(lswl!=nil && lswl.length>1)
        lswl[1].click
      else
        lswl[0].click
      end
      timduration(strMins)
      ok_element.click
      sleep 3
    end

    def selectgoal
      lsweGls = glcategory_elements
      if(lsweGls!=nil && lsweGls.length>0)
        if(lsweGls.length==2)
          lsweGls[1].click
        else if(lsweGls.length==1)
               lsweGls[0].click
             end
        end
      end
    end

    def timduration(strdur)
      timesz=timepickerdl_element.size
      timelc=timepickerdl_element.location
      clhnd=timepicker_element.size

      varcenterX=timesz[:width]/2+timelc[:x]
      varcenterY=(timesz[:height]/2)+ timelc[:y]
      case strdur
        when '3to6'
          # puts "Tapco=#{tapX} #{tapY}"
          tapX=varcenterX+(clhnd[:height]/4)
          tapY=varcenterY
          taponbutton(1,1,0.5,tapX,tapY)
          sleep 1
          next_element.click
          sleep 1
          tapY=varcenterY + (clhnd[:height]/4)
          # puts "Tapco=#{varcenterX} #{tapY}"
          taponbutton(1,1,0.5,varcenterX,tapY)
          sleep 1
          ok_element.click
          sleep 2

        when '3to9'
          tapX=varcenterX+(clhnd[:height]/4)
          tapY=varcenterY
          taponbutton(1,1,0.5,tapX,tapY)
          sleep 1
          next_element.click
          sleep 1
          tapX=varcenterX-(clhnd[:height]/4)
          tapY=varcenterY
          taponbutton(1,1,0.5,tapX,tapY)
          sleep 1
          ok_element.click
        when 'min180'
          tapX=varcenterX+(clhnd[:height]/4)
          tapY=varcenterY
          taponbutton(1,1,0.5,tapX,tapY)
        when 'min540'
          tapX=varcenterX-(clhnd[:height]/4)
          tapY=varcenterY
          taponbutton(1,1,0.5,tapX,tapY)
      end
    end

    def savegl
      wlcllyt = chllyt_element
      chlsize=wlcllyt.size
      chlloc=wlcllyt.location
      btmscreen=chlsize[:height]+chlloc[:y]
      cntsize = chllmcnt_element.size
      sleep 2
      adele=addgoal_elements
      strloc=(@driver.manage().window().size[:height])/2
      COMMON_UI.appium_swipe((chlsize[:width]/2),strloc,(chlsize[:width]/2),100,1)
      sleep 3
      lswl=savegoal_elements
      if(lswl!=nil && lswl.length>1)
        wlsv=lswl[1]
      else
        wlsv=lswl[0]
      end
      wlsv.click
      sleep 2
    end

  end
end