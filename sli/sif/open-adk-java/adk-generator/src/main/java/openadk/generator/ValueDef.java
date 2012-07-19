//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.generator;

public class ValueDef extends AbstractDef
{
	protected String fValue;

    public ValueDef()
	{
		super();
    }

	public ValueDef( String tag, String value, String desc ) {
		setName(tag);
		fValue=value;
		setDesc(desc);
	}

	public String getValue() {
		return fValue;
	}
}
