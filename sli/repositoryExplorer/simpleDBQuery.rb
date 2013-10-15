require 'json'
require 'mongo'
require 'date'


@conn ||= Mongo::Connection.new("localhost", 27017 )
@tenantDb = @conn.db('02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a')



#query = <<-jsonDelimiter
#  [
#  {"$project":{"schools":1}}
#  ,{"$unwind":"$schools"}
#  ,{"$match":{ "$or":[     {"schools.exitWithdrawDate":{"$exists":true, "$gt": "2013-09-16"}} ,{"schools.exitWithdrawDate":{"$exists":false}}    ]}}
#  ,{"$project":{"_id":1, "schools._id":1}}
#  ,{"$group":{"_id":"$schools._id", "students":{"$addToSet":"$_id"}}}
#  ]
#jsonDelimiter
#
#query = JSON.parse(query)
#result = @tenantDb.collection('student').aggregate(query)
#schoolStudents = {}
#result.each{ |schoolIdToStudents|
#  schoolId = schoolIdToStudents['_id']
#  students = schoolIdToStudents['students']
#  schoolStudents[schoolId] = students
#}
#
#edOrgId = '772a61c687ee7ecd8e6d9ad3369f7883409f803b_id';
#
#result = @tenantDb.collection('attendance').find({'body.schoolId' => edOrgId}).count()
#puts result

date = DateTime.now.strftime('%Y-%m-%d')

edOrgId = "352e8570bd1116d11a72755b987902440045d346_id"


query = <<-jsonDelimiter
{
   "$and":[
      {
         "$or":[
            {
               "body.beginDate":{
                  "$lt":"#{date}",
                  "$exists":true
               }
            },
            {
               "body.beginDate":{
                  "$exists":false
               }
            }
         ]
      },
      {
         "$or":[
            {
               "body.endDate":{
                  "$gt":"#{date}",
                  "$exists":true
               }
            },
            {
               "body.endDate":{
                  "$exists":false
               }
            }
         ]
      },
      {
         "body.educationOrganizationReference":"#{edOrgId}"
      }
   ]
}
jsonDelimiter
query = JSON.parse(query)
puts query.to_json
staffForEdOrgRS = @tenantDb.collection('staffEducationOrganizationAssociation').find(query)

staffForEdOrg = staffForEdOrgRS.map{ |staffEducationOrganizationAssociation|
  staffEducationOrganizationAssociation['body']['staffReference']
}

puts staffForEdOrg

cohortForEdOrgStaff =  @tenantDb.collection('staffCohortAssociation').find({'body.staffId' => {'$in' => staffForEdOrg}})
puts cohortForEdOrgStaff.count()
