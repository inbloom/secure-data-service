//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 *  Describes the acceptable values for an enumerated SIF attribute or element
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public class SIFEnum extends SIFString
{
	private static final long serialVersionUID = Element.CURRENT_SERIALIZE_VERSION;

	/**
     * Creats an instance of an Enum class with the specified value
     * @param val The value to assign to this enum
     */
    public SIFEnum( String val ) {
    	super(val);
    }
}
