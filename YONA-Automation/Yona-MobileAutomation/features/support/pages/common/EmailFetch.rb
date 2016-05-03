require 'mail'

# mail = Mail.find keys: ['NOT', 'SEEN']
# mail = Mail.find(:what => :last, :count => 10, :order => :desc, :keys=>['NOT', 'SEEN'])\
# mail=Mail.find(what: :last, count: 10, order: :desc)



def fetch_lastEmail
  Mail.defaults do
    retriever_method :pop3, :address    => "pop.gmail.com",
                     :port       => 995,
                     :user_name  => 'myorderuser1@gmail.com',
                     :password   => 'myorder1',
                     :enable_ssl => true,
                     :timeout    => 100
  end

  mail = Mail.last
  puts "EMAIL=#{mail.to_s}"
  mail = mail.to_s

end


def fetch_CodeFromEmail
  puts("Fetching code")
  strEmail = fetch_lastEmail
  puts strEmail;
  strVerCode=""
  if strEmail.nil?
    puts "Sorry, Email could not be loaded"
  else
    strCodeString =  strEmail.to_s[/(verifyLogin\/[A-Z]{5})/m]
    if !(strCodeString.nil?)
      strArr = strCodeString.split(/\//)
      if !(strArr.nil?)
        if(strArr.length==2)
          strVerCode=strArr[1].strip
        else
          puts "OOPS, sorry we could not locate the code"
          strVerCode=""
        end
      end
    end
  end
  puts "Verification Code is #{strVerCode}"
  # puts "String After RegEx=#{strArr}"
  return strVerCode
end

def fetch_InvoiceNoFromEmail
  strEmail = fetch_lastEmail
  # puts strEmail
  strVerCode=""
  if strEmail.nil?
    puts "Sorry, Email could not be loaded"
  else
    strCodeString =  strEmail.to_s[/(MyOrder bestelling [0-9]{5})/m]
    if !(strCodeString.nil?)
      strArr = strCodeString.split
      if !(strArr.nil?)
        if(strArr.length==3)
          strVerCode=strArr[2].strip
        else
          puts "OOPS, sorry we could not locate the code"
          strVerCode=""
        end
      end
    end
  end
  puts "Order no is #{strVerCode}"
  return strVerCode
end

# strCode=fetch_CodeFromEmail
# puts "Code is #{strCode}"