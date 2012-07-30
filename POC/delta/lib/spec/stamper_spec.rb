require File.dirname(__FILE__) + '/../stamper'
require 'mongo'

describe Stamper::BaseStamper do
  before(:each) do
    @db = Mongo::Connection.new('localhost', 27017)['delta_spec']
    @stamper = Stamper::BaseStamper.new(@db, nil, nil)
  end
  describe "#stamp" do
    it "should raise a 'Not implemented exception'" do
      expect {@stamper.stamp}.to raise_error "Not implemented"
    end
  end
  describe "#get_edorgs" do
    it "should raise a 'Not implemented exception'" do
      expect {@stamper.get_edorgs}.to raise_error "Not implemented"
    end
  end
  describe "#get_teachers" do
    it "should raise a 'Not implemented exception'" do
      expect {@stamper.get_teachers}.to raise_error "Not implemented"
    end
  end
  describe "#wrap_up" do
    it "should raise a 'Not implemented exception'" do
      expect {@stamper.wrap_up}.to raise_error "Not implemented"
    end
  end
end
describe Stamper::StudentStamper do
  before(:each) do
    @db = Mongo::Connection.new('localhost', 27017)['delta_spec']
    @stamper = Stamper::StudentStamper.new(@db, nil, nil)
  end
  describe "#stamp" do
    it "should raise a 'Not implemented exception'" do
      expect {@stamper.stamp}.to raise_error "Not implemented"
    end
  end
  describe "#get_edorgs" do
    it "should raise a 'Not implemented exception'" do
      expect {@stamper.get_edorgs}.to raise_error "Not implemented"
    end
  end
  describe "#get_teachers" do
    it "should raise a 'Not implemented exception'" do
      expect {@stamper.get_teachers}.to raise_error "Not implemented"
    end
  end
  describe "#wrap_up" do
    it "should raise a 'Not implemented exception'" do
      expect {@stamper.wrap_up}.to raise_error "Not implemented"
    end
  end
end
