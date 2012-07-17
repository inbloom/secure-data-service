//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.log;

////////////////////////////////////////////////////////////////////////////////
//
//  Copyright (c)2001-2007 Edustructures LLC
//  All rights reserved.
//
//  This software is the confidential and proprietary information of
//  Edustructures LLC ("Confidential Information").  You shall not disclose
//  such Confidential Information and shall use it only in accordance with the
//  terms of the license agreement you entered into with Edustructures.
//

/**
 * 	SIF_LogEntry error category and code constants as defined by the SIF 1.5 Specification.<p>
 */
public class LogEntryCodes
{
	//	Categories
	public static final int CATEGORY_SUCCESS = 1;
	public static final int CATEGORY_DATA_ISSUES_WITH_SUCCESS = 2;
	public static final int CATEGORY_DATA_ISSUES_WITH_FAILURE = 3;
	public static final int CATEGORY_ERROR = 4;
	
	//	Success Category Codes
	public static final int CODE_SUCCESS = 1;
	
	//	Data Issues with Success Result Category Codes
	public static final int CODE_DATA_CHANGED_SUCCESS = 1;
	public static final int CODE_DATA_ADDED_SUCCESS = 2;
	
	//	Data Issues with Failure Result Category Codes
	public static final int CODE_INSUFFICIENT_INFO_FAILURE = 1;
	public static final int CODE_BUSINESS_RULE_FAILURE = 2;
	public static final int CODE_INCOMPLETE_DATA_FAILURE = 3;
	
	//	Agent Error Conditions Category Codes
	public static final int CODE_AGENT_FAILURE = 1;
	
	//	ZIS Error Conditions Category Codes
	public static final int CODE_ZIS_FAILURE = 1;
	public static final int CODE_MAXBUFFERSIZE_FAILURE = 2;
	public static final int CODE_INSECURE_CHANNEL_FAILURE = 3;
	
	
	
	
}
