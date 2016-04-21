#TODO - Needs work
# This hack is use to iterate through scenarios to work around some of cucumber limitations
devices = $devices
puts "list of devices #{devices}"
devices = devices.to_s.split('avd:').select { |dvc| dvc != '' } if devices.kind_of?(String)

devices.each do |device|
  puts "device #{device}"
  system "rake android_avd['@#{$tag}','avd:#{device}']"
end
