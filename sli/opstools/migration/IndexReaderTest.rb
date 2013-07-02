require 'test/unit'
require './73_db_reset'

class IndexReaderTest < Test::Unit::TestCase

  def test_read_indexes
    test_input = "applicationAuthorization,false,body.applicationId:1"
    expected = Index.new "applicationAuthorization", false, { "body.applicationId" => Mongo::ASCENDING }

    actual = IndexReader.from_string(test_input)
    assert_equal(expected, actual)
  end

  def test_complex_index
    test_input = "custom_entities,false,metaData.entityId:-1,metaData.clientId:1"
    expected = Index.new "custom_entities", false, { "metaData.entityId" => Mongo::DESCENDING,
                                                     "metaData.clientId" => Mongo::ASCENDING }
    actual = IndexReader.from_string(test_input)
    assert_equal(expected, actual)

    test_input = "assessment,false,body.assessmentTitle:1,body.academicSubject:1,body.gradeLevelAssessed:1"
    expected = Index.new "assessment", false, { "body.assessmentTitle" => Mongo::ASCENDING,
                                                "body.academicSubject" => Mongo::ASCENDING,
                                                "body.gradeLevelAssessed" => Mongo::ASCENDING }
    actual = IndexReader.from_string(test_input)
    assert_equal(expected, actual)
  end
end
