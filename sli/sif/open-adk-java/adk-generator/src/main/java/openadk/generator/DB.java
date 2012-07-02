//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.generator;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

/**
 *  Metadata database for a version of the SIF Specification.
 */
public class DB
{
	protected SIFVersion fVersion;
	protected String fNamespace;
	protected Hashtable<String, EnumDef> fEnums = new Hashtable<String, EnumDef>();
	private Hashtable<String, ObjectDef> fObjects = new Hashtable<String, ObjectDef>();
	private Hashtable<String, DefinitionFile> fDefinitionFiles = new Hashtable<String, DefinitionFile>();
	protected ObjectDef fSIFTimeDef;

    public DB( SIFVersion version, String namespace )
	{
		fVersion = version;
		fNamespace = namespace;
		try
		{
		// Create an ObjectDef that will be used for any instances of SIFTime
		fSIFTimeDef = new ObjectDef( -99999, "SIFTime", "common", SIFVersion.SIF11 );
		FieldDef zone = fSIFTimeDef.defineAttr( "Zone", "String" );
		zone.setFlags( "R" );
		zone.setDesc( "The Zone attribute describes the time zone as an offset from the zero meridian (e.g <code>\"UTC-08:00\"</code> )." );
		}
		catch( ParseException parseEx ){
			System.out.println( parseEx );
		}

    }

	/**
	 *  Merge the definitions of this DB into another DB
	 */
	public void mergeInto( DB target )
		throws MergeException
	{
		System.out.println("Merging metadata for \"" + fVersion.toString() + "\" into \"" + target.getVersion().toString() + "\"..." );
		mergeEnums( target );
		mergeObjectDefs( target );
	}

	/**
	 * Merge all of the object defs from the target DB into this DB
	 * @param mergeTargetDB
	 * @throws MergeException
	 */
	private void mergeObjectDefs(DB mergeTargetDB) throws MergeException {
		
		System.out.println("- Processing object definitions...");
		for( Enumeration e = fObjects.keys(); e.hasMoreElements(); )
		{
			String key = (String)e.nextElement();
			ObjectDef mergeSourceObjectDef = (ObjectDef)fObjects.get( key );
			ObjectDef mergeTargetObjectDef = (ObjectDef)mergeTargetDB.fObjects.get(key);
			
//			if( key.equals( "MediumOfInstruction") ){
//				System.out.println( "Break Time");
//			}
			
			
			
			if( mergeTargetObjectDef == null )
			{
				//  Add the missing ObjectDef to the target
				System.out.println( "  (+) \""+key+"\" (" + mergeSourceObjectDef.getAllFields().length + " fields) not found in target; adding");
				mergeTargetDB.fObjects.put( key, mergeSourceObjectDef );
			}
			else
			{
				System.out.println("Object "+mergeTargetObjectDef.getName()+" now has SIFVersion range of "+mergeTargetObjectDef.getEarliestVersion()+" - "+fVersion);
				mergeTargetObjectDef.setLatestVersion( fVersion );
				
				//  Do some sanity checking
				if( !mergeTargetObjectDef.fPackage.equals( mergeSourceObjectDef.fPackage ) )
					throw new MergeException("Target and source have different package values (target=\""+mergeTargetObjectDef.getName()+"\", package=\""+mergeTargetObjectDef.fPackage+"\", source=\""+mergeSourceObjectDef.getName()+"\", package=\""+mergeSourceObjectDef.fPackage+"\"");
				if( !mergeTargetObjectDef.fSuperclass.equals( mergeSourceObjectDef.fSuperclass ) )
					throw new MergeException("Target and source have different superclass values (target=\""+mergeTargetObjectDef.getName()+"\", superclass=\""+mergeTargetObjectDef.fSuperclass+"\", source=\""+mergeSourceObjectDef.getName()+"\", superclass=\""+mergeSourceObjectDef.fSuperclass+"\"");

				//  Append this fExtrasFile to the target's if necessary
				if( mergeSourceObjectDef.fExtrasFile != null ) {
					System.out.println( "  (+) \""+key+"\" has an Extras File; adding");
					if( mergeTargetObjectDef.fExtrasFile == null ){
						mergeTargetObjectDef.fExtrasFile = mergeSourceObjectDef.fExtrasFile;
					} else {
						throw new IllegalStateException( "ADKGen does not yet support multiple Extras files.");
						//existingObjectDef.fExtrasFile = existingObjectDef.fExtrasFile + ";" + thisObjectDef.fExtrasFile;
					}
				}

				//  Determine if the object's key fields (required elements and
				//  attributes) differ
				StringBuffer keyCmp1s = new StringBuffer();
				FieldDef[] keyCmp1 = mergeSourceObjectDef.getKey( null );
				for( int n = 0; n < keyCmp1.length; n++ ) {
					keyCmp1s.append( keyCmp1[n].getName() == null ? "null" : keyCmp1[n].getName() );
					if( n != keyCmp1.length-1 )
						keyCmp1s.append('+');
				}

				StringBuffer keyCmp2s = new StringBuffer();
				FieldDef[] keyCmp2 = mergeTargetObjectDef.getKey( null );
				for( int n = 0; n < keyCmp2.length; n++ ) {
					keyCmp2s.append( keyCmp2[n].getName() == null ? "null" : keyCmp2[n].getName() );
					if( n != keyCmp2.length-1 )
						keyCmp2s.append('+');
				}

				if( !keyCmp1s.toString().equals(keyCmp2s.toString()) )
					throw new MergeException("\""+key+"\" target and source have different key signature; merge not yet supported by adkgen." +
					" Target=\"" + keyCmp2s.toString() + "\"; Source=\"" + keyCmp1s.toString() + "\"" );

				mergeTargetObjectDef.setLatestVersion( getVersion() );
				
				// Update the description if there is an updated description
				if( mergeSourceObjectDef.fDesc != null && mergeSourceObjectDef.fDesc.length() > 0 ){
					mergeTargetObjectDef.setDesc( mergeSourceObjectDef.fDesc );
				}
				
				
				if( !(mergeTargetObjectDef.getTag().equals( mergeSourceObjectDef.getTag() ))){
					System.out.println( " (~) Object has different tag; adding alias");
					System.out.println( "        "+mergeTargetDB.getVersion()+" -> " + mergeTargetObjectDef.getTag() );
					System.out.println( "        "+this.getVersion()+" -> " + mergeSourceObjectDef.getTag() );
					mergeTargetObjectDef.addAlias( fVersion, mergeSourceObjectDef.getTag() );
				}
				
				mergeFieldDefs( mergeSourceObjectDef, mergeTargetObjectDef, this.getVersion(), key );
			}
		}
	}

	/**
	 * Merges a set of Field Defs from a new version of SIF into a target DB that represents an older version of SIF
	 * 
	 * @param mergeSource An Object Def from a more recent version of SIF
	 * @param mergeTarget An object Def from a DB object representing a prior version of SIF
	 * @param objectKey
	 * @param target
	 * @throws MergeException
	 */
	private void mergeFieldDefs(ObjectDef mergeSource, ObjectDef mergeTarget, SIFVersion mergeVersion, String objectKey) throws MergeException {
		
		//  Determine if any of the object's fields differ in their definition
		for( String fieldKey : mergeSource.fFields.keySet() )
		{
			FieldDef mergeSourceField = (FieldDef)mergeSource.fFields.get( fieldKey );
			FieldDef mergeTargetField = (FieldDef)mergeTarget.fFields.get( fieldKey );

			if( mergeTargetField == null )
			{
				System.out.println( "  (+) Field \""+objectKey+"::"+fieldKey+"\" not found in target; adding");
				mergeTarget.fFields.put( fieldKey, mergeSourceField );
			}
			else
			{
				System.out.println("  Field "+objectKey+"::"+fieldKey+" now has SIFVersion range of " + mergeTargetField.getLatestVersion() + " - " + mergeSourceField.getEarliestVersion() );				
				mergeTargetField.mergeFrom( mergeSourceField, mergeVersion, objectKey );
			}
		}
	}

	private void mergeEnums(DB mergeTargetDB) throws MergeException {
		System.out.println("- Processing enumerated types...");
		for( Enumeration e = fEnums.keys(); e.hasMoreElements(); )
		{
			String key = (String)e.nextElement();
			EnumDef val = fEnums.get(key);

			EnumDef targetEnum = (EnumDef)mergeTargetDB.fEnums.get(key);
			if( targetEnum == null )
			{
				//  Add the missing EnumDef to the target
				System.out.println( "  (+) \""+key+"\" not found in target; adding");
				mergeTargetDB.fEnums.put( key, val );
			}
			else
			{
				//  Visit all values in the target's enumeration and add values
				//  for any that are new in this version of SIF
				String key2 = null;
				Hashtable<String,ValueDef> enums = val.fValues;
				for(  Enumeration k = enums.keys(); k.hasMoreElements(); ) {
					key2 = (String)k.nextElement();
					if( targetEnum.fValues.containsKey(key2) ) {
						ValueDef enumValue = enums.get( key2 );
						ValueDef existing = targetEnum.fValues.get( key2 );
						if( !(existing.getValue().equals( enumValue.getValue() ) ) ){
							throw new MergeException( 
									"Cannot add enum value {" + key2 + "} to " + 
									val.getName() + ". Value {" + enumValue.getValue() + "} does not match value {" + existing.getValue() + "}" );
						}
						
						if( enumValue.fDesc != null && enumValue.fDesc.length() > 0 ){
							// Update the description for this enum value
							existing.setDesc( enumValue.fDesc );
						}
					} else {
						System.out.println( "  (~) \""+key+"::"+key2+"\" not found in target; adding");
						targetEnum.fValues.put( key2, enums.get(key2) );
					}
				}
			}
		}

		//  Update the target's SIFVersion range
		for( Enumeration e = mergeTargetDB.fEnums.keys(); e.hasMoreElements(); ) {
			String key = (String)e.nextElement();
			EnumDef enumDef = (EnumDef)mergeTargetDB.fEnums.get(key);
			enumDef.setLatestVersion( fVersion );
			System.out.println("Enum "+enumDef.getName()+" now has SIFVersion range of "+enumDef.getEarliestVersion()+".."+enumDef.getLatestVersion());
		}

	
	}
	
	public static boolean areEqual( String a, String b ){
		if( a == null ){
			return b == null;
		}
		return a.equals( b );
	}
	
	public void defineDefinitionFile( DefinitionFile def )
	{
		fDefinitionFiles.put( def.getLocalPackage(), def );
	}
	
	public DefinitionFile getDefinitionFile( String packageName )
	{
		return fDefinitionFiles.get( packageName );
	}
	
	public Set<String> getDefinitionFileKeysSet()
	{
		return fDefinitionFiles.keySet();
	}

	public ObjectDef defineObject( int id, String name, String pkg )
	{
		ObjectDef d = (ObjectDef)fObjects.get(name);
		if( d == null ) {
			d = new ObjectDef(id,name,pkg,fVersion);
			fObjects.put(name,d);
		} else {
			if( !d.fPackage.equals( pkg ) ){
				throw new RuntimeException( "Cannot define object " + name + " as belonging to more than one package: " + pkg + "/" + d.fPackage );
			}
		}
		return d;
	}

	public ObjectDef[] getObjects()
	{
		int i = 0;
		ObjectDef[] arr = new ObjectDef[fObjects.size()];
		for( Enumeration e = fObjects.elements(); e.hasMoreElements(); i++ ) {
			arr[i] = (ObjectDef)e.nextElement();
		}
		return arr;
	}

	public Collection<ObjectDef> getObjectCollection()
	{
		return fObjects.values();
	}
	
	public ObjectDef getObject( String name )
	{
		if( name == null ){
			return null;
		}
		if( name.equals( "SIFTime") || name.equals( "Time" ) )
		{
			/* SIFTime objects are treated differently than any other field type
			 */
			return fSIFTimeDef;
		}
		else
		{ 
			return (ObjectDef)fObjects.get(name);
		}
	}

	public void defineEnum( String name, EnumDef enumDef )
	{
		fEnums.put( name, enumDef);
	}

	public EnumDef[] getEnums()
	{
		int i = 0;
		EnumDef[] arr = new EnumDef[fEnums.size()];
		for( Enumeration e = fEnums.elements(); e.hasMoreElements(); i++ ) {
			arr[i] = (EnumDef)e.nextElement();
		}
		return arr;
	}

	public EnumDef getEnum( String enumName )
	{
		return fEnums.get( enumName );
	}
	
	public SIFVersion getVersion() {
		return fVersion;
	}
	public String getNamespace() {
		return fNamespace;
	}
}
