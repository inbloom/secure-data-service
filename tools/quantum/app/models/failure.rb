class Failure
    include Mongoid::Document

    store_in collection: "failure"

    field :feature,     :type => String
    field :scenario,    :type => String
    field :component,    :type => String
    field :hostname,     :type => String
    field :timestamp,     :type => Float
end