Feature: Generic EdOrg Ingestion

  Scenario: Post Generic EdOrg Sample Data Set
    Given the "Midgar" tenant db is empty
    When I ingest "GenericEdOrg.zip"

    #basic SEA
    Then there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "1"
        | field                               | value                                       |
        | _id                                 | 884daa27d806c2d725bc469b273d840493f84b4d_id |
        | body.stateOrganizationId            | STANDARD-SEA                                |
        | body.organizationCategories         | State Education Agency                      |
    #basic LEA
    And there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "2"
        | field                               | value                                       |
        | _id                                 | 1b223f577827204a1c7e9c851dba06bea6b031fe_id |
        | body.stateOrganizationId            | IL-DAYBREAK                                 |
        | body.organizationCategories         | Local Education Agency                      |
        | body.parentEducationAgencyReference | 884daa27d806c2d725bc469b273d840493f84b4d_id |
    #basic school
    And there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "3"
        | field                               | value                                       |
        | _id                                 | 352e8570bd1116d11a72755b987902440045d346_id |
        | body.stateOrganizationId            | South Daybreak Elementary                   |
        | body.organizationCategories         | School                                      |
        | body.parentEducationAgencyReference | 1b223f577827204a1c7e9c851dba06bea6b031fe_id |
       #| metaData.edOrgs                     | 1b223f577827204a1c7e9c851dba06bea6b031fe_id |
       #| metaData.edOrgs                     | 352e8570bd1116d11a72755b987902440045d346_id |
    #nested LEA
    And there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "4"
        | field                               | value                                       |
        | _id                                 | c58b7bec2c0a9496e9e6f3b85426cd2f65f23ec4_id |
        | body.stateOrganizationId            | IL-DAYBREAK-SUB-LEA                         |
        | body.organizationCategories         | Local Education Agency                      |
        | body.parentEducationAgencyReference | 1b223f577827204a1c7e9c851dba06bea6b031fe_id |
    #school off nested LEA
    And there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "5"
        | field                               | value                                       |
        | _id                                 | e6972a8edb280114d26b1c4b919801f73c21232e_id |
        | body.stateOrganizationId            | South Daybreak Elementary 2                 |
        | body.organizationCategories         | School                                      |
        | body.parentEducationAgencyReference | c58b7bec2c0a9496e9e6f3b85426cd2f65f23ec4_id |
       #| metaData.edOrgs                     | e6972a8edb280114d26b1c4b919801f73c21232e_id |
       #| metaData.edOrgs                     | c58b7bec2c0a9496e9e6f3b85426cd2f65f23ec4_id |
       #| metaData.edOrgs                     | 1b223f577827204a1c7e9c851dba06bea6b031fe_id |
       #| metaData.edOrgs                     | 884daa27d806c2d725bc469b273d840493f84b4d_id |
    #multiple parents test with LEAs at same level - regular leas
    And there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "6"
        | field                               | value                                       |
        | _id                                 | a9bef2e3a445efb50d39fa47784a0542eaff5589_id |
        | body.stateOrganizationId            | IL-DAYBREAK2                                |
        | body.organizationCategories         | Local Education Agency                      |
        | body.parentEducationAgencyReference | 884daa27d806c2d725bc469b273d840493f84b4d_id |
    And there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "7"
        | field                               | value                                       |
        | _id                                 | 612e326cff7a9f85beb9a3fa6287e31a5bd5c62d_id |
        | body.stateOrganizationId            | IL-DAYBREAK3                                |
        | body.organizationCategories         | Local Education Agency                      |
        | body.parentEducationAgencyReference | 884daa27d806c2d725bc469b273d840493f84b4d_id |
    And there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "8"
        | field                               | value                                       |
        | _id                                 | 411f22e41453fba1c75c13b09b541520acf5be5b_id |
        | body.stateOrganizationId            | South Daybreak Elementary 3                 |
        | body.organizationCategories         | School                                      |
        | body.parentEducationAgencyReference | 1b223f577827204a1c7e9c851dba06bea6b031fe_id |
        | body.parentEducationAgencyReference | a9bef2e3a445efb50d39fa47784a0542eaff5589_id |
        | body.parentEducationAgencyReference | 612e326cff7a9f85beb9a3fa6287e31a5bd5c62d_id |
       #| metaData.edOrgs                     | 411f22e41453fba1c75c13b09b541520acf5be5b_id |
       #| metaData.edOrgs                     | 1b223f577827204a1c7e9c851dba06bea6b031fe_id |
       #| metaData.edOrgs                     | a9bef2e3a445efb50d39fa47784a0542eaff5589_id |
       #| metaData.edOrgs                     | 612e326cff7a9f85beb9a3fa6287e31a5bd5c62d_id |
       #| metaData.edOrgs                     | 884daa27d806c2d725bc469b273d840493f84b4d_id |
    #multiple parents test with LEAs at same level - nested leas
    And there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "9"
        | field                               | value                                       |
        | _id                                 | fa8f2751396d8162683eecfe86bb472119fc540c_id |
        | body.stateOrganizationId            | IL-DAYBREAK-SUB-LEA2                        |
        | body.organizationCategories         | Local Education Agency                      |
        | body.parentEducationAgencyReference | 1b223f577827204a1c7e9c851dba06bea6b031fe_id |
    And there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "10"
        | field                               | value                                       |
        | _id                                 | 215575a74c87251a8d28f8e02b7ebdfd547d954b_id |
        | body.stateOrganizationId            | South Daybreak Elementary 4                 |
        | body.organizationCategories         | School                                      |
        | body.parentEducationAgencyReference | e6972a8edb280114d26b1c4b919801f73c21232e_id |
        | body.parentEducationAgencyReference | fa8f2751396d8162683eecfe86bb472119fc540c_id |
    #school with multiple LEAs at different levels and same levels (above two combined)
    And there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "11"
        | field                               | value                                       |
        | _id                                 | 016a47e1a44fa2097032f82f7163eeabe6f5c377_id |
        | body.stateOrganizationId            | South Daybreak Elementary 5                 |
        | body.organizationCategories         | School                                      |
        | body.parentEducationAgencyReference | 1b223f577827204a1c7e9c851dba06bea6b031fe_id |
        | body.parentEducationAgencyReference | a9bef2e3a445efb50d39fa47784a0542eaff5589_id |
        | body.parentEducationAgencyReference | 612e326cff7a9f85beb9a3fa6287e31a5bd5c62d_id |
        | body.parentEducationAgencyReference | e6972a8edb280114d26b1c4b919801f73c21232e_id |
        | body.parentEducationAgencyReference | fa8f2751396d8162683eecfe86bb472119fc540c_id |
       #| metaData.edOrgs                     | 016a47e1a44fa2097032f82f7163eeabe6f5c377_id |
       #| metaData.edOrgs                     | 1b223f577827204a1c7e9c851dba06bea6b031fe_id |
       #| metaData.edOrgs                     | a9bef2e3a445efb50d39fa47784a0542eaff5589_id |
       #| metaData.edOrgs                     | 612e326cff7a9f85beb9a3fa6287e31a5bd5c62d_id |
       #| metaData.edOrgs                     | e6972a8edb280114d26b1c4b919801f73c21232e_id |
       #| metaData.edOrgs                     | fa8f2751396d8162683eecfe86bb472119fc540c_id |
       #| metaData.edOrgs                     | 884daa27d806c2d725bc469b273d840493f84b4d_id |
    #school/LEA off SEA
    And there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "12"
        | field                               | value                                       |
        | _id                                 | de66bbfafd994193ae6aaf019ecfa14825c32575_id |
        | body.stateOrganizationId            | South Daybreak Elementary 6                 |
        | body.organizationCategories         | School                                      |
        | body.organizationCategories         | Local Education Agency                      |
        | body.parentEducationAgencyReference | 884daa27d806c2d725bc469b273d840493f84b4d_id |
        | body.schoolCategories               | Elementary School                           |
        | body.charterStatus                  | School Charter                              |
        | body.gradesOffered                  | Kindergarten                                |
        | body.gradesOffered                  | First grade                                 |
        | body.gradesOffered                  | Second grade                                |
        | body.gradesOffered                  | Third grade                                 |
        | body.gradesOffered                  | Fourth grade                                |
        | body.gradesOffered                  | Fifth grade                                 |
        | body.LEACategory                    | Independent                                 |
       #| metaData.edOrgs                     | de66bbfafd994193ae6aaf019ecfa14825c32575_id |
       #| metaData.edOrgs                     | 884daa27d806c2d725bc469b273d840493f84b4d_id |
    #other organization categories
   #And there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "13"
       #| field                               | value                                       |
       #| _id                                 | de66bbfafd994193ae6aaf019ecfa14825c32575_id |
       #| body.stateOrganizationId            | South Daybreak Elementary 7                 |
       #| body.organizationCategories         | School                                      |
       #| body.organizationCategories         | Local Education Agency                      |
       #| body.parentEducationAgencyReference | 884daa27d806c2d725bc469b273d840493f84b4d_id |
       #| body.schoolCategories               | Elementary School                           |
       #| body.charterStatus                  | School Charter                              |
       #| body.gradesOffered                  | Kindergarten                                |
       #| body.gradesOffered                  | First grade                                 |
       #| body.gradesOffered                  | Second grade                                |
       #| body.gradesOffered                  | Third grade                                 |
       #| body.gradesOffered                  | Fourth grade                                |
       #| body.gradesOffered                  | Fifth grade                                 |
       #| body.LEACategory                    | Independent                                 |
       #| metaData.edOrgs                     | de66bbfafd994193ae6aaf019ecfa14825c32575_id |
       #| metaData.edOrgs                     | 884daa27d806c2d725bc469b273d840493f84b4d_id |

    #cycle tests - referring to itself
    And there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "14"
        | field                               | value                                       |
        | _id                                 | 265cdc11571c4e28aceb6ed5fef795a9ffb2ea5c_id |
        | body.stateOrganizationId            | Cycle 1                                     |
        | body.organizationCategories         | Local Education Agency                      |
        | body.parentEducationAgencyReference | 265cdc11571c4e28aceb6ed5fef795a9ffb2ea5c_id |
    #cycle tests - 3 way cycle
    And there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "15"
        | field                               | value                                       |
        | _id                                 | 2a05470ef1a2eacb408513fc646b8f39f1d9cd61_id |
        | body.stateOrganizationId            | Cycle 2                                     |
        | body.organizationCategories         | Local Education Agency                      |
        | body.parentEducationAgencyReference | 3657ac93276c35e866e0b20b523dcca6c807cc81_id |
    And there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "16"
        | field                               | value                                       |
        | _id                                 | 3657ac93276c35e866e0b20b523dcca6c807cc81_id |
        | body.stateOrganizationId            | Cycle 3                                     |
        | body.organizationCategories         | Local Education Agency                      |
        | body.parentEducationAgencyReference | 2e68cac9a6a00c44a2f314219794859b4b503e6e_id |
    And there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "17"
        | field                               | value                                       |
        | _id                                 | 2e68cac9a6a00c44a2f314219794859b4b503e6e_id |
        | body.stateOrganizationId            | Cycle 4                                     |
        | body.organizationCategories         | Local Education Agency                      |
        | body.parentEducationAgencyReference | 2a05470ef1a2eacb408513fc646b8f39f1d9cd61_id |

    #update tests
    Then "0" records in the "educationOrganization" collection with field "body.nameOfInstitution" matching ".*Updated" should be in the "Midgar" tenant db
    When I ingest "GenericEdOrgUpdates.zip"
    Then "16" records in the "educationOrganization" collection with field "body.nameOfInstitution" matching ".*Updated" should be in the "Midgar" tenant db

    #duplicate detection test
    And I should see following map of entry counts in the corresponding collections:
      | collectionName                           |              count|
      | recordHash                               |                 16|
      | educationOrganization                    |                 16|
    # clear out the ingested entities to ensure we aren't upserting a second time
    Given the following collections are empty in datastore:
      | collectionName                            |
      | educationOrganization                     |
    When zip file is scp to ingestion landing zone with name "Reingest-GenericEdOrg.zip"
    Then a batch job for file "Reingest-GenericEdOrg.zip" is completed in database
    And I should see "InterchangeEducationOrganization.xml educationOrganization 16 deltas" in the resulting batch job file
    And I should not see a warning log file created
    And I should not see an error log file created
    And I should see following map of entry counts in the corresponding collections:
      | collectionName                           |              count|
      | recordHash                               |                 16|
      | educationOrganization                    |                  0|

