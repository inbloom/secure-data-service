package org.slc.sli.ingestion.xml.idref;

import java.io.File;

/**
 *
 * @author npandey
 *
 */
public class IdRefFile extends File {

    public IdRefFile(File file) {
        super(file.toURI());
    }


}