package org.slc.sli.modeling.xmi2Psm;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import org.slc.sli.modeling.psm.PsmClassType;
import org.slc.sli.modeling.psm.PsmCollection;
import org.slc.sli.modeling.psm.PsmConfig;
import org.slc.sli.modeling.psm.PsmConfigWriter;
import org.slc.sli.modeling.psm.PsmResource;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.NamespaceOwnedElement;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.xmi.reader.XmiReader;

/**
 * A utility for converting XMI to Diagrammatic Predicate Logic (or maybe just documentation :).
 */
public final class Xmi2Psm {
    
    public static void main(final String[] args) {
        try {
            final Model model = XmiReader.readModel("../data/SLI.xmi");
            final PsmConfig<Type> psm = convert(model);
            PsmConfigWriter.writeConfig(psm, model, "sli-psm-cfg.xml");
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private Xmi2Psm() {
        // Prevent instantiation, even through reflection.
        throw new RuntimeException();
    }
    
    private static final PsmConfig<Type> convert(final Model model) {
        final List<PsmClassType<Type>> resources = new LinkedList<PsmClassType<Type>>();
        for (final NamespaceOwnedElement ownedElement : model.getOwnedElements()) {
            if (ownedElement instanceof ClassType) {
                final ClassType classType = (ClassType) ownedElement;
                resources.add(convertClassType(classType));
            }
        }
        return new PsmConfig<Type>(resources);
    }
    
    private static final PsmClassType<Type> convertClassType(final ClassType classType) {
        final String camelName = camelCase(classType.getName());
        final PsmCollection collection = new PsmCollection(camelName);
        final PsmResource resource = new PsmResource(camelName.concat("s"));
        return new PsmClassType<Type>(classType, resource, collection);
    }
    
    private static final String camelCase(final String name) {
        return name.substring(0, 1).toLowerCase().concat(name.substring(1));
    }
}
