//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.generator;

import java.util.*;

public class FieldDef extends AbstractDef implements Comparable
{
	public static final int
		FLAG_DO_NOT_ENCODE = 0x00001000,
		FLAG_ELEMENT = 0x10000000,
		FLAG_ATTRIBUTE = 0x20000000,
		FLAG_COMPLEX = 0x40000000,
		FLAG_NOT_A_KEY = 0x80000000,
		FLAG_TEXT_VALUE = 0x01000000,
		FLAG_COLLAPSED = 0x02000000;
	
	
	protected FieldType fFieldType;
	protected String fSuperclass;
	protected int fSequence;
	protected ObjectDef fParent;
	protected String fRenderAs;
	protected String fDtdSymbol;

	/**
	 *  If this field is known by more than one tag, or has different sequence
	 *  numbers for different versions of SIF, an Alias is added to the table
	 *  by the DB.mergeInto method. Alias entries are keyed by SIF version string.
	 */
	protected SortedSet<Alias> fAliases;


	private String fElementDefConst;


	private String fSurrogate;

    public FieldDef( ObjectDef parent, String name, String classType, int sequence, int nodeTypeFlag )
    throws ParseException
	{
    	this( parent, name,FieldType.getFieldType( classType ), sequence, nodeTypeFlag );
    }
    
    public FieldDef( ObjectDef parent, String name, FieldType fieldType, int sequence, int nodeTypeFlag )
    throws ParseException
	{
		fParent=parent;
		fSequence=sequence;
		fName=name;
		fFieldType= fieldType;
		fFlags = nodeTypeFlag;

		if( fFieldType.isComplex() ) {
			if( fFlags == FieldDef.FLAG_ATTRIBUTE ){
				throw new ParseException(
						"Cannot create an attribute as a ComplexType: " + 
						parent.getName() + "." + name + " {" + fieldType.getClassType() + "}");
			}
			fFlags |= FLAG_COMPLEX;
			if( fieldType.getClassType().equals(name) )
				fSuperclass =  fieldType.getClassType();
		} 
		fDtdSymbol = fParent.getDTDSymbol()+"_"+(getName().toUpperCase());
		fElementDefConst = fParent.getPackageQualifiedDTDSymbol() +"_"+(getName().toUpperCase()); 
    }
    
	/**
	 * Allows the def to validate itself after all values are set
	 * @return
	 */
	@Override
	public void validate() throws ParseException
	{
		if( isRepeatable() && !fFieldType.isComplex() ){
			throw new ParseException( "Cannot create a repeatable element that is not a complex type: " +
					fParent.getName() + "." + fName );
			
		}
		
	}
	
	/**
	 * Returns true if this field is repeatable. If the element switches repeatability 
	 * between SIF Version 1.5r1 and 2.0, the repeatability value from 2.0 is returned,
	 * since all ADK apis are derived from their 2.0 version
	 * @return True if this element is repeatable in all versions or in version SIF 2.0 
	 * or greater
	 */
	public boolean isRepeatable()
	{
		int flags = fFlags;
		if( fAliases != null ){
			// Search for the flags from SIF 2.0
			Alias latestAlias = fAliases.last();
			if( latestAlias.getVersion().getMajor() == 2 ){
					flags = latestAlias.getFlags();
			}
		}
		return ( flags & AbstractDef.FLAG_REPEATABLE ) > 0;
	}
	
	
	
	/**
	 * Prints a textual description of the specified flags value for debugging purposes
	 * @param flags
	 * @return
	 */
	public static String flagsToString( int flags )
	{
		StringBuilder sb = new StringBuilder();
		if( ( flags & FLAG_ELEMENT ) > 0 ){
			sb.append( "ELEMENT | " );
		}
		
		if( ( flags & FLAG_ATTRIBUTE ) > 0 ){
			sb.append( "ATTRIBUTE |" );
		}
		
		if( ( flags & FLAG_COMPLEX ) > 0 ){
			sb.append( "COMPLEX |" );
		}
		
		if( ( flags & FLAG_NOT_A_KEY ) > 0 ){
			sb.append( "NOT_A_KEY |" );
		}
		
		if( ( flags & FLAG_TEXT_VALUE ) > 0 ){
			sb.append( "TEXT_VALUE |" );
		}
		
		if( ( flags & FLAG_COLLAPSED ) > 0 ){
			sb.append( "COLLAPSED |" );
		}
		
		if( ( flags & FLAG_OPTIONAL ) > 0 ){
			sb.append( "OPTIONAL |" );
		}
		
		if( ( flags & FLAG_MANDATORY ) > 0 ){
			sb.append( "MANDATORY |" );
		}
		
		if( ( flags & FLAG_CONDITIONAL ) > 0 ){
			sb.append( "CONDITIONAL |" );
		}
		
		if( ( flags & FLAG_REPEATABLE ) > 0 ){
			sb.append( "REPEATABLE |" );
		}
		
		return sb.toString();
			
	}
	
	
	
    
       
	/**
	 * Adds a new Alias to this field
	 * @param version
	 * @param tag
	 * @param surrogate
	 * @param sequence
	 * @param flags
	 * @throws MergeException
	 */
	private void addAlias( SIFVersion version, String tag, String surrogate, int sequence, int flags )
		throws MergeException
	{
		
		if( version.compareTo( this.getEarliestVersion() ) < 1 ){
			throw new MergeException( "Cannot add an alias. Version precedes earliest version" );
		}
		
		if( fAliases == null ){
			fAliases = new TreeSet<Alias>();
		} else if( version.compareTo( fAliases.last().getVersion() ) < 1 ) {
			// Ensure that the new version being added as an alias is greater than
			// the latest alias that was defined
			throw new MergeException( getTag() + " alias already defined for " + version );
		}

		Alias a = new Alias( version, tag, surrogate, sequence );
		a.setFlags( flags );
		fAliases.add( a );
	}
	

	/**
	 * Gets a list of all Aliases defined for this field, sorted by SIF Version
	 * @return a list of all Aliases defined for this field, sorted by SIF Version
	 */
	public SortedSet<Alias> getAliases() {
		return fAliases;
	}
	
	/**
	 * Returns an array of Aliases that have unique tag names
	 * @return an array of Aliases that have unique tag names
	 */
	public Alias[] getUniqueTagAliases(){
		Alias last = new Alias( getEarliestVersion(), getTag(), getSurrogate(), getSequence() );
		if( fAliases == null ){
			return new Alias[] { last };
		}
		ArrayList<Alias> allAliases = new ArrayList<Alias>();
		allAliases.add( last );
		for( Alias alias : fAliases ){
			if( !last.getTag().equals( alias.getTag() ) ){
				last = alias;
				allAliases.add( alias );
			}
		}
		
		Alias[] returnValue = new Alias[ allAliases.size()];
		allAliases.toArray( returnValue );
		return returnValue;
	}
	
	
	public int compareTo( Object o )
	{
		int cmp = ((FieldDef)o).fSequence;
		if( fSequence < cmp )
			return -1;
		if( fSequence > cmp )
			return 1;

		return 0;
	}

	/**
	 *  Gets the name used to represent this object in the DTD class.
	 *
	 *  A static String is defined in the DTD class having the name "parent_this",
	 *  where "parent" is the value returned by the parent ObjectDef's getDTDSymbol
	 *  and "this" is the name of this field in uppercase (e.g. "STUDENTPERSONAL_REFID").
	 *  The value of that static will be the string returned by getName.
	 */
	public String getDTDSymbol()
	{
		return fDtdSymbol;
	}
	
	
	/**
	 * Gets the ElementDef const to use for this field.
	 * @param generator The generator to use to generate the ElementDef const name
	 * if it is an element value
	 * @return
	 */
	public String getElementDefConst( CodeGenerator generator )
	{
		if( fElementDefConst == null ){
			return null;
		}
		else {
			return fElementDefConst;
		}
	}
	
	public void setElementDefConst(String value)
	{
		fElementDefConst = value;
	}
	
	public void setDTDSymbol( String symbol )
	{
		fDtdSymbol = symbol;
	}

	@Override
	public String getName()
	{
		return fName;
	}

	public int getSequence()
	{
		if( fSeqOverride != -1 )
			return fSeqOverride;
			
		return fSequence;
	}


	public void setEnum( String enumName )
	throws ParseException
	{
		fFieldType = FieldType.toEnumType( fFieldType, enumName );
	}

	public String getSuperclass()
	{
		if( fSuperclass == null )
			return fName;
		return fSuperclass;
	}


	public FieldType getFieldType()
	{
		return fFieldType;
	}

	public void setRenderAs( String tag ) {
		fRenderAs = tag;
	}

	public String getRenderAs() {
		return fRenderAs;
	}
	
	public String getElementDefExpression()
	{
		if( fSurrogate != null )
		{
			return "~" + fSurrogate +  ( fRenderAs == null ? "" : fRenderAs );
		}
		return fRenderAs;
	}

	public String getTag() {
		if( fRenderAs == null )
			return fName;
		return fRenderAs;
	}

	public boolean isComplex()
	{
		return ( fFlags & FLAG_COMPLEX ) != 0;
	}

	/**
	 *  For complex fields, returns the names of the fields that serve as the
	 *  object's key. By default this method returns all attributes marked with
	 *  an "R" flag. For nearly all SIF objects this method returns a single
	 *  key named "RefId".
	 */
	public FieldDef[] getKey()
	{
		Vector<FieldDef> v = new Vector<FieldDef>();
		FieldDef[] attrs = fParent.getAttributes();
		for( int i = 0; i < attrs.length; i++ ) {
			if( (( attrs[i].getFlags() & FLAG_MANDATORY ) != 0 ) &&
				(( attrs[i].getFlags() & FLAG_NOT_A_KEY ) == 0 )
			  )
				v.addElement(attrs[i]);
		}

		FieldDef[] arr = new FieldDef[v.size()];
		v.copyInto(arr);
		return arr;
	}

	public void setSurrogate(String attr) {
		if( attr != null ){
			System.out.print( "Surrogate: " + attr );
		}
		fSurrogate = attr;
		
	}
	
	public String getSurrogate()
	{
		return fSurrogate;
	}

	public ObjectDef getParent() {
		return fParent;
	}

	/**
	 * Merges the information in the specified field into this field's data set. 
	 * @param mergeSourceField The field to merge with this one. This field must come from a DB that represents a newer version of SIF
	 * @param mergeSourceVersion The version of SIF that this merge field represents
	 * @param objectKey The name of the object (used only for logging purposes)
	 * @throws MergeException If the version represents a version that already been merged
	 */
	public void mergeFrom(FieldDef mergeSourceField, SIFVersion mergeSourceVersion, String objectKey )
		throws MergeException
	{
		
		Alias currentAlias;
		if( fAliases != null ){
			// We have already defined aliases for this field. Therefore, the latest
			// Alias in fAliases is what we should compare to
			currentAlias = fAliases.last();
			
		} else {
			// No aliases have yet been defined for this field. Create a new, fake one
			// that represents this field's originial values
			currentAlias = new Alias( getEarliestVersion(), this.getTag(), this.getSurrogate(), this.getSequence() );
			currentAlias.setFlags(  fFlags );
		}
		
		if( mergeSourceVersion.compareTo( currentAlias.getVersion() ) < 1 ){
			throw new MergeException( "Cannot merge field. Version precedes the latest version already marged. Fields must be merged in order." );
		}
		
		boolean methodDiff = false;
		boolean attrDiff = false;
		
		//  Check the field for differences...
		if( ( mergeSourceField.fFieldType == null && this.fFieldType != null ) ||
			!( mergeSourceField.fFieldType.equals( this.fFieldType ) ) )
		{
			System.out.println( "    (~) Field \""+objectKey+"::"+ this.getName() +"\" has different ClassType; adding version-specific field. 1:" + 
					mergeSourceField.fFieldType + " - 2:" + this.fFieldType );
			methodDiff = true;
		}

		if( !currentAlias.getTag().equals( mergeSourceField.getTag() ) )
		{
			System.out.println( "    (~) Field \""+objectKey+"::"+ this.getName() +"\" has different tag; adding alias");
			System.out.println( "        "+currentAlias.getVersion()+" -> " + currentAlias.getTag()  );
			System.out.println( "        "+mergeSourceVersion+" -> " + mergeSourceField.getTag());
			attrDiff = true;
		}

		if( currentAlias.getSequence() != mergeSourceField.getSequence() )
		{
			System.out.println( "    (~) Field \""+objectKey+"::"+ this.getName() +"\" has different sequence number; adding alias");
			System.out.println( "        "+currentAlias.getVersion()+" -> " +currentAlias.getSequence() );
			System.out.println( "        "+mergeSourceVersion+" -> " + mergeSourceField.getSequence() );
			attrDiff = true;
		}
		
		if( currentAlias.getFlags() != mergeSourceField.getFlags() )
		{
			System.out.println( "    (~) Field \""+objectKey+"::"+ this.getName() +"\" has different flags adding alias");
			System.out.println( "        "+currentAlias.getVersion()+" -> " + FieldDef.flagsToString( currentAlias.getFlags() ) );
			System.out.println( "        "+mergeSourceVersion+" -> " + FieldDef.flagsToString( mergeSourceField.getFlags() ) );
			attrDiff = true;
		}
		
		String existingSurrogate = currentAlias.getSurrogate();
		String srcSurrogate = mergeSourceField.getSurrogate();
		
		if( !DB.areEqual( existingSurrogate, srcSurrogate ) )
		{
			System.out.println( "    (~) Field \""+objectKey+"::"+this.getName()+"\" has different surrogate adding alias");
			System.out.println( "        "+currentAlias.getVersion()+" -> " +currentAlias.getSurrogate() );
			System.out.println( "        "+mergeSourceVersion+" -> " + srcSurrogate);
			attrDiff = true;
		
			
			// Validation of metadata. Surrogates should only appear in 1.1 and 1.5r1
			if( existingSurrogate != null && srcSurrogate != null ){
				// Warning
				throw new RuntimeException( "WARNING: TWO DIFFERENT SURROGATE VALUES FOUND, POSSIBLE METADATA BUG!" ) ;
			}
			
			if( existingSurrogate == null ){
				// Warning
				throw new RuntimeException( "WARNING: Switching from null to Surrogate, POSSIBLE METADATA BUG!" );
			}
			
			if( mergeSourceVersion.equals( SIFVersion.SIF15r1 ) ){
				//	Warning
				throw new RuntimeException( "WARNING: Surrogate was added or changed in 1.5r1, POSSIBLE METADATA BUG!" );
			}
		
		}
		
		if( methodDiff )
		{
			//  If there were any differences that would result in new
			//  methods to the implementation class, create a new FieldDef
			System.out.println("*** DIFF ***");
			throw new MergeException( "Method merge not yet supported" );
		}
		else
		if( attrDiff )
		{
			//  If there were any differences in tag name or sequence number, add an alias to the FieldDef
		    this.addAlias( mergeSourceVersion, mergeSourceField.getTag(), mergeSourceField.getSurrogate(), mergeSourceField.getSequence(), mergeSourceField.getFlags() );
		}
		this.setLatestVersion( mergeSourceVersion );
		
		// Update the field description to the latest if this version has documentation
		if( mergeSourceField.fDesc != null && mergeSourceField.fDesc.length() > 0 ){
			this.setDesc( mergeSourceField.fDesc );
		}
		
		
	}
}
