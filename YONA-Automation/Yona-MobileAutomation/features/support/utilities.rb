module Utils
  def take_screenshot(scenario, driver)
    dt = Time.new
    path = "#{Dir.pwd}/features/results"
    date_dir = "#{dt.year}#{dt.month}#{dt.day}"
    create_directory(path, date_dir)
    screenshot_name = "#{ENV['PLATFORM']}_#{ENV['DEVICE']}_#{scenario.name.gsub(' ', '_').gsub(/[^0-9A-Za-z_]/, '')}_#{dt.hour}_#{dt.min}_#{dt.sec}.png"
    screenshot = Dir.pwd + '/features/results/' + date_dir + '/' + screenshot_name
    save_and_embed_image(screenshot, driver)
  end

  def save_screenshot_locally(name, driver)
    dt = Time.new
    screenshot_name = "#{name}_#{dt.day}_#{dt.hour}_#{dt.min}_#{dt.sec}.png"
    dir = Dir.home + '/Screenshots'
    Dir.mkdir(dir) unless Dir.exist?(dir)
    screenshot = dir + '/' + screenshot_name
    driver.save_screenshot(screenshot)
  end

  def create_directory(path, dir_name)
    dir = "#{path}/#{dir_name}"
    Dir.mkdir(path) unless Dir.exist? path
    Dir.mkdir(dir) unless Dir.exist? dir
  end

  def email_results
    #TODO
  end

  def ios_settings
    setup = YAML.load_file(Dir.pwd + '/features/support/settings/ios.yml')
    setup[:app_path] = ENV['IOS_DERIVED_DATA_PATH']+'/'+setup[:app] if setup[:app_path].nil?
    setup
  end

  def android_settings
    YAML.load_file(Dir.pwd + '/features/support/settings/android.yml')
  end

  private
  def save_and_embed_image(path, driver)
    driver.save_screenshot(path)
    embed(path, "image/png")
  end
end