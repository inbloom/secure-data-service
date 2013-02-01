=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


require 'rubygems'
require 'net/ldap'
require 'net/ldap/dn'
require 'date'
require 'duckpunch-netldap'

class InvalidPasswordException < StandardError
end

class LDAPStorage
  ####################################################################
  # Description of LDAP related data model
  # The following should fully describe how we interact with LDAP
  # no specifics shouldbe in the implementation code below
  ####################################################################
  # set the objectClasses for user objects
  OBJECT_CLASS = ["inetOrgPerson", "posixAccount", "top"]

  # group object classes
  GROUP_OBJECT_CLASSES = ["posixGroup", "top"]
  GROUP_MEMBER_ATTRIBUTE = :memberUid

  CONST_GROUPID_NUM = "113"
  CONST_USERID_NUM  = "500"

  DEFAULT_GROUP_ATTRIBUTES = {
      :gidNumber => CONST_GROUPID_NUM
  }

  LDAP_ATTR_MAPPING = {
      :givenname            => :first,
      :sn                   => :last,
      :uid                  => :email,
      :userPassword         => :password,
      :o                    => :vendor,
      :displayName          => :emailtoken,
      :destinationIndicator => :status,
      :homeDirectory        => :homedir,
      :uidNumber            => :uidnumber,
      :gidNumber            => :gidnumber,
      :mail                 => :emailAddress,
      :gecos                => :resetKey
  }
  ENTITY_ATTR_MAPPING = LDAP_ATTR_MAPPING.invert

  REQUIRED_FIELDS = [
      :first,
      :last,
      :email,
      :password,
      :emailtoken,
      :homedir,
      :status,
      :emailAddress
  ]

  LDAP_WO_CONSTANTS = {
      :loginShell           =>  "/sbin/nologin"
  }

  # some fields are stored in the description field as
  # key/value pairs
  # WARNING: Modifying these values will not work unless the unpacking logic in search_map_user_fields is also updated
  PACKED_ENTITY_FIELD_MAPPING = {
      :tenant => "tenant",
      :edorg  => "edOrg"
  }

  # Additional fiels (see above) are packed into this field as key/value pairs
  LDAP_DESCRIPTION_FIELD = :description

  # READ-ONLY FIELDS
  RO_LDAP_ATTR_MAPPING = {
      :createTimestamp => :created,
      :modifyTimestamp => :updated,
      :cn              => :cn
  }

  # these values are injected when the user is created
  ENTITY_CONSTANTS = {
      :emailtoken => "-",
      :uidnumber  => CONST_USERID_NUM,
      :gidnumber  => CONST_GROUPID_NUM,
      :vendor     => "none",
      :homedir    => "/dev/null",
      :resetKey   => ""
  }

  # List of fields to fetch from LDAP for user
  COMBINED_LDAP_ATTR_MAPPING = LDAP_ATTR_MAPPING.merge(RO_LDAP_ATTR_MAPPING)

  ALLOW_UPDATING = [
      :first,
      :last,
      :password,
      :vendor,
      :emailtoken,
      :homedir,
      :tenant,
      :edorg,
      :emailAddress,
      :resetKey
  ]

  LDAP_DATETIME_FIELDS = Set.new [
                                     :createTimestamp,
                                     :modifyTimestamp
                                 ]

  ################################################################
  # Implementation
  ################################################################

  # Initialize the module
  def initialize(host, port, base, username, password, use_ssl)
    @people_base = "ou=people,#{base}"
    @group_base  = "ou=groups,#{base}"
    @ldap_conf = {
        :host => host,
        :port => port,
        :base => @people_base,
        :auth => {
            :method => :simple,
            :username => username,
            :password => password
        }
    }

    # enable SSL if n
    if use_ssl
      @ldap_conf[:encryption] = {    :method => :simple_tls    }
    end

    # test whether it can bind
    #test_ldap = Net::LDAP.new @ldap_conf
    #raise ldap_ex(test_ldap, "Could not bind to ldap server.") if !test_ldap.bind
  end

  def get_ldap_config
    return @ldap_conf.clone
  end

  # user_info = {
  #     :first => "John",
  #     :last => "Doe",
  #     :email => "jdoe@example.com",
  #     :password => "secret",
  #     :vendor => "Acme Inc."
  #     :emailtoken ... hash string
  #     :tenant
  #     :edorg
  #     :status  ... "submitted"
  # }
  def create_user(user_info)
    cn = user_info[:email]
    dn = get_DN(cn)
    attributes = {
        :cn => cn,
        :objectclass => OBJECT_CLASS,
    }

    # inject the constant values into the user info
    e_user_info = ENTITY_CONSTANTS.merge(user_info)

    # make sure the required features are there
    REQUIRED_FIELDS.each do |k|
      if !e_user_info[k] || (e_user_info[k].strip == "")
        raise "The following attributes #{REQUIRED_FIELDS} need to be set. These were provided: #{e_user_info}"
      end
    end

    LDAP_ATTR_MAPPING.each do |ldap_k, rec_k|
      value = e_user_info[rec_k]
      attributes[ldap_k] = value
    end

    # add all the required LDAP constants. These are write-only
    attributes.merge!(LDAP_WO_CONSTANTS)

    # pack additional fields into the description field
    packed_fields = pack_fields(e_user_info)
    if packed_fields.strip != ""
      attributes[LDAP_DESCRIPTION_FIELD] = packed_fields
    end

    # make the ldap call
    Net::LDAP.open(@ldap_conf) do |ldap|
      if !(ldap.add(:dn => dn, :attributes => attributes))
        raise ldap_ex(ldap, "Unable to create user in LDAP: #{attributes}.")
      end
    end
  end

  def authenticate(uid, password)
    # retrieve the raw user record
    filter = Net::LDAP::Filter.eq(ENTITY_ATTR_MAPPING[:email].to_s, uid)
    user = search_map_user_fields(filter, 1, true)[0]
    return false if !user

    local_conf = @ldap_conf.clone
    local_conf[:auth] = {
        :method => :simple,
        :username => user[:dn],
        :password => password
    }
    ldap = Net::LDAP.new local_conf
    return ldap.bind
  end

  # returns extended user_info
  def read_user(email_address)
    filter = Net::LDAP::Filter.eq(ENTITY_ATTR_MAPPING[:email].to_s, email_address)
    return search_map_user_fields(filter, 1)[0]
  end

  # returns extended user_info for the given emailtoken (see create_user) or nil
  def read_user_emailtoken(emailtoken)
    filter = Net::LDAP::Filter.eq(ENTITY_ATTR_MAPPING[:emailtoken].to_s, emailtoken)
    return search_map_user_fields(filter, 1)[0]
  end

  # returns extended user_info for the given resetKey (see create_user) or nil
  def read_user_resetkey(resetKey)
    filter = Net::LDAP::Filter.begins(ENTITY_ATTR_MAPPING[:resetKey].to_s, resetKey + "@")
    return search_map_user_fields(filter, 1)[0]
  end
  # returns array of extended user_info for all users or all users with given status
  # use constants in approval.rb
  def read_users(status=nil)
    # if a filter is provided for the status then set it otherwise just search for people
    # Note: The filter will not capture users that do not have their status set. 
    if status
      filter = Net::LDAP::Filter.eq(ENTITY_ATTR_MAPPING[:status].to_s, status ? status : "*")
    else
      filter = Net::LDAP::Filter.eq(:objectClass, "inetOrgPerson")
    end
    return search_map_user_fields(filter)
  end

  # returns array of extended user_info for all users or all users with given status
  # use constants in approval.rb
  def search_users(wildcard_email_address)
    filter = Net::LDAP::Filter.eq(ENTITY_ATTR_MAPPING[:email].to_s, wildcard_email_address)
    return search_map_user_fields(filter)
  end

  def search_users_raw(wildcard_email_address)
    filter = Net::LDAP::Filter.eq(ENTITY_ATTR_MAPPING[:email].to_s, wildcard_email_address)
    return search_map_user_fields(filter, nil, true)
  end

  # updates the user status from an extended user_info
  def update_status(user)
    if user_exists?(user[:email])
      found_user = read_user(user[:email])
      raise "Could not update user #{user[:email]}. User does not exist." if !found_user

      dn = get_DN(found_user[:cn])
      Net::LDAP.open(@ldap_conf) do |ldap|

        if !ldap.replace_attribute(dn, ENTITY_ATTR_MAPPING[:status], user[:status])
          raise ldap_ex(ldap, "Could not update user status for user #{user[:email]}")
        end
      end
    end
  end

  # enable login and update the status
  # This means the user is added to the LDAP group that corresponds to the given role
  def add_user_group(email_address, group_id)
    user_dn  = email_address
    group_dn = get_group_DN(group_id)

    filter = Net::LDAP::Filter.eq( "cn", group_id)

    Net::LDAP.open(@ldap_conf) do |ldap|
      group_found = ldap.search(:base => @group_base, :filter => filter).to_a()[0]
      if !group_found
        group_attrib = DEFAULT_GROUP_ATTRIBUTES.clone
        group_attrib.update({
                                :cn => group_id,
                                :objectclass => GROUP_OBJECT_CLASSES,
                                GROUP_MEMBER_ATTRIBUTE => user_dn
                            })

        if !ldap.add(:dn => group_dn, :attributes => group_attrib)
          raise ldap_ex(ldap, "Could not add #{email_address} to new group #{group_id} using attributes: #{group_attrib}")
        end
      else
        if !group_found[GROUP_MEMBER_ATTRIBUTE].index(user_dn)
          if !ldap.modify(:dn => group_dn, :operations => [[:add, GROUP_MEMBER_ATTRIBUTE, user_dn]])
            raise ldap_ex(ldap, "Could not add #{email_address} to existing  group #{group_id} using attributed '#{GROUP_MEMBER_ATTRIBUTE}' with LDAP base '#{@group_base}'")
          end
        end
      end
    end
  end

  # disable login and update the status
  def remove_user_group(email_address, group_id)
    # note the user is stored in the group via uid only
    user_dn = email_address
    group_dn = get_group_DN(group_id)

    filter = Net::LDAP::Filter.eq( "cn", group_id)
    Net::LDAP.open(@ldap_conf) do |ldap|
      group_found = ldap.search(:base => @group_base, :filter => filter).to_a()[0]

      if group_found
        removed = group_found[GROUP_MEMBER_ATTRIBUTE].delete(user_dn)
        if removed
          if group_found[GROUP_MEMBER_ATTRIBUTE].empty?
            if !ldap.delete(:dn => group_dn)
              raise ldap_ex(ldap, "Could not remove user #{user_dn} for group #{group_id}")
            end
          else
            if !ldap.replace_attribute(group_dn, GROUP_MEMBER_ATTRIBUTE, group_found[GROUP_MEMBER_ATTRIBUTE])
              raise ldap_ex(ldap, "Could not remove user #{user_dn} for group #{group_id}")
            end
          end
        end
      end
    end
  end

  def get_user_groups(email_address)
    user_dn = email_address
    filter = Net::LDAP::Filter.eq( GROUP_MEMBER_ATTRIBUTE, user_dn)

    Net::LDAP.open(@ldap_conf) do |ldap|
      ldap.search(:base => @group_base, :filter => filter).to_a().map do |group|
        group[:cn][0]
      end
    end
  end

  # returns true if the user exists
  def user_exists?(email_address)
    !!read_user(email_address)
  end

  # updates the user_info except for the user status
  # user_info is the same input as for create_user
  def update_user_info(user_info)
    return if !user_info || user_info.empty?

    # get the current user entry
    curr_user_info = read_user(user_info[:email])
    if curr_user_info
      dn = get_DN(curr_user_info[:cn])

      Net::LDAP.open(@ldap_conf) do |ldap|
        #update the ldap attributes
        desc_attributes = {}
        ALLOW_UPDATING.each do |attribute|
          if user_info && !!user_info[attribute] && (curr_user_info[attribute] != user_info[attribute])
            if PACKED_ENTITY_FIELD_MAPPING.include?(attribute)
              desc_attributes[attribute] = user_info[attribute]
            else
              if !(ldap.replace_attribute(dn, ENTITY_ATTR_MAPPING[attribute], user_info[attribute]))
                if !curr_user_info[attribute]
                  if !(ldap.add_attribute(dn, ENTITY_ATTR_MAPPING[attribute], user_info[attribute]))
                    puts curr_user_info
                    puts user_info

                    ops = [[:add, ENTITY_ATTR_MAPPING[attribute], user_info[attribute]]]
                    ldap.modify :dn => dn, :operations => ops

                    raise ldap_ex(ldap, "Unable to add new attribute '#{ENTITY_ATTR_MAPPING[attribute]}' with value '#{user_info[attribute]}'.")
                  end
                else
                  raise ldap_ex(ldap, "Unable to update attribute '#{ENTITY_ATTR_MAPPING[attribute]}' with value '#{user_info[attribute]}'.")
                end
              end
            end
          end
        end

        # updat the attributes that are encoded in description
        if !desc_attributes.empty?
          temp = (curr_user_info.clone).merge(desc_attributes)
          packed = pack_fields(temp)
          ldap.replace_attribute(dn, LDAP_DESCRIPTION_FIELD, packed)
        end
      end
    end
  end

  # deletes the user entirely
  def delete_user(email_address)
    found_user = read_user(email_address)
    if found_user
      Net::LDAP.open(@ldap_conf) do |ldap|
        ldap.delete(:dn => get_DN(found_user[:cn]))
      end
    end
  end

  # delete attribute for a user
  def delete_user_attribute(email_address, attribute)
    found_user = read_user(email_address)
    if found_user
      dn = get_DN(found_user[:cn])
      Net::LDAP.open(@ldap_conf) do |ldap|
        ldap.delete_attribute(dn, ENTITY_ATTR_MAPPING[attribute])
      end
    end
  end

  # retrieve one group from ldap
  def get_group(group_id)
    group_found = nil
    filter = Net::LDAP::Filter.eq( "cn", group_id)
    Net::LDAP.open(@ldap_conf) do |ldap|
      group_found = ldap.search(:base => @group_base, :filter => filter).to_a()[0]
    end
    group_found
  end

  #############################################################################
  # PRIVATE methods
  #############################################################################
  private

  # returns the LDAP DN
  def get_DN(cn)
    return "cn=#{escape_seg(cn)},#{@people_base}"
  end

  def get_group_DN(group_id)
    return "cn=#{group_id},#{@group_base}"
  end

  def escape_seg(value)
    return Net::LDAP::DN.escape(value)
  end

  # extract the user from the ldap record
  def search_map_user_fields(filter, max_recs=nil, raw=false)
    # search for all fields plus the description field
    Net::LDAP.open(@ldap_conf) do |ldap|
      attributes = COMBINED_LDAP_ATTR_MAPPING.keys + [LDAP_DESCRIPTION_FIELD] + (raw ? LDAP_WO_CONSTANTS.keys : [])
      arr = ldap.search(:filter => filter, :attributes => attributes).to_a()
      if !max_recs
        max_recs = arr.length
      end

      # if the raw ldap records were requested then return them
      return arr if raw

      # process them into records
      return arr[0..(max_recs-1)].map do |entry|
        user_rec = {}
        COMBINED_LDAP_ATTR_MAPPING.each do |ldap_k, rec_k|
          attr_val = entry[ldap_k].is_a?(Array) ?  entry[ldap_k][0] : entry[ldap_k]
          user_rec[rec_k] = LDAP_DATETIME_FIELDS.include?(ldap_k) ? DateTime.iso8601(attr_val) : attr_val
        end

        # unpack the fields that are stored in the description field
        desc = (entry[LDAP_DESCRIPTION_FIELD] ? entry[LDAP_DESCRIPTION_FIELD] : [])[0]
        # handle packed fields deliminated by carriage return, newline, comma, or space
        desc = desc.gsub(/tenant=([^\s,]+)[,\s]+/, "tenant=\\1\n") unless desc == nil
        unpacked_fields = if desc && (desc.strip != "")
                            mapping = desc.strip().split("\n").map {|x| x.strip}
                            mapping = mapping.map do |x|
                              x.split("=").map {|y| y.strip }
                            end
                            mapping = Hash[mapping]
                          else
                            {}
                          end
        PACKED_ENTITY_FIELD_MAPPING.each do |rec_k, packed_k|
          if unpacked_fields.include?(packed_k)
            user_rec[rec_k] = unpacked_fields[packed_k]
          end
        end

        user_rec
      end
    end
  end

  def pack_fields(user_info)
    packed_fields = Hash[ PACKED_ENTITY_FIELD_MAPPING.map { |k,v| [v, user_info[k]] } ]
    ((packed_fields.select {|k,v| !!v}).map { |k,v|  "#{k}=#{v}" }).join("\n")
  end

  def ldap_ex(ldap, msg, check_password_error=false)
    op = ldap.get_operation_result
    if op.code == 19   # indicates a password that does not comply with policy
      raise InvalidPasswordException
    else
      "#{msg} (#{op.code}) #{op.message}"
    end
  end

end
