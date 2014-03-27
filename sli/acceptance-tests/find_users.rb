# Ad hoc script to find users referenced by the ATs
# This script was needed when we first switched to use of IM-LDAP and needed
# to populate LDAP with needed users

require 'find'
require 'json'
require 'pp'

class FindUsers

  attr :users, :missing_users
  attr_reader :feature_files

  KNOWN_USERS = [
    ['iladmin',              'iladmin1234',              'inBloom'],
    ['slcoperator',          'slcoperator1234',          'inBloom'],
    ['rrogers'               'rrogers1234',              'IL'],
    ['cgray'                 'cgray1234',                'IL'],
    ['cgrayadmin',           'cgray1234',                'IL'],
    ['iladmin',              'iladmin1234',              'SLI'],
    ['administrator',        'administrator1234',        'SLI'],
    ['leader',               'leader1234',               'SLI'],
    ['sunsetadmin',          'sunsetadmin1234',          'SLI'],
    ['operator',             'operator1234',             'SLI'],
    ['sandboxoperator',      'sandboxoperator1234',      'SLI'],
    ['sandboxadministrator', 'sandboxadministrator1234', 'SLI'],
    ['ingestionuser',        'ingestionuser1234',        'SLI'],
    ['sandboxingestionuser', 'sandboxingestionuser1234', 'SLI'],
    ['sunsetrealmadmin',     'sunsetrealmadmin1234',     'SLI'],
    ['sandboxdeveloper',     'sandboxdeveloper',         'SLI'],
    ['sunsetadmin',          'sunsetadmin1234',          'SLI'],
    ['sbantu',               'sbantu1234',               'IL'],
    ['rbraverman',           'rbraverman1234',           'IL'],
    ['msmith',               'msmith1234',               'IL'],
    ['mgonzales',            'mgonzales1234',            'IL'],
    ['linda.kim',            'linda.kim1234',            'IL'],
    ['llogan',               'llogan1234',               'IL'],
    ['manthony',             'manthony1234',             'IL'],
    ['jstevenson',           'jstevenson1234',           'IL'],
    ['msmith',               'msmith1234',               'Illinois Daybreak School District 4529'],
    ['linda.kim',            'linda.kim1234',            'Illinois Daybreak School District 4529'],
    ['akopel',               'akopel1234',               'Illinois Daybreak School District 4529'],
    ['mgonzales',            'mgonzales1234',            'Illinois Daybreak School District 4529'],
    ['marsha.sollars',       'marsha.sollars1234',       'Illinois Daybreak Parents'],
    ['student.m.sollars',    'student.m.sollars1234',    'Illinois Daybreak Students'],
    ['jpratt',               'jpratt1234',               'NY'],
    ['johndoe',              'johndoe1234',              'NY'],
  ]

  def initialize
    @users = []
    @feature_files = []
    cur = File.dirname(__FILE__);
    Find.find("#{cur}/test/features") do |path|
      @feature_files << path if path =~ /.*\.feature$/
    end
  end

  def find
    @feature_files.each do |file|
      content = IO.read(file)

      pattern = %r{\sI am logged in using "([^\"<]*)" "([^\"]*)" to realm "([^\"]*)"$}
      content.scan(pattern) { |username, password, realm| users << [username, password, realm] }

      pattern = %r{\sI log in to realm "(.*?)" using simple-idp as "(.*?)" "([^\"<].*?)" with password "(.*?)"$}
      content.scan(pattern) { |realm, user_type, username, password| users << [username, password, realm] }

      pattern = %r{\sI am user "([^\"<]*)" in IDP "([^"]*)"$}
      content.scan(pattern) { |username, realm| users << [username, "#{username}1234", realm]}
    end

    @users += KNOWN_USERS

    dedup
  end

  def find_missing
    @missing_users = []
    @feature_files.each do |file|
      content = IO.read(file)

      pattern = %r{\sI submit the credentials "([^"]*)" "([^"]*)"}
      content.scan(pattern) { |username, password| @missing_users << username }
    end
    @missing_users.sort!.uniq!
  end

  def users_by_realm
    grouped = users.group_by { |item| item[2] }
    grouped.each do |realm, users|
      users.each {|user| user.delete_at(-1)}
    end
    grouped
  end

  private

  def dedup
    @users.uniq!.sort!
  end

end

f=FindUsers.new
users = f.find
known_users = users.map{|u| u.first}
other_users = f.find_missing

missing_users = other_users - known_users

puts "Found #{missing_users.count} users"

missing_users.each {|u| puts u}
