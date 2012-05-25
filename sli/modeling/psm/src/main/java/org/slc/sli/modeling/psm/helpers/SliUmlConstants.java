package org.slc.sli.modeling.psm.helpers;

/**
 * Constants used in the SLI UML Model.
 */
public final class SliUmlConstants {
    /**
     * Prefix avoids collisions with other tag definitions.
     */
    private static final String TAGDEF_PREFIX = "dataStore.";

    public static final String TAGDEF_NATURAL_KEY = TAGDEF_PREFIX.concat("naturalKey");
    public static final String TAGDEF_PII = TAGDEF_PREFIX.concat("pii");
    public static final String TAGDEF_ENFORCE_READ = TAGDEF_PREFIX.concat("enforceRead");
    public static final String TAGDEF_ENFORCE_WRITE = TAGDEF_PREFIX.concat("enforceWrite");
    public static final String TAGDEF_REFERENCE = TAGDEF_PREFIX.concat("reference");
    public static final String TAGDEF_RELAXED_BLACKLIST = TAGDEF_PREFIX.concat("relaxedBlacklist");
    public static final String TAGDEF_REST_RESOURCE = TAGDEF_PREFIX.concat("resource");
    public static final String TAGDEF_RESTRICTED_FOR_LOGGING = TAGDEF_PREFIX.concat("restrictedForLogging");
    public static final String TAGDEF_SECURITY_SPHERE = TAGDEF_PREFIX.concat("securitySphere");
}
