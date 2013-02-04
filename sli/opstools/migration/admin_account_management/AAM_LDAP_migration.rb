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


# README
# This script is to support the release of the Admin Account Management Tool (AAM) (formerly SAMT)
# It creates the new LDAP group, and adds all existing Application_developers to the group.
# This should not be run after the release of the AAM, because at that point, users can be application_developers without the sandbox administrator role.

require 'net/ldap'
require 'net/ldap/dn'
require 'logger'

encrypted = false
force = false
args = ARGV.select do |arg|
  encrypted = true if arg =~ /-encrypt/
  force = true if arg =~ /-force/
  !(arg =~ /-encrypt/ or arg =~ /-force/)
end

if (args.length != 5) 
  puts "Usage: <ldap_host> <ldap_port> <ldap_group_base> <ldap_admin_user> <ldap_pass> [-encrypt] [-force]"
  puts "<ldap_host> LDAP host name"
  puts "<ldap_port> LDAP port.  If 636 is specified, then TLS encryption will be used"
  puts "<ldap_group_base> LDAP base under which the application_developer and sandbox_administrator groups live"
  puts "<ldap_admin_user> Admin user that has the ability to write/update the group membership information"
  puts "<ldap_pass> Admin user's password"
  puts "-encrypt Optional param that will enable TLS regardless of port"
  puts "-force Optional param that forces script to update group even if it already has members in it"
  exit(1)
end

host = args[0]
port = args[1].to_i
group_base = args[2]
username = args[3]
password = args[4]
group_cn = "application_developer"
new_group_cn = "Sandbox Administrator"

encrypted = true if port == 636
new_group_gidNumber = 516  # only used if we need to create the new group.  Ignored if it already exists.

# Duckpunch net-ldap to get around the 126 result bug
class Net::LDAP::Connection #:nodoc:

  #--
  # Alternate implementation, this yields each search entry to the caller as
  # it are received.
  #
  # TODO: certain search parameters are hardcoded.
  # TODO: if we mis-parse the server results or the results are wrong, we
  # can block forever. That's because we keep reading results until we get a
  # type-5 packet, which might never come. We need to support the time-limit
  # in the protocol.
  #++
  def search(args = {})
    search_filter = (args && args[:filter]) ||
      Net::LDAP::Filter.eq("objectclass", "*")
    search_filter = Net::LDAP::Filter.construct(search_filter) if search_filter.is_a?(String)
    search_base = (args && args[:base]) || "dc=example, dc=com"
    search_attributes = ((args && args[:attributes]) || []).map { |attr| attr.to_s.to_ber}
    return_referrals = args && args[:return_referrals] == true
    sizelimit = (args && args[:size].to_i) || 0
    raise Net::LDAP::LdapError, "invalid search-size" unless sizelimit >= 0
    paged_searches_supported = (args && args[:paged_searches_supported])

    attributes_only = (args and args[:attributes_only] == true)
    scope = args[:scope] || Net::LDAP::SearchScope_WholeSubtree
    raise Net::LDAP::LdapError, "invalid search scope" unless Net::LDAP::SearchScopes.include?(scope)

    # An interesting value for the size limit would be close to A/D's
    # built-in page limit of 1000 records, but openLDAP newer than version
    # 2.2.0 chokes on anything bigger than 126. You get a silent error that
    # is easily visible by running slapd in debug mode. Go figure.
    #
    # Changed this around 06Sep06 to support a caller-specified search-size
    # limit. Because we ALWAYS do paged searches, we have to work around the
    # problem that it's not legal to specify a "normal" sizelimit (in the
    # body of the search request) that is larger than the page size we're
    # requesting. Unfortunately, I have the feeling that this will break
    # with LDAP servers that don't support paged searches!!!
    #
    # (Because we pass zero as the sizelimit on search rounds when the
    # remaining limit is larger than our max page size of 126. In these
    # cases, I think the caller's search limit will be ignored!)
    #
    # CONFIRMED: This code doesn't work on LDAPs that don't support paged
    # searches when the size limit is larger than 126. We're going to have
    # to do a root-DSE record search and not do a paged search if the LDAP
    # doesn't support it. Yuck.
    rfc2696_cookie = [126, ""]
    result_code = 0
    n_results = 0

    loop {
      # should collect this into a private helper to clarify the structure
      query_limit = 0
      if sizelimit > 0
        if paged_searches_supported
          query_limit = (((sizelimit - n_results) < 126) ? (sizelimit -
                                                            n_results) : 0)
        else
          query_limit = sizelimit
        end
      end

      request = [
        search_base.to_ber,
        scope.to_ber_enumerated,
        0.to_ber_enumerated,
        query_limit.to_ber, # size limit
        0.to_ber,
        attributes_only.to_ber,
        search_filter.to_ber,
        search_attributes.to_ber_sequence
      ].to_ber_appsequence(3)

      controls = []
      controls <<
        [
          Net::LDAP::LdapControls::PagedResults.to_ber,
          # Criticality MUST be false to interoperate with normal LDAPs.
          false.to_ber,
          rfc2696_cookie.map{ |v| v.to_ber}.to_ber_sequence.to_s.force_encoding("UTF-8").to_ber
        ].to_ber_sequence if paged_searches_supported
      controls = controls.empty? ? nil : controls.to_ber_contextspecific(0)

      pkt = [next_msgid.to_ber, request, controls].compact.to_ber_sequence
      @conn.write pkt

      result_code = 0
      controls = []

      while (be = @conn.read_ber(Net::LDAP::AsnSyntax)) && (pdu = Net::LDAP::PDU.new(be))
        case pdu.app_tag
        when 4 # search-data
          n_results += 1
          yield pdu.search_entry if block_given?
        when 19 # search-referral
          if return_referrals
            if block_given?
              se = Net::LDAP::Entry.new
              se[:search_referrals] = (pdu.search_referrals || [])
              yield se
            end
          end
        when 5 # search-result
          result_code = pdu.result_code
          controls = pdu.result_controls
          if return_referrals && result_code == 10
            if block_given?
              se = Net::LDAP::Entry.new
              se[:search_referrals] = (pdu.search_referrals || [])
              yield se
            end
          end
          break
        else
          raise Net::LDAP::LdapError, "invalid response-type in search: #{pdu.app_tag}"
        end
      end

      # When we get here, we have seen a type-5 response. If there is no
      # error AND there is an RFC-2696 cookie, then query again for the next
      # page of results. If not, we're done. Don't screw this up or we'll
      # break every search we do.
      #
      # Noticed 02Sep06, look at the read_ber call in this loop, shouldn't
      # that have a parameter of AsnSyntax? Does this just accidentally
      # work? According to RFC-2696, the value expected in this position is
      # of type OCTET STRING, covered in the default syntax supported by
      # read_ber, so I guess we're ok.
      more_pages = false
      if result_code == 0 and controls
        controls.each do |c|
          if c.oid == Net::LDAP::LdapControls::PagedResults
            # just in case some bogus server sends us more than 1 of these.
            more_pages = false
            if c.value and c.value.length > 0
              cookie = c.value.read_ber[1]
              if cookie and cookie.length > 0
                rfc2696_cookie[1] = cookie
                more_pages = true
              end
            end
          end
        end
      end

      break unless more_pages
    } # loop

    result_code
  end
end
# END: Duckpunch net-ldap to get around the 126 result bug

ldap_conf = {
  :host => host,
  :port => port,
  :base => group_base,
  :auth => {
    :method => :simple,
    :username => username,
    :password => password
  }
}
if encrypted == true
  ldap_conf[:encryption] = { :method => :simple_tls }
end

uuids = nil

Net::LDAP.open(ldap_conf) do |ldap|
  # find users in our pre-existing group
  groups_found = ldap.search(:base => group_base, :filter => Net::LDAP::Filter.eq( "cn", group_cn))
  raise "No group found with name: #{group_cn}" unless groups_found.size > 0
  raise "Found too many groups matching name: #{group_cn}" unless groups_found.size == 1
  group_found = groups_found[0]
  uuids = Set.new group_found[:memberUid]
  raise "No users found in group #{group_cn}.  Nothing to do." unless uuids.size > 0
  puts "Found #{uuids.size} users in #{group_cn}"

  # find the group we're adding them to
  new_group = ldap.search(:base => group_base, :filter => Net::LDAP::Filter.eq( "cn", new_group_cn))
  if new_group.size == 0
    puts "Group #{new_group_cn} does not exist.  Adding it...  "
    success = ldap.add(:dn => "cn=#{new_group_cn},#{group_base}", :attributes => { 
        :cn => new_group_cn,
        :objectclass => ["posixGroup", "top"],
        :gidNumber => new_group_gidNumber.to_s
      })
    raise "Error creating new group: code: #{ldap.get_operation_result.code}, message: #{ldap.get_operation_result.message}" unless success == true
    puts "Added new posixGroup at cn=#{new_group_cn},#{group_base}"
    new_group = ldap.search(:base => group_base, :filter => Net::LDAP::Filter.eq( "cn", new_group_cn))
  end
  raise "Found too many groups matching name: #{new_group_cn}" unless new_group.size == 1
  new_group = new_group[0]

  existing_uuids = new_group[:memberUid]
  puts "Found #{existing_uuids.size} users in #{new_group_cn}"
  raise "#{existing_uuids.size} users already exist in group #{new_group_cn}.  Cowardly refusing to perform migration.  Override with -force option." if existing_uuids.size > 0 && force == false
  
  # Add users to new group
  uuids = uuids.subtract(existing_uuids).to_a
  if uuids.size == 0
    puts "All users already exist in both groups.  Nothing to do."
    exit
  end
  
  if existing_uuids.size == 0
    # if :memberUid attr doesn't exist, we have to add one.  
    success = ldap.add_attribute new_group[:dn][0], :memberUid, uuids
  else
    existing_uuids.each { |uid| uuids << uid }
    
    success = ldap.replace_attribute new_group[:dn], :memberUid, uuids
  end
  raise "An error occured adding users to #{new_group_cn}:  code: #{ldap.get_operation_result.code}, message: #{ldap.get_operation_result.message}" unless success == true
  puts "Done:  #{uuids.size - existing_uuids.size} added to #{new_group_cn}"
end

