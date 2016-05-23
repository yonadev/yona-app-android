
class ADDCHELLENGS<MobTest::Base


  android do
    $strGoalCat=nil
    button(:addgoal, id: "#{$android_package}:id/img_add_goal")
    labels(:glcategory, id: "#{$android_package}:id/goal_title")
    button(:savegoal, id: "#{$android_package}:id/btnChallenges")
    button(:gocount, id: "#{$android_package}:id/tab_item_count")
    button(:delgoal, id: "#{$android_package}:id/rightIcon")
    button(:acceptdelete, id: "android:id/button1")


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
        # puts "Goal Count=#{weGlcnt}"
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

        lsweGls = glcategory_elements
        if(lsweGls!=nil && lsweGls.length>0)
          lsweGls[1].click
          sleep 2
          delgoal_element.click
          sleep 2
          acceptdelete_element.click
        end
    end

    def isgoaldeleted?
      bool=false
      begin
        if(addgoal_element.displayed? && gocount_element.attribute('text').to_i==1)
          bool=true
        end
      rescue Exception=>e
        puts e.message
        return false
      end
    bool
    end


  end



end