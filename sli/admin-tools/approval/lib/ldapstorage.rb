require 'rubygems'
require 'net/ldap'
require 'date'

class LDAPStorage 
	# set the objectClasses for user objects
	OBJECT_CLASS = ["inetOrgPerson", "posixAccount", "top"]

	# group object classes 
	GROUP_OBJECT_CLASSES = ["groupOfNames", "top"]
	GROUP_MEMBER_ATTRIBUTE = :member

	LDAP_ATTR_MAPPING = {
		:givenname            => :first,
		:sn                   => :last,
		:uid                  => :email,
		:userpassword         => :password,
		:o                    => :vendor,
		:displayname          => :emailtoken,
		:destinationindicator => :status,
		:homeDirectory        => :homedir,
		:uidNumber            => :uid, 
		:gidNumber            => :gid
	}
	ENTITY_ATTR_MAPPING = LDAP_ATTR_MAPPING.invert

	# READ-ONLY FIELDS 
	RO_LDAP_ATTR_MAPPING = {
		:createTimestamp => :created, 
    	:modifyTimestamp => :updated
	}

	# these values are injected when the user is created 
	ENTITY_CONSTANTS = {
		:uid => "500", 
		:gid => "500"
	}

	# description field to contain mapping between LDAP and applicaton fields
	LDAP_DESCRIPTION_FIELD = :description

	# List of fields to fetch from LDAP for user 
	COMBINED_LDAP_ATTR_MAPPING = LDAP_ATTR_MAPPING.merge(RO_LDAP_ATTR_MAPPING)

	ALLOW_UPDATING = [
		:first,
		:last,
		:password, 
		:vendor,
		:emailtoken,
		:homedir
	]

	LDAP_DATETIME_FIELDS = Set.new [
		:createTimestamp, 
		:modifyTimestamp
	]

	# SEC_AUTH_REALM  = "sandboxAuthRealm"
	# SEC_ADMIN_REALM = "sandboxAdminRealm"
	# SEC_EDORG       = "sandboxEdOrg"

	def initialize(host, port, base, username, password)
     	@people_base = "ou=people,#{base}"
     	@group_base  = "ou=groups,#{base}"
		@ldap_conf = { :host => host,
			:port => port,
     		:base => @people_base,
     		:auth => {
           		:method => :simple,
           		:username => username,
           		:password => password
     		}
     	}
     	@ldap = Net::LDAP.new @ldap_conf
     	raise ldap_ex("Could not bind to ldap server.") if !@ldap.bind 
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
		attributes = {
			:cn => cn,
			:objectclass => OBJECT_CLASS,
		}

		# inject the constant values into the user info 
		e_user_info = user_info.merge(ENTITY_CONSTANTS)

		if ENTITY_ATTR_MAPPING.keys().sort != e_user_info.keys().sort
		 	raise "The following attributes #{ENTITY_ATTR_MAPPING.keys} need to be set" 
		end
		
		LDAP_ATTR_MAPPING.each { |ldap_k, rec_k| attributes[ldap_k] = e_user_info[rec_k] }
		descr = (COMBINED_LDAP_ATTR_MAPPING.map { |k,v|  "#{k}:#{v}" }).join("\n")
		attributes[LDAP_DESCRIPTION_FIELD] = descr
		if !(@ldap.add(:dn => dn, :attributes => attributes))
			raise ldap_ex("Unable to create user in LDAP: #{attributes}.")
		end
	end

	# returns extended user_info
	def read_user(email_address)
		filter = Net::LDAP::Filter.eq( "cn", email_address)
		return search_map_user_fields(filter, 1)[0]
	end

	# returns extended user_info for the given emailtoken (see create_user) or nil 
	def read_user_emailtoken(emailtoken)
		filter = Net::LDAP::Filter.eq(ENTITY_ATTR_MAPPING[:emailtoken].to_s, emailtoken)
		return search_map_user_fields(filter, 1)[0]		
	end

	# returns array of extended user_info for all users or all users with given status 
	# use constants in approval.rb 
	def read_users(status=nil)
		filter = Net::LDAP::Filter.eq(ENTITY_ATTR_MAPPING[:status].to_s, status ? status : "*")
		return search_map_user_fields(filter)
	end	

	# updates the user status from an extended user_info 
	def update_status(user)
		if user_exists?(user[:email])
			dn = get_DN(user[:email])
			if !@ldap.replace_attribute(dn, ENTITY_ATTR_MAPPING[:status], user[:status])
				raise ldap_ex("Could not update user status for user #{user[:email]}")
			end
		end
	end

	# enable login and update the status
	# This means the user is added to the LDAP group that corresponds to the given role
	def add_user_group(email_address, group_id)
		user_dn  = get_DN(email_address)
		group_dn = get_group_DN(group_id)

		filter = Net::LDAP::Filter.eq( "cn", group_id)
		group_found = @ldap.search(:base => @group_base, :filter => filter).to_a()[0]
		if !group_found
  			member_attrib = {
    			:cn => group_id,
    			:objectclass => GROUP_OBJECT_CLASSES,
    			:member => user_dn
  			}
  			if !@ldap.add(:dn => group_dn, :attributes => member_attrib)
  				raise ldap_ex("Could not add #{email_address} to group #{group_id}.")
	  		end
	  	else
	  		if !group_found[:member].index(user_dn)
		  		if !@ldap.modify(:dn => group_dn, :operations => [[:add, GROUP_MEMBER_ATTRIBUTE, user_dn]])
	  				raise ldap_ex("Could not add #{email_address} to group #{group_id}.")
		  		end
		  	end
	  	end
	end

	# disable login and update the status 
	def remove_user_group(email_address, group_id)
		user_dn = get_DN(email_address)
		group_dn = get_group_DN(group_id)

		filter = Net::LDAP::Filter.eq( "cn", group_id)
		group_found = @ldap.search(:base => @group_base, :filter => filter).to_a()[0]

		if group_found
			removed = group_found[:member].delete(user_dn)
			if removed
				if group_found[:member].empty? 
					@ldap.delete(:dn => group_dn)
				else 
					@ldap.replace_attribute(group_dn, GROUP_MEMBER_ATTRIBUTE, group_found[:member])
				end 
			end
		end
	end

	def get_user_groups(email_address)
		user_dn = get_DN(email_address)
		filter = Net::LDAP::Filter.eq( :member, user_dn)
		@ldap.search(:base => @group_base, :filter => filter).to_a().map do |group|
			group[:cn][0]
		end
	end

	# returns true if the user exists 
	def user_exists?(email_address)
		!!read_user(email_address)
	end

	# updates the user_info except for the user status 
	# user_info is the same input as for create_user 
	def update_user_info(user_info)
		curr_user_info = read_user(user_info[:email])
		if curr_user_info
			dn = get_DN(user_info[:email])
			ALLOW_UPDATING.each do |attribute|
				if user_info && (curr_user_info[attribute] != user_info[attribute])
					@ldap.replace_attribute(dn, ENTITY_ATTR_MAPPING[:status], user_info[:status])
				end
			end
									
		end
	end 

	# deletes the user entirely 
	def delete_user(email_address)
		@ldap.delete(:dn => get_DN(email_address))
	end 

	#############################################################################
	# PRIVATE methods
	#############################################################################
	private

	# returns the LDAP DN
	def get_DN(email_address)
		return "cn=#{email_address},#{@people_base}"
	end

	def get_group_DN(group_id)
		return "cn=#{group_id},#{@group_base}"
	end

	# extract the user from the ldap record
	def search_map_user_fields(filter, max_recs=nil)
		attributes = COMBINED_LDAP_ATTR_MAPPING.keys
		arr = @ldap.search(:filter => filter, :attributes => attributes).to_a()
		if !max_recs
			max_recs = arr.length
		end

		return arr[0..(max_recs-1)].map do |entry|
			user_rec = {}
			COMBINED_LDAP_ATTR_MAPPING.each do |ldap_k, rec_k| 
				attr_val = entry[ldap_k].is_a?(Array) ?  entry[ldap_k][0] : entry[ldap_k]
				user_rec[rec_k] = LDAP_DATETIME_FIELDS.include?(ldap_k) ? DateTime.iso8601(attr_val) : attr_val
			end
			user_rec
		end
	end

	def ldap_ex(msg)
		op = @ldap.get_operation_result
		"#{msg} (#{op.code}) #{op.message}"
	end
end

# usage 
#require 'approval'
#storage = LDAPStorage.new("ldap.slidev.org", 389, "cn=DevLDAP User, ou=People,dc=slidev,dc=org", "Y;Gtf@w{")

