/****** Ed-Fi Logical Database Schema SQL Script 1.0 11/11/11 ******/



/****** Object:  Schema [edfi]    Script Date: 10/20/2011 10:43:59 ******/

CREATE SCHEMA [edfi] AUTHORIZATION [dbo]

GO

/****** Object:  Table [edfi].[AttendanceEventType]    Script Date: 10/18/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AttendanceEventType](

	[AttendanceEventTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](30) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKAttendanceEventType] PRIMARY KEY NONCLUSTERED 

(

	[AttendanceEventTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIAttendanceEventTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO



/****** Object:  Table [edfi].[AddressType]    Script Date: 10/18/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AddressType](

	[AddressTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKAddressType] PRIMARY KEY NONCLUSTERED 

(

	[AddressTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIAddressTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO









/****** Object:  Table [edfi].[LinguisticAccommodationsType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[LinguisticAccommodationsType](

	[LinguisticAccommodationsTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](50) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKLinguisticAccommodationsType] PRIMARY KEY NONCLUSTERED 

(

	[LinguisticAccommodationsTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UILinguisticAccommodationsTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[LimitedEnglishProficiencyType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[LimitedEnglishProficiencyType](

	[LimitedEnglishProficiencyTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](400) NOT NULL,

 CONSTRAINT [PKLimitedEnglishProficiencyType] PRIMARY KEY NONCLUSTERED 

(

	[LimitedEnglishProficiencyTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UILimitedEnglishProficiencyTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[LevelType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[LevelType](

	[LevelTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](30) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKLevelType] PRIMARY KEY NONCLUSTERED 

(

	[LevelTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UILevelTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[LevelOfEducationType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[LevelOfEducationType](

	[LevelOfEducationTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKLevelOfEducationType] PRIMARY KEY NONCLUSTERED 

(

	[LevelOfEducationTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UILevelOfEducationTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[LeaveEventCategoryType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[LeaveEventCategoryType](

	[LeaveEventCategoryTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](30) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKLeaveEventCategoryType] PRIMARY KEY NONCLUSTERED 

(

	[LeaveEventCategoryTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UILeaveEventCategoryTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[MeetingDaysType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[MeetingDaysType](

	[MeetingDaysTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKMeetingDaysType] PRIMARY KEY NONCLUSTERED 

(

	[MeetingDaysTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIMeetingDaysTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[MediumOfInstructionType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[MediumOfInstructionType](

	[MediumOfInstructionTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKMediumOfInstructionType] PRIMARY KEY NONCLUSTERED 

(

	[MediumOfInstructionTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIMediumOfInstructionTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[MagnetSpecialProgramEmphasisSchoolType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[MagnetSpecialProgramEmphasisSchoolType](

	[MagnetSpecialProgramEmphasisSchoolTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](60) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKMagnetSpecialProgramEmphasisSchoolType] PRIMARY KEY NONCLUSTERED 

(

	[MagnetSpecialProgramEmphasisSchoolTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIMagnetSpecialProgramEmphasisSchoolTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[LEACategoryType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[LEACategoryType](

	[LEACategoryTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](30) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKLEACategoryType] PRIMARY KEY NONCLUSTERED 

(

	[LEACategoryTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UILEACategoryTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[LanguagesType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[LanguagesType](

	[LanguageTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKLanguagesType] PRIMARY KEY NONCLUSTERED 

(

	[LanguageTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UILanguagesTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[ItemCategoryType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ItemCategoryType](

	[ItemCategoryTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKItemCategoryType] PRIMARY KEY NONCLUSTERED 

(

	[ItemCategoryTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIItemCategoryTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[InstitutionalTelephoneNumberType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[InstitutionalTelephoneNumberType](

	[InstitutionalTelephoneNumberTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PK_InstitutionalTelephoneNumberType] PRIMARY KEY CLUSTERED 

(

	[InstitutionalTelephoneNumberTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[IncidentLocationType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[IncidentLocationType](

	[IncidentLocationTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](50) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKIncidentLocationType] PRIMARY KEY NONCLUSTERED 

(

	[IncidentLocationTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIIncidentLocationTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[IdeaEligibilityType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[IdeaEligibilityType](

	[IdeaEligibilityTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKIdeaEligibilityType] PRIMARY KEY NONCLUSTERED 

(

	[IdeaEligibilityTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIIdeaEligibilityTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[GraduationPlanType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[GraduationPlanType](

	[GraduationPlanTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKGraduationPlanType] PRIMARY KEY NONCLUSTERED 

(

	[GraduationPlanTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIGraduationPlanTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[EducationPlansType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[EducationPlansType](

	[EducationPlansTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKEducationPlansType] PRIMARY KEY NONCLUSTERED 

(

	[EducationPlansTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIEducationPlansTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[EducationOrgIdentificationSystemType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[EducationOrgIdentificationSystemType](

	[EducationOrgIdentificationSystemTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](50) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PK_EducationOrgIdentificationSystemType] PRIMARY KEY CLUSTERED 

(

	[EducationOrgIdentificationSystemTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[GradingPeriodType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[GradingPeriodType](

	[GradingPeriodTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKGradingPeriodType] PRIMARY KEY NONCLUSTERED 

(

	[GradingPeriodTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIGradingPeriodTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[GradeType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[GradeType](

	[GradeTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKGradeTypeType] PRIMARY KEY NONCLUSTERED 

(

	[GradeTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIGradeTypeTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[GradeLevelType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[GradeLevelType](

	[GradeLevelTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](40) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PK_GradeLevelType] PRIMARY KEY CLUSTERED 

(

	[GradeLevelTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[GenerationCodeSuffixType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[GenerationCodeSuffixType](

	[GenerationCodeSuffixTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKGenerationCodeSuffixType] PRIMARY KEY NONCLUSTERED 

(

	[GenerationCodeSuffixTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIGenerationCodeSuffixTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[FieldType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[FieldType](

	[FieldTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](60) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKFieldType] PRIMARY KEY NONCLUSTERED 

(

	[FieldTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIFieldTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[ExitWithdrawType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ExitWithdrawType](

	[ExitWithdrawTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKExitWithdrawTypeType] PRIMARY KEY NONCLUSTERED 

(

	[ExitWithdrawTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIExitWithdrawTypeTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[EntryType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[EntryType](

	[EntryTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKEntryTypeType] PRIMARY KEY NONCLUSTERED 

(

	[EntryTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIEntryTypeTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[EmploymentStatusType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[EmploymentStatusType](

	[EmploymentStatusTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKEmploymentStatusType] PRIMARY KEY NONCLUSTERED 

(

	[EmploymentStatusTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIEmploymentStatusTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[ElectronicMailType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ElectronicMailType](

	[ElectronicMailTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PK_ElectronicMailType] PRIMARY KEY CLUSTERED 

(

	[ElectronicMailTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[EducationalEnvironmentType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[EducationalEnvironmentType](

	[EducationalEnvironmentTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](40) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKEducationalEnvironmentType] PRIMARY KEY NONCLUSTERED 

(

	[EducationalEnvironmentTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIEducationalEnvironmentTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[EducationOrganizationCategoryType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[EducationOrganizationCategoryType](

	[EducationOrganizationCategoryTypeId] [int] IDENTITY(1,1) NOT NULL,

	[OrganizationCategory] [nvarchar](30) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKOrganizationCategoryType] PRIMARY KEY NONCLUSTERED 

(

	[EducationOrganizationCategoryTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIOrganizationCategoryTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[OrganizationCategory] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[CourseCodeSystemType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CourseCodeSystemType](

	[CourseCodeSystemTypeId] [int] NOT NULL,

	[CodeValue] [nvarchar](50) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PK_CourseCodeSystemType] PRIMARY KEY CLUSTERED 

(

	[CourseCodeSystemTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[CourseGPAApplicabilityType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CourseGPAApplicabilityType](

	[CourseGPAApplicabilityTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKCourseGPAApplicabilityType] PRIMARY KEY NONCLUSTERED 

(

	[CourseGPAApplicabilityTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UICourseGPAApplicabilityTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[CourseLevelType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CourseLevelType](

	[CourseLevelTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKCourseLevelType] PRIMARY KEY NONCLUSTERED 

(

	[CourseLevelTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UICourseLevelTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO



/****** Object:  Table [edfi].[CourseDefinedByType]    Script Date: 11/12/2010 15:46:14 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CourseDefinedByType](

	[CourseDefinedByTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](30) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKCourseDefinedByType] PRIMARY KEY NONCLUSTERED 

(

	[CourseDefinedByTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UICourseDefinedByTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[CourseLevelCharacteristicsType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CourseLevelCharacteristicsType](

	[CourseLevelCharacteristicsTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](40) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKCourseLevelCharacteristicsType] PRIMARY KEY NONCLUSTERED 

(

	[CourseLevelCharacteristicsTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UICourseLevelCharacteristicsTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[CreditType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CreditType](

	[CreditTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NULL,

 CONSTRAINT [PK_CreditType] PRIMARY KEY CLUSTERED 

(

	[CreditTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[CredentialType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CredentialType](

	[CredentialTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKCredentialTypeType] PRIMARY KEY NONCLUSTERED 

(

	[CredentialTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UICredentialTypeTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[CredentialFieldDescriptor]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CredentialFieldDescriptor](

	[CredentialFieldDescriptorId] [int] IDENTITY(1,1) NOT NULL,

	[EducationOrganizationId] [int] NOT NULL,

	[CodeValue] [nvarchar](20) NULL,

	[Description] [nvarchar](1024) NOT NULL,

	[AcademicSubjectTypeId] [int] NULL,

 CONSTRAINT [PK_CredentialFieldDescriptor] PRIMARY KEY CLUSTERED 

(

	[CredentialFieldDescriptorId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[CourseRepeatCodeType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CourseRepeatCodeType](

	[CourseRepeatCodeTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKCourseRepeatCodeType] PRIMARY KEY NONCLUSTERED 

(

	[CourseRepeatCodeTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UICourseRepeatCodeTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[DiplomaType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[DiplomaType](

	[DiplomaTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKDiplomaTypeType] PRIMARY KEY NONCLUSTERED 

(

	[DiplomaTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIDiplomaTypeTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[DiplomaLevelType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[DiplomaLevelType](

	[DiplomaLevelTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKDiplomaLevelType] PRIMARY KEY NONCLUSTERED 

(

	[DiplomaLevelTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIDiplomaLevelTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[DisciplineActionLengthDifferenceReasonType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[DisciplineActionLengthDifferenceReasonType](

	[DisciplineActionLengthDifferenceReasonTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PK_DisciplineActionLengthDifferenceReasonType] PRIMARY KEY CLUSTERED 

(

	[DisciplineActionLengthDifferenceReasonTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[DisciplineDescriptor]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[DisciplineDescriptor](

	[DisciplineDescriptorId] [int] IDENTITY(1,1) NOT NULL,

	[EducationOrganizationId] [int] NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](1024) NULL,

	[ShortDescription] [nvarchar](50) NOT NULL,

 CONSTRAINT [PK_DisciplineDescriptor] PRIMARY KEY CLUSTERED 

(

	[DisciplineDescriptorId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[DisabilityType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[DisabilityType](

	[DisabilityTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKDisabilityType] PRIMARY KEY NONCLUSTERED 

(

	[DisabilityTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIDisabilityTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[CohortYearType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CohortYearType](

	[CohortYearTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKCohortYearType] PRIMARY KEY NONCLUSTERED 

(

	[CohortYearTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UICohortYearTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[CohortType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CohortType](

	[CohortTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](35) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKCohortTypeType] PRIMARY KEY NONCLUSTERED 

(

	[CohortTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UICohortTypeTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[CohortScopeType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CohortScopeType](

	[CohortScopeTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKCohortScopeType] PRIMARY KEY NONCLUSTERED 

(

	[CohortScopeTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UICohortScopeTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[ClassroomPositionType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ClassroomPositionType](

	[ClassroomPositionTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKClassroomPositionType] PRIMARY KEY NONCLUSTERED 

(

	[ClassroomPositionTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIClassroomPositionTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[CourseAttemptResultType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CourseAttemptResultType](

	[CourseAttemptResultTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKCourseAttemptResultType] PRIMARY KEY NONCLUSTERED 

(

	[CourseAttemptResultTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UICourseAttemptResultTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[CountryCodeType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CountryCodeType](

	[CountryCodeTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKCountryCodeType] PRIMARY KEY NONCLUSTERED 

(

	[CountryCodeTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UICountryCodeTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[ContentStandardType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ContentStandardType](

	[ContentStandardTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](40) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKContentStandardType] PRIMARY KEY NONCLUSTERED 

(

	[ContentStandardTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIContentStandardTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[BehaviorCategoryType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[BehaviorCategoryType](

	[BehaviorCategoryTypeId] [int] NOT NULL,

	[CodeValue] [nvarchar](30) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PK_BehaviorCategoryType] PRIMARY KEY CLUSTERED 

(

	[BehaviorCategoryTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO



/****** Object:  Table [edfi].[AttendanceEventCategoryType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AttendanceEventCategoryType](

	[AttendanceEventCategoryTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKAttendanceEventCategoryType] PRIMARY KEY NONCLUSTERED 

(

	[AttendanceEventCategoryTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIAttendanceEventCategoryTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[ClassPeriod]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ClassPeriod](

	[SchoolId] [int] NOT NULL,

	[ClassPeriodName] [nvarchar](20) NOT NULL,

 CONSTRAINT [PK_ClassPeriod] PRIMARY KEY CLUSTERED 

(

	[SchoolId] ASC,

	[ClassPeriodName] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the portion of a typical daily session in which students receive instruction in a specified subject (e.g., morning, sixth period, block period, or AB schedules). 

NEDM: Class Period

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ClassPeriod', @level2type=N'COLUMN',@level2name=N'ClassPeriodName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This entity represents the designation of regularly scheduled series of class meetings at designated times and days of the week.

NEDM: Class Schedule

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ClassPeriod'

GO

/****** Object:  Table [edfi].[CharterStatusType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CharterStatusType](

	[CharterStatusTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](30) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKCharterStatusType] PRIMARY KEY NONCLUSTERED 

(

	[CharterStatusTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UICharterStatusTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[CareerPathwayType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CareerPathwayType](

	[CareerPathwayTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKCareerPathwayType] PRIMARY KEY NONCLUSTERED 

(

	[CareerPathwayTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UICareerPathwayTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[CalendarEventType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CalendarEventType](

	[CalendarEventTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](40) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKCalendarEventType] PRIMARY KEY NONCLUSTERED 

(

	[CalendarEventTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UICalendarEventTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[AssessmentItemResultType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AssessmentItemResultType](

	[AssessmentItemResultTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKAssessmentItemResultType] PRIMARY KEY NONCLUSTERED 

(

	[AssessmentItemResultTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIAssessmentItemResultTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[AssessmentReportingMethodType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AssessmentReportingMethodType](

	[AssessmentReportingMethodTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](50) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKAssessmentReportingMethodType] PRIMARY KEY NONCLUSTERED 

(

	[AssessmentReportingMethodTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIAssessmentReportingMethodTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[AssessmentPeriodDescriptor]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AssessmentPeriodDescriptor](

	[AssessmentPeriodDescriptorId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](1024) NOT NULL,

	[ShortDescription] [nvarchar](50) NULL,

	[BeginDate] [date] NULL,

	[EndDate] [date] NULL,

 CONSTRAINT [PK_AssessmentPeriodDescriptor] PRIMARY KEY CLUSTERED 

(

	[AssessmentPeriodDescriptorId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[AssessmentIdentificationSystemType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AssessmentIdentificationSystemType](

	[AssessmentIdentificationSystemTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](50) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PK_AssessmentIdentificationSystemType] PRIMARY KEY CLUSTERED 

(

	[AssessmentIdentificationSystemTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[AcademicSubjectType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AcademicSubjectType](

	[AcademicSubjectTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](50) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKAcademicSubjectType] PRIMARY KEY NONCLUSTERED 

(

	[AcademicSubjectTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIAcademicSubjectTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[AcademicHonorsType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AcademicHonorsType](

	[AcademicHonorsTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKAcademicHonorsTypeType] PRIMARY KEY NONCLUSTERED 

(

	[AcademicHonorsTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIAcademicHonorsTypeTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[AdministrativeFundingControlType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AdministrativeFundingControlType](

	[AdministrativeFundingControlTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKAdministrativeFundingControlType] PRIMARY KEY NONCLUSTERED 

(

	[AdministrativeFundingControlTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIAdministrativeFundingControlTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[AdministrationEnvironmentType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AdministrationEnvironmentType](

	[AdministrationEnvironmentTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKAdministrationEnvironmentType] PRIMARY KEY NONCLUSTERED 

(

	[AdministrationEnvironmentTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIAdministrationEnvironmentTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[AssessmentCategoryType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AssessmentCategoryType](

	[AssessmentCategoryTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](60) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKAssessmentCategoryType] PRIMARY KEY NONCLUSTERED 

(

	[AssessmentCategoryTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIAssessmentCategoryTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[WeaponsType]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[WeaponsType](

	[WeaponsTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKWeaponsType] PRIMARY KEY NONCLUSTERED 

(

	[WeaponsTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIWeaponsTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[TitleIPartASchoolDesignationType]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[TitleIPartASchoolDesignationType](

	[TitleIPartASchoolDesignationTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](60) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKTitleIPartASchoolDesignationType] PRIMARY KEY NONCLUSTERED 

(

	[TitleIPartASchoolDesignationTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UITitleIPartASchoolDesignationTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[TitleIPartAParticipantType]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[TitleIPartAParticipantType](

	[TitleIPartAParticipantTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKTitleIPartAParticipantType] PRIMARY KEY NONCLUSTERED 

(

	[TitleIPartAParticipantTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UITitleIPartAParticipantTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[TermType]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[TermType](

	[TermTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKTermType] PRIMARY KEY NONCLUSTERED 

(

	[TermTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UITermTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[TelephoneNumberType]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[TelephoneNumberType](

	[TelephoneNumberTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](50) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PK_TelephoneNumberType] PRIMARY KEY CLUSTERED 

(

	[TelephoneNumberTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[TeachingCredentialType]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[TeachingCredentialType](

	[TeachingCredentialTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](30) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKTeachingCredentialTypeType] PRIMARY KEY NONCLUSTERED 

(

	[TeachingCredentialTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UITeachingCredentialTypeTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[TeachingCredentialBasisType]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[TeachingCredentialBasisType](

	[TeachingCredentialBasisTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKTeachingCredentialBasisType] PRIMARY KEY NONCLUSTERED 

(

	[TeachingCredentialBasisTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UITeachingCredentialBasisTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[StaffIdentificationSystemType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StaffIdentificationSystemType](

	[StaffIdentificationSystemTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](50) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PK_StaffIdentificationSystemType] PRIMARY KEY CLUSTERED 

(

	[StaffIdentificationSystemTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[StudentCharacteristicsType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentCharacteristicsType](

	[StudentCharacteristicsTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKStudentCharacteristicsType] PRIMARY KEY NONCLUSTERED 

(

	[StudentCharacteristicsTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIStudentCharacteristicsTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[StudentParticipationCodeType]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentParticipationCodeType](

	[StudentParticipationCodeTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKStudentParticipationCodeType] PRIMARY KEY NONCLUSTERED 

(

	[StudentParticipationCodeTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIStudentParticipationCodeTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[StudentIdentificationSystemType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentIdentificationSystemType](

	[StudentIdentificationSystemTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](50) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PK_StudentIdentificationSystemType] PRIMARY KEY CLUSTERED 

(

	[StudentIdentificationSystemTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[StateAbbreviationType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StateAbbreviationType](

	[StateAbbreviationTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKStateAbbreviationType] PRIMARY KEY NONCLUSTERED 

(

	[StateAbbreviationTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIStateAbbreviationTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[SpecialAccommodationsType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[SpecialAccommodationsType](

	[SpecialAccommodationsTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](25) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKSpecialAccommodationsType] PRIMARY KEY NONCLUSTERED 

(

	[SpecialAccommodationsTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UISpecialAccommodationsTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[SexType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[SexType](

	[SexTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKSexType] PRIMARY KEY NONCLUSTERED 

(

	[SexTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UISexTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[StaffClassificationType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StaffClassificationType](

	[StaffClassificationTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKStaffClassificationType] PRIMARY KEY NONCLUSTERED 

(

	[StaffClassificationTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIStaffClassificationTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[SchoolFoodServicesEligibilityType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[SchoolFoodServicesEligibilityType](

	[SchoolFoodServicesEligibilityTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](600) NOT NULL,

 CONSTRAINT [PKSchoolFoodServicesEligibilityType] PRIMARY KEY NONCLUSTERED 

(

	[SchoolFoodServicesEligibilityTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UISchoolFoodServicesEligibilityTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[SchoolCategoryType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[SchoolCategoryType](

	[SchoolCategoryTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](30) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKSchoolCategoryType] PRIMARY KEY NONCLUSTERED 

(

	[SchoolCategoryTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UISchoolCategoryTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[SchoolYearType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[SchoolYearType](

	[SchoolYear] [smallint] NOT NULL,

	[SchoolYearDescription] [nvarchar](50) NOT NULL,

	[CurrentSchoolYear] [bit] NOT NULL,

 CONSTRAINT [PK_SchoolYearType] PRIMARY KEY CLUSTERED 

(

	[SchoolYear] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[SchoolType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[SchoolType](

	[SchoolTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKSchoolTypeType] PRIMARY KEY NONCLUSTERED 

(

	[SchoolTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UISchoolTypeTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[RetestIndicatorType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[RetestIndicatorType](

	[RetestIndicatorTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKRetestIndicatorType] PRIMARY KEY NONCLUSTERED 

(

	[RetestIndicatorTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIRetestIndicatorTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[RestraintEventReasonsType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[RestraintEventReasonsType](

	[RestraintEventReasonsTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKRestraintEventReasonsType] PRIMARY KEY NONCLUSTERED 

(

	[RestraintEventReasonsTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIRestraintEventReasonsTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[PostSecondaryEventCategoryType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[PostSecondaryEventCategoryType](

	[PostSecondaryEventCategoryTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKPostSecondaryEventCategoryType] PRIMARY KEY NONCLUSTERED 

(

	[PostSecondaryEventCategoryTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIPostSecondaryEventCategoryTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[RepeatIdentifierType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[RepeatIdentifierType](

	[RepeatIdentifierTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKRepeatIdentifierType] PRIMARY KEY NONCLUSTERED 

(

	[RepeatIdentifierTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIRepeatIdentifierTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[RelationType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[RelationType](

	[RelationTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](35) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKRelationType] PRIMARY KEY NONCLUSTERED 

(

	[RelationTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIRelationTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[RecognitionType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[RecognitionType](

	[RecognitionTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PK_RecognitionType] PRIMARY KEY CLUSTERED 

(

	[RecognitionTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[ResponseIndicatorType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ResponseIndicatorType](

	[ResponseIndicatorTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKResponseIndicatorType] PRIMARY KEY NONCLUSTERED 

(

	[ResponseIndicatorTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIResponseIndicatorTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[ReporterDescriptionType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ReporterDescriptionType](

	[ReporterDescriptionTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKReporterDescriptionType] PRIMARY KEY NONCLUSTERED 

(

	[ReporterDescriptionTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIReporterDescriptionTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[ServiceDescriptor]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ServiceDescriptor](

	[ServiceDescriptorId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](1024) NULL,

	[ShortDescription] [nvarchar](50) NOT NULL,

	[ServiceCategory] [nvarchar](50) NULL,

 CONSTRAINT [PK_ServiceDescriptor] PRIMARY KEY CLUSTERED 

(

	[ServiceDescriptorId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[SeparationType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[SeparationType](

	[SeparationTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](25) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKSeparationType] PRIMARY KEY NONCLUSTERED 

(

	[SeparationTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UISeparationTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[SeparationReasonType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[SeparationReasonType](

	[SeparationReasonTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](50) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKSeparationReasonType] PRIMARY KEY NONCLUSTERED 

(

	[SeparationReasonTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UISeparationReasonTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[Section504DisabilityType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[Section504DisabilityType](

	[Section504DisabilityTypeId] [int] NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PK_Section504DisabilityType] PRIMARY KEY CLUSTERED 

(

	[Section504DisabilityTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[ReasonNotTestedType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ReasonNotTestedType](

	[ReasonNotTestedTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](40) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKReasonNotTestedType] PRIMARY KEY NONCLUSTERED 

(

	[ReasonNotTestedTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIReasonNotTestedTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[ReasonExitedType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ReasonExitedType](

	[ReasonExitedTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](50) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKReasonExitedType] PRIMARY KEY NONCLUSTERED 

(

	[ReasonExitedTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIReasonExitedTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[RaceType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[RaceType](

	[RaceTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](35) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKRaceType] PRIMARY KEY NONCLUSTERED 

(

	[RaceTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIRaceTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[ProgramType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ProgramType](

	[ProgramTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](50) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PK_ProgramType] PRIMARY KEY CLUSTERED 

(

	[ProgramTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[ProgramSponsorType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ProgramSponsorType](

	[ProgramSponsorTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](30) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PK_ProgramSponsorType] PRIMARY KEY CLUSTERED 

(

	[ProgramSponsorTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[ProgramAssignmentType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ProgramAssignmentType](

	[ProgramAssignmentTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKProgramAssignmentType] PRIMARY KEY NONCLUSTERED 

(

	[ProgramAssignmentTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIProgramAssignmentTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[PostingResultType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[PostingResultType](

	[PostingResultTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKPostingResultType] PRIMARY KEY NONCLUSTERED 

(

	[PostingResultTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIPostingResultTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[PopulationServedType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[PopulationServedType](

	[PopulationServedTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKPopulationServedType] PRIMARY KEY NONCLUSTERED 

(

	[PopulationServedTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIPopulationServedTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[PersonalTitlePrefixType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[PersonalTitlePrefixType](

	[PersonalTitlePrefixTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PK_PersonalTitlePrefixType] PRIMARY KEY CLUSTERED 

(

	[PersonalTitlePrefixTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[PersonalInformationVerificationType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[PersonalInformationVerificationType](

	[PersonalInformationVerificationTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](35) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKPersonalInformationVerificationType] PRIMARY KEY NONCLUSTERED 

(

	[PersonalInformationVerificationTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIPersonalInformationVerificationTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[OtherNameType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[OtherNameType](

	[OtherNameTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](50) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PK_OtherNameType] PRIMARY KEY CLUSTERED 

(

	[OtherNameTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[OperationalStatusType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[OperationalStatusType](

	[OperationalStatusTypeId] [int] NOT NULL,

	[CodeValue] [nvarchar](50) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PK_OperationalStatusType] PRIMARY KEY CLUSTERED 

(

	[OperationalStatusTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[OldEthnicityType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[OldEthnicityType](

	[OldEthnicityTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](35) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKOldEthnicityType] PRIMARY KEY NONCLUSTERED 

(

	[OldEthnicityTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIOldEthnicityTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[PerformanceBaseType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[PerformanceBaseType](

	[PerformanceBaseTypeId] [int] NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PK_PerformanceBaseType] PRIMARY KEY CLUSTERED 

(

	[PerformanceBaseTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[MethodCreditEarnedType]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[MethodCreditEarnedType](

	[MethodCreditEarnedTypeId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](50) NOT NULL,

	[Description] [nvarchar](200) NOT NULL,

 CONSTRAINT [PKMethodCreditEarnedType] PRIMARY KEY NONCLUSTERED 

(

	[MethodCreditEarnedTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],

 CONSTRAINT [UIMethodCreditEarnedTypeCodeValue] UNIQUE NONCLUSTERED 

(

	[CodeValue] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[Parent]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[Parent](

	[ParentUSI] [int] NOT NULL,

	[PersonalTitlePrefixTypeId] [int] NULL,

	[FirstName] [nvarchar](75) NOT NULL,

	[MiddleName] [nvarchar](75) NULL,

	[LastSurname] [nvarchar](75) NOT NULL,

	[GenerationCodeSuffixTypeId] [int] NULL,

	[MaidenName] [nvarchar](35) NULL,

	[PersonalInformationVerificationTypeId] [int] NULL,

	[SexTypeId] [int] NULL,

	[LoginId] [nvarchar](60) NULL,

 CONSTRAINT [PKParent] PRIMARY KEY NONCLUSTERED 

(

	[ParentUSI] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Parent Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Parent', @level2type=N'COLUMN',@level2name=N'ParentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A name given to an individual at birth, baptism, or during another naming ceremony, or through legal change.

NEDM: First Name

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Parent', @level2type=N'COLUMN',@level2name=N'FirstName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A secondary name given to an individual at birth, baptism, or during another naming ceremony.

NEDM: Middle Name

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Parent', @level2type=N'COLUMN',@level2name=N'MiddleName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name borne in common by members of a family.

NEDM: Last Name/Surname

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Parent', @level2type=N'COLUMN',@level2name=N'LastSurname'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An appendage, if any, used to denote an individual''s generation in his family (e.g., Jr., Sr., III).

NEDM: Generation Code / Suffix

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Parent', @level2type=N'COLUMN',@level2name=N'GenerationCodeSuffixTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The person''s maiden name, if applicable.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Parent', @level2type=N'COLUMN',@level2name=N'MaidenName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The evidence presented to verify one''s personal identity; for example: drivers license, passport, birth certificate, etc.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Parent', @level2type=N'COLUMN',@level2name=N'PersonalInformationVerificationTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A person''s gender.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Parent', @level2type=N'COLUMN',@level2name=N'SexTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This entity represents a parent or guardian of a student, such as mother, father, or caretaker.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Parent'

GO

/****** Object:  Table [edfi].[PerformanceLevelDescriptor]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[PerformanceLevelDescriptor](

	[PerformanceLevelDescriptorId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NULL,

	[Description] [nvarchar](1024) NOT NULL,

	[PerformanceBaseConversionTypeId] [int] NULL,

 CONSTRAINT [PK_PerformanceLevelDescriptor] PRIMARY KEY CLUSTERED 

(

	[PerformanceLevelDescriptorId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[Staff]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[Staff](

	[StaffUSI] [int] NOT NULL,

	[PersonalTitlePrefixTypeId] [int] NULL,

	[FirstName] [nvarchar](75) NOT NULL,

	[MiddleName] [nvarchar](75) NULL,

	[LastSurname] [nvarchar](75) NOT NULL,

	[GenerationCodeSuffixTypeId] [int] NULL,

	[MaidenName] [nvarchar](35) NULL,

	[PersonalInformationVerificationTypeId] [int] NULL,

	[SexTypeId] [int] NOT NULL,

	[BirthDate] [date] NULL,

	[HispanicLatinoEthnicity] [bit] NOT NULL,

	[OldEthnicityTypeId] [int] NULL,

	[HighestLevelOfEducationCompletedTypeId] [int] NOT NULL,

	[YearsOfPriorProfessionalExperience] [int] NULL,

	[YearsOfPriorTeachingExperience] [int] NULL,

	[HighlyQualifiedTeacher] [bit] NULL,

	[TeacherUSI] [int] NULL,

	[LoginId] [nvarchar](60) NULL,

 CONSTRAINT [PKStaff] PRIMARY KEY NONCLUSTERED 

(

	[StaffUSI] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Staff Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Staff', @level2type=N'COLUMN',@level2name=N'StaffUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A name given to an individual at birth, baptism, or during another naming ceremony, or through legal change.

NEDM: First Name

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Staff', @level2type=N'COLUMN',@level2name=N'FirstName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A secondary name given to an individual at birth, baptism, or during another naming ceremony.

NEDM: Middle Name

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Staff', @level2type=N'COLUMN',@level2name=N'MiddleName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name borne in common by members of a family.

NEDM: Last Name/Surname

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Staff', @level2type=N'COLUMN',@level2name=N'LastSurname'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An appendage, if any, used to denote an individual''s generation in his family (e.g., Jr., Sr., III).

NEDM: Generation Code / Suffix

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Staff', @level2type=N'COLUMN',@level2name=N'GenerationCodeSuffixTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The person''s maiden name, if applicable.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Staff', @level2type=N'COLUMN',@level2name=N'MaidenName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The evidence presented to verify one''s personal identity; for example: drivers license, passport, birth certificate, etc.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Staff', @level2type=N'COLUMN',@level2name=N'PersonalInformationVerificationTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A person''s gender.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Staff', @level2type=N'COLUMN',@level2name=N'SexTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year on which an individual was born.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Staff', @level2type=N'COLUMN',@level2name=N'BirthDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication that the individual traces his or her origin or descent to Mexico, Puerto Rico, Cuba, Central and South America, and other Spanish cultures, regardless of race. The term, "Spanish origin," can be used in addition to "Hispanic or Latino."

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Staff', @level2type=N'COLUMN',@level2name=N'HispanicLatinoEthnicity'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Previous definition of Ethnicity combining Hispanic/latino and race:

1 - American Indian or Alaskan Native

2 - Asian or Pacific Islander

3 - Black, not of Hispanic origin

4 - Hispanic

5 - White, not of Hispanic origin

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Staff', @level2type=N'COLUMN',@level2name=N'OldEthnicityTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The extent of formal instruction an individual has received (e.g., the highest grade in school completed or its equivalent or the highest degree received). 

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Staff', @level2type=N'COLUMN',@level2name=N'HighestLevelOfEducationCompletedTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The total number of years that an individual has previously held a similar professional position in one or more education institutions.

NEDM: Years of Prior Teaching Experience

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Staff', @level2type=N'COLUMN',@level2name=N'YearsOfPriorProfessionalExperience'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The total number of years that an individual has previously held a teaching position in one or more education institutions.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Staff', @level2type=N'COLUMN',@level2name=N'YearsOfPriorTeachingExperience'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of whether a teacher is classified as highly qualified for his/her assignment according to state definition.  This attribute indicates the teacher is highly qualified for ALL sections being taught.

Section 9101(23) of the ESEA defines the term &ldquo;highly qualified.&rdquo;

NEDM: HQT Qualification Status

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Staff', @level2type=N'COLUMN',@level2name=N'HighlyQualifiedTeacher'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An ID number maintained by the state that uniquely identifies this teacher.

TDCARSI Study: Recommendation for a statewide teacher UID

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Staff', @level2type=N'COLUMN',@level2name=N'TeacherUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This entity represents an individual who performs specified activities for any public or private education institution or agency that provides instructional and/or support services to students or staff at the early childhood level through high school completion. For example, this includes: 

1) an "employee" who performs services under the direction of the employing institution or agency, is compensated for such services by the employer, and is eligible for employee benefits and wage or salary tax withholdings; 

2) a "contractor" or "consultant" who performs services for an agreed upon fee, or an employee of a management service contracted to work on site; 

3) a "volunteer" who performs services on a voluntary and uncompensated basis; 

4) an in kind service provider; or 

5) an independent contractor or businessperson working at a school site.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Staff'

GO

/****** Object:  Table [edfi].[Student]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[Student](

	[StudentUSI] [int] NOT NULL,

	[PersonalTitlePrefixTypeId] [int] NULL,

	[FirstName] [nvarchar](75) NOT NULL,

	[MiddleName] [nvarchar](75) NULL,

	[LastSurname] [nvarchar](75) NOT NULL,

	[GenerationCodeSuffixTypeId] [int] NULL,

	[MaidenName] [nvarchar](35) NULL,

	[PersonalInformationVerificationTypeId] [int] NULL,

	[SexTypeId] [int] NOT NULL,

	[BirthDate] [date] NOT NULL,

	[CityOfBirth] [nvarchar](30) NULL,

	[StateOfBirthAbbreviationTypeId] [int] NULL,

	[CountryOfBirthCodeTypeId] [int] NULL,

	[DateEnteredUS] [date] NULL,

	[MultipleBirthStatus] [bit] NULL,

	[ProfileThumbnail] [nvarchar](59) NULL,

	[HispanicLatinoEthnicity] [bit] NOT NULL,

	[OldEthnicityTypeId] [int] NULL,

	[EconomicDisadvantaged] [bit] NULL,

	[SchoolFoodServicesEligibilityTypeId] [int] NULL,

	[LimitedEnglishProficiencyTypeId] [int] NULL,

	[DisplacementStatusType] [nvarchar](30) NULL,

	[LoginId] [nvarchar](60) NULL,

 CONSTRAINT [PKStudent] PRIMARY KEY NONCLUSTERED 

(

	[StudentUSI] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Student', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A name given to an individual at birth, baptism, or during another naming ceremony, or through legal change.

NEDM: First Name

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Student', @level2type=N'COLUMN',@level2name=N'FirstName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A secondary name given to an individual at birth, baptism, or during another naming ceremony.

NEDM: Middle Name

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Student', @level2type=N'COLUMN',@level2name=N'MiddleName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name borne in common by members of a family.

NEDM: Last Name/Surname

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Student', @level2type=N'COLUMN',@level2name=N'LastSurname'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An appendage, if any, used to denote an individual''s generation in his family (e.g., Jr., Sr., III).

NEDM: Generation Code / Suffix

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Student', @level2type=N'COLUMN',@level2name=N'GenerationCodeSuffixTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The person''s maiden name, if applicable.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Student', @level2type=N'COLUMN',@level2name=N'MaidenName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The evidence presented to verify one''s personal identity; for example: drivers license, passport, birth certificate, etc.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Student', @level2type=N'COLUMN',@level2name=N'PersonalInformationVerificationTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A person''s gender.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Student', @level2type=N'COLUMN',@level2name=N'SexTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year on which an individual was born.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Student', @level2type=N'COLUMN',@level2name=N'BirthDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Student', @level2type=N'COLUMN',@level2name=N'CityOfBirth'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The abbreviation for the name of the state (within the United States) or extra-state jurisdiction in which an individual was born.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Student', @level2type=N'COLUMN',@level2name=N'StateOfBirthAbbreviationTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the country in which an individual was born. (Note: A list of countries and codes is currently maintained and updated by the ISO as ISO 3166 on its website: http://www.iso.org/iso/country_codes.htm)

NEDM: Country Of Birth Code

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Student', @level2type=N'COLUMN',@level2name=N'CountryOfBirthCodeTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Student', @level2type=N'COLUMN',@level2name=N'MultipleBirthStatus'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'File name for the ProfileThumbnail photograph

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Student', @level2type=N'COLUMN',@level2name=N'ProfileThumbnail'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication that the individual traces his or her origin or descent to Mexico, Puerto Rico, Cuba, Central and South America, and other Spanish cultures, regardless of race. The term, "Spanish origin," can be used in addition to "Hispanic or Latino."

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Student', @level2type=N'COLUMN',@level2name=N'HispanicLatinoEthnicity'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Previous definition of Ethnicity combining Hispanic/latino and race:

1 - American Indian or Alaskan Native

2 - Asian or Pacific Islander

3 - Black, not of Hispanic origin

4 - Hispanic

5 - White, not of Hispanic origin

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Student', @level2type=N'COLUMN',@level2name=N'OldEthnicityTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of inadequate financial condition of an individual''s family, as determined by family income, number of family members/dependents, participation in public assistance programs, and/or other characteristics considered relevant by federal, state, and local policy.

NEDM: Economic Disadvantaged Status

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Student', @level2type=N'COLUMN',@level2name=N'EconomicDisadvantaged'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of a student''s level of eligibility for breakfast, lunch, snack, supper, and milk programs.

NEDM: Eligibility Status for School Food Service Programs

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Student', @level2type=N'COLUMN',@level2name=N'SchoolFoodServicesEligibilityTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication that the student has sufficient difficulty speaking, reading, writing, or understanding the English language, as to require special English Language services.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Student', @level2type=N'COLUMN',@level2name=N'LimitedEnglishProficiencyTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Indicates a state health or weather related event that displaces a group of students, and may require additional funding, educational, or social services.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Student', @level2type=N'COLUMN',@level2name=N'DisplacementStatusType'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This entity represents an individual for whom instruction, services and/or care are provided in an early childhood, elementary or secondary educational program under the jurisdiction of a school, education agency, or other institution or program.  A student is a person who has been enrolled in a campus or other educational institution.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Student'

GO

/****** Object:  Table [edfi].[Assessment]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[Assessment](

	[AssessmentTitle] [nvarchar](60) NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

	[AssessedGradeLevelTypeId] [int] NOT NULL,

	[Version] [int] NOT NULL,

	[AssessmentCategoryTypeId] [int] NOT NULL,

	[LowestAssessedGradeLevelTypeId] [int] NULL,

	[ContentStandardTypeId] [int] NULL,

	[AssessmentForm] [nvarchar](60) NULL,

	[RevisionDate] [date] NULL,

	[MaxRawScore] [int] NULL,

	[Nomenclature] [nvarchar](35) NULL,

	[AssessmentPeriodDescriptorId] [int] NULL,

 CONSTRAINT [PK_Assessment] PRIMARY KEY CLUSTERED 

(

	[AssessmentTitle] ASC,

	[AcademicSubjectTypeId] ASC,

	[AssessedGradeLevelTypeId] ASC,

	[Version] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The title or name of the assessment.

NEDM: Assessment Title

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Assessment', @level2type=N'COLUMN',@level2name=N'AssessmentTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

NEDM: Assessment Content, Academic Subject

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Assessment', @level2type=N'COLUMN',@level2name=N'AcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The typical grade level for which an assessment is designed. If the test assessment spans a range of grades, then this attribute holds the highest grade assessed.  If only one grade level is assessed, then only this attribute is used. For example:

Adult

Prekindergarten

First grade

Second grade

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Assessment', @level2type=N'COLUMN',@level2name=N'AssessedGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The version identifier for the test assessment.

NEDM: Assessment Version

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Assessment', @level2type=N'COLUMN',@level2name=N'Version'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The category of an assessment based on format and content. For example:

Achievement test

ACT

Advanced placement test

Alternate assessment/grade-level standards

Attitudinal test

Benchmark test

Cognitive and perceptual skills test

SAT

TAKS



...

NEDM: Assessment Type

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Assessment', @level2type=N'COLUMN',@level2name=N'AssessmentCategoryTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'If the test assessment spans a range of grades, then this attribute holds the lowest grade assessed.  If only one grade level is assessed, then this attribute is omitted. For example:

Adult

Prekindergarten

First grade

Second grade

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Assessment', @level2type=N'COLUMN',@level2name=N'LowestAssessedGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication as to whether an assessment conforms to a standard. For example:

Local standard

Statewide standard

Regional standard

Association standard

School standard

...

NEDM: Assessment Content Standard

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Assessment', @level2type=N'COLUMN',@level2name=N'ContentStandardTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year that the conceptual design for the assessment was most recently revised substantially.

NEDM: Date Of Assessment Revision

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Assessment', @level2type=N'COLUMN',@level2name=N'RevisionDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The maximum raw score achievable across all assessment items that are correct and scored at the maximum.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Assessment', @level2type=N'COLUMN',@level2name=N'MaxRawScore'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A tool, instrument, process, or exhibition composed of a systematic sampling of behavior for measuring a student''s competence, knowledge, skills or behavior. An assessment can be used to measure differences in individuals or groups and changes in performance from one occasion to the next.

NEDM: Test Assessment

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Assessment'

GO

/****** Object:  Table [edfi].[AssessmentFamily]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AssessmentFamily](

	[AssessmentFamilyTitle] [nvarchar](60) NOT NULL,

	[AcademicSubjectTypeId] [int] NULL,

	[AssessedGradeLevelTypeId] [int] NULL,

	[Version] [int] NULL,

	[AssessmentCategoryTypeId] [int] NULL,

	[LowestAssessedGradeLevelTypeId] [int] NULL,

	[ContentStandardTypeId] [int] NULL,

	[RevisionDate] [date] NULL,

	[Nomenclature] [nvarchar](35) NULL,

	[ParentAssessmentFamilyTitle] [nvarchar](60) NULL,

 CONSTRAINT [PK_AssessmentFamily] PRIMARY KEY CLUSTERED 

(

	[AssessmentFamilyTitle] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[CompetencyLevelDescriptor]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CompetencyLevelDescriptor](

	[CompetencyLevelDescriptorId] [int] IDENTITY(1,1) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](1024) NULL,

	[PerformanceBaseConversionTypeId] [int] NULL,

 CONSTRAINT [PK_CompetencyLevelDescriptor] PRIMARY KEY CLUSTERED 

(

	[CompetencyLevelDescriptorId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[BehaviorDescriptor]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[BehaviorDescriptor](

	[BehaviorDescriptorId] [int] IDENTITY(1,1) NOT NULL,

	[EducationOrganizationId] [int] NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](1024) NULL,

	[ShortDescription] [nvarchar](50) NOT NULL,

	[BehaviorCategoryTypeId] [int] NOT NULL,

 CONSTRAINT [PK_BehaviorDescriptor] PRIMARY KEY CLUSTERED 

(

	[BehaviorDescriptorId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[EducationOrganization]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[EducationOrganization](

	[EducationOrganizationId] [int] NOT NULL,

	[StateOrganizationId] [nvarchar](60) NOT NULL,

	[NameOfInstitution] [nvarchar](75) NOT NULL,

	[ShortNameOfInstitution] [nvarchar](60) NULL,

	[WebSite] [nvarchar](80) NULL,

	[OperationalStatusTypeId] [int] NULL,

	[Nomenclature] [nvarchar](35) NULL,

 CONSTRAINT [PKEducationOrganization] PRIMARY KEY NONCLUSTERED 

(

	[EducationOrganizationId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'EducationOrganization', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The identifier assigned to an education agency by the State Education Agency (SEA).  Also known as the State LEP ID.

NEDM: IdentificationCode, LEA Identifier (State)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'EducationOrganization', @level2type=N'COLUMN',@level2name=N'StateOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The full, legally accepted name of the institution.

NEDM: Name of Institution

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'EducationOrganization', @level2type=N'COLUMN',@level2name=N'NameOfInstitution'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A short name for the institution.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'EducationOrganization', @level2type=N'COLUMN',@level2name=N'ShortNameOfInstitution'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The public web site address (URL) for the educational organization.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'EducationOrganization', @level2type=N'COLUMN',@level2name=N'WebSite'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This entity represents any public or private institution, organization, or agency that provides instructional or support services to students or staff at any level. 

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'EducationOrganization'

GO

/****** Object:  Table [edfi].[GradingPeriod]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[GradingPeriod](

	[EducationOrganizationId] [int] NOT NULL,

	[GradingPeriodTypeId] [int] NOT NULL,

	[BeginDate] [date] NOT NULL,

	[EndDate] [date] NOT NULL,

	[TotalInstructionalDays] [int] NOT NULL,

 CONSTRAINT [PK_GradingPeriod] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[GradingPeriodTypeId] ASC,

	[BeginDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradingPeriod', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the grading period during the school year in which the grade is offered (e.g., 1st cycle, 1st semester)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradingPeriod', @level2type=N'COLUMN',@level2name=N'GradingPeriodTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Month, day, and year of the first day of the grading period.



' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradingPeriod', @level2type=N'COLUMN',@level2name=N'BeginDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Month, day, and year of the last day of the grading period.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradingPeriod', @level2type=N'COLUMN',@level2name=N'EndDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Total days available for educational instruction during the grading period.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradingPeriod', @level2type=N'COLUMN',@level2name=N'TotalInstructionalDays'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This entity represents the time span for which grades are reported.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradingPeriod'

GO

/****** Object:  Table [edfi].[MeetingTime]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[MeetingTime](

	[SchoolId] [int] NOT NULL,

	[ClassPeriodName] [nvarchar](20) NOT NULL,

	[WeekNumber] [int] NOT NULL,

	[AlternateDayName] [nvarchar](20) NULL,

	[StartTime] [time](7) NOT NULL,

	[EndTime] [time](7) NOT NULL,

	[OfficialAttendancePeriod] [bit] NULL,

 CONSTRAINT [PK_MeetingTime] PRIMARY KEY CLUSTERED 

(

	[SchoolId] ASC,

	[ClassPeriodName] ASC,

	[WeekNumber] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the portion of a typical daily session in which students receive instruction in a specified subject (e.g., morning, sixth period, block period, or AB schedules). 

NEDM: Class Period

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'MeetingTime', @level2type=N'COLUMN',@level2name=N'ClassPeriodName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The week number (out of the number of weeks in the cycle) for this meeting time.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'MeetingTime', @level2type=N'COLUMN',@level2name=N'WeekNumber'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An alternate name for the day (e.g., Red, Blue)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'MeetingTime', @level2type=N'COLUMN',@level2name=N'AlternateDayName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the time of day the class begins

NEDM: Class Beginning Time

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'MeetingTime', @level2type=N'COLUMN',@level2name=N'StartTime'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the time of day the class ends.

NEDM: Class Ending Time

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'MeetingTime', @level2type=N'COLUMN',@level2name=N'EndTime'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Indicator of whether this meeting time is used for official daily attendance.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'MeetingTime', @level2type=N'COLUMN',@level2name=N'OfficialAttendancePeriod'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Record for a meeting time for a class period.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'MeetingTime'

GO

/****** Object:  Table [edfi].[LearningStandard]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[LearningStandard](

	[LearningStandardId] [nvarchar](40) NOT NULL,

	[Description] [nvarchar](1024) NOT NULL,

	[ContentStandardTypeId] [int] NULL,

	[GradeLevelTypeId] [int] NOT NULL,

	[SubjectAreaTypeId] [int] NOT NULL,

	[CourseTitle] [nvarchar](60) NULL,

 CONSTRAINT [PK_LearningStandard] PRIMARY KEY CLUSTERED 

(

	[LearningStandardId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[LeaveEvent]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[LeaveEvent](

	[StaffUSI] [int] NOT NULL,

	[EventDate] [date] NOT NULL,

	[LeaveEventCategoryTypeId] [int] NOT NULL,

	[LeaveEventReason] [nvarchar](40) NULL,

	[HoursOnLeave] [decimal](18, 2) NULL,

	[SubstituteAssigned] [bit] NULL,

 CONSTRAINT [PK_LeaveEvent] PRIMARY KEY CLUSTERED 

(

	[StaffUSI] ASC,

	[EventDate] ASC,

	[LeaveEventCategoryTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Staff Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'LeaveEvent', @level2type=N'COLUMN',@level2name=N'StaffUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Date for this leave event.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'LeaveEvent', @level2type=N'COLUMN',@level2name=N'EventDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The code describing the type of leave taken, for example:

Sick

Personal

Vacation

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'LeaveEvent', @level2type=N'COLUMN',@level2name=N'LeaveEventCategoryTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Expanded reason for the staff leave.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'LeaveEvent', @level2type=N'COLUMN',@level2name=N'LeaveEventReason'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The hours the staff was absent, if not the entire working day.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'LeaveEvent', @level2type=N'COLUMN',@level2name=N'HoursOnLeave'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Indicator of whether a substitute was assigned during the period of staff leave.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'LeaveEvent', @level2type=N'COLUMN',@level2name=N'SubstituteAssigned'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This event entity represents the recording of the dates of staff leave (e.g., sick leave, personal time, vacation, etc.).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'LeaveEvent'

GO

/****** Object:  Table [edfi].[LearningStandardIdentificationCode]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[LearningStandardIdentificationCode](

	[LearningStandardId] [nvarchar](40) NOT NULL,

	[LearningStandardIdentificationCode] [nvarchar](60) NOT NULL,

	[ContentStandardName] [nvarchar](65) NULL,

 CONSTRAINT [PK_LearningStandardIdentificationCode] PRIMARY KEY CLUSTERED 

(

	[LearningStandardId] ASC,

	[LearningStandardIdentificationCode] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[LearningObjective]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[LearningObjective](

	[Objective] [nvarchar](60) NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

	[ObjectiveGradeLevelTypeId] [int] NOT NULL,

	[Description] [nvarchar](1024) NULL,

	[LearningStandardId] [nvarchar](40) NULL,

	[ParentObjective] [nvarchar](60) NULL,

	[ParentAcademicSubjectTypeId] [int] NULL,

	[ParentObjectiveGradeLevelTypeId] [int] NULL,

 CONSTRAINT [PK_LearningObjective] PRIMARY KEY CLUSTERED 

(

	[Objective] ASC,

	[AcademicSubjectTypeId] ASC,

	[ObjectiveGradeLevelTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The designated title of the learning objective.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'LearningObjective', @level2type=N'COLUMN',@level2name=N'Objective'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'LearningObjective', @level2type=N'COLUMN',@level2name=N'AcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The grade level for which the learning objective is targeted,

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'LearningObjective', @level2type=N'COLUMN',@level2name=N'ObjectiveGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the learning objective.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'LearningObjective', @level2type=N'COLUMN',@level2name=N'Description'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The designated title of the learning objective.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'LearningObjective', @level2type=N'COLUMN',@level2name=N'ParentObjective'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'LearningObjective', @level2type=N'COLUMN',@level2name=N'ParentAcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The grade level for which the learning objective is targeted,

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'LearningObjective', @level2type=N'COLUMN',@level2name=N'ParentObjectiveGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This entity represents identified learning objectives that are tested.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'LearningObjective'

GO

/****** Object:  Table [edfi].[GraduationPlan]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[GraduationPlan](

	[GraduationPlanTypeId] [int] NOT NULL,

	[EducationOrganizationId] [int] NOT NULL,

	[IndividualPlan] [bit] NULL,

	[TotalCreditsRequired] [decimal](9, 2) NOT NULL,

	[TotalCreditsRequiredCreditTypeId] [int] NULL,

	[TotalCreditsRequiredCreditConversion] [decimal](9, 2) NULL,

 CONSTRAINT [PK_GraduationPlan] PRIMARY KEY CLUSTERED 

(

	[GraduationPlanTypeId] ASC,

	[EducationOrganizationId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GraduationPlan', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

/****** Object:  Table [edfi].[EducationOrgIdentificationCode]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[EducationOrgIdentificationCode](

	[EducationOrganizationId] [int] NOT NULL,

	[EducationOrgIdentificationSystemTypeId] [int] NOT NULL,

	[EducationOrgIdentificationCode] [nvarchar](60) NOT NULL,

 CONSTRAINT [PK_EducationOrgIdentificationCode] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[EducationOrgIdentificationSystemTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[EducationOrganizationTelephone]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[EducationOrganizationTelephone](

	[EducationOrganizationId] [int] NOT NULL,

	[InstitutionTelephoneNumberTypeId] [int] NOT NULL,

	[TelephoneNumber] [nvarchar](24) NOT NULL,

 CONSTRAINT [PK_EducationOrganizationTelephone] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[InstitutionTelephoneNumberTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[EducationOrganizationPeer]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[EducationOrganizationPeer](

	[EducationOrganizationId] [int] NOT NULL,

	[PeerEducationOrganizationId] [int] NOT NULL,

 CONSTRAINT [PK_EducationOrganizationPeerReference] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[PeerEducationOrganizationId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[EducationOrganizationCategory]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[EducationOrganizationCategory](

	[EducationOrganizationId] [int] NOT NULL,

	[EducationOrganizationCategoryTypeId] [int] NOT NULL,

 CONSTRAINT [PK_EducationOrganizationCategory] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[EducationOrganizationCategoryTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The classification of the education agency within the geographic boundaries of a state according to the level of administrative and operational control granted by the state.

NEDM: Agency Type

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'EducationOrganizationCategory', @level2type=N'COLUMN',@level2name=N'EducationOrganizationCategoryTypeId'

GO

/****** Object:  Table [edfi].[EducationOrganizationAddress]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[EducationOrganizationAddress](

	[EducationOrganizationId] [int] NOT NULL,

	[AddressTypeId] [int] NOT NULL,

	[StreetNumberName] [nvarchar](150) NOT NULL,

	[ApartmentRoomSuiteNumber] [nvarchar](50) NULL,

	[BuildingSiteNumber] [nvarchar](20) NULL,

	[City] [nvarchar](30) NOT NULL,

	[StateAbbreviationTypeId] [int] NOT NULL,

	[PostalCode] [nvarchar](17) NOT NULL,

	[NameOfCounty] [nvarchar](30) NULL,

	[CountyFIPSCode] [nvarchar](5) NULL,

	[CountryCodeTypeId] [int] NULL,

	[Latitude] [nvarchar](20) NULL,

	[Longitude] [nvarchar](20) NULL,

	[BeginDate] [date] NULL,

	[EndDate] [date] NULL,

 CONSTRAINT [PK_EducationOrganizationAddress] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[AddressTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Address Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'EducationOrganizationAddress', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The street number and street name or post office box number of an address.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'EducationOrganizationAddress', @level2type=N'COLUMN',@level2name=N'StreetNumberName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The apartment, room, or suite number of an address. 

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'EducationOrganizationAddress', @level2type=N'COLUMN',@level2name=N'ApartmentRoomSuiteNumber'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of the building on the site, if more than one building shares the same address.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'EducationOrganizationAddress', @level2type=N'COLUMN',@level2name=N'BuildingSiteNumber'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the city in which an address is located. 

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'EducationOrganizationAddress', @level2type=N'COLUMN',@level2name=N'City'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The abbreviation for the state (within the United States) or outlying area in which an address is located. 

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'EducationOrganizationAddress', @level2type=N'COLUMN',@level2name=N'StateAbbreviationTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The five or nine digit zip code portion of an address.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'EducationOrganizationAddress', @level2type=N'COLUMN',@level2name=N'PostalCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the county, parish, borough, or comparable unit (within a state) in which an address is located.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'EducationOrganizationAddress', @level2type=N'COLUMN',@level2name=N'NameOfCounty'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'5 digit code consisting of the two digit state code followed by the three digit FIPS code for the county.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'EducationOrganizationAddress', @level2type=N'COLUMN',@level2name=N'CountyFIPSCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The unique two character International Organization for Standardization (ISO) code for the country in which an address is located.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'EducationOrganizationAddress', @level2type=N'COLUMN',@level2name=N'CountryCodeTypeId'

GO

/****** Object:  Table [edfi].[AccountCodeDescriptor]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AccountCodeDescriptor](

	[AccountCodeDescriptorId] [int] IDENTITY(1,1) NOT NULL,

	[EducationOrganizationId] [int] NOT NULL,

	[AccountCodeType] [nvarchar](20) NOT NULL,

	[CodeValue] [nvarchar](20) NOT NULL,

	[Description] [nvarchar](1024) NOT NULL,

	[ShortDescription] [nvarchar](50) NULL,

	[BeginDate] [date] NULL,

	[EndDate] [date] NULL,

 CONSTRAINT [PK_AccountCodeDescriptor] PRIMARY KEY CLUSTERED 

(

	[AccountCodeDescriptorId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AccountCodeDescriptor', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

/****** Object:  Table [edfi].[Cohort]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[Cohort](

	[EducationOrganizationId] [int] NOT NULL,

	[CohortIdentifier] [nvarchar](20) NOT NULL,

	[CohortDescription] [nvarchar](1024) NULL,

	[CohortTypeId] [int] NOT NULL,

	[CohortScopeTypeId] [int] NULL,

	[AcademicSubjectTypeId] [int] NULL,

 CONSTRAINT [PK_Cohort] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[CohortIdentifier] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Cohort', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name or ID for the cohort.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Cohort', @level2type=N'COLUMN',@level2name=N'CohortIdentifier'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of he cohort and its purpose.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Cohort', @level2type=N'COLUMN',@level2name=N'CohortDescription'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The type of the cohort (academic intervention, attendance intervention, discipline intervention, breakout session, etc.)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Cohort', @level2type=N'COLUMN',@level2name=N'CohortTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The scope of cohort (e.g., campus, district, classroom, etc.)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Cohort', @level2type=N'COLUMN',@level2name=N'CohortScopeTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The subject for an academic intervention (e.g., science, mathematics, etc.)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Cohort', @level2type=N'COLUMN',@level2name=N'AcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Identification of a named and managed cohort of students for tracking or intervention purposes.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Cohort'

GO

/****** Object:  Table [edfi].[Course]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[Course](

	[EducationOrganizationId] [int] NOT NULL,

	[IdentityCourseCode] [nvarchar](20) NOT NULL,

	[CourseTitle] [nvarchar](60) NOT NULL,

	[NumberOfParts] [int] NOT NULL,

	[CourseLevelTypeId] [int] NULL,

	[SubjectAreaTypeId] [int] NULL,

	[CourseDescription] [nvarchar](20) NULL,

	[DateCourseAdopted] [date] NULL,

	[HighSchoolCourseRequirement] [bit] NULL,

	[CourseGPAApplicabilityTypeId] [int] NULL,

	[CourseDefinedByTypeId] [int] NULL,

	[MinimumAvailableCreditTypeId] [int] NULL,

	[MinimumAvailableCredit] [decimal](9, 2) NULL,

	[MaximumAvailableCreditTypeId] [int] NULL,

	[MaximumAvailableCredit] [decimal](9, 2) NULL,

	[CareerPathwayTypeId] [int] NULL,

	[CompetencyLevelDescriptorId] [int] NULL,

 CONSTRAINT [PK_Course] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[IdentityCourseCode] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'TThe actual code that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Course', @level2type=N'COLUMN',@level2name=N'IdentityCourseCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The descriptive name given to a course of study offered in a school or other institution or organization. In departmentalized classes at the elementary, secondary, and postsecondary levels (and for staff development activities), this refers to the name by which a course is identified (e.g., American History, English III). For elementary and other non-departmentalized classes, it refers to any portion of the instruction for which a grade or report is assigned (e.g., reading, composition, spelling, and language arts).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Course', @level2type=N'COLUMN',@level2name=N'CourseTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of parts identified for a course.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Course', @level2type=N'COLUMN',@level2name=N'NumberOfParts'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The course''s level of rigor

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Course', @level2type=N'COLUMN',@level2name=N'CourseLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The intended major subject area of the course.

NEDM: Secondary Course Subject Area

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Course', @level2type=N'COLUMN',@level2name=N'SubjectAreaTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A description of the content standards and goals covered in the course. Reference may be made to state or national content standards.

NEDM: Course Description

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Course', @level2type=N'COLUMN',@level2name=N'CourseDescription'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The date the district adopted the course.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Course', @level2type=N'COLUMN',@level2name=N'DateCourseAdopted'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication that this course credit is required for a high school diploma.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Course', @level2type=N'COLUMN',@level2name=N'HighSchoolCourseRequirement'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indicator of whether or not this course being described is included in the computation of the student&rsquo;s Grade Point Average, and if so, if it weighted differently from regular courses.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Course', @level2type=N'COLUMN',@level2name=N'CourseGPAApplicabilityTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This educational entity represents the organization of subject matter and related learning experiences provided for the instruction of students on a regular or systematic basis. 

NEDM: Course' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Course'

GO

/****** Object:  Table [edfi].[Disability]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[Disability](

	[StudentUSI] [int] NOT NULL,

	[DisabilityTypeId] [int] NOT NULL,

	[DisabilityDiagnosis] [nvarchar](80) NULL,

	[OrderOfDisability] [int] NULL,

 CONSTRAINT [PK_Disability] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[DisabilityTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A disability category that describes a child''s impairment.

NEDM: Primary Disability Type, Secondary Disability Type

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Disability', @level2type=N'COLUMN',@level2name=N'DisabilityTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A description of the exact disability diagnosis.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Disability', @level2type=N'COLUMN',@level2name=N'DisabilityDiagnosis'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Primary, Secondary, Tertiary, etc.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Disability', @level2type=N'COLUMN',@level2name=N'OrderOfDisability'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This type represents an impairment of body structure or function, a limitation in activities, or a restriction in participation, as ordered by severity of impairment.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Disability'

GO

/****** Object:  Table [edfi].[Credential]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[Credential](

	[StaffUSI] [int] NOT NULL,

	[CredentialTypeId] [int] NOT NULL,

	[CredentialFieldDescriptorId] [int] NOT NULL,

	[LevelTypeId] [int] NOT NULL,

	[TeachingCredentialTypeId] [int] NOT NULL,

	[CredentialIssuanceDate] [date] NOT NULL,

	[CredentialExpirationDate] [date] NULL,

	[TeachingCredentialBasisTypeId] [int] NULL,

 CONSTRAINT [PK_Credential] PRIMARY KEY CLUSTERED 

(

	[StaffUSI] ASC,

	[CredentialTypeId] ASC, 

	[CredentialFieldDescriptorId] ASC,

	[LevelTypeId] ASC,

	[CredentialIssuanceDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the category of credential an individual holds.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Credential', @level2type=N'COLUMN',@level2name=N'CredentialTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The grade level(s) certified for teaching.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Credential', @level2type=N'COLUMN',@level2name=N'LevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the category of a legal document giving authorization to perform teaching assignment services.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Credential', @level2type=N'COLUMN',@level2name=N'TeachingCredentialTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year on which an active credential was issued to an individual.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Credential', @level2type=N'COLUMN',@level2name=N'CredentialIssuanceDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year on which an active credential held by an individual will expire.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Credential', @level2type=N'COLUMN',@level2name=N'CredentialExpirationDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the pre-determined criteria for granting the teaching credential that an individual holds.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Credential', @level2type=N'COLUMN',@level2name=N'TeachingCredentialBasisTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The legal document or authorization giving authorization to perform teaching assignment services.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Credential'

GO

/****** Object:  Table [edfi].[AssessmentPerformanceLevel]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AssessmentPerformanceLevel](

	[AssessmentTitle] [nvarchar](60) NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

	[AssessedGradeLevelTypeId] [int] NOT NULL,

	[Version] [int] NOT NULL,

	[PerformanceLevelDescriptorId] [int] NOT NULL,

	[AssessmentReportingMethodTypeId] [int] NOT NULL,

	[MinimumScore] [int] NULL,

	[MaximumScore] [int] NULL,

 CONSTRAINT [PK_AssessmentPerformanceLevel] PRIMARY KEY CLUSTERED 

(

	[AssessmentTitle] ASC,

	[AcademicSubjectTypeId] ASC,

	[AssessedGradeLevelTypeId] ASC,

	[Version] ASC,

	[PerformanceLevelDescriptorId] ASC,

	[AssessmentReportingMethodTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The title or name of the assessment.

NEDM: Assessment Title

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentPerformanceLevel', @level2type=N'COLUMN',@level2name=N'AssessmentTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

NEDM: Assessment Content, Academic Subject

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentPerformanceLevel', @level2type=N'COLUMN',@level2name=N'AcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The typical grade level for which an assessment is designed. If the test assessment spans a range of grades, then this attribute holds the highest grade assessed.  If only one grade level is assessed, then only this attribute is used. For example:

Adult

Prekindergarten

First grade

Second grade

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentPerformanceLevel', @level2type=N'COLUMN',@level2name=N'AssessedGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The version identifier for the test assessment.

NEDM: Assessment Version

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentPerformanceLevel', @level2type=N'COLUMN',@level2name=N'Version'

GO

/****** Object:  Table [edfi].[AssessmentItem]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AssessmentItem](

	[AssesmentItem] [int] NOT NULL,

	[AssessmentTitle] [nvarchar](60) NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

	[AssessedGradeLevelTypeId] [int] NOT NULL,

	[Version] [int] NOT NULL,

	[IdentificationCode] [nvarchar](20) NOT NULL,

	[ItemCategoryTypeId] [int] NOT NULL,

	[MaxRawScore] [int] NOT NULL,

	[CorrectResponse] [nvarchar](20) NOT NULL,

	[LearningStandardId] [nvarchar](40) NULL,

	[Nomenclature] [nvarchar](35) NULL,

 CONSTRAINT [PK_AssessmentItem] PRIMARY KEY CLUSTERED 

(

	[AssesmentItem] ASC,

	[AssessmentTitle] ASC,

	[AcademicSubjectTypeId] ASC,

	[AssessedGradeLevelTypeId] ASC,

	[Version] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The title or name of the assessment.

NEDM: Assessment Title

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentItem', @level2type=N'COLUMN',@level2name=N'AssessmentTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

NEDM: Assessment Content, Academic Subject

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentItem', @level2type=N'COLUMN',@level2name=N'AcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The typical grade level for which an assessment is designed. If the test assessment spans a range of grades, then this attribute holds the highest grade assessed.  If only one grade level is assessed, then only this attribute is used. For example:

Adult

Prekindergarten

First grade

Second grade

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentItem', @level2type=N'COLUMN',@level2name=N'AssessedGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The version identifier for the test assessment.

NEDM: Assessment Version

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentItem', @level2type=N'COLUMN',@level2name=N'Version'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Category or type of the assessment item.  For example:

Multiple choice

Analytic

Prose

....



' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentItem', @level2type=N'COLUMN',@level2name=N'ItemCategoryTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The maximum raw score achievable is all assessment items are correct and scored at the maximum.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentItem', @level2type=N'COLUMN',@level2name=N'MaxRawScore'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The correct response for the assessment item.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentItem', @level2type=N'COLUMN',@level2name=N'CorrectResponse'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'One of many single measures that make up an assessment. 

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentItem'

GO

/****** Object:  Table [edfi].[AssessmentIdentificationCode]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AssessmentIdentificationCode](

	[AssessmentTitle] [nvarchar](60) NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

	[AssessedGradeLevelTypeId] [int] NOT NULL,

	[Version] [int] NOT NULL,

	[AssessmentIdentificationSystemTypeId] [int] NOT NULL,

	[AssigningOrganizationCode] [nvarchar](60) NULL,

	[IdentificationCode] [nvarchar](60) NOT NULL,

 CONSTRAINT [PK_AssessmentIdentificationCode] PRIMARY KEY CLUSTERED 

(

	[AssessmentTitle] ASC,

	[AcademicSubjectTypeId] ASC,

	[AssessedGradeLevelTypeId] ASC,

	[Version] ASC,

	[AssessmentIdentificationSystemTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The title or name of the assessment.

NEDM: Assessment Title

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentIdentificationCode', @level2type=N'COLUMN',@level2name=N'AssessmentTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

NEDM: Assessment Content, Academic Subject

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentIdentificationCode', @level2type=N'COLUMN',@level2name=N'AcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The typical grade level for which an assessment is designed. If the test assessment spans a range of grades, then this attribute holds the highest grade assessed.  If only one grade level is assessed, then only this attribute is used. For example:

Adult

Prekindergarten

First grade

Second grade

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentIdentificationCode', @level2type=N'COLUMN',@level2name=N'AssessedGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The version identifier for the test assessment.

NEDM: Assessment Version

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentIdentificationCode', @level2type=N'COLUMN',@level2name=N'Version'

GO

/****** Object:  Table [edfi].[AssessmentFamilyIdentificationCode]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AssessmentFamilyIdentificationCode](

	[AssessmentFamilyTitle] [nvarchar](60) NOT NULL,

	[AssessmentIdentificationSystemTypeId] [int] NOT NULL,

	[AssigningOrganizationCode] [nvarchar](60) NULL,

	[IdentificationCode] [nvarchar](60) NOT NULL,

 CONSTRAINT [PK_AssessmentFamilyIdentificationCode] PRIMARY KEY CLUSTERED 

(

	[AssessmentFamilyTitle] ASC,

	[AssessmentIdentificationSystemTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[AssessmentFamilyAssociation]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AssessmentFamilyAssociation](

	[AssessmentFamilyTitle] [nvarchar](60) NOT NULL,

	[AssessmentTitle] [nvarchar](60) NOT NULL,

	[AssessmentAcademicSubjectTypeId] [int] NOT NULL,

	[AssessmentAssessedGradeLevelTypeId] [int] NOT NULL,

	[AssessmentVersion] [int] NOT NULL,

 CONSTRAINT [PK_AssessmentFamilyAssociation] PRIMARY KEY CLUSTERED 

(

	[AssessmentFamilyTitle] ASC,

	[AssessmentTitle] ASC,

	[AssessmentAcademicSubjectTypeId] ASC,

	[AssessmentAssessedGradeLevelTypeId] ASC,

	[AssessmentVersion] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The title or name of the assessment.

NEDM: Assessment Title

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentFamilyAssociation', @level2type=N'COLUMN',@level2name=N'AssessmentTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

NEDM: Assessment Content, Academic Subject

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentFamilyAssociation', @level2type=N'COLUMN',@level2name=N'AssessmentAcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The typical grade level for which an assessment is designed. If the test assessment spans a range of grades, then this attribute holds the highest grade assessed.  If only one grade level is assessed, then only this attribute is used. For example:

Adult

Prekindergarten

First grade

Second grade

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentFamilyAssociation', @level2type=N'COLUMN',@level2name=N'AssessmentAssessedGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The version identifier for the test assessment.

NEDM: Assessment Version

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentFamilyAssociation', @level2type=N'COLUMN',@level2name=N'AssessmentVersion'

GO

/****** Object:  Table [edfi].[AssessmentFamilyAssessmentPeriod]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AssessmentFamilyAssessmentPeriod](

	[AssessmentFamilyTitle] [nvarchar](60) NOT NULL,

	[AssessmentPeriodDescriptorId] [int] NOT NULL,

 CONSTRAINT [PK_AssessmentFamilyAssessmentPeriod] PRIMARY KEY CLUSTERED 

(

	[AssessmentFamilyTitle] ASC,

	[AssessmentPeriodDescriptorId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[CalendarDate]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CalendarDate](

	[EducationOrganizationId] [int] NOT NULL,

	[Date] [date] NOT NULL,

	[CalendarEventTypeId] [int] NOT NULL,

 CONSTRAINT [PK_CalendarDate_1] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[Date] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Month, day, and year of the first day of the grading period.



' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CalendarDate', @level2type=N'COLUMN',@level2name=N'Date'

GO

/****** Object:  Table [edfi].[AccountabilityRating]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AccountabilityRating](

	[EducationOrganizationId] [int] NOT NULL,

	[RatingTitle] [nvarchar](30) NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

	[Rating] [nvarchar](30) NOT NULL,

	[RatingDate] [date] NULL,

	[RatingOrganization] [nvarchar](20) NULL,

	[RatingProgram] [nvarchar](30) NULL,

 CONSTRAINT [PK_AccountabilityRating] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[RatingTitle] ASC,

	[SchoolYear] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The title of the rating (e.g., Campus Rating, Safety Score, etc.)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AccountabilityRating', @level2type=N'COLUMN',@level2name=N'RatingTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The school year for which the rating was awarded.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AccountabilityRating', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An accountability rating level, designation, or assessment.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AccountabilityRating', @level2type=N'COLUMN',@level2name=N'Rating'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The date the rating was awarded.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AccountabilityRating', @level2type=N'COLUMN',@level2name=N'RatingDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The organization assigning the accountability rating.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AccountabilityRating', @level2type=N'COLUMN',@level2name=N'RatingOrganization'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The rating program (e.g., NCLB, AEIS, etc.)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AccountabilityRating', @level2type=N'COLUMN',@level2name=N'RatingProgram'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An accountability rating for a campus or district.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AccountabilityRating'

GO

/****** Object:  Table [edfi].[Account]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[Account](

	[EducationOrganizationId] [int] NOT NULL,

	[AccountNumber] [nvarchar](50) NOT NULL,

	[FiscalYear] [int] NOT NULL,

 CONSTRAINT [PK_Account] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[AccountNumber] ASC,

	[FiscalYear] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Account', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The financial accounting year.

NEDM: Fiscal Year

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Account', @level2type=N'COLUMN',@level2name=N'FiscalYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This financial entity represents a funding source combined with its purpose and type of transaction.  It provides a formal record of the debits and credits relating to the specific account.

NEDM: Financial Account

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Account'

GO

/****** Object:  Table [edfi].[StudentIdentificationCode]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentIdentificationCode](

	[StudentUSI] [int] NOT NULL,

	[StudentIdentificationSystemTypeId] [int] NOT NULL,

	[AssigningOrganizationCode] [nvarchar](60) NULL,

	[IdentificationCode] [nvarchar](60) NOT NULL,

 CONSTRAINT [PK_StudentIdentificationCode] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[StudentIdentificationSystemTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[StudentHomeLanguages]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentHomeLanguages](

	[StudentUSI] [int] NOT NULL,

	[LanguageTypeId] [int] NOT NULL,

 CONSTRAINT [PK_StudentHomeLanguages] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[LanguageTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[StudentLearningStyle]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentLearningStyle](

	[StudentUSI] [int] NOT NULL,

	[VisualLearning] [int] NOT NULL,

	[AuditoryLearning] [int] NOT NULL,

	[TactileLearning] [int] NOT NULL,

 CONSTRAINT [PK_StudentLearningStyle] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[StudentLanguages]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentLanguages](

	[StudentUSI] [int] NOT NULL,

	[LanguageTypeId] [int] NOT NULL,

 CONSTRAINT [PK_StudentLanguages] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[LanguageTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[StudentIndicator]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentIndicator](

	[StudentUSI] [int] NOT NULL,

	[IndicatorName] [nvarchar](60) NOT NULL,

	[Indicator] [nvarchar](35) NOT NULL,

	[IndicatorGroup] [nvarchar](60) NULL,

	[BeginDate] [datetime] NULL,

	[EndDate] [datetime] NULL,

	[DesignatedBy] [nvarchar](60) NULL,

 CONSTRAINT [PK_StudentIndicator] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[IndicatorName] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[StudentElectronicMail]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentElectronicMail](

	[StudentUSI] [int] NOT NULL,

	[ElectronicMailTypeId] [int] NOT NULL,

	[ElectronicMailAddress] [nvarchar](128) NOT NULL,

 CONSTRAINT [PK_StudentElectronicMail] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[ElectronicMailTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[StudentParentAssociation]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentParentAssociation](

	[StudentUSI] [int] NOT NULL,

	[ParentUSI] [int] NOT NULL,

	[RelationTypeId] [int] NOT NULL,

	[PrimaryContactStatus] [bit] NULL,

	[LivesWith] [bit] NULL,

	[EmergencyContactStatus] [bit] NULL,

	[ContactPriority] [int] NULL,

	[ContactRestrictions] [nvarchar](250) NULL,

 CONSTRAINT [PK_StudentParentAssociation] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[ParentUSI] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentParentAssociation', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Parent Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentParentAssociation', @level2type=N'COLUMN',@level2name=N'ParentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The nature of an individual''s relationship to a student; for example:

Father

Mother

Step Father

Step Mother

Foster Father

Foster Mother

Guardian

...

NEDM: Relationship to Student

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentParentAssociation', @level2type=N'COLUMN',@level2name=N'RelationTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Indicator of whether the person is a primary parental contact for the student.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentParentAssociation', @level2type=N'COLUMN',@level2name=N'PrimaryContactStatus'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Indicator of whether the student lives with the associated parent.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentParentAssociation', @level2type=N'COLUMN',@level2name=N'LivesWith'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Indicator of whether the person is a designated emergency contact for the student.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentParentAssociation', @level2type=N'COLUMN',@level2name=N'EmergencyContactStatus'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This association relates students to their parents, guardians, or caretakers and indicates their relationship.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentParentAssociation'

GO

/****** Object:  Table [edfi].[StudentOtherName]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentOtherName](

	[StudentUSI] [int] NOT NULL,

	[OtherNameTypeId] [int] NOT NULL,

	[PersonalTitlePrefixTypeId] [int] NULL,

	[FirstName] [nvarchar](75) NOT NULL,

	[MiddleName] [nvarchar](75) NULL,

	[LastSurname] [nvarchar](75) NOT NULL,

	[GenerationCodeSuffixTypeId] [int] NULL,

 CONSTRAINT [PK_StudentOtherName] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[OtherNameTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A prefix used to denote the title, degree, position or seniority of the person.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentOtherName', @level2type=N'COLUMN',@level2name=N'PersonalTitlePrefixTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A name given to an individual at birth, baptism, or during another naming ceremony, or through legal change.

NEDM: First Name

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentOtherName', @level2type=N'COLUMN',@level2name=N'FirstName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A secondary name given to an individual at birth, baptism, or during another naming ceremony.

NEDM: Middle Name

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentOtherName', @level2type=N'COLUMN',@level2name=N'MiddleName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name borne in common by members of a family.

NEDM: Last Name/Surname

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentOtherName', @level2type=N'COLUMN',@level2name=N'LastSurname'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An appendage, if any, used to denote an individual''s generation in his family (e.g., Jr., Sr., III).

NEDM: Generation Code / Suffix

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentOtherName', @level2type=N'COLUMN',@level2name=N'GenerationCodeSuffixTypeId'

GO

/****** Object:  Table [edfi].[StudentCharacteristics]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentCharacteristics](

	[StudentUSI] [int] NOT NULL,

	[StudentCharacteristicsTypeId] [int] NOT NULL,

	[BeginDate] [date] NULL,

	[EndDate] [date] NULL,

	[DesignatedBy] [nvarchar](60) NULL,

 CONSTRAINT [PK_StudentCharacteristics] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[StudentCharacteristicsTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[StaffIdentificationCode]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StaffIdentificationCode](

	[StaffUSI] [int] NOT NULL,

	[StaffIdentificationSystemTypeId] [int] NOT NULL,

	[AssigningOrganizationCode] [nvarchar](60) NULL,

	[IdentificationCode] [nvarchar](60) NOT NULL,

 CONSTRAINT [PK_StaffIdentificationCode] PRIMARY KEY CLUSTERED 

(

	[StaffUSI] ASC,

	[StaffIdentificationSystemTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[StaffElectronicMail]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StaffElectronicMail](

	[StaffUSI] [int] NOT NULL,

	[ElectronicMailTypeId] [int] NOT NULL,

	[ElectronicMailAddress] [nvarchar](128) NOT NULL,

 CONSTRAINT [PK_StaffElectronicMail] PRIMARY KEY CLUSTERED 

(

	[StaffUSI] ASC,

	[ElectronicMailTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[StaffEducationOrgEmploymentAssociation]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StaffEducationOrgEmploymentAssociation](

	[StaffUSI] [int] NOT NULL,

	[EducationOrganizationId] [int] NOT NULL,

	[EmploymentStatusTypeId] [int] NOT NULL,

	[HireDate] [date] NOT NULL,

	[EndDate] [date] NULL,

	[SeparationTypeId] [int] NULL,

	[SeparationReasonTypeId] [int] NULL,

	[Department] [nvarchar](3) NULL,

	[FullTimeEquivalency] [int] NULL,

 CONSTRAINT [PK_StaffEducationOrgEmploymentAssociation] PRIMARY KEY CLUSTERED 

(

	[StaffUSI] ASC,

	[EducationOrganizationId] ASC,

	[EmploymentStatusTypeId] ASC,

	[HireDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Staff Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffEducationOrgEmploymentAssociation', @level2type=N'COLUMN',@level2name=N'StaffUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffEducationOrgEmploymentAssociation', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Reflects the type of employment or contract; for example:

Probationary

Contractual

Substitute/temporary

Tenured or permanent

Volunteer/no contract

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffEducationOrgEmploymentAssociation', @level2type=N'COLUMN',@level2name=N'EmploymentStatusTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year on which a contract between an individual and a governing authority specifies that employment is to begin (or the date on which the agreement is made valid).

NEDM: Contract Beginning Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffEducationOrgEmploymentAssociation', @level2type=N'COLUMN',@level2name=N'HireDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year on which a contract between an individual and a governing authority ends or is terminated under the provisions of the contract (or the date on which the agreement is made invalid).

NEDM: Contract Ending Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffEducationOrgEmploymentAssociation', @level2type=N'COLUMN',@level2name=N'EndDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Type of employment separation; for example:

Voluntary separation

Involuntary separation

Mutual agreement

Other

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffEducationOrgEmploymentAssociation', @level2type=N'COLUMN',@level2name=N'SeparationTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Reason for terminating the employment; for example:

Employment in education

Employment outside of education

Retirement

Family/personal relocation

Change of assignment

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffEducationOrgEmploymentAssociation', @level2type=N'COLUMN',@level2name=N'SeparationReasonTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The department or suborganization the employee/contractor is associated with in the Education Organization.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffEducationOrgEmploymentAssociation', @level2type=N'COLUMN',@level2name=N'Department'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The ratio between the hours of work expected in a position and the hours of work normally expected in a full-time position in the same setting.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffEducationOrgEmploymentAssociation', @level2type=N'COLUMN',@level2name=N'FullTimeEquivalency'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This association indicates the education organization an employee, contractor, volunteeer or other service provider is formally associated with, typically indicated by which organization the staff member has a services contract with or receives their compensation.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffEducationOrgEmploymentAssociation'

GO

/****** Object:  Table [edfi].[StaffEducationOrgAssignmentAssociation]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StaffEducationOrgAssignmentAssociation](

	[StaffUSI] [int] NOT NULL,

	[EducationOrganizationId] [int] NOT NULL,

	[StaffClassificationTypeId] [int] NOT NULL,

	[BeginDate] [date] NOT NULL,

	[PositionTitle] [nvarchar](45) NULL,

	[EndDate] [date] NULL,

 CONSTRAINT [PK_StaffEducationOrgAssignmentAssociation] PRIMARY KEY CLUSTERED 

(

	[StaffUSI] ASC,

	[EducationOrganizationId] ASC,

	[StaffClassificationTypeId] ASC,

	[BeginDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Staff Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffEducationOrgAssignmentAssociation', @level2type=N'COLUMN',@level2name=N'StaffUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffEducationOrgAssignmentAssociation', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The titles of employment, official status, or rank of education staff.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffEducationOrgAssignmentAssociation', @level2type=N'COLUMN',@level2name=N'StaffClassificationTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Month, day and year of the start or effective date of a staff member''s employment, contract or relationship with the LEA.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffEducationOrgAssignmentAssociation', @level2type=N'COLUMN',@level2name=N'BeginDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The descriptive name of an individual''s position.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffEducationOrgAssignmentAssociation', @level2type=N'COLUMN',@level2name=N'PositionTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Month, day and year of the end or termination date of a staff member''s employment, contract or relationship with the LEA.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffEducationOrgAssignmentAssociation', @level2type=N'COLUMN',@level2name=N'EndDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This association indicates theeducation organization to which a staff member provides services. Also known as campus of service.

NEDM: Staff Services



' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffEducationOrgAssignmentAssociation'

GO

/****** Object:  Table [edfi].[StaffOtherName]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StaffOtherName](

	[StaffUSI] [int] NOT NULL,

	[OtherNameTypeId] [int] NOT NULL,

	[PersonalTitlePrefixTypeId] [int] NULL,

	[FirstName] [nvarchar](75) NOT NULL,

	[MiddleName] [nvarchar](75) NULL,

	[LastSurname] [nvarchar](75) NOT NULL,

	[GenerationCodeSuffixTypeId] [int] NULL,

 CONSTRAINT [PK_StaffOtherName] PRIMARY KEY CLUSTERED 

(

	[StaffUSI] ASC,

	[OtherNameTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A prefix used to denote the title, degree, position or seniority of the person.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffOtherName', @level2type=N'COLUMN',@level2name=N'PersonalTitlePrefixTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A name given to an individual at birth, baptism, or during another naming ceremony, or through legal change.

NEDM: First Name

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffOtherName', @level2type=N'COLUMN',@level2name=N'FirstName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A secondary name given to an individual at birth, baptism, or during another naming ceremony.

NEDM: Middle Name

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffOtherName', @level2type=N'COLUMN',@level2name=N'MiddleName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name borne in common by members of a family.

NEDM: Last Name/Surname

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffOtherName', @level2type=N'COLUMN',@level2name=N'LastSurname'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An appendage, if any, used to denote an individual''s generation in his family (e.g., Jr., Sr., III).

NEDM: Generation Code / Suffix

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffOtherName', @level2type=N'COLUMN',@level2name=N'GenerationCodeSuffixTypeId'

GO

/****** Object:  Table [edfi].[StudentCohortYears]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentCohortYears](

	[StudentUSI] [int] NOT NULL,

	[CohortYearTypeId] [int] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

 CONSTRAINT [PK_StudentCohortYears] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[CohortYearTypeId] ASC,

	[SchoolYear] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCohortYears', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

/****** Object:  Table [edfi].[StudentTelephone]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentTelephone](

	[StudentUSI] [int] NOT NULL,

	[TelephoneNumberTypeId] [int] NOT NULL,

	[PrimaryTelephoneNumberIndicator] [bit] NULL,

	[TelephoneNumber] [nvarchar](24) NOT NULL,

 CONSTRAINT [PK_StudentTelephone] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[TelephoneNumberTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[StudentRace]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentRace](

	[StudentUSI] [int] NOT NULL,

	[RaceTypeId] [int] NOT NULL,

 CONSTRAINT [PK_StudentRace] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[RaceTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[StudentProgramParticipations]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentProgramParticipations](

	[StudentUSI] [int] NOT NULL,

	[ProgramTypeId] [int] NOT NULL,

	[BeginDate] [datetime] NULL,

	[EndDate] [datetime] NULL,

	[DesignatedBy] [nvarchar](60) NULL,

 CONSTRAINT [PK_StudentProgramParticipations] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[ProgramTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[StateEducationAgency]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StateEducationAgency](

	[StateEducationAgencyId] [int] NOT NULL,

 CONSTRAINT [PK_StateEducationAgency] PRIMARY KEY CLUSTERED 

(

	[StateEducationAgencyId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[Session]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[Session](

	[EducationOrganizationId] [int] NOT NULL,

	[TermTypeId] [int] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

	[SessionName] [nvarchar](20) NOT NULL,

	[BeginDate] [date] NOT NULL,

	[EndDate] [date] NOT NULL,

	[TotalInstructionalDays] [int] NOT NULL,

 CONSTRAINT [PK_Session] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[TermTypeId] ASC,

	[SchoolYear] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Session', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The identifier for the school year (e.g., 2010/11).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Session', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The identifier for the calendar for the academic session (e.g., 2010/11, 2011 Summer).

NEDM: Session Type

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Session', @level2type=N'COLUMN',@level2name=N'SessionName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The statrt date for the academic week.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Session', @level2type=N'COLUMN',@level2name=N'BeginDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Month, day and year of the last day of the instruction for the school year.

NEDM: Last Day of Class for Students

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Session', @level2type=N'COLUMN',@level2name=N'EndDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The total number of instructional days in the school calendar.

NEDM: Total Days in Session

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Session', @level2type=N'COLUMN',@level2name=N'TotalInstructionalDays'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A calendar of days made available to instruct students.  The school calendar could be organized by semester, or by school year plus summer school.

NEDM: School Year

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Session'

GO

/****** Object:  Table [edfi].[StaffAddress]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StaffAddress](

	[StaffUSI] [int] NOT NULL,

	[AddressTypeId] [int] NOT NULL,

	[StreetNumberName] [nvarchar](150) NOT NULL,

	[ApartmentRoomSuiteNumber] [nvarchar](20) NULL,

	[BuildingSiteNumber] [nvarchar](20) NULL,

	[City] [nvarchar](30) NOT NULL,

	[StateAbbreviationTypeId] [int] NOT NULL,

	[PostalCode] [nvarchar](17) NOT NULL,

	[NameOfCounty] [nvarchar](30) NULL,

	[CountyFIPSCode] [nvarchar](5) NULL,

	[CountryCodeTypeId] [int] NULL,

	[Latitude] [nvarchar](20) NULL,

	[Longitude] [nvarchar](20) NULL,

	[BeginDate] [date] NULL,

	[EndDate] [date] NULL,

 CONSTRAINT [PK_StaffAddress] PRIMARY KEY CLUSTERED 

(

	[StaffUSI] ASC,

	[AddressTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Address Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffAddress', @level2type=N'COLUMN',@level2name=N'StaffUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The street number and street name or post office box number of an address.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffAddress', @level2type=N'COLUMN',@level2name=N'StreetNumberName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The apartment, room, or suite number of an address. 

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffAddress', @level2type=N'COLUMN',@level2name=N'ApartmentRoomSuiteNumber'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of the building on the site, if more than one building shares the same address.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffAddress', @level2type=N'COLUMN',@level2name=N'BuildingSiteNumber'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the city in which an address is located. 

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffAddress', @level2type=N'COLUMN',@level2name=N'City'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The abbreviation for the state (within the United States) or outlying area in which an address is located. 

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffAddress', @level2type=N'COLUMN',@level2name=N'StateAbbreviationTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The five or nine digit zip code portion of an address.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffAddress', @level2type=N'COLUMN',@level2name=N'PostalCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the county, parish, borough, or comparable unit (within a state) in which an address is located.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffAddress', @level2type=N'COLUMN',@level2name=N'NameOfCounty'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'5 digit code consisting of the two digit state code followed by the three digit FIPS code for the county.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffAddress', @level2type=N'COLUMN',@level2name=N'CountyFIPSCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The unique two character International Organization for Standardization (ISO) code for the country in which an address is located.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffAddress', @level2type=N'COLUMN',@level2name=N'CountryCodeTypeId'

GO

/****** Object:  Table [edfi].[StaffTelephone]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StaffTelephone](

	[StaffUSI] [int] NOT NULL,

	[TelephoneNumberTypeId] [int] NOT NULL,

	[PrimaryTelephoneNumberIndicator] [bit] NULL,

	[TelephoneNumber] [nvarchar](24) NOT NULL,

 CONSTRAINT [PK_StaffTelephone] PRIMARY KEY CLUSTERED 

(

	[StaffUSI] ASC,

	[TelephoneNumberTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[StaffRace]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StaffRace](

	[StaffUSI] [int] NOT NULL,

	[RaceTypeId] [int] NOT NULL,

 CONSTRAINT [PK_StaffRace] PRIMARY KEY CLUSTERED 

(

	[StaffUSI] ASC,

	[RaceTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The general racial category which most clearly reflects the individual''s recognition of his or her community or with which the individual most identifies. The way this data element is listed, it must allow for multiple entries so that each individual can specify all appropriate races.

      AMERICAN-INDIAN-ALASKA-NATIVE-CODE

      ASIAN-CODE

      BLACK-AFRICAN-AMERICAN-CODE

      NATIVE-HAWAIIAN-PACIFIC-ISLANDER-CODE

      WHITE-CODE

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffRace', @level2type=N'COLUMN',@level2name=N'RaceTypeId'

GO

/****** Object:  Table [edfi].[StudentAssessment]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentAssessment](

	[StudentUSI] [int] NOT NULL,

	[AssessmentTitle] [nvarchar](60) NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

	[AssessedGradeLevelTypeId] [int] NOT NULL,

	[Version] [int] NOT NULL,

	[AdministrationDate] [date] NOT NULL,

	[AdministrationEndDate] [date] NULL,

	[SerialNumber] [nvarchar](20) NULL,

	[AdministrationLanguageTypeId] [int] NULL,

	[AdministrationEnvironmentTypeId] [int] NULL,

	[RetestIndicatorTypeId] [int] NULL,

	[ReasonNotTestedTypeId] [int] NULL,

	[WhenAssessedGradeLevelTypeId] [int] NULL,

 CONSTRAINT [PK_StudentAssessment] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[AssessmentTitle] ASC,

	[AcademicSubjectTypeId] ASC,

	[AssessedGradeLevelTypeId] ASC,

	[Version] ASC,

	[AdministrationDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessment', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The title or name of the assessment.

NEDM: Assessment Title

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessment', @level2type=N'COLUMN',@level2name=N'AssessmentTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

NEDM: Assessment Content, Academic Subject

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessment', @level2type=N'COLUMN',@level2name=N'AcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The typical grade level for which an assessment is designed. If the test assessment spans a range of grades, then this attribute holds the highest grade assessed.  If only one grade level is assessed, then only this attribute is used. For example:

Adult

Prekindergarten

First grade

Second grade

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessment', @level2type=N'COLUMN',@level2name=N'AssessedGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The version identifier for the test assessment.

NEDM: Assessment Version

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessment', @level2type=N'COLUMN',@level2name=N'Version'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month(s), day(s), and year on which an assessment is administered or first day of administration if over multiple days.

NEDM: Assessment Administration Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessment', @level2type=N'COLUMN',@level2name=N'AdministrationDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Assessment Administration End Date, if administered over multiple days.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessment', @level2type=N'COLUMN',@level2name=N'AdministrationEndDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The unique number of the assessment form or answer document.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessment', @level2type=N'COLUMN',@level2name=N'SerialNumber'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The language in which an assessment is written and/or administered. 

NEDM: Assessment Administration Language

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessment', @level2type=N'COLUMN',@level2name=N'AdministrationLanguageTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The environment in which the test was administered.  For example:

Electronic

Classroom

Testing Center

...

....

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessment', @level2type=N'COLUMN',@level2name=N'AdministrationEnvironmentTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Indicator if the test was retaken.  For example:

Primary administration

First retest

Second retest

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessment', @level2type=N'COLUMN',@level2name=N'RetestIndicatorTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The primary reason student is not tested. For example:

Absent

Refusal by parent

Refusal by student

Medical waiver

Illness

Disruptive behavior

LEP Exempt

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessment', @level2type=N'COLUMN',@level2name=N'ReasonNotTestedTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The grade level of a student when assessed.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessment', @level2type=N'COLUMN',@level2name=N'WhenAssessedGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This represents the analysis or scoring of a student''s response on an assessment. The analysis results in a value that represents a student''s performance on a set of items on a test.

NEDM: Assessment Score

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessment'

GO

/****** Object:  Table [edfi].[StudentAddress]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentAddress](

	[StudentUSI] [int] NOT NULL,

	[AddressTypeId] [int] NOT NULL,

	[StreetNumberName] [nvarchar](150) NOT NULL,

	[ApartmentRoomSuiteNumber] [nvarchar](20) NULL,

	[BuildingSiteNumber] [nvarchar](20) NULL,

	[City] [nvarchar](30) NOT NULL,

	[StateAbbreviationTypeId] [int] NOT NULL,

	[PostalCode] [nvarchar](17) NOT NULL,

	[NameOfCounty] [nvarchar](30) NULL,

	[CountyFIPSCode] [nvarchar](5) NULL,

	[CountryCodeTypeId] [int] NULL,

	[Latitude] [nvarchar](20) NULL,

	[Longitude] [nvarchar](20) NULL,

	[BeginDate] [date] NULL,

	[EndDate] [date] NULL,

 CONSTRAINT [PK_StudentAddress] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[AddressTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Address Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAddress', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The street number and street name or post office box number of an address.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAddress', @level2type=N'COLUMN',@level2name=N'StreetNumberName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The apartment, room, or suite number of an address. 

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAddress', @level2type=N'COLUMN',@level2name=N'ApartmentRoomSuiteNumber'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of the building on the site, if more than one building shares the same address.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAddress', @level2type=N'COLUMN',@level2name=N'BuildingSiteNumber'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the city in which an address is located. 

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAddress', @level2type=N'COLUMN',@level2name=N'City'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The abbreviation for the state (within the United States) or outlying area in which an address is located. 

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAddress', @level2type=N'COLUMN',@level2name=N'StateAbbreviationTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The five or nine digit zip code portion of an address.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAddress', @level2type=N'COLUMN',@level2name=N'PostalCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the county, parish, borough, or comparable unit (within a state) in which an address is located.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAddress', @level2type=N'COLUMN',@level2name=N'NameOfCounty'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'5 digit code consisting of the two digit state code followed by the three digit FIPS code for the county.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAddress', @level2type=N'COLUMN',@level2name=N'CountyFIPSCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The unique two character International Organization for Standardization (ISO) code for the country in which an address is located.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAddress', @level2type=N'COLUMN',@level2name=N'CountryCodeTypeId'

GO

/****** Object:  Table [edfi].[Section504Disability]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[Section504Disability](

	[StudentUSI] [int] NOT NULL,

	[Section504DisabilityTypeId] [int] NOT NULL,

 CONSTRAINT [PK_Section504Disability] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[Section504DisabilityTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[ReportCard]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ReportCard](

	[StudentUSI] [int] NOT NULL,

	[EducationOrganizationId] [int] NOT NULL,

	[GradingPeriodTypeId] [int] NOT NULL,

	[GradingPeriodBeginDate] [date] NOT NULL,

	[GPAGivenGradingPeriod] [decimal](18, 4) NOT NULL,

	[GPACumulative] [decimal](18, 4) NOT NULL,

	[NumberOfDaysAbsent] [decimal](18, 4) NULL,

	[NumberOfDaysInAttendance] [decimal](18, 4) NULL,

	[NumberOfDaysTardy] [int] NULL,

 CONSTRAINT [PK_ReportCard] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[EducationOrganizationId] ASC,

	[GradingPeriodTypeId] ASC,

	[GradingPeriodBeginDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ReportCard', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ReportCard', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the grading period during the school year in which the grade is offered (e.g., 1st cycle, 1st semester)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ReportCard', @level2type=N'COLUMN',@level2name=N'GradingPeriodTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Month, day, and year of the first day of the grading period.



' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ReportCard', @level2type=N'COLUMN',@level2name=N'GradingPeriodBeginDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A measure of average performance in all courses taken by an individual for the current grading period.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ReportCard', @level2type=N'COLUMN',@level2name=N'GPAGivenGradingPeriod'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A measure of cumulative average performance in all courses taken by an individual from the beginning of the school year through the current grading period.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ReportCard', @level2type=N'COLUMN',@level2name=N'GPACumulative'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of days an individual is absent when school is in session during a given reporting period. 

NEDM: Number of Days Absent

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ReportCard', @level2type=N'COLUMN',@level2name=N'NumberOfDaysAbsent'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of days an individual is present when school is in session during a given reporting period. 

NEDM: Number Of Days in Attendance

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ReportCard', @level2type=N'COLUMN',@level2name=N'NumberOfDaysInAttendance'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The numbre of days an individual is tardy during a given reporting period.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ReportCard', @level2type=N'COLUMN',@level2name=N'NumberOfDaysTardy'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This educational entity represents the collecton of student grades for courses taken during a grading period.

NEDM: Report Card

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ReportCard'

GO

/****** Object:  Table [edfi].[PostSecondaryEvent]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[PostSecondaryEvent](

	[StudentUSI] [int] NOT NULL,

	[PostSecondaryEventCategoryTypeId] [int] NOT NULL,

	[EventDate] [date] NOT NULL,

	[NameOfInstitution] [nvarchar](75) NULL,

	[InstitutionId] [nvarchar](30) NULL,

 CONSTRAINT [PK_PostSecondaryEvent] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[PostSecondaryEventCategoryTypeId] ASC,

	[EventDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'PostSecondaryEvent', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The postsecondary event that is logged (e.g., FAFSA application, college application, college acceptance)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'PostSecondaryEvent', @level2type=N'COLUMN',@level2name=N'PostSecondaryEventCategoryTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The date the event occurred or was recorded.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'PostSecondaryEvent', @level2type=N'COLUMN',@level2name=N'EventDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The PostSecondary institution associated with the event.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'PostSecondaryEvent', @level2type=N'COLUMN',@level2name=N'NameOfInstitution'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The ID of the postsecondary institution.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'PostSecondaryEvent', @level2type=N'COLUMN',@level2name=N'InstitutionId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Records significant events with postsecondary taken by students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'PostSecondaryEvent'

GO

/****** Object:  Table [edfi].[Program]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[Program](

	[EducationOrganizationId] [int] NOT NULL,

	[ProgramTypeId] [int] NOT NULL,

	[ProgramId] [nvarchar](20) NULL,

	[ProgramSponsorTypeId] [int] NULL,

 CONSTRAINT [PKProgram] PRIMARY KEY NONCLUSTERED 

(

	[EducationOrganizationId] ASC,

	[ProgramTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[ObjectiveAssessment]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ObjectiveAssessment](

	[ObjectiveItem] [int] NOT NULL,

	[AssessmentTitle] [nvarchar](60) NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

	[AssessedGradeLevelTypeId] [int] NOT NULL,

	[Version] [int] NOT NULL,

	[IdentificationCode] [nvarchar](20) NOT NULL,

	[MaxRawScore] [int] NULL,

	[PercentOfAssessment] [decimal](6, 2) NULL,

	[Nomenclature] [nvarchar](35) NULL,

	[LearningStandardId] [nvarchar](40) NULL,

 CONSTRAINT [PK_ObjectiveAssessment] PRIMARY KEY CLUSTERED 

(

	[ObjectiveItem] ASC,

	[AssessmentTitle] ASC,

	[AcademicSubjectTypeId] ASC,

	[AssessedGradeLevelTypeId] ASC,

	[Version] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The title or name of the assessment.

NEDM: Assessment Title

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessment', @level2type=N'COLUMN',@level2name=N'AssessmentTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

NEDM: Assessment Content, Academic Subject

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessment', @level2type=N'COLUMN',@level2name=N'AcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The typical grade level for which an assessment is designed. If the test assessment spans a range of grades, then this attribute holds the highest grade assessed.  If only one grade level is assessed, then only this attribute is used. For example:

Adult

Prekindergarten

First grade

Second grade

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessment', @level2type=N'COLUMN',@level2name=N'AssessedGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The version identifier for the test assessment.

NEDM: Assessment Version

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessment', @level2type=N'COLUMN',@level2name=N'Version'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A unique number or alphanumeric code assigned to a space, room, site, building, individual, organization, program, or institution by a school, school system, a state, or other agency or entity.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessment', @level2type=N'COLUMN',@level2name=N'IdentificationCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The maximum raw score achievable across all assessment items for the objective that are correct and scored at the maximum.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessment', @level2type=N'COLUMN',@level2name=N'MaxRawScore'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This entity represents subtests that assess specific learning objectives.

NEDM: subtests

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessment'

GO

/****** Object:  Table [edfi].[OpenStaffPosition]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[OpenStaffPosition](

	[EducationOrganizationId] [int] NOT NULL,

	[EmploymentStatusTypeId] [int] NOT NULL,

	[StaffClassificationTypeId] [int] NOT NULL,

	[RequisitionNumber] [nvarchar](10) NOT NULL,

	[DatePosted] [date] NOT NULL,

	[PositionTitle] [nvarchar](45) NULL,

	[ProgramAssignmentTypeId] [int] NULL,

	[DatePostingRemoved] [date] NULL,

	[PostingResultTypeId] [int] NULL,

 CONSTRAINT [PK_OpenStaffPosition] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[EmploymentStatusTypeId] ASC,

	[StaffClassificationTypeId] ASC,

	[RequisitionNumber] ASC,

	[DatePosted] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'OpenStaffPosition', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The desired employment status (e.g., contractual, substitute, permanent employee)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'OpenStaffPosition', @level2type=N'COLUMN',@level2name=N'EmploymentStatusTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The position''s title or rank (e.g., Counselor, teacher, etc)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'OpenStaffPosition', @level2type=N'COLUMN',@level2name=N'StaffClassificationTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The date the position was posted.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'OpenStaffPosition', @level2type=N'COLUMN',@level2name=N'DatePosted'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The descriptive name of an individual''s position.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'OpenStaffPosition', @level2type=N'COLUMN',@level2name=N'PositionTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The program to which a teaching position would be assigned (e.g., regular education, special education, etc.)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'OpenStaffPosition', @level2type=N'COLUMN',@level2name=N'ProgramAssignmentTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The date the posting was removed or filled.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'OpenStaffPosition', @level2type=N'COLUMN',@level2name=N'DatePostingRemoved'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Indication of whether the position was filled or retired without filling.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'OpenStaffPosition', @level2type=N'COLUMN',@level2name=N'PostingResultTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Open staff position that s seeking to be filled.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'OpenStaffPosition'

GO

/****** Object:  Table [edfi].[MeetingTimeMeetingDay]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[MeetingTimeMeetingDay](

	[SchoolId] [int] NOT NULL,

	[ClassPeriodName] [nvarchar](20) NOT NULL,

	[WeekNumber] [int] NOT NULL,

	[MeetingDaysTypeId] [int] NOT NULL,

 CONSTRAINT [PK_MeetingTimeMeetingDay] PRIMARY KEY CLUSTERED 

(

	[SchoolId] ASC,

	[ClassPeriodName] ASC,

	[WeekNumber] ASC,

	[MeetingDaysTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the portion of a typical daily session in which students receive instruction in a specified subject (e.g., morning, sixth period, block period, or AB schedules). 

NEDM: Class Period

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'MeetingTimeMeetingDay', @level2type=N'COLUMN',@level2name=N'ClassPeriodName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The week number (out of the number of weeks in the cycle) for this meeting time.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'MeetingTimeMeetingDay', @level2type=N'COLUMN',@level2name=N'WeekNumber'

GO

/****** Object:  Table [edfi].[ParentTelephone]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ParentTelephone](

	[ParentUSI] [int] NOT NULL,

	[TelephoneNumberTypeId] [int] NOT NULL,

	[PrimaryTelephoneNumberIndicator] [bit] NULL,

	[TelephoneNumber] [nvarchar](24) NOT NULL,

 CONSTRAINT [PK_ParentTelephone] PRIMARY KEY CLUSTERED 

(

	[ParentUSI] ASC,

	[TelephoneNumberTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Parent Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ParentTelephone', @level2type=N'COLUMN',@level2name=N'ParentUSI'

GO

/****** Object:  Table [edfi].[ParentOtherName]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ParentOtherName](

	[ParentUSI] [int] NOT NULL,

	[OtherNameTypeId] [int] NOT NULL,

	[PersonalTitlePrefixTypeId] [int] NULL,

	[FirstName] [nvarchar](75) NOT NULL,

	[MiddleName] [nvarchar](75) NULL,

	[LastSurname] [nvarchar](75) NOT NULL,

	[GenerationCodeSuffixTypeId] [int] NULL,

	[PersonalInformationVerificationTypeId] [int] NULL,

 CONSTRAINT [PK_ParentOtherName] PRIMARY KEY CLUSTERED 

(

	[ParentUSI] ASC,

	[OtherNameTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Parent Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ParentOtherName', @level2type=N'COLUMN',@level2name=N'ParentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A name given to an individual at birth, baptism, or during another naming ceremony, or through legal change.

NEDM: First Name

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ParentOtherName', @level2type=N'COLUMN',@level2name=N'FirstName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A secondary name given to an individual at birth, baptism, or during another naming ceremony.

NEDM: Middle Name

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ParentOtherName', @level2type=N'COLUMN',@level2name=N'MiddleName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name borne in common by members of a family.

NEDM: Last Name/Surname

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ParentOtherName', @level2type=N'COLUMN',@level2name=N'LastSurname'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An appendage, if any, used to denote an individual''s generation in his family (e.g., Jr., Sr., III).

NEDM: Generation Code / Suffix

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ParentOtherName', @level2type=N'COLUMN',@level2name=N'GenerationCodeSuffixTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The evidence presented to verify one''s personal identity; for example: drivers license, passport, birth certificate, etc.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ParentOtherName', @level2type=N'COLUMN',@level2name=N'PersonalInformationVerificationTypeId'

GO

/****** Object:  Table [edfi].[ParentElectronicMail]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ParentElectronicMail](

	[ParentUSI] [int] NOT NULL,

	[ElectronicMailTypeId] [int] NOT NULL,

	[ElectronicMailAddress] [nvarchar](128) NOT NULL,

 CONSTRAINT [PK_ParentElectronicMail] PRIMARY KEY CLUSTERED 

(

	[ParentUSI] ASC,

	[ElectronicMailTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Parent Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ParentElectronicMail', @level2type=N'COLUMN',@level2name=N'ParentUSI'

GO

/****** Object:  Table [edfi].[ParentAddress]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ParentAddress](

	[ParentUSI] [int] NOT NULL,

	[AddressTypeId] [int] NOT NULL,

	[StreetNumberName] [nvarchar](150) NOT NULL,

	[ApartmentRoomSuiteNumber] [nvarchar](20) NULL,

	[BuildingSiteNumber] [nvarchar](20) NULL,

	[City] [nvarchar](30) NOT NULL,

	[StateAbbreviationTypeId] [int] NOT NULL,

	[PostalCode] [nvarchar](17) NOT NULL,

	[NameOfCounty] [nvarchar](30) NULL,

	[CountyFIPSCode] [nvarchar](5) NULL,

	[CountryCodeTypeId] [int] NULL,

	[Latitude] [nvarchar](20) NULL,

	[Longitude] [nvarchar](20) NULL,

	[BeginDate] [date] NULL,

	[EndDate] [date] NULL,

 CONSTRAINT [PK_ParentAddress] PRIMARY KEY CLUSTERED 

(

	[ParentUSI] ASC,

	[AddressTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Parent Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ParentAddress', @level2type=N'COLUMN',@level2name=N'ParentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The street number and street name or post office box number of an address.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ParentAddress', @level2type=N'COLUMN',@level2name=N'StreetNumberName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The apartment, room, or suite number of an address. 

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ParentAddress', @level2type=N'COLUMN',@level2name=N'ApartmentRoomSuiteNumber'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of the building on the site, if more than one building shares the same address.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ParentAddress', @level2type=N'COLUMN',@level2name=N'BuildingSiteNumber'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the city in which an address is located. 

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ParentAddress', @level2type=N'COLUMN',@level2name=N'City'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The abbreviation for the state (within the United States) or outlying area in which an address is located. 

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ParentAddress', @level2type=N'COLUMN',@level2name=N'StateAbbreviationTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The five or nine digit zip code portion of an address.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ParentAddress', @level2type=N'COLUMN',@level2name=N'PostalCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the county, parish, borough, or comparable unit (within a state) in which an address is located.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ParentAddress', @level2type=N'COLUMN',@level2name=N'NameOfCounty'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'5 digit code consisting of the two digit state code followed by the three digit FIPS code for the county.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ParentAddress', @level2type=N'COLUMN',@level2name=N'CountyFIPSCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The unique two character International Organization for Standardization (ISO) code for the country in which an address is located.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ParentAddress', @level2type=N'COLUMN',@level2name=N'CountryCodeTypeId'

GO

/****** Object:  Table [edfi].[Payroll]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[Payroll](

	[StaffUSI] [int] NOT NULL,

	[EducationOrganizationId] [int] NOT NULL,

	[AccountNumber] [nvarchar](50) NOT NULL,

	[FiscalYear] [int] NOT NULL,

	[AsOfDate] [date] NOT NULL,

	[AmountToDate] [money] NOT NULL,

 CONSTRAINT [PK_Payroll] PRIMARY KEY CLUSTERED 

(

	[StaffUSI] ASC,

	[EducationOrganizationId] ASC,

	[AccountNumber] ASC,

	[FiscalYear] ASC,

	[AsOfDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Staff Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Payroll', @level2type=N'COLUMN',@level2name=N'StaffUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Payroll', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The financial accounting year.

NEDM: Fiscal Year

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Payroll', @level2type=N'COLUMN',@level2name=N'FiscalYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Current balance (amount paid to employee) for account for the fiscal year.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Payroll', @level2type=N'COLUMN',@level2name=N'AmountToDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This financial entity represents the sum of the financal transactions to date for employee compensation. An "employee" who performs services under the direction of the employing institution or agency, is compensated for such services by the employer, and is eligible for employee benefits and wage or salary tax withholdings.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Payroll'

GO

/****** Object:  Table [edfi].[OpenStaffPositionInstructionalGradeLevels]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[OpenStaffPositionInstructionalGradeLevels](

	[EducationOrganizationId] [int] NOT NULL,

	[EmploymentStatusTypeId] [int] NOT NULL,

	[StaffClassificationTypeId] [int] NOT NULL,

	[RequisitionNumber] [nvarchar](10) NOT NULL,

	[DatePosted] [date] NOT NULL,

	[GradeLevelTypeId] [int] NOT NULL,

 CONSTRAINT [PK_OpenStaffPositionInstructionalGradeLevels] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[EmploymentStatusTypeId] ASC,

	[StaffClassificationTypeId] ASC,

	[RequisitionNumber] ASC,

	[DatePosted] ASC,

	[GradeLevelTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'OpenStaffPositionInstructionalGradeLevels', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The desired employment status (e.g., contractual, substitute, permanent employee)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'OpenStaffPositionInstructionalGradeLevels', @level2type=N'COLUMN',@level2name=N'EmploymentStatusTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The position''s title or rank (e.g., Counselor, teacher, etc)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'OpenStaffPositionInstructionalGradeLevels', @level2type=N'COLUMN',@level2name=N'StaffClassificationTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The date the position was posted.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'OpenStaffPositionInstructionalGradeLevels', @level2type=N'COLUMN',@level2name=N'DatePosted'

GO

/****** Object:  Table [edfi].[OpenStaffPositionAcademicSubjects]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[OpenStaffPositionAcademicSubjects](

	[EducationOrganizationId] [int] NOT NULL,

	[EmploymentStatusTypeId] [int] NOT NULL,

	[StaffClassificationTypeId] [int] NOT NULL,

	[RequisitionNumber] [nvarchar](10) NOT NULL,

	[DatePosted] [date] NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

 CONSTRAINT [PK_OpenStaffPositionAcademicSubjects] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[EmploymentStatusTypeId] ASC,

	[StaffClassificationTypeId] ASC,

	[RequisitionNumber] ASC,

	[DatePosted] ASC,

	[AcademicSubjectTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'OpenStaffPositionAcademicSubjects', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The desired employment status (e.g., contractual, substitute, permanent employee)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'OpenStaffPositionAcademicSubjects', @level2type=N'COLUMN',@level2name=N'EmploymentStatusTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The position''s title or rank (e.g., Counselor, teacher, etc)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'OpenStaffPositionAcademicSubjects', @level2type=N'COLUMN',@level2name=N'StaffClassificationTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The date the position was posted.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'OpenStaffPositionAcademicSubjects', @level2type=N'COLUMN',@level2name=N'DatePosted'

GO

/****** Object:  Table [edfi].[ObjectiveAssessmentPerformanceLevel]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ObjectiveAssessmentPerformanceLevel](

	[ObjectiveItem] [int] NOT NULL,

	[AssessmentTitle] [nvarchar](60) NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

	[AssessedGradeLevelTypeId] [int] NOT NULL,

	[Version] [int] NOT NULL,

	[PerformanceLevelDescriptorId] [int] NOT NULL,

	[AssessmentReportingMethodTypeId] [int] NOT NULL,

	[MinimumScore] [int] NULL,

	[MaximumScore] [int] NULL,

 CONSTRAINT [PK_ObjectiveAssessmentPerformanceLevel] PRIMARY KEY CLUSTERED 

(

	[ObjectiveItem] ASC,

	[AssessmentTitle] ASC,

	[AcademicSubjectTypeId] ASC,

	[AssessedGradeLevelTypeId] ASC,

	[Version] ASC,

	[PerformanceLevelDescriptorId] ASC,

	[AssessmentReportingMethodTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The title or name of the assessment.

NEDM: Assessment Title

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessmentPerformanceLevel', @level2type=N'COLUMN',@level2name=N'AssessmentTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

NEDM: Assessment Content, Academic Subject

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessmentPerformanceLevel', @level2type=N'COLUMN',@level2name=N'AcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The typical grade level for which an assessment is designed. If the test assessment spans a range of grades, then this attribute holds the highest grade assessed.  If only one grade level is assessed, then only this attribute is used. For example:

Adult

Prekindergarten

First grade

Second grade

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessmentPerformanceLevel', @level2type=N'COLUMN',@level2name=N'AssessedGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The version identifier for the test assessment.

NEDM: Assessment Version

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessmentPerformanceLevel', @level2type=N'COLUMN',@level2name=N'Version'

GO

/****** Object:  Table [edfi].[ObjectiveAssessmentLearningObjective]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ObjectiveAssessmentLearningObjective](

	[ObjectiveItem] [int] NOT NULL,

	[AssessmentTitle] [nvarchar](60) NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

	[AssessedGradeLevelTypeId] [int] NOT NULL,

	[Version] [int] NOT NULL,

	[Objective] [nvarchar](60) NOT NULL,

 CONSTRAINT [PK_LearningObjectiveAssessment] PRIMARY KEY CLUSTERED 

(

	[ObjectiveItem] ASC,

	[AssessmentTitle] ASC,

	[AcademicSubjectTypeId] ASC,

	[AssessedGradeLevelTypeId] ASC,

	[Version] ASC,

	[Objective] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The title or name of the assessment.

NEDM: Assessment Title

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessmentLearningObjective', @level2type=N'COLUMN',@level2name=N'AssessmentTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

NEDM: Assessment Content, Academic Subject

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessmentLearningObjective', @level2type=N'COLUMN',@level2name=N'AcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The typical grade level for which an assessment is designed. If the test assessment spans a range of grades, then this attribute holds the highest grade assessed.  If only one grade level is assessed, then only this attribute is used. For example:

Adult

Prekindergarten

First grade

Second grade

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessmentLearningObjective', @level2type=N'COLUMN',@level2name=N'AssessedGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The version identifier for the test assessment.

NEDM: Assessment Version

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessmentLearningObjective', @level2type=N'COLUMN',@level2name=N'Version'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The designated title of the learning objective.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessmentLearningObjective', @level2type=N'COLUMN',@level2name=N'Objective'

GO

/****** Object:  Table [edfi].[ObjectiveAssessementItem]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ObjectiveAssessementItem](

	[ObjectiveItem] [int] NOT NULL,

	[AssessmentTitle] [nvarchar](60) NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

	[AssessedGradeLevelTypeId] [int] NOT NULL,

	[Version] [int] NOT NULL,

	[AssessmentItem] [int] NOT NULL,

	[ParentObjectiveItem] [int] NULL,

	[ParentAssessmentTitle] [nvarchar](60) NULL,

	[ParentAcademicSubjectTypeId] [int] NULL,

	[ParentAssessedGradeLevelTypeId] [int] NULL,

	[ParentVersion] [int] NULL,

	[ParentAssessmentItem] [int] NULL,

 CONSTRAINT [PK_ObjectiveAssessementItem] PRIMARY KEY CLUSTERED 

(

	[ObjectiveItem] ASC,

	[AssessmentTitle] ASC,

	[AcademicSubjectTypeId] ASC,

	[AssessedGradeLevelTypeId] ASC,

	[Version] ASC,

	[AssessmentItem] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The title or name of the assessment.

NEDM: Assessment Title

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessementItem', @level2type=N'COLUMN',@level2name=N'AssessmentTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

NEDM: Assessment Content, Academic Subject

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessementItem', @level2type=N'COLUMN',@level2name=N'AcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The typical grade level for which an assessment is designed. For example:

Adult

Prekindergarten

First grade

Second grade

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessementItem', @level2type=N'COLUMN',@level2name=N'AssessedGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The version identifier for the test assessment.

NEDM: Assessment Version

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessementItem', @level2type=N'COLUMN',@level2name=N'Version'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The title or name of the assessment.

NEDM: Assessment Title

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessementItem', @level2type=N'COLUMN',@level2name=N'ParentAssessmentTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

NEDM: Assessment Content, Academic Subject

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessementItem', @level2type=N'COLUMN',@level2name=N'ParentAcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The typical grade level for which an assessment is designed. For example:

Adult

Prekindergarten

First grade

Second grade

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessementItem', @level2type=N'COLUMN',@level2name=N'ParentAssessedGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The version identifier for the test assessment.

NEDM: Assessment Version

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ObjectiveAssessementItem', @level2type=N'COLUMN',@level2name=N'ParentVersion'

GO

/****** Object:  Table [edfi].[StudentAcademicRecord]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentAcademicRecord](

	[StudentUSI] [int] NOT NULL,

	[EducationOrganizationId] [int] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

	[TermTypeId] [int] NOT NULL,

	[CumulativeCreditsEarned] [decimal](9, 2) NULL,

	[CumulativeCreditsAttempted] [decimal](9, 2) NULL,

	[CumulativeGradePointsEarned] [decimal](18, 4) NULL,

	[CumulativeGradePointAverage] [decimal](18, 4) NULL,

	[GradeValueQualifier] [nvarchar](20) NULL,

	[ProjectedGraduationDate] [date] NULL,

 CONSTRAINT [PK_StudentAcademicRecord] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[EducationOrganizationId] ASC,

	[SchoolYear] ASC,

	[TermTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAcademicRecord', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAcademicRecord', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The "as of" school year the student academic record.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAcademicRecord', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAcademicRecord', @level2type=N'COLUMN',@level2name=N'TermTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The cumulative number of credits an individual earns by completing courses or examinations during his or her enrollment in the current school as well as those credits transferred from schools in which the individual had been previously enrolled.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAcademicRecord', @level2type=N'COLUMN',@level2name=N'CumulativeCreditsEarned'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The cumulative number of credits an individual attempts to earn by taking courses during his or her enrollment in the current school as well as those credits transferred from schools in which the individual had been previously enrolled.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAcademicRecord', @level2type=N'COLUMN',@level2name=N'CumulativeCreditsAttempted'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The cumulative number of grade points an individual earns by successfully completing courses or examinations during his or her enrollment in the current school as well as those transferred from schools in which the individual had been previously enrolled.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAcademicRecord', @level2type=N'COLUMN',@level2name=N'CumulativeGradePointsEarned'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A measure of average performance in all courses taken by an individual during his or her school career as determined for record-keeping purposes. This is obtained by dividing the total grade points received by the total number of credits attempted. This usually includes grade points received and credits attempted in his or her current school as well as those transferred from schools in which the individual was previously enrolled.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAcademicRecord', @level2type=N'COLUMN',@level2name=N'CumulativeGradePointAverage'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The scale of equivalents, if applicable, for grades awarded as indicators of performance in schoolwork. For example, numerical equivalents for letter grades used in determining a student''s Grade Point Average (A=4, B=3, C=2, D=1 in a four-point system) or letter equivalents for percentage grades (90-100%=A, 80-90%=B, etc.).

NEDM: Grade Value Qualifier

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAcademicRecord', @level2type=N'COLUMN',@level2name=N'GradeValueQualifier'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month and year the student is projected to graduate.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAcademicRecord', @level2type=N'COLUMN',@level2name=N'ProjectedGraduationDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This educational entity represents the cumulative record of academic achievement for a student.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAcademicRecord'

GO

/****** Object:  Table [edfi].[StaffProgramAssociation]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StaffProgramAssociation](

	[EducationOrganizationId] [int] NOT NULL,

	[ProgramTypeId] [int] NOT NULL,

	[StaffUSI] [int] NOT NULL,

	[BeginDate] [date] NOT NULL,

	[EndDate] [date] NULL,

	[StudentRecordAccess] [bit] NULL,

 CONSTRAINT [PK_StaffProgramAssociation] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[ProgramTypeId] ASC,

	[StaffUSI] ASC,

	[BeginDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Staff Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffProgramAssociation', @level2type=N'COLUMN',@level2name=N'StaffUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year on which the student first received services.

NEDM: Beginning Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffProgramAssociation', @level2type=N'COLUMN',@level2name=N'BeginDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year on which the student exited the program or stopped receiving services.

NEDM: Ending Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffProgramAssociation', @level2type=N'COLUMN',@level2name=N'EndDate'

GO

/****** Object:  Table [edfi].[SessionGradingPeriod]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[SessionGradingPeriod](

	[EducationOrganizationId] [int] NOT NULL,

	[TermTypeId] [int] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

	[GradingPeriodTypeId] [int] NOT NULL,

	[GradingPeriodBeginDate] [date] NOT NULL,

 CONSTRAINT [PK_SessionGradingPeriod] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[TermTypeId] ASC,

	[SchoolYear] ASC,

	[GradingPeriodTypeId] ASC,

	[GradingPeriodBeginDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'SessionGradingPeriod', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The identifier for the school year (e.g., 2010/11).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'SessionGradingPeriod', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the grading period during the school year in which the grade is offered (e.g., 1st cycle, 1st semester)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'SessionGradingPeriod', @level2type=N'COLUMN',@level2name=N'GradingPeriodTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Month, day, and year of the first day of the grading period.



' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'SessionGradingPeriod', @level2type=N'COLUMN',@level2name=N'GradingPeriodBeginDate'

GO

/****** Object:  Table [edfi].[StudentProgramAssociation]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentProgramAssociation](

	[StudentUSI] [int] NOT NULL,

	[ProgramTypeId] [int] NOT NULL,

	[EducationOrganizationId] [int] NOT NULL,

	[BeginDate] [date] NOT NULL,

	[EndDate] [date] NULL,

	[ReasonExitedTypeId] [int] NULL,

 CONSTRAINT [PK_StudentProgramAssociation] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[ProgramTypeId] ASC,

	[EducationOrganizationId] ASC,

	[BeginDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year on which the student first received services.

NEDM: Beginning Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentProgramAssociation', @level2type=N'COLUMN',@level2name=N'BeginDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year on which the student exited the program or stopped receiving services.

NEDM: Ending Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentProgramAssociation', @level2type=N'COLUMN',@level2name=N'EndDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The reason the child left the program within a school or district.

NEDM: Reason Exited

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentProgramAssociation', @level2type=N'COLUMN',@level2name=N'ReasonExitedTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This association represents the program(s) that a student participates in or is served by.

NEDM: ServedByProgram

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentProgramAssociation'

GO

/****** Object:  Table [edfi].[StudentObjectiveAssessment]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentObjectiveAssessment](

	[StudentUSI] [int] NOT NULL,

	[ObjectiveItem] [int] NOT NULL,

	[AssessmentTitle] [nvarchar](60) NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

	[AssessedGradeLevelTypeId] [int] NOT NULL,

	[Version] [int] NOT NULL,

	[AdministrationDate] [date] NOT NULL,

 CONSTRAINT [PK_StudentObjectiveAssessment] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[ObjectiveItem] ASC,

	[AssessmentTitle] ASC,

	[AcademicSubjectTypeId] ASC,

	[AssessedGradeLevelTypeId] ASC,

	[Version] ASC,

	[AdministrationDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentObjectiveAssessment', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The title or name of the assessment.

NEDM: Assessment Title

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentObjectiveAssessment', @level2type=N'COLUMN',@level2name=N'AssessmentTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

NEDM: Assessment Content, Academic Subject

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentObjectiveAssessment', @level2type=N'COLUMN',@level2name=N'AcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The typical grade level for which an assessment is designed. If the test assessment spans a range of grades, then this attribute holds the highest grade assessed.  If only one grade level is assessed, then only this attribute is used. For example:

Adult

Prekindergarten

First grade

Second grade

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentObjectiveAssessment', @level2type=N'COLUMN',@level2name=N'AssessedGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The version identifier for the test assessment.

NEDM: Assessment Version

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentObjectiveAssessment', @level2type=N'COLUMN',@level2name=N'Version'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month(s), day(s), and year on which an assessment is administered or first day of administration if over multiple days.

NEDM: Assessment Administration Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentObjectiveAssessment', @level2type=N'COLUMN',@level2name=N'AdministrationDate'

GO

/****** Object:  Table [edfi].[StudentCohortAssociation]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentCohortAssociation](

	[StudentUSI] [int] NOT NULL,

	[EducationOrganizationId] [int] NOT NULL,

	[CohortIdentifier] [nvarchar](20) NOT NULL,

	[BeginDate] [date] NOT NULL,

	[EndDate] [date] NULL,

 CONSTRAINT [PK_StudentCohortAssociation] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[EducationOrganizationId] ASC,

	[CohortIdentifier] ASC,

	[BeginDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCohortAssociation', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCohortAssociation', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name or ID for the cohort.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCohortAssociation', @level2type=N'COLUMN',@level2name=N'CohortIdentifier'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The start date for assignment to this cohort.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCohortAssociation', @level2type=N'COLUMN',@level2name=N'BeginDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The end date for assignment to this cohort.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCohortAssociation', @level2type=N'COLUMN',@level2name=N'EndDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCohortAssociation'

GO

/****** Object:  Table [edfi].[Actual]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[Actual](

	[EducationOrganizationId] [int] NOT NULL,

	[AccountNumber] [nvarchar](50) NOT NULL,

	[FiscalYear] [int] NOT NULL,

	[AsOfDate] [date] NOT NULL,

	[AmountToDate] [money] NOT NULL,

 CONSTRAINT [PK_Actual] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[AccountNumber] ASC,

	[FiscalYear] ASC,

	[AsOfDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Actual', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The financial accounting year.

NEDM: Fiscal Year

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Actual', @level2type=N'COLUMN',@level2name=N'FiscalYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Current balance for the account.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Actual', @level2type=N'COLUMN',@level2name=N'AmountToDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This financial entity represents the sum of the financal transactions to date relating to a specific account.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Actual'

GO

/****** Object:  Table [edfi].[StaffCohortAssociation]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StaffCohortAssociation](

	[StaffUSI] [int] NOT NULL,

	[EducationOrganizationId] [int] NOT NULL,

	[CohortIdentifier] [nvarchar](20) NOT NULL,

	[BeginDate] [date] NOT NULL,

	[EndDate] [date] NULL,

	[StudentRecordAccess] [bit] NULL,

 CONSTRAINT [PK_StaffCohortAssociation] PRIMARY KEY CLUSTERED 

(

	[StaffUSI] ASC,

	[EducationOrganizationId] ASC,

	[CohortIdentifier] ASC,

	[BeginDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Staff Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffCohortAssociation', @level2type=N'COLUMN',@level2name=N'StaffUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffCohortAssociation', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name or ID for the cohort.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffCohortAssociation', @level2type=N'COLUMN',@level2name=N'CohortIdentifier'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The start date the staff was associated with the cohort.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffCohortAssociation', @level2type=N'COLUMN',@level2name=N'BeginDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The end date of the staff association with the cohort.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffCohortAssociation', @level2type=N'COLUMN',@level2name=N'EndDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Indicator of whether the staff has access to the student records of the cohort per district interpretation of FERPA and other privacy laws, regulations and policies.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffCohortAssociation', @level2type=N'COLUMN',@level2name=N'StudentRecordAccess'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StaffCohortAssociation'

GO

/****** Object:  Table [edfi].[StudentAssessmentSpecialAccommodations]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentAssessmentSpecialAccommodations](

	[StudentUSI] [int] NOT NULL,

	[AssessmentTitle] [nvarchar](60) NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

	[AssessedGradeLevelTypeId] [int] NOT NULL,

	[Version] [int] NOT NULL,

	[AdministrationDate] [date] NOT NULL,

	[SpecialAccommodationsTypeId] [int] NOT NULL,

 CONSTRAINT [PK_StudentAssessmentSpecialAccommodations] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[AssessmentTitle] ASC,

	[AcademicSubjectTypeId] ASC,

	[AssessedGradeLevelTypeId] ASC,

	[Version] ASC,

	[AdministrationDate] ASC,

	[SpecialAccommodationsTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentSpecialAccommodations', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The title or name of the assessment.

NEDM: Assessment Title

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentSpecialAccommodations', @level2type=N'COLUMN',@level2name=N'AssessmentTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

NEDM: Assessment Content, Academic Subject

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentSpecialAccommodations', @level2type=N'COLUMN',@level2name=N'AcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The typical grade level for which an assessment is designed. If the test assessment spans a range of grades, then this attribute holds the highest grade assessed.  If only one grade level is assessed, then only this attribute is used. For example:

Adult

Prekindergarten

First grade

Second grade

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentSpecialAccommodations', @level2type=N'COLUMN',@level2name=N'AssessedGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The version identifier for the test assessment.

NEDM: Assessment Version

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentSpecialAccommodations', @level2type=N'COLUMN',@level2name=N'Version'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month(s), day(s), and year on which an assessment is administered or first day of administration if over multiple days.

NEDM: Assessment Administration Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentSpecialAccommodations', @level2type=N'COLUMN',@level2name=N'AdministrationDate'

GO

/****** Object:  Table [edfi].[StudentAssessmentScoreResult]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentAssessmentScoreResult](

	[StudentUSI] [int] NOT NULL,

	[AssessmentTitle] [nvarchar](60) NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

	[AssessedGradeLevelTypeId] [int] NOT NULL,

	[Version] [int] NOT NULL,

	[AdministrationDate] [date] NOT NULL,

	[AssessmentReportingMethodTypeId] [int] NOT NULL,

	[Result] [nvarchar](35) NOT NULL,

 CONSTRAINT [PK_StudentAssessmentScoreResult] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[AssessmentTitle] ASC,

	[AcademicSubjectTypeId] ASC,

	[AssessedGradeLevelTypeId] ASC,

	[Version] ASC,

	[AdministrationDate] ASC,

	[AssessmentReportingMethodTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentScoreResult', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The title or name of the assessment.

NEDM: Assessment Title

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentScoreResult', @level2type=N'COLUMN',@level2name=N'AssessmentTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

NEDM: Assessment Content, Academic Subject

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentScoreResult', @level2type=N'COLUMN',@level2name=N'AcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The typical grade level for which an assessment is designed. If the test assessment spans a range of grades, then this attribute holds the highest grade assessed.  If only one grade level is assessed, then only this attribute is used. For example:

Adult

Prekindergarten

First grade

Second grade

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentScoreResult', @level2type=N'COLUMN',@level2name=N'AssessedGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The version identifier for the test assessment.

NEDM: Assessment Version

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentScoreResult', @level2type=N'COLUMN',@level2name=N'Version'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month(s), day(s), and year on which an assessment is administered or first day of administration if over multiple days.

NEDM: Assessment Administration Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentScoreResult', @level2type=N'COLUMN',@level2name=N'AdministrationDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the analytic score.  For example:

Percentile

Quantile measure

Lexile measure

Vertical scale score

TPM score

...

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentScoreResult', @level2type=N'COLUMN',@level2name=N'AssessmentReportingMethodTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Value for the analytic score.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentScoreResult', @level2type=N'COLUMN',@level2name=N'Result'

GO

/****** Object:  Table [edfi].[StudentAssessmentPerformanceLevel]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentAssessmentPerformanceLevel](

	[StudentUSI] [int] NOT NULL,

	[AssessmentTitle] [nvarchar](60) NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

	[AssessedGradeLevelTypeId] [int] NOT NULL,

	[Version] [int] NOT NULL,

	[AdministrationDate] [date] NOT NULL,

	[PerformanceLevelDescriptorId] [int] NOT NULL,

	[PerformanceLevelMet] [bit] NOT NULL,

 CONSTRAINT [PK_StudentAssessmentPerformanceLevel] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[AssessmentTitle] ASC,

	[AcademicSubjectTypeId] ASC,

	[AssessedGradeLevelTypeId] ASC,

	[Version] ASC,

	[AdministrationDate] ASC,

	[PerformanceLevelDescriptorId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentPerformanceLevel', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The title or name of the assessment.

NEDM: Assessment Title

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentPerformanceLevel', @level2type=N'COLUMN',@level2name=N'AssessmentTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

NEDM: Assessment Content, Academic Subject

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentPerformanceLevel', @level2type=N'COLUMN',@level2name=N'AcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The typical grade level for which an assessment is designed. If the test assessment spans a range of grades, then this attribute holds the highest grade assessed.  If only one grade level is assessed, then only this attribute is used. For example:

Adult

Prekindergarten

First grade

Second grade

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentPerformanceLevel', @level2type=N'COLUMN',@level2name=N'AssessedGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The version identifier for the test assessment.

NEDM: Assessment Version

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentPerformanceLevel', @level2type=N'COLUMN',@level2name=N'Version'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month(s), day(s), and year on which an assessment is administered or first day of administration if over multiple days.

NEDM: Assessment Administration Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentPerformanceLevel', @level2type=N'COLUMN',@level2name=N'AdministrationDate'

GO

/****** Object:  Table [edfi].[StudentAssessmentLinguisticAccommodations]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentAssessmentLinguisticAccommodations](

	[StudentUSI] [int] NOT NULL,

	[AssessmentTitle] [nvarchar](60) NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

	[AssessedGradeLevelTypeId] [int] NOT NULL,

	[Version] [int] NOT NULL,

	[AdministrationDate] [date] NOT NULL,

	[LinguisticAccommodationsTypeId] [int] NOT NULL,

 CONSTRAINT [PK_StudentAssessmentLinguisticAccommodations] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[AssessmentTitle] ASC,

	[AcademicSubjectTypeId] ASC,

	[AssessedGradeLevelTypeId] ASC,

	[Version] ASC,

	[AdministrationDate] ASC,

	[LinguisticAccommodationsTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentLinguisticAccommodations', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The title or name of the assessment.

NEDM: Assessment Title

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentLinguisticAccommodations', @level2type=N'COLUMN',@level2name=N'AssessmentTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

NEDM: Assessment Content, Academic Subject

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentLinguisticAccommodations', @level2type=N'COLUMN',@level2name=N'AcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The typical grade level for which an assessment is designed. If the test assessment spans a range of grades, then this attribute holds the highest grade assessed.  If only one grade level is assessed, then only this attribute is used. For example:

Adult

Prekindergarten

First grade

Second grade

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentLinguisticAccommodations', @level2type=N'COLUMN',@level2name=N'AssessedGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The version identifier for the test assessment.

NEDM: Assessment Version

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentLinguisticAccommodations', @level2type=N'COLUMN',@level2name=N'Version'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month(s), day(s), and year on which an assessment is administered or first day of administration if over multiple days.

NEDM: Assessment Administration Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentLinguisticAccommodations', @level2type=N'COLUMN',@level2name=N'AdministrationDate'

GO

/****** Object:  Table [edfi].[StudentAssessmentItem]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentAssessmentItem](

	[StudentUSI] [int] NOT NULL,

	[AssesmentItem] [int] NOT NULL,

	[AssessmentTitle] [nvarchar](60) NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

	[AssessedGradeLevelTypeId] [int] NOT NULL,

	[Version] [int] NOT NULL,

	[AdministrationDate] [date] NOT NULL,

	[AssessmentResponse] [nvarchar](20) NULL,

	[ResponseIndicatorTypeId] [int] NULL,

	[AssessmentItemResultTypeId] [int] NOT NULL,

	[RawScoreResult] [int] NULL,

 CONSTRAINT [PK_StudentAssessmentItem] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[AssesmentItem] ASC,

	[AssessmentTitle] ASC,

	[AcademicSubjectTypeId] ASC,

	[AssessedGradeLevelTypeId] ASC,

	[Version] ASC,

	[AdministrationDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentItem', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The title or name of the assessment.

NEDM: Assessment Title

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentItem', @level2type=N'COLUMN',@level2name=N'AssessmentTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

NEDM: Assessment Content, Academic Subject

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentItem', @level2type=N'COLUMN',@level2name=N'AcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The typical grade level for which an assessment is designed. If the test assessment spans a range of grades, then this attribute holds the highest grade assessed.  If only one grade level is assessed, then only this attribute is used. For example:

Adult

Prekindergarten

First grade

Second grade

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentItem', @level2type=N'COLUMN',@level2name=N'AssessedGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The version identifier for the test assessment.

NEDM: Assessment Version

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentItem', @level2type=N'COLUMN',@level2name=N'Version'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month(s), day(s), and year on which an assessment is administered or first day of administration if over multiple days.

NEDM: Assessment Administration Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentItem', @level2type=N'COLUMN',@level2name=N'AdministrationDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A student''s response to a stimulus on a test.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentItem', @level2type=N'COLUMN',@level2name=N'AssessmentResponse'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Indicator of the response.  For example:

Nonscorable response

Ineffective response

Effective response

Partial response

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentItem', @level2type=N'COLUMN',@level2name=N'ResponseIndicatorTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The analyzed result of a student''s response to an assessment item.. For example:

Correct

Incorrect

Met standard

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentItem', @level2type=N'COLUMN',@level2name=N'AssessmentItemResultTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A meaningful raw score of the performance of an individual on a test assessment item.

NEDM: Score Results

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentItem', @level2type=N'COLUMN',@level2name=N'RawScoreResult'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This represents the student''s response to an assessment item and the item-level scores such as correct, incorrect, or met standard. 

NEDM: Assessment Score

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentAssessmentItem'

GO

/****** Object:  Table [edfi].[AcademicWeek]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AcademicWeek](

	[EducationOrganizationId] [int] NOT NULL,

	[TermTypeId] [int] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

	[WeekIdentifer] [nvarchar](80) NOT NULL,

	[BeginDate] [date] NOT NULL,

	[EndDate] [date] NOT NULL,

	[TotalInstructionalDays] [int] NOT NULL,

 CONSTRAINT [PK_AcademicWeek] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[TermTypeId] ASC,

	[SchoolYear] ASC,

	[WeekIdentifer] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AcademicWeek', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The identifier for the school year (e.g., 2010/11).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AcademicWeek', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The campus label for the week.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AcademicWeek', @level2type=N'COLUMN',@level2name=N'WeekIdentifer'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The statrt date for the academic week.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AcademicWeek', @level2type=N'COLUMN',@level2name=N'BeginDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The end date for the academic week.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AcademicWeek', @level2type=N'COLUMN',@level2name=N'EndDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The total instructional days during the academic week.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AcademicWeek', @level2type=N'COLUMN',@level2name=N'TotalInstructionalDays'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This entitiy represents the academic weeks for a school year, optionally captured to support analyses.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AcademicWeek'

GO

/****** Object:  Table [edfi].[Budget]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[Budget](

	[EducationOrganizationId] [int] NOT NULL,

	[AccountNumber] [nvarchar](50) NOT NULL,

	[FiscalYear] [int] NOT NULL,

	[AsOfDate] [date] NOT NULL,

	[Amount] [money] NOT NULL,

 CONSTRAINT [PK_Budget] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[AccountNumber] ASC,

	[FiscalYear] ASC,

	[AsOfDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Budget', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The financial accounting year.

NEDM: Fiscal Year

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Budget', @level2type=N'COLUMN',@level2name=N'FiscalYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Amount budgeted for the account for this fiscal year.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Budget', @level2type=N'COLUMN',@level2name=N'Amount'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This financial entity represents the amount of monies allocated to be spent or received by an education organization as related to a specific account.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Budget'

GO

/****** Object:  Table [edfi].[CourseLearningStandard]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CourseLearningStandard](

	[EducationOrganizationId] [int] NOT NULL,

	[IdentityCourseCode] [nvarchar](20) NOT NULL,

	[LearningStandardId] [nvarchar](40) NOT NULL,

 CONSTRAINT [PK_CourseLearningStandard] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[IdentityCourseCode] ASC,

	[LearningStandardId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'TThe actual code that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseLearningStandard', @level2type=N'COLUMN',@level2name=N'IdentityCourseCode'

GO

/****** Object:  Table [edfi].[CourseLearningObjective]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CourseLearningObjective](

	[EducationOrganizationId] [int] NOT NULL,

	[IdentityCourseCode] [nvarchar](20) NOT NULL,

	[Objective] [nvarchar](60) NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

	[ObjectiveGradeLevelTypeId] [int] NOT NULL,

 CONSTRAINT [PK_CourseLearningObjective] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[IdentityCourseCode] ASC,

	[Objective] ASC,

	[AcademicSubjectTypeId] ASC,

	[ObjectiveGradeLevelTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'TThe actual code that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseLearningObjective', @level2type=N'COLUMN',@level2name=N'IdentityCourseCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The designated title of the learning objective.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseLearningObjective', @level2type=N'COLUMN',@level2name=N'Objective'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseLearningObjective', @level2type=N'COLUMN',@level2name=N'AcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The grade level for which the learning objective is targeted,

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseLearningObjective', @level2type=N'COLUMN',@level2name=N'ObjectiveGradeLevelTypeId'

GO

/****** Object:  Table [edfi].[CourseGradesOffered]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CourseGradesOffered](

	[EducationOrganizationId] [int] NOT NULL,

	[IdentityCourseCode] [nvarchar](20) NOT NULL,

	[GradesOfferedTypeId] [int] NOT NULL,

 CONSTRAINT [PK_CourseGradesOffered] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[IdentityCourseCode] ASC,

	[GradesOfferedTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'TThe actual code that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseGradesOffered', @level2type=N'COLUMN',@level2name=N'IdentityCourseCode'

GO

/****** Object:  Table [edfi].[CourseLevelCharacteristics]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CourseLevelCharacteristics](

	[EducationOrganizationId] [int] NOT NULL,

	[IdentityCourseCode] [nvarchar](20) NOT NULL,

	[CourseLevelCharacteristicsTypeId] [int] NOT NULL,

 CONSTRAINT [PK_CourseLevelCharacteristics] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[IdentityCourseCode] ASC,

	[CourseLevelCharacteristicsTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'TThe actual code that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseLevelCharacteristics', @level2type=N'COLUMN',@level2name=N'IdentityCourseCode'

GO

/****** Object:  Table [edfi].[CourseCodeIdentificationCode]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CourseCodeIdentificationCode](

	[EducationOrganizationId] [int] NOT NULL,

	[IdentityCourseCode] [nvarchar](20) NOT NULL,

	[CourseCodeSystemTypeId] [int] NOT NULL,

	[AssigningOrganizationCode] [nvarchar](60) NULL,

	[IdentificationCode] [nvarchar](60) NOT NULL,

 CONSTRAINT [PK_CourseCodeIdentificationCode] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[IdentityCourseCode] ASC,

	[CourseCodeSystemTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'TThe actual code that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseCodeIdentificationCode', @level2type=N'COLUMN',@level2name=N'IdentityCourseCode'

GO

/****** Object:  Table [edfi].[ContractedStaff]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ContractedStaff](

	[StaffUSI] [int] NOT NULL,

	[EducationOrganizationId] [int] NOT NULL,

	[AccountNumber] [nvarchar](50) NOT NULL,

	[FiscalYear] [int] NOT NULL,

	[AsOfDate] [date] NOT NULL,

	[AmountToDate] [money] NOT NULL,

 CONSTRAINT [PK_ContractedStaff] PRIMARY KEY CLUSTERED 

(

	[StaffUSI] ASC,

	[EducationOrganizationId] ASC,

	[AccountNumber] ASC,

	[FiscalYear] ASC,

	[AsOfDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Staff Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ContractedStaff', @level2type=N'COLUMN',@level2name=N'StaffUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ContractedStaff', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The financial accounting year.

NEDM: Fiscal Year

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ContractedStaff', @level2type=N'COLUMN',@level2name=N'FiscalYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Current balance (amount paid to contractor) for account for the fiscal year.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ContractedStaff', @level2type=N'COLUMN',@level2name=N'AmountToDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This financial entity represents the sum of the financal transactions to date for contracted staff. Contracted staff includes "contractors" or "consultants" who perform services for an agreed upon fee, or an employee of a management service contracted to work on site.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ContractedStaff'

GO

/****** Object:  Table [edfi].[CohortProgram]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CohortProgram](

	[EducationOrganizationId] [int] NOT NULL,

	[CohortIdentifier] [nvarchar](20) NOT NULL,

	[ProgramTypeId] [int] NOT NULL,

 CONSTRAINT [PK_CohortProgram] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[CohortIdentifier] ASC,

	[ProgramTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CohortProgram', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name or ID for the cohort.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CohortProgram', @level2type=N'COLUMN',@level2name=N'CohortIdentifier'

GO

/****** Object:  Table [edfi].[AccountCode]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AccountCode](

	[EducationOrganizationId] [int] NOT NULL,

	[AccountNumber] [nvarchar](50) NOT NULL,

	[FiscalYear] [int] NOT NULL,

	[AccountCodeDescriptorId] [int] NOT NULL,

 CONSTRAINT [PK_AccountCode] PRIMARY KEY CLUSTERED 

(

	[EducationOrganizationId] ASC,

	[AccountNumber] ASC,

	[FiscalYear] ASC,

	[AccountCodeDescriptorId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AccountCode', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The financial accounting year.

NEDM: Fiscal Year

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AccountCode', @level2type=N'COLUMN',@level2name=N'FiscalYear'

GO

/****** Object:  Table [edfi].[BellSchedule]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[BellSchedule](

	[SchoolId] [int] NOT NULL,

	[ClassPeriodName] [nvarchar](20) NOT NULL,

	[WeekNumber] [int] NOT NULL,

	[TermTypeId] [int] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

	[BellScheduleName] [nvarchar](60) NOT NULL,

	[WeeksInCycle] [int] NOT NULL,

 CONSTRAINT [PK_BellSchedule] PRIMARY KEY CLUSTERED 

(

	[SchoolId] ASC,

	[ClassPeriodName] ASC,

	[WeekNumber] ASC,

	[TermTypeId] ASC,

	[SchoolYear] ASC,

	[BellScheduleName] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the portion of a typical daily session in which students receive instruction in a specified subject (e.g., morning, sixth period, block period, or AB schedules). 

NEDM: Class Period

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'BellSchedule', @level2type=N'COLUMN',@level2name=N'ClassPeriodName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The week number (out of the number of weeks in the cycle) for this meeting time.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'BellSchedule', @level2type=N'COLUMN',@level2name=N'WeekNumber'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The identifier for the school year (e.g., 2010/11).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'BellSchedule', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of weeks in which the meeting time schedules repeats.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'BellSchedule', @level2type=N'COLUMN',@level2name=N'WeeksInCycle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The schedule of class period meeting times.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'BellSchedule'

GO

/****** Object:  Table [edfi].[EducationServiceCenter]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[EducationServiceCenter](

	[EducationServiceCenterId] [int] NOT NULL,

	[StateEducationAgencyId] [int] NULL,

 CONSTRAINT [PKEducationServiceCenter] PRIMARY KEY NONCLUSTERED 

(

	[EducationServiceCenterId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationServiceCenter Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'EducationServiceCenter', @level2type=N'COLUMN',@level2name=N'EducationServiceCenterId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This entity represents a regional, multi-services public agency authorized by State law to develop, manage, and provide services, programs, or other options support (e.g., construction, food services, technology services) to LEAs.

NEDM: Intermediate Educational Unit (IEU)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'EducationServiceCenter'

GO

/****** Object:  Table [edfi].[GraduationPlanCreditsBySubject]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[GraduationPlanCreditsBySubject](

	[GraduationPlanTypeId] [int] NOT NULL,

	[EducationOrganizationId] [int] NOT NULL,

	[SubjectAreaTypeId] [int] NOT NULL,

	[Credit] [decimal](9, 2) NOT NULL,

	[CreditTypeId] [int] NULL,

	[CreditConversion] [decimal](9, 2) NULL,

 CONSTRAINT [PK_GraduationPlanCreditsBySubject] PRIMARY KEY CLUSTERED 

(

	[GraduationPlanTypeId] ASC,

	[EducationOrganizationId] ASC,

	[SubjectAreaTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GraduationPlanCreditsBySubject', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

/****** Object:  Table [edfi].[GraduationPlanCreditsByCourse]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[GraduationPlanCreditsByCourse](

	[GraduationPlanTypeId] [int] NOT NULL,

	[EducationOrganizationId] [int] NOT NULL,

	[IdentityCourseCode] [nvarchar](20) NOT NULL,

	[Credit] [decimal](9, 2) NOT NULL,

	[CreditTypeId] [int] NULL,

	[CreditConversion] [decimal](9, 2) NULL,

	[GradeLevelTypeId] [int] NULL,

 CONSTRAINT [PK_GraduationPlanCreditsByCourse] PRIMARY KEY CLUSTERED 

(

	[GraduationPlanTypeId] ASC,

	[EducationOrganizationId] ASC,

	[IdentityCourseCode] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GraduationPlanCreditsByCourse', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'TThe actual code that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GraduationPlanCreditsByCourse', @level2type=N'COLUMN',@level2name=N'IdentityCourseCode'

GO

/****** Object:  Table [edfi].[StudentTitleIPartAProgramAssociation]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentTitleIPartAProgramAssociation](

	[StudentUSI] [int] NOT NULL,

	[ProgramTypeId] [int] NOT NULL,

	[EducationOrganizationId] [int] NOT NULL,

	[BeginDate] [date] NOT NULL,

	[TitleIPartAParticipantTypeId] [int] NOT NULL,

 CONSTRAINT [PK_StudentTitleIPartAProgramAssociation] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[ProgramTypeId] ASC,

	[EducationOrganizationId] ASC,

	[BeginDate] ASC,

	[TitleIPartAParticipantTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year on which the student first received services.

NEDM: Beginning Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentTitleIPartAProgramAssociation', @level2type=N'COLUMN',@level2name=N'BeginDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the type of Title I program, if any, in which the student is participating and served:

Public Targeted Assistance Program

Public Schoolwide Program

Private school student participating

Local Neglected Program

NEDM: Title I Participlant

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentTitleIPartAProgramAssociation', @level2type=N'COLUMN',@level2name=N'TitleIPartAParticipantTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This association represents the Title I Part A program(s) that a student participates in or receives services from.  The association is an extension of the StudentProgramAssociation particular for Title I part A programs.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentTitleIPartAProgramAssociation'

GO

/****** Object:  Table [edfi].[LocalEducationAgency]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[LocalEducationAgency](

	[LocalEducationAgencyId] [int] NOT NULL,

	[LEACategoryTypeId] [int] NOT NULL,

	[CharterStatusTypeId] [int] NULL,

	[EducationServiceCenterId] [int] NULL,

	[StateEducationAgencyId] [int] NULL,

	[ParentLocalEducationAgencyId] [int] NULL

 CONSTRAINT [PKLocalEducationAgency] PRIMARY KEY NONCLUSTERED 

(

	[LocalEducationAgencyId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'LocalEducationAgency Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'LocalEducationAgency', @level2type=N'COLUMN',@level2name=N'LocalEducationAgencyId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The category of LEA/district (e.g.,  Independent or Charter)



' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'LocalEducationAgency', @level2type=N'COLUMN',@level2name=N'LEACategoryTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A school or agency providing free public elementary or secondary education to eligible students under a specific charter granted by the state legislature or other appropriate authority and designated by such authority to be a charter school.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'LocalEducationAgency', @level2type=N'COLUMN',@level2name=N'CharterStatusTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This entity represents an administrative unit at the local level which exists primarily to operate schools or to contract for educational services.  It includes school districts, charter schools, charter management organizations, or other local administrative organizations.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'LocalEducationAgency'

GO

/****** Object:  Table [edfi].[ClassRanking]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[ClassRanking](

	[StudentUSI] [int] NOT NULL,

	[EducationOrganizationId] [int] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

	[TermTypeId] [int] NOT NULL,

	[ClassRank] [int] NOT NULL,

	[TotalNumberInClass] [int] NOT NULL,

	[PercentageRanking] [int] NOT NULL,

	[ClassRankingDate] [date] NOT NULL,

 CONSTRAINT [PK_ClassRanking] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[EducationOrganizationId] ASC,

	[SchoolYear] ASC,

	[TermTypeId] ASC,

	[ClassRank] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ClassRanking', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'EducationOrganization Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ClassRanking', @level2type=N'COLUMN',@level2name=N'EducationOrganizationId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The "as of" school year the student academic record.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ClassRanking', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ClassRanking', @level2type=N'COLUMN',@level2name=N'TermTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The academic rank of a student in relation to his or her graduating class (e.g., 1st, 2nd, 3rd).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ClassRanking', @level2type=N'COLUMN',@level2name=N'ClassRank'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The total number of students in the student''s graduating class.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ClassRanking', @level2type=N'COLUMN',@level2name=N'TotalNumberInClass'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The academic percentage rank of a student in relation to his or her graduating class (e.g., 95%, 80%, 50%).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ClassRanking', @level2type=N'COLUMN',@level2name=N'PercentageRanking'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Date class ranking was determined.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ClassRanking', @level2type=N'COLUMN',@level2name=N'ClassRankingDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'ClassRanking'

GO

/****** Object:  Table [edfi].[CourseTranscript]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CourseTranscript](

	[StudentUSI] [int] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

	[TermTypeId] [int] NOT NULL,

	[EducationOrganizationId] [int] NOT NULL,

	[IdentityCourseCode] [nvarchar](20) NOT NULL,

	[CourseAttemptResultTypeId] [int] NOT NULL,

	[AttemptedCreditTypeId] [int] NULL,

	[CreditsAttempted] [decimal](9, 2) NULL,

	[EarnedCreditTypeId] [int] NULL,

	[CreditsEarned] [decimal](9, 2) NOT NULL,

	[GradeLevelWhenTakenTypeId] [int] NULL,

	[MethodCreditEarnedTypeId] [int] NULL,

	[FinalLetterGrade] [nvarchar](20) NULL,

	[FinalNumericGrade] [int] NULL,

	[CourseRepeatCodeTypeId] [int] NULL,

 CONSTRAINT [PK_CourseTranscript] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[SchoolYear] ASC,

	[TermTypeId] ASC,

	[EducationOrganizationId] ASC,

	[IdentityCourseCode] ASC,

	[CourseAttemptResultTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseTranscript', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The "as of" school year the student academic record.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseTranscript', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseTranscript', @level2type=N'COLUMN',@level2name=N'TermTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'TThe actual code that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseTranscript', @level2type=N'COLUMN',@level2name=N'IdentityCourseCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The result from the student''s attempt to take the course, for example:

Pass

Fail

Incomplete

Withdrawn

Expelled

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseTranscript', @level2type=N'COLUMN',@level2name=N'CourseAttemptResultTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of credits attempted for a course.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseTranscript', @level2type=N'COLUMN',@level2name=N'CreditsAttempted'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of credits awarded or earned for the course.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseTranscript', @level2type=N'COLUMN',@level2name=N'CreditsEarned'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student''s grade level at time of course.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseTranscript', @level2type=N'COLUMN',@level2name=N'GradeLevelWhenTakenTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The method the credits were earned, for example:  Classroom, Examination, Transfer

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseTranscript', @level2type=N'COLUMN',@level2name=N'MethodCreditEarnedTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The final letter grade earned for the course.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseTranscript', @level2type=N'COLUMN',@level2name=N'FinalLetterGrade'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The final numeric grade earned for the course.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseTranscript', @level2type=N'COLUMN',@level2name=N'FinalNumericGrade'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Indicates that an academic course has been repeated by a student and how that repeat is to be computed in the student''s academic grade average.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseTranscript', @level2type=N'COLUMN',@level2name=N'CourseRepeatCodeTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The final record of a student''s performance in their courses.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseTranscript'

GO

/****** Object:  Table [edfi].[BellScheduleGradeLevels]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[BellScheduleGradeLevels](

	[SchoolId] [int] NOT NULL,

	[ClassPeriodName] [nvarchar](20) NOT NULL,

	[WeekNumber] [int] NOT NULL,

	[TermTypeId] [int] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

	[BellScheduleName] [nvarchar](60) NOT NULL,

	[GradeLevelTypeId] [int] NOT NULL,

 CONSTRAINT [PK_BellScheduleGradeLevels] PRIMARY KEY CLUSTERED 

(

	[SchoolId] ASC,

	[ClassPeriodName] ASC,

	[WeekNumber] ASC,

	[TermTypeId] ASC,

	[SchoolYear] ASC,

	[BellScheduleName] ASC,

	[GradeLevelTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the portion of a typical daily session in which students receive instruction in a specified subject (e.g., morning, sixth period, block period, or AB schedules). 

NEDM: Class Period

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'BellScheduleGradeLevels', @level2type=N'COLUMN',@level2name=N'ClassPeriodName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The week number (out of the number of weeks in the cycle) for this meeting time.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'BellScheduleGradeLevels', @level2type=N'COLUMN',@level2name=N'WeekNumber'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The identifier for the school year (e.g., 2010/11).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'BellScheduleGradeLevels', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

/****** Object:  Table [edfi].[BellScheduleCalendarDate]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[BellScheduleCalendarDate](

	[SchoolId] [int] NOT NULL,

	[ClassPeriodName] [nvarchar](20) NOT NULL,

	[WeekNumber] [int] NOT NULL,

	[TermTypeId] [int] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

	[BellScheduleName] [nvarchar](60) NOT NULL,

	[Date] [date] NOT NULL,

 CONSTRAINT [PK_BellScheduleCalendarDate] PRIMARY KEY CLUSTERED 

(

	[SchoolId] ASC,

	[ClassPeriodName] ASC,

	[WeekNumber] ASC,

	[TermTypeId] ASC,

	[SchoolYear] ASC,

	[BellScheduleName] ASC,

	[Date] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the portion of a typical daily session in which students receive instruction in a specified subject (e.g., morning, sixth period, block period, or AB schedules). 

NEDM: Class Period

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'BellScheduleCalendarDate', @level2type=N'COLUMN',@level2name=N'ClassPeriodName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The week number (out of the number of weeks in the cycle) for this meeting time.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'BellScheduleCalendarDate', @level2type=N'COLUMN',@level2name=N'WeekNumber'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The identifier for the school year (e.g., 2010/11).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'BellScheduleCalendarDate', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

/****** Object:  Table [edfi].[StudentObjectiveAssessmentScoreResults]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentObjectiveAssessmentScoreResults](

	[StudentUSI] [int] NOT NULL,

	[ObjectiveItem] [int] NOT NULL,

	[AssessmentTitle] [nvarchar](60) NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

	[AssessedGradeLevelTypeId] [int] NOT NULL,

	[Version] [int] NOT NULL,

	[AdministrationDate] [date] NOT NULL,

	[AssessmentReportingMethodTypeId] [int] NOT NULL,

	[Result] [nvarchar](35) NOT NULL,

 CONSTRAINT [PK_StudentObjectiveAssessmentScoreResults] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[ObjectiveItem] ASC,

	[AssessmentTitle] ASC,

	[AcademicSubjectTypeId] ASC,

	[AssessedGradeLevelTypeId] ASC,

	[Version] ASC,

	[AdministrationDate] ASC,

	[AssessmentReportingMethodTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentObjectiveAssessmentScoreResults', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The title or name of the assessment.

NEDM: Assessment Title

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentObjectiveAssessmentScoreResults', @level2type=N'COLUMN',@level2name=N'AssessmentTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

NEDM: Assessment Content, Academic Subject

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentObjectiveAssessmentScoreResults', @level2type=N'COLUMN',@level2name=N'AcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The typical grade level for which an assessment is designed. If the test assessment spans a range of grades, then this attribute holds the highest grade assessed.  If only one grade level is assessed, then only this attribute is used. For example:

Adult

Prekindergarten

First grade

Second grade

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentObjectiveAssessmentScoreResults', @level2type=N'COLUMN',@level2name=N'AssessedGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The version identifier for the test assessment.

NEDM: Assessment Version

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentObjectiveAssessmentScoreResults', @level2type=N'COLUMN',@level2name=N'Version'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month(s), day(s), and year on which an assessment is administered or first day of administration if over multiple days.

NEDM: Assessment Administration Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentObjectiveAssessmentScoreResults', @level2type=N'COLUMN',@level2name=N'AdministrationDate'

GO

/****** Object:  Table [edfi].[StudentObjectiveAssessmentPerformanceLevel]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentObjectiveAssessmentPerformanceLevel](

	[StudentUSI] [int] NOT NULL,

	[ObjectiveItem] [int] NOT NULL,

	[AssessmentTitle] [nvarchar](60) NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

	[AssessedGradeLevelTypeId] [int] NOT NULL,

	[Version] [int] NOT NULL,

	[AdministrationDate] [date] NOT NULL,

	[PerformanceLevelDescriptorId] [int] NOT NULL,

	[PerformanceLevelMet] [bit] NOT NULL,

 CONSTRAINT [PK_StudentObjectiveAssessmentPerformanceLevel] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[ObjectiveItem] ASC,

	[AssessmentTitle] ASC,

	[AcademicSubjectTypeId] ASC,

	[AssessedGradeLevelTypeId] ASC,

	[Version] ASC,

	[AdministrationDate] ASC,

	[PerformanceLevelDescriptorId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentObjectiveAssessmentPerformanceLevel', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The title or name of the assessment.

NEDM: Assessment Title

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentObjectiveAssessmentPerformanceLevel', @level2type=N'COLUMN',@level2name=N'AssessmentTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

NEDM: Assessment Content, Academic Subject

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentObjectiveAssessmentPerformanceLevel', @level2type=N'COLUMN',@level2name=N'AcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The typical grade level for which an assessment is designed. If the test assessment spans a range of grades, then this attribute holds the highest grade assessed.  If only one grade level is assessed, then only this attribute is used. For example:

Adult

Prekindergarten

First grade

Second grade

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentObjectiveAssessmentPerformanceLevel', @level2type=N'COLUMN',@level2name=N'AssessedGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The version identifier for the test assessment.

NEDM: Assessment Version

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentObjectiveAssessmentPerformanceLevel', @level2type=N'COLUMN',@level2name=N'Version'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month(s), day(s), and year on which an assessment is administered or first day of administration if over multiple days.

NEDM: Assessment Administration Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentObjectiveAssessmentPerformanceLevel', @level2type=N'COLUMN',@level2name=N'AdministrationDate'

GO

/****** Object:  Table [edfi].[StudentCTEProgramAssociation]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentCTEProgramAssociation](

	[StudentUSI] [int] NOT NULL,

	[ProgramTypeId] [int] NOT NULL,

	[EducationOrganizationId] [int] NOT NULL,

	[BeginDate] [date] NOT NULL,

	[CareerPathwayTypeId] [int] NOT NULL,

	[CIPCode] [nvarchar](120) NULL,

	[PrimaryCTEProgramIndicator] [bit] NULL,

	[CTEProgramCompletionIndicator] [bit] NULL,

 CONSTRAINT [PK_StudentCTEProgramAssociation] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[ProgramTypeId] ASC,

	[EducationOrganizationId] ASC,

	[BeginDate] ASC,

	[CareerPathwayTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year on which the student first received services.

NEDM: Beginning Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCTEProgramAssociation', @level2type=N'COLUMN',@level2name=N'BeginDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The career cluster representing the career path of the Vocational/Career Tech concentrator.

NEDM: Career Cluster



' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCTEProgramAssociation', @level2type=N'COLUMN',@level2name=N'CareerPathwayTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This association represents the career and technical education (CTE) program that a student participates in.  The association is an extension of the StudentProgramAssociation particular for CTE programs.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCTEProgramAssociation'

GO

/****** Object:  Table [edfi].[StudentProgramAssociationService]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentProgramAssociationService](

	[StudentUSI] [int] NOT NULL,

	[ProgramTypeId] [int] NOT NULL,

	[EducationOrganizationId] [int] NOT NULL,

	[BeginDate] [date] NOT NULL,

	[ServiceDescriptorId] [int] NOT NULL,

 CONSTRAINT [PK_StudentProgramAssociationService] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[ProgramTypeId] ASC,

	[EducationOrganizationId] ASC,

	[BeginDate] ASC,

	[ServiceDescriptorId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year on which the student first received services.

NEDM: Beginning Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentProgramAssociationService', @level2type=N'COLUMN',@level2name=N'BeginDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This association represents the supplemental program(s) that a student participates in or receives services from.  The association is an extension of the StudentProgramAssociation particular for supplemental services.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentProgramAssociationService'

GO

/****** Object:  Table [edfi].[StudentSpecialEdProgramAssociation]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentSpecialEdProgramAssociation](

	[StudentUSI] [int] NOT NULL,

	[ProgramTypeId] [int] NOT NULL,

	[EducationOrganizationId] [int] NOT NULL,

	[BeginDate] [date] NOT NULL,

	[IdeaEligibilityTypeId] [int] NOT NULL,

	[EducationalEnvironmentTypeId] [int] NOT NULL,

	[MultiplyDisabled] [bit] NULL,

	[MedicallyFragile] [bit] NULL,

	[SpecialEducationHoursPerWeek] [decimal](5, 2) NULL,

	[LastEvaluationDate] [date] NULL,

	[IEPReviewDate] [date] NULL,

	[IEPBeginDate] [date] NULL,

	[IEPEndDate] [date] NULL,

 CONSTRAINT [PK_StudentSpecialEdProgramAssociation] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[ProgramTypeId] ASC,

	[EducationOrganizationId] ASC,

	[BeginDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year on which the student first received services.

NEDM: Beginning Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSpecialEdProgramAssociation', @level2type=N'COLUMN',@level2name=N'BeginDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Indicator of the eligibility of the student to receive special education services according to the Individuals with Disabilities Education Act (IDEA).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSpecialEdProgramAssociation', @level2type=N'COLUMN',@level2name=N'IdeaEligibilityTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The setting in which a child receives special education and related services.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSpecialEdProgramAssociation', @level2type=N'COLUMN',@level2name=N'EducationalEnvironmentTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Indicates whether the student receiving special education and related services has been designated as multiply disabled by the admission, review, and dismissal committee. [See 19 TAC §89.1040(c)(6).]

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSpecialEdProgramAssociation', @level2type=N'COLUMN',@level2name=N'MultiplyDisabled'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Indicates whether the student receiving special education and related services is:

1) in the age range of birth to 22 years, and

2) has a serious, ongoing illness or a chronic condition that has lasted or is anticipated to last at least 12 or more months or has required at least one month of hospitalization, and that requires daily, ongoing medical treatments and monitoring by appropriately trained personnel which may include parents or other family members, and

3) requires the routine use of medical device or of assistive technology to compensate for the loss of usefulness of a body function needed to participate in activities of daily living, and

4) lives with ongoing threat to his or her continued well-being.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSpecialEdProgramAssociation', @level2type=N'COLUMN',@level2name=N'MedicallyFragile'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This association represents the special education program(s) that a student participates in or receives services from.  The association is an extension of the StudentProgramAssociation particular for special education programs.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSpecialEdProgramAssociation'

GO

/****** Object:  Table [edfi].[School]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[School](

	[SchoolId] [int] NOT NULL,

	[LocalEducationAgencyId] [int] NULL,

	[SchoolTypeId] [int] NULL,

	[CharterStatusTypeId] [int] NULL,

	[TitleIPartASchoolDesignationTypeId] [int] NULL,

	[MagnetSpecialProgramEmphasisSchoolTypeId] [int] NULL,

	[AdministrativeFundingControlTypeId] [int] NULL,

 CONSTRAINT [PKSchool] PRIMARY KEY NONCLUSTERED 

(

	[SchoolId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'School', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The instructional categorization of the school (e.g., Regular, Alternative, etc.)



' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'School', @level2type=N'COLUMN',@level2name=N'SchoolTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A school or agency providing free public elementary or secondary education to eligible students under a specific charter granted by the state legislature or other appropriate authority and designated by such authority to be a charter school.



' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'School', @level2type=N'COLUMN',@level2name=N'CharterStatusTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Denotes the Title I Part A designation for the school.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'School', @level2type=N'COLUMN',@level2name=N'TitleIPartASchoolDesignationTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A school that has been designed: 1) to attract students of different racial/ethnic backgrounds for the purpose of reducing, preventing, or eliminating racial isolation; and/or 2)to provide an academic or social focus on a particular theme (e.g., science/math, performing arts, gifted/talented, or foreign language).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'School', @level2type=N'COLUMN',@level2name=N'MagnetSpecialProgramEmphasisSchoolTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The type of education institution as classified by its funding source.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'School', @level2type=N'COLUMN',@level2name=N'AdministrativeFundingControlTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This entity represents an educational organization that includes staff and students who participate in classes and educational activity groups.

NEDM: School

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'School'

GO

/****** Object:  Table [edfi].[AdditionalCredit]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AdditionalCredit](

	[StudentUSI] [int] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

	[TermTypeId] [int] NOT NULL,

	[EducationOrganizationId] [int] NOT NULL,

	[IdentityCourseCode] [nvarchar](20) NOT NULL,

	[CourseAttemptResultTypeId] [int] NOT NULL,

	[CreditTypeId] [int] NOT NULL,

	[CreditsEarned] [decimal](9, 2) NOT NULL,

 CONSTRAINT [PK_AdditionalCredit] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[SchoolYear] ASC,

	[TermTypeId] ASC,

	[EducationOrganizationId] ASC,

	[IdentityCourseCode] ASC,

	[CourseAttemptResultTypeId] ASC,

	[CreditTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AdditionalCredit', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The "as of" school year the student academic record.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AdditionalCredit', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AdditionalCredit', @level2type=N'COLUMN',@level2name=N'TermTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'TThe actual code that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AdditionalCredit', @level2type=N'COLUMN',@level2name=N'IdentityCourseCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The result from the student''s attempt to take the course, for example:

Pass

Fail

Incomplete

Withdrawn

Expelled

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AdditionalCredit', @level2type=N'COLUMN',@level2name=N'CourseAttemptResultTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of credits awarded or earned for the course.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AdditionalCredit', @level2type=N'COLUMN',@level2name=N'CreditTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of credits awarded or earned for the course.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AdditionalCredit', @level2type=N'COLUMN',@level2name=N'CreditsEarned'

GO

/****** Object:  Table [edfi].[Diploma]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[Diploma](

	[StudentUSI] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[DiplomaTypeId] [int] NOT NULL,

	[DiplomaAwardDate] [date] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

	[TermTypeId] [int] NOT NULL,

	[DiplomaLevelTypeId] [int] NULL,

	[CTECompleter] [bit] NULL,

	[AcademicHonors] [int] NOT NULL,

	[Recognitions] [int] NOT NULL,

 CONSTRAINT [PK_Diploma] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[SchoolId] ASC,

	[DiplomaTypeId] ASC,

	[DiplomaAwardDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Diploma', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Diploma', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The type of diploma/credential that is awarded to a student in recognition of his/her completion of the curricular requirements.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Diploma', @level2type=N'COLUMN',@level2name=N'DiplomaTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year on which the student met  graduation requirements and was awarded a diploma.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Diploma', @level2type=N'COLUMN',@level2name=N'DiplomaAwardDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The "as of" school year the student academic record.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Diploma', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Diploma', @level2type=N'COLUMN',@level2name=N'TermTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The type of diploma/credential that is awarded to a student in recognition of his/her completion of the curricular requirements.

Minimum high school program

Recommended high school program

Distinguished Achievement Program



' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Diploma', @level2type=N'COLUMN',@level2name=N'DiplomaLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Indicated a student who reached a state&ndash;defined threshold of vocational education and who attained a high school diploma or its recognized state equivalent or GED.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Diploma', @level2type=N'COLUMN',@level2name=N'CTECompleter'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Academic distinctions earned by or awarded to the student.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Diploma', @level2type=N'COLUMN',@level2name=N'AcademicHonors'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Recognitions given to the student for accomplishments in a co-curricular, or extra-curricular activity.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Diploma', @level2type=N'COLUMN',@level2name=N'Recognitions'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This educational entity represents the conferring or certification by an educational organization that the student has successfully completed a particular course of study.  It represents the electronic version of its physical document counterpart.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Diploma'

GO

/****** Object:  Table [edfi].[CourseOffering]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[CourseOffering](

	[SchoolId] [int] NOT NULL,

	[TermTypeId] [int] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

	[LocalCourseCode] [nvarchar](20) NOT NULL,

	[LocalCourseTitle] [nvarchar](60) NULL,

	[IdentityCourseCode] [nvarchar](20) NOT NULL,

 CONSTRAINT [PK_CourseOffering] PRIMARY KEY CLUSTERED 

(

	[SchoolId] ASC,

	[TermTypeId] ASC,

	[SchoolYear] ASC,

	[LocalCourseCode] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseOffering', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The identifier for the school year (e.g., 2010/11).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseOffering', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The local code assigned by the LEA or Campus that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseOffering', @level2type=N'COLUMN',@level2name=N'LocalCourseCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'TThe actual code that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'CourseOffering', @level2type=N'COLUMN',@level2name=N'IdentityCourseCode'

GO

/****** Object:  Table [edfi].[Location]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[Location](

	[SchoolId] [int] NOT NULL,

	[ClassroomIdentificationCode] [nvarchar](20) NOT NULL,

	[MaximumNumberOfSeats] [int] NULL,

	[OptimalNumberOfSeats] [int] NULL,

 CONSTRAINT [PKLocation] PRIMARY KEY NONCLUSTERED 

(

	[SchoolId] ASC,

	[ClassroomIdentificationCode] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Location Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Location', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A unique number or alphanumeric code assigned to a room by a school, school system, state, or other agency or entity.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Location', @level2type=N'COLUMN',@level2name=N'ClassroomIdentificationCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The most number of seats the class can maintain.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Location', @level2type=N'COLUMN',@level2name=N'MaximumNumberOfSeats'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The number of seats that is most favorable to the class.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Location', @level2type=N'COLUMN',@level2name=N'OptimalNumberOfSeats'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This entity represents the physical space where students gather for a particular class/section.  The location may be an indoor or outdoor area designated for the purpose of meeting the educational needs of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Location'

GO

/****** Object:  Table [edfi].[FeederSchoolAssociation]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[FeederSchoolAssociation](

	[SchoolId] [int] NOT NULL,

	[FeederSchoolId] [int] NOT NULL,

	[BeginDate] [date] NOT NULL,

	[EndDate] [date] NULL,

	[FeederRelationshipDescription] [nvarchar](1024) NULL,

 CONSTRAINT [PK_FeederSchoolAssociation] PRIMARY KEY CLUSTERED 

(

	[SchoolId] ASC,

	[FeederSchoolId] ASC,

	[BeginDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'FeederSchoolAssociation', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'FeederSchoolAssociation', @level2type=N'COLUMN',@level2name=N'FeederSchoolId'

GO

/****** Object:  Table [edfi].[DisciplineIncident]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[DisciplineIncident](

	[SchoolId] [int] NOT NULL,

	[IncidentIdentifier] [nvarchar](20) NOT NULL,

	[IncidentDate] [date] NOT NULL,

	[IncidentTime] [time](7) NULL,

	[IncidentLocationTypeId] [int] NOT NULL,

	[ReporterDescriptionTypeId] [int] NULL,

	[ReporterName] [nvarchar](75) NULL,

	[ReportedToLawEnforcement] [bit] NULL,

	[CaseNumber] [nvarchar](20) NULL,

	[StaffUSI] [int] NULL,

 CONSTRAINT [PK_DisciplineIncident] PRIMARY KEY CLUSTERED 

(

	[SchoolId] ASC,

	[IncidentIdentifier] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineIncident', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A locally assigned unique identifier (within the school or school district) to identify each specific incident or occurrence. The same identifier should be used to document the entire incident even if it included multiple offenses and multiple offenders.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineIncident', @level2type=N'COLUMN',@level2name=N'IncidentIdentifier'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year on which the incident occurred. 

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineIncident', @level2type=N'COLUMN',@level2name=N'IncidentDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the time of day the incident took place.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineIncident', @level2type=N'COLUMN',@level2name=N'IncidentTime'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Identifies where the incident occurred and whether or not it occurred on campus, for example:

On campus

Administrative offices area

Cafeteria area

Classroom

Hallway or stairs

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineIncident', @level2type=N'COLUMN',@level2name=N'IncidentLocationTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Information on the type of individual who reported the incident. When known and/or if useful, use a more specific option code (e.g., "Counselor" rather than "Professional Staff"); for example:Student

Parent/guardian

Law enforcement officer

Nonschool personnel

Representative of visiting school

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineIncident', @level2type=N'COLUMN',@level2name=N'ReporterDescriptionTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Identifies the reporter of the incident by name.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineIncident', @level2type=N'COLUMN',@level2name=N'ReporterName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Indicator of whether the incident was reported to law enforcement.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineIncident', @level2type=N'COLUMN',@level2name=N'ReportedToLawEnforcement'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The case number assigned to the incident by law enforcement or other organization.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineIncident', @level2type=N'COLUMN',@level2name=N'CaseNumber'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Staff Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineIncident', @level2type=N'COLUMN',@level2name=N'StaffUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This event entity represents an occurrence of an infraction ranging from a minor problem behavior that disrupts the orderly functioning of a school or classroom (such as tardiness) to a criminal act that results in the involvement of a law enforcement official (such as robbery). A single event (e.g., a fight) is one incident regardless of how many perpetrators or victims are involved.  Discipline incidents are events classified as warranting discipline action.

NEDM: Discipline Incident

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineIncident'

GO

/****** Object:  Table [edfi].[SchoolGradesOffered]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[SchoolGradesOffered](

	[SchoolId] [int] NOT NULL,

	[GradesOfferedTypeId] [int] NOT NULL,

 CONSTRAINT [PK_SchoolGradesOffered] PRIMARY KEY CLUSTERED 

(

	[SchoolId] ASC,

	[GradesOfferedTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[RestraintEvent]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[RestraintEvent](

	[StudentUSI] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[RestraintEventIdentifier] [nvarchar](20) NOT NULL,

	[EventDate] [date] NOT NULL,

	[EducationalEnvironmentTypeId] [int] NULL,

 CONSTRAINT [PK_RestraintEvent] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[SchoolId] ASC,

	[RestraintEventIdentifier] ASC,

	[EventDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'RestraintEvent', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'RestraintEvent', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A unique number or alphanumeric code assigned to a restraint event by a school, school system, a state, or other agency or entity.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'RestraintEvent', @level2type=N'COLUMN',@level2name=N'RestraintEventIdentifier'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Month, day and year of the resteraint event.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'RestraintEvent', @level2type=N'COLUMN',@level2name=N'EventDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The setting where the restrint was exercised..

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'RestraintEvent', @level2type=N'COLUMN',@level2name=N'EducationalEnvironmentTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This event entity represents the instances where a special education student was physically or mechanically restrained due to imminent serious physical harm to themselves or others, imminent serious property destruction, or a combination of both imminent serious physical harm to themselves or others and imminent serious property destruction.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'RestraintEvent'

GO

/****** Object:  Table [edfi].[SchoolCategory]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[SchoolCategory](

	[SchoolId] [int] NOT NULL,

	[SchoolCategoryTypeId] [int] NOT NULL,

 CONSTRAINT [PK_SchoolCategory] PRIMARY KEY CLUSTERED 

(

	[SchoolId] ASC,

	[SchoolCategoryTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[StudentSchoolAssociation]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentSchoolAssociation](

	[StudentUSI] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[EntryDate] [date] NOT NULL,

	[EntryGradeLevelTypeId] [int] NOT NULL,

	[EntryTypeId] [int] NULL,

	[RepeatGradeIndicator] [bit] NULL,

	[ClassOf] [int] NULL,

	[SchoolChoiceTransfer] [bit] NULL,

	[ExitWithdrawDate] [date] NULL,

	[ExitWithdrawTypeId] [int] NULL,

 CONSTRAINT [PK_StudentSchoolAssociation] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[SchoolId] ASC,

	[EntryDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSchoolAssociation', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSchoolAssociation', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year on which an individual enters and begins to receive instructional services in a campus.

NEDM: School Entry Date

NEDM: LEA Entry Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSchoolAssociation', @level2type=N'COLUMN',@level2name=N'EntryDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The grade level or primary instructional level at which a student enters and receives services in a school or an educational institution during a given academic session.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSchoolAssociation', @level2type=N'COLUMN',@level2name=N'EntryGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The process by which a student enters a school during a given academic session.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSchoolAssociation', @level2type=N'COLUMN',@level2name=N'EntryTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of whether students transferred in or out of the school did so during the school year under the provisions for public school choice in accordance with Title I, Part A, Section 1116.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSchoolAssociation', @level2type=N'COLUMN',@level2name=N'SchoolChoiceTransfer'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year of the first day after the date of an individual''s last attendance at a campus (if known), the day on which an individual graduated, or the date on which it becomes known officially that an individual left school.

NEDM: Exit/Withdraw Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSchoolAssociation', @level2type=N'COLUMN',@level2name=N'ExitWithdrawDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The circumstances under which the student exited from membership in an educational institution.

NEDM: Exit Withdraw Type

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSchoolAssociation', @level2type=N'COLUMN',@level2name=N'ExitWithdrawTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This association represents the school to which a student is enrolled.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSchoolAssociation'

GO

/****** Object:  Table [edfi].[TeacherSchoolAssociation]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[TeacherSchoolAssociation](

	[StaffUSI] [int] NOT NULL,

	[ProgramAssignmentTypeId] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

 CONSTRAINT [PK_TeacherSchoolAssociation] PRIMARY KEY CLUSTERED 

(

	[StaffUSI] ASC,

	[ProgramAssignmentTypeId] ASC,

	[SchoolId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Staff Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'TeacherSchoolAssociation', @level2type=N'COLUMN',@level2name=N'StaffUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the program for which the individual is assigned; for example:

Regular education

Title I-Academic

Title I-Non-Academic

Special Education

Bilingual/English as a Second Language

NEDM: Program Assignment



' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'TeacherSchoolAssociation', @level2type=N'COLUMN',@level2name=N'ProgramAssignmentTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'TeacherSchoolAssociation', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This association indicates the school(s) to which a teacher provides instructional services to.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'TeacherSchoolAssociation'

GO

/****** Object:  Table [edfi].[TeacherSchoolAssociationInstructionalGradeLevels]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[TeacherSchoolAssociationInstructionalGradeLevels](

	[StaffUSI] [int] NOT NULL,

	[ProgramAssignmentTypeId] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[InstructionalGradeLevelTypeId] [int] NOT NULL,

 CONSTRAINT [PK_TeacherSchoolAssociationInstructionalGradeLevels] PRIMARY KEY CLUSTERED 

(

	[StaffUSI] ASC,

	[ProgramAssignmentTypeId] ASC,

	[SchoolId] ASC,

	[InstructionalGradeLevelTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Staff Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'TeacherSchoolAssociationInstructionalGradeLevels', @level2type=N'COLUMN',@level2name=N'StaffUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the program for which the individual is assigned; for example:

Regular education

Title I-Academic

Title I-Non-Academic

Special Education

Bilingual/English as a Second Language

NEDM: Program Assignment



' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'TeacherSchoolAssociationInstructionalGradeLevels', @level2type=N'COLUMN',@level2name=N'ProgramAssignmentTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'TeacherSchoolAssociationInstructionalGradeLevels', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

/****** Object:  Table [edfi].[TeacherSchoolAssociationAcademicSubjects]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[TeacherSchoolAssociationAcademicSubjects](

	[StaffUSI] [int] NOT NULL,

	[ProgramAssignmentTypeId] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

 CONSTRAINT [PK_TeacherSchoolAssociationAcademicSubjects] PRIMARY KEY CLUSTERED 

(

	[StaffUSI] ASC,

	[ProgramAssignmentTypeId] ASC,

	[SchoolId] ASC,

	[AcademicSubjectTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Staff Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'TeacherSchoolAssociationAcademicSubjects', @level2type=N'COLUMN',@level2name=N'StaffUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the program for which the individual is assigned; for example:

Regular education

Title I-Academic

Title I-Non-Academic

Special Education

Bilingual/English as a Second Language

NEDM: Program Assignment



' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'TeacherSchoolAssociationAcademicSubjects', @level2type=N'COLUMN',@level2name=N'ProgramAssignmentTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'TeacherSchoolAssociationAcademicSubjects', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

/****** Object:  Table [edfi].[StudentDisciplineIncidentAssociation]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentDisciplineIncidentAssociation](

	[StudentUSI] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[IncidentIdentifier] [nvarchar](20) NOT NULL,

	[StudentParticipationCodeTypeId] [int] NOT NULL,

 CONSTRAINT [PK_StudentDisciplineIncidentAssociation] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[SchoolId] ASC,

	[IncidentIdentifier] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentDisciplineIncidentAssociation', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentDisciplineIncidentAssociation', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A locally assigned unique identifier (within the school or school district) to identify each specific incident or occurrence. The same identifier should be used to document the entire incident even if it included multiple offenses and multiple offenders.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentDisciplineIncidentAssociation', @level2type=N'COLUMN',@level2name=N'IncidentIdentifier'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The role or type of participation of a student in a discipline incident; for example:

Victim

Perpetrator

Witness

Reporter

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentDisciplineIncidentAssociation', @level2type=N'COLUMN',@level2name=N'StudentParticipationCodeTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This association indicates those students related to a discipline incident who were victims, perpetrators, witnesses, and/or reporters.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentDisciplineIncidentAssociation'

GO

/****** Object:  Table [edfi].[StudentSchoolAssociationGraduationPlan]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentSchoolAssociationGraduationPlan](

	[GraduationPlanTypeId] [int] NOT NULL,

	[StudentUSI] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[EntryDate] [date] NOT NULL,

 CONSTRAINT [PK_StudentSchoolAssociationGraduationPlan] PRIMARY KEY CLUSTERED 

(

	[GraduationPlanTypeId] ASC,

	[StudentUSI] ASC,

	[SchoolId] ASC,

	[EntryDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSchoolAssociationGraduationPlan', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSchoolAssociationGraduationPlan', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year on which an individual enters and begins to receive instructional services in a campus.

NEDM: School Entry Date

NEDM: LEA Entry Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSchoolAssociationGraduationPlan', @level2type=N'COLUMN',@level2name=N'EntryDate'

GO

/****** Object:  Table [edfi].[StudentSchoolAssociationEducationPlans]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentSchoolAssociationEducationPlans](

	[StudentUSI] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[EntryDate] [date] NOT NULL,

	[EducationPlansTypeId] [int] NOT NULL,

 CONSTRAINT [PK_StudentSchoolAssociationEducationPlans] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[SchoolId] ASC,

	[EntryDate] ASC,

	[EducationPlansTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSchoolAssociationEducationPlans', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSchoolAssociationEducationPlans', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year on which an individual enters and begins to receive instructional services in a campus.

NEDM: School Entry Date

NEDM: LEA Entry Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSchoolAssociationEducationPlans', @level2type=N'COLUMN',@level2name=N'EntryDate'

GO

/****** Object:  Table [edfi].[RestraintEventReason]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[RestraintEventReason](

	[StudentUSI] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[RestraintEventIdentifier] [nvarchar](20) NOT NULL,

	[EventDate] [date] NOT NULL,

	[RestraintEventReasonsTypeId] [int] NOT NULL,

 CONSTRAINT [PK_RestraintEventReason] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[SchoolId] ASC,

	[RestraintEventIdentifier] ASC,

	[EventDate] ASC,

	[RestraintEventReasonsTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'RestraintEventReason', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'RestraintEventReason', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A unique number or alphanumeric code assigned to a restraint event by a school, school system, a state, or other agency or entity.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'RestraintEventReason', @level2type=N'COLUMN',@level2name=N'RestraintEventIdentifier'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Month, day and year of the resteraint event.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'RestraintEventReason', @level2type=N'COLUMN',@level2name=N'EventDate'

GO

/****** Object:  Table [edfi].[RestraintEventProgram]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[RestraintEventProgram](

	[StudentUSI] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[RestraintEventIdentifier] [nvarchar](20) NOT NULL,

	[EventDate] [date] NOT NULL,

	[ProgramTypeId] [int] NOT NULL,

	[BeginDate] [date] NOT NULL,

 CONSTRAINT [PK_RestraintEventProgram] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[SchoolId] ASC,

	[RestraintEventIdentifier] ASC,

	[EventDate] ASC,

	[ProgramTypeId] ASC,

	[BeginDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'RestraintEventProgram', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'RestraintEventProgram', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A unique number or alphanumeric code assigned to a restraint event by a school, school system, a state, or other agency or entity.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'RestraintEventProgram', @level2type=N'COLUMN',@level2name=N'RestraintEventIdentifier'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Month, day and year of the resteraint event.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'RestraintEventProgram', @level2type=N'COLUMN',@level2name=N'EventDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year on which the student first received services.

NEDM: Beginning Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'RestraintEventProgram', @level2type=N'COLUMN',@level2name=N'BeginDate'

GO

/****** Object:  Table [edfi].[Recognition]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[Recognition](

	[StudentUSI] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[DiplomaTypeId] [int] NOT NULL,

	[DiplomaAwardDate] [date] NOT NULL,

	[RecognitionTypeId] [int] NOT NULL,

	[RecognitionAwardDate] [date] NULL,

	[RecognitionDescription] [nvarchar](80) NULL,

	[SchoolYear] [smallint] NULL,

	[TermTypeId] [int] NULL,

 CONSTRAINT [PK_Recognition] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[SchoolId] ASC,

	[DiplomaTypeId] ASC,

	[DiplomaAwardDate] ASC,

	[RecognitionTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Recognition', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Recognition', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The type of diploma/credential that is awarded to a student in recognition of his/her completion of the curricular requirements.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Recognition', @level2type=N'COLUMN',@level2name=N'DiplomaTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year on which the student met  graduation requirements and was awarded a diploma.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Recognition', @level2type=N'COLUMN',@level2name=N'DiplomaAwardDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The "as of" school year the student academic record.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Recognition', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Recognition', @level2type=N'COLUMN',@level2name=N'TermTypeId'

GO

/****** Object:  Table [edfi].[Section]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[Section](

	[SchoolId] [int] NOT NULL,

	[ClassPeriodName] [nvarchar](20) NOT NULL,

	[ClassroomIdentificationCode] [nvarchar](20) NOT NULL,

	[LocalCourseCode] [nvarchar](20) NOT NULL,

	[TermTypeId] [int] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

	[UniqueSectionCode] [nvarchar](30) NOT NULL,

	[SequenceOfCourse] [int] NOT NULL,

	[EducationalEnvironmentTypeId] [int] NULL,

	[MediumOfInstructionTypeId] [int] NULL,

	[PopulationServedTypeId] [int] NULL,

	[AvailableCreditTypeId] [int] NULL,

	[AvailableCredit] [decimal](18, 0) NULL,

 CONSTRAINT [PK_Section] PRIMARY KEY CLUSTERED 

(

	[SchoolId] ASC,

	[ClassPeriodName] ASC,

	[ClassroomIdentificationCode] ASC,

	[LocalCourseCode] ASC,

	[TermTypeId] ASC,

	[SchoolYear] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Section', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the portion of a typical daily session in which students receive instruction in a specified subject (e.g., morning, sixth period, block period, or AB schedules). 

NEDM: Class Period

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Section', @level2type=N'COLUMN',@level2name=N'ClassPeriodName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A unique number or alphanumeric code assigned to a room by a school, school system, state, or other agency or entity.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Section', @level2type=N'COLUMN',@level2name=N'ClassroomIdentificationCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The local code assigned by the LEA or Campus that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Section', @level2type=N'COLUMN',@level2name=N'LocalCourseCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the term in which the section is offered (e.g., First semester, Second semester, Year long, summer school)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Section', @level2type=N'COLUMN',@level2name=N'TermTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The identifier for the school year (e.g., 2010/11).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Section', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A unique identifier for the section, that is defined for a campus by the classroom, the subjects taught, and the instructors that are assigned.

NEDM: Unique Course Code

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Section', @level2type=N'COLUMN',@level2name=N'UniqueSectionCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'When a section is part of a sequence of parts for a course, the number if the sequence.  If the course has only onle part, the value of this section attribute should be 1.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Section', @level2type=N'COLUMN',@level2name=N'SequenceOfCourse'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The setting in which a child receives education and related services; for example:

Center-based instruction

Home-based instruction

Hospital class

Mainstream

Residential care and treatment facility

....



' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Section', @level2type=N'COLUMN',@level2name=N'EducationalEnvironmentTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The media through which teachers provide instruction to students and students and teachers communicate about instructional matters; for example:

Technology-based instruction in classroom

Correspondence instruction

Face-to-face instruction

Virtual/On-line Distance learning

Center-based instruction

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Section', @level2type=N'COLUMN',@level2name=N'MediumOfInstructionTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The population for which the course was designed; for example:

Bilingual students

Remedial education students

Gifted and talented students

Career and Technical Education students

Special education students

....

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Section', @level2type=N'COLUMN',@level2name=N'PopulationServedTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This educational entity represents a setting in which organized instruction of course content is provided to one or more students for a given period of time.  A course may be offered to more than one class/section. Instruction, provided by one or more teachers or other staff members, may be delivered in person or via a different medium. 

NEDM: Section

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Section'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The type of credits or units of value awarded for the completion of a course.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Section', @level2type=N'COLUMN',@level2name=N'AvailableCreditTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Credits or units of value awarded for the completion of a course.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Section', @level2type=N'COLUMN',@level2name=N'AvailableCredit'

GO

/****** Object:  Table [edfi].[DisciplineIncidentWeapons]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[DisciplineIncidentWeapons](

	[SchoolId] [int] NOT NULL,

	[IncidentIdentifier] [nvarchar](20) NOT NULL,

	[WeaponsTypeId] [int] NOT NULL,

 CONSTRAINT [PK_DisciplineIncidentWeapons] PRIMARY KEY CLUSTERED 

(

	[SchoolId] ASC,

	[IncidentIdentifier] ASC,

	[WeaponsTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineIncidentWeapons', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A locally assigned unique identifier (within the school or school district) to identify each specific incident or occurrence. The same identifier should be used to document the entire incident even if it included multiple offenses and multiple offenders.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineIncidentWeapons', @level2type=N'COLUMN',@level2name=N'IncidentIdentifier'

GO

/****** Object:  Table [edfi].[DisciplineIncidentSecondaryBehavior]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[DisciplineIncidentSecondaryBehavior](

	[SchoolId] [int] NOT NULL,

	[IncidentIdentifier] [nvarchar](20) NOT NULL,

	[BehaviorCategoryTypeId] [int] NOT NULL,

	[SecondaryBehavior] [nvarchar](50) NOT NULL,

 CONSTRAINT [PK_DisciplineIncidentSecondaryBehavior] PRIMARY KEY CLUSTERED 

(

	[SchoolId] ASC,

	[IncidentIdentifier] ASC,

	[BehaviorCategoryTypeId] ASC,

	[SecondaryBehavior] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineIncidentSecondaryBehavior', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A locally assigned unique identifier (within the school or school district) to identify each specific incident or occurrence. The same identifier should be used to document the entire incident even if it included multiple offenses and multiple offenders.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineIncidentSecondaryBehavior', @level2type=N'COLUMN',@level2name=N'IncidentIdentifier'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The category of the incident behavior for classification purposes,

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineIncidentSecondaryBehavior', @level2type=N'COLUMN',@level2name=N'BehaviorCategoryTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the behavior of a student for a discipline incident.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineIncidentSecondaryBehavior', @level2type=N'COLUMN',@level2name=N'SecondaryBehavior'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Definition of beahior coded for describing an incident.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineIncidentSecondaryBehavior'

GO

/****** Object:  Table [edfi].[DisciplineIncidentBehavior]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[DisciplineIncidentBehavior](

	[SchoolId] [int] NOT NULL,

	[IncidentIdentifier] [nvarchar](20) NOT NULL,

	[BehaviorDescriptorId] [int] NOT NULL,

 CONSTRAINT [PK_DisciplineIncidentBehavior] PRIMARY KEY CLUSTERED 

(

	[SchoolId] ASC,

	[IncidentIdentifier] ASC,

	[BehaviorDescriptorId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[AcademicHonor]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AcademicHonor](

	[StudentUSI] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[DiplomaTypeId] [int] NOT NULL,

	[DiplomaAwardDate] [date] NOT NULL,

	[AcademicHonorsTypeId] [int] NOT NULL,

	[HonorAwardDate] [date] NOT NULL,

	[HonorsDescription] [nvarchar](80) NOT NULL,

	[SchoolYear] [smallint] NULL,

	[TermTypeId] [int] NULL,

 CONSTRAINT [PK_AcademicHonor] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[SchoolId] ASC,

	[DiplomaTypeId] ASC,

	[DiplomaAwardDate] ASC,

	[AcademicHonorsTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AcademicHonor', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AcademicHonor', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The type of diploma/credential that is awarded to a student in recognition of his/her completion of the curricular requirements.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AcademicHonor', @level2type=N'COLUMN',@level2name=N'DiplomaTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The month, day, and year on which the student met  graduation requirements and was awarded a diploma.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AcademicHonor', @level2type=N'COLUMN',@level2name=N'DiplomaAwardDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A designation of the type of academic distinctions earned by or awarded to the student.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AcademicHonor', @level2type=N'COLUMN',@level2name=N'AcademicHonorsTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The date the honor was awarded or earned.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AcademicHonor', @level2type=N'COLUMN',@level2name=N'HonorAwardDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A description of the type of academic distinctions earned by or awarded to the individual.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AcademicHonor', @level2type=N'COLUMN',@level2name=N'HonorsDescription'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The "as of" school year the student academic record.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AcademicHonor', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AcademicHonor', @level2type=N'COLUMN',@level2name=N'TermTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Academic distinctions earned by or awarded to the student

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AcademicHonor'

GO

/****** Object:  Table [edfi].[DisciplineAction]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[DisciplineAction](

	[StudentUSI] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[IncidentIdentifier] [nvarchar](20) NOT NULL,

	[DisciplineActionIdentifier] [nvarchar](20) NOT NULL,

	[DisciplineDate] [date] NOT NULL,

	[DisciplineActionLength] [int] NULL,

	[ActualDisciplineActionLength] [int] NULL,

	[DisciplineActionLengthDifferenceReasonTypeId] [int] NULL,

	[ResponsibilitySchoolId] [int] NOT NULL,

	[AssignmentSchoolId] [int] NULL,

 CONSTRAINT [PK_DisciplineAction] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[SchoolId] ASC,

	[IncidentIdentifier] ASC,

	[DisciplineActionIdentifier] ASC,

	[DisciplineDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineAction', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineAction', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A locally assigned unique identifier (within the school or school district) to identify each specific incident or occurrence. The same identifier should be used to document the entire incident even if it included multiple offenses and multiple offenders.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineAction', @level2type=N'COLUMN',@level2name=N'IncidentIdentifier'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Identifier assigned by the education organization to the discipline action.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineAction', @level2type=N'COLUMN',@level2name=N'DisciplineActionIdentifier'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Month, day and year of the discipline action.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineAction', @level2type=N'COLUMN',@level2name=N'DisciplineDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The length of time for the Discipline Action (e.g. removal, detention), if applicable.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineAction', @level2type=N'COLUMN',@level2name=N'DisciplineActionLength'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The length of time for the Discipline Action (e.g. removal, detention), if applicable.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineAction', @level2type=N'COLUMN',@level2name=N'ActualDisciplineActionLength'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This event entity represents actions taken by an education organization after a disruptive event that is recorded as a discipline incident.

NEDM: Discipline Action

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineAction'

GO

/****** Object:  Table [edfi].[DisciplineActionStaff]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[DisciplineActionStaff](

	[StudentUSI] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[IncidentIdentifier] [nvarchar](20) NOT NULL,

	[DisciplineActionIdentifier] [nvarchar](20) NOT NULL,

	[DisciplineDate] [date] NOT NULL,

	[StaffUSI] [int] NOT NULL,

 CONSTRAINT [PK_DisciplineActionStaff] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[SchoolId] ASC,

	[IncidentIdentifier] ASC,

	[DisciplineActionIdentifier] ASC,

	[DisciplineDate] ASC,

	[StaffUSI] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineActionStaff', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineActionStaff', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A locally assigned unique identifier (within the school or school district) to identify each specific incident or occurrence. The same identifier should be used to document the entire incident even if it included multiple offenses and multiple offenders.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineActionStaff', @level2type=N'COLUMN',@level2name=N'IncidentIdentifier'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Identifier assigned by the education organization to the discipline action.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineActionStaff', @level2type=N'COLUMN',@level2name=N'DisciplineActionIdentifier'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Month, day and year of the discipline action.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineActionStaff', @level2type=N'COLUMN',@level2name=N'DisciplineDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Staff Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineActionStaff', @level2type=N'COLUMN',@level2name=N'StaffUSI'

GO

/****** Object:  Table [edfi].[DisciplineActionDiscipline]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[DisciplineActionDiscipline](

	[StudentUSI] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[IncidentIdentifier] [nvarchar](20) NOT NULL,

	[DisciplineActionIdentifier] [nvarchar](20) NOT NULL,

	[DisciplineDate] [date] NOT NULL,

	[DisciplineDescriptorId] [int] NOT NULL,

 CONSTRAINT [PK_DisciplineActionDiscipline] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[SchoolId] ASC,

	[IncidentIdentifier] ASC,

	[DisciplineActionIdentifier] ASC,

	[DisciplineDate] ASC,

	[DisciplineDescriptorId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineActionDiscipline', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineActionDiscipline', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A locally assigned unique identifier (within the school or school district) to identify each specific incident or occurrence. The same identifier should be used to document the entire incident even if it included multiple offenses and multiple offenders.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineActionDiscipline', @level2type=N'COLUMN',@level2name=N'IncidentIdentifier'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Identifier assigned by the education organization to the discipline action.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineActionDiscipline', @level2type=N'COLUMN',@level2name=N'DisciplineActionIdentifier'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Month, day and year of the discipline action.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'DisciplineActionDiscipline', @level2type=N'COLUMN',@level2name=N'DisciplineDate'

GO

/****** Object:  Table [edfi].[AttendanceEvent]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AttendanceEvent](

	[StudentUSI] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[ClassPeriodName] [nvarchar](20) NOT NULL,

	[ClassroomIdentificationCode] [nvarchar](20) NOT NULL,

	[LocalCourseCode] [nvarchar](20) NOT NULL,

	[TermTypeId] [int] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

	[EventDate] [date] NOT NULL,

	[AttendanceEventCategoryTypeId] [int] NOT NULL,

	[AttendanceEventTypeId] [int] NULL,

	[AttendanceEventReason] [nvarchar](40) NULL,

	[EducationalEnvironmentTypeId] [int] NULL,

 CONSTRAINT [PK_AttendanceEvent] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[SchoolId] ASC,

	[ClassPeriodName] ASC,

	[ClassroomIdentificationCode] ASC,

	[LocalCourseCode] ASC,

	[TermTypeId] ASC,

	[SchoolYear] ASC,

	[EventDate] ASC,

	[AttendanceEventCategoryTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AttendanceEvent', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AttendanceEvent', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the portion of a typical daily session in which students receive instruction in a specified subject (e.g., morning, sixth period, block period, or AB schedules). 

NEDM: Class Period

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AttendanceEvent', @level2type=N'COLUMN',@level2name=N'ClassPeriodName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A unique number or alphanumeric code assigned to a room by a school, school system, state, or other agency or entity.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AttendanceEvent', @level2type=N'COLUMN',@level2name=N'ClassroomIdentificationCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The local code assigned by the LEA or Campus that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AttendanceEvent', @level2type=N'COLUMN',@level2name=N'LocalCourseCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the term in which the section is offered (e.g., First semester, Second semester, Year long, summer school)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AttendanceEvent', @level2type=N'COLUMN',@level2name=N'TermTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The identifier for the school year (e.g., 2010/11).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AttendanceEvent', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Date for this attendance event.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AttendanceEvent', @level2type=N'COLUMN',@level2name=N'EventDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The type of the attendance event, for example daily attendance, class section attendance, program attendance.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AttendanceEvent', @level2type=N'COLUMN',@level2name=N'AttendanceEventTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A code describing the type of the attendance event.

present

Unexcused absence

Excused absence

Tardy

NEDM: Attendance Event Category

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AttendanceEvent', @level2type=N'COLUMN',@level2name=N'AttendanceEventCategoryTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The reason for the absence or tardy.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AttendanceEvent', @level2type=N'COLUMN',@level2name=N'AttendanceEventReason'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The setting in which a child receives education and related services.  This is only used in the AttendanceEvent if different from that in the related Section.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AttendanceEvent', @level2type=N'COLUMN',@level2name=N'EducationalEnvironmentTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This event entity represents the recording of whether a student is in attendance for a class or in attendance to receive or participate in program services.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AttendanceEvent'

GO

/****** Object:  Table [edfi].[AssessmentSection]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[AssessmentSection](

	[AssessmentTitle] [nvarchar](60) NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

	[AssessedGradeLevelTypeId] [int] NOT NULL,

	[Version] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[ClassPeriodName] [nvarchar](20) NOT NULL,

	[ClassroomIdentificationCode] [nvarchar](20) NOT NULL,

	[LocalCourseCode] [nvarchar](20) NOT NULL,

	[TermTypeId] [int] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

 CONSTRAINT [PK_AssessmentSection] PRIMARY KEY CLUSTERED 

(

	[AssessmentTitle] ASC,

	[AcademicSubjectTypeId] ASC,

	[AssessedGradeLevelTypeId] ASC,

	[Version] ASC,

	[SchoolId] ASC,

	[ClassPeriodName] ASC,

	[ClassroomIdentificationCode] ASC,

	[LocalCourseCode] ASC,

	[TermTypeId] ASC,

	[SchoolYear] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The title or name of the assessment.

NEDM: Assessment Title

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentSection', @level2type=N'COLUMN',@level2name=N'AssessmentTitle'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

NEDM: Assessment Content, Academic Subject

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentSection', @level2type=N'COLUMN',@level2name=N'AcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The typical grade level for which an assessment is designed. If the test assessment spans a range of grades, then this attribute holds the highest grade assessed.  If only one grade level is assessed, then only this attribute is used. For example:

Adult

Prekindergarten

First grade

Second grade

...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentSection', @level2type=N'COLUMN',@level2name=N'AssessedGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The version identifier for the test assessment.

NEDM: Assessment Version

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentSection', @level2type=N'COLUMN',@level2name=N'Version'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentSection', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the portion of a typical daily session in which students receive instruction in a specified subject (e.g., morning, sixth period, block period, or AB schedules). 

NEDM: Class Period

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentSection', @level2type=N'COLUMN',@level2name=N'ClassPeriodName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A unique number or alphanumeric code assigned to a room by a school, school system, state, or other agency or entity.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentSection', @level2type=N'COLUMN',@level2name=N'ClassroomIdentificationCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The local code assigned by the LEA or Campus that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentSection', @level2type=N'COLUMN',@level2name=N'LocalCourseCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the term in which the section is offered (e.g., First semester, Second semester, Year long, summer school)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentSection', @level2type=N'COLUMN',@level2name=N'TermTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The identifier for the school year (e.g., 2010/11).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'AssessmentSection', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

/****** Object:  Table [edfi].[GradebookEntry]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[GradebookEntry](

	[SchoolId] [int] NOT NULL,

	[ClassPeriodName] [nvarchar](20) NOT NULL,

	[ClassroomIdentificationCode] [nvarchar](20) NOT NULL,

	[LocalCourseCode] [nvarchar](20) NOT NULL,

	[TermTypeId] [int] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

	[GradebookEntryType] [nvarchar](60) NOT NULL,

	[DateAssigned] [date] NOT NULL,

	[Description] [nvarchar](1024) NULL,

	[GradingPeriodTypeId] [int] NULL,

	[GradingPeriodBeginDate] [date] NULL,

 CONSTRAINT [PK_GradebookEntry] PRIMARY KEY CLUSTERED 

(

	[SchoolId] ASC,

	[ClassPeriodName] ASC,

	[ClassroomIdentificationCode] ASC,

	[LocalCourseCode] ASC,

	[TermTypeId] ASC,

	[SchoolYear] ASC,

	[GradebookEntryType] ASC,

	[DateAssigned] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradebookEntry', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the portion of a typical daily session in which students receive instruction in a specified subject (e.g., morning, sixth period, block period, or AB schedules). 

NEDM: Class Period

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradebookEntry', @level2type=N'COLUMN',@level2name=N'ClassPeriodName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A unique number or alphanumeric code assigned to a room by a school, school system, state, or other agency or entity.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradebookEntry', @level2type=N'COLUMN',@level2name=N'ClassroomIdentificationCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The local code assigned by the LEA or Campus that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradebookEntry', @level2type=N'COLUMN',@level2name=N'LocalCourseCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the term in which the section is offered (e.g., First semester, Second semester, Year long, summer school)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradebookEntry', @level2type=N'COLUMN',@level2name=N'TermTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The identifier for the school year (e.g., 2010/11).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradebookEntry', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the grading period during the school year in which the grade is offered (e.g., 1st cycle, 1st semester)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradebookEntry', @level2type=N'COLUMN',@level2name=N'GradingPeriodTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Month, day, and year of the first day of the grading period.



' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradebookEntry', @level2type=N'COLUMN',@level2name=N'GradingPeriodBeginDate'

GO

/****** Object:  Table [edfi].[Grade]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[Grade](

	[StudentUSI] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[ClassPeriodName] [nvarchar](20) NOT NULL,

	[ClassroomIdentificationCode] [nvarchar](20) NOT NULL,

	[LocalCourseCode] [nvarchar](20) NOT NULL,

	[TermTypeId] [int] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

	[GradingPeriodTypeId] [int] NOT NULL,

	[GradingPeriodBeginDate] [date] NOT NULL,

	[LetterGradeEarned] [nvarchar](20) NULL,

	[NumericGradeEarned] [int] NULL,

	[DiagnosticStatement] [text] NULL,

	[GradeTypeId] [int] NOT NULL,

	[PerformanceBaseConversionTypeId] [int] NULL,

 CONSTRAINT [PK_Grade] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[SchoolId] ASC,

	[ClassPeriodName] ASC,

	[ClassroomIdentificationCode] ASC,

	[LocalCourseCode] ASC,

	[TermTypeId] ASC,

	[SchoolYear] ASC,

	[GradingPeriodTypeId] ASC,

	[GradingPeriodBeginDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Grade', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Grade', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the portion of a typical daily session in which students receive instruction in a specified subject (e.g., morning, sixth period, block period, or AB schedules). 

NEDM: Class Period

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Grade', @level2type=N'COLUMN',@level2name=N'ClassPeriodName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A unique number or alphanumeric code assigned to a room by a school, school system, state, or other agency or entity.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Grade', @level2type=N'COLUMN',@level2name=N'ClassroomIdentificationCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The local code assigned by the LEA or Campus that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Grade', @level2type=N'COLUMN',@level2name=N'LocalCourseCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the term in which the section is offered (e.g., First semester, Second semester, Year long, summer school)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Grade', @level2type=N'COLUMN',@level2name=N'TermTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The identifier for the school year (e.g., 2010/11).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Grade', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the grading period during the school year in which the grade is offered (e.g., 1st cycle, 1st semester)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Grade', @level2type=N'COLUMN',@level2name=N'GradingPeriodTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Month, day, and year of the first day of the grading period.



' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Grade', @level2type=N'COLUMN',@level2name=N'GradingPeriodBeginDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A final or interim (grading period) indicator of student performance in a class as submitted by the instructor.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Grade', @level2type=N'COLUMN',@level2name=N'LetterGradeEarned'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A final or interim (grading period) indicator of student performance in a class as submitted by the instructor.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Grade', @level2type=N'COLUMN',@level2name=N'NumericGradeEarned'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A statement provided by the teacher that provides information in addition to the grade or assessment score.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Grade', @level2type=N'COLUMN',@level2name=N'DiagnosticStatement'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The type of grade (e.g., Exam, Final, Grading Period, Progress Report)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Grade', @level2type=N'COLUMN',@level2name=N'GradeTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This educational entity represents an overall score or assessment tied to a course over a period of time (i.e., the grading period).  Student grades are usually a compilation of marks and other scores.  The model supports letter and/or numeric grades plus and and optional end-of-grading period assessment score.

NEDM: Grade Earned

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'Grade'

GO

/****** Object:  Table [edfi].[SectionProgram]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[SectionProgram](

	[SchoolId] [int] NOT NULL,

	[ClassPeriodName] [nvarchar](20) NOT NULL,

	[ClassroomIdentificationCode] [nvarchar](20) NOT NULL,

	[LocalCourseCode] [nvarchar](20) NOT NULL,

	[TermTypeId] [int] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

	[ProgramTypeId] [int] NOT NULL,

 CONSTRAINT [PK_SectionProgram] PRIMARY KEY CLUSTERED 

(

	[SchoolId] ASC,

	[ClassPeriodName] ASC,

	[ClassroomIdentificationCode] ASC,

	[LocalCourseCode] ASC,

	[TermTypeId] ASC,

	[SchoolYear] ASC,

	[ProgramTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'SectionProgram', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the portion of a typical daily session in which students receive instruction in a specified subject (e.g., morning, sixth period, block period, or AB schedules). 

NEDM: Class Period

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'SectionProgram', @level2type=N'COLUMN',@level2name=N'ClassPeriodName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A unique number or alphanumeric code assigned to a room by a school, school system, state, or other agency or entity.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'SectionProgram', @level2type=N'COLUMN',@level2name=N'ClassroomIdentificationCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The local code assigned by the LEA or Campus that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'SectionProgram', @level2type=N'COLUMN',@level2name=N'LocalCourseCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the term in which the section is offered (e.g., First semester, Second semester, Year long, summer school)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'SectionProgram', @level2type=N'COLUMN',@level2name=N'TermTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The identifier for the school year (e.g., 2010/11).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'SectionProgram', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

/****** Object:  Table [edfi].[TeacherSectionAssociation]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[TeacherSectionAssociation](

	[StaffUSI] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[ClassPeriodName] [nvarchar](20) NOT NULL,

	[ClassroomIdentificationCode] [nvarchar](20) NOT NULL,

	[LocalCourseCode] [nvarchar](20) NOT NULL,

	[TermTypeId] [int] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

	[ClassroomPositionTypeId] [int] NOT NULL,

	[BeginDate] [date] NULL,

	[EndDate] [date] NULL,

	[HighlyQualifiedTeacher] [bit] NULL,

 CONSTRAINT [PK_TeacherSectionAssociation] PRIMARY KEY CLUSTERED 

(

	[StaffUSI] ASC,

	[SchoolId] ASC,

	[ClassPeriodName] ASC,

	[ClassroomIdentificationCode] ASC,

	[LocalCourseCode] ASC,

	[TermTypeId] ASC,

	[SchoolYear] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Staff Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'TeacherSectionAssociation', @level2type=N'COLUMN',@level2name=N'StaffUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'TeacherSectionAssociation', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the portion of a typical daily session in which students receive instruction in a specified subject (e.g., morning, sixth period, block period, or AB schedules). 

NEDM: Class Period

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'TeacherSectionAssociation', @level2type=N'COLUMN',@level2name=N'ClassPeriodName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A unique number or alphanumeric code assigned to a room by a school, school system, state, or other agency or entity.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'TeacherSectionAssociation', @level2type=N'COLUMN',@level2name=N'ClassroomIdentificationCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The local code assigned by the LEA or Campus that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'TeacherSectionAssociation', @level2type=N'COLUMN',@level2name=N'LocalCourseCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the term in which the section is offered (e.g., First semester, Second semester, Year long, summer school)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'TeacherSectionAssociation', @level2type=N'COLUMN',@level2name=N'TermTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The identifier for the school year (e.g., 2010/11).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'TeacherSectionAssociation', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The type of position the staff member holds in the specific class/section; for example:

Teacher of Record, Assistant Teacher, Support Teacher, Substitute Teacher...

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'TeacherSectionAssociation', @level2type=N'COLUMN',@level2name=N'ClassroomPositionTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Month, day and year of a teacher''s assignment to the section.  If blank, defaults to the first day of the first grading period for the section.



' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'TeacherSectionAssociation', @level2type=N'COLUMN',@level2name=N'BeginDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Month, day and year of the last day of a teacher''s assignment to the section.

NEDM: Ending Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'TeacherSectionAssociation', @level2type=N'COLUMN',@level2name=N'EndDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of whether a teacher is classified as highly qualified for his/her assignment according to state definition.  This attribute indicates the teacher is highly qualified for this section being taught.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'TeacherSectionAssociation', @level2type=N'COLUMN',@level2name=N'HighlyQualifiedTeacher'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This association indicates the class sections to which a teacher is assigned to.

NEDM: Teacher Section Assignment

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'TeacherSectionAssociation'

GO

/****** Object:  Table [edfi].[StudentSectionAssociation]    Script Date: 10/20/2011 10:44:07 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentSectionAssociation](

	[StudentUSI] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[ClassPeriodName] [nvarchar](20) NOT NULL,

	[ClassroomIdentificationCode] [nvarchar](20) NOT NULL,

	[LocalCourseCode] [nvarchar](20) NOT NULL,

	[TermTypeId] [int] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

	[BeginDate] [date] NOT NULL,

	[EndDate] [date] NULL,

	[HomeroomIndicator] [bit] NULL,

	[RepeatIdentifierTypeId] [int] NULL,

 CONSTRAINT [PK_StudentSectionAssociation] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[SchoolId] ASC,

	[ClassPeriodName] ASC,

	[ClassroomIdentificationCode] ASC,

	[LocalCourseCode] ASC,

	[TermTypeId] ASC,

	[SchoolYear] ASC,

	[BeginDate] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSectionAssociation', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSectionAssociation', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the portion of a typical daily session in which students receive instruction in a specified subject (e.g., morning, sixth period, block period, or AB schedules). 

NEDM: Class Period

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSectionAssociation', @level2type=N'COLUMN',@level2name=N'ClassPeriodName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A unique number or alphanumeric code assigned to a room by a school, school system, state, or other agency or entity.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSectionAssociation', @level2type=N'COLUMN',@level2name=N'ClassroomIdentificationCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The local code assigned by the LEA or Campus that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSectionAssociation', @level2type=N'COLUMN',@level2name=N'LocalCourseCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the term in which the section is offered (e.g., First semester, Second semester, Year long, summer school)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSectionAssociation', @level2type=N'COLUMN',@level2name=N'TermTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The identifier for the school year (e.g., 2010/11).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSectionAssociation', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Month, day and year of the student''s entry or assignment to the section.  If blank, default is the start date of the first grading period.

NEDM: EntryDate

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSectionAssociation', @level2type=N'COLUMN',@level2name=N'BeginDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Month, day, and year of the withdrawal or exit of the student from the section.

NEDM: Exit/Withdraw Date

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSectionAssociation', @level2type=N'COLUMN',@level2name=N'EndDate'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Indicator that the class is the student''s designated homeroom used for daily attendance reporting.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSectionAssociation', @level2type=N'COLUMN',@level2name=N'HomeroomIndicator'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication as to whether a student has previously taken a given course.

NEDM: Repeat Identifier

Repeated, counted in grade point average

Repeated, not counted in grade point average

Not repeated

Other

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSectionAssociation', @level2type=N'COLUMN',@level2name=N'RepeatIdentifierTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'This association indicates the course sections a student is assigned to.

NEDM: Student Section Assignment

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentSectionAssociation'

GO

/****** Object:  Table [edfi].[StudentDisciplineIncidentSecondaryBehavior]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentDisciplineIncidentSecondaryBehavior](

	[StudentUSI] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[IncidentIdentifier] [nvarchar](20) NOT NULL,

	[BehaviorCategoryTypeId] [int] NOT NULL,

	[SecondaryBehavior] [nvarchar](50) NOT NULL,

 CONSTRAINT [PK_StudentDisciplineIncidentSecondaryBehavior] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[SchoolId] ASC,

	[IncidentIdentifier] ASC,

	[BehaviorCategoryTypeId] ASC,

	[SecondaryBehavior] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentDisciplineIncidentSecondaryBehavior', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentDisciplineIncidentSecondaryBehavior', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A locally assigned unique identifier (within the school or school district) to identify each specific incident or occurrence. The same identifier should be used to document the entire incident even if it included multiple offenses and multiple offenders.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentDisciplineIncidentSecondaryBehavior', @level2type=N'COLUMN',@level2name=N'IncidentIdentifier'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The category of the incident behavior for classification purposes,

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentDisciplineIncidentSecondaryBehavior', @level2type=N'COLUMN',@level2name=N'BehaviorCategoryTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the behavior of a student for a discipline incident.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentDisciplineIncidentSecondaryBehavior', @level2type=N'COLUMN',@level2name=N'SecondaryBehavior'

GO

/****** Object:  Table [edfi].[StudentDisciplineIncidentBehavior]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentDisciplineIncidentBehavior](

	[StudentUSI] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[IncidentIdentifier] [nvarchar](20) NOT NULL,

	[BehaviorDescriptorId] [int] NOT NULL,

 CONSTRAINT [PK_StudentDisciplineIncidentBehavior] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[SchoolId] ASC,

	[IncidentIdentifier] ASC,

	[BehaviorDescriptorId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

/****** Object:  Table [edfi].[StudentGradebookEntry]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentGradebookEntry](

	[StudentUSI] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[ClassPeriodName] [nvarchar](20) NOT NULL,

	[ClassroomIdentificationCode] [nvarchar](20) NOT NULL,

	[LocalCourseCode] [nvarchar](20) NOT NULL,

	[TermTypeId] [int] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

	[BeginDate] [date] NOT NULL,

	[GradebookEntryType] [nvarchar](60) NOT NULL,

	[DateAssigned] [date] NOT NULL,

	[DateFullfilled] [date] NULL,

	[LetterGradeEarned] [nvarchar](20) NULL,

	[NumericGradeEarned] [int] NULL,

	[CompetencyLevelDescriptorId] [int] NULL,

	[DiagnosticStatement] [nvarchar](1024) NULL,

 CONSTRAINT [PK_StudentGradebookEntry] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[SchoolId] ASC,

	[ClassPeriodName] ASC,

	[ClassroomIdentificationCode] ASC,

	[LocalCourseCode] ASC,

	[TermTypeId] ASC,

	[SchoolYear] ASC,

	[BeginDate] ASC,

	[GradebookEntryType] ASC,

	[DateAssigned] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentGradebookEntry', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentGradebookEntry', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the portion of a typical daily session in which students receive instruction in a specified subject (e.g., morning, sixth period, block period, or AB schedules). 

NEDM: Class Period

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentGradebookEntry', @level2type=N'COLUMN',@level2name=N'ClassPeriodName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A unique number or alphanumeric code assigned to a room by a school, school system, state, or other agency or entity.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentGradebookEntry', @level2type=N'COLUMN',@level2name=N'ClassroomIdentificationCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The local code assigned by the LEA or Campus that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentGradebookEntry', @level2type=N'COLUMN',@level2name=N'LocalCourseCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the term in which the section is offered (e.g., First semester, Second semester, Year long, summer school)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentGradebookEntry', @level2type=N'COLUMN',@level2name=N'TermTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The identifier for the school year (e.g., 2010/11).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentGradebookEntry', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Month, day and year of the student''s entry or assignment to the section.  If blank, default is the start date of the first grading period.

NEDM: EntryDate

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentGradebookEntry', @level2type=N'COLUMN',@level2name=N'BeginDate'

GO

/****** Object:  Table [edfi].[StudentCompetencyObjective]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentCompetencyObjective](

	[StudentUSI] [int] NOT NULL,

	[SchoolId] [int] NOT NULL,

	[CompetencyLevelDescriptorId] [int] NOT NULL,

	[Objective] [nvarchar](60) NOT NULL,

	[Description] [nvarchar](1024) NULL,

	[ObjectiveGradeLevelTypeId] [int] NOT NULL,

	[StudentCompetencyObjectiveId] [nvarchar](60) NULL,

	[DiagnosticStatement] [nvarchar](1024) NULL,

	[ClassPeriodName] [nvarchar](20) NULL,

	[ClassroomIdentificationCode] [nvarchar](20) NULL,

	[LocalCourseCode] [nvarchar](20) NULL,

	[TermTypeId] [int] NULL,

	[SchoolYear] [smallint] NULL,

	[BeginDate] [date] NULL,

 CONSTRAINT [PK_StudentCompetencyObjective] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[SchoolId] ASC,

	[CompetencyLevelDescriptorId] ASC,

	[Objective] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCompetencyObjective', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCompetencyObjective', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The designated title of the learning objective.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCompetencyObjective', @level2type=N'COLUMN',@level2name=N'Objective'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The grade level for which the learning objective is targeted,

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCompetencyObjective', @level2type=N'COLUMN',@level2name=N'ObjectiveGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The designated title of the learning objective.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCompetencyObjective', @level2type=N'COLUMN',@level2name=N'StudentCompetencyObjectiveId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the portion of a typical daily session in which students receive instruction in a specified subject (e.g., morning, sixth period, block period, or AB schedules). 

NEDM: Class Period

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCompetencyObjective', @level2type=N'COLUMN',@level2name=N'ClassPeriodName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A unique number or alphanumeric code assigned to a room by a school, school system, state, or other agency or entity.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCompetencyObjective', @level2type=N'COLUMN',@level2name=N'ClassroomIdentificationCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The local code assigned by the LEA or Campus that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCompetencyObjective', @level2type=N'COLUMN',@level2name=N'LocalCourseCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the term in which the section is offered (e.g., First semester, Second semester, Year long, summer school)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCompetencyObjective', @level2type=N'COLUMN',@level2name=N'TermTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The identifier for the school year (e.g., 2010/11).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCompetencyObjective', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Month, day and year of the student''s entry or assignment to the section.  If blank, default is the start date of the first grading period.

NEDM: EntryDate

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCompetencyObjective', @level2type=N'COLUMN',@level2name=N'BeginDate'

GO

/****** Object:  Table [edfi].[StudentCompetencyLearningObjective]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[StudentCompetencyLearningObjective](

	[StudentUSI] [int] NOT NULL,

	[CompetencyLevelDescriptorId] [int] NOT NULL,

	[Objective] [nvarchar](60) NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

	[ObjectiveGradeLevelTypeId] [int] NOT NULL,

	[DiagnosticStatement] [nvarchar](1024) NULL,

	[SchoolId] [int] NULL,

	[ClassPeriodName] [nvarchar](20) NULL,

	[ClassroomIdentificationCode] [nvarchar](20) NULL,

	[LocalCourseCode] [nvarchar](20) NULL,

	[TermTypeId] [int] NULL,

	[SchoolYear] [smallint] NULL,

	[BeginDate] [date] NULL,

 CONSTRAINT [PK_StudentCompetencyLearningObjective] PRIMARY KEY CLUSTERED 

(

	[StudentUSI] ASC,

	[CompetencyLevelDescriptorId] ASC,

	[Objective] ASC,

	[AcademicSubjectTypeId] ASC,

	[ObjectiveGradeLevelTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Student Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCompetencyLearningObjective', @level2type=N'COLUMN',@level2name=N'StudentUSI'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The designated title of the learning objective.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCompetencyLearningObjective', @level2type=N'COLUMN',@level2name=N'Objective'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCompetencyLearningObjective', @level2type=N'COLUMN',@level2name=N'AcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The grade level for which the learning objective is targeted,

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCompetencyLearningObjective', @level2type=N'COLUMN',@level2name=N'ObjectiveGradeLevelTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCompetencyLearningObjective', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the portion of a typical daily session in which students receive instruction in a specified subject (e.g., morning, sixth period, block period, or AB schedules). 

NEDM: Class Period

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCompetencyLearningObjective', @level2type=N'COLUMN',@level2name=N'ClassPeriodName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A unique number or alphanumeric code assigned to a room by a school, school system, state, or other agency or entity.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCompetencyLearningObjective', @level2type=N'COLUMN',@level2name=N'ClassroomIdentificationCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The local code assigned by the LEA or Campus that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCompetencyLearningObjective', @level2type=N'COLUMN',@level2name=N'LocalCourseCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the term in which the section is offered (e.g., First semester, Second semester, Year long, summer school)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCompetencyLearningObjective', @level2type=N'COLUMN',@level2name=N'TermTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The identifier for the school year (e.g., 2010/11).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCompetencyLearningObjective', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'Month, day and year of the student''s entry or assignment to the section.  If blank, default is the start date of the first grading period.

NEDM: EntryDate

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'StudentCompetencyLearningObjective', @level2type=N'COLUMN',@level2name=N'BeginDate'

GO

/****** Object:  Table [edfi].[GradebookEntryLearningStandard]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[GradebookEntryLearningStandard](

	[SchoolId] [int] NOT NULL,

	[ClassPeriodName] [nvarchar](20) NOT NULL,

	[ClassroomIdentificationCode] [nvarchar](20) NOT NULL,

	[LocalCourseCode] [nvarchar](20) NOT NULL,

	[TermTypeId] [int] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

	[GradebookEntryType] [nvarchar](60) NOT NULL,

	[DateAssigned] [date] NOT NULL,

	[LearningStandardId] [nvarchar](40) NOT NULL,

 CONSTRAINT [PK_GradebookEntryLearningStandard] PRIMARY KEY CLUSTERED 

(

	[SchoolId] ASC,

	[ClassPeriodName] ASC,

	[ClassroomIdentificationCode] ASC,

	[LocalCourseCode] ASC,

	[TermTypeId] ASC,

	[SchoolYear] ASC,

	[GradebookEntryType] ASC,

	[DateAssigned] ASC,

	[LearningStandardId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradebookEntryLearningStandard', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the portion of a typical daily session in which students receive instruction in a specified subject (e.g., morning, sixth period, block period, or AB schedules). 

NEDM: Class Period

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradebookEntryLearningStandard', @level2type=N'COLUMN',@level2name=N'ClassPeriodName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A unique number or alphanumeric code assigned to a room by a school, school system, state, or other agency or entity.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradebookEntryLearningStandard', @level2type=N'COLUMN',@level2name=N'ClassroomIdentificationCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The local code assigned by the LEA or Campus that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradebookEntryLearningStandard', @level2type=N'COLUMN',@level2name=N'LocalCourseCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the term in which the section is offered (e.g., First semester, Second semester, Year long, summer school)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradebookEntryLearningStandard', @level2type=N'COLUMN',@level2name=N'TermTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The identifier for the school year (e.g., 2010/11).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradebookEntryLearningStandard', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

/****** Object:  Table [edfi].[GradebookEntryLearningObjective]    Script Date: 10/20/2011 10:44:06 ******/

SET ANSI_NULLS ON

GO

SET QUOTED_IDENTIFIER ON

GO

CREATE TABLE [edfi].[GradebookEntryLearningObjective](

	[SchoolId] [int] NOT NULL,

	[ClassPeriodName] [nvarchar](20) NOT NULL,

	[ClassroomIdentificationCode] [nvarchar](20) NOT NULL,

	[LocalCourseCode] [nvarchar](20) NOT NULL,

	[TermTypeId] [int] NOT NULL,

	[SchoolYear] [smallint] NOT NULL,

	[GradebookEntryType] [nvarchar](60) NOT NULL,

	[DateAssigned] [date] NOT NULL,

	[Objective] [nvarchar](60) NOT NULL,

	[AcademicSubjectTypeId] [int] NOT NULL,

	[ObjectiveGradeLevelTypeId] [int] NOT NULL,

 CONSTRAINT [PK_GradebookEntryLearningObjective] PRIMARY KEY CLUSTERED 

(

	[SchoolId] ASC,

	[ClassPeriodName] ASC,

	[ClassroomIdentificationCode] ASC,

	[LocalCourseCode] ASC,

	[TermTypeId] ASC,

	[SchoolYear] ASC,

	[GradebookEntryType] ASC,

	[DateAssigned] ASC,

	[Objective] ASC,

	[AcademicSubjectTypeId] ASC,

	[ObjectiveGradeLevelTypeId] ASC

)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'School Identity Column' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradebookEntryLearningObjective', @level2type=N'COLUMN',@level2name=N'SchoolId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'An indication of the portion of a typical daily session in which students receive instruction in a specified subject (e.g., morning, sixth period, block period, or AB schedules). 

NEDM: Class Period

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradebookEntryLearningObjective', @level2type=N'COLUMN',@level2name=N'ClassPeriodName'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'A unique number or alphanumeric code assigned to a room by a school, school system, state, or other agency or entity.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradebookEntryLearningObjective', @level2type=N'COLUMN',@level2name=N'ClassroomIdentificationCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The local code assigned by the LEA or Campus that identifies the organization of subject matter and related learning experiences provided for the instruction of students.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradebookEntryLearningObjective', @level2type=N'COLUMN',@level2name=N'LocalCourseCode'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The name of the term in which the section is offered (e.g., First semester, Second semester, Year long, summer school)

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradebookEntryLearningObjective', @level2type=N'COLUMN',@level2name=N'TermTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The identifier for the school year (e.g., 2010/11).

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradebookEntryLearningObjective', @level2type=N'COLUMN',@level2name=N'SchoolYear'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The designated title of the learning objective.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradebookEntryLearningObjective', @level2type=N'COLUMN',@level2name=N'Objective'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The description of the content or subject area (e.g., arts, mathematics, reading, stenography, or a foreign language) of an assessment.

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradebookEntryLearningObjective', @level2type=N'COLUMN',@level2name=N'AcademicSubjectTypeId'

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'The grade level for which the learning objective is targeted,

' , @level0type=N'SCHEMA',@level0name=N'edfi', @level1type=N'TABLE',@level1name=N'GradebookEntryLearningObjective', @level2type=N'COLUMN',@level2name=N'ObjectiveGradeLevelTypeId'

GO

/****** Object:  ForeignKey [FK_AcademicHonor_Diploma]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AcademicHonor]  WITH CHECK ADD  CONSTRAINT [FK_AcademicHonor_Diploma] FOREIGN KEY([StudentUSI], [SchoolId], [DiplomaTypeId], [DiplomaAwardDate])

REFERENCES [edfi].[Diploma] ([StudentUSI], [SchoolId], [DiplomaTypeId], [DiplomaAwardDate])

GO

ALTER TABLE [edfi].[AcademicHonor] CHECK CONSTRAINT [FK_AcademicHonor_Diploma]

GO

/****** Object:  ForeignKey [FK_AcademicHonorsTypeType_AcademicHonor]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AcademicHonor]  WITH CHECK ADD  CONSTRAINT [FK_AcademicHonorsTypeType_AcademicHonor] FOREIGN KEY([AcademicHonorsTypeId])

REFERENCES [edfi].[AcademicHonorsType] ([AcademicHonorsTypeId])

GO

ALTER TABLE [edfi].[AcademicHonor] CHECK CONSTRAINT [FK_AcademicHonorsTypeType_AcademicHonor]

GO

/****** Object:  ForeignKey [FK_AcademicWeek_CalendarBeginDate]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AcademicWeek]  WITH CHECK ADD  CONSTRAINT [FK_AcademicWeek_CalendarBeginDate] FOREIGN KEY([EducationOrganizationId], [BeginDate])

REFERENCES [edfi].[CalendarDate] ([EducationOrganizationId], [Date])

GO

ALTER TABLE [edfi].[AcademicWeek] CHECK CONSTRAINT [FK_AcademicWeek_CalendarBeginDate]

GO

/****** Object:  ForeignKey [FK_AcademicWeek_CalendarEndDate]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AcademicWeek]  WITH CHECK ADD  CONSTRAINT [FK_AcademicWeek_CalendarEndDate] FOREIGN KEY([EducationOrganizationId], [EndDate])

REFERENCES [edfi].[CalendarDate] ([EducationOrganizationId], [Date])

GO

ALTER TABLE [edfi].[AcademicWeek] CHECK CONSTRAINT [FK_AcademicWeek_CalendarEndDate]

GO

/****** Object:  ForeignKey [FK_AcademicWeek_Session]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AcademicWeek]  WITH CHECK ADD  CONSTRAINT [FK_AcademicWeek_Session] FOREIGN KEY([EducationOrganizationId], [TermTypeId], [SchoolYear])

REFERENCES [edfi].[Session] ([EducationOrganizationId], [TermTypeId], [SchoolYear])

GO

ALTER TABLE [edfi].[AcademicWeek] CHECK CONSTRAINT [FK_AcademicWeek_Session]

GO

/****** Object:  ForeignKey [FK_Account_EducationOrganization]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Account]  WITH CHECK ADD  CONSTRAINT [FK_Account_EducationOrganization] FOREIGN KEY([EducationOrganizationId])

REFERENCES [edfi].[EducationOrganization] ([EducationOrganizationId])

GO

ALTER TABLE [edfi].[Account] CHECK CONSTRAINT [FK_Account_EducationOrganization]

GO

/****** Object:  ForeignKey [FK_AccountabilityRating_EducationOrganization]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AccountabilityRating]  WITH CHECK ADD  CONSTRAINT [FK_AccountabilityRating_EducationOrganization] FOREIGN KEY([EducationOrganizationId])

REFERENCES [edfi].[EducationOrganization] ([EducationOrganizationId])

GO

ALTER TABLE [edfi].[AccountabilityRating] CHECK CONSTRAINT [FK_AccountabilityRating_EducationOrganization]

GO

/****** Object:  ForeignKey [FK_AccountabilityRating_SchoolYearType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AccountabilityRating]  WITH CHECK ADD  CONSTRAINT [FK_AccountabilityRating_SchoolYearType] FOREIGN KEY([SchoolYear])

REFERENCES [edfi].[SchoolYearType] ([SchoolYear])

GO

ALTER TABLE [edfi].[AccountabilityRating] CHECK CONSTRAINT [FK_AccountabilityRating_SchoolYearType]

GO

/****** Object:  ForeignKey [FK_AccountCode_Account]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AccountCode]  WITH CHECK ADD  CONSTRAINT [FK_AccountCode_Account] FOREIGN KEY([EducationOrganizationId], [AccountNumber], [FiscalYear])

REFERENCES [edfi].[Account] ([EducationOrganizationId], [AccountNumber], [FiscalYear])

GO

ALTER TABLE [edfi].[AccountCode] CHECK CONSTRAINT [FK_AccountCode_Account]

GO

/****** Object:  ForeignKey [FK_AccountCode_AccountCodeDescriptor]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AccountCode]  WITH CHECK ADD  CONSTRAINT [FK_AccountCode_AccountCodeDescriptor] FOREIGN KEY([EducationOrganizationId])

REFERENCES [edfi].[AccountCodeDescriptor] ([AccountCodeDescriptorId])

GO

ALTER TABLE [edfi].[AccountCode] CHECK CONSTRAINT [FK_AccountCode_AccountCodeDescriptor]

GO

/****** Object:  ForeignKey [FK_AccountCodeDescriptor_EducationOrganization]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AccountCodeDescriptor]  WITH CHECK ADD  CONSTRAINT [FK_AccountCodeDescriptor_EducationOrganization] FOREIGN KEY([EducationOrganizationId])

REFERENCES [edfi].[EducationOrganization] ([EducationOrganizationId])

GO

ALTER TABLE [edfi].[AccountCodeDescriptor] CHECK CONSTRAINT [FK_AccountCodeDescriptor_EducationOrganization]

GO

/****** Object:  ForeignKey [FK_Actual_Account]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Actual]  WITH CHECK ADD  CONSTRAINT [FK_Actual_Account] FOREIGN KEY([EducationOrganizationId], [AccountNumber], [FiscalYear])

REFERENCES [edfi].[Account] ([EducationOrganizationId], [AccountNumber], [FiscalYear])

GO

ALTER TABLE [edfi].[Actual] CHECK CONSTRAINT [FK_Actual_Account]

GO

/****** Object:  ForeignKey [FK_AdditionalCredit_CreditType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AdditionalCredit]  WITH CHECK ADD  CONSTRAINT [FK_AdditionalCredit_CreditType] FOREIGN KEY([CreditTypeId])

REFERENCES [edfi].[CreditType] ([CreditTypeId])

GO

ALTER TABLE [edfi].[AdditionalCredit] CHECK CONSTRAINT [FK_AdditionalCredit_CreditType]

GO

/****** Object:  ForeignKey [FK_AdditionalCredit_CourseTranscript]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AdditionalCredit]  WITH CHECK ADD  CONSTRAINT [FK_AdditionalCredit_CourseTranscript] FOREIGN KEY([StudentUSI], [SchoolYear], [TermTypeId], [EducationOrganizationId], [IdentityCourseCode], [CourseAttemptResultTypeId])

REFERENCES [edfi].[CourseTranscript] ([StudentUSI], [SchoolYear], [TermTypeId], [EducationOrganizationId], [IdentityCourseCode], [CourseAttemptResultTypeId])

GO

ALTER TABLE [edfi].[AdditionalCredit] CHECK CONSTRAINT [FK_AdditionalCredit_CourseTranscript]

GO

/****** Object:  ForeignKey [FK_AcademicSubjectType_Assessment]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Assessment]  WITH CHECK ADD  CONSTRAINT [FK_AcademicSubjectType_Assessment] FOREIGN KEY([AcademicSubjectTypeId])

REFERENCES [edfi].[AcademicSubjectType] ([AcademicSubjectTypeId])

GO

ALTER TABLE [edfi].[Assessment] CHECK CONSTRAINT [FK_AcademicSubjectType_Assessment]

GO

/****** Object:  ForeignKey [FK_Assessment_AssessmentCategoryType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Assessment]  WITH CHECK ADD  CONSTRAINT [FK_Assessment_AssessmentCategoryType] FOREIGN KEY([AssessmentCategoryTypeId])

REFERENCES [edfi].[AssessmentCategoryType] ([AssessmentCategoryTypeId])

GO

ALTER TABLE [edfi].[Assessment] CHECK CONSTRAINT [FK_Assessment_AssessmentCategoryType]

GO

/****** Object:  ForeignKey [FK_Assessment_AssessmentPeriodDescriptor]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Assessment]  WITH CHECK ADD  CONSTRAINT [FK_Assessment_AssessmentPeriodDescriptor] FOREIGN KEY([AssessmentPeriodDescriptorId])

REFERENCES [edfi].[AssessmentPeriodDescriptor] ([AssessmentPeriodDescriptorId])

GO

ALTER TABLE [edfi].[Assessment] CHECK CONSTRAINT [FK_Assessment_AssessmentPeriodDescriptor]

GO

/****** Object:  ForeignKey [FK_Assessment_ContentStandardType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Assessment]  WITH CHECK ADD  CONSTRAINT [FK_Assessment_ContentStandardType] FOREIGN KEY([ContentStandardTypeId])

REFERENCES [edfi].[ContentStandardType] ([ContentStandardTypeId])

GO

ALTER TABLE [edfi].[Assessment] CHECK CONSTRAINT [FK_Assessment_ContentStandardType]

GO

/****** Object:  ForeignKey [FK_Assessment_GradeLevelType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Assessment]  WITH CHECK ADD  CONSTRAINT [FK_Assessment_GradeLevelType] FOREIGN KEY([AssessedGradeLevelTypeId])

REFERENCES [edfi].[GradeLevelType] ([GradeLevelTypeId])

GO

ALTER TABLE [edfi].[Assessment] CHECK CONSTRAINT [FK_Assessment_GradeLevelType]

GO

/****** Object:  ForeignKey [FK_AssessmentFamily_AssessmentFamily]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AssessmentFamily]  WITH CHECK ADD  CONSTRAINT [FK_AssessmentFamily_AssessmentFamily] FOREIGN KEY([ParentAssessmentFamilyTitle])

REFERENCES [edfi].[AssessmentFamily] ([AssessmentFamilyTitle])

GO

ALTER TABLE [edfi].[AssessmentFamily] CHECK CONSTRAINT [FK_AssessmentFamily_AssessmentFamily]

GO

/****** Object:  ForeignKey [FK_AssessmentFamily_AcademicSubjectType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AssessmentFamily]  WITH CHECK ADD  CONSTRAINT [FK_AssessmentFamily_AcademicSubjectType] FOREIGN KEY([AcademicSubjectTypeId])

REFERENCES [edfi].[AcademicSubjectType] ([AcademicSubjectTypeId])

GO

ALTER TABLE [edfi].[AssessmentFamily] CHECK CONSTRAINT [FK_AssessmentFamily_AcademicSubjectType]

GO

/****** Object:  ForeignKey [FK_AssessmentFamily_AssessmentCategoryType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AssessmentFamily]  WITH CHECK ADD  CONSTRAINT [FK_AssessmentFamily_AssessmentCategoryType] FOREIGN KEY([AssessmentCategoryTypeId])

REFERENCES [edfi].[AssessmentCategoryType] ([AssessmentCategoryTypeId])

GO

ALTER TABLE [edfi].[AssessmentFamily] CHECK CONSTRAINT [FK_AssessmentFamily_AssessmentCategoryType]

GO

/****** Object:  ForeignKey [FK_AssessmentFamily_ContentStandardType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AssessmentFamily]  WITH CHECK ADD  CONSTRAINT [FK_AssessmentFamily_ContentStandardType] FOREIGN KEY([ContentStandardTypeId])

REFERENCES [edfi].[ContentStandardType] ([ContentStandardTypeId])

GO

ALTER TABLE [edfi].[AssessmentFamily] CHECK CONSTRAINT [FK_AssessmentFamily_ContentStandardType]

GO

/****** Object:  ForeignKey [FK_AssessmentFamily_GradeLevelType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AssessmentFamily]  WITH CHECK ADD  CONSTRAINT [FK_AssessmentFamily_GradeLevelType] FOREIGN KEY([AssessedGradeLevelTypeId])

REFERENCES [edfi].[GradeLevelType] ([GradeLevelTypeId])

GO

ALTER TABLE [edfi].[AssessmentFamily] CHECK CONSTRAINT [FK_AssessmentFamily_GradeLevelType]

GO

/****** Object:  ForeignKey [FK_AssessmentFamily_GradeLevelType1]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AssessmentFamily]  WITH CHECK ADD  CONSTRAINT [FK_AssessmentFamily_GradeLevelType1] FOREIGN KEY([LowestAssessedGradeLevelTypeId])

REFERENCES [edfi].[GradeLevelType] ([GradeLevelTypeId])

GO

ALTER TABLE [edfi].[AssessmentFamily] CHECK CONSTRAINT [FK_AssessmentFamily_GradeLevelType1]

GO

/****** Object:  ForeignKey [FK_AssessmentFamilyAssessmentPeriod_AssessmentFamily]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AssessmentFamilyAssessmentPeriod]  WITH CHECK ADD  CONSTRAINT [FK_AssessmentFamilyAssessmentPeriod_AssessmentFamily] FOREIGN KEY([AssessmentFamilyTitle])

REFERENCES [edfi].[AssessmentFamily] ([AssessmentFamilyTitle])

GO

ALTER TABLE [edfi].[AssessmentFamilyAssessmentPeriod] CHECK CONSTRAINT [FK_AssessmentFamilyAssessmentPeriod_AssessmentFamily]

GO

/****** Object:  ForeignKey [FK_AssessmentFamilyAssessmentPeriod_AssessmentPeriodDescriptor]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AssessmentFamilyAssessmentPeriod]  WITH CHECK ADD  CONSTRAINT [FK_AssessmentFamilyAssessmentPeriod_AssessmentPeriodDescriptor] FOREIGN KEY([AssessmentPeriodDescriptorId])

REFERENCES [edfi].[AssessmentPeriodDescriptor] ([AssessmentPeriodDescriptorId])

GO

ALTER TABLE [edfi].[AssessmentFamilyAssessmentPeriod] CHECK CONSTRAINT [FK_AssessmentFamilyAssessmentPeriod_AssessmentPeriodDescriptor]

GO

/****** Object:  ForeignKey [FK_AssessmentFamilyAssociation_Assessment]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AssessmentFamilyAssociation]  WITH CHECK ADD  CONSTRAINT [FK_AssessmentFamilyAssociation_Assessment] FOREIGN KEY([AssessmentTitle], [AssessmentAcademicSubjectTypeId], [AssessmentAssessedGradeLevelTypeId], [AssessmentVersion])

REFERENCES [edfi].[Assessment] ([AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

GO

ALTER TABLE [edfi].[AssessmentFamilyAssociation] CHECK CONSTRAINT [FK_AssessmentFamilyAssociation_Assessment]

GO

/****** Object:  ForeignKey [FK_AssessmentFamilyAssociation_AssessmentFamily]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AssessmentFamilyAssociation]  WITH CHECK ADD  CONSTRAINT [FK_AssessmentFamilyAssociation_AssessmentFamily] FOREIGN KEY([AssessmentFamilyTitle])

REFERENCES [edfi].[AssessmentFamily] ([AssessmentFamilyTitle])

GO

ALTER TABLE [edfi].[AssessmentFamilyAssociation] CHECK CONSTRAINT [FK_AssessmentFamilyAssociation_AssessmentFamily]

GO

/****** Object:  ForeignKey [FK_AssessmentFamilyIdentificationCode_AssessmentFamily]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AssessmentFamilyIdentificationCode]  WITH CHECK ADD  CONSTRAINT [FK_AssessmentFamilyIdentificationCode_AssessmentFamily] FOREIGN KEY([AssessmentFamilyTitle])

REFERENCES [edfi].[AssessmentFamily] ([AssessmentFamilyTitle])

GO

ALTER TABLE [edfi].[AssessmentFamilyIdentificationCode] CHECK CONSTRAINT [FK_AssessmentFamilyIdentificationCode_AssessmentFamily]

GO

/****** Object:  ForeignKey [FK_AssessmentFamilyIdentificationCode_AssessmentIdentificationSystemType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AssessmentFamilyIdentificationCode]  WITH CHECK ADD  CONSTRAINT [FK_AssessmentFamilyIdentificationCode_AssessmentIdentificationSystemType] FOREIGN KEY([AssessmentIdentificationSystemTypeId])

REFERENCES [edfi].[AssessmentIdentificationSystemType] ([AssessmentIdentificationSystemTypeId])

GO

ALTER TABLE [edfi].[AssessmentFamilyIdentificationCode] CHECK CONSTRAINT [FK_AssessmentFamilyIdentificationCode_AssessmentIdentificationSystemType]

GO

/****** Object:  ForeignKey [FK_AssessmentIdentificationCode_Assessment]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AssessmentIdentificationCode]  WITH CHECK ADD  CONSTRAINT [FK_AssessmentIdentificationCode_Assessment] FOREIGN KEY([AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

REFERENCES [edfi].[Assessment] ([AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

GO

ALTER TABLE [edfi].[AssessmentIdentificationCode] CHECK CONSTRAINT [FK_AssessmentIdentificationCode_Assessment]

GO

/****** Object:  ForeignKey [FK_AssessmentIdentificationCode_AssessmentIdentificationSystemType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AssessmentIdentificationCode]  WITH CHECK ADD  CONSTRAINT [FK_AssessmentIdentificationCode_AssessmentIdentificationSystemType] FOREIGN KEY([AssessmentIdentificationSystemTypeId])

REFERENCES [edfi].[AssessmentIdentificationSystemType] ([AssessmentIdentificationSystemTypeId])

GO

ALTER TABLE [edfi].[AssessmentIdentificationCode] CHECK CONSTRAINT [FK_AssessmentIdentificationCode_AssessmentIdentificationSystemType]

GO

/****** Object:  ForeignKey [FK_AssessmentItem_Assessment]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AssessmentItem]  WITH CHECK ADD  CONSTRAINT [FK_AssessmentItem_Assessment] FOREIGN KEY([AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

REFERENCES [edfi].[Assessment] ([AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

GO

ALTER TABLE [edfi].[AssessmentItem] CHECK CONSTRAINT [FK_AssessmentItem_Assessment]

GO

/****** Object:  ForeignKey [FK_AssessmentItem_LearningStandard]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AssessmentItem]  WITH CHECK ADD  CONSTRAINT [FK_AssessmentItem_LearningStandard] FOREIGN KEY([LearningStandardId])

REFERENCES [edfi].[LearningStandard] ([LearningStandardId])

GO

ALTER TABLE [edfi].[AssessmentItem] CHECK CONSTRAINT [FK_AssessmentItem_LearningStandard]

GO

/****** Object:  ForeignKey [FK_ItemCategoryType_AssessmentItem]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AssessmentItem]  WITH CHECK ADD  CONSTRAINT [FK_ItemCategoryType_AssessmentItem] FOREIGN KEY([ItemCategoryTypeId])

REFERENCES [edfi].[ItemCategoryType] ([ItemCategoryTypeId])

GO

ALTER TABLE [edfi].[AssessmentItem] CHECK CONSTRAINT [FK_ItemCategoryType_AssessmentItem]

GO

/****** Object:  ForeignKey [FK_AssessmentPerformanceLevel_Assessment]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AssessmentPerformanceLevel]  WITH CHECK ADD  CONSTRAINT [FK_AssessmentPerformanceLevel_Assessment] FOREIGN KEY([AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

REFERENCES [edfi].[Assessment] ([AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

GO

ALTER TABLE [edfi].[AssessmentPerformanceLevel] CHECK CONSTRAINT [FK_AssessmentPerformanceLevel_Assessment]

GO

/****** Object:  ForeignKey [FK_AssessmentPerformanceLevel_PerformanceLevelDescriptor]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AssessmentPerformanceLevel]  WITH CHECK ADD  CONSTRAINT [FK_AssessmentPerformanceLevel_PerformanceLevelDescriptor] FOREIGN KEY([PerformanceLevelDescriptorId])

REFERENCES [edfi].[PerformanceLevelDescriptor] ([PerformanceLevelDescriptorId])

GO

ALTER TABLE [edfi].[AssessmentPerformanceLevel] CHECK CONSTRAINT [FK_AssessmentPerformanceLevel_PerformanceLevelDescriptor]

GO

/****** Object:  ForeignKey [FK_AssessmentSection_Assessment]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AssessmentSection]  WITH CHECK ADD  CONSTRAINT [FK_AssessmentSection_Assessment] FOREIGN KEY([AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

REFERENCES [edfi].[Assessment] ([AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

GO

ALTER TABLE [edfi].[AssessmentSection] CHECK CONSTRAINT [FK_AssessmentSection_Assessment]

GO

/****** Object:  ForeignKey [FK_AssessmentSection_Section]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AssessmentSection]  WITH CHECK ADD  CONSTRAINT [FK_AssessmentSection_Section] FOREIGN KEY([SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear])

REFERENCES [edfi].[Section] ([SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear])

GO

ALTER TABLE [edfi].[AssessmentSection] CHECK CONSTRAINT [FK_AssessmentSection_Section]

GO

/****** Object:  ForeignKey [FK_AttendanceEvent_Section]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AttendanceEvent]  WITH CHECK ADD  CONSTRAINT [FK_AttendanceEvent_Section] FOREIGN KEY([SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear])

REFERENCES [edfi].[Section] ([SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear])

GO

ALTER TABLE [edfi].[AttendanceEvent] CHECK CONSTRAINT [FK_AttendanceEvent_Section]

GO

/****** Object:  ForeignKey [FK_AttendanceEvent_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AttendanceEvent]  WITH CHECK ADD  CONSTRAINT [FK_AttendanceEvent_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[AttendanceEvent] CHECK CONSTRAINT [FK_AttendanceEvent_Student]

GO

/****** Object:  ForeignKey [FK_AttendanceEventCategoryType_AttendanceEvent]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AttendanceEvent]  WITH CHECK ADD  CONSTRAINT [FK_AttendanceEventCategoryType_AttendanceEvent] FOREIGN KEY([AttendanceEventCategoryTypeId])

REFERENCES [edfi].[AttendanceEventCategoryType] ([AttendanceEventCategoryTypeId])

GO

ALTER TABLE [edfi].[AttendanceEvent] CHECK CONSTRAINT [FK_AttendanceEventCategoryType_AttendanceEvent]

GO

/****** Object:  ForeignKey [FK_AttendanceEventTypeType_AttendanceEvent]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AttendanceEvent]  WITH CHECK ADD  CONSTRAINT [FK_AttendanceEventTypeType_AttendanceEvent] FOREIGN KEY([AttendanceEventTypeId])

REFERENCES [edfi].[AttendanceEventType] ([AttendanceEventTypeId])

GO

ALTER TABLE [edfi].[AttendanceEvent] CHECK CONSTRAINT [FK_AttendanceEventTypeType_AttendanceEvent]

GO

/****** Object:  ForeignKey [FK_EducationalEnvironmentType_AttendanceEvent]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AttendanceEvent]  WITH CHECK ADD  CONSTRAINT [FK_EducationalEnvironmentType_AttendanceEvent] FOREIGN KEY([EducationalEnvironmentTypeId])

REFERENCES [edfi].[EducationalEnvironmentType] ([EducationalEnvironmentTypeId])

GO

ALTER TABLE [edfi].[AttendanceEvent] CHECK CONSTRAINT [FK_EducationalEnvironmentType_AttendanceEvent]

GO

/****** Object:  ForeignKey [FK_BehaviorDescriptor_BehaviorCategoryType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[BehaviorDescriptor]  WITH CHECK ADD  CONSTRAINT [FK_BehaviorDescriptor_BehaviorCategoryType] FOREIGN KEY([BehaviorCategoryTypeId])

REFERENCES [edfi].[BehaviorCategoryType] ([BehaviorCategoryTypeId])

GO

ALTER TABLE [edfi].[BehaviorDescriptor] CHECK CONSTRAINT [FK_BehaviorDescriptor_BehaviorCategoryType]

GO

/****** Object:  ForeignKey [FK_BellSchedule_MeetingTime]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[BellSchedule]  WITH CHECK ADD  CONSTRAINT [FK_BellSchedule_MeetingTime] FOREIGN KEY([SchoolId], [ClassPeriodName], [WeekNumber])

REFERENCES [edfi].[MeetingTime] ([SchoolId], [ClassPeriodName], [WeekNumber])

GO

ALTER TABLE [edfi].[BellSchedule] CHECK CONSTRAINT [FK_BellSchedule_MeetingTime]

GO

/****** Object:  ForeignKey [FK_BellSchedule_Session]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[BellSchedule]  WITH CHECK ADD  CONSTRAINT [FK_BellSchedule_Session] FOREIGN KEY([SchoolId], [TermTypeId], [SchoolYear])

REFERENCES [edfi].[Session] ([EducationOrganizationId], [TermTypeId], [SchoolYear])

GO

ALTER TABLE [edfi].[BellSchedule] CHECK CONSTRAINT [FK_BellSchedule_Session]

GO

/****** Object:  ForeignKey [FK_BellScheduleCalendarDate_BellSchedule]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[BellScheduleCalendarDate]  WITH CHECK ADD  CONSTRAINT [FK_BellScheduleCalendarDate_BellSchedule] FOREIGN KEY([SchoolId], [ClassPeriodName], [WeekNumber], [TermTypeId], [SchoolYear], [BellScheduleName])

REFERENCES [edfi].[BellSchedule] ([SchoolId], [ClassPeriodName], [WeekNumber], [TermTypeId], [SchoolYear], [BellScheduleName])

GO

ALTER TABLE [edfi].[BellScheduleCalendarDate] CHECK CONSTRAINT [FK_BellScheduleCalendarDate_BellSchedule]

GO

/****** Object:  ForeignKey [FK_BellScheduleCalendarDate_CalendarDate]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[BellScheduleCalendarDate]  WITH CHECK ADD  CONSTRAINT [FK_BellScheduleCalendarDate_CalendarDate] FOREIGN KEY([SchoolId], [Date])

REFERENCES [edfi].[CalendarDate] ([EducationOrganizationId], [Date])

GO

ALTER TABLE [edfi].[BellScheduleCalendarDate] CHECK CONSTRAINT [FK_BellScheduleCalendarDate_CalendarDate]

GO

/****** Object:  ForeignKey [FK_BellScheduleGradeLevels_BellSchedule]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[BellScheduleGradeLevels]  WITH CHECK ADD  CONSTRAINT [FK_BellScheduleGradeLevels_BellSchedule] FOREIGN KEY([SchoolId], [ClassPeriodName], [WeekNumber], [TermTypeId], [SchoolYear], [BellScheduleName])

REFERENCES [edfi].[BellSchedule] ([SchoolId], [ClassPeriodName], [WeekNumber], [TermTypeId], [SchoolYear], [BellScheduleName])

GO

ALTER TABLE [edfi].[BellScheduleGradeLevels] CHECK CONSTRAINT [FK_BellScheduleGradeLevels_BellSchedule]

GO

/****** Object:  ForeignKey [FK_BellScheduleGradeLevels_GradeLevelType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[BellScheduleGradeLevels]  WITH CHECK ADD  CONSTRAINT [FK_BellScheduleGradeLevels_GradeLevelType] FOREIGN KEY([GradeLevelTypeId])

REFERENCES [edfi].[GradeLevelType] ([GradeLevelTypeId])

GO

ALTER TABLE [edfi].[BellScheduleGradeLevels] CHECK CONSTRAINT [FK_BellScheduleGradeLevels_GradeLevelType]

GO

/****** Object:  ForeignKey [FK_Budget_Account]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Budget]  WITH CHECK ADD  CONSTRAINT [FK_Budget_Account] FOREIGN KEY([EducationOrganizationId], [AccountNumber], [FiscalYear])

REFERENCES [edfi].[Account] ([EducationOrganizationId], [AccountNumber], [FiscalYear])

GO

ALTER TABLE [edfi].[Budget] CHECK CONSTRAINT [FK_Budget_Account]

GO

/****** Object:  ForeignKey [FK_GradingPeriod_CalendarBeginDate]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[GradingPeriod]  WITH CHECK ADD  CONSTRAINT [FK_GradingPeriod_CalendarBeginDate] FOREIGN KEY([EducationOrganizationId], [BeginDate])

REFERENCES [edfi].[CalendarDate] ([EducationOrganizationId], [Date])

GO

ALTER TABLE [edfi].[GradingPeriod] CHECK CONSTRAINT [FK_GradingPeriod_CalendarBeginDate]

GO

/****** Object:  ForeignKey [FK_GradingPeriod_CalendarEndDate]    Script Date: 10/20/2011 10:44:06 ******/



ALTER TABLE [edfi].[GradingPeriod]  WITH CHECK ADD  CONSTRAINT [FK_GradingPeriod_CalendarEndDate] FOREIGN KEY([EducationOrganizationId], [EndDate])

REFERENCES [edfi].[CalendarDate] ([EducationOrganizationId], [Date])

GO

ALTER TABLE [edfi].[GradingPeriod] CHECK CONSTRAINT [FK_GradingPeriod_CalendarEndDate]

GO

/****** Object:  ForeignKey [FK_ClassRanking_StudentAcademicRecord]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ClassRanking]  WITH CHECK ADD  CONSTRAINT [FK_ClassRanking_StudentAcademicRecord] FOREIGN KEY([StudentUSI], [EducationOrganizationId], [SchoolYear], [TermTypeId])

REFERENCES [edfi].[StudentAcademicRecord] ([StudentUSI], [EducationOrganizationId], [SchoolYear], [TermTypeId])

GO

ALTER TABLE [edfi].[ClassRanking] CHECK CONSTRAINT [FK_ClassRanking_StudentAcademicRecord]

GO

/****** Object:  ForeignKey [FK_AcademicSubjectType_Cohort]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Cohort]  WITH CHECK ADD  CONSTRAINT [FK_AcademicSubjectType_Cohort] FOREIGN KEY([AcademicSubjectTypeId])

REFERENCES [edfi].[AcademicSubjectType] ([AcademicSubjectTypeId])

GO

ALTER TABLE [edfi].[Cohort] CHECK CONSTRAINT [FK_AcademicSubjectType_Cohort]

GO

/****** Object:  ForeignKey [FK_Cohort_EducationOrganization]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Cohort]  WITH CHECK ADD  CONSTRAINT [FK_Cohort_EducationOrganization] FOREIGN KEY([EducationOrganizationId])

REFERENCES [edfi].[EducationOrganization] ([EducationOrganizationId])

GO

ALTER TABLE [edfi].[Cohort] CHECK CONSTRAINT [FK_Cohort_EducationOrganization]

GO

/****** Object:  ForeignKey [FK_CohortScopeType_Cohort]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Cohort]  WITH CHECK ADD  CONSTRAINT [FK_CohortScopeType_Cohort] FOREIGN KEY([CohortScopeTypeId])

REFERENCES [edfi].[CohortScopeType] ([CohortScopeTypeId])

GO

ALTER TABLE [edfi].[Cohort] CHECK CONSTRAINT [FK_CohortScopeType_Cohort]

GO

/****** Object:  ForeignKey [FK_CohortTypeType_Cohort]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Cohort]  WITH CHECK ADD  CONSTRAINT [FK_CohortTypeType_Cohort] FOREIGN KEY([CohortTypeId])

REFERENCES [edfi].[CohortType] ([CohortTypeId])

GO

ALTER TABLE [edfi].[Cohort] CHECK CONSTRAINT [FK_CohortTypeType_Cohort]

GO

/****** Object:  ForeignKey [FK_CohortProgram_Cohort]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CohortProgram]  WITH CHECK ADD  CONSTRAINT [FK_CohortProgram_Cohort] FOREIGN KEY([EducationOrganizationId], [CohortIdentifier])

REFERENCES [edfi].[Cohort] ([EducationOrganizationId], [CohortIdentifier])

GO

ALTER TABLE [edfi].[CohortProgram] CHECK CONSTRAINT [FK_CohortProgram_Cohort]

GO

/****** Object:  ForeignKey [FK_CohortProgram_Program]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CohortProgram]  WITH CHECK ADD  CONSTRAINT [FK_CohortProgram_Program] FOREIGN KEY([EducationOrganizationId], [ProgramTypeId])

REFERENCES [edfi].[Program] ([EducationOrganizationId], [ProgramTypeId])

GO

ALTER TABLE [edfi].[CohortProgram] CHECK CONSTRAINT [FK_CohortProgram_Program]

GO

/****** Object:  ForeignKey [FK_CompetencyLevelDescriptor_PerformanceBaseType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CompetencyLevelDescriptor]  WITH CHECK ADD  CONSTRAINT [FK_CompetencyLevelDescriptor_PerformanceBaseType] FOREIGN KEY([PerformanceBaseConversionTypeId])

REFERENCES [edfi].[PerformanceBaseType] ([PerformanceBaseTypeId])

GO

ALTER TABLE [edfi].[CompetencyLevelDescriptor] CHECK CONSTRAINT [FK_CompetencyLevelDescriptor_PerformanceBaseType]

GO

/****** Object:  ForeignKey [FK_ContractedStaff_Account]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ContractedStaff]  WITH CHECK ADD  CONSTRAINT [FK_ContractedStaff_Account] FOREIGN KEY([EducationOrganizationId], [AccountNumber], [FiscalYear])

REFERENCES [edfi].[Account] ([EducationOrganizationId], [AccountNumber], [FiscalYear])

GO

ALTER TABLE [edfi].[ContractedStaff] CHECK CONSTRAINT [FK_ContractedStaff_Account]

GO

/****** Object:  ForeignKey [FK_ContractedStaff_Staff]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ContractedStaff]  WITH CHECK ADD  CONSTRAINT [FK_ContractedStaff_Staff] FOREIGN KEY([StaffUSI])

REFERENCES [edfi].[Staff] ([StaffUSI])

GO

ALTER TABLE [edfi].[ContractedStaff] CHECK CONSTRAINT [FK_ContractedStaff_Staff]

GO

/****** Object:  ForeignKey [FK_Course_CompetencyLevelDescriptor]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Course]  WITH CHECK ADD  CONSTRAINT [FK_Course_CompetencyLevelDescriptor] FOREIGN KEY([CompetencyLevelDescriptorId])

REFERENCES [edfi].[CompetencyLevelDescriptor] ([CompetencyLevelDescriptorId])

GO

ALTER TABLE [edfi].[Course] CHECK CONSTRAINT [FK_Course_CompetencyLevelDescriptor]

GO

/****** Object:  ForeignKey [FK_Course_EducationOrganization]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Course]  WITH CHECK ADD  CONSTRAINT [FK_Course_EducationOrganization] FOREIGN KEY([EducationOrganizationId])

REFERENCES [edfi].[EducationOrganization] ([EducationOrganizationId])

GO

ALTER TABLE [edfi].[Course] CHECK CONSTRAINT [FK_Course_EducationOrganization]

GO

/****** Object:  ForeignKey [FK_CourseGPAApplicabilityType_Course]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Course]  WITH CHECK ADD  CONSTRAINT [FK_CourseGPAApplicabilityType_Course] FOREIGN KEY([CourseGPAApplicabilityTypeId])

REFERENCES [edfi].[CourseGPAApplicabilityType] ([CourseGPAApplicabilityTypeId])

GO

ALTER TABLE [edfi].[Course] CHECK CONSTRAINT [FK_CourseGPAApplicabilityType_Course]

GO

/****** Object:  ForeignKey [FK_CourseLevelType_Course]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Course]  WITH CHECK ADD  CONSTRAINT [FK_CourseLevelType_Course] FOREIGN KEY([CourseLevelTypeId])

REFERENCES [edfi].[CourseLevelType] ([CourseLevelTypeId])

GO

ALTER TABLE [edfi].[Course] CHECK CONSTRAINT [FK_CourseLevelType_Course]

GO

/****** Object:  ForeignKey [FK_AcademicSubjectType_Course]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Course]  WITH CHECK ADD  CONSTRAINT [FK_AcademicSubjectType_Course] FOREIGN KEY([SubjectAreaTypeId])

REFERENCES [edfi].[AcademicSubjectType] ([AcademicSubjectTypeId])

GO

ALTER TABLE [edfi].[Course] CHECK CONSTRAINT [FK_AcademicSubjectType_Course]

GO

/****** Object:  ForeignKey [FK_CourseCodeIdentificationCode_Course]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CourseCodeIdentificationCode]  WITH CHECK ADD  CONSTRAINT [FK_CourseCodeIdentificationCode_Course] FOREIGN KEY([EducationOrganizationId], [IdentityCourseCode])

REFERENCES [edfi].[Course] ([EducationOrganizationId], [IdentityCourseCode])

GO

ALTER TABLE [edfi].[CourseCodeIdentificationCode] CHECK CONSTRAINT [FK_CourseCodeIdentificationCode_Course]

GO

/****** Object:  ForeignKey [FK_CourseCodeIdentificationCode_CourseCodeSystemType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CourseCodeIdentificationCode]  WITH CHECK ADD  CONSTRAINT [FK_CourseCodeIdentificationCode_CourseCodeSystemType] FOREIGN KEY([CourseCodeSystemTypeId])

REFERENCES [edfi].[CourseCodeSystemType] ([CourseCodeSystemTypeId])

GO

ALTER TABLE [edfi].[CourseCodeIdentificationCode] CHECK CONSTRAINT [FK_CourseCodeIdentificationCode_CourseCodeSystemType]

GO

/****** Object:  ForeignKey [FK_CourseLevelCharacteristics_Course]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CourseLevelCharacteristics]  WITH CHECK ADD  CONSTRAINT [FK_CourseLevelCharacteristics_Course] FOREIGN KEY([EducationOrganizationId], [IdentityCourseCode])

REFERENCES [edfi].[Course] ([EducationOrganizationId], [IdentityCourseCode])

GO

ALTER TABLE [edfi].[CourseLevelCharacteristics] CHECK CONSTRAINT [FK_CourseLevelCharacteristics_Course]

GO

/****** Object:  ForeignKey [FK_CourseLevelCharacteristicsType_CourseLevelCharacteristics]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CourseLevelCharacteristics]  WITH CHECK ADD  CONSTRAINT [FK_CourseLevelCharacteristicsType_CourseLevelCharacteristics] FOREIGN KEY([CourseLevelCharacteristicsTypeId])

REFERENCES [edfi].[CourseLevelCharacteristicsType] ([CourseLevelCharacteristicsTypeId])

GO

ALTER TABLE [edfi].[CourseLevelCharacteristics] CHECK CONSTRAINT [FK_CourseLevelCharacteristicsType_CourseLevelCharacteristics]

GO

/****** Object:  ForeignKey [FK_CourseGradesOffered_Course]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CourseGradesOffered]  WITH CHECK ADD  CONSTRAINT [FK_CourseGradesOffered_Course] FOREIGN KEY([EducationOrganizationId], [IdentityCourseCode])

REFERENCES [edfi].[Course] ([EducationOrganizationId], [IdentityCourseCode])

GO

ALTER TABLE [edfi].[CourseGradesOffered] CHECK CONSTRAINT [FK_CourseGradesOffered_Course]

GO

/****** Object:  ForeignKey [FK_CourseGradesOffered_GradeLevelType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CourseGradesOffered]  WITH CHECK ADD  CONSTRAINT [FK_CourseGradesOffered_GradeLevelType] FOREIGN KEY([GradesOfferedTypeId])

REFERENCES [edfi].[GradeLevelType] ([GradeLevelTypeId])

GO

ALTER TABLE [edfi].[CourseGradesOffered] CHECK CONSTRAINT [FK_CourseGradesOffered_GradeLevelType]

GO

/****** Object:  ForeignKey [FK_CourseLearningObjective_Course]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CourseLearningObjective]  WITH CHECK ADD  CONSTRAINT [FK_CourseLearningObjective_Course] FOREIGN KEY([EducationOrganizationId], [IdentityCourseCode])

REFERENCES [edfi].[Course] ([EducationOrganizationId], [IdentityCourseCode])

GO

ALTER TABLE [edfi].[CourseLearningObjective] CHECK CONSTRAINT [FK_CourseLearningObjective_Course]

GO

/****** Object:  ForeignKey [FK_CourseLearningObjective_LearningObjective]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CourseLearningObjective]  WITH CHECK ADD  CONSTRAINT [FK_CourseLearningObjective_LearningObjective] FOREIGN KEY([Objective], [AcademicSubjectTypeId], [ObjectiveGradeLevelTypeId])

REFERENCES [edfi].[LearningObjective] ([Objective], [AcademicSubjectTypeId], [ObjectiveGradeLevelTypeId])

GO

ALTER TABLE [edfi].[CourseLearningObjective] CHECK CONSTRAINT [FK_CourseLearningObjective_LearningObjective]

GO

/****** Object:  ForeignKey [FK_CourseLearningStandard_Course]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CourseLearningStandard]  WITH CHECK ADD  CONSTRAINT [FK_CourseLearningStandard_Course] FOREIGN KEY([EducationOrganizationId], [IdentityCourseCode])

REFERENCES [edfi].[Course] ([EducationOrganizationId], [IdentityCourseCode])

GO

ALTER TABLE [edfi].[CourseLearningStandard] CHECK CONSTRAINT [FK_CourseLearningStandard_Course]

GO

/****** Object:  ForeignKey [FK_CourseLearningStandard_LearningStandard]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CourseLearningStandard]  WITH CHECK ADD  CONSTRAINT [FK_CourseLearningStandard_LearningStandard] FOREIGN KEY([LearningStandardId])

REFERENCES [edfi].[LearningStandard] ([LearningStandardId])

GO

ALTER TABLE [edfi].[CourseLearningStandard] CHECK CONSTRAINT [FK_CourseLearningStandard_LearningStandard]

GO

/****** Object:  ForeignKey [FK_CourseOffering_Course]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CourseOffering]  WITH CHECK ADD  CONSTRAINT [FK_CourseOffering_Course] FOREIGN KEY([SchoolId], [IdentityCourseCode])

REFERENCES [edfi].[Course] ([EducationOrganizationId], [IdentityCourseCode])

GO

ALTER TABLE [edfi].[CourseOffering] CHECK CONSTRAINT [FK_CourseOffering_Course]

GO

/****** Object:  ForeignKey [FK_CourseOffering_School]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CourseOffering]  WITH CHECK ADD  CONSTRAINT [FK_CourseOffering_School] FOREIGN KEY([SchoolId])

REFERENCES [edfi].[School] ([SchoolId])

GO

ALTER TABLE [edfi].[CourseOffering] CHECK CONSTRAINT [FK_CourseOffering_School]

GO

/****** Object:  ForeignKey [FK_CourseOffering_Session]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CourseOffering]  WITH CHECK ADD  CONSTRAINT [FK_CourseOffering_Session] FOREIGN KEY([SchoolId], [TermTypeId], [SchoolYear])

REFERENCES [edfi].[Session] ([EducationOrganizationId], [TermTypeId], [SchoolYear])

GO

ALTER TABLE [edfi].[CourseOffering] CHECK CONSTRAINT [FK_CourseOffering_Session]

GO

/****** Object:  ForeignKey [FK_CourseAttemptResultType_CourseTranscript]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CourseTranscript]  WITH CHECK ADD  CONSTRAINT [FK_CourseAttemptResultType_CourseTranscript] FOREIGN KEY([CourseAttemptResultTypeId])

REFERENCES [edfi].[CourseAttemptResultType] ([CourseAttemptResultTypeId])

GO

ALTER TABLE [edfi].[CourseTranscript] CHECK CONSTRAINT [FK_CourseAttemptResultType_CourseTranscript]

GO

/****** Object:  ForeignKey [FK_CourseRepeatCodeType_CourseTranscript]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CourseTranscript]  WITH CHECK ADD  CONSTRAINT [FK_CourseRepeatCodeType_CourseTranscript] FOREIGN KEY([CourseRepeatCodeTypeId])

REFERENCES [edfi].[CourseRepeatCodeType] ([CourseRepeatCodeTypeId])

GO

ALTER TABLE [edfi].[CourseTranscript] CHECK CONSTRAINT [FK_CourseRepeatCodeType_CourseTranscript]

GO

/****** Object:  ForeignKey [FK_CourseTranscript_Course]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CourseTranscript]  WITH CHECK ADD  CONSTRAINT [FK_CourseTranscript_Course] FOREIGN KEY([EducationOrganizationId], [IdentityCourseCode])

REFERENCES [edfi].[Course] ([EducationOrganizationId], [IdentityCourseCode])

GO

ALTER TABLE [edfi].[CourseTranscript] CHECK CONSTRAINT [FK_CourseTranscript_Course]

GO

/****** Object:  ForeignKey [FK_CourseTranscript_CreditType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CourseTranscript]  WITH CHECK ADD  CONSTRAINT [FK_CourseTranscript_CreditType] FOREIGN KEY([AttemptedCreditTypeId])

REFERENCES [edfi].[CreditType] ([CreditTypeId])

GO

ALTER TABLE [edfi].[CourseTranscript] CHECK CONSTRAINT [FK_CourseTranscript_CreditType]

GO

/****** Object:  ForeignKey [FK_CourseTranscript_EducationOrganization]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CourseTranscript]  WITH CHECK ADD  CONSTRAINT [FK_CourseTranscript_EducationOrganization] FOREIGN KEY([EducationOrganizationId])

REFERENCES [edfi].[EducationOrganization] ([EducationOrganizationId])

GO

ALTER TABLE [edfi].[CourseTranscript] CHECK CONSTRAINT [FK_CourseTranscript_EducationOrganization]

GO

/****** Object:  ForeignKey [FK_CourseTranscript_GradeLevelType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CourseTranscript]  WITH CHECK ADD  CONSTRAINT [FK_CourseTranscript_GradeLevelType] FOREIGN KEY([GradeLevelWhenTakenTypeId])

REFERENCES [edfi].[GradeLevelType] ([GradeLevelTypeId])

GO

ALTER TABLE [edfi].[CourseTranscript] CHECK CONSTRAINT [FK_CourseTranscript_GradeLevelType]

GO

/****** Object:  ForeignKey [FK_CourseTranscript_StudentAcademicRecord]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CourseTranscript]  WITH CHECK ADD  CONSTRAINT [FK_CourseTranscript_StudentAcademicRecord] FOREIGN KEY([StudentUSI], [EducationOrganizationId], [SchoolYear], [TermTypeId])

REFERENCES [edfi].[StudentAcademicRecord] ([StudentUSI], [EducationOrganizationId], [SchoolYear], [TermTypeId])

GO

ALTER TABLE [edfi].[CourseTranscript] CHECK CONSTRAINT [FK_CourseTranscript_StudentAcademicRecord]

GO

/****** Object:  ForeignKey [FK_MethodCreditEarnedType_CourseTranscript]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CourseTranscript]  WITH CHECK ADD  CONSTRAINT [FK_MethodCreditEarnedType_CourseTranscript] FOREIGN KEY([MethodCreditEarnedTypeId])

REFERENCES [edfi].[MethodCreditEarnedType] ([MethodCreditEarnedTypeId])

GO

ALTER TABLE [edfi].[CourseTranscript] CHECK CONSTRAINT [FK_MethodCreditEarnedType_CourseTranscript]

GO

/****** Object:  ForeignKey [FK_Credential_CredentialFieldDescriptor]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Credential]  WITH CHECK ADD  CONSTRAINT [FK_Credential_CredentialFieldDescriptor] FOREIGN KEY([CredentialFieldDescriptorId])

REFERENCES [edfi].[CredentialFieldDescriptor] ([CredentialFieldDescriptorId])

GO

ALTER TABLE [edfi].[Credential] CHECK CONSTRAINT [FK_Credential_CredentialFieldDescriptor]

GO

/****** Object:  ForeignKey [FK_AcademicSubjectType_CredentialFieldDescriptor]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[CredentialFieldDescriptor]  WITH CHECK ADD  CONSTRAINT [FK_AcademicSubjectType_CredentialFieldDescriptor] FOREIGN KEY([AcademicSubjectTypeId])

REFERENCES [edfi].[AcademicSubjectType] ([AcademicSubjectTypeId])

GO

ALTER TABLE [edfi].[CredentialFieldDescriptor] CHECK CONSTRAINT [FK_AcademicSubjectType_CredentialFieldDescriptor]

GO

/****** Object:  ForeignKey [FK_Credential_Staff]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Credential]  WITH CHECK ADD  CONSTRAINT [FK_Credential_Staff] FOREIGN KEY([StaffUSI])

REFERENCES [edfi].[Staff] ([StaffUSI])

GO

ALTER TABLE [edfi].[Credential] CHECK CONSTRAINT [FK_Credential_Staff]

GO

/****** Object:  ForeignKey [FK_CredentialTypeType_Credential]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Credential]  WITH CHECK ADD  CONSTRAINT [FK_CredentialTypeType_Credential] FOREIGN KEY([CredentialTypeId])

REFERENCES [edfi].[CredentialType] ([CredentialTypeId])

GO

ALTER TABLE [edfi].[Credential] CHECK CONSTRAINT [FK_CredentialTypeType_Credential]

GO

/****** Object:  ForeignKey [FK_LevelType_Credential]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Credential]  WITH CHECK ADD  CONSTRAINT [FK_LevelType_Credential] FOREIGN KEY([LevelTypeId])

REFERENCES [edfi].[LevelType] ([LevelTypeId])

GO

ALTER TABLE [edfi].[Credential] CHECK CONSTRAINT [FK_LevelType_Credential]

GO

/****** Object:  ForeignKey [FK_TeachingCredentialBasisType_Credential]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Credential]  WITH CHECK ADD  CONSTRAINT [FK_TeachingCredentialBasisType_Credential] FOREIGN KEY([TeachingCredentialBasisTypeId])

REFERENCES [edfi].[TeachingCredentialBasisType] ([TeachingCredentialBasisTypeId])

GO

ALTER TABLE [edfi].[Credential] CHECK CONSTRAINT [FK_TeachingCredentialBasisType_Credential]

GO

/****** Object:  ForeignKey [FK_TeachingCredentialTypeType_Credential]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Credential]  WITH CHECK ADD  CONSTRAINT [FK_TeachingCredentialTypeType_Credential] FOREIGN KEY([TeachingCredentialTypeId])

REFERENCES [edfi].[TeachingCredentialType] ([TeachingCredentialTypeId])

GO

ALTER TABLE [edfi].[Credential] CHECK CONSTRAINT [FK_TeachingCredentialTypeType_Credential]

GO

/****** Object:  ForeignKey [FK_Diploma_DiplomaLevelType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Diploma]  WITH CHECK ADD  CONSTRAINT [FK_Diploma_DiplomaLevelType] FOREIGN KEY([DiplomaLevelTypeId])

REFERENCES [edfi].[DiplomaLevelType] ([DiplomaLevelTypeId])

GO

ALTER TABLE [edfi].[Diploma] CHECK CONSTRAINT [FK_Diploma_DiplomaLevelType]

GO

/****** Object:  ForeignKey [FK_Diploma_DiplomaType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Diploma]  WITH CHECK ADD  CONSTRAINT [FK_Diploma_DiplomaType] FOREIGN KEY([DiplomaTypeId])

REFERENCES [edfi].[DiplomaType] ([DiplomaTypeId])

GO

ALTER TABLE [edfi].[Diploma] CHECK CONSTRAINT [FK_Diploma_DiplomaType]

GO

/****** Object:  ForeignKey [FK_Diploma_School]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Diploma]  WITH CHECK ADD  CONSTRAINT [FK_Diploma_School] FOREIGN KEY([SchoolId])

REFERENCES [edfi].[School] ([SchoolId])

GO

ALTER TABLE [edfi].[Diploma] CHECK CONSTRAINT [FK_Diploma_School]

GO

/****** Object:  ForeignKey [FK_Diploma_StudentAcademicRecord]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Diploma]  WITH CHECK ADD  CONSTRAINT [FK_Diploma_StudentAcademicRecord] FOREIGN KEY([StudentUSI], [SchoolId], [SchoolYear], [TermTypeId])

REFERENCES [edfi].[StudentAcademicRecord] ([StudentUSI], [EducationOrganizationId], [SchoolYear], [TermTypeId])

GO

ALTER TABLE [edfi].[Diploma] CHECK CONSTRAINT [FK_Diploma_StudentAcademicRecord]

GO

/****** Object:  ForeignKey [FK_Disability_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Disability]  WITH CHECK ADD  CONSTRAINT [FK_Disability_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[Disability] CHECK CONSTRAINT [FK_Disability_Student]

GO

/****** Object:  ForeignKey [FK_DisabilityType_Disability]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Disability]  WITH CHECK ADD  CONSTRAINT [FK_DisabilityType_Disability] FOREIGN KEY([DisabilityTypeId])

REFERENCES [edfi].[DisabilityType] ([DisabilityTypeId])

GO

ALTER TABLE [edfi].[Disability] CHECK CONSTRAINT [FK_DisabilityType_Disability]

GO

/****** Object:  ForeignKey [FK_DisciplineAction_DisciplineActionLengthDifferenceReasonType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[DisciplineAction]  WITH CHECK ADD  CONSTRAINT [FK_DisciplineAction_DisciplineActionLengthDifferenceReasonType] FOREIGN KEY([DisciplineActionLengthDifferenceReasonTypeId])

REFERENCES [edfi].[DisciplineActionLengthDifferenceReasonType] ([DisciplineActionLengthDifferenceReasonTypeId])

GO

ALTER TABLE [edfi].[DisciplineAction] CHECK CONSTRAINT [FK_DisciplineAction_DisciplineActionLengthDifferenceReasonType]

GO

/****** Object:  ForeignKey [FK_DisciplineAction_DisciplineIncident]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[DisciplineAction]  WITH CHECK ADD  CONSTRAINT [FK_DisciplineAction_DisciplineIncident] FOREIGN KEY([SchoolId], [IncidentIdentifier])

REFERENCES [edfi].[DisciplineIncident] ([SchoolId], [IncidentIdentifier])

GO

ALTER TABLE [edfi].[DisciplineAction] CHECK CONSTRAINT [FK_DisciplineAction_DisciplineIncident]

GO

/****** Object:  ForeignKey [FK_DisciplineAction_School]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[DisciplineAction]  WITH CHECK ADD  CONSTRAINT [FK_DisciplineAction_School] FOREIGN KEY([AssignmentSchoolId])

REFERENCES [edfi].[School] ([SchoolId])

GO

ALTER TABLE [edfi].[DisciplineAction] CHECK CONSTRAINT [FK_DisciplineAction_School]

GO

/****** Object:  ForeignKey [FK_DisciplineAction_School2]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[DisciplineAction]  WITH CHECK ADD  CONSTRAINT [FK_DisciplineAction_School2] FOREIGN KEY([ResponsibilitySchoolId])

REFERENCES [edfi].[School] ([SchoolId])

GO

ALTER TABLE [edfi].[DisciplineAction] CHECK CONSTRAINT [FK_DisciplineAction_School2]

GO

/****** Object:  ForeignKey [FK_DisciplineAction_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[DisciplineAction]  WITH CHECK ADD  CONSTRAINT [FK_DisciplineAction_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[DisciplineAction] CHECK CONSTRAINT [FK_DisciplineAction_Student]

GO

/****** Object:  ForeignKey [FK_DisciplineActionDiscipline_DisciplineAction]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[DisciplineActionDiscipline]  WITH CHECK ADD  CONSTRAINT [FK_DisciplineActionDiscipline_DisciplineAction] FOREIGN KEY([StudentUSI], [SchoolId], [IncidentIdentifier], [DisciplineActionIdentifier], [DisciplineDate])

REFERENCES [edfi].[DisciplineAction] ([StudentUSI], [SchoolId], [IncidentIdentifier], [DisciplineActionIdentifier], [DisciplineDate])

GO

ALTER TABLE [edfi].[DisciplineActionDiscipline] CHECK CONSTRAINT [FK_DisciplineActionDiscipline_DisciplineAction]

GO

/****** Object:  ForeignKey [FK_DisciplineActionDiscipline_DisciplineDescriptor]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[DisciplineActionDiscipline]  WITH CHECK ADD  CONSTRAINT [FK_DisciplineActionDiscipline_DisciplineDescriptor] FOREIGN KEY([DisciplineDescriptorId])

REFERENCES [edfi].[DisciplineDescriptor] ([DisciplineDescriptorId])

GO

ALTER TABLE [edfi].[DisciplineActionDiscipline] CHECK CONSTRAINT [FK_DisciplineActionDiscipline_DisciplineDescriptor]

GO

/****** Object:  ForeignKey [FK_DisciplineActionStaff_DisciplineAction]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[DisciplineActionStaff]  WITH CHECK ADD  CONSTRAINT [FK_DisciplineActionStaff_DisciplineAction] FOREIGN KEY([StudentUSI], [SchoolId], [IncidentIdentifier], [DisciplineActionIdentifier], [DisciplineDate])

REFERENCES [edfi].[DisciplineAction] ([StudentUSI], [SchoolId], [IncidentIdentifier], [DisciplineActionIdentifier], [DisciplineDate])

GO

ALTER TABLE [edfi].[DisciplineActionStaff] CHECK CONSTRAINT [FK_DisciplineActionStaff_DisciplineAction]

GO

/****** Object:  ForeignKey [FK_DisciplineActionStaff_Staff]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[DisciplineActionStaff]  WITH CHECK ADD  CONSTRAINT [FK_DisciplineActionStaff_Staff] FOREIGN KEY([StaffUSI])

REFERENCES [edfi].[Staff] ([StaffUSI])

GO

ALTER TABLE [edfi].[DisciplineActionStaff] CHECK CONSTRAINT [FK_DisciplineActionStaff_Staff]

GO

/****** Object:  ForeignKey [FK_DisciplineIncident_School]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[DisciplineIncident]  WITH CHECK ADD  CONSTRAINT [FK_DisciplineIncident_School] FOREIGN KEY([SchoolId])

REFERENCES [edfi].[School] ([SchoolId])

GO

ALTER TABLE [edfi].[DisciplineIncident] CHECK CONSTRAINT [FK_DisciplineIncident_School]

GO

/****** Object:  ForeignKey [FK_DisciplineIncident_Staff]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[DisciplineIncident]  WITH CHECK ADD  CONSTRAINT [FK_DisciplineIncident_Staff] FOREIGN KEY([StaffUSI])

REFERENCES [edfi].[Staff] ([StaffUSI])

GO

ALTER TABLE [edfi].[DisciplineIncident] CHECK CONSTRAINT [FK_DisciplineIncident_Staff]

GO

/****** Object:  ForeignKey [FK_IncidentLocationType_DisciplineIncident]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[DisciplineIncident]  WITH CHECK ADD  CONSTRAINT [FK_IncidentLocationType_DisciplineIncident] FOREIGN KEY([IncidentLocationTypeId])

REFERENCES [edfi].[IncidentLocationType] ([IncidentLocationTypeId])

GO

ALTER TABLE [edfi].[DisciplineIncident] CHECK CONSTRAINT [FK_IncidentLocationType_DisciplineIncident]

GO

/****** Object:  ForeignKey [FK_ReporterDescriptionType_DisciplineIncident]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[DisciplineIncident]  WITH CHECK ADD  CONSTRAINT [FK_ReporterDescriptionType_DisciplineIncident] FOREIGN KEY([ReporterDescriptionTypeId])

REFERENCES [edfi].[ReporterDescriptionType] ([ReporterDescriptionTypeId])

GO

ALTER TABLE [edfi].[DisciplineIncident] CHECK CONSTRAINT [FK_ReporterDescriptionType_DisciplineIncident]

GO

/****** Object:  ForeignKey [FK_DisciplineIncidentBehavior_BehaviorDescriptor]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[DisciplineIncidentBehavior]  WITH CHECK ADD  CONSTRAINT [FK_DisciplineIncidentBehavior_BehaviorDescriptor] FOREIGN KEY([BehaviorDescriptorId])

REFERENCES [edfi].[BehaviorDescriptor] ([BehaviorDescriptorId])

GO

ALTER TABLE [edfi].[DisciplineIncidentBehavior] CHECK CONSTRAINT [FK_DisciplineIncidentBehavior_BehaviorDescriptor]

GO

/****** Object:  ForeignKey [FK_DisciplineIncidentBehavior_DisciplineIncident]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[DisciplineIncidentBehavior]  WITH CHECK ADD  CONSTRAINT [FK_DisciplineIncidentBehavior_DisciplineIncident] FOREIGN KEY([SchoolId], [IncidentIdentifier])

REFERENCES [edfi].[DisciplineIncident] ([SchoolId], [IncidentIdentifier])

GO

ALTER TABLE [edfi].[DisciplineIncidentBehavior] CHECK CONSTRAINT [FK_DisciplineIncidentBehavior_DisciplineIncident]

GO

/****** Object:  ForeignKey [FK_DisciplineIncidentSecondaryBehavior_BehaviorCategoryType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[DisciplineIncidentSecondaryBehavior]  WITH CHECK ADD  CONSTRAINT [FK_DisciplineIncidentSecondaryBehavior_BehaviorCategoryType] FOREIGN KEY([BehaviorCategoryTypeId])

REFERENCES [edfi].[BehaviorCategoryType] ([BehaviorCategoryTypeId])

GO

ALTER TABLE [edfi].[DisciplineIncidentSecondaryBehavior] CHECK CONSTRAINT [FK_DisciplineIncidentSecondaryBehavior_BehaviorCategoryType]

GO

/****** Object:  ForeignKey [FK_SecondaryIncidentBehavior_DisciplineIncident]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[DisciplineIncidentSecondaryBehavior]  WITH CHECK ADD  CONSTRAINT [FK_SecondaryIncidentBehavior_DisciplineIncident] FOREIGN KEY([SchoolId], [IncidentIdentifier])

REFERENCES [edfi].[DisciplineIncident] ([SchoolId], [IncidentIdentifier])

GO

ALTER TABLE [edfi].[DisciplineIncidentSecondaryBehavior] CHECK CONSTRAINT [FK_SecondaryIncidentBehavior_DisciplineIncident]

GO

/****** Object:  ForeignKey [FK_DisciplineIncidentWeapons_DisciplineIncident]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[DisciplineIncidentWeapons]  WITH CHECK ADD  CONSTRAINT [FK_DisciplineIncidentWeapons_DisciplineIncident] FOREIGN KEY([SchoolId], [IncidentIdentifier])

REFERENCES [edfi].[DisciplineIncident] ([SchoolId], [IncidentIdentifier])

GO

ALTER TABLE [edfi].[DisciplineIncidentWeapons] CHECK CONSTRAINT [FK_DisciplineIncidentWeapons_DisciplineIncident]

GO

/****** Object:  ForeignKey [FK_WeaponsType_DisciplineIncidentWeapons]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[DisciplineIncidentWeapons]  WITH CHECK ADD  CONSTRAINT [FK_WeaponsType_DisciplineIncidentWeapons] FOREIGN KEY([WeaponsTypeId])

REFERENCES [edfi].[WeaponsType] ([WeaponsTypeId])

GO

ALTER TABLE [edfi].[DisciplineIncidentWeapons] CHECK CONSTRAINT [FK_WeaponsType_DisciplineIncidentWeapons]

GO

/****** Object:  ForeignKey [FK_EducationOrganization_OperationalStatusType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[EducationOrganization]  WITH CHECK ADD  CONSTRAINT [FK_EducationOrganization_OperationalStatusType] FOREIGN KEY([OperationalStatusTypeId])

REFERENCES [edfi].[OperationalStatusType] ([OperationalStatusTypeId])

GO

ALTER TABLE [edfi].[EducationOrganization] CHECK CONSTRAINT [FK_EducationOrganization_OperationalStatusType]

GO

/****** Object:  ForeignKey [FK_EducationOrganizationAddress_AddressType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[EducationOrganizationAddress]  WITH CHECK ADD  CONSTRAINT [FK_EducationOrganizationAddress_AddressType] FOREIGN KEY([AddressTypeId])

REFERENCES [edfi].[AddressType] ([AddressTypeId])

GO

ALTER TABLE [edfi].[EducationOrganizationAddress] CHECK CONSTRAINT [FK_EducationOrganizationAddress_AddressType]

GO

/****** Object:  ForeignKey [FK_EducationOrganizationAddress_CountryCodeType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[EducationOrganizationAddress]  WITH CHECK ADD  CONSTRAINT [FK_EducationOrganizationAddress_CountryCodeType] FOREIGN KEY([CountryCodeTypeId])

REFERENCES [edfi].[CountryCodeType] ([CountryCodeTypeId])

GO

ALTER TABLE [edfi].[EducationOrganizationAddress] CHECK CONSTRAINT [FK_EducationOrganizationAddress_CountryCodeType]

GO

/****** Object:  ForeignKey [FK_EducationOrganizationAddress_EducationOrganization]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[EducationOrganizationAddress]  WITH CHECK ADD  CONSTRAINT [FK_EducationOrganizationAddress_EducationOrganization] FOREIGN KEY([EducationOrganizationId])

REFERENCES [edfi].[EducationOrganization] ([EducationOrganizationId])

GO

ALTER TABLE [edfi].[EducationOrganizationAddress] CHECK CONSTRAINT [FK_EducationOrganizationAddress_EducationOrganization]

GO

/****** Object:  ForeignKey [FK_EducationOrganizationAddress_StateAbbreviationType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[EducationOrganizationAddress]  WITH CHECK ADD  CONSTRAINT [FK_EducationOrganizationAddress_StateAbbreviationType] FOREIGN KEY([StateAbbreviationTypeId])

REFERENCES [edfi].[StateAbbreviationType] ([StateAbbreviationTypeId])

GO

ALTER TABLE [edfi].[EducationOrganizationAddress] CHECK CONSTRAINT [FK_EducationOrganizationAddress_StateAbbreviationType]

GO

/****** Object:  ForeignKey [FK_EducationOrganizationCategory_EducationOrganization]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[EducationOrganizationCategory]  WITH CHECK ADD  CONSTRAINT [FK_EducationOrganizationCategory_EducationOrganization] FOREIGN KEY([EducationOrganizationId])

REFERENCES [edfi].[EducationOrganization] ([EducationOrganizationId])

GO

ALTER TABLE [edfi].[EducationOrganizationCategory] CHECK CONSTRAINT [FK_EducationOrganizationCategory_EducationOrganization]

GO

/****** Object:  ForeignKey [FK_EducationOrganizationCategory_EducationOrganizationCategoryType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[EducationOrganizationCategory]  WITH CHECK ADD  CONSTRAINT [FK_EducationOrganizationCategory_EducationOrganizationCategoryType] FOREIGN KEY([EducationOrganizationCategoryTypeId])

REFERENCES [edfi].[EducationOrganizationCategoryType] ([EducationOrganizationCategoryTypeId])

GO

ALTER TABLE [edfi].[EducationOrganizationCategory] CHECK CONSTRAINT [FK_EducationOrganizationCategory_EducationOrganizationCategoryType]

GO

/****** Object:  ForeignKey [FK_EducationOrganizationPeerReference_EducationOrganization]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[EducationOrganizationPeer]  WITH CHECK ADD  CONSTRAINT [FK_EducationOrganizationPeerReference_EducationOrganization] FOREIGN KEY([EducationOrganizationId])

REFERENCES [edfi].[EducationOrganization] ([EducationOrganizationId])

GO

ALTER TABLE [edfi].[EducationOrganizationPeer] CHECK CONSTRAINT [FK_EducationOrganizationPeerReference_EducationOrganization]

GO

/****** Object:  ForeignKey [FK_EducationOrganizationPeerReference_EducationOrganization1]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[EducationOrganizationPeer]  WITH CHECK ADD  CONSTRAINT [FK_EducationOrganizationPeerReference_EducationOrganization1] FOREIGN KEY([PeerEducationOrganizationId])

REFERENCES [edfi].[EducationOrganization] ([EducationOrganizationId])

GO

ALTER TABLE [edfi].[EducationOrganizationPeer] CHECK CONSTRAINT [FK_EducationOrganizationPeerReference_EducationOrganization1]

GO

/****** Object:  ForeignKey [FK_EducationOrganizationTelephone_EducationOrganization]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[EducationOrganizationTelephone]  WITH CHECK ADD  CONSTRAINT [FK_EducationOrganizationTelephone_EducationOrganization] FOREIGN KEY([EducationOrganizationId])

REFERENCES [edfi].[EducationOrganization] ([EducationOrganizationId])

GO

ALTER TABLE [edfi].[EducationOrganizationTelephone] CHECK CONSTRAINT [FK_EducationOrganizationTelephone_EducationOrganization]

GO

/****** Object:  ForeignKey [FK_EducationOrganizationTelephone_InstitutionalTelephoneNumberType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[EducationOrganizationTelephone]  WITH CHECK ADD  CONSTRAINT [FK_EducationOrganizationTelephone_InstitutionalTelephoneNumberType] FOREIGN KEY([InstitutionTelephoneNumberTypeId])

REFERENCES [edfi].[InstitutionalTelephoneNumberType] ([InstitutionalTelephoneNumberTypeId])

GO

ALTER TABLE [edfi].[EducationOrganizationTelephone] CHECK CONSTRAINT [FK_EducationOrganizationTelephone_InstitutionalTelephoneNumberType]

GO

/****** Object:  ForeignKey [FK_EducationOrgIdentificationCode_EducationOrganization]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[EducationOrgIdentificationCode]  WITH CHECK ADD  CONSTRAINT [FK_EducationOrgIdentificationCode_EducationOrganization] FOREIGN KEY([EducationOrganizationId])

REFERENCES [edfi].[EducationOrganization] ([EducationOrganizationId])

GO

ALTER TABLE [edfi].[EducationOrgIdentificationCode] CHECK CONSTRAINT [FK_EducationOrgIdentificationCode_EducationOrganization]

GO

/****** Object:  ForeignKey [FK_EducationOrgIdentificationCode_EducationOrgIdentificationSystemType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[EducationOrgIdentificationCode]  WITH CHECK ADD  CONSTRAINT [FK_EducationOrgIdentificationCode_EducationOrgIdentificationSystemType] FOREIGN KEY([EducationOrgIdentificationSystemTypeId])

REFERENCES [edfi].[EducationOrgIdentificationSystemType] ([EducationOrgIdentificationSystemTypeId])

GO

ALTER TABLE [edfi].[EducationOrgIdentificationCode] CHECK CONSTRAINT [FK_EducationOrgIdentificationCode_EducationOrgIdentificationSystemType]

GO

/****** Object:  ForeignKey [FK_EducationServiceCenter_EducationOrganization]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[EducationServiceCenter]  WITH CHECK ADD  CONSTRAINT [FK_EducationServiceCenter_EducationOrganization] FOREIGN KEY([EducationServiceCenterId])

REFERENCES [edfi].[EducationOrganization] ([EducationOrganizationId])

GO

ALTER TABLE [edfi].[EducationServiceCenter] CHECK CONSTRAINT [FK_EducationServiceCenter_EducationOrganization]

GO

/****** Object:  ForeignKey [FK_EducationServiceCenter_StateEducationAgency]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[EducationServiceCenter]  WITH CHECK ADD  CONSTRAINT [FK_EducationServiceCenter_StateEducationAgency] FOREIGN KEY([StateEducationAgencyId])

REFERENCES [edfi].[StateEducationAgency] ([StateEducationAgencyId])

GO

ALTER TABLE [edfi].[EducationServiceCenter] CHECK CONSTRAINT [FK_EducationServiceCenter_StateEducationAgency]

GO

/****** Object:  ForeignKey [FK_FeederSchoolAssociation_School]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[FeederSchoolAssociation]  WITH CHECK ADD  CONSTRAINT [FK_FeederSchoolAssociation_School] FOREIGN KEY([SchoolId])

REFERENCES [edfi].[School] ([SchoolId])

GO

ALTER TABLE [edfi].[FeederSchoolAssociation] CHECK CONSTRAINT [FK_FeederSchoolAssociation_School]

GO

/****** Object:  ForeignKey [FK_FeederSchoolAssociation_School1]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[FeederSchoolAssociation]  WITH CHECK ADD  CONSTRAINT [FK_FeederSchoolAssociation_School1] FOREIGN KEY([FeederSchoolId])

REFERENCES [edfi].[School] ([SchoolId])

GO

ALTER TABLE [edfi].[FeederSchoolAssociation] CHECK CONSTRAINT [FK_FeederSchoolAssociation_School1]

GO

/****** Object:  ForeignKey [FK_Grade_GradingPeriod]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Grade]  WITH CHECK ADD  CONSTRAINT [FK_Grade_GradingPeriod] FOREIGN KEY([SchoolId], [GradingPeriodTypeId], [GradingPeriodBeginDate])

REFERENCES [edfi].[GradingPeriod] ([EducationOrganizationId], [GradingPeriodTypeId], [BeginDate])

GO

ALTER TABLE [edfi].[Grade] CHECK CONSTRAINT [FK_Grade_GradingPeriod]

GO

/****** Object:  ForeignKey [FK_Grade_PerformanceBaseType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Grade]  WITH CHECK ADD  CONSTRAINT [FK_Grade_PerformanceBaseType] FOREIGN KEY([PerformanceBaseConversionTypeId])

REFERENCES [edfi].[PerformanceBaseType] ([PerformanceBaseTypeId])

GO

ALTER TABLE [edfi].[Grade] CHECK CONSTRAINT [FK_Grade_PerformanceBaseType]

GO

/****** Object:  ForeignKey [FK_Grade_Section]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Grade]  WITH CHECK ADD  CONSTRAINT [FK_Grade_Section] FOREIGN KEY([SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear])

REFERENCES [edfi].[Section] ([SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear])

GO

ALTER TABLE [edfi].[Grade] CHECK CONSTRAINT [FK_Grade_Section]

GO

/****** Object:  ForeignKey [FK_Grade_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Grade]  WITH CHECK ADD  CONSTRAINT [FK_Grade_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[Grade] CHECK CONSTRAINT [FK_Grade_Student]

GO

/****** Object:  ForeignKey [FK_GradeTypeType_Grade]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Grade]  WITH CHECK ADD  CONSTRAINT [FK_GradeTypeType_Grade] FOREIGN KEY([GradeTypeId])

REFERENCES [edfi].[GradeType] ([GradeTypeId])

GO

ALTER TABLE [edfi].[Grade] CHECK CONSTRAINT [FK_GradeTypeType_Grade]

GO

/****** Object:  ForeignKey [FK_GradebookEntry_GradingPeriod]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[GradebookEntry]  WITH CHECK ADD  CONSTRAINT [FK_GradebookEntry_GradingPeriod] FOREIGN KEY([SchoolId], [GradingPeriodTypeId], [GradingPeriodBeginDate])

REFERENCES [edfi].[GradingPeriod] ([EducationOrganizationId], [GradingPeriodTypeId], [BeginDate])

GO

ALTER TABLE [edfi].[GradebookEntry] CHECK CONSTRAINT [FK_GradebookEntry_GradingPeriod]

GO

/****** Object:  ForeignKey [FK_GradebookEntry_Section]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[GradebookEntry]  WITH CHECK ADD  CONSTRAINT [FK_GradebookEntry_Section] FOREIGN KEY([SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear])

REFERENCES [edfi].[Section] ([SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear])

GO

ALTER TABLE [edfi].[GradebookEntry] CHECK CONSTRAINT [FK_GradebookEntry_Section]

GO

/****** Object:  ForeignKey [FK_GradebookEntryLearningObjective_GradebookEntry]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[GradebookEntryLearningObjective]  WITH CHECK ADD  CONSTRAINT [FK_GradebookEntryLearningObjective_GradebookEntry] FOREIGN KEY([SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear], [GradebookEntryType], [DateAssigned])

REFERENCES [edfi].[GradebookEntry] ([SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear], [GradebookEntryType], [DateAssigned])

GO

ALTER TABLE [edfi].[GradebookEntryLearningObjective] CHECK CONSTRAINT [FK_GradebookEntryLearningObjective_GradebookEntry]

GO

/****** Object:  ForeignKey [FK_GradebookEntryLearningObjective_LearningObjective]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[GradebookEntryLearningObjective]  WITH CHECK ADD  CONSTRAINT [FK_GradebookEntryLearningObjective_LearningObjective] FOREIGN KEY([Objective], [AcademicSubjectTypeId], [ObjectiveGradeLevelTypeId])

REFERENCES [edfi].[LearningObjective] ([Objective], [AcademicSubjectTypeId], [ObjectiveGradeLevelTypeId])

GO

ALTER TABLE [edfi].[GradebookEntryLearningObjective] CHECK CONSTRAINT [FK_GradebookEntryLearningObjective_LearningObjective]

GO

/****** Object:  ForeignKey [FK_GradebookEntryLearningStandard_GradebookEntry]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[GradebookEntryLearningStandard]  WITH CHECK ADD  CONSTRAINT [FK_GradebookEntryLearningStandard_GradebookEntry] FOREIGN KEY([SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear], [GradebookEntryType], [DateAssigned])

REFERENCES [edfi].[GradebookEntry] ([SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear], [GradebookEntryType], [DateAssigned])

GO

ALTER TABLE [edfi].[GradebookEntryLearningStandard] CHECK CONSTRAINT [FK_GradebookEntryLearningStandard_GradebookEntry]

GO

/****** Object:  ForeignKey [FK_GradebookEntryLearningStandard_LearningStandard]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[GradebookEntryLearningStandard]  WITH CHECK ADD  CONSTRAINT [FK_GradebookEntryLearningStandard_LearningStandard] FOREIGN KEY([LearningStandardId])

REFERENCES [edfi].[LearningStandard] ([LearningStandardId])

GO

ALTER TABLE [edfi].[GradebookEntryLearningStandard] CHECK CONSTRAINT [FK_GradebookEntryLearningStandard_LearningStandard]

GO

/****** Object:  ForeignKey [FK_GradingPeriodType_GradingPeriod]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[GradingPeriod]  WITH CHECK ADD  CONSTRAINT [FK_GradingPeriodType_GradingPeriod] FOREIGN KEY([GradingPeriodTypeId])

REFERENCES [edfi].[GradingPeriodType] ([GradingPeriodTypeId])

GO

ALTER TABLE [edfi].[GradingPeriod] CHECK CONSTRAINT [FK_GradingPeriodType_GradingPeriod]

GO

/****** Object:  ForeignKey [FK_GraduationPlan_CreditType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[GraduationPlan]  WITH CHECK ADD  CONSTRAINT [FK_GraduationPlan_CreditType] FOREIGN KEY([TotalCreditsRequiredCreditTypeId])

REFERENCES [edfi].[CreditType] ([CreditTypeId])

GO

ALTER TABLE [edfi].[GraduationPlan] CHECK CONSTRAINT [FK_GraduationPlan_CreditType]

GO

/****** Object:  ForeignKey [FK_GraduationPlan_EducationOrganization]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[GraduationPlan]  WITH CHECK ADD  CONSTRAINT [FK_GraduationPlan_EducationOrganization] FOREIGN KEY([EducationOrganizationId])

REFERENCES [edfi].[EducationOrganization] ([EducationOrganizationId])

GO

ALTER TABLE [edfi].[GraduationPlan] CHECK CONSTRAINT [FK_GraduationPlan_EducationOrganization]

GO

/****** Object:  ForeignKey [FK_GraduationPlan_GraduationPlanType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[GraduationPlan]  WITH CHECK ADD  CONSTRAINT [FK_GraduationPlan_GraduationPlanType] FOREIGN KEY([GraduationPlanTypeId])

REFERENCES [edfi].[GraduationPlanType] ([GraduationPlanTypeId])

GO

ALTER TABLE [edfi].[GraduationPlan] CHECK CONSTRAINT [FK_GraduationPlan_GraduationPlanType]

GO

/****** Object:  ForeignKey [FK_GraduationPlanCreditsByCourse_CreditType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[GraduationPlanCreditsByCourse]  WITH CHECK ADD  CONSTRAINT [FK_GraduationPlanCreditsByCourse_CreditType] FOREIGN KEY([CreditTypeId])

REFERENCES [edfi].[CreditType] ([CreditTypeId])

GO

ALTER TABLE [edfi].[GraduationPlanCreditsByCourse] CHECK CONSTRAINT [FK_GraduationPlanCreditsByCourse_CreditType]

GO

/****** Object:  ForeignKey [FK_GraduationPlanCreditsByCourse_GradeLevelType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[GraduationPlanCreditsByCourse]  WITH CHECK ADD  CONSTRAINT [FK_GraduationPlanCreditsByCourse_GradeLevelType] FOREIGN KEY([GradeLevelTypeId])

REFERENCES [edfi].[GradeLevelType] ([GradeLevelTypeId])

GO

ALTER TABLE [edfi].[GraduationPlanCreditsByCourse] CHECK CONSTRAINT [FK_GraduationPlanCreditsByCourse_GradeLevelType]

GO

/****** Object:  ForeignKey [FK_GraduationPlanCreditsByCourse_GraduationPlan]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[GraduationPlanCreditsByCourse]  WITH CHECK ADD  CONSTRAINT [FK_GraduationPlanCreditsByCourse_GraduationPlan] FOREIGN KEY([GraduationPlanTypeId], [EducationOrganizationId])

REFERENCES [edfi].[GraduationPlan] ([GraduationPlanTypeId], [EducationOrganizationId])

GO

ALTER TABLE [edfi].[GraduationPlanCreditsByCourse] CHECK CONSTRAINT [FK_GraduationPlanCreditsByCourse_GraduationPlan]

GO

/****** Object:  ForeignKey [FK_GraduationPlanCreditsBySubject_CreditType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[GraduationPlanCreditsBySubject]  WITH CHECK ADD  CONSTRAINT [FK_GraduationPlanCreditsBySubject_CreditType] FOREIGN KEY([CreditTypeId])

REFERENCES [edfi].[CreditType] ([CreditTypeId])

GO

ALTER TABLE [edfi].[GraduationPlanCreditsBySubject] CHECK CONSTRAINT [FK_GraduationPlanCreditsBySubject_CreditType]

GO

/****** Object:  ForeignKey [FK_GraduationPlanCreditsBySubject_GraduationPlan]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[GraduationPlanCreditsBySubject]  WITH CHECK ADD  CONSTRAINT [FK_GraduationPlanCreditsBySubject_GraduationPlan] FOREIGN KEY([GraduationPlanTypeId], [EducationOrganizationId])

REFERENCES [edfi].[GraduationPlan] ([GraduationPlanTypeId], [EducationOrganizationId])

GO

ALTER TABLE [edfi].[GraduationPlanCreditsBySubject] CHECK CONSTRAINT [FK_GraduationPlanCreditsBySubject_GraduationPlan]

GO

/****** Object:  ForeignKey [FK_GraduationPlanCreditsBySubject_AcademicSubjectType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[GraduationPlanCreditsBySubject]  WITH CHECK ADD  CONSTRAINT [FK_GraduationPlanCreditsBySubject_AcademicSubjectType] FOREIGN KEY([SubjectAreaTypeId])

REFERENCES [edfi].[AcademicSubjectType] ([AcademicSubjectTypeId])

GO

ALTER TABLE [edfi].[GraduationPlanCreditsBySubject] CHECK CONSTRAINT [FK_GraduationPlanCreditsBySubject_AcademicSubjectType]

GO

/****** Object:  ForeignKey [FK_AcademicSubjectType_LearningObjective]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[LearningObjective]  WITH CHECK ADD  CONSTRAINT [FK_AcademicSubjectType_LearningObjective] FOREIGN KEY([AcademicSubjectTypeId])

REFERENCES [edfi].[AcademicSubjectType] ([AcademicSubjectTypeId])

GO

ALTER TABLE [edfi].[LearningObjective] CHECK CONSTRAINT [FK_AcademicSubjectType_LearningObjective]

GO

/****** Object:  ForeignKey [FK_LearningObjective_AcademicSubjectType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[LearningObjective]  WITH CHECK ADD  CONSTRAINT [FK_LearningObjective_AcademicSubjectType] FOREIGN KEY([AcademicSubjectTypeId])

REFERENCES [edfi].[AcademicSubjectType] ([AcademicSubjectTypeId])

GO

ALTER TABLE [edfi].[LearningObjective] CHECK CONSTRAINT [FK_LearningObjective_AcademicSubjectType]

GO

/****** Object:  ForeignKey [FK_LearningObjective_GradeLevelType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[LearningObjective]  WITH CHECK ADD  CONSTRAINT [FK_LearningObjective_GradeLevelType] FOREIGN KEY([ObjectiveGradeLevelTypeId])

REFERENCES [edfi].[GradeLevelType] ([GradeLevelTypeId])

GO

ALTER TABLE [edfi].[LearningObjective] CHECK CONSTRAINT [FK_LearningObjective_GradeLevelType]

GO

/****** Object:  ForeignKey [FK_LearningObjective_LearningObjective]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[LearningObjective]  WITH CHECK ADD  CONSTRAINT [FK_LearningObjective_LearningObjective] FOREIGN KEY([ParentObjective], [ParentAcademicSubjectTypeId], [ParentObjectiveGradeLevelTypeId])

REFERENCES [edfi].[LearningObjective] ([Objective], [AcademicSubjectTypeId], [ObjectiveGradeLevelTypeId])

GO

ALTER TABLE [edfi].[LearningObjective] CHECK CONSTRAINT [FK_LearningObjective_LearningObjective]

GO

/****** Object:  ForeignKey [FK_LearningObjective_LearningStandard]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[LearningObjective]  WITH CHECK ADD  CONSTRAINT [FK_LearningObjective_LearningStandard] FOREIGN KEY([LearningStandardId])

REFERENCES [edfi].[LearningStandard] ([LearningStandardId])

GO

ALTER TABLE [edfi].[LearningObjective] CHECK CONSTRAINT [FK_LearningObjective_LearningStandard]

GO

/****** Object:  ForeignKey [FK_LearningStandard_ContentStandardType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[LearningStandard]  WITH CHECK ADD  CONSTRAINT [FK_LearningStandard_ContentStandardType] FOREIGN KEY([ContentStandardTypeId])

REFERENCES [edfi].[ContentStandardType] ([ContentStandardTypeId])

GO

ALTER TABLE [edfi].[LearningStandard] CHECK CONSTRAINT [FK_LearningStandard_ContentStandardType]

GO

/****** Object:  ForeignKey [FK_LearningStandard_GradeLevelType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[LearningStandard]  WITH CHECK ADD  CONSTRAINT [FK_LearningStandard_GradeLevelType] FOREIGN KEY([GradeLevelTypeId])

REFERENCES [edfi].[GradeLevelType] ([GradeLevelTypeId])

GO

ALTER TABLE [edfi].[LearningStandard] CHECK CONSTRAINT [FK_LearningStandard_GradeLevelType]

GO

/****** Object:  ForeignKey [FK_LearningStandard_AcademicSubjectType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[LearningStandard]  WITH CHECK ADD  CONSTRAINT [FK_LearningStandard_AcademicSubjectType] FOREIGN KEY([SubjectAreaTypeId])

REFERENCES [edfi].[AcademicSubjectType] ([AcademicSubjectTypeId])

GO

ALTER TABLE [edfi].[LearningStandard] CHECK CONSTRAINT [FK_LearningStandard_AcademicSubjectType]

GO

/****** Object:  ForeignKey [FK_LearningStandardIdentificationCode_LearningStandard]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[LearningStandardIdentificationCode]  WITH CHECK ADD  CONSTRAINT [FK_LearningStandardIdentificationCode_LearningStandard] FOREIGN KEY([LearningStandardId])

REFERENCES [edfi].[LearningStandard] ([LearningStandardId])

GO

ALTER TABLE [edfi].[LearningStandardIdentificationCode] CHECK CONSTRAINT [FK_LearningStandardIdentificationCode_LearningStandard]

GO

/****** Object:  ForeignKey [FK_LeaveEvent_Staff]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[LeaveEvent]  WITH CHECK ADD  CONSTRAINT [FK_LeaveEvent_Staff] FOREIGN KEY([StaffUSI])

REFERENCES [edfi].[Staff] ([StaffUSI])

GO

ALTER TABLE [edfi].[LeaveEvent] CHECK CONSTRAINT [FK_LeaveEvent_Staff]

GO

/****** Object:  ForeignKey [FK_LeaveEventCategoryType_LeaveEvent]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[LeaveEvent]  WITH CHECK ADD  CONSTRAINT [FK_LeaveEventCategoryType_LeaveEvent] FOREIGN KEY([LeaveEventCategoryTypeId])

REFERENCES [edfi].[LeaveEventCategoryType] ([LeaveEventCategoryTypeId])

GO

ALTER TABLE [edfi].[LeaveEvent] CHECK CONSTRAINT [FK_LeaveEventCategoryType_LeaveEvent]

GO

/****** Object:  ForeignKey [FK_CharterStatusType_LocalEducationAgency]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[LocalEducationAgency]  WITH CHECK ADD  CONSTRAINT [FK_CharterStatusType_LocalEducationAgency] FOREIGN KEY([CharterStatusTypeId])

REFERENCES [edfi].[CharterStatusType] ([CharterStatusTypeId])

GO

ALTER TABLE [edfi].[LocalEducationAgency] CHECK CONSTRAINT [FK_CharterStatusType_LocalEducationAgency]

GO

/****** Object:  ForeignKey [FK_LEACategoryType_LocalEducationAgency]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[LocalEducationAgency]  WITH CHECK ADD  CONSTRAINT [FK_LEACategoryType_LocalEducationAgency] FOREIGN KEY([LEACategoryTypeId])

REFERENCES [edfi].[LEACategoryType] ([LEACategoryTypeId])

GO

ALTER TABLE [edfi].[LocalEducationAgency] CHECK CONSTRAINT [FK_LEACategoryType_LocalEducationAgency]

GO

/****** Object:  ForeignKey [FK_LocalEducationAgency_EducationOrganization]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[LocalEducationAgency]  WITH CHECK ADD  CONSTRAINT [FK_LocalEducationAgency_EducationOrganization] FOREIGN KEY([LocalEducationAgencyId])

REFERENCES [edfi].[EducationOrganization] ([EducationOrganizationId])

GO

ALTER TABLE [edfi].[LocalEducationAgency] CHECK CONSTRAINT [FK_LocalEducationAgency_EducationOrganization]

GO

/****** Object:  ForeignKey [FK_LocalEducationAgency_EducationServiceCenter]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[LocalEducationAgency]  WITH CHECK ADD  CONSTRAINT [FK_LocalEducationAgency_EducationServiceCenter] FOREIGN KEY([EducationServiceCenterId])

REFERENCES [edfi].[EducationServiceCenter] ([EducationServiceCenterId])

GO

ALTER TABLE [edfi].[LocalEducationAgency] CHECK CONSTRAINT [FK_LocalEducationAgency_EducationServiceCenter]

GO

/****** Object:  ForeignKey [FK_LocalEducationAgency_StateEducationAgency]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[LocalEducationAgency]  WITH CHECK ADD  CONSTRAINT [FK_LocalEducationAgency_StateEducationAgency] FOREIGN KEY([StateEducationAgencyId])

REFERENCES [edfi].[StateEducationAgency] ([StateEducationAgencyId])

GO

ALTER TABLE [edfi].[LocalEducationAgency] CHECK CONSTRAINT [FK_LocalEducationAgency_StateEducationAgency]

GO

/****** Object:  ForeignKey [FK_Location_School]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Location]  WITH CHECK ADD  CONSTRAINT [FK_Location_School] FOREIGN KEY([SchoolId])

REFERENCES [edfi].[School] ([SchoolId])

GO

ALTER TABLE [edfi].[Location] CHECK CONSTRAINT [FK_Location_School]

GO

/****** Object:  ForeignKey [FK_MeetingTime_ClassPeriod]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[MeetingTime]  WITH CHECK ADD  CONSTRAINT [FK_MeetingTime_ClassPeriod] FOREIGN KEY([SchoolId], [ClassPeriodName])

REFERENCES [edfi].[ClassPeriod] ([SchoolId], [ClassPeriodName])

GO

ALTER TABLE [edfi].[MeetingTime] CHECK CONSTRAINT [FK_MeetingTime_ClassPeriod]

GO

/****** Object:  ForeignKey [FK_MeetingTimeMeetingDay_MeetingDaysType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[MeetingTimeMeetingDay]  WITH CHECK ADD  CONSTRAINT [FK_MeetingTimeMeetingDay_MeetingDaysType] FOREIGN KEY([MeetingDaysTypeId])

REFERENCES [edfi].[MeetingDaysType] ([MeetingDaysTypeId])

GO

ALTER TABLE [edfi].[MeetingTimeMeetingDay] CHECK CONSTRAINT [FK_MeetingTimeMeetingDay_MeetingDaysType]

GO

/****** Object:  ForeignKey [FK_MeetingTimeMeetingDay_MeetingTime]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[MeetingTimeMeetingDay]  WITH CHECK ADD  CONSTRAINT [FK_MeetingTimeMeetingDay_MeetingTime] FOREIGN KEY([SchoolId], [ClassPeriodName], [WeekNumber])

REFERENCES [edfi].[MeetingTime] ([SchoolId], [ClassPeriodName], [WeekNumber])

GO

ALTER TABLE [edfi].[MeetingTimeMeetingDay] CHECK CONSTRAINT [FK_MeetingTimeMeetingDay_MeetingTime]

GO

/****** Object:  ForeignKey [FK_ObjectiveAssessementItem_AssessmentItem]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ObjectiveAssessementItem]  WITH CHECK ADD  CONSTRAINT [FK_ObjectiveAssessementItem_AssessmentItem] FOREIGN KEY([AssessmentItem], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

REFERENCES [edfi].[AssessmentItem] ([AssesmentItem], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

GO

ALTER TABLE [edfi].[ObjectiveAssessementItem] CHECK CONSTRAINT [FK_ObjectiveAssessementItem_AssessmentItem]

GO

/****** Object:  ForeignKey [FK_ObjectiveAssessementItem_ObjectiveAssessementItem]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ObjectiveAssessementItem]  WITH CHECK ADD  CONSTRAINT [FK_ObjectiveAssessementItem_ObjectiveAssessementItem] FOREIGN KEY([ParentObjectiveItem], [ParentAssessmentTitle], [ParentAcademicSubjectTypeId], [ParentAssessedGradeLevelTypeId], [ParentVersion], [ParentAssessmentItem])

REFERENCES [edfi].[ObjectiveAssessementItem] ([ObjectiveItem], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version], [AssessmentItem])

GO

ALTER TABLE [edfi].[ObjectiveAssessementItem] CHECK CONSTRAINT [FK_ObjectiveAssessementItem_ObjectiveAssessementItem]

GO

/****** Object:  ForeignKey [FK_ObjectiveAssessementItem_ObjectiveAssessment]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ObjectiveAssessementItem]  WITH CHECK ADD  CONSTRAINT [FK_ObjectiveAssessementItem_ObjectiveAssessment] FOREIGN KEY([ObjectiveItem], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

REFERENCES [edfi].[ObjectiveAssessment] ([ObjectiveItem], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

GO

ALTER TABLE [edfi].[ObjectiveAssessementItem] CHECK CONSTRAINT [FK_ObjectiveAssessementItem_ObjectiveAssessment]

GO

/****** Object:  ForeignKey [FK_ObjectiveAssessment_Assessment]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ObjectiveAssessment]  WITH CHECK ADD  CONSTRAINT [FK_ObjectiveAssessment_Assessment] FOREIGN KEY([AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

REFERENCES [edfi].[Assessment] ([AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

GO

ALTER TABLE [edfi].[ObjectiveAssessment] CHECK CONSTRAINT [FK_ObjectiveAssessment_Assessment]

GO

/****** Object:  ForeignKey [FK_ObjectiveAssessment_LearningStandard]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ObjectiveAssessment]  WITH CHECK ADD  CONSTRAINT [FK_ObjectiveAssessment_LearningStandard] FOREIGN KEY([LearningStandardId])

REFERENCES [edfi].[LearningStandard] ([LearningStandardId])

GO

ALTER TABLE [edfi].[ObjectiveAssessment] CHECK CONSTRAINT [FK_ObjectiveAssessment_LearningStandard]

GO

/****** Object:  ForeignKey [FK_LearningObjectiveAssessment_LearningObjective]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ObjectiveAssessmentLearningObjective]  WITH CHECK ADD  CONSTRAINT [FK_LearningObjectiveAssessment_LearningObjective] FOREIGN KEY([Objective], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId])

REFERENCES [edfi].[LearningObjective] ([Objective], [AcademicSubjectTypeId], [ObjectiveGradeLevelTypeId])

GO

ALTER TABLE [edfi].[ObjectiveAssessmentLearningObjective] CHECK CONSTRAINT [FK_LearningObjectiveAssessment_LearningObjective]

GO

/****** Object:  ForeignKey [FK_LearningObjectiveAssessment_ObjectiveAssessment]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ObjectiveAssessmentLearningObjective]  WITH CHECK ADD  CONSTRAINT [FK_LearningObjectiveAssessment_ObjectiveAssessment] FOREIGN KEY([ObjectiveItem], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

REFERENCES [edfi].[ObjectiveAssessment] ([ObjectiveItem], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

GO

ALTER TABLE [edfi].[ObjectiveAssessmentLearningObjective] CHECK CONSTRAINT [FK_LearningObjectiveAssessment_ObjectiveAssessment]

GO

/****** Object:  ForeignKey [FK_ObjectiveAssessmentPerformanceLevel_ObjectiveAssessment]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ObjectiveAssessmentPerformanceLevel]  WITH CHECK ADD  CONSTRAINT [FK_ObjectiveAssessmentPerformanceLevel_ObjectiveAssessment] FOREIGN KEY([ObjectiveItem], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

REFERENCES [edfi].[ObjectiveAssessment] ([ObjectiveItem], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

GO

ALTER TABLE [edfi].[ObjectiveAssessmentPerformanceLevel] CHECK CONSTRAINT [FK_ObjectiveAssessmentPerformanceLevel_ObjectiveAssessment]

GO

/****** Object:  ForeignKey [FK_ObjectiveAssessmentPerformanceLevel_PerformanceLevelDescriptor]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ObjectiveAssessmentPerformanceLevel]  WITH CHECK ADD  CONSTRAINT [FK_ObjectiveAssessmentPerformanceLevel_PerformanceLevelDescriptor] FOREIGN KEY([PerformanceLevelDescriptorId])

REFERENCES [edfi].[PerformanceLevelDescriptor] ([PerformanceLevelDescriptorId])

GO

ALTER TABLE [edfi].[ObjectiveAssessmentPerformanceLevel] CHECK CONSTRAINT [FK_ObjectiveAssessmentPerformanceLevel_PerformanceLevelDescriptor]

GO

/****** Object:  ForeignKey [FK_EmploymentStatusType_OpenStaffPosition]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[OpenStaffPosition]  WITH CHECK ADD  CONSTRAINT [FK_EmploymentStatusType_OpenStaffPosition] FOREIGN KEY([EmploymentStatusTypeId])

REFERENCES [edfi].[EmploymentStatusType] ([EmploymentStatusTypeId])

GO

ALTER TABLE [edfi].[OpenStaffPosition] CHECK CONSTRAINT [FK_EmploymentStatusType_OpenStaffPosition]

GO

/****** Object:  ForeignKey [FK_OpenStaffPosition_EducationOrganization]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[OpenStaffPosition]  WITH CHECK ADD  CONSTRAINT [FK_OpenStaffPosition_EducationOrganization] FOREIGN KEY([EducationOrganizationId])

REFERENCES [edfi].[EducationOrganization] ([EducationOrganizationId])

GO

ALTER TABLE [edfi].[OpenStaffPosition] CHECK CONSTRAINT [FK_OpenStaffPosition_EducationOrganization]

GO

/****** Object:  ForeignKey [FK_PostingResultType_OpenStaffPosition]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[OpenStaffPosition]  WITH CHECK ADD  CONSTRAINT [FK_PostingResultType_OpenStaffPosition] FOREIGN KEY([PostingResultTypeId])

REFERENCES [edfi].[PostingResultType] ([PostingResultTypeId])

GO

ALTER TABLE [edfi].[OpenStaffPosition] CHECK CONSTRAINT [FK_PostingResultType_OpenStaffPosition]

GO

/****** Object:  ForeignKey [FK_ProgramAssignmentType_OpenStaffPosition]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[OpenStaffPosition]  WITH CHECK ADD  CONSTRAINT [FK_ProgramAssignmentType_OpenStaffPosition] FOREIGN KEY([ProgramAssignmentTypeId])

REFERENCES [edfi].[ProgramAssignmentType] ([ProgramAssignmentTypeId])

GO

ALTER TABLE [edfi].[OpenStaffPosition] CHECK CONSTRAINT [FK_ProgramAssignmentType_OpenStaffPosition]

GO

/****** Object:  ForeignKey [FK_StaffClassificationType_OpenStaffPosition]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[OpenStaffPosition]  WITH CHECK ADD  CONSTRAINT [FK_StaffClassificationType_OpenStaffPosition] FOREIGN KEY([StaffClassificationTypeId])

REFERENCES [edfi].[StaffClassificationType] ([StaffClassificationTypeId])

GO

ALTER TABLE [edfi].[OpenStaffPosition] CHECK CONSTRAINT [FK_StaffClassificationType_OpenStaffPosition]

GO

/****** Object:  ForeignKey [FK_OpenStaffPositionAcademicSubjects_AcademicSubjectType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[OpenStaffPositionAcademicSubjects]  WITH CHECK ADD  CONSTRAINT [FK_OpenStaffPositionAcademicSubjects_AcademicSubjectType] FOREIGN KEY([AcademicSubjectTypeId])

REFERENCES [edfi].[AcademicSubjectType] ([AcademicSubjectTypeId])

GO

ALTER TABLE [edfi].[OpenStaffPositionAcademicSubjects] CHECK CONSTRAINT [FK_OpenStaffPositionAcademicSubjects_AcademicSubjectType]

GO

/****** Object:  ForeignKey [FK_OpenStaffPositionAcademicSubjects_OpenStaffPosition]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[OpenStaffPositionAcademicSubjects]  WITH CHECK ADD  CONSTRAINT [FK_OpenStaffPositionAcademicSubjects_OpenStaffPosition] FOREIGN KEY([EducationOrganizationId], [EmploymentStatusTypeId], [StaffClassificationTypeId], [RequisitionNumber], [DatePosted])

REFERENCES [edfi].[OpenStaffPosition] ([EducationOrganizationId], [EmploymentStatusTypeId], [StaffClassificationTypeId], [RequisitionNumber], [DatePosted])

GO

ALTER TABLE [edfi].[OpenStaffPositionAcademicSubjects] CHECK CONSTRAINT [FK_OpenStaffPositionAcademicSubjects_OpenStaffPosition]

GO

/****** Object:  ForeignKey [FK_OpenStaffPositionInstructionalGradeLevels_GradeLevelType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[OpenStaffPositionInstructionalGradeLevels]  WITH CHECK ADD  CONSTRAINT [FK_OpenStaffPositionInstructionalGradeLevels_GradeLevelType] FOREIGN KEY([GradeLevelTypeId])

REFERENCES [edfi].[GradeLevelType] ([GradeLevelTypeId])

GO

ALTER TABLE [edfi].[OpenStaffPositionInstructionalGradeLevels] CHECK CONSTRAINT [FK_OpenStaffPositionInstructionalGradeLevels_GradeLevelType]

GO

/****** Object:  ForeignKey [FK_OpenStaffPositionInstructionalGradeLevels_OpenStaffPosition]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[OpenStaffPositionInstructionalGradeLevels]  WITH CHECK ADD  CONSTRAINT [FK_OpenStaffPositionInstructionalGradeLevels_OpenStaffPosition] FOREIGN KEY([EducationOrganizationId], [EmploymentStatusTypeId], [StaffClassificationTypeId], [RequisitionNumber], [DatePosted])

REFERENCES [edfi].[OpenStaffPosition] ([EducationOrganizationId], [EmploymentStatusTypeId], [StaffClassificationTypeId], [RequisitionNumber], [DatePosted])

GO

ALTER TABLE [edfi].[OpenStaffPositionInstructionalGradeLevels] CHECK CONSTRAINT [FK_OpenStaffPositionInstructionalGradeLevels_OpenStaffPosition]

GO

/****** Object:  ForeignKey [FK_Parent_GenerationCodeSuffixType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Parent]  WITH CHECK ADD  CONSTRAINT [FK_Parent_GenerationCodeSuffixType] FOREIGN KEY([GenerationCodeSuffixTypeId])

REFERENCES [edfi].[GenerationCodeSuffixType] ([GenerationCodeSuffixTypeId])

GO

ALTER TABLE [edfi].[Parent] CHECK CONSTRAINT [FK_Parent_GenerationCodeSuffixType]

GO

/****** Object:  ForeignKey [FK_Parent_PersonalInformationVerificationType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Parent]  WITH CHECK ADD  CONSTRAINT [FK_Parent_PersonalInformationVerificationType] FOREIGN KEY([PersonalInformationVerificationTypeId])

REFERENCES [edfi].[PersonalInformationVerificationType] ([PersonalInformationVerificationTypeId])

GO

ALTER TABLE [edfi].[Parent] CHECK CONSTRAINT [FK_Parent_PersonalInformationVerificationType]

GO

/****** Object:  ForeignKey [FK_Parent_PersonalTitlePrefixType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Parent]  WITH CHECK ADD  CONSTRAINT [FK_Parent_PersonalTitlePrefixType] FOREIGN KEY([PersonalTitlePrefixTypeId])

REFERENCES [edfi].[PersonalTitlePrefixType] ([PersonalTitlePrefixTypeId])

GO

ALTER TABLE [edfi].[Parent] CHECK CONSTRAINT [FK_Parent_PersonalTitlePrefixType]

GO

/****** Object:  ForeignKey [FK_SexType_Parent]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Parent]  WITH CHECK ADD  CONSTRAINT [FK_SexType_Parent] FOREIGN KEY([SexTypeId])

REFERENCES [edfi].[SexType] ([SexTypeId])

GO

ALTER TABLE [edfi].[Parent] CHECK CONSTRAINT [FK_SexType_Parent]

GO

/****** Object:  ForeignKey [FK_ParentAddress_AddressType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ParentAddress]  WITH CHECK ADD  CONSTRAINT [FK_ParentAddress_AddressType] FOREIGN KEY([AddressTypeId])

REFERENCES [edfi].[AddressType] ([AddressTypeId])

GO

ALTER TABLE [edfi].[ParentAddress] CHECK CONSTRAINT [FK_ParentAddress_AddressType]

GO

/****** Object:  ForeignKey [FK_ParentAddress_CountryCodeType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ParentAddress]  WITH CHECK ADD  CONSTRAINT [FK_ParentAddress_CountryCodeType] FOREIGN KEY([CountryCodeTypeId])

REFERENCES [edfi].[CountryCodeType] ([CountryCodeTypeId])

GO

ALTER TABLE [edfi].[ParentAddress] CHECK CONSTRAINT [FK_ParentAddress_CountryCodeType]

GO

/****** Object:  ForeignKey [FK_ParentAddress_Parent]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ParentAddress]  WITH CHECK ADD  CONSTRAINT [FK_ParentAddress_Parent] FOREIGN KEY([ParentUSI])

REFERENCES [edfi].[Parent] ([ParentUSI])

GO

ALTER TABLE [edfi].[ParentAddress] CHECK CONSTRAINT [FK_ParentAddress_Parent]

GO

/****** Object:  ForeignKey [FK_ParentAddress_StateAbbreviationType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ParentAddress]  WITH CHECK ADD  CONSTRAINT [FK_ParentAddress_StateAbbreviationType] FOREIGN KEY([StateAbbreviationTypeId])

REFERENCES [edfi].[StateAbbreviationType] ([StateAbbreviationTypeId])

GO

ALTER TABLE [edfi].[ParentAddress] CHECK CONSTRAINT [FK_ParentAddress_StateAbbreviationType]

GO

/****** Object:  ForeignKey [FK_ParentElectronicMail_ElectronicMailType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ParentElectronicMail]  WITH CHECK ADD  CONSTRAINT [FK_ParentElectronicMail_ElectronicMailType] FOREIGN KEY([ElectronicMailTypeId])

REFERENCES [edfi].[ElectronicMailType] ([ElectronicMailTypeId])

GO

ALTER TABLE [edfi].[ParentElectronicMail] CHECK CONSTRAINT [FK_ParentElectronicMail_ElectronicMailType]

GO

/****** Object:  ForeignKey [FK_ParentElectronicMail_Parent]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ParentElectronicMail]  WITH CHECK ADD  CONSTRAINT [FK_ParentElectronicMail_Parent] FOREIGN KEY([ParentUSI])

REFERENCES [edfi].[Parent] ([ParentUSI])

GO

ALTER TABLE [edfi].[ParentElectronicMail] CHECK CONSTRAINT [FK_ParentElectronicMail_Parent]

GO

/****** Object:  ForeignKey [FK_ParentOtherName_GenerationCodeSuffixType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ParentOtherName]  WITH CHECK ADD  CONSTRAINT [FK_ParentOtherName_GenerationCodeSuffixType] FOREIGN KEY([GenerationCodeSuffixTypeId])

REFERENCES [edfi].[GenerationCodeSuffixType] ([GenerationCodeSuffixTypeId])

GO

ALTER TABLE [edfi].[ParentOtherName] CHECK CONSTRAINT [FK_ParentOtherName_GenerationCodeSuffixType]

GO

/****** Object:  ForeignKey [FK_ParentOtherName_OtherNameType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ParentOtherName]  WITH CHECK ADD  CONSTRAINT [FK_ParentOtherName_OtherNameType] FOREIGN KEY([OtherNameTypeId])

REFERENCES [edfi].[OtherNameType] ([OtherNameTypeId])

GO

ALTER TABLE [edfi].[ParentOtherName] CHECK CONSTRAINT [FK_ParentOtherName_OtherNameType]

GO

/****** Object:  ForeignKey [FK_ParentOtherName_Parent]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ParentOtherName]  WITH CHECK ADD  CONSTRAINT [FK_ParentOtherName_Parent] FOREIGN KEY([ParentUSI])

REFERENCES [edfi].[Parent] ([ParentUSI])

GO

ALTER TABLE [edfi].[ParentOtherName] CHECK CONSTRAINT [FK_ParentOtherName_Parent]

GO

/****** Object:  ForeignKey [FK_ParentOtherName_PersonalInformationVerificationType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ParentOtherName]  WITH CHECK ADD  CONSTRAINT [FK_ParentOtherName_PersonalInformationVerificationType] FOREIGN KEY([PersonalInformationVerificationTypeId])

REFERENCES [edfi].[PersonalInformationVerificationType] ([PersonalInformationVerificationTypeId])

GO

ALTER TABLE [edfi].[ParentOtherName] CHECK CONSTRAINT [FK_ParentOtherName_PersonalInformationVerificationType]

GO

/****** Object:  ForeignKey [FK_ParentOtherName_PersonalTitlePrefixType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ParentOtherName]  WITH CHECK ADD  CONSTRAINT [FK_ParentOtherName_PersonalTitlePrefixType] FOREIGN KEY([PersonalTitlePrefixTypeId])

REFERENCES [edfi].[PersonalTitlePrefixType] ([PersonalTitlePrefixTypeId])

GO

ALTER TABLE [edfi].[ParentOtherName] CHECK CONSTRAINT [FK_ParentOtherName_PersonalTitlePrefixType]

GO

/****** Object:  ForeignKey [FK_ParentTelephone_Parent]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ParentTelephone]  WITH CHECK ADD  CONSTRAINT [FK_ParentTelephone_Parent] FOREIGN KEY([ParentUSI])

REFERENCES [edfi].[Parent] ([ParentUSI])

GO

ALTER TABLE [edfi].[ParentTelephone] CHECK CONSTRAINT [FK_ParentTelephone_Parent]

GO

/****** Object:  ForeignKey [FK_ParentTelephone_TelephoneNumberType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ParentTelephone]  WITH CHECK ADD  CONSTRAINT [FK_ParentTelephone_TelephoneNumberType] FOREIGN KEY([TelephoneNumberTypeId])

REFERENCES [edfi].[TelephoneNumberType] ([TelephoneNumberTypeId])

GO

ALTER TABLE [edfi].[ParentTelephone] CHECK CONSTRAINT [FK_ParentTelephone_TelephoneNumberType]

GO

/****** Object:  ForeignKey [FK_Payroll_Account]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Payroll]  WITH CHECK ADD  CONSTRAINT [FK_Payroll_Account] FOREIGN KEY([EducationOrganizationId], [AccountNumber], [FiscalYear])

REFERENCES [edfi].[Account] ([EducationOrganizationId], [AccountNumber], [FiscalYear])

GO

ALTER TABLE [edfi].[Payroll] CHECK CONSTRAINT [FK_Payroll_Account]

GO

/****** Object:  ForeignKey [FK_Payroll_Staff]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Payroll]  WITH CHECK ADD  CONSTRAINT [FK_Payroll_Staff] FOREIGN KEY([StaffUSI])

REFERENCES [edfi].[Staff] ([StaffUSI])

GO

ALTER TABLE [edfi].[Payroll] CHECK CONSTRAINT [FK_Payroll_Staff]

GO

/****** Object:  ForeignKey [FK_AssessmentPerformanceLevel_AssessmentReportingMethodType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[AssessmentPerformanceLevel]  WITH CHECK ADD  CONSTRAINT [FK_AssessmentPerformanceLevel_AssessmentReportingMethodType] FOREIGN KEY([AssessmentReportingMethodTypeId])

REFERENCES [edfi].[AssessmentReportingMethodType] ([AssessmentReportingMethodTypeId])

GO

ALTER TABLE [edfi].[AssessmentPerformanceLevel] CHECK CONSTRAINT [FK_AssessmentPerformanceLevel_AssessmentReportingMethodType]

GO

/****** Object:  ForeignKey [FK_ObjectiveAssessmentPerformanceLevel_AssessmentReportingMethodType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ObjectiveAssessmentPerformanceLevel]  WITH CHECK ADD  CONSTRAINT [FK_ObjectiveAssessmentPerformanceLevel_AssessmentReportingMethodType] FOREIGN KEY([AssessmentReportingMethodTypeId])

REFERENCES [edfi].[AssessmentReportingMethodType] ([AssessmentReportingMethodTypeId])

GO

ALTER TABLE [edfi].[ObjectiveAssessmentPerformanceLevel] CHECK CONSTRAINT [FK_ObjectiveAssessmentPerformanceLevel_AssessmentReportingMethodType]

GO

/****** Object:  ForeignKey [FK_PerformanceLevelDescriptor_PerformanceBaseType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[PerformanceLevelDescriptor]  WITH CHECK ADD  CONSTRAINT [FK_PerformanceLevelDescriptor_PerformanceBaseType] FOREIGN KEY([PerformanceBaseConversionTypeId])

REFERENCES [edfi].[PerformanceBaseType] ([PerformanceBaseTypeId])

GO

ALTER TABLE [edfi].[PerformanceLevelDescriptor] CHECK CONSTRAINT [FK_PerformanceLevelDescriptor_PerformanceBaseType]

GO

/****** Object:  ForeignKey [FK_PostSecondaryEvent_PostSecondaryEventCategoryType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[PostSecondaryEvent]  WITH CHECK ADD  CONSTRAINT [FK_PostSecondaryEvent_PostSecondaryEventCategoryType] FOREIGN KEY([PostSecondaryEventCategoryTypeId])

REFERENCES [edfi].[PostSecondaryEventCategoryType] ([PostSecondaryEventCategoryTypeId])

GO

ALTER TABLE [edfi].[PostSecondaryEvent] CHECK CONSTRAINT [FK_PostSecondaryEvent_PostSecondaryEventCategoryType]

GO

/****** Object:  ForeignKey [FK_PostSecondaryEvent_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[PostSecondaryEvent]  WITH CHECK ADD  CONSTRAINT [FK_PostSecondaryEvent_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[PostSecondaryEvent] CHECK CONSTRAINT [FK_PostSecondaryEvent_Student]

GO

/****** Object:  ForeignKey [FK_Program_EducationOrganization]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Program]  WITH CHECK ADD  CONSTRAINT [FK_Program_EducationOrganization] FOREIGN KEY([EducationOrganizationId])

REFERENCES [edfi].[EducationOrganization] ([EducationOrganizationId])

GO

ALTER TABLE [edfi].[Program] CHECK CONSTRAINT [FK_Program_EducationOrganization]

GO

/****** Object:  ForeignKey [FK_Program_ProgramSponsorType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Program]  WITH CHECK ADD  CONSTRAINT [FK_Program_ProgramSponsorType] FOREIGN KEY([ProgramSponsorTypeId])

REFERENCES [edfi].[ProgramSponsorType] ([ProgramSponsorTypeId])

GO

ALTER TABLE [edfi].[Program] CHECK CONSTRAINT [FK_Program_ProgramSponsorType]

GO

/****** Object:  ForeignKey [FK_Program_ProgramType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Program]  WITH CHECK ADD  CONSTRAINT [FK_Program_ProgramType] FOREIGN KEY([ProgramTypeId])

REFERENCES [edfi].[ProgramType] ([ProgramTypeId])

GO

ALTER TABLE [edfi].[Program] CHECK CONSTRAINT [FK_Program_ProgramType]

GO

/****** Object:  ForeignKey [FK_Recognition_Diploma]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Recognition]  WITH CHECK ADD  CONSTRAINT [FK_Recognition_Diploma] FOREIGN KEY([StudentUSI], [SchoolId], [DiplomaTypeId], [DiplomaAwardDate])

REFERENCES [edfi].[Diploma] ([StudentUSI], [SchoolId], [DiplomaTypeId], [DiplomaAwardDate])

GO

ALTER TABLE [edfi].[Recognition] CHECK CONSTRAINT [FK_Recognition_Diploma]

GO

/****** Object:  ForeignKey [FK_Recognition_RecognitionType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Recognition]  WITH CHECK ADD  CONSTRAINT [FK_Recognition_RecognitionType] FOREIGN KEY([RecognitionTypeId])

REFERENCES [edfi].[RecognitionType] ([RecognitionTypeId])

GO

ALTER TABLE [edfi].[Recognition] CHECK CONSTRAINT [FK_Recognition_RecognitionType]

GO

/****** Object:  ForeignKey [FK_ReportCard_GradingPeriod]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ReportCard]  WITH CHECK ADD  CONSTRAINT [FK_ReportCard_GradingPeriod] FOREIGN KEY([EducationOrganizationId], [GradingPeriodTypeId], [GradingPeriodBeginDate])

REFERENCES [edfi].[GradingPeriod] ([EducationOrganizationId], [GradingPeriodTypeId], [BeginDate])

GO

ALTER TABLE [edfi].[ReportCard] CHECK CONSTRAINT [FK_ReportCard_GradingPeriod]

GO

/****** Object:  ForeignKey [FK_ReportCard_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[ReportCard]  WITH CHECK ADD  CONSTRAINT [FK_ReportCard_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[ReportCard] CHECK CONSTRAINT [FK_ReportCard_Student]

GO

/****** Object:  ForeignKey [FK_EducationalEnvironmentType_RestraintEvent]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[RestraintEvent]  WITH CHECK ADD  CONSTRAINT [FK_EducationalEnvironmentType_RestraintEvent] FOREIGN KEY([EducationalEnvironmentTypeId])

REFERENCES [edfi].[EducationalEnvironmentType] ([EducationalEnvironmentTypeId])

GO

ALTER TABLE [edfi].[RestraintEvent] CHECK CONSTRAINT [FK_EducationalEnvironmentType_RestraintEvent]

GO

/****** Object:  ForeignKey [FK_RestraintEvent_School]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[RestraintEvent]  WITH CHECK ADD  CONSTRAINT [FK_RestraintEvent_School] FOREIGN KEY([SchoolId])

REFERENCES [edfi].[School] ([SchoolId])

GO

ALTER TABLE [edfi].[RestraintEvent] CHECK CONSTRAINT [FK_RestraintEvent_School]

GO

/****** Object:  ForeignKey [FK_RestraintEvent_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[RestraintEvent]  WITH CHECK ADD  CONSTRAINT [FK_RestraintEvent_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[RestraintEvent] CHECK CONSTRAINT [FK_RestraintEvent_Student]

GO

/****** Object:  ForeignKey [FK_RestraintEventProgram_RestraintEvent]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[RestraintEventProgram]  WITH CHECK ADD  CONSTRAINT [FK_RestraintEventProgram_RestraintEvent] FOREIGN KEY([StudentUSI], [SchoolId], [RestraintEventIdentifier], [EventDate])

REFERENCES [edfi].[RestraintEvent] ([StudentUSI], [SchoolId], [RestraintEventIdentifier], [EventDate])

GO

ALTER TABLE [edfi].[RestraintEventProgram] CHECK CONSTRAINT [FK_RestraintEventProgram_RestraintEvent]

GO

/****** Object:  ForeignKey [FK_RestraintEventProgram_StudentProgramAssociation]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[RestraintEventProgram]  WITH CHECK ADD  CONSTRAINT [FK_RestraintEventProgram_StudentProgramAssociation] FOREIGN KEY([StudentUSI], [ProgramTypeId], [SchoolId], [BeginDate])

REFERENCES [edfi].[StudentProgramAssociation] ([StudentUSI], [ProgramTypeId], [EducationOrganizationId], [BeginDate])

GO

ALTER TABLE [edfi].[RestraintEventProgram] CHECK CONSTRAINT [FK_RestraintEventProgram_StudentProgramAssociation]

GO

/****** Object:  ForeignKey [FK_RestraintEventReason_RestraintEvent]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[RestraintEventReason]  WITH CHECK ADD  CONSTRAINT [FK_RestraintEventReason_RestraintEvent] FOREIGN KEY([StudentUSI], [SchoolId], [RestraintEventIdentifier], [EventDate])

REFERENCES [edfi].[RestraintEvent] ([StudentUSI], [SchoolId], [RestraintEventIdentifier], [EventDate])

GO

ALTER TABLE [edfi].[RestraintEventReason] CHECK CONSTRAINT [FK_RestraintEventReason_RestraintEvent]

GO

/****** Object:  ForeignKey [FK_RestraintEventReasonsType_RestraintEventRestraintEventReasons]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[RestraintEventReason]  WITH CHECK ADD  CONSTRAINT [FK_RestraintEventReasonsType_RestraintEventRestraintEventReasons] FOREIGN KEY([RestraintEventReasonsTypeId])

REFERENCES [edfi].[RestraintEventReasonsType] ([RestraintEventReasonsTypeId])

GO

ALTER TABLE [edfi].[RestraintEventReason] CHECK CONSTRAINT [FK_RestraintEventReasonsType_RestraintEventRestraintEventReasons]

GO

/****** Object:  ForeignKey [FK_AdministrativeFundingControlType_School]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[School]  WITH CHECK ADD  CONSTRAINT [FK_AdministrativeFundingControlType_School] FOREIGN KEY([AdministrativeFundingControlTypeId])

REFERENCES [edfi].[AdministrativeFundingControlType] ([AdministrativeFundingControlTypeId])

GO

ALTER TABLE [edfi].[School] CHECK CONSTRAINT [FK_AdministrativeFundingControlType_School]

GO

/****** Object:  ForeignKey [FK_CharterStatusType_School]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[School]  WITH CHECK ADD  CONSTRAINT [FK_CharterStatusType_School] FOREIGN KEY([CharterStatusTypeId])

REFERENCES [edfi].[CharterStatusType] ([CharterStatusTypeId])

GO

ALTER TABLE [edfi].[School] CHECK CONSTRAINT [FK_CharterStatusType_School]

GO

/****** Object:  ForeignKey [FK_MagnetSpecialProgramEmphasisSchoolType_School]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[School]  WITH CHECK ADD  CONSTRAINT [FK_MagnetSpecialProgramEmphasisSchoolType_School] FOREIGN KEY([MagnetSpecialProgramEmphasisSchoolTypeId])

REFERENCES [edfi].[MagnetSpecialProgramEmphasisSchoolType] ([MagnetSpecialProgramEmphasisSchoolTypeId])

GO

ALTER TABLE [edfi].[School] CHECK CONSTRAINT [FK_MagnetSpecialProgramEmphasisSchoolType_School]

GO

/****** Object:  ForeignKey [FK_School_EducationOrganization]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[School]  WITH CHECK ADD  CONSTRAINT [FK_School_EducationOrganization] FOREIGN KEY([SchoolId])

REFERENCES [edfi].[EducationOrganization] ([EducationOrganizationId])

GO

ALTER TABLE [edfi].[School] CHECK CONSTRAINT [FK_School_EducationOrganization]

GO

/****** Object:  ForeignKey [FK_School_LocalEducationAgency]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[School]  WITH CHECK ADD  CONSTRAINT [FK_School_LocalEducationAgency] FOREIGN KEY([LocalEducationAgencyId])

REFERENCES [edfi].[LocalEducationAgency] ([LocalEducationAgencyId])

GO

ALTER TABLE [edfi].[School] CHECK CONSTRAINT [FK_School_LocalEducationAgency]

GO

/****** Object:  ForeignKey [FK_SchoolTypeType_School]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[School]  WITH CHECK ADD  CONSTRAINT [FK_SchoolTypeType_School] FOREIGN KEY([SchoolTypeId])

REFERENCES [edfi].[SchoolType] ([SchoolTypeId])

GO

ALTER TABLE [edfi].[School] CHECK CONSTRAINT [FK_SchoolTypeType_School]

GO

/****** Object:  ForeignKey [FK_TitleIPartASchoolDesignationType_School]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[School]  WITH CHECK ADD  CONSTRAINT [FK_TitleIPartASchoolDesignationType_School] FOREIGN KEY([TitleIPartASchoolDesignationTypeId])

REFERENCES [edfi].[TitleIPartASchoolDesignationType] ([TitleIPartASchoolDesignationTypeId])

GO

ALTER TABLE [edfi].[School] CHECK CONSTRAINT [FK_TitleIPartASchoolDesignationType_School]

GO

/****** Object:  ForeignKey [FK_SchoolCategory_School]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[SchoolCategory]  WITH CHECK ADD  CONSTRAINT [FK_SchoolCategory_School] FOREIGN KEY([SchoolId])

REFERENCES [edfi].[School] ([SchoolId])

GO

ALTER TABLE [edfi].[SchoolCategory] CHECK CONSTRAINT [FK_SchoolCategory_School]

GO

/****** Object:  ForeignKey [FK_SchoolCategory_SchoolCategoryType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[SchoolCategory]  WITH CHECK ADD  CONSTRAINT [FK_SchoolCategory_SchoolCategoryType] FOREIGN KEY([SchoolCategoryTypeId])

REFERENCES [edfi].[SchoolCategoryType] ([SchoolCategoryTypeId])

GO

ALTER TABLE [edfi].[SchoolCategory] CHECK CONSTRAINT [FK_SchoolCategory_SchoolCategoryType]

GO

/****** Object:  ForeignKey [FK_SchoolGradesOffered_GradeLevelType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[SchoolGradesOffered]  WITH CHECK ADD  CONSTRAINT [FK_SchoolGradesOffered_GradeLevelType] FOREIGN KEY([GradesOfferedTypeId])

REFERENCES [edfi].[GradeLevelType] ([GradeLevelTypeId])

GO

ALTER TABLE [edfi].[SchoolGradesOffered] CHECK CONSTRAINT [FK_SchoolGradesOffered_GradeLevelType]

GO

/****** Object:  ForeignKey [FK_SchoolGradesOffered_School]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[SchoolGradesOffered]  WITH CHECK ADD  CONSTRAINT [FK_SchoolGradesOffered_School] FOREIGN KEY([SchoolId])

REFERENCES [edfi].[School] ([SchoolId])

GO

ALTER TABLE [edfi].[SchoolGradesOffered] CHECK CONSTRAINT [FK_SchoolGradesOffered_School]

GO

/****** Object:  ForeignKey [FK_EducationalEnvironmentType_Section]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Section]  WITH CHECK ADD  CONSTRAINT [FK_EducationalEnvironmentType_Section] FOREIGN KEY([EducationalEnvironmentTypeId])

REFERENCES [edfi].[EducationalEnvironmentType] ([EducationalEnvironmentTypeId])

GO

ALTER TABLE [edfi].[Section] CHECK CONSTRAINT [FK_EducationalEnvironmentType_Section]

GO

/****** Object:  ForeignKey [FK_MediumOfInstructionType_Section]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Section]  WITH CHECK ADD  CONSTRAINT [FK_MediumOfInstructionType_Section] FOREIGN KEY([MediumOfInstructionTypeId])

REFERENCES [edfi].[MediumOfInstructionType] ([MediumOfInstructionTypeId])

GO

ALTER TABLE [edfi].[Section] CHECK CONSTRAINT [FK_MediumOfInstructionType_Section]

GO

/****** Object:  ForeignKey [FK_PopulationServedType_Section]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Section]  WITH CHECK ADD  CONSTRAINT [FK_PopulationServedType_Section] FOREIGN KEY([PopulationServedTypeId])

REFERENCES [edfi].[PopulationServedType] ([PopulationServedTypeId])

GO

ALTER TABLE [edfi].[Section] CHECK CONSTRAINT [FK_PopulationServedType_Section]

GO

/****** Object:  ForeignKey [FK_Section_ClassPeriod]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Section]  WITH CHECK ADD  CONSTRAINT [FK_Section_ClassPeriod] FOREIGN KEY([SchoolId], [ClassPeriodName])

REFERENCES [edfi].[ClassPeriod] ([SchoolId], [ClassPeriodName])

GO

ALTER TABLE [edfi].[Section] CHECK CONSTRAINT [FK_Section_ClassPeriod]

GO

/****** Object:  ForeignKey [FK_Section_CourseOffering]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Section]  WITH CHECK ADD  CONSTRAINT [FK_Section_CourseOffering] FOREIGN KEY([SchoolId], [TermTypeId], [SchoolYear], [LocalCourseCode])

REFERENCES [edfi].[CourseOffering] ([SchoolId], [TermTypeId], [SchoolYear], [LocalCourseCode])

GO

ALTER TABLE [edfi].[Section] CHECK CONSTRAINT [FK_Section_CourseOffering]

GO

/****** Object:  ForeignKey [FK_Section_Location]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Section]  WITH CHECK ADD  CONSTRAINT [FK_Section_Location] FOREIGN KEY([SchoolId], [ClassroomIdentificationCode])

REFERENCES [edfi].[Location] ([SchoolId], [ClassroomIdentificationCode])

GO

ALTER TABLE [edfi].[Section] CHECK CONSTRAINT [FK_Section_Location]

GO

/****** Object:  ForeignKey [FK_Section_School]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Section]  WITH CHECK ADD  CONSTRAINT [FK_Section_School] FOREIGN KEY([SchoolId])

REFERENCES [edfi].[School] ([SchoolId])

GO

ALTER TABLE [edfi].[Section] CHECK CONSTRAINT [FK_Section_School]

GO

/****** Object:  ForeignKey [FK_Section_Session]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Section]  WITH CHECK ADD  CONSTRAINT [FK_Section_Session] FOREIGN KEY([SchoolId], [TermTypeId], [SchoolYear])

REFERENCES [edfi].[Session] ([EducationOrganizationId], [TermTypeId], [SchoolYear])

GO

ALTER TABLE [edfi].[Section] CHECK CONSTRAINT [FK_Section_Session]

GO

/****** Object:  ForeignKey [FK_Section504Disability_Section504DisabilityType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Section504Disability]  WITH CHECK ADD  CONSTRAINT [FK_Section504Disability_Section504DisabilityType] FOREIGN KEY([Section504DisabilityTypeId])

REFERENCES [edfi].[Section504DisabilityType] ([Section504DisabilityTypeId])

GO

ALTER TABLE [edfi].[Section504Disability] CHECK CONSTRAINT [FK_Section504Disability_Section504DisabilityType]

GO

/****** Object:  ForeignKey [FK_Section504Disability_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Section504Disability]  WITH CHECK ADD  CONSTRAINT [FK_Section504Disability_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[Section504Disability] CHECK CONSTRAINT [FK_Section504Disability_Student]

GO

/****** Object:  ForeignKey [FK_Section_CreditType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Section]  WITH CHECK ADD  CONSTRAINT [FK_Section_CreditType] FOREIGN KEY([AvailableCreditTypeId])

REFERENCES [edfi].[CreditType] ([CreditTypeId])

GO

ALTER TABLE [edfi].[Section] CHECK CONSTRAINT [FK_Section_CreditType]

GO

/****** Object:  ForeignKey [FK_SectionProgram_Program]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[SectionProgram]  WITH CHECK ADD  CONSTRAINT [FK_SectionProgram_Program] FOREIGN KEY([SchoolId], [ProgramTypeId])

REFERENCES [edfi].[Program] ([EducationOrganizationId], [ProgramTypeId])

GO

ALTER TABLE [edfi].[SectionProgram] CHECK CONSTRAINT [FK_SectionProgram_Program]

GO

/****** Object:  ForeignKey [FK_SectionProgram_Section]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[SectionProgram]  WITH CHECK ADD  CONSTRAINT [FK_SectionProgram_Section] FOREIGN KEY([SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear])

REFERENCES [edfi].[Section] ([SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear])

GO

ALTER TABLE [edfi].[SectionProgram] CHECK CONSTRAINT [FK_SectionProgram_Section]

GO

/****** Object:  ForeignKey [FK_Session_EducationOrganization]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Session]  WITH CHECK ADD  CONSTRAINT [FK_Session_EducationOrganization] FOREIGN KEY([EducationOrganizationId])

REFERENCES [edfi].[EducationOrganization] ([EducationOrganizationId])

GO

ALTER TABLE [edfi].[Session] CHECK CONSTRAINT [FK_Session_EducationOrganization]

GO

/****** Object:  ForeignKey [FK_Session_SchoolYearType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Session]  WITH CHECK ADD  CONSTRAINT [FK_Session_SchoolYearType] FOREIGN KEY([SchoolYear])

REFERENCES [edfi].[SchoolYearType] ([SchoolYear])

GO

ALTER TABLE [edfi].[Session] CHECK CONSTRAINT [FK_Session_SchoolYearType]

GO

/****** Object:  ForeignKey [FK_Session_TermType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Session]  WITH CHECK ADD  CONSTRAINT [FK_Session_TermType] FOREIGN KEY([TermTypeId])

REFERENCES [edfi].[TermType] ([TermTypeId])

GO

ALTER TABLE [edfi].[Session] CHECK CONSTRAINT [FK_Session_TermType]

GO

/****** Object:  ForeignKey [FK_SessionGradingPeriod_GradingPeriod]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[SessionGradingPeriod]  WITH CHECK ADD  CONSTRAINT [FK_SessionGradingPeriod_GradingPeriod] FOREIGN KEY([EducationOrganizationId], [GradingPeriodTypeId], [GradingPeriodBeginDate])

REFERENCES [edfi].[GradingPeriod] ([EducationOrganizationId], [GradingPeriodTypeId], [BeginDate])

GO

ALTER TABLE [edfi].[SessionGradingPeriod] CHECK CONSTRAINT [FK_SessionGradingPeriod_GradingPeriod]

GO

/****** Object:  ForeignKey [FK_SessionGradingPeriod_Session]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[SessionGradingPeriod]  WITH CHECK ADD  CONSTRAINT [FK_SessionGradingPeriod_Session] FOREIGN KEY([EducationOrganizationId], [TermTypeId], [SchoolYear])

REFERENCES [edfi].[Session] ([EducationOrganizationId], [TermTypeId], [SchoolYear])

GO

ALTER TABLE [edfi].[SessionGradingPeriod] CHECK CONSTRAINT [FK_SessionGradingPeriod_Session]

GO

/****** Object:  ForeignKey [FK_LevelOfEducationType_Staff]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Staff]  WITH CHECK ADD  CONSTRAINT [FK_LevelOfEducationType_Staff] FOREIGN KEY([HighestLevelOfEducationCompletedTypeId])

REFERENCES [edfi].[LevelOfEducationType] ([LevelOfEducationTypeId])

GO

ALTER TABLE [edfi].[Staff] CHECK CONSTRAINT [FK_LevelOfEducationType_Staff]

GO

/****** Object:  ForeignKey [FK_OldEthnicityType_Staff]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Staff]  WITH CHECK ADD  CONSTRAINT [FK_OldEthnicityType_Staff] FOREIGN KEY([OldEthnicityTypeId])

REFERENCES [edfi].[OldEthnicityType] ([OldEthnicityTypeId])

GO

ALTER TABLE [edfi].[Staff] CHECK CONSTRAINT [FK_OldEthnicityType_Staff]

GO

/****** Object:  ForeignKey [FK_SexType_Staff]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Staff]  WITH CHECK ADD  CONSTRAINT [FK_SexType_Staff] FOREIGN KEY([SexTypeId])

REFERENCES [edfi].[SexType] ([SexTypeId])

GO

ALTER TABLE [edfi].[Staff] CHECK CONSTRAINT [FK_SexType_Staff]

GO

/****** Object:  ForeignKey [FK_Staff_GenerationCodeSuffixType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Staff]  WITH CHECK ADD  CONSTRAINT [FK_Staff_GenerationCodeSuffixType] FOREIGN KEY([GenerationCodeSuffixTypeId])

REFERENCES [edfi].[GenerationCodeSuffixType] ([GenerationCodeSuffixTypeId])

GO

ALTER TABLE [edfi].[Staff] CHECK CONSTRAINT [FK_Staff_GenerationCodeSuffixType]

GO

/****** Object:  ForeignKey [FK_Staff_PersonalInformationVerificationType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Staff]  WITH CHECK ADD  CONSTRAINT [FK_Staff_PersonalInformationVerificationType] FOREIGN KEY([PersonalInformationVerificationTypeId])

REFERENCES [edfi].[PersonalInformationVerificationType] ([PersonalInformationVerificationTypeId])

GO

ALTER TABLE [edfi].[Staff] CHECK CONSTRAINT [FK_Staff_PersonalInformationVerificationType]

GO

/****** Object:  ForeignKey [FK_Staff_PersonalTitlePrefixType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Staff]  WITH CHECK ADD  CONSTRAINT [FK_Staff_PersonalTitlePrefixType] FOREIGN KEY([PersonalTitlePrefixTypeId])

REFERENCES [edfi].[PersonalTitlePrefixType] ([PersonalTitlePrefixTypeId])

GO

ALTER TABLE [edfi].[Staff] CHECK CONSTRAINT [FK_Staff_PersonalTitlePrefixType]

GO

/****** Object:  ForeignKey [FK_StaffAddress_AddressType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffAddress]  WITH CHECK ADD  CONSTRAINT [FK_StaffAddress_AddressType] FOREIGN KEY([AddressTypeId])

REFERENCES [edfi].[AddressType] ([AddressTypeId])

GO

ALTER TABLE [edfi].[StaffAddress] CHECK CONSTRAINT [FK_StaffAddress_AddressType]

GO

/****** Object:  ForeignKey [FK_StaffAddress_CountryCodeType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffAddress]  WITH CHECK ADD  CONSTRAINT [FK_StaffAddress_CountryCodeType] FOREIGN KEY([CountryCodeTypeId])

REFERENCES [edfi].[CountryCodeType] ([CountryCodeTypeId])

GO

ALTER TABLE [edfi].[StaffAddress] CHECK CONSTRAINT [FK_StaffAddress_CountryCodeType]

GO

/****** Object:  ForeignKey [FK_StaffAddress_Staff]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffAddress]  WITH CHECK ADD  CONSTRAINT [FK_StaffAddress_Staff] FOREIGN KEY([StaffUSI])

REFERENCES [edfi].[Staff] ([StaffUSI])

GO

ALTER TABLE [edfi].[StaffAddress] CHECK CONSTRAINT [FK_StaffAddress_Staff]

GO

/****** Object:  ForeignKey [FK_StaffAddress_StateAbbreviationType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffAddress]  WITH CHECK ADD  CONSTRAINT [FK_StaffAddress_StateAbbreviationType] FOREIGN KEY([StateAbbreviationTypeId])

REFERENCES [edfi].[StateAbbreviationType] ([StateAbbreviationTypeId])

GO

ALTER TABLE [edfi].[StaffAddress] CHECK CONSTRAINT [FK_StaffAddress_StateAbbreviationType]

GO

/****** Object:  ForeignKey [FK_StaffCohortAssociation_Cohort]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffCohortAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StaffCohortAssociation_Cohort] FOREIGN KEY([EducationOrganizationId], [CohortIdentifier])

REFERENCES [edfi].[Cohort] ([EducationOrganizationId], [CohortIdentifier])

GO

ALTER TABLE [edfi].[StaffCohortAssociation] CHECK CONSTRAINT [FK_StaffCohortAssociation_Cohort]

GO

/****** Object:  ForeignKey [FK_StaffCohortAssociation_Staff]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffCohortAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StaffCohortAssociation_Staff] FOREIGN KEY([StaffUSI])

REFERENCES [edfi].[Staff] ([StaffUSI])

GO

ALTER TABLE [edfi].[StaffCohortAssociation] CHECK CONSTRAINT [FK_StaffCohortAssociation_Staff]

GO

/****** Object:  ForeignKey [FK_StaffClassificationType_StaffEducationOrgAssignmentAssociation]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffEducationOrgAssignmentAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StaffClassificationType_StaffEducationOrgAssignmentAssociation] FOREIGN KEY([StaffClassificationTypeId])

REFERENCES [edfi].[StaffClassificationType] ([StaffClassificationTypeId])

GO

ALTER TABLE [edfi].[StaffEducationOrgAssignmentAssociation] CHECK CONSTRAINT [FK_StaffClassificationType_StaffEducationOrgAssignmentAssociation]

GO

/****** Object:  ForeignKey [FK_StaffEducationOrgAssignmentAssociation_EducationOrganization]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffEducationOrgAssignmentAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StaffEducationOrgAssignmentAssociation_EducationOrganization] FOREIGN KEY([EducationOrganizationId])

REFERENCES [edfi].[EducationOrganization] ([EducationOrganizationId])

GO

ALTER TABLE [edfi].[StaffEducationOrgAssignmentAssociation] CHECK CONSTRAINT [FK_StaffEducationOrgAssignmentAssociation_EducationOrganization]

GO

/****** Object:  ForeignKey [FK_StaffEducationOrgAssignmentAssociation_Staff]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffEducationOrgAssignmentAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StaffEducationOrgAssignmentAssociation_Staff] FOREIGN KEY([StaffUSI])

REFERENCES [edfi].[Staff] ([StaffUSI])

GO

ALTER TABLE [edfi].[StaffEducationOrgAssignmentAssociation] CHECK CONSTRAINT [FK_StaffEducationOrgAssignmentAssociation_Staff]

GO

/****** Object:  ForeignKey [FK_EmploymentStatusType_StaffEducationOrgEmploymentAssociation]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffEducationOrgEmploymentAssociation]  WITH CHECK ADD  CONSTRAINT [FK_EmploymentStatusType_StaffEducationOrgEmploymentAssociation] FOREIGN KEY([EmploymentStatusTypeId])

REFERENCES [edfi].[EmploymentStatusType] ([EmploymentStatusTypeId])

GO

ALTER TABLE [edfi].[StaffEducationOrgEmploymentAssociation] CHECK CONSTRAINT [FK_EmploymentStatusType_StaffEducationOrgEmploymentAssociation]

GO

/****** Object:  ForeignKey [FK_StaffEducationOrgEmploymentAssociation_EducationOrganization]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffEducationOrgEmploymentAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StaffEducationOrgEmploymentAssociation_EducationOrganization] FOREIGN KEY([EducationOrganizationId])

REFERENCES [edfi].[EducationOrganization] ([EducationOrganizationId])

GO

ALTER TABLE [edfi].[StaffEducationOrgEmploymentAssociation] CHECK CONSTRAINT [FK_StaffEducationOrgEmploymentAssociation_EducationOrganization]

GO

/****** Object:  ForeignKey [FK_StaffEducationOrgEmploymentAssociation_SeparationReasonType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffEducationOrgEmploymentAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StaffEducationOrgEmploymentAssociation_SeparationReasonType] FOREIGN KEY([SeparationReasonTypeId])

REFERENCES [edfi].[SeparationReasonType] ([SeparationReasonTypeId])

GO

ALTER TABLE [edfi].[StaffEducationOrgEmploymentAssociation] CHECK CONSTRAINT [FK_StaffEducationOrgEmploymentAssociation_SeparationReasonType]

GO

/****** Object:  ForeignKey [FK_StaffEducationOrgEmploymentAssociation_SeparationType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffEducationOrgEmploymentAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StaffEducationOrgEmploymentAssociation_SeparationType] FOREIGN KEY([SeparationTypeId])

REFERENCES [edfi].[SeparationType] ([SeparationTypeId])

GO

ALTER TABLE [edfi].[StaffEducationOrgEmploymentAssociation] CHECK CONSTRAINT [FK_StaffEducationOrgEmploymentAssociation_SeparationType]

GO

/****** Object:  ForeignKey [FK_StaffEducationOrgEmploymentAssociation_Staff]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffEducationOrgEmploymentAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StaffEducationOrgEmploymentAssociation_Staff] FOREIGN KEY([StaffUSI])

REFERENCES [edfi].[Staff] ([StaffUSI])

GO

ALTER TABLE [edfi].[StaffEducationOrgEmploymentAssociation] CHECK CONSTRAINT [FK_StaffEducationOrgEmploymentAssociation_Staff]

GO

/****** Object:  ForeignKey [FK_StaffElectronicMail_ElectronicMailType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffElectronicMail]  WITH CHECK ADD  CONSTRAINT [FK_StaffElectronicMail_ElectronicMailType] FOREIGN KEY([ElectronicMailTypeId])

REFERENCES [edfi].[ElectronicMailType] ([ElectronicMailTypeId])

GO

ALTER TABLE [edfi].[StaffElectronicMail] CHECK CONSTRAINT [FK_StaffElectronicMail_ElectronicMailType]

GO

/****** Object:  ForeignKey [FK_StaffElectronicMail_Staff]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffElectronicMail]  WITH CHECK ADD  CONSTRAINT [FK_StaffElectronicMail_Staff] FOREIGN KEY([StaffUSI])

REFERENCES [edfi].[Staff] ([StaffUSI])

GO

ALTER TABLE [edfi].[StaffElectronicMail] CHECK CONSTRAINT [FK_StaffElectronicMail_Staff]

GO

/****** Object:  ForeignKey [FK_StaffIdentificationCode_Staff]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffIdentificationCode]  WITH CHECK ADD  CONSTRAINT [FK_StaffIdentificationCode_Staff] FOREIGN KEY([StaffUSI])

REFERENCES [edfi].[Staff] ([StaffUSI])

GO

ALTER TABLE [edfi].[StaffIdentificationCode] CHECK CONSTRAINT [FK_StaffIdentificationCode_Staff]

GO

/****** Object:  ForeignKey [FK_StaffIdentificationCode_StaffIdentificationSystemType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffIdentificationCode]  WITH CHECK ADD  CONSTRAINT [FK_StaffIdentificationCode_StaffIdentificationSystemType] FOREIGN KEY([StaffIdentificationSystemTypeId])

REFERENCES [edfi].[StaffIdentificationSystemType] ([StaffIdentificationSystemTypeId])

GO

ALTER TABLE [edfi].[StaffIdentificationCode] CHECK CONSTRAINT [FK_StaffIdentificationCode_StaffIdentificationSystemType]

GO

/****** Object:  ForeignKey [FK_StaffOtherName_GenerationCodeSuffixType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffOtherName]  WITH CHECK ADD  CONSTRAINT [FK_StaffOtherName_GenerationCodeSuffixType] FOREIGN KEY([GenerationCodeSuffixTypeId])

REFERENCES [edfi].[GenerationCodeSuffixType] ([GenerationCodeSuffixTypeId])

GO

ALTER TABLE [edfi].[StaffOtherName] CHECK CONSTRAINT [FK_StaffOtherName_GenerationCodeSuffixType]

GO

/****** Object:  ForeignKey [FK_StaffOtherName_OtherNameType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffOtherName]  WITH CHECK ADD  CONSTRAINT [FK_StaffOtherName_OtherNameType] FOREIGN KEY([OtherNameTypeId])

REFERENCES [edfi].[OtherNameType] ([OtherNameTypeId])

GO

ALTER TABLE [edfi].[StaffOtherName] CHECK CONSTRAINT [FK_StaffOtherName_OtherNameType]

GO

/****** Object:  ForeignKey [FK_StaffOtherName_PersonalTitlePrefixType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffOtherName]  WITH CHECK ADD  CONSTRAINT [FK_StaffOtherName_PersonalTitlePrefixType] FOREIGN KEY([PersonalTitlePrefixTypeId])

REFERENCES [edfi].[PersonalTitlePrefixType] ([PersonalTitlePrefixTypeId])

GO

ALTER TABLE [edfi].[StaffOtherName] CHECK CONSTRAINT [FK_StaffOtherName_PersonalTitlePrefixType]

GO

/****** Object:  ForeignKey [FK_StaffOtherName_Staff]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffOtherName]  WITH CHECK ADD  CONSTRAINT [FK_StaffOtherName_Staff] FOREIGN KEY([StaffUSI])

REFERENCES [edfi].[Staff] ([StaffUSI])

GO

ALTER TABLE [edfi].[StaffOtherName] CHECK CONSTRAINT [FK_StaffOtherName_Staff]

GO

/****** Object:  ForeignKey [FK_StaffProgramAssociation_Program]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffProgramAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StaffProgramAssociation_Program] FOREIGN KEY([EducationOrganizationId], [ProgramTypeId])

REFERENCES [edfi].[Program] ([EducationOrganizationId], [ProgramTypeId])

GO

ALTER TABLE [edfi].[StaffProgramAssociation] CHECK CONSTRAINT [FK_StaffProgramAssociation_Program]

GO

/****** Object:  ForeignKey [FK_StaffProgramAssociation_Staff]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffProgramAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StaffProgramAssociation_Staff] FOREIGN KEY([StaffUSI])

REFERENCES [edfi].[Staff] ([StaffUSI])

GO

ALTER TABLE [edfi].[StaffProgramAssociation] CHECK CONSTRAINT [FK_StaffProgramAssociation_Staff]

GO

/****** Object:  ForeignKey [FK_RaceType_StaffRace]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffRace]  WITH CHECK ADD  CONSTRAINT [FK_RaceType_StaffRace] FOREIGN KEY([RaceTypeId])

REFERENCES [edfi].[RaceType] ([RaceTypeId])

GO

ALTER TABLE [edfi].[StaffRace] CHECK CONSTRAINT [FK_RaceType_StaffRace]

GO

/****** Object:  ForeignKey [FK_StaffRace_Staff]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffRace]  WITH CHECK ADD  CONSTRAINT [FK_StaffRace_Staff] FOREIGN KEY([StaffUSI])

REFERENCES [edfi].[Staff] ([StaffUSI])

GO

ALTER TABLE [edfi].[StaffRace] CHECK CONSTRAINT [FK_StaffRace_Staff]

GO

/****** Object:  ForeignKey [FK_StaffTelephone_Staff]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffTelephone]  WITH CHECK ADD  CONSTRAINT [FK_StaffTelephone_Staff] FOREIGN KEY([StaffUSI])

REFERENCES [edfi].[Staff] ([StaffUSI])

GO

ALTER TABLE [edfi].[StaffTelephone] CHECK CONSTRAINT [FK_StaffTelephone_Staff]

GO

/****** Object:  ForeignKey [FK_StaffTelephone_TelephoneNumberType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StaffTelephone]  WITH CHECK ADD  CONSTRAINT [FK_StaffTelephone_TelephoneNumberType] FOREIGN KEY([TelephoneNumberTypeId])

REFERENCES [edfi].[TelephoneNumberType] ([TelephoneNumberTypeId])

GO

ALTER TABLE [edfi].[StaffTelephone] CHECK CONSTRAINT [FK_StaffTelephone_TelephoneNumberType]

GO

/****** Object:  ForeignKey [FK_StateEducationAgency_EducationOrganization]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StateEducationAgency]  WITH CHECK ADD  CONSTRAINT [FK_StateEducationAgency_EducationOrganization] FOREIGN KEY([StateEducationAgencyId])

REFERENCES [edfi].[EducationOrganization] ([EducationOrganizationId])

GO

ALTER TABLE [edfi].[StateEducationAgency] CHECK CONSTRAINT [FK_StateEducationAgency_EducationOrganization]

GO

/****** Object:  ForeignKey [FK_LimitedEnglishProficiencyType_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Student]  WITH CHECK ADD  CONSTRAINT [FK_LimitedEnglishProficiencyType_Student] FOREIGN KEY([LimitedEnglishProficiencyTypeId])

REFERENCES [edfi].[LimitedEnglishProficiencyType] ([LimitedEnglishProficiencyTypeId])

GO

ALTER TABLE [edfi].[Student] CHECK CONSTRAINT [FK_LimitedEnglishProficiencyType_Student]

GO

/****** Object:  ForeignKey [FK_OldEthnicityType_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Student]  WITH CHECK ADD  CONSTRAINT [FK_OldEthnicityType_Student] FOREIGN KEY([OldEthnicityTypeId])

REFERENCES [edfi].[OldEthnicityType] ([OldEthnicityTypeId])

GO

ALTER TABLE [edfi].[Student] CHECK CONSTRAINT [FK_OldEthnicityType_Student]

GO

/****** Object:  ForeignKey [FK_SchoolFoodServicesEligibilityType_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Student]  WITH CHECK ADD  CONSTRAINT [FK_SchoolFoodServicesEligibilityType_Student] FOREIGN KEY([SchoolFoodServicesEligibilityTypeId])

REFERENCES [edfi].[SchoolFoodServicesEligibilityType] ([SchoolFoodServicesEligibilityTypeId])

GO

ALTER TABLE [edfi].[Student] CHECK CONSTRAINT [FK_SchoolFoodServicesEligibilityType_Student]

GO

/****** Object:  ForeignKey [FK_SexType_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Student]  WITH CHECK ADD  CONSTRAINT [FK_SexType_Student] FOREIGN KEY([SexTypeId])

REFERENCES [edfi].[SexType] ([SexTypeId])

GO

ALTER TABLE [edfi].[Student] CHECK CONSTRAINT [FK_SexType_Student]

GO

/****** Object:  ForeignKey [FK_Student_CountryCodeType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Student]  WITH CHECK ADD  CONSTRAINT [FK_Student_CountryCodeType] FOREIGN KEY([CountryOfBirthCodeTypeId])

REFERENCES [edfi].[CountryCodeType] ([CountryCodeTypeId])

GO

ALTER TABLE [edfi].[Student] CHECK CONSTRAINT [FK_Student_CountryCodeType]

GO

/****** Object:  ForeignKey [FK_Student_GenerationCodeSuffixType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Student]  WITH CHECK ADD  CONSTRAINT [FK_Student_GenerationCodeSuffixType] FOREIGN KEY([GenerationCodeSuffixTypeId])

REFERENCES [edfi].[GenerationCodeSuffixType] ([GenerationCodeSuffixTypeId])

GO

ALTER TABLE [edfi].[Student] CHECK CONSTRAINT [FK_Student_GenerationCodeSuffixType]

GO

/****** Object:  ForeignKey [FK_Student_PersonalInformationVerificationType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Student]  WITH CHECK ADD  CONSTRAINT [FK_Student_PersonalInformationVerificationType] FOREIGN KEY([PersonalInformationVerificationTypeId])

REFERENCES [edfi].[PersonalInformationVerificationType] ([PersonalInformationVerificationTypeId])

GO

ALTER TABLE [edfi].[Student] CHECK CONSTRAINT [FK_Student_PersonalInformationVerificationType]

GO

/****** Object:  ForeignKey [FK_Student_PersonalTitlePrefixType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Student]  WITH CHECK ADD  CONSTRAINT [FK_Student_PersonalTitlePrefixType] FOREIGN KEY([PersonalTitlePrefixTypeId])

REFERENCES [edfi].[PersonalTitlePrefixType] ([PersonalTitlePrefixTypeId])

GO

ALTER TABLE [edfi].[Student] CHECK CONSTRAINT [FK_Student_PersonalTitlePrefixType]

GO

/****** Object:  ForeignKey [FK_Student_StateAbbreviationType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[Student]  WITH CHECK ADD  CONSTRAINT [FK_Student_StateAbbreviationType] FOREIGN KEY([StateOfBirthAbbreviationTypeId])

REFERENCES [edfi].[StateAbbreviationType] ([StateAbbreviationTypeId])

GO

ALTER TABLE [edfi].[Student] CHECK CONSTRAINT [FK_Student_StateAbbreviationType]

GO

/****** Object:  ForeignKey [FK_StudentAcademicRecord_SchoolYearType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAcademicRecord]  WITH CHECK ADD  CONSTRAINT [FK_StudentAcademicRecord_SchoolYearType] FOREIGN KEY([SchoolYear])

REFERENCES [edfi].[SchoolYearType] ([SchoolYear])

GO

ALTER TABLE [edfi].[StudentAcademicRecord] CHECK CONSTRAINT [FK_StudentAcademicRecord_SchoolYearType]

GO

/****** Object:  ForeignKey [FK_StudentAcademicRecord_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAcademicRecord]  WITH CHECK ADD  CONSTRAINT [FK_StudentAcademicRecord_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[StudentAcademicRecord] CHECK CONSTRAINT [FK_StudentAcademicRecord_Student]

GO

/****** Object:  ForeignKey [FK_StudentAddress_AddressType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAddress]  WITH CHECK ADD  CONSTRAINT [FK_StudentAddress_AddressType] FOREIGN KEY([AddressTypeId])

REFERENCES [edfi].[AddressType] ([AddressTypeId])

GO

ALTER TABLE [edfi].[StudentAddress] CHECK CONSTRAINT [FK_StudentAddress_AddressType]

GO

/****** Object:  ForeignKey [FK_StudentAddress_CountryCode]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAddress]  WITH CHECK ADD  CONSTRAINT [FK_StudentAddress_CountryCode] FOREIGN KEY([CountryCodeTypeId])

REFERENCES [edfi].[CountryCodeType] ([CountryCodeTypeId])

GO

ALTER TABLE [edfi].[StudentAddress] CHECK CONSTRAINT [FK_StudentAddress_CountryCode]

GO

/****** Object:  ForeignKey [FK_StudentAddress_StateAbbreviationType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAddress]  WITH CHECK ADD  CONSTRAINT [FK_StudentAddress_StateAbbreviationType] FOREIGN KEY([StateAbbreviationTypeId])

REFERENCES [edfi].[StateAbbreviationType] ([StateAbbreviationTypeId])

GO

ALTER TABLE [edfi].[StudentAddress] CHECK CONSTRAINT [FK_StudentAddress_StateAbbreviationType]

GO

/****** Object:  ForeignKey [FK_StudentAddress_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAddress]  WITH CHECK ADD  CONSTRAINT [FK_StudentAddress_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[StudentAddress] CHECK CONSTRAINT [FK_StudentAddress_Student]

GO

/****** Object:  ForeignKey [FK_AdministrationEnvironmentType_StudentAssessment]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAssessment]  WITH CHECK ADD  CONSTRAINT [FK_AdministrationEnvironmentType_StudentAssessment] FOREIGN KEY([AdministrationEnvironmentTypeId])

REFERENCES [edfi].[AdministrationEnvironmentType] ([AdministrationEnvironmentTypeId])

GO

ALTER TABLE [edfi].[StudentAssessment] CHECK CONSTRAINT [FK_AdministrationEnvironmentType_StudentAssessment]

GO

/****** Object:  ForeignKey [FK_ReasonNotTestedType_StudentAssessment]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAssessment]  WITH CHECK ADD  CONSTRAINT [FK_ReasonNotTestedType_StudentAssessment] FOREIGN KEY([ReasonNotTestedTypeId])

REFERENCES [edfi].[ReasonNotTestedType] ([ReasonNotTestedTypeId])

GO

ALTER TABLE [edfi].[StudentAssessment] CHECK CONSTRAINT [FK_ReasonNotTestedType_StudentAssessment]

GO

/****** Object:  ForeignKey [FK_RetestIndicatorType_StudentAssessment]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAssessment]  WITH CHECK ADD  CONSTRAINT [FK_RetestIndicatorType_StudentAssessment] FOREIGN KEY([RetestIndicatorTypeId])

REFERENCES [edfi].[RetestIndicatorType] ([RetestIndicatorTypeId])

GO

ALTER TABLE [edfi].[StudentAssessment] CHECK CONSTRAINT [FK_RetestIndicatorType_StudentAssessment]

GO

/****** Object:  ForeignKey [FK_StudentAssessment_Assessment]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAssessment]  WITH CHECK ADD  CONSTRAINT [FK_StudentAssessment_Assessment] FOREIGN KEY([AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

REFERENCES [edfi].[Assessment] ([AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

GO

ALTER TABLE [edfi].[StudentAssessment] CHECK CONSTRAINT [FK_StudentAssessment_Assessment]

GO

/****** Object:  ForeignKey [FK_StudentAssessment_GradeLevelType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAssessment]  WITH CHECK ADD  CONSTRAINT [FK_StudentAssessment_GradeLevelType] FOREIGN KEY([WhenAssessedGradeLevelTypeId])

REFERENCES [edfi].[GradeLevelType] ([GradeLevelTypeId])

GO

ALTER TABLE [edfi].[StudentAssessment] CHECK CONSTRAINT [FK_StudentAssessment_GradeLevelType]

GO

/****** Object:  ForeignKey [FK_StudentAssessment_LanguagesType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAssessment]  WITH CHECK ADD  CONSTRAINT [FK_StudentAssessment_LanguagesType] FOREIGN KEY([AdministrationLanguageTypeId])

REFERENCES [edfi].[LanguagesType] ([LanguageTypeId])

GO

ALTER TABLE [edfi].[StudentAssessment] CHECK CONSTRAINT [FK_StudentAssessment_LanguagesType]

GO

/****** Object:  ForeignKey [FK_StudentAssessment_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAssessment]  WITH CHECK ADD  CONSTRAINT [FK_StudentAssessment_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[StudentAssessment] CHECK CONSTRAINT [FK_StudentAssessment_Student]

GO

/****** Object:  ForeignKey [FK_AssessmentItemResultType_StudentAssessmentItem]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAssessmentItem]  WITH CHECK ADD  CONSTRAINT [FK_AssessmentItemResultType_StudentAssessmentItem] FOREIGN KEY([AssessmentItemResultTypeId])

REFERENCES [edfi].[AssessmentItemResultType] ([AssessmentItemResultTypeId])

GO

ALTER TABLE [edfi].[StudentAssessmentItem] CHECK CONSTRAINT [FK_AssessmentItemResultType_StudentAssessmentItem]

GO

/****** Object:  ForeignKey [FK_ResponseIndicatorType_StudentAssessmentItem]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAssessmentItem]  WITH CHECK ADD  CONSTRAINT [FK_ResponseIndicatorType_StudentAssessmentItem] FOREIGN KEY([ResponseIndicatorTypeId])

REFERENCES [edfi].[ResponseIndicatorType] ([ResponseIndicatorTypeId])

GO

ALTER TABLE [edfi].[StudentAssessmentItem] CHECK CONSTRAINT [FK_ResponseIndicatorType_StudentAssessmentItem]

GO

/****** Object:  ForeignKey [FK_StudentAssessmentItem_AssessmentItem]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAssessmentItem]  WITH CHECK ADD  CONSTRAINT [FK_StudentAssessmentItem_AssessmentItem] FOREIGN KEY([AssesmentItem], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

REFERENCES [edfi].[AssessmentItem] ([AssesmentItem], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

GO

ALTER TABLE [edfi].[StudentAssessmentItem] CHECK CONSTRAINT [FK_StudentAssessmentItem_AssessmentItem]

GO

/****** Object:  ForeignKey [FK_StudentAssessmentItem_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAssessmentItem]  WITH CHECK ADD  CONSTRAINT [FK_StudentAssessmentItem_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[StudentAssessmentItem] CHECK CONSTRAINT [FK_StudentAssessmentItem_Student]

GO

/****** Object:  ForeignKey [FK_StudentAssessmentItem_StudentAssessment]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAssessmentItem]  WITH CHECK ADD  CONSTRAINT [FK_StudentAssessmentItem_StudentAssessment] FOREIGN KEY([StudentUSI], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version], [AdministrationDate])

REFERENCES [edfi].[StudentAssessment] ([StudentUSI], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version], [AdministrationDate])

GO

ALTER TABLE [edfi].[StudentAssessmentItem] CHECK CONSTRAINT [FK_StudentAssessmentItem_StudentAssessment]

GO

/****** Object:  ForeignKey [FK_LinguisticAccommodationsType_StudentAssessmentLinguisticAccommodations]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAssessmentLinguisticAccommodations]  WITH CHECK ADD  CONSTRAINT [FK_LinguisticAccommodationsType_StudentAssessmentLinguisticAccommodations] FOREIGN KEY([LinguisticAccommodationsTypeId])

REFERENCES [edfi].[LinguisticAccommodationsType] ([LinguisticAccommodationsTypeId])

GO

ALTER TABLE [edfi].[StudentAssessmentLinguisticAccommodations] CHECK CONSTRAINT [FK_LinguisticAccommodationsType_StudentAssessmentLinguisticAccommodations]

GO

/****** Object:  ForeignKey [FK_StudentAssessmentLinguisticAccommodations_StudentAssessment]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAssessmentLinguisticAccommodations]  WITH CHECK ADD  CONSTRAINT [FK_StudentAssessmentLinguisticAccommodations_StudentAssessment] FOREIGN KEY([StudentUSI], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version], [AdministrationDate])

REFERENCES [edfi].[StudentAssessment] ([StudentUSI], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version], [AdministrationDate])

GO

ALTER TABLE [edfi].[StudentAssessmentLinguisticAccommodations] CHECK CONSTRAINT [FK_StudentAssessmentLinguisticAccommodations_StudentAssessment]

GO

/****** Object:  ForeignKey [FK_StudentAssessmentPerformanceLevel_PerformanceLevelDescriptor]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAssessmentPerformanceLevel]  WITH CHECK ADD  CONSTRAINT [FK_StudentAssessmentPerformanceLevel_PerformanceLevelDescriptor] FOREIGN KEY([PerformanceLevelDescriptorId])

REFERENCES [edfi].[PerformanceLevelDescriptor] ([PerformanceLevelDescriptorId])

GO

ALTER TABLE [edfi].[StudentAssessmentPerformanceLevel] CHECK CONSTRAINT [FK_StudentAssessmentPerformanceLevel_PerformanceLevelDescriptor]

GO

/****** Object:  ForeignKey [FK_StudentAssessmentPerformanceLevel_StudentAssessment]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAssessmentPerformanceLevel]  WITH CHECK ADD  CONSTRAINT [FK_StudentAssessmentPerformanceLevel_StudentAssessment] FOREIGN KEY([StudentUSI], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version], [AdministrationDate])

REFERENCES [edfi].[StudentAssessment] ([StudentUSI], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version], [AdministrationDate])

GO

ALTER TABLE [edfi].[StudentAssessmentPerformanceLevel] CHECK CONSTRAINT [FK_StudentAssessmentPerformanceLevel_StudentAssessment]

GO

/****** Object:  ForeignKey [FK_StudentAssessmentScoreResult_AssessmentReportingMethodType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAssessmentScoreResult]  WITH CHECK ADD  CONSTRAINT [FK_StudentAssessmentScoreResult_AssessmentReportingMethodType] FOREIGN KEY([AssessmentReportingMethodTypeId])

REFERENCES [edfi].[AssessmentReportingMethodType] ([AssessmentReportingMethodTypeId])

GO

ALTER TABLE [edfi].[StudentAssessmentScoreResult] CHECK CONSTRAINT [FK_StudentAssessmentScoreResult_AssessmentReportingMethodType]

GO

/****** Object:  ForeignKey [FK_StudentAssessmentScoreResult_StudentAssessment]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAssessmentScoreResult]  WITH CHECK ADD  CONSTRAINT [FK_StudentAssessmentScoreResult_StudentAssessment] FOREIGN KEY([StudentUSI], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version], [AdministrationDate])

REFERENCES [edfi].[StudentAssessment] ([StudentUSI], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version], [AdministrationDate])

GO

ALTER TABLE [edfi].[StudentAssessmentScoreResult] CHECK CONSTRAINT [FK_StudentAssessmentScoreResult_StudentAssessment]

GO

/****** Object:  ForeignKey [FK_SpecialAccommodationsType_StudentAssessmentSpecialAccommodations]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAssessmentSpecialAccommodations]  WITH CHECK ADD  CONSTRAINT [FK_SpecialAccommodationsType_StudentAssessmentSpecialAccommodations] FOREIGN KEY([SpecialAccommodationsTypeId])

REFERENCES [edfi].[SpecialAccommodationsType] ([SpecialAccommodationsTypeId])

GO

ALTER TABLE [edfi].[StudentAssessmentSpecialAccommodations] CHECK CONSTRAINT [FK_SpecialAccommodationsType_StudentAssessmentSpecialAccommodations]

GO

/****** Object:  ForeignKey [FK_StudentAssessmentSpecialAccommodations_StudentAssessment]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentAssessmentSpecialAccommodations]  WITH CHECK ADD  CONSTRAINT [FK_StudentAssessmentSpecialAccommodations_StudentAssessment] FOREIGN KEY([StudentUSI], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version], [AdministrationDate])

REFERENCES [edfi].[StudentAssessment] ([StudentUSI], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version], [AdministrationDate])

GO

ALTER TABLE [edfi].[StudentAssessmentSpecialAccommodations] CHECK CONSTRAINT [FK_StudentAssessmentSpecialAccommodations_StudentAssessment]

GO

/****** Object:  ForeignKey [FK_StudentCharacteristics_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentCharacteristics]  WITH CHECK ADD  CONSTRAINT [FK_StudentCharacteristics_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[StudentCharacteristics] CHECK CONSTRAINT [FK_StudentCharacteristics_Student]

GO

/****** Object:  ForeignKey [FK_StudentCharacteristicsType_StudentCharacteristics]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentCharacteristics]  WITH CHECK ADD  CONSTRAINT [FK_StudentCharacteristicsType_StudentCharacteristics] FOREIGN KEY([StudentCharacteristicsTypeId])

REFERENCES [edfi].[StudentCharacteristicsType] ([StudentCharacteristicsTypeId])

GO

ALTER TABLE [edfi].[StudentCharacteristics] CHECK CONSTRAINT [FK_StudentCharacteristicsType_StudentCharacteristics]

GO

/****** Object:  ForeignKey [FK_StudentCohortAssociation_Cohort]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentCohortAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StudentCohortAssociation_Cohort] FOREIGN KEY([EducationOrganizationId], [CohortIdentifier])

REFERENCES [edfi].[Cohort] ([EducationOrganizationId], [CohortIdentifier])

GO

ALTER TABLE [edfi].[StudentCohortAssociation] CHECK CONSTRAINT [FK_StudentCohortAssociation_Cohort]

GO

/****** Object:  ForeignKey [FK_StudentCohortAssociation_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentCohortAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StudentCohortAssociation_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[StudentCohortAssociation] CHECK CONSTRAINT [FK_StudentCohortAssociation_Student]

GO

/****** Object:  ForeignKey [FK_StudentCohortYears_CohortYearType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentCohortYears]  WITH CHECK ADD  CONSTRAINT [FK_StudentCohortYears_CohortYearType] FOREIGN KEY([CohortYearTypeId])

REFERENCES [edfi].[CohortYearType] ([CohortYearTypeId])

GO

ALTER TABLE [edfi].[StudentCohortYears] CHECK CONSTRAINT [FK_StudentCohortYears_CohortYearType]

GO

/****** Object:  ForeignKey [FK_StudentCohortYears_SchoolYearType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentCohortYears]  WITH CHECK ADD  CONSTRAINT [FK_StudentCohortYears_SchoolYearType] FOREIGN KEY([SchoolYear])

REFERENCES [edfi].[SchoolYearType] ([SchoolYear])

GO

ALTER TABLE [edfi].[StudentCohortYears] CHECK CONSTRAINT [FK_StudentCohortYears_SchoolYearType]

GO

/****** Object:  ForeignKey [FK_StudentCohortYears_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentCohortYears]  WITH CHECK ADD  CONSTRAINT [FK_StudentCohortYears_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[StudentCohortYears] CHECK CONSTRAINT [FK_StudentCohortYears_Student]

GO

/****** Object:  ForeignKey [FK_StudentCompetencyLearningObjective_CompetencyLevelDescriptor]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentCompetencyLearningObjective]  WITH CHECK ADD  CONSTRAINT [FK_StudentCompetencyLearningObjective_CompetencyLevelDescriptor] FOREIGN KEY([CompetencyLevelDescriptorId])

REFERENCES [edfi].[CompetencyLevelDescriptor] ([CompetencyLevelDescriptorId])

GO

ALTER TABLE [edfi].[StudentCompetencyLearningObjective] CHECK CONSTRAINT [FK_StudentCompetencyLearningObjective_CompetencyLevelDescriptor]

GO

/****** Object:  ForeignKey [FK_StudentCompetencyLearningObjective_LearningObjective]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentCompetencyLearningObjective]  WITH CHECK ADD  CONSTRAINT [FK_StudentCompetencyLearningObjective_LearningObjective] FOREIGN KEY([Objective], [AcademicSubjectTypeId], [ObjectiveGradeLevelTypeId])

REFERENCES [edfi].[LearningObjective] ([Objective], [AcademicSubjectTypeId], [ObjectiveGradeLevelTypeId])

GO

ALTER TABLE [edfi].[StudentCompetencyLearningObjective] CHECK CONSTRAINT [FK_StudentCompetencyLearningObjective_LearningObjective]

GO

/****** Object:  ForeignKey [FK_StudentCompetencyLearningObjective_StudentSectionAssociation]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentCompetencyLearningObjective]  WITH CHECK ADD  CONSTRAINT [FK_StudentCompetencyLearningObjective_StudentSectionAssociation] FOREIGN KEY([StudentUSI], [SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear], [BeginDate])

REFERENCES [edfi].[StudentSectionAssociation] ([StudentUSI], [SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear], [BeginDate])

GO

ALTER TABLE [edfi].[StudentCompetencyLearningObjective] CHECK CONSTRAINT [FK_StudentCompetencyLearningObjective_StudentSectionAssociation]

GO

/****** Object:  ForeignKey [FK_StudentCompetencyObjective_StudentSectionAssociation]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentCompetencyObjective]  WITH CHECK ADD  CONSTRAINT [FK_StudentCompetencyObjective_StudentSectionAssociation] FOREIGN KEY([StudentUSI], [SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear], [BeginDate])

REFERENCES [edfi].[StudentSectionAssociation] ([StudentUSI], [SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear], [BeginDate])

GO

ALTER TABLE [edfi].[StudentCompetencyObjective] CHECK CONSTRAINT [FK_StudentCompetencyObjective_StudentSectionAssociation]

GO

/****** Object:  ForeignKey [FK_CareerPathwayType_StudentCTEProgramAssociation]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentCTEProgramAssociation]  WITH CHECK ADD  CONSTRAINT [FK_CareerPathwayType_StudentCTEProgramAssociation] FOREIGN KEY([CareerPathwayTypeId])

REFERENCES [edfi].[CareerPathwayType] ([CareerPathwayTypeId])

GO

ALTER TABLE [edfi].[StudentCTEProgramAssociation] CHECK CONSTRAINT [FK_CareerPathwayType_StudentCTEProgramAssociation]

GO

/****** Object:  ForeignKey [FK_StudentCTEProgramAssociation_StudentProgramAssociation]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentCTEProgramAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StudentCTEProgramAssociation_StudentProgramAssociation] FOREIGN KEY([StudentUSI], [ProgramTypeId], [EducationOrganizationId], [BeginDate])

REFERENCES [edfi].[StudentProgramAssociation] ([StudentUSI], [ProgramTypeId], [EducationOrganizationId], [BeginDate])

GO

ALTER TABLE [edfi].[StudentCTEProgramAssociation] CHECK CONSTRAINT [FK_StudentCTEProgramAssociation_StudentProgramAssociation]

GO

/****** Object:  ForeignKey [FK_StudentDisciplineIncidentAssociation_DisciplineIncident]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentDisciplineIncidentAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StudentDisciplineIncidentAssociation_DisciplineIncident] FOREIGN KEY([SchoolId], [IncidentIdentifier])

REFERENCES [edfi].[DisciplineIncident] ([SchoolId], [IncidentIdentifier])

GO

ALTER TABLE [edfi].[StudentDisciplineIncidentAssociation] CHECK CONSTRAINT [FK_StudentDisciplineIncidentAssociation_DisciplineIncident]

GO

/****** Object:  ForeignKey [FK_StudentDisciplineIncidentAssociation_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentDisciplineIncidentAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StudentDisciplineIncidentAssociation_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[StudentDisciplineIncidentAssociation] CHECK CONSTRAINT [FK_StudentDisciplineIncidentAssociation_Student]

GO

/****** Object:  ForeignKey [FK_StudentParticipationCodeType_StudentDisciplineIncidentAssociation]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentDisciplineIncidentAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StudentParticipationCodeType_StudentDisciplineIncidentAssociation] FOREIGN KEY([StudentParticipationCodeTypeId])

REFERENCES [edfi].[StudentParticipationCodeType] ([StudentParticipationCodeTypeId])

GO

ALTER TABLE [edfi].[StudentDisciplineIncidentAssociation] CHECK CONSTRAINT [FK_StudentParticipationCodeType_StudentDisciplineIncidentAssociation]

GO

/****** Object:  ForeignKey [FK_StudentDisciplineIncidentBehavior_BehaviorDescriptor]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentDisciplineIncidentBehavior]  WITH CHECK ADD  CONSTRAINT [FK_StudentDisciplineIncidentBehavior_BehaviorDescriptor] FOREIGN KEY([BehaviorDescriptorId])

REFERENCES [edfi].[BehaviorDescriptor] ([BehaviorDescriptorId])

GO

ALTER TABLE [edfi].[StudentDisciplineIncidentBehavior] CHECK CONSTRAINT [FK_StudentDisciplineIncidentBehavior_BehaviorDescriptor]

GO

/****** Object:  ForeignKey [FK_StudentDisciplineIncidentBehavior_StudentDisciplineIncidentAssociation]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentDisciplineIncidentBehavior]  WITH CHECK ADD  CONSTRAINT [FK_StudentDisciplineIncidentBehavior_StudentDisciplineIncidentAssociation] FOREIGN KEY([StudentUSI], [SchoolId], [IncidentIdentifier])

REFERENCES [edfi].[StudentDisciplineIncidentAssociation] ([StudentUSI], [SchoolId], [IncidentIdentifier])

GO

ALTER TABLE [edfi].[StudentDisciplineIncidentBehavior] CHECK CONSTRAINT [FK_StudentDisciplineIncidentBehavior_StudentDisciplineIncidentAssociation]

GO

/****** Object:  ForeignKey [FK_StudentDisciplineIncidentSecondaryBehavior_BehaviorCategoryType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentDisciplineIncidentSecondaryBehavior]  WITH CHECK ADD  CONSTRAINT [FK_StudentDisciplineIncidentSecondaryBehavior_BehaviorCategoryType] FOREIGN KEY([BehaviorCategoryTypeId])

REFERENCES [edfi].[BehaviorCategoryType] ([BehaviorCategoryTypeId])

GO

ALTER TABLE [edfi].[StudentDisciplineIncidentSecondaryBehavior] CHECK CONSTRAINT [FK_StudentDisciplineIncidentSecondaryBehavior_BehaviorCategoryType]

GO

/****** Object:  ForeignKey [FK_StudentDisciplineIncidentSecondaryBehavior_StudentDisciplineIncidentAssociation]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentDisciplineIncidentSecondaryBehavior]  WITH CHECK ADD  CONSTRAINT [FK_StudentDisciplineIncidentSecondaryBehavior_StudentDisciplineIncidentAssociation] FOREIGN KEY([StudentUSI], [SchoolId], [IncidentIdentifier])

REFERENCES [edfi].[StudentDisciplineIncidentAssociation] ([StudentUSI], [SchoolId], [IncidentIdentifier])

GO

ALTER TABLE [edfi].[StudentDisciplineIncidentSecondaryBehavior] CHECK CONSTRAINT [FK_StudentDisciplineIncidentSecondaryBehavior_StudentDisciplineIncidentAssociation]

GO

/****** Object:  ForeignKey [FK_StudentElectronicMail_ElectronicMailType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentElectronicMail]  WITH CHECK ADD  CONSTRAINT [FK_StudentElectronicMail_ElectronicMailType] FOREIGN KEY([ElectronicMailTypeId])

REFERENCES [edfi].[ElectronicMailType] ([ElectronicMailTypeId])

GO

ALTER TABLE [edfi].[StudentElectronicMail] CHECK CONSTRAINT [FK_StudentElectronicMail_ElectronicMailType]

GO

/****** Object:  ForeignKey [FK_StudentElectronicMail_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentElectronicMail]  WITH CHECK ADD  CONSTRAINT [FK_StudentElectronicMail_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[StudentElectronicMail] CHECK CONSTRAINT [FK_StudentElectronicMail_Student]

GO

/****** Object:  ForeignKey [FK_StudentGradebookEntry_StudentSectionAssociation]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentGradebookEntry]  WITH CHECK ADD  CONSTRAINT [FK_StudentGradebookEntry_StudentSectionAssociation] FOREIGN KEY([StudentUSI], [SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear], [BeginDate])

REFERENCES [edfi].[StudentSectionAssociation] ([StudentUSI], [SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear], [BeginDate])

GO

ALTER TABLE [edfi].[StudentGradebookEntry] CHECK CONSTRAINT [FK_StudentGradebookEntry_StudentSectionAssociation]

GO

/****** Object:  ForeignKey [FK_StudentHomeLanguages_LanguagesType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentHomeLanguages]  WITH CHECK ADD  CONSTRAINT [FK_StudentHomeLanguages_LanguagesType] FOREIGN KEY([LanguageTypeId])

REFERENCES [edfi].[LanguagesType] ([LanguageTypeId])

GO

ALTER TABLE [edfi].[StudentHomeLanguages] CHECK CONSTRAINT [FK_StudentHomeLanguages_LanguagesType]

GO

/****** Object:  ForeignKey [FK_StudentHomeLanguages_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentHomeLanguages]  WITH CHECK ADD  CONSTRAINT [FK_StudentHomeLanguages_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[StudentHomeLanguages] CHECK CONSTRAINT [FK_StudentHomeLanguages_Student]

GO

/****** Object:  ForeignKey [FK_StudentIdentificationCode_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentIdentificationCode]  WITH CHECK ADD  CONSTRAINT [FK_StudentIdentificationCode_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[StudentIdentificationCode] CHECK CONSTRAINT [FK_StudentIdentificationCode_Student]

GO

/****** Object:  ForeignKey [FK_StudentIdentificationCode_StudentIdentificationSystemType]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentIdentificationCode]  WITH CHECK ADD  CONSTRAINT [FK_StudentIdentificationCode_StudentIdentificationSystemType] FOREIGN KEY([StudentIdentificationSystemTypeId])

REFERENCES [edfi].[StudentIdentificationSystemType] ([StudentIdentificationSystemTypeId])

GO

ALTER TABLE [edfi].[StudentIdentificationCode] CHECK CONSTRAINT [FK_StudentIdentificationCode_StudentIdentificationSystemType]

GO

/****** Object:  ForeignKey [FK_StudentIndicator_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentIndicator]  WITH CHECK ADD  CONSTRAINT [FK_StudentIndicator_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[StudentIndicator] CHECK CONSTRAINT [FK_StudentIndicator_Student]

GO

/****** Object:  ForeignKey [FK_LanguagesType_StudentLanguages]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentLanguages]  WITH CHECK ADD  CONSTRAINT [FK_LanguagesType_StudentLanguages] FOREIGN KEY([LanguageTypeId])

REFERENCES [edfi].[LanguagesType] ([LanguageTypeId])

GO

ALTER TABLE [edfi].[StudentLanguages] CHECK CONSTRAINT [FK_LanguagesType_StudentLanguages]

GO

/****** Object:  ForeignKey [FK_StudentLanguages_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentLanguages]  WITH CHECK ADD  CONSTRAINT [FK_StudentLanguages_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[StudentLanguages] CHECK CONSTRAINT [FK_StudentLanguages_Student]

GO

/****** Object:  ForeignKey [FK_StudentLearningStyle_Student]    Script Date: 10/20/2011 10:44:06 ******/

ALTER TABLE [edfi].[StudentLearningStyle]  WITH CHECK ADD  CONSTRAINT [FK_StudentLearningStyle_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[StudentLearningStyle] CHECK CONSTRAINT [FK_StudentLearningStyle_Student]

GO

/****** Object:  ForeignKey [FK_StudentObjectiveAssessment_ObjectiveAssessment]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentObjectiveAssessment]  WITH CHECK ADD  CONSTRAINT [FK_StudentObjectiveAssessment_ObjectiveAssessment] FOREIGN KEY([ObjectiveItem], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

REFERENCES [edfi].[ObjectiveAssessment] ([ObjectiveItem], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version])

GO

ALTER TABLE [edfi].[StudentObjectiveAssessment] CHECK CONSTRAINT [FK_StudentObjectiveAssessment_ObjectiveAssessment]

GO

/****** Object:  ForeignKey [FK_StudentObjectiveAssessment_StudentAssessment]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentObjectiveAssessment]  WITH CHECK ADD  CONSTRAINT [FK_StudentObjectiveAssessment_StudentAssessment] FOREIGN KEY([StudentUSI], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version], [AdministrationDate])

REFERENCES [edfi].[StudentAssessment] ([StudentUSI], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version], [AdministrationDate])

GO

ALTER TABLE [edfi].[StudentObjectiveAssessment] CHECK CONSTRAINT [FK_StudentObjectiveAssessment_StudentAssessment]

GO

/****** Object:  ForeignKey [FK_StudentObjectiveAssessmentPerformanceLevel_PerformanceLevelDescriptor]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentObjectiveAssessmentPerformanceLevel]  WITH CHECK ADD  CONSTRAINT [FK_StudentObjectiveAssessmentPerformanceLevel_PerformanceLevelDescriptor] FOREIGN KEY([PerformanceLevelDescriptorId])

REFERENCES [edfi].[PerformanceLevelDescriptor] ([PerformanceLevelDescriptorId])

GO

ALTER TABLE [edfi].[StudentObjectiveAssessmentPerformanceLevel] CHECK CONSTRAINT [FK_StudentObjectiveAssessmentPerformanceLevel_PerformanceLevelDescriptor]

GO

/****** Object:  ForeignKey [FK_StudentObjectiveAssessmentPerformanceLevel_StudentObjectiveAssessment]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentObjectiveAssessmentPerformanceLevel]  WITH CHECK ADD  CONSTRAINT [FK_StudentObjectiveAssessmentPerformanceLevel_StudentObjectiveAssessment] FOREIGN KEY([StudentUSI], [ObjectiveItem], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version], [AdministrationDate])

REFERENCES [edfi].[StudentObjectiveAssessment] ([StudentUSI], [ObjectiveItem], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version], [AdministrationDate])

GO

ALTER TABLE [edfi].[StudentObjectiveAssessmentPerformanceLevel] CHECK CONSTRAINT [FK_StudentObjectiveAssessmentPerformanceLevel_StudentObjectiveAssessment]

GO

/****** Object:  ForeignKey [FK_StudentObjectiveAssessmentScoreResults_AssessmentReportingMethodType]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentObjectiveAssessmentScoreResults]  WITH CHECK ADD  CONSTRAINT [FK_StudentObjectiveAssessmentScoreResults_AssessmentReportingMethodType] FOREIGN KEY([AssessmentReportingMethodTypeId])

REFERENCES [edfi].[AssessmentReportingMethodType] ([AssessmentReportingMethodTypeId])

GO

ALTER TABLE [edfi].[StudentObjectiveAssessmentScoreResults] CHECK CONSTRAINT [FK_StudentObjectiveAssessmentScoreResults_AssessmentReportingMethodType]

GO

/****** Object:  ForeignKey [FK_StudentObjectiveAssessmentScoreResults_StudentObjectiveAssessment]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentObjectiveAssessmentScoreResults]  WITH CHECK ADD  CONSTRAINT [FK_StudentObjectiveAssessmentScoreResults_StudentObjectiveAssessment] FOREIGN KEY([StudentUSI], [ObjectiveItem], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version], [AdministrationDate])

REFERENCES [edfi].[StudentObjectiveAssessment] ([StudentUSI], [ObjectiveItem], [AssessmentTitle], [AcademicSubjectTypeId], [AssessedGradeLevelTypeId], [Version], [AdministrationDate])

GO

ALTER TABLE [edfi].[StudentObjectiveAssessmentScoreResults] CHECK CONSTRAINT [FK_StudentObjectiveAssessmentScoreResults_StudentObjectiveAssessment]

GO

/****** Object:  ForeignKey [FK_StudentOtherName_GenerationCodeSuffixType]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentOtherName]  WITH CHECK ADD  CONSTRAINT [FK_StudentOtherName_GenerationCodeSuffixType] FOREIGN KEY([GenerationCodeSuffixTypeId])

REFERENCES [edfi].[GenerationCodeSuffixType] ([GenerationCodeSuffixTypeId])

GO

ALTER TABLE [edfi].[StudentOtherName] CHECK CONSTRAINT [FK_StudentOtherName_GenerationCodeSuffixType]

GO

/****** Object:  ForeignKey [FK_StudentOtherName_OtherNameType]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentOtherName]  WITH CHECK ADD  CONSTRAINT [FK_StudentOtherName_OtherNameType] FOREIGN KEY([OtherNameTypeId])

REFERENCES [edfi].[OtherNameType] ([OtherNameTypeId])

GO

ALTER TABLE [edfi].[StudentOtherName] CHECK CONSTRAINT [FK_StudentOtherName_OtherNameType]

GO

/****** Object:  ForeignKey [FK_StudentOtherName_PersonalTitlePrefixType]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentOtherName]  WITH CHECK ADD  CONSTRAINT [FK_StudentOtherName_PersonalTitlePrefixType] FOREIGN KEY([PersonalTitlePrefixTypeId])

REFERENCES [edfi].[PersonalTitlePrefixType] ([PersonalTitlePrefixTypeId])

GO

ALTER TABLE [edfi].[StudentOtherName] CHECK CONSTRAINT [FK_StudentOtherName_PersonalTitlePrefixType]

GO

/****** Object:  ForeignKey [FK_StudentOtherName_Student]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentOtherName]  WITH CHECK ADD  CONSTRAINT [FK_StudentOtherName_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[StudentOtherName] CHECK CONSTRAINT [FK_StudentOtherName_Student]

GO

/****** Object:  ForeignKey [FK_RelationType_StudentParentAssociation]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentParentAssociation]  WITH CHECK ADD  CONSTRAINT [FK_RelationType_StudentParentAssociation] FOREIGN KEY([RelationTypeId])

REFERENCES [edfi].[RelationType] ([RelationTypeId])

GO

ALTER TABLE [edfi].[StudentParentAssociation] CHECK CONSTRAINT [FK_RelationType_StudentParentAssociation]

GO

/****** Object:  ForeignKey [FK_StudentParentAssociation_Parent]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentParentAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StudentParentAssociation_Parent] FOREIGN KEY([ParentUSI])

REFERENCES [edfi].[Parent] ([ParentUSI])

GO

ALTER TABLE [edfi].[StudentParentAssociation] CHECK CONSTRAINT [FK_StudentParentAssociation_Parent]

GO

/****** Object:  ForeignKey [FK_StudentParentAssociation_Student]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentParentAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StudentParentAssociation_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[StudentParentAssociation] CHECK CONSTRAINT [FK_StudentParentAssociation_Student]

GO

/****** Object:  ForeignKey [FK_ReasonExitedType_StudentProgramAssociation]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentProgramAssociation]  WITH CHECK ADD  CONSTRAINT [FK_ReasonExitedType_StudentProgramAssociation] FOREIGN KEY([ReasonExitedTypeId])

REFERENCES [edfi].[ReasonExitedType] ([ReasonExitedTypeId])

GO

ALTER TABLE [edfi].[StudentProgramAssociation] CHECK CONSTRAINT [FK_ReasonExitedType_StudentProgramAssociation]

GO

/****** Object:  ForeignKey [FK_StudentProgramAssociation_Program]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentProgramAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StudentProgramAssociation_Program] FOREIGN KEY([EducationOrganizationId], [ProgramTypeId])

REFERENCES [edfi].[Program] ([EducationOrganizationId], [ProgramTypeId])

GO

ALTER TABLE [edfi].[StudentProgramAssociation] CHECK CONSTRAINT [FK_StudentProgramAssociation_Program]

GO

/****** Object:  ForeignKey [FK_StudentProgramAssociation_ProgramType]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentProgramAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StudentProgramAssociation_ProgramType] FOREIGN KEY([ProgramTypeId])

REFERENCES [edfi].[ProgramType] ([ProgramTypeId])

GO

ALTER TABLE [edfi].[StudentProgramAssociation] CHECK CONSTRAINT [FK_StudentProgramAssociation_ProgramType]

GO

/****** Object:  ForeignKey [FK_StudentProgramAssociation_Student]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentProgramAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StudentProgramAssociation_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[StudentProgramAssociation] CHECK CONSTRAINT [FK_StudentProgramAssociation_Student]

GO

/****** Object:  ForeignKey [FK_StudentProgramAssociationService_ServiceDescriptor]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentProgramAssociationService]  WITH CHECK ADD  CONSTRAINT [FK_StudentProgramAssociationService_ServiceDescriptor] FOREIGN KEY([ServiceDescriptorId])

REFERENCES [edfi].[ServiceDescriptor] ([ServiceDescriptorId])

GO

ALTER TABLE [edfi].[StudentProgramAssociationService] CHECK CONSTRAINT [FK_StudentProgramAssociationService_ServiceDescriptor]

GO

/****** Object:  ForeignKey [FK_StudentProgramAssociationService_StudentProgramAssociation]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentProgramAssociationService]  WITH CHECK ADD  CONSTRAINT [FK_StudentProgramAssociationService_StudentProgramAssociation] FOREIGN KEY([StudentUSI], [ProgramTypeId], [EducationOrganizationId], [BeginDate])

REFERENCES [edfi].[StudentProgramAssociation] ([StudentUSI], [ProgramTypeId], [EducationOrganizationId], [BeginDate])

GO

ALTER TABLE [edfi].[StudentProgramAssociationService] CHECK CONSTRAINT [FK_StudentProgramAssociationService_StudentProgramAssociation]

GO

/****** Object:  ForeignKey [FK_StudentProgramParticipations_ProgramType]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentProgramParticipations]  WITH CHECK ADD  CONSTRAINT [FK_StudentProgramParticipations_ProgramType] FOREIGN KEY([ProgramTypeId])

REFERENCES [edfi].[ProgramType] ([ProgramTypeId])

GO

ALTER TABLE [edfi].[StudentProgramParticipations] CHECK CONSTRAINT [FK_StudentProgramParticipations_ProgramType]

GO

/****** Object:  ForeignKey [FK_StudentProgramParticipations_Student]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentProgramParticipations]  WITH CHECK ADD  CONSTRAINT [FK_StudentProgramParticipations_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[StudentProgramParticipations] CHECK CONSTRAINT [FK_StudentProgramParticipations_Student]

GO

/****** Object:  ForeignKey [FK_RaceType_StudentRace]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentRace]  WITH CHECK ADD  CONSTRAINT [FK_RaceType_StudentRace] FOREIGN KEY([RaceTypeId])

REFERENCES [edfi].[RaceType] ([RaceTypeId])

GO

ALTER TABLE [edfi].[StudentRace] CHECK CONSTRAINT [FK_RaceType_StudentRace]

GO

/****** Object:  ForeignKey [FK_StudentRace_Student]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentRace]  WITH CHECK ADD  CONSTRAINT [FK_StudentRace_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[StudentRace] CHECK CONSTRAINT [FK_StudentRace_Student]

GO

/****** Object:  ForeignKey [FK_EntryTypeType_StudentSchoolAssociation]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentSchoolAssociation]  WITH CHECK ADD  CONSTRAINT [FK_EntryTypeType_StudentSchoolAssociation] FOREIGN KEY([EntryTypeId])

REFERENCES [edfi].[EntryType] ([EntryTypeId])

GO

ALTER TABLE [edfi].[StudentSchoolAssociation] CHECK CONSTRAINT [FK_EntryTypeType_StudentSchoolAssociation]

GO

/****** Object:  ForeignKey [FK_ExitWithdrawTypeType_StudentSchoolAssociation]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentSchoolAssociation]  WITH CHECK ADD  CONSTRAINT [FK_ExitWithdrawTypeType_StudentSchoolAssociation] FOREIGN KEY([ExitWithdrawTypeId])

REFERENCES [edfi].[ExitWithdrawType] ([ExitWithdrawTypeId])

GO

ALTER TABLE [edfi].[StudentSchoolAssociation] CHECK CONSTRAINT [FK_ExitWithdrawTypeType_StudentSchoolAssociation]

GO

/****** Object:  ForeignKey [FK_StudentSchoolAssociation_GradeLevelType]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentSchoolAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StudentSchoolAssociation_GradeLevelType] FOREIGN KEY([EntryGradeLevelTypeId])

REFERENCES [edfi].[GradeLevelType] ([GradeLevelTypeId])

GO

ALTER TABLE [edfi].[StudentSchoolAssociation] CHECK CONSTRAINT [FK_StudentSchoolAssociation_GradeLevelType]

GO

/****** Object:  ForeignKey [FK_StudentSchoolAssociation_School]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentSchoolAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StudentSchoolAssociation_School] FOREIGN KEY([SchoolId])

REFERENCES [edfi].[School] ([SchoolId])

GO

ALTER TABLE [edfi].[StudentSchoolAssociation] CHECK CONSTRAINT [FK_StudentSchoolAssociation_School]

GO

/****** Object:  ForeignKey [FK_StudentSchoolAssociation_Student]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentSchoolAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StudentSchoolAssociation_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[StudentSchoolAssociation] CHECK CONSTRAINT [FK_StudentSchoolAssociation_Student]

GO

/****** Object:  ForeignKey [FK_EducationPlansType_StudentSchoolAssociationEducationPlans]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentSchoolAssociationEducationPlans]  WITH CHECK ADD  CONSTRAINT [FK_EducationPlansType_StudentSchoolAssociationEducationPlans] FOREIGN KEY([EducationPlansTypeId])

REFERENCES [edfi].[EducationPlansType] ([EducationPlansTypeId])

GO

ALTER TABLE [edfi].[StudentSchoolAssociationEducationPlans] CHECK CONSTRAINT [FK_EducationPlansType_StudentSchoolAssociationEducationPlans]

GO

/****** Object:  ForeignKey [FK_StudentSchoolAssociationEducationPlans_StudentSchoolAssociation]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentSchoolAssociationEducationPlans]  WITH CHECK ADD  CONSTRAINT [FK_StudentSchoolAssociationEducationPlans_StudentSchoolAssociation] FOREIGN KEY([StudentUSI], [SchoolId], [EntryDate])

REFERENCES [edfi].[StudentSchoolAssociation] ([StudentUSI], [SchoolId], [EntryDate])

GO

ALTER TABLE [edfi].[StudentSchoolAssociationEducationPlans] CHECK CONSTRAINT [FK_StudentSchoolAssociationEducationPlans_StudentSchoolAssociation]

GO

/****** Object:  ForeignKey [FK_StudentSchoolAssociationGraduationPlan_GraduationPlan]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentSchoolAssociationGraduationPlan]  WITH CHECK ADD  CONSTRAINT [FK_StudentSchoolAssociationGraduationPlan_GraduationPlan] FOREIGN KEY([GraduationPlanTypeId], [SchoolId])

REFERENCES [edfi].[GraduationPlan] ([GraduationPlanTypeId], [EducationOrganizationId])

GO

ALTER TABLE [edfi].[StudentSchoolAssociationGraduationPlan] CHECK CONSTRAINT [FK_StudentSchoolAssociationGraduationPlan_GraduationPlan]

GO

/****** Object:  ForeignKey [FK_StudentSchoolAssociationGraduationPlan_StudentSchoolAssociation]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentSchoolAssociationGraduationPlan]  WITH CHECK ADD  CONSTRAINT [FK_StudentSchoolAssociationGraduationPlan_StudentSchoolAssociation] FOREIGN KEY([StudentUSI], [SchoolId], [EntryDate])

REFERENCES [edfi].[StudentSchoolAssociation] ([StudentUSI], [SchoolId], [EntryDate])

GO

ALTER TABLE [edfi].[StudentSchoolAssociationGraduationPlan] CHECK CONSTRAINT [FK_StudentSchoolAssociationGraduationPlan_StudentSchoolAssociation]

GO

/****** Object:  ForeignKey [FK_RepeatIdentifierType_StudentSectionAssociation]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentSectionAssociation]  WITH CHECK ADD  CONSTRAINT [FK_RepeatIdentifierType_StudentSectionAssociation] FOREIGN KEY([RepeatIdentifierTypeId])

REFERENCES [edfi].[RepeatIdentifierType] ([RepeatIdentifierTypeId])

GO

ALTER TABLE [edfi].[StudentSectionAssociation] CHECK CONSTRAINT [FK_RepeatIdentifierType_StudentSectionAssociation]

GO

/****** Object:  ForeignKey [FK_StudentSectionAssociation_Section]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentSectionAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StudentSectionAssociation_Section] FOREIGN KEY([SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear])

REFERENCES [edfi].[Section] ([SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear])

GO

ALTER TABLE [edfi].[StudentSectionAssociation] CHECK CONSTRAINT [FK_StudentSectionAssociation_Section]

GO

/****** Object:  ForeignKey [FK_StudentSectionAssociation_Student]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentSectionAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StudentSectionAssociation_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[StudentSectionAssociation] CHECK CONSTRAINT [FK_StudentSectionAssociation_Student]

GO

/****** Object:  ForeignKey [FK_EducationalEnvironmentType_StudentSpecialEdProgramAssociation]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentSpecialEdProgramAssociation]  WITH CHECK ADD  CONSTRAINT [FK_EducationalEnvironmentType_StudentSpecialEdProgramAssociation] FOREIGN KEY([EducationalEnvironmentTypeId])

REFERENCES [edfi].[EducationalEnvironmentType] ([EducationalEnvironmentTypeId])

GO

ALTER TABLE [edfi].[StudentSpecialEdProgramAssociation] CHECK CONSTRAINT [FK_EducationalEnvironmentType_StudentSpecialEdProgramAssociation]

GO

/****** Object:  ForeignKey [FK_IdeaEligibilityType_StudentSpecialEdProgramAssociation]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentSpecialEdProgramAssociation]  WITH CHECK ADD  CONSTRAINT [FK_IdeaEligibilityType_StudentSpecialEdProgramAssociation] FOREIGN KEY([IdeaEligibilityTypeId])

REFERENCES [edfi].[IdeaEligibilityType] ([IdeaEligibilityTypeId])

GO

ALTER TABLE [edfi].[StudentSpecialEdProgramAssociation] CHECK CONSTRAINT [FK_IdeaEligibilityType_StudentSpecialEdProgramAssociation]

GO

/****** Object:  ForeignKey [FK_StudentSpecialEdProgramAssociation_StudentProgramAssociation]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentSpecialEdProgramAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StudentSpecialEdProgramAssociation_StudentProgramAssociation] FOREIGN KEY([StudentUSI], [ProgramTypeId], [EducationOrganizationId], [BeginDate])

REFERENCES [edfi].[StudentProgramAssociation] ([StudentUSI], [ProgramTypeId], [EducationOrganizationId], [BeginDate])

GO

ALTER TABLE [edfi].[StudentSpecialEdProgramAssociation] CHECK CONSTRAINT [FK_StudentSpecialEdProgramAssociation_StudentProgramAssociation]

GO

/****** Object:  ForeignKey [FK_StudentTelephone_Student]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentTelephone]  WITH CHECK ADD  CONSTRAINT [FK_StudentTelephone_Student] FOREIGN KEY([StudentUSI])

REFERENCES [edfi].[Student] ([StudentUSI])

GO

ALTER TABLE [edfi].[StudentTelephone] CHECK CONSTRAINT [FK_StudentTelephone_Student]

GO

/****** Object:  ForeignKey [FK_StudentTelephone_TelephoneNumberType]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentTelephone]  WITH CHECK ADD  CONSTRAINT [FK_StudentTelephone_TelephoneNumberType] FOREIGN KEY([TelephoneNumberTypeId])

REFERENCES [edfi].[TelephoneNumberType] ([TelephoneNumberTypeId])

GO

ALTER TABLE [edfi].[StudentTelephone] CHECK CONSTRAINT [FK_StudentTelephone_TelephoneNumberType]

GO

/****** Object:  ForeignKey [FK_StudentTitleIPartAProgramAssociation_StudentProgramAssociation]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentTitleIPartAProgramAssociation]  WITH CHECK ADD  CONSTRAINT [FK_StudentTitleIPartAProgramAssociation_StudentProgramAssociation] FOREIGN KEY([StudentUSI], [ProgramTypeId], [EducationOrganizationId], [BeginDate])

REFERENCES [edfi].[StudentProgramAssociation] ([StudentUSI], [ProgramTypeId], [EducationOrganizationId], [BeginDate])

GO

ALTER TABLE [edfi].[StudentTitleIPartAProgramAssociation] CHECK CONSTRAINT [FK_StudentTitleIPartAProgramAssociation_StudentProgramAssociation]

GO

/****** Object:  ForeignKey [FK_TitleIPartAParticipantType_StudentTitleIPartAProgramAssociation]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[StudentTitleIPartAProgramAssociation]  WITH CHECK ADD  CONSTRAINT [FK_TitleIPartAParticipantType_StudentTitleIPartAProgramAssociation] FOREIGN KEY([TitleIPartAParticipantTypeId])

REFERENCES [edfi].[TitleIPartAParticipantType] ([TitleIPartAParticipantTypeId])

GO

ALTER TABLE [edfi].[StudentTitleIPartAProgramAssociation] CHECK CONSTRAINT [FK_TitleIPartAParticipantType_StudentTitleIPartAProgramAssociation]

GO

/****** Object:  ForeignKey [FK_ProgramAssignmentType_TeacherSchoolAssociation]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[TeacherSchoolAssociation]  WITH CHECK ADD  CONSTRAINT [FK_ProgramAssignmentType_TeacherSchoolAssociation] FOREIGN KEY([ProgramAssignmentTypeId])

REFERENCES [edfi].[ProgramAssignmentType] ([ProgramAssignmentTypeId])

GO

ALTER TABLE [edfi].[TeacherSchoolAssociation] CHECK CONSTRAINT [FK_ProgramAssignmentType_TeacherSchoolAssociation]

GO

/****** Object:  ForeignKey [FK_TeacherSchoolAssociation_School]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[TeacherSchoolAssociation]  WITH CHECK ADD  CONSTRAINT [FK_TeacherSchoolAssociation_School] FOREIGN KEY([SchoolId])

REFERENCES [edfi].[School] ([SchoolId])

GO

ALTER TABLE [edfi].[TeacherSchoolAssociation] CHECK CONSTRAINT [FK_TeacherSchoolAssociation_School]

GO

/****** Object:  ForeignKey [FK_TeacherSchoolAssociation_Staff]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[TeacherSchoolAssociation]  WITH CHECK ADD  CONSTRAINT [FK_TeacherSchoolAssociation_Staff] FOREIGN KEY([StaffUSI])

REFERENCES [edfi].[Staff] ([StaffUSI])

GO

ALTER TABLE [edfi].[TeacherSchoolAssociation] CHECK CONSTRAINT [FK_TeacherSchoolAssociation_Staff]

GO

/****** Object:  ForeignKey [FK_TeacherSchoolAssociationAcademicSubjects_AcademicSubjectType]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[TeacherSchoolAssociationAcademicSubjects]  WITH CHECK ADD  CONSTRAINT [FK_TeacherSchoolAssociationAcademicSubjects_AcademicSubjectType] FOREIGN KEY([AcademicSubjectTypeId])

REFERENCES [edfi].[AcademicSubjectType] ([AcademicSubjectTypeId])

GO

ALTER TABLE [edfi].[TeacherSchoolAssociationAcademicSubjects] CHECK CONSTRAINT [FK_TeacherSchoolAssociationAcademicSubjects_AcademicSubjectType]

GO

/****** Object:  ForeignKey [FK_TeacherSchoolAssociationAcademicSubjects_TeacherSchoolAssociation]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[TeacherSchoolAssociationAcademicSubjects]  WITH CHECK ADD  CONSTRAINT [FK_TeacherSchoolAssociationAcademicSubjects_TeacherSchoolAssociation] FOREIGN KEY([StaffUSI], [ProgramAssignmentTypeId], [SchoolId])

REFERENCES [edfi].[TeacherSchoolAssociation] ([StaffUSI], [ProgramAssignmentTypeId], [SchoolId])

GO

ALTER TABLE [edfi].[TeacherSchoolAssociationAcademicSubjects] CHECK CONSTRAINT [FK_TeacherSchoolAssociationAcademicSubjects_TeacherSchoolAssociation]

GO

/****** Object:  ForeignKey [FK_TeacherSchoolAssociationInstructionalGradeLevels_GradeLevelType]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[TeacherSchoolAssociationInstructionalGradeLevels]  WITH CHECK ADD  CONSTRAINT [FK_TeacherSchoolAssociationInstructionalGradeLevels_GradeLevelType] FOREIGN KEY([InstructionalGradeLevelTypeId])

REFERENCES [edfi].[GradeLevelType] ([GradeLevelTypeId])

GO

ALTER TABLE [edfi].[TeacherSchoolAssociationInstructionalGradeLevels] CHECK CONSTRAINT [FK_TeacherSchoolAssociationInstructionalGradeLevels_GradeLevelType]

GO

/****** Object:  ForeignKey [FK_TeacherSchoolAssociationInstructionalGradeLevels_TeacherSchoolAssociation]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[TeacherSchoolAssociationInstructionalGradeLevels]  WITH CHECK ADD  CONSTRAINT [FK_TeacherSchoolAssociationInstructionalGradeLevels_TeacherSchoolAssociation] FOREIGN KEY([StaffUSI], [ProgramAssignmentTypeId], [SchoolId])

REFERENCES [edfi].[TeacherSchoolAssociation] ([StaffUSI], [ProgramAssignmentTypeId], [SchoolId])

GO

ALTER TABLE [edfi].[TeacherSchoolAssociationInstructionalGradeLevels] CHECK CONSTRAINT [FK_TeacherSchoolAssociationInstructionalGradeLevels_TeacherSchoolAssociation]

GO

/****** Object:  ForeignKey [FK_ClassroomPositionType_TeacherSectionAssociation]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[TeacherSectionAssociation]  WITH CHECK ADD  CONSTRAINT [FK_ClassroomPositionType_TeacherSectionAssociation] FOREIGN KEY([ClassroomPositionTypeId])

REFERENCES [edfi].[ClassroomPositionType] ([ClassroomPositionTypeId])

GO

ALTER TABLE [edfi].[TeacherSectionAssociation] CHECK CONSTRAINT [FK_ClassroomPositionType_TeacherSectionAssociation]

GO

/****** Object:  ForeignKey [FK_TeacherSectionAssociation_Section]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[TeacherSectionAssociation]  WITH CHECK ADD  CONSTRAINT [FK_TeacherSectionAssociation_Section] FOREIGN KEY([SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear])

REFERENCES [edfi].[Section] ([SchoolId], [ClassPeriodName], [ClassroomIdentificationCode], [LocalCourseCode], [TermTypeId], [SchoolYear])

GO

ALTER TABLE [edfi].[TeacherSectionAssociation] CHECK CONSTRAINT [FK_TeacherSectionAssociation_Section]

GO

/****** Object:  ForeignKey [FK_TeacherSectionAssociation_Staff]    Script Date: 10/20/2011 10:44:07 ******/

ALTER TABLE [edfi].[TeacherSectionAssociation]  WITH CHECK ADD  CONSTRAINT [FK_TeacherSectionAssociation_Staff] FOREIGN KEY([StaffUSI])

REFERENCES [edfi].[Staff] ([StaffUSI])

GO

ALTER TABLE [edfi].[TeacherSectionAssociation] CHECK CONSTRAINT [FK_TeacherSectionAssociation_Staff]

GO

