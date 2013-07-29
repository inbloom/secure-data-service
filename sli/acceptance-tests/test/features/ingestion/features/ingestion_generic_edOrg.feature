Feature: Generic EdOrg Ingestion

  Scenario: Post Generic EdOrg Sample Data Set
    Given the "Midgar" tenant db is empty
    When I ingest "GenericEdOrg.zip"
    #basic SEA
    # basic LEA
    #basic school
    #nested LEA
    #school off nested LEA
    #school with two LEA parents at same level
    #school with LEA parents at different levels
    #school off muliple level and same levels (combining above two scenarios)
    #school/LEA off SEA


