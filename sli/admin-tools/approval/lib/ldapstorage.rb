require 'rubygems'
require 'net/ldap'

class LDAPStorage 
	# set the objectClasses 
	OBJECT_CLASS = ["inetOrgPerson", "top"]
	#TARGET_TREE  = "ou=people,ou=DevTest,dc=slidev,dc=org"

	LDAP_ATTR_MAPPING = {
		:givenname            => :first,
		:sn                   => :last,
		:uid                  => :email,
		:userpassword         => :password,
		:o                    => :vendor,
		:displayname          => :emailtoken,
		:destinationindicator => :status
	}

	ENTITY_ATTR_MAPPING = LDAP_ATTR_MAPPING.invert

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
     	@base = base
     	@ldap = Net::LDAP.new @ldap_conf
     	raise "Could not bind to ldap server." if !@ldap.bind 
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
		cn = user_info[:email]
		dn = get_DN(user_info[:email])
		puts "cn: #{cn}\ndn: #{dn}\npassword: #{user_info[:password]}"
		puts user_info[:vendor]
		attr = {
			:cn => cn,
			:objectclass => OBJECT_CLASS,
		}

		LDAP_ATTR_MAPPING.each { |ldap_k, rec_k| attr[ldap_k] = user_info[rec_k] }
		return @ldap.add(:dn => dn, :attributes => attr)
	end

	# returns extended user_info
	def read_user(email_address)
		filter = Net::LDAP::Filter.eq( "cn", email_address)
		return map_fields(@ldap.search(:filter => filter), 1)[0]
	end

	# updates the user status from an extended user_info 
	def update_status(user)
		if user_exists?(user[:email])
			dn = get_DN(user[:email])
			@ldap.replace_attribute(dn, ENTITY_ATTR_MAPPING[:status], user[:status])
		end
	end

	# enable login and update the status
	def enable_update_status(user)
	end

	# disable login and update the status 
	def disable_update_status(user)
	end

	# returns true if the user exists 
	def user_exists?(email_address)
		!!read_user(email_address)
	end

	# returns extended user_info for the given emailtoken (see create_user) or nil 
	def read_user_emailtoken(emailtoken)
		filter = Net::LDAP::Filter.eq(ENTITY_ATTR_MAPPING[:emailtoken].to_s, emailtoken)
		return map_fields(@ldap.search(:filter => filter), 1)[0]		
	end

	# returns array of extended user_info for all users or all users with given status 
	# use constants in approval.rb 
	def read_users(status=nil)
		filter = Net::LDAP::Filter.eq(ENTITY_ATTR_MAPPING[:status].to_s, status ? status : "*")
		return map_fields(@ldap.search(:filter => filter))		
	end

	# updates the user_info except for the user status 
	# user_info is the same input as for create_user 
	def update_user_info(user_info)
	end 

	# deletes the user entirely 
	def delete_user(email_address)
		@ldap.delete(:dn => get_DN(email_address))
	end 

	# returns the LDAP DN
	def get_DN(email_address)
		return "cn=#{email_address},#{@base}"
	end

	# extract the user from the ldap record
	def map_fields(search_result, max_recs=nil)
		arr = search_result.to_a()
		if !max_recs
			max_recs = arr.length
		end

		return arr[0..(max_recs-1)].map do |entry|
			user_rec = {}
			LDAP_ATTR_MAPPING.each { |ldap_k, rec_k| user_rec[rec_k] = entry[ldap_k]}
			user_rec
		end
	end
end


# usage 
#require 'approval'
#storage = LDAPStorage.new("ldap.slidev.org", 389, "cn=DevLDAP User, ou=People,dc=slidev,dc=org", "Y;Gtf@w{")

