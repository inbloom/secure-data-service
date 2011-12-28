package org.slc.sli.ingestion.smooks;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.milyn.Smooks;
import org.milyn.delivery.Visitor;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.NeutralRecordFileWriter;
import org.xml.sax.SAXException;

/**
 * Factory class for Smooks
 *
 * @author dduran
 *
 */
public class SliSmooksFactory {

    private static Map<FileType, SliSmooksConfig> sliSmooksConfigMap;
    private static String beanId;

    public static Smooks createInstance(FileType fileType, NeutralRecordFileWriter fileWriter) throws IOException,
            SAXException {

        SliSmooksConfig sliSmooksConfig = sliSmooksConfigMap.get(fileType);
        if (sliSmooksConfig != null) {

            return createSmooksFromConfig(sliSmooksConfig, fileWriter);

        } else {
            throw new IllegalArgumentException("File type not supported : " + fileType);
        }
    }

    private static Smooks createSmooksFromConfig(SliSmooksConfig sliSmooksConfig, NeutralRecordFileWriter fileWriter)
            throws IOException, SAXException {
        Smooks smooks = new Smooks(sliSmooksConfig.getConfigFileName());

        // based on target selectors for this file type, add visitors
        List<String> targetSelectorList = sliSmooksConfig.getTargetSelectors();
        if (targetSelectorList != null) {

            // just one visitor instance that can be added with multiple target selectors
            Visitor smooksEdFiVisitor = new SmooksEdFiVisitor(beanId, fileWriter);

            for (String targetSelector : targetSelectorList) {
                smooks.addVisitor(smooksEdFiVisitor, targetSelector);
            }
        }
        return smooks;
    }

    public static void setSliSmooksConfigMap(Map<FileType, SliSmooksConfig> sliSmooksConfigMap) {
        SliSmooksFactory.sliSmooksConfigMap = sliSmooksConfigMap;
    }

    public static void setBeanId(String beanId) {
        SliSmooksFactory.beanId = beanId;
    }
}
