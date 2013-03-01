package test.camel.support.zip;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;

import org.apache.camel.CamelExchangeException;
import org.apache.camel.Exchange;
import org.apache.camel.support.ExpressionAdapter;
import org.apache.commons.compress.archivers.zip.ZipFile;

public class ZipFileValidator extends ExpressionAdapter {

    public Object evaluate(Exchange exchange) {

        ZipFile zipFile = null;

        try {
            File file = exchange.getIn().getMandatoryBody(File.class);

            zipFile = new ZipFile(file);

            Enumeration<? extends ZipEntry> zipEntries = zipFile.getEntries();

            while(zipEntries.hasMoreElements()) {
                ZipEntry zipEntry = zipEntries.nextElement();

                if (zipEntry.isDirectory() || zipEntry.getName().contains("/")) {
                    throw new IOException("Zip folders are not supported.");
                } else {
                    if (zipEntry.getName().toLowerCase().endsWith(".ctl")) {
                        return Boolean.TRUE;
                    }
                }
            }
        } catch (CamelExchangeException e) {
            exchange.setException(e);

            return Boolean.FALSE;
        } catch (IOException e) {
            exchange.setException(e);

            return Boolean.FALSE;
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                }
            }
        }

        return Boolean.FALSE;
    }

}
