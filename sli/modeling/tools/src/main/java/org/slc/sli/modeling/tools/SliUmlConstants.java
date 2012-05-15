package org.slc.sli.modeling.tools;

/**
 * Constants used in the SLI UML Model.
 */
public final class SliUmlConstants {
    /**
     * Prefix avoids collisions with other tag definitions.
     */
    private static final String TAGDEF_DATASTORE_PREFIX = "dataStore.";

    public static final String TAGDEF_DATASTORE_PII = TAGDEF_DATASTORE_PREFIX.concat("pii");
    public static final String TAGDEF_DATASTORE_ENFORCEMENT_READ = TAGDEF_DATASTORE_PREFIX.concat("enforceRead");
    public static final String TAGDEF_DATASTORE_ENFORCEMENT_WRITE = TAGDEF_DATASTORE_PREFIX.concat("enforceWrite");
    public static final String TAGDEF_DATASTORE_REFERENCE = TAGDEF_DATASTORE_PREFIX.concat("reference");
    public static final String TAGDEF_DATASTORE_RELAXED_BLACKLIST = TAGDEF_DATASTORE_PREFIX.concat("relaxedBlacklist");
    public static final String TAGDEF_DATASTORE_REST_RESOURCE = TAGDEF_DATASTORE_PREFIX.concat("resource");
    public static final String TAGDEF_DATASTORE_SECURITY_SPHERE = TAGDEF_DATASTORE_PREFIX.concat("securitySphere");
}
