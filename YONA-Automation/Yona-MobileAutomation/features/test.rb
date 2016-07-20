# # require 'net/http'
# #
# # response = Net::HTTP.get('example.com', '/index.html')
# # Net::HTTP.get
# # # puts response
# # puts response.body

# require_relative '../features/support/pages/adddevice'
# obj = AddDevice.new
# obj.singUpAPIs
# puts "Mobile Number=#{$strMobliNumb}, Yona Password=#{$yona_pwd} and NewDevicepassword=#{$newDevicePwd}"
# # # #
# # # #
str='http://85.222.227.142/users/7a7b3fc2-fa58-4944-902e-08be25e38df4?includePrivateData=true'
puts  /(?<=users\/)[^}]*(?=\?)/.match(str)

