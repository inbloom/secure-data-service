package org.slc.sli.modeling.xsd2xmi.cmdline;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.index.DefaultMapper;
import org.slc.sli.modeling.uml.index.Mapper;
import org.slc.sli.modeling.xmi.reader.XmiReader;

public final class EdFiSLI {
	private static final Set<String> finance = finance();
	private static final Map<String, String> classRenames = classRenames();

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		try {
			final Mapper slim = new DefaultMapper(
					XmiReader.readModel("../data/SLI.xmi"));
			final Mapper edfi = new DefaultMapper(
					XmiReader.readModel("../data/ED-Fi-Core.xmi"));
			compareClasses(slim, edfi);
			// compareAttributes(slim, edfi);
		} catch (final FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private static final void compareClasses(final Mapper slimModel,
			final Mapper edfiModel) {
		System.out.println("Ed-Fi - SLI deviations from UML models.");
		printGMT();
		System.out.println("");
		System.out.println("SLI:");
		final Set<String> slimRaw = classNames(slimModel.getClassTypes());
		System.out.println("Raw complexTypes . . . . . : " + slimRaw.size());
		final Set<String> slim = rename(
				subtractEndsWith(subtractEndsWith(slimRaw, "ReferenceType"),
						"IdentityType"), invert(classRenames));
		System.out.println("Normalized . . . . . . . . : " + slim.size()
				+ " (remove *ReferenceType or *IdentityType)");

		System.out.println("");
		System.out.println("Ed-Fi-Core:");
		final Set<String> edfiRaw = classNames(edfiModel.getClassTypes());
		System.out.println("Raw complexTypes . . . . . : " + edfiRaw.size());
		final Set<String> edfi = subtractEndsWith(
				subtractEndsWith(edfiRaw, "ReferenceType"), "IdentityType");
		System.out.println("Normalized . . . . . . . . : " + edfi.size()
				+ " (remove *ReferenceType or *IdentityType)");

		System.out.println("");
		System.out.println("Adjustment for Finance domain.");
		System.out.println("finance = " + finance);
		System.out.println("size(finance)  . . . . . . : " + finance.size());
		final Set<String> edfiMinusFinance = subtract(edfi, finance);
		System.out.println("size(edfi - finance) . . . : "
				+ edfiMinusFinance.size());

		final Set<String> edfiMinusFinanceMinuSlim = subtract(edfiMinusFinance,
				slim);
		System.out.println("");
		System.out.println("How does Ed-Fi-Core exceed SLI?");
		System.out.println("size(edfi - finance - sli) "
				+ edfiMinusFinanceMinuSlim.size());
		print(edfiMinusFinanceMinuSlim);

		final Set<String> slimMinusEdfiMinusFinance = subtract(slim,
				edfiMinusFinance);
		System.out.println("");
		System.out.println("How does SLI exceed Ed-Fi-Core?");
		System.out.println("sli - (edfi - finance)) "
				+ slimMinusEdfiMinusFinance.size());
		print(slimMinusEdfiMinusFinance);

		System.out.println("");
		System.out.println("Summary:");
		final int missing = edfiMinusFinanceMinuSlim.size();
		final int common = edfiMinusFinance.size() - missing;
		final int excess = slimMinusEdfiMinusFinance.size();
		System.out.println("common: " + common);
		System.out.println("missing: " + missing);
		System.out.println("excess: " + excess);

		System.out.println("coverage: " + (common * 100.0d)
				/ (common + missing) + "%");
		System.out.println("");
		System.out
				.println("Disclaimer: This assumes that attribuites of classes are 100% covered.");
	}

	private static final void printGMT() {
		Calendar c = Calendar.getInstance();
		System.out.println("" + c.getTime());
	}

	private static final void print(final Set<String> strings) {
		final List<String> sortNames = new ArrayList<String>(strings);
		Collections.sort(sortNames);
		for (final String name : sortNames) {
			System.out.println("" + name);
		}
	}

	private static final <T> Set<T> subtract(final Set<T> lhs, final Set<T> rhs) {
		final Set<T> copy = new HashSet<T>(lhs);
		copy.removeAll(rhs);
		return Collections.unmodifiableSet(copy);
	}

	@SuppressWarnings("unused")
	private static final void compareAttributes(final Mapper slim,
			final Mapper edfi) {

		final Set<QName> slimNames = attributeNames(slim.getClassTypes());
		System.out.println("slimNames.size=" + slimNames.size());
		System.out.println("slimNames:" + slimNames);

		final Set<QName> edfiNames = attributeNames(edfi.getClassTypes());
		System.out.println("edfiNames.size=" + edfiNames.size());
		System.out.println("edfiNames:" + edfiNames);

		edfiNames.removeAll(slimNames);
		System.out.println("edfiNames.size=" + edfiNames.size());
		for (final QName name : edfiNames) {
			System.out.println("" + name);
		}
	}

	private static final Set<String> subtractEndsWith(
			final Set<String> strings, final String s) {
		final Set<String> result = new HashSet<String>();
		for (final String name : strings) {
			if (!name.endsWith(s)) {
				result.add(name);
			}
		}
		return Collections.unmodifiableSet(result);
	}

	private EdFiSLI() {
		// Prevent instantiation, even through reflection.
		throw new RuntimeException();
	}

	private static final Set<QName> attributeNames(
			final Iterable<ClassType> classTypes) {
		final Set<QName> names = new HashSet<QName>();
		for (final ClassType classType : classTypes) {
			for (final Attribute attribute : classType.getAttributes()) {
				names.add(new QName(classType.getName(), attribute.getName()));
			}
		}
		return names;
	}

	private static final Set<String> classNames(
			final Iterable<ClassType> classTypes) {
		final Set<String> names = new HashSet<String>();
		for (final ClassType classType : classTypes) {
			names.add(classType.getName());
		}
		return Collections.unmodifiableSet(names);
	}

	private static final Set<String> finance() {
		final Set<String> finance = new HashSet<String>();
		finance.add("Account");
		finance.add("AccountCodeDescriptor");
		finance.add("AccountCodeDescriptorType");
		finance.add("Actual");
		finance.add("Budget");
		finance.add("ContractedStaff");
		finance.add("Payroll");
		return Collections.unmodifiableSet(finance);
	}

	private static final Set<String> rename(final Set<String> originals,
			final Map<String, String> renames) {
		final Set<String> result = new HashSet<String>();
		for (final String original : originals) {
			if (renames.containsKey(original)) {
				final String rename = renames.get(original);
				result.add(rename);
			} else {
				result.add(original);
			}

		}
		return Collections.unmodifiableSet(result);
	}

	private static final Map<String, String> classRenames() {
		final Map<String, String> renames = new HashMap<String, String>();
		renames.put("StudentAssessment", "StudentAssessmentAssociation");
		renames.put("StudentGradebookEntry", "StudentSectionGradebookEntry");
		return Collections.unmodifiableMap(renames);
	}

	private static final Map<String, String> invert(
			final Map<String, String> mapping) {
		final Map<String, String> inversion = new HashMap<String, String>();
		for (final String lhs : mapping.keySet()) {
			final String rhs = mapping.get(lhs);
			inversion.put(rhs, lhs);
		}
		return Collections.unmodifiableMap(inversion);
	}
}
