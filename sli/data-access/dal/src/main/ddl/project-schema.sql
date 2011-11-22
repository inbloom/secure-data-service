
# -----------------------------------------------------------------------
# student
# -----------------------------------------------------------------------
drop table if exists student;

CREATE TABLE student
(
    student_id INTEGER NOT NULL AUTO_INCREMENT,
    student_school_id VARCHAR(20) NOT NULL,
    personal_title_prefix VARCHAR(20),
    first_name VARCHAR(35) NOT NULL,
    middle_name VARCHAR(35),
    last_surname VARCHAR(35) NOT NULL,
    generation_code_suffix VARCHAR(20),
    maiden_name VARCHAR(35),
    personal_information_verification VARCHAR(20),
    sex VARCHAR(20) NOT NULL,
    birth_date DATETIME NOT NULL,
    city_of_birth VARCHAR(30),
    state_of_birth_abbreviation VARCHAR(30),
    country_of_birth VARCHAR(20),
    date_entered_US DATETIME,
    multiple_birth_status BIT,
    profile_thumbnail VARCHAR(59),
    hispanic_latino_ethnicity BIT,
    old_ethnicity VARCHAR(40),
    economic_disadvantaged BIT,
    school_food_services_eligibility VARCHAR(20),
    limited_english_proficiency VARCHAR(20),
    displacement_status VARCHAR(30),
    PRIMARY KEY(student_id));

CREATE TABLE school
(
	school_id INTEGER NOT NULL AUTO_INCREMENT,
	full_name VARCHAR(60) NOT NULL,
	short_name VARCHAR(60),
	state_organization_id VARCHAR(60),
	web_site VARCHAR(80),
	
	school_type VARCHAR(30),
	charter_status VARCHAR(50),
	title_i_part_a_school_designation VARCHAR(75),
	magnet_special_program_emphasis_school VARCHAR(50),
	administrative_funding_control VARCHAR(30),
	operational_status VARCHAR(30),
	PRIMARY KEY(school_id)
);
