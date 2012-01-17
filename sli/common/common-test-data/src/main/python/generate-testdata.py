"""
@todo: factor Reader and Interchange File classes into separate module.
@todo: doctests.
"""

import csv
import cStringIO
import optparse, os, sys
import logging

# edfi xml types
from xmltypes import *


logging.basicConfig(level=logging.INFO, handler=logging.StreamHandler())
log = logging.getLogger(__name__)


def _get_sub_dict(d, prefix, strip=False):
    """
    Given a dictionary, returns a second dictionary with only those items
    whose key starts with the value of the prefix argument.  In the resulting
    dictionary, the part of the keys matching the prefix are dropped.  When
    the strip argument evaluates to True, dictionary items whose values are 
    empty strings (or contain only whitespace) will be ignored.
    
    >>> _get_sub_dict({'sayhello':'world', 'goodbye':'abc'}, 'say')
    {'hello': 'world'}
    
    >>> _get_sub_dict(
    ...     {'sayhello':'world', 'saygoodbye':'so long', 'saywhat':' '}, 'say'
    ... ) == {'hello': 'world', 'goodbye': 'so long', 'what': ' '}
    True
    
    >>> _get_sub_dict(
    ...     {'sayhello':'world', 'saygoodbye':'so long', 'saywhat':' '}, 'say', True
    ... ) == {'hello': 'world', 'goodbye': 'so long'}
    True

    """
    tmpd = {}
    for k in d:
        if k.startswith(prefix):
            v = d[k]
            if strip:
                v =v.strip()
                if not v:
                    continue
            tmpd[k[len(prefix):]] = v
    return tmpd


def dictify(xmlobject, hide_empty=False):
    """
    Take one of the generateDS objects and turn it into a dictionary.
    
    This is intended for debugging, but could be more broadly useful (for json 
    conversion, perhaps.)  Use the hide_empty flag to suppress empty attributes
    (which can reduce a lot of output).
    
    >>> dictify(Student(Name=Name(FirstName="John", LastSurname="Doe"))
    ... ) == {'CohortYears': [], 'DisplacementStatus': None, 
    ... 'EconomicDisadvantaged': None, 'OldEthnicity': None, 'Languages': None, 
    ... 'id': None, 'BirthData': None, 'LimitedEnglishProficiency': None, 
    ... 'Address': [], 'StudentCharacteristics': [], 'OtherName': [], 
    ... 'LearningStyles': None, 'Name': {'FirstName': 'John', 'MiddleName': 
    ... None, 'MaidenName': None, 'PersonalTitlePrefix': None, 'LastSurname': 
    ... 'Doe', 'Verification': None, 'GenerationCodeSuffix': None}, 
    ... 'HomeLanguages': None, 'Disabilities': [], 'Race': None, 
    ... 'StudentIndicators': [], 'ProfileThumbnail': None, 'ElectronicMail': [], 
    ... 'StudentUniqueStateId': None, 'StudentIdentificationCode': [], 
    ... 'Section504Disabilities': None, 'Telephone': [], 'Sex': None, 
    ... 'HispanicLatinoEthnicity': None, 'ProgramParticipations': [], 
    ... 'SchoolFoodServicesEligibility': None}
    True
    
    >>> dictify(Student(Name=Name(FirstName="John", LastSurname="Doe")), True
    ... ) == {'Name': {'LastSurname': 'Doe', 'FirstName': 'John'}}
    True

    
    """
    if isinstance(xmlobject, list):
        return [dictify(v) for v in xmlobject]
    elif xmlobject is None:
        return None
    elif isinstance(xmlobject, str):
        return xmlobject
    elif isinstance(xmlobject, int):
        return str(xmlobject)
    else:
        return dict([(k,dictify(v, hide_empty)) for k,v in xmlobject.__dict__.items() \
                     if k not in ('extensiontype_',) \
                     and (v or not hide_empty) ])
    



class CSV_Reader(object):
    """
    """
    
    def __init__(self, csv_file):
        """
        """
        self.file = csv_file
        self.reader = csv.reader(csv_file)
        self.headers = self.reader.next()
        
    def get_xml_object(self, d):
        """
        abstract generator method
        """
        raise NotImplementedError    

    def generate_xml_strings(self):
        """
        """
        
        string_io = cStringIO.StringIO()
        
        for row in self.reader:
            rowd = dict([(self.headers[i], row[i]) for i in range(len(self.headers))])
            object = self.get_xml_object(rowd)
            log.debug("generating xml for %s: %s" % 
                      (object.__class__.__name__, dictify(object, True)))
            object.export(string_io, 1)
            yield string_io.getvalue().strip()
            string_io.seek(0)
            string_io.truncate()
        
        raise StopIteration
    
    def close(self):
        if (not self.file.closed) and (sys.stdin != self.file):
            self.file.close()

    @staticmethod
    def get_name(d, prefix="name_", strip=True):
        """
        """
        value_dict = {}
        for xml_key, key in (
                 ('Verification', 'verification')
                , ('PersonalTitlePrefix', 'prefix')
                , ('FirstName', 'first')
                , ('MiddleName', 'middle')
                , ('LastSurname', 'last')
                , ('GenerationCodeSuffix', 'suffix')
                , ('MaidenName', 'maiden')
            ):
            if (not strip) or len(d[prefix + key]):
                value_dict[xml_key] = d[prefix + key]
        return Name(**value_dict)     

    @staticmethod
    def get_other_name(d, prefix="other_name_1_", strip=True):
        """
        """
        value_dict = {}
        for xml_key, key in (
                 ('OtherNameType', 'type')
                , ('PersonalTitlePrefix', 'prefix')
                , ('FirstName', 'first')
                , ('MiddleName', 'middle')
                , ('LastSurname', 'last')
                , ('GenerationCodeSuffix', 'suffix')
            ):
            if (not strip) or len(d[prefix + key]):
                value_dict[xml_key] = d[prefix + key]
        return OtherName(**value_dict)     

    @staticmethod
    def get_program_participation(d, prefix="programs_1_", strip=True):
        """
        """
        value_dict = {}
        for xml_key, key in (
                 ('Program', 'program')
                , ('BeginDate', 'begin_date')
                , ('EndDate', 'end_date')
                , ('DesignatedBy', 'designated_by')
            ):
            if (not strip) or len(d[prefix + key]):
                value_dict[xml_key] = d[prefix + key]
        return ProgramParticipation(**value_dict)     

    @staticmethod
    def get_learning_styles(d, prefix="learning_styles_", strip=True):
        """
        """
        value_dict = {}
        for xml_key, key in (
                 ('VisualLearning', 'visual')
                , ('AuditoryLearning', 'auditory')
                , ('TactileLearning', 'tactile')
            ):
            if (not strip) or d[prefix + key]:
                value_dict[xml_key] = d[prefix + key]
        return LearningStyles(**value_dict)     

    @staticmethod
    def get_indicator(d, prefix="indicator_1_", strip=True):
        """
        """
        value_dict = {}
        for xml_key, key in (
                 ('IndicatorName', 'name')
                , ('Indicator', 'indicator')
                , ('BeginDate', 'begin_date')
                , ('EndDate', 'end_date')
                , ('DesignatedBy', 'designated_by')
            ):
            if (not strip) or d[prefix + key]:
                value_dict[xml_key] = d[prefix + key]
        return StudentIndicator(**value_dict)     

    @staticmethod            
    def get_address(d, prefix="address_1_", strip=True):
        """
        """
        value_dict = {}
        for xml_key, key in (
                 ('AddressType', 'type')
                , ('StreetNumberName', 'street_number_name')
                , ('ApartmentRoomSuiteNumber', 'apt_number')
                , ('BuildingSiteNumber', 'bldg_number')
                , ('City', 'city')
                , ('StateAbbreviation', 'state_abbrev')
                , ('PostalCode', 'postal_code')
                , ('NameOfCounty', 'county_name')
                , ('CountyFIPSCode', 'county_fips_code')
                , ('CountryCode', 'country_code')
                , ('Latitude', 'latitude')
                , ('Longitude', 'longitude')
            ):
            if (not strip) or len(d[prefix + key]):
                value_dict[xml_key] = d[prefix + key]
        return Address(**value_dict)
                    
    @staticmethod            
    def get_birth_data(d, prefix="birth_", strip=True):
        """
        """
        value_dict = {}
        for xml_key, key in (
                  ('BirthDate', 'date')
                , ('CityOfBirth', 'city')
                , ('StateOfBirthAbbreviation', 'state_abbrev')
                , ('CountryOfBirthCode', 'country_code')
                , ('MultipleBirthStatus', 'multiple_status')
                #, ('DateEnteredUS', 'date_entered_us')
            ):
            if (not strip) or len(d[prefix + key]):
                value_dict[xml_key] = d[prefix + key]
        return BirthData(**value_dict)
                    
    @staticmethod            
    def get_ed_org_ref(d, prefix):
        """
        """
        tmpd = _get_sub_dict(d, prefix)
        return EducationalOrgReferenceType(
                        EducationalOrgIdentity=EducationalOrgIdentityType(
                                StateOrganizationId=tmpd['state_id']
                                                  )
                                        )            

    @staticmethod            
    def get_section_ref(d, prefix):
        """
        """
        tmpd = _get_sub_dict(d, prefix)
        return SectionReferenceType(
                        SectionIdentity=SectionIdentityType(
                                SchoolOrganizationID=tmpd['school_org_id']
                              , UniqueSectionCode=tmpd['unique_section_code']
                                                  )
                                        )            

    @staticmethod            
    def get_course_ref(d, prefix):
        """
        """
        tmpd = _get_sub_dict(d, prefix)
        return CourseReferenceType(
                        CourseIdentity=CourseIdentityType(
                                EducationOrganizationID=tmpd['ed_org_id']
                              , LocalCourseCode=tmpd['local_course_code']
                                                  )
                                        )            

    @staticmethod            
    def get_program_ref(d, prefix):
        """
        """
        
        def get_program_identity(d, prefix):
            tmpd = _get_sub_dict(d, prefix)
            kwargs = dict()
            if tmpd['id']:
                kwargs['ProgramId'] = tmpd['id']
            if tmpd['type']:
                kwargs['ProgramType'] = tmpd['type']
            return ProgramIdentityType(**kwargs)
            
        tmpd = _get_sub_dict(d, prefix)
        return ProgramReferenceType(
                        ProgramIdentity=get_program_identity(tmpd, 'program_identity_1_')
                                        )            


class Student_CSV_Reader(CSV_Reader):
    """
    """
    
    def get_xml_object(self, d):
        """
        """
        kwargs = dict( 
                    Name = self.get_name(d) 
                   , StudentUniqueStateId=int( d['state_id'])
                    )
        
        address = self.get_address(d, "address_1_")
        if address.hasContent_():
            kwargs['Address'] = [address]
        
        birthData = self.get_birth_data(d)
        kwargs['BirthData'] = birthData
        
        if d['telephone_1_number']:
            telephone1 = Telephone(TelephoneNumberType= d['telephone_1_type']
                                            ,PrimaryTelephoneNumberIndicator=d['telephone_1_primary_indicator']
                                            ,TelephoneNumber= d['telephone_1_number'])
            kwargs['Telephone'] = [telephone1]
        
        if d['other_name_1_type']:
            otherName1 = self.get_other_name(d)
            kwargs['OtherName'] = [otherName1]
        
        if d['email_1_type']:
            email1 = ElectronicMail(EmailAddress= d['email_1_address']
                                    ,EmailAddressType=d['email_1_type'])
            kwargs['ElectronicMail'] = [email1]
        
        if d['sex']:
            kwargs['Sex'] = d['sex']
        
        if d['hispanic_latino_ethnicity']:
            kwargs['HispanicLatinoEthnicity'] = d['hispanic_latino_ethnicity']
        else:
            # @todo: this is being put in just to get the xml validator to stop complaining. 
            kwargs['HispanicLatinoEthnicity'] = 'false'
        
        if d['old_ethnicity']:
            kwargs['OldEthnicity'] = d['old_ethnicity']
        
        if d['economic_disadvantaged']:
            kwargs['EconomicDisadvantaged'] = d['economic_disadvantaged']

        kwargs['Race'] = RaceType(RacialCategory=_get_sub_dict(d, "racial_category_", True).values())
        
        if d['school_food_services_eligibility']:
            kwargs['SchoolFoodServicesEligibility'] = d['school_food_services_eligibility']
        
        if d['characteristic_1_characteristic']:
            characteristic1 = StudentCharacteristic(Characteristic=d['characteristic_1_characteristic']
                                                    , BeginDate=d['characteristic_1_begin_date']
                                                    , EndDate=d['characteristic_1_end_date']
                                                    , DesignatedBy=d['characteristic_1_designated_by'])
            kwargs['StudentCharacteristics'] = [characteristic1]
        
        if d['disability_1_disability']:
            disability1 = Disability(Disability=d['disability_1_disability']
                                                    , DisabilityDiagnosis=d['disability_1_diagnosis']
                                                    , OrderOfDisability=int(d['disability_1_order']))
            kwargs['Disabilities'] = [disability1]

        # section_504_disability_1
        if d['section_504_disability_1']:
            kwargs['Section504Disabilities'] = Section504DisabilitiesType([d['section_504_disability_1']])

        # displacement_status_type
        if d['displacement_status_type']:
            kwargs['DisplacementStatus'] = d['displacement_status_type']
        
        # programs_1_program    programs_1_begin_date    programs_1_end_date    programs_1_designated_by
        if d['programs_1_program']:
            program1 = self.get_program_participation(d)
            kwargs['ProgramParticipations'] = [program1]
            
        # learning_styles_visual    learning_styles_auditory    learning_styles_tactile
        if d['learning_styles_visual']:
            for kp in ('visual', 'auditory', 'tactile'):
                k = 'learning_styles_' + kp
                if d.get(k):
                    d[k] = int(d[k])
            kwargs['LearningStyles'] = self.get_learning_styles(d)

        # cohort_year_type    cohort_year
        # FIXME:  seems to be a bug in generateDS - can't correctly add a year to Student.CohortYear ang get it to export properly.
        # if d['cohort_year']:
        #    kwargs['CohortYears'] = [CohortYear(CohortYearType=d['cohort_year_type'], Year=d['cohort_year'])]            
                
        # indicator_1_name    indicator_1_indicator    indicator_1_begin_date    indicator_1_end_date    indicator_1_designated_by
        if d['indicator_1_name']:
            indicator1 = self.get_indicator(d)
            kwargs['StudentIndicators'] = [indicator1]

        object = Student(**kwargs)
        return object
    

class Staff_CSV_Reader(CSV_Reader):
    """
    @todo: much overlap with Student reader
    """
    
    def get_xml_object(self, d):
        """
        """
        kwargs = dict( 
                    Name = self.get_name(d) 
                   , StaffUniqueStateId=int( d['state_id'])
                   , HighestLevelOfEducationCompleted=d['level_of_education']
                    )
        address = self.get_address(d, "address_1_")
        if address.hasContent_():
            kwargs['Address'] = address
        if d['birth_date']:
            kwargs['BirthData'] = BirthData(BirthDate= d['birth_date'])
        if d['sex']:
            kwargs['Sex'] = d['sex']
        if d['hispanic_latino_ethnicity']:
            kwargs['HispanicLatinoEthnicity'] = d['hispanic_latino_ethnicity']
        else:
            # @todo: this is being put in just to get the xml validator to stop complaining. 
            kwargs['HispanicLatinoEthnicity'] = 'false'
        #if d['racial_category_1']:
        kwargs['Race'] = RaceType(RacialCategory=_get_sub_dict(d, "racial_category_", True).values())
            
            
        #kwargs = dict([(k,v) for k,v in kwargs.items() if (getattr(v,'hasContent_',None) and v.hasContent_()) or v])
        object = Staff(**kwargs)
        return object
    

class Student_School_Assoc_CSV_Reader(CSV_Reader):
    """
    """
    
    def get_xml_object(self, d):
        """
        """
        identity_kwargs = dict(
                StudentUniqueStateId=int(d['student_state_id'])
                )
        name = self.get_name(d, "student_name_")
        if name.hasContent_():
            identity_kwargs['Name'] = name

        object = StudentSchoolAssociation (
                    StudentReference=StudentReferenceType(
                        StudentIdentity=StudentIdentityType(**identity_kwargs)
                                                      )
                  , SchoolReference=self.get_ed_org_ref(d, "school_")
                  , EntryDate=d['entry_date']
                  , EntryGradeLevel=d['entry_grade_level']
                  , GraduationPlan=d['graduation_plan']
                  , EducationalPlans=EducationalPlansType(EducationalPlan=_get_sub_dict(d, "educational_plan_").values())
                    )
        return object
    

class Student_Section_Assoc_CSV_Reader(CSV_Reader):
    """
    """
    
    def get_xml_object(self, d):
        """
        """
        identity_kwargs = dict(
                StudentUniqueStateId=int(d['student_state_id'])
                )
        name = self.get_name(d, "student_name_")
        if name.hasContent_():
            identity_kwargs['Name'] = name

        object = StudentSectionAssociation (
                    StudentReference=StudentReferenceType(
                        StudentIdentity=StudentIdentityType(**identity_kwargs)
                                                      )
                  , SectionReference=self.get_section_ref(d, "section_")
                    )
        return object
    

class Teacher_School_Assoc_CSV_Reader(CSV_Reader):
    """
    """
    
    def get_xml_object(self, d):
        """
        """
        identity_kwargs = dict(
                StaffUniqueStateId=int(d['staff_state_id'])
                )
        name = self.get_name(d, "staff_name_")
        if name.hasContent_():
            identity_kwargs['Name'] = name

        object = TeacherSchoolAssociation (
                    TeacherReference=StaffReferenceType(
                        StaffIdentity=StaffIdentityType(**identity_kwargs)
                                                      )
                  , SchoolReference=[self.get_ed_org_ref(d, "school_1_")]
                  , InstructionalGradeLevels = GradeLevelsType(GradeLevel=_get_sub_dict(d, "instr_grade_level_", True).values())
                  , ProgramAssignment=d['program_assignment']
                  , AcademicSubjects = AcademicSubjectsType(AcademicSubject=_get_sub_dict(d, "academic_subject_", True).values())
                    )
        return object
    

class Teacher_Section_Assoc_CSV_Reader(CSV_Reader):
    """
    """
    
    def get_xml_object(self, d):
        """
        """
        identity_kwargs = dict(
                StaffUniqueStateId=int(d['staff_state_id'])
                )
        name = self.get_name(d, "staff_name_")
        if name.hasContent_():
            identity_kwargs['Name'] = name

        object = TeacherSectionAssociation (
                    TeacherReference=StaffReferenceType(
                        StaffIdentity=StaffIdentityType(**identity_kwargs)
                                                      )
                  , SectionReference=self.get_section_ref(d, "section_")
                  , ClassroomPosition=d['classroom_position']
                    )
        return object
    

class School_CSV_Reader(CSV_Reader):
    """
    """
    
    def get_xml_object(self, d):
        """
        """
        
        kwargs = dict(
                          StateOrganizationId=d['state_id']
                        , NameOfInstitution=d['name']
                        , OrganizationCategory=d['org_category']
                        , Address = [ self.get_address(d) ]
                        , Telephone = [ InstitutionTelephone(TelephoneNumber=d['telephone_1_number'], 
                                                InstitutionTelephoneNumberType=d['telephone_1_type']) ]
                        , GradesOffered = GradeLevelsType(GradeLevel=_get_sub_dict(d, "grade_level_", True).values())
                        , SchoolCategories = SchoolCategoriesType(SchoolCategory=[
                                                                d['school_category_1']
                                                              ])
                        , LocalEducationAgencyReference = self.get_ed_org_ref(d, "lea_reference_1_")
                        )
        object = School(**kwargs)
        return object

    
class Course_CSV_Reader(CSV_Reader):
    """
    """
    
    def get_xml_object(self, d):
        """
        """
        
        kwargs = dict(
                          CourseTitle=d['course_title']
                        , NumberOfParts=int(d['number_of_parts'])
                        , LocalCourseCode=d['local_course_code']
                        , GradesOffered = GradeLevelsType(GradeLevel=_get_sub_dict(d, "grade_level_", True).values())
                        , EducationOrganizationReference = self.get_ed_org_ref(d, "ed_org_reference_")
                        )
        object = Course(**kwargs)
        return object

        
class Section_CSV_Reader(CSV_Reader):
    """
    """
    
    def get_xml_object(self, d):
        """
        """
        
        kwargs = dict(
                          UniqueSectionCode=d['unique_section_code']
                        , Term=d['term']
                        , SequenceOfCourse=int(d['seq_of_course'])
                        , CourseReference=self.get_course_ref(d, "course_")
                        , SchoolReference=self.get_ed_org_ref(d, "school_")
                        )
        object = Section(**kwargs)
        return object

        
class LEA_CSV_Reader(CSV_Reader):
    """
    """
    
    def get_xml_object(self, d):
        """
        """
        # @todo: FIXME - duplicates School reader
        kwargs = dict(
                          StateOrganizationId=d['state_id']
                        , NameOfInstitution=d['name']
                        , OrganizationCategory=d['org_category']
                        , Telephone = [ InstitutionTelephone(
                                             TelephoneNumber=d['telephone_1_number']
                                           , InstitutionTelephoneNumberType=d['telephone_1_type']) ]
                        , Address = [ self.get_address(d) ]
                        , LEACategory=d['lea_category']
                        #, ProgramReference = [self.get_program_ref(d, "program_reference_1_")]
                        )
        program_ref = self.get_program_ref(d, "program_reference_1_")
        if program_ref.hasContent_():
            kwargs['ProgramReference'] = [program_ref]
        object = LocalEducationAgency(**kwargs)
        return object

    
    
class Interchange_File(object):
    """
    """
    root_element_name = 'not implemented'
    schema_name = 'not implemented'
    
    def __init__(self, destination_file, schema_path):
        """
        """
        self.file = destination_file
        self.schema_path = schema_path
        self.file.write(self.get_header().strip() + os.linesep)

    def get_header(self):
        """
        """
        return """
<?xml version="1.0" encoding="UTF-8"?>
<%s xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="%s" xmlns="http://ed-fi.org/0100RFC062811">
        """ % (self.root_element_name, self.schema_name) #os.path.join(self.schema_path, self.schema_name))
        
    def get_footer(self):
        """
        """
        return '</%s>' % self.root_element_name
                
    def write(self, xml_string):
        """
        """
        self.file.write(xml_string)
        
    def close(self):
        if not self.file.closed:
            self.file.write(self.get_footer().strip() + os.linesep)
        if sys.stdout != self.file:
            self.file.close()


class Student_Interchange_File(Interchange_File):
    root_element_name = 'InterchangeStudent'
    schema_name = 'Interchange-Student.xsd'
    
class Enrollment_Interchange_File(Interchange_File):
    root_element_name = 'InterchangeStudentEnrollment'
    schema_name = 'Interchange-StudentEnrollment.xsd'
    
class Ed_Org_Interchange_File(Interchange_File):
    root_element_name = 'InterchangeEducationOrganization'
    schema_name = 'Interchange-EducationOrganization.xsd'
    
class Staff_Assoc_Interchange_File(Interchange_File):
    root_element_name = 'InterchangeStaffAssociation'
    schema_name = 'Interchange-StaffAssociation.xsd'
    
        
        
def main():
    """
    """
    parser = optparse.OptionParser()
    
    # meta
    parser.add_option('--verbose', '-v', dest='verbose',
                      action="store_true",  
                      help="be verbose")
    parser.add_option('--doctest', '-t', dest='run_doctest', action="store_true", 
                      help="run doctests")


    options, positional_args = parser.parse_args()
    
    if options.verbose:
        log.setLevel(logging.DEBUG)
    
    if options.run_doctest:
        import doctest
        doctest.testmod()
        return 0
    
    source_directory, output_directory = positional_args if len(positional_args)==2 else (None, None)
    if not source_directory:
        parser.print_usage()
        return 1

    # @todo: FIXME - assumes relative location        
    schema_dir = os.path.join(os.path.dirname(__file__), '../schemas/ed-fi')
    schema_path = os.path.relpath(schema_dir, output_directory)

    if not os.path.isdir(output_directory):
        os.makedirs(output_directory)
                
    for csv_sources, xml_file_name, xml_file_class in (
           ([('student-sample-data.csv', Student_CSV_Reader)], 'InterchangeStudent.xml', Student_Interchange_File)
          ,([('lea-sample-data.csv', LEA_CSV_Reader), 
             ('school-sample-data.csv', School_CSV_Reader),
             ('course-sample-data.csv', Course_CSV_Reader),
             ('section-sample-data.csv', Section_CSV_Reader)], 'InterchangeSchool.xml', Ed_Org_Interchange_File)
         # ,('lea-sample-data.csv', Student_CSV_Reader, 'InterchangeLEA.xml', Student_Interchange_File)
          ,([('student-school-assoc-sample-data.csv', Student_School_Assoc_CSV_Reader),
             ('student-section-assoc-sample-data.csv', Student_Section_Assoc_CSV_Reader)], 'InterchangeEnrollment.xml', Enrollment_Interchange_File)
          ,([('staff-sample-data.csv', Staff_CSV_Reader), 
             ('teacher-school-assoc-sample-data.csv', Teacher_School_Assoc_CSV_Reader), 
             ('teacher-section-assoc-sample-data.csv', Teacher_Section_Assoc_CSV_Reader)], 'InterchangeStaffAssociation.xml', Staff_Assoc_Interchange_File)
           ):

        xml_file = xml_file_class(open(os.path.join(output_directory, xml_file_name), 'w'), schema_path) 
        
        try:
            for csv_file_name, csv_reader_class in csv_sources:
                csv_reader = csv_reader_class(open(os.path.join(source_directory, csv_file_name),'rU'))
                for xml_string in csv_reader.generate_xml_strings():
                    print >> xml_file, xml_string
        finally:
            xml_file.close()
            
    return 0



if __name__=="__main__":
    sys.exit( main() )
    
