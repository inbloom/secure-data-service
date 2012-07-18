 /// <summary>
   /// Parses the list of SIF_Version elements and returns an array of SIFVersions
   /// </summary>
   /// <param name="failureLog">The log to write failures to, if any of the SIFVersions fail
   /// to be parsed.</param>
   /// <returns><An array of SIFVersion elements. This will never be null/returns>
    internal SifVersion[] parseRequestVersions( log4net.ILog failureLog )
	{
		if( failureLog == null ){
			failureLog = Adk.Log;
		}
		System.Collections.Generic.List<SifVersion> versionList = new System.Collections.Generic.List<SifVersion>();
       	foreach( SifElement element in GetChildList( InfraDTD.SIF_REQUEST_SIF_VERSION ) ){
			SIF_Version candidate = (SIF_Version)element;
			SifVersion version = null;
			try {
				// Check for "1.*" and "2.*"
				String ver = candidate.Value;
				if( ver != null ){
					if( ver.IndexOf( ".*" ) > 0 ){
						version = SifVersion.GetLatest( int.Parse( ver.Substring( 0, 1 ) ) );
					} else {
						version = SifVersion.Parse( ver );
					}
				}
			} catch( ArgumentException exc ){
                failureLog.Warn( String.Format( "Unable to parse '{0}' from SIF_Request/SIF_Version as SIFVersion.", candidate.Value), exc  );
			}
            catch( FormatException exc )
            {
                failureLog.Warn( String.Format( "Unable to parse '{0}' from SIF_Request/SIF_Version as SIFVersion.", candidate.Value ), exc );
            } 
			if( version != null && !versionList.Contains( version ) ){
				versionList.Add( version );
			}
		}

        return versionList.ToArray();
	}