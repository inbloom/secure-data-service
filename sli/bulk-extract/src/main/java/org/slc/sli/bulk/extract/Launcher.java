package org.slc.sli.bulk.extract;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slc.sli.bulk.extract.extractor.TenantExtractor;
import org.slc.sli.bulk.extract.zip.OutstreamZipFile;
import org.slc.sli.dal.repository.connection.TenantAwareMongoDbFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Launcher {
    private final static String USAGE = "Usage: bulk-extract <tenant>";
    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);

    private String baseDirectory;

    private TenantExtractor tenantExtractor;

    public void execute(String tenant){

        Date startTime = new Date();
        OutstreamZipFile outputStream = null;
        try {
            outputStream = createExtractArchiveFile(tenant, startTime);
            tenantExtractor.execute(tenant, outputStream, startTime);
        } catch (IOException e) {
            LOG.error("Error while extracting data for tenant " + tenant, e);
        }
    }

    private OutstreamZipFile createExtractArchiveFile(String tenant, Date startTime) throws IOException {

        return new OutstreamZipFile(getTenantDirectory(tenant), tenant + "-" + getTimeStamp(startTime));
    }

    private String getTenantDirectory(String tenant) {

        File tenantDirectory = new File(baseDirectory, TenantAwareMongoDbFactory.getTenantDatabaseName(tenant));
        tenantDirectory.mkdirs();
        return tenantDirectory.getPath();
    }

    public static String getTimeStamp(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
        String timeStamp = df.format(date);
        return timeStamp;
    }

    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public void setTenantExtractor(TenantExtractor tenantExtractor){
        this.tenantExtractor = tenantExtractor;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/application-context.xml");

        Launcher main = context.getBean(Launcher.class);

        if (args.length != 1) {
            LOG.error(USAGE);
            return;
        }

        String tenantId = args[0];

        main.execute(tenantId);

    }

}
