require 'test/unit'
require 'eventbus' 

class TestLdap < Test::Unit::TestCase
    def setup
        @scheduler = Eventbus::JobScheduler.new 
    end

    def test_jobpoller
        setupJobPollerFixture
        puts "test_mefirst called !"
    end 

    


end
