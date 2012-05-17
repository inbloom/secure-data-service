package org.slc.sli.modeling.tools.edfisli.cmdline;

import java.io.FileNotFoundException;
import java.util.Set;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;

public final class WhereUsedCmdLine {

    public static void main(final String[] args) {
        try {
            final Model model = XmiReader.readModel("SLI.xmi");
            final ModelIndex index = new DefaultModelIndex(model);

            final String name = "percent";
            final Set<ModelElement> matches = index.lookupByName(new QName(name));
            for (final ModelElement match : matches) {
                System.out.println("name : " + name + " => " + match);
                showUsage(index, match.getId(), "  ");
            }
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static final void showUsage(final ModelIndex index, final Identifier id, final String indent) {
        final Set<ModelElement> usages = index.whereUsed(id);
        for (final ModelElement usage : usages) {
            if (usage instanceof ClassType) {
                final ClassType classType = (ClassType) usage;
                System.out.println(indent + "classType : " + classType.getName());
                showUsage(index, classType.getId(), indent.concat("  "));
            } else if (usage instanceof Attribute) {
                final Attribute attribute = (Attribute) usage;
                System.out.println(indent + "attribute : " + attribute.getName());
                showUsage(index, attribute.getId(), indent.concat("  "));
            } else {
                System.out.println(indent + "usage : " + usage.getClass());
            }
        }
    }
}
