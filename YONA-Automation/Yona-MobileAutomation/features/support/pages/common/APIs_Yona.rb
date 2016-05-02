require 'net/http'
require 'crack'
require 'json'

module COMMONAPIS
  def COMMONAPIS.add_user (url,pl_json)
    begin

      # Parse the URl and create new instance of http request
      uri = URI.parse(url)
      https = Net::HTTP.new(uri.host,uri.port)

      # Creates a request instance and form the request by specifying
      # content type, request parameters and request body in json format
      req = Net::HTTP::Post.new(uri.path)
      req.set_content_type('application/json')
      req['Yona-Password'] = $yona_pwd
      req.body = pl_json
      # Makes the API request and stores response in variable
      res = https.request(req)

      if res.code == '201'
        puts "User created:Code: #{res.code} Message:#{res.message}:"
        json = JSON.parse(res.body)
        COMMONAPIS.validateMobileNumber(json["_links"]["yona:confirmMobileNumber"]["href"].to_s)
      else
        "Seems like an issue with API, user not created"
      end
    rescue => e
      # puts "Response=#{e.response}"
      puts "Message=#{e.message}"
      puts "Cause=#{e.cause}"
    end
  end


  # Makes a POST API call to validate  mobile Number
  def COMMONAPIS.validateMobileNumber(mobNumberUri)
    begin

      #Parse the URl and create new instance of http request
      uri = URI.parse(mobNumberUri)
      https = Net::HTTP.new(uri.host,uri.port)

      # Creates a request instance and form the request by specifying
      # content type, request parameters and request body in json format
      req = Net::HTTP::Post.new(uri.path)
      req.set_content_type('application/json')
      req['Yona-Password'] = $yona_pwd
      payLoad = {
          "code": "1234"
      }.to_json
      req.body = payLoad
      res = https.request(req)
      if(res.code=='200')
        puts "Mobile number validated successfully: Code #{res.code} Message: #{res.message}"
        json = JSON.parse(res.body)
        $newDeviceUri=json["_links"]["yona:newDeviceRequest"]["href"].to_s
      else
        puts"Could not validate mobile number"
      end

    rescue => e
      # puts "Response=#{e.response}"
      puts "Message=#{e.message}"
      puts "Cause=#{e.cause}"
    end

  end

  def COMMONAPIS.newDevicePutRequest(newDeviceUri)
    begin
      #Parse the URl and create new instance of http request
      uri = URI.parse(newDeviceUri)
      https = Net::HTTP.new(uri.host,uri.port)

      # Creates a request instance and form the request by specifying
      # content type, request parameters and request body in json format
      req = Net::HTTP::Put.new(uri.path)
      req.set_content_type('application/json')
      req['Yona-Password'] = $yona_pwd
      payLoad = {
          "newDeviceRequestPassword": $newDevicePwd
      }.to_json
      req.body = payLoad
      res = https.request(req)
      if(res.code=='200')
        puts "New device request succesful: Code #{res.code} Message: #{res.message}"
      else
        puts"Add device request failed"
      end

    rescue => e
      puts "Message=#{e.message}"
      puts "Cause=#{e.cause}"
    end
  end

end