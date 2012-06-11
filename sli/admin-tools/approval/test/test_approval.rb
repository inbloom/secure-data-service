testdir = File.dirname(__FILE__)
$LOAD_PATH << testdir + "/../lib"
require 'approval'
require 'test/unit'
require 'socket'

class MockEmailer 
    def send_approval_email(args = {})
        @last_call = args.clone 
    end
end

class MockTransitionActionConfig
  def initialize(emailer, is_sandbox)
    @emailer = emailer
    @is_sandbox = is_sandbox
  end

  def transition(user)
    if user && user[:status] == ApprovalEngine::ACTION_APPROVE
      @emailer.send_approval_email()
    end
  end
end

class TestApprovalEngine < Test::Unit::TestCase
    def setup
        # define two basic users 
        @jd_email = "jdoe_#{Socket.gethostname}@example.com"
        @jd_emailtoken = "0102030405060708090A0B0C0D0E0F"
        @jd_user = {
            :first      => "John",
            :last       => "Doe", 
            :email      => @jd_email,
            :password   => "secret", 
            :vendor     => "Acme Inc.",
#            :emailtoken => @jd_emailtoken,
            :status     => "submitted",
            :homedir    => "/home/exampleuser",
            :emailAddress => @jd_email
        }

        @td_email = "tdoe_#{Socket.gethostname}@example.com"
        @td_user = @jd_user.clone
        @td_user[:email] = @td_email
    end

    def regular_workflow(is_sandbox)
        #@ldap = LDAPStorage.new("ldap.slidev.org", 389, "ou=DevTest,dc=slidev,dc=org", "cn=DevLDAP User, ou=People,dc=slidev,dc=org", "Y;Gtf@w{")
        #@ldap = LDAPStorage.new("ldap.slidev.org", 389, "ou=Sandbox,ou=DevTest,dc=slidev,dc=org", "cn=DevLDAP User,ou=People,dc=slidev,dc=org", "Y;Gtf@w{")
        @ldap = LDAPStorage.new("ldap.slidev.org", 389, "ou=Local,ou=DevTest,dc=slidev,dc=org", "cn=DevLDAP User,ou=People,dc=slidev,dc=org", "Y;Gtf@w{")
        #@ldap = LDAPStorage.new("ldap.slidev.org", 389, "ou=ProductionTest,ou=DevTest,dc=slidev,dc=org", "cn=DevLDAP User,ou=People,dc=slidev,dc=org", "Y;Gtf@w{")
        #@ldap = LDAPStorage.new("ldap.slidev.org", 389, "ou=ciTest,ou=DevTest,dc=slidev,dc=org", "cn=DevLDAP User,ou=People,dc=slidev,dc=org", "Y;Gtf@w{")
        #@ldap = LDAPStorage.new("rcldap01.slidev.org", 636, "dc=slidev,dc=org", "cn=admin,dc=slidev,dc=org", "Y;Gtf@w{")
        @mock_emailer = MockEmailer.new
        @transition_action_config = MockTransitionActionConfig.new(@mock_emailer, is_sandbox)
        ApprovalEngine.init(@ldap, @mock_emailer, @transition_action_config, is_sandbox)
        @ldap.get_user_groups(@jd_email).each do |g|
            @ldap.remove_user_group(@jd_email, g)
        end
        @ldap.get_user_groups(@td_email).each do |g|
            @ldap.remove_user_group(@td_email, g)
        end

        ApprovalEngine.remove_user(@jd_email)
        ApprovalEngine.remove_user(@td_email)

        # create the new users and verify the initial state 
        ApprovalEngine.add_disabled_user(@td_user)
        ApprovalEngine.add_disabled_user(@jd_user)
        assert(@ldap.user_exists?(@jd_email))
        user = @ldap.read_user(@jd_email)
        assert(user)
        assert(user[:email] == @jd_email)
        assert(user[:status] == ApprovalEngine::STATE_SUBMITTED)
        assert(!!user[:emailtoken])

        # transition to approve the eula. Note: transitions should only contain one 
        # transition, namely to go to state EULA_ACCEPTED
        user = ApprovalEngine.get_user(@jd_email)
        ApprovalEngine.change_user_status(@jd_email, user[:transitions][0])
        user = ApprovalEngine.get_user(@jd_email)
        assert(user)
        assert(user[:email] == @jd_email)
        assert(user[:status] == ApprovalEngine::STATE_EULA_ACCEPTED)

        ApprovalEngine.verify_email(user[:emailtoken])
        if !is_sandbox
            assert(@ldap.read_user(@jd_email)[:status] == ApprovalEngine::STATE_PENDING)
            ApprovalEngine.change_user_status(@jd_email, ApprovalEngine::ACTION_APPROVE)
        end
        assert(@ldap.read_user(@jd_email)[:status] == ApprovalEngine::STATE_APPROVED)

        if is_sandbox
            assert(@ldap.get_user_groups(@jd_email).sort == ApprovalEngine::SANDBOX_ROLES.sort)
        else 
            assert(@ldap.get_user_groups(@jd_email).sort == ApprovalEngine::PRODUCTION_ROLES.sort)
        end 

        ApprovalEngine.change_user_status(@jd_email, ApprovalEngine::ACTION_DISABLE)
        assert(@ldap.read_user(@jd_email)[:status] == ApprovalEngine::STATE_DISABLED)
        assert(ApprovalEngine.get_roles(@jd_email) == [])
        ApprovalEngine.change_user_status(@jd_email, ApprovalEngine::ACTION_ENABLE)
        assert(@ldap.read_user(@jd_email)[:status] == ApprovalEngine::STATE_APPROVED)

        if is_sandbox
            assert(ApprovalEngine.get_roles(@jd_email).sort == ApprovalEngine::SANDBOX_ROLES.sort)
        else
            assert(ApprovalEngine.get_roles(@jd_email).sort == ApprovalEngine::PRODUCTION_ROLES.sort)
        end

        # move the other user to rejected state 
        if !is_sandbox
            ApprovalEngine.change_user_status(@td_email, ApprovalEngine::ACTION_ACCEPT_EULA)
            user = ApprovalEngine.get_user(@td_email)
            ApprovalEngine.verify_email(user[:emailtoken])
            ApprovalEngine.change_user_status(@td_email, ApprovalEngine::ACTION_REJECT)
            assert(@ldap.read_user(@td_email)[:status] == ApprovalEngine::STATE_REJECTED)
            roles = ApprovalEngine.get_roles(@td_email) 
            assert(roles == [], "Expected empty roles but got #{roles}")

            user = ApprovalEngine.get_user(@td_email)
            ApprovalEngine.change_user_status(@td_email, user[:transitions][0])
            user = ApprovalEngine.get_user(@td_email)
            assert(user[:status] == ApprovalEngine::STATE_APPROVED)

            pending_or_approved = ApprovalEngine.get_users().select do |u|
                ([ApprovalEngine::STATE_APPROVED, ApprovalEngine::STATE_PENDING]).include?(u[:status])
            end

            assert(ApprovalEngine.get_user_count == pending_or_approved.length)
        end

        # make sure we have two 

        ApprovalEngine.remove_user(@td_email)
        ApprovalEngine.remove_user(@jd_email)
        #ApprovalEngine.remove_user(@jd_email)
        #assert(!ApprovalEngine.user_exists?(@jd_email))
        #assert(ApprovalEngine.get_roles(@jd_email) == [])
        #ApprovalEngine.remove_user(@td_email)
        #users = ApprovalEngine.get_users().select { |u| !![@jd_email, @td_email].index(u[:email]) }
        #assert(users == [], "Expected empty array got #{users}.")
    end
    

    def test_production
        regular_workflow(true)
    end

    def test_sandbox
        regular_workflow(false)
    end
end
