Feature: Search Indexer reindexes accordingly when we update/delete/create Assessment,
  Assessment Period Descriptor, and AssessmentFamily

Scenario: Search Indexer should reindex when I update, delete, and create
  the Assessment Period Descriptor and Assessment Family

  # oauth
  Given I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "staff" "rrogers" with password "rrogers1234"

  # update assessment period descriptor
  Given format "application/json"
  When I navigate to GET "/v1/assessments?assessmentPeriodDescriptor.description=Beginning%20of%20Year%202013-2014%20for%20First%20grade"
  Then I should receive a return code of 200
  Then I should receive a collection with 2 elements
  When I navigate to GET "/v1/assessments?assessmentPeriodDescriptor.description=Hello%20World"
  Then I should receive a return code of 200
  Then I should receive a collection with 0 elements
  Then I update the "assessmentPeriodDescriptor" with ID "7bcb5c6cb1b13bf11406676f4397d6dec4659561_id" field "body.description" to "Hello World"

#  Note, this is commented out because it's not valid if oplog agent is running.
#  When I navigate to GET "/v1/assessments?assessmentPeriodDescriptor.description=Hello%20World"
#  Then I should receive a return code of 200
#  Then I should receive a collection with 0 elements

  When I send an update event to the search indexer for collection "assessmentPeriodDescriptor" and ID "7bcb5c6cb1b13bf11406676f4397d6dec4659561_id"
  Then I will EVENTUALLY GET "/v1/assessments?assessmentPeriodDescriptor.description=Hello%20World" with 2 elements
  When I navigate to GET "/v1/assessments?assessmentPeriodDescriptor.description=Hello%20World"
  Then I should receive a return code of 200
  Then I should receive a collection with 2 elements
  When I navigate to GET "/v1/assessments?assessmentPeriodDescriptor.description=Beginning%20of%20Year%202013-2014%20for%20First%20grade"
  Then I should receive a return code of 200
  Then I should receive a collection with 0 elements
  Then I update the "assessmentPeriodDescriptor" with ID "7bcb5c6cb1b13bf11406676f4397d6dec4659561_id" field "body.description" to "Beginning of Year 2013-2014 for First grade"
  When I send an update event to the search indexer for collection "assessmentPeriodDescriptor" and ID "7bcb5c6cb1b13bf11406676f4397d6dec4659561_id"
  Then I will EVENTUALLY GET "/v1/assessments?assessmentPeriodDescriptor.description=Hello%20World" with 0 elements
  Then I will EVENTUALLY GET "/v1/assessments?assessmentPeriodDescriptor.description=Beginning%20of%20Year%202013-2014%20for%20First%20grade" with 2 elements
  When I navigate to GET "/v1/assessments?assessmentPeriodDescriptor.description=Hello%20World"
  Then I should receive a return code of 200
  Then I should receive a collection with 0 elements
  When I navigate to GET "/v1/assessments?assessmentPeriodDescriptor.description=Beginning%20of%20Year%202013-2014%20for%20First%20grade"
  Then I should receive a return code of 200
  Then I should receive a collection with 2 elements

  # update assessment family
  Given format "application/json"
  When I navigate to GET "/v1/assessments?assessmentFamilyHierarchyName=2013%20Standard.2013%20Tenth%20grade%20Standard"
  Then I should receive a return code of 200
  Then I should receive a collection with 2 elements
  When I navigate to GET "/v1/assessments?assessmentFamilyHierarchyName=2013%20Standard.Hello%20World"
  Then I should receive a return code of 200
  Then I should receive a collection with 0 elements
  Then I update the "assessmentFamily" with ID "7a5d0827649621e700028e0c574a4cbb73de1404_id" field "body.assessmentFamilyTitle" to "Hello World"
  When I send an update event to the search indexer for collection "assessmentFamily" and ID "7a5d0827649621e700028e0c574a4cbb73de1404_id"
  Then I will EVENTUALLY GET "/v1/assessments?assessmentFamilyHierarchyName=2013%20Standard.Hello%20World" with 2 elements
  When I navigate to GET "/v1/assessments?assessmentFamilyHierarchyName=2013%20Standard.Hello%20World"
  Then I should receive a return code of 200
  Then I should receive a collection with 2 elements
  When I navigate to GET "/v1/assessments?assessmentFamilyHierarchyName=2013%20Standard.2013%20Tenth%20grade%20Standard"
  Then I should receive a return code of 200
  Then I should receive a collection with 0 elements
  Then I update the "assessmentFamily" with ID "7a5d0827649621e700028e0c574a4cbb73de1404_id" field "body.assessmentFamilyTitle" to "2013 Tenth grade Standard"
  When I send an update event to the search indexer for collection "assessmentFamily" and ID "7a5d0827649621e700028e0c574a4cbb73de1404_id"
  Then I will EVENTUALLY GET "/v1/assessments?assessmentFamilyHierarchyName=2013%20Standard.Hello%20World" with 0 elements
  Then I will EVENTUALLY GET "/v1/assessments?assessmentFamilyHierarchyName=2013%20Standard.2013%20Tenth%20grade%20Standard" with 2 elements
  When I navigate to GET "/v1/assessments?assessmentFamilyHierarchyName=2013%20Standard.Hello%20World"
  Then I should receive a return code of 200
  Then I should receive a collection with 0 elements
  When I navigate to GET "/v1/assessments?assessmentFamilyHierarchyName=2013%20Standard.2013%20Tenth%20grade%20Standard"
  Then I should receive a return code of 200
  Then I should receive a collection with 2 elements

  Given format "application/json"
  Then I navigate to GET "/v1/assessments?assessmentPeriodDescriptor.description=Beginning%20of%20Year%202013-2014%20for%20First%20grade"
  Then I should receive a return code of 200
  Then I should receive a collection with 2 elements

  # Search Indexer does not update its index until an oplog event is received
  When I delete the "assessmentPeriodDescriptor" with ID "7bcb5c6cb1b13bf11406676f4397d6dec4659561_id"
  Then I navigate to GET "/v1/assessments?assessmentPeriodDescriptor.description=Beginning%20of%20Year%202013-2014%20for%20First%20grade"
  Then I should receive a return code of 200
  Then I should receive a collection with 2 elements

  # Sending the delete message to search indexer should update its index asynchronously
  Then I send a delete event to the search indexer for collection "assessmentPeriodDescriptor" and ID "7bcb5c6cb1b13bf11406676f4397d6dec4659561_id"
  Then I navigate to GET "/v1/assessments?assessmentPeriodDescriptor.description=Beginning%20of%20Year%202013-2014%20for%20First%20grade"
  Then I should receive a return code of 200
  Then I will EVENTUALLY GET "/v1/assessments?assessmentPeriodDescriptor.description=Beginning%20of%20Year%202013-2014%20for%20First%20grade" with 0 elements

  # Just to be paranoid, I am doing a check using another step definition
  Then I navigate to GET "/v1/assessments?assessmentPeriodDescriptor.description=Beginning%20of%20Year%202013-2014%20for%20First%20grade"
  Then I should receive a return code of 200
  Then I should receive a collection with 0 elements

  # Search Indexer does not update its index until an oplog event is received
  Then I create the previously deleted entity
  Then I navigate to GET "/v1/assessments?assessmentPeriodDescriptor.description=Beginning%20of%20Year%202013-2014%20for%20First%20grade"
  Then I should receive a return code of 200
  Then I should receive a collection with 0 elements

  Then I send an insert event to the search indexer from last created entity
  Then I navigate to GET "/v1/assessments?assessmentPeriodDescriptor.description=Beginning%20of%20Year%202013-2014%20for%20First%20grade"
  Then I should receive a return code of 200
  Then I will EVENTUALLY GET "/v1/assessments?assessmentPeriodDescriptor.description=Beginning%20of%20Year%202013-2014%20for%20First%20grade" with 2 elements

  # Just to be paranoid, I am doing a check using another step definition
  Then I navigate to GET "/v1/assessments?assessmentPeriodDescriptor.description=Beginning%20of%20Year%202013-2014%20for%20First%20grade"
  Then I should receive a return code of 200
  Then I should receive a collection with 2 elements

   # delete assessment family, check assessmentFamilyHierarchyName get updated accordingly
    Given format "application/json"
    When I navigate to GET "/v1/assessments?assessmentFamilyHierarchyName=2013%20Standard.2013%20Tenth%20grade%20Standard"
    Then I should receive a return code of 200
    Then I should receive a collection with 2 elements

    # Search Indexer does not update its index until an oplog event is received
    When I delete the "assessmentFamily" with ID "c461a52b5257e2784a25ee44d0fa888decf86d32_id"
    Then I navigate to GET "/v1/assessments?assessmentFamilyHierarchyName=2013%20Standard.2013%20Tenth%20grade%20Standard"
    Then I should receive a return code of 200
    Then I should receive a collection with 2 elements

    # Sending the delete message to search indexer should update its index asynchronously
    Then I send a delete event to the search indexer for collection "assessmentFamily" and ID "c461a52b5257e2784a25ee44d0fa888decf86d32_id"
    Then I navigate to GET "/v1/assessments?assessmentFamilyHierarchyName=2013%20Standard.2013%20Tenth%20grade%20Standard"
    Then I should receive a return code of 200

    # existing assessmentFamilyHierarchyName search should return 0 entity
    Then I will EVENTUALLY GET "/v1/assessments?assessmentFamilyHierarchyName=2013%20Standard.2013%20Tenth%20grade%20Standard" with 0 elements

    # updated assessmentFamilyHierarchyName search should return 2 assessment entities
    Then I navigate to GET "/v1/assessments?assessmentFamilyHierarchyName=2013%20Tenth%20grade%20Standard"
    Then I should receive a return code of 200
    Then I will EVENTUALLY GET "/v1/assessments?assessmentFamilyHierarchyName=2013%20Tenth%20grade%20Standard" with 2 elements

    # insert the deleted assessmentFamily entity back, check assessmentFamilyHierarchyName get updated accordingly
    Then I create the previously deleted entity
    Then I navigate to GET "/v1/assessments?assessmentFamilyHierarchyName=2013%20Standard.2013%20Tenth%20grade%20Standard"
    Then I should receive a return code of 200
    Then I should receive a collection with 0 elements
    Then I send an insert event to the search indexer from last created entity
    Then I navigate to GET "/v1/assessments?assessmentFamilyHierarchyName=2013%20Standard.2013%20Tenth%20grade%20Standard"
    Then I should receive a return code of 200
    Then I will EVENTUALLY GET "/v1/assessments?assessmentFamilyHierarchyName=2013%20Standard.2013%20Tenth%20grade%20Standard" with 2 elements


