require 'mongo'

$index_70_deletes = {
  "applicationAuthorization" => [
    {"body.appIds" => Mongo::ASCENDING},
    {"body.authId" => Mongo::ASCENDING, "body.authType" => Mongo::ASCENDING}
    ],
  "assessment" => [
    {"body.assessmentIdentificationCode" => Mongo::ASCENDING},
    {"body.assessmentPeriodDescriptor.codeValue" => Mongo::ASCENDING}
    ],
  "calendarDate" => [
    {"body.date" => Mongo::ASCENDING, "body.calendarEvent" => Mongo::ASCENDING}
    ],
  "course" => [
    {"body.courseCode.ID" => Mongo::ASCENDING, "body.courseCode.identificationSystem" => Mongo::ASCENDING}
    ],
  "educationOrganization" => [
    {"body.educationOrgIdentificationCode.ID" => Mongo::ASCENDING},
    {"body.educationOrgIdentificationCode" => Mongo::ASCENDING},
    {"body.parentEducationAgencyReference" => Mongo::ASCENDING, "type" => Mongo::ASCENDING}
    ],
  "gradingPeriod" => [
    {"body.beginDate" => Mongo::ASCENDING},
    {"body.gradingPeriodIdentity.schoolYear" => Mongo::ASCENDING}
    ],
  "graduationPlan" => [
    {"body.graduationPlanType" => Mongo::ASCENDING}
    ],
  "learningObjective" => [
    {"body.learningObjectiveId.identificationCode" => Mongo::ASCENDING},
    {"body.learningObjectiveId" => Mongo::ASCENDING},
    {"body.learningStandards" => Mongo::ASCENDING}
    ],
  "program" => [
    {"studentProgramAssociation.body.educationOrganizationId" => Mongo::ASCENDING}
    ],
  "reportCard" => [
    {"body.gpaCumulative" => Mongo::ASCENDING}
    ],
  "section" => [
    {"body.uniqueSectionCode" => Mongo::ASCENDING}
    ],
  "session" => [
    {"body.gradingPeriodReference" => Mongo::ASCENDING},
    {"body.sessionName" => Mongo::ASCENDING}
    ],
  "studentAcademicRecord" => [
    {"body.reportCards" => Mongo::ASCENDING}
    ],
  "studentCompetency" => [
    {"body.objectiveId.studentCompetencyObjectiveId" => Mongo::ASCENDING},
    {"metaData.externalId" => Mongo::ASCENDING, "metaData.studentUniqueStateId" => Mongo::ASCENDING, "metaData.uniqueSectionCode" => Mongo::ASCENDING}
    ],
  "studentCompetencyObjective" => [
    {"body.studentCompetencyObjectiveId" => Mongo::ASCENDING}
    ],
  "studentSchoolAssociation" => [
    {"body.graduationPlanId" => Mongo::ASCENDING}
    ],
}


def get_tenant_names(host, port)
  tenant_names = []

  client = Mongo::MongoClient.new(host, port)

  tenants = client.db('sli').collection('tenant').find({}, {:fields => {"body.dbName" => 1}})

  tenants.each do |t|
    tenant_names << t["body"]["dbName"]
  end

  client.close()

  tenant_names
end


def rmv_indexes(host, port, tenant_name)
  client = Mongo::MongoClient.new(host, port)

  $index_70_deletes.each  do |coll, coll_idxs|
    crnt_idxs = client.db(tenant_name).collection(coll).index_information()

    crnt_idxs.each do |idx|
      idx_key = idx[1]["key"]
      idx_name = idx[1]["name"]

      if coll_idxs.include? idx_key
        puts "Removing index: #{tenant_name}:#{coll}.#{idx_name}"
        client.db(tenant_name).collection(coll).drop_index(idx_name)
      end
    end
  end

  client.close()
end


if ARGV.count < 1
  puts "This script removes indexes from a MongoDB cluster for Release 70"
  puts "Usage: <cluster dbhost:port>"
  puts "\t dbhost - hostname for mongo"
  puts "\t port - port mongo is running on (27017 is common)"
  exit
end

cluster_hp = ARGV[0].split(":")

tenant_names = get_tenant_names(cluster_hp[0], cluster_hp[1])

tenant_names.each do |tn|
  rmv_indexes(cluster_hp[0], cluster_hp[1], tn)
end
