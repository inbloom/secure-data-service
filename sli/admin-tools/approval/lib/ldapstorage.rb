require 'net/ldap'

class LDAPStorage 
	# set the objectClasses 
	OBJECT_CLASS = ["inetOrgPerson", "top"]
	TARGET_TREE  = "ou=people,ou=DevTest,dc=slidev,dc=org"

	def initialize(host, port, base, username, password)
		@ldap_conf = { :host => host,
			:port => port,
     		:base => base,
     		:auth => {
           		:method => :simple,
           		:username => username,
           		:password => password
     		}
     	}
     	@ldap = Net::LDAP.new ldap_conf
     	if ldap.bind
     		puts "Authentication successfull."
		else
			puts "Authentication failed."
		end
	end

	# user_info = {
	#     :first => "John",
	#     :last => "Doe", 
	#     :email => "jdoe@example.com",
	#     :password => "secret", 
	#     :vendor => "Acme Inc."
	#     :emailtoken ... hash string 
	#     :updated ... datetime
	#     :status  ... "submitted"
	# }
	def create_user(user_info)
		cn = "#{user_info[:first]} #{user_info[:last]}"
		dn = "cn=#{cn},#{TARGET_TREE}"
		attr = {
			:cn => cn,
			:objectclass => OBJECT_CLASS,
			:gn => user_info[:first]
			:sn => user_info[:last]
			:uid  => user_info[:email]
			:userpassword => user_info[:password]
			:organizationname => user_info[:vendor]
			:seealso => user_info[:emailtoken]
			:destinationindicator => user_info[:status]
		}
		result = ldap.add(:dn => dn, :attributes => attr)
	end

	# returns extended user_info
	def read_user(email_address)
	end

	# updates the user status from an extended user_info 
	def update_status(user)
	end

	# returns true if the user exists 
	def user_exists?(email_address)
	end

	# returns extended user_info for the given emailtoken (see create_user) or nil 
	def read_user_emailtoken(emailtoken)
	end

	# returns array of extended user_info for all users or all users with given status 
	# use constants in approval.rb 
	def read_users(status=nil)
	end

	# updates the user_info except for the user status 
	# user_info is the same input as for create_user 
	def update_user_info(user_info)
	end 

	# deletes the user entirely 
	def delete_user(email_address)
	end 
end


# usage 
require 'approval'
storage = LDAPStorage.new("ldap.slidev.org", 389, "cn=DevLDAP User, ou=People,dc=slidev,dc=org", "Y;Gtf@w{")

