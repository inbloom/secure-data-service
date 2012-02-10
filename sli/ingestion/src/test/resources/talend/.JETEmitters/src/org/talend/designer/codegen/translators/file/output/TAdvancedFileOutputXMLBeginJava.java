package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.IMetadataTable;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;

public class TAdvancedFileOutputXMLBeginJava
{
  protected static String nl;
  public static synchronized TAdvancedFileOutputXMLBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TAdvancedFileOutputXMLBeginJava result = new TAdvancedFileOutputXMLBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t\t\tint nb_line_";
  protected final String TEXT_2 = " = 0;" + NL + "" + NL + "\t\t\t";
  protected final String TEXT_3 = NL + "\t\t\t\tboolean needRoot_";
  protected final String TEXT_4 = "  = false;" + NL + "\t\t\t";
  protected final String TEXT_5 = NL + "\t\t\t\tboolean\tneedRoot_";
  protected final String TEXT_6 = " = true;" + NL + "\t\t\t";
  protected final String TEXT_7 = NL + "\t\t\tString fileName_";
  protected final String TEXT_8 = " = ";
  protected final String TEXT_9 = ";" + NL + "\t\t\tBoolean alreadyExistsFile_";
  protected final String TEXT_10 = " = new java.io.File(fileName_";
  protected final String TEXT_11 = ").exists();" + NL + "\t\t\tfileName_";
  protected final String TEXT_12 = " = new java.io.File(fileName_";
  protected final String TEXT_13 = ").getAbsolutePath().replace(\"\\\\\", \"/\");" + NL + "\t\t\tString file_";
  protected final String TEXT_14 = " = \"\";" + NL + "\t\t\t" + NL + "\t\t\tif (fileName_";
  protected final String TEXT_15 = ".indexOf(\"/\") < 0) {" + NL + "\t\t\t\tthrow new IllegalArgumentException(\"not a correct file name.\");\t\t\t\t" + NL + "\t\t\t} else {" + NL + "\t\t\t\tString tail_";
  protected final String TEXT_16 = " = fileName_";
  protected final String TEXT_17 = ".substring(fileName_";
  protected final String TEXT_18 = ".lastIndexOf(\"/\"));" + NL + "\t\t\t\tfile_";
  protected final String TEXT_19 = " = tail_";
  protected final String TEXT_20 = ".lastIndexOf(\".\") > 0 ? fileName_";
  protected final String TEXT_21 = ".substring(0, fileName_";
  protected final String TEXT_22 = ".lastIndexOf(\".\")) : fileName_";
  protected final String TEXT_23 = ";" + NL + "\t\t\t}" + NL + "\t\t\tjava.io.File createFile";
  protected final String TEXT_24 = " = new java.io.File(fileName_";
  protected final String TEXT_25 = "); " + NL + "" + NL + "\t\t\t";
  protected final String TEXT_26 = NL + "\t\t\t\tif (!createFile";
  protected final String TEXT_27 = ".exists()) { " + NL + "\t\t\t\t\t(new java.io.File(fileName_";
  protected final String TEXT_28 = ".substring(0,fileName_";
  protected final String TEXT_29 = ".lastIndexOf(\"/\")))).mkdirs(); " + NL + "\t\t\t\t\tneedRoot_";
  protected final String TEXT_30 = "=true;" + NL + "\t\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_31 = NL + "\t\t\t\tif (!createFile";
  protected final String TEXT_32 = ".exists() || (createFile";
  protected final String TEXT_33 = ".isFile() && createFile";
  protected final String TEXT_34 = ".length() < 1)) {" + NL + "\t\t\t\t\tneedRoot_";
  protected final String TEXT_35 = " = true;" + NL + "\t\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_36 = NL + "\t\t\t\tint currentRowCount_";
  protected final String TEXT_37 = " = 0;" + NL + "\t\t\t\tint currentFileCount_";
  protected final String TEXT_38 = " = 0;" + NL + "\t\t\t\tString suffix_";
  protected final String TEXT_39 = " = \"\";" + NL + "" + NL + "\t\t\t\tif (fileName_";
  protected final String TEXT_40 = ".substring(fileName_";
  protected final String TEXT_41 = ".lastIndexOf(\"/\")).lastIndexOf(\".\") > 0) {" + NL + "\t\t\t\t\tsuffix_";
  protected final String TEXT_42 = " = fileName_";
  protected final String TEXT_43 = ".substring(fileName_";
  protected final String TEXT_44 = ".lastIndexOf(\".\"));" + NL + "\t\t\t\t}" + NL + "\t\t\t\tfileName_";
  protected final String TEXT_45 = " = file_";
  protected final String TEXT_46 = "+\"0\" + suffix_";
  protected final String TEXT_47 = ";" + NL + "\t\t\t";
  protected final String TEXT_48 = NL + "\t\t\tint nb_line_";
  protected final String TEXT_49 = " = 0;" + NL + "\t\t\tboolean\tneedRoot_";
  protected final String TEXT_50 = " = true;" + NL + "\t\t";
  protected final String TEXT_51 = NL + "\t\tjava.util.List<java.util.List<String>> groupbyList_";
  protected final String TEXT_52 = " = new java.util.ArrayList<java.util.List<String>>();" + NL + "\t\tjava.util.Map<String,String> valueMap_";
  protected final String TEXT_53 = " = new java.util.HashMap<String,String>();" + NL + "" + NL + "\t\t";
  protected final String TEXT_54 = NL + NL + "\t\t\tclass NestXMLTool_";
  protected final String TEXT_55 = " {" + NL + "\t\t\t\tpublic void parseAndAdd(org.dom4j.Element nestRoot, String value) {" + NL + "\t\t\t\t\ttry {" + NL + "\t\t\t\t\t\torg.dom4j.Document doc4Str = org.dom4j.DocumentHelper.parseText(\"<root>\"+ value + \"</root>\");" + NL + "\t\t\t\t\t\tnestRoot.setContent(doc4Str.getRootElement().content());" + NL + "\t\t\t\t\t} catch (Exception e) {" + NL + "\t\t\t\t\t\t//    \t\te.printStackTrace();" + NL + "\t\t\t\t\t\tnestRoot.setText(value);" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t}" + NL + "" + NL + "\t\t\t\tpublic void setText(org.dom4j.Element element, String value) {" + NL + "\t\t\t\t\tif (value.startsWith(\"<![CDATA[\") && value.endsWith(\"]]>\")) {" + NL + "\t\t\t\t\t\tString text = value.substring(9, value.length()-3);" + NL + "\t\t\t\t\t\telement.addCDATA(text);" + NL + "\t\t\t\t\t} else {" + NL + "\t\t\t\t\t\telement.setText(value);" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t}" + NL + "" + NL + "\t\t\t\tpublic void replaceDefaultNameSpace(org.dom4j.Element nestRoot){" + NL + "\t\t\t\t\tif (nestRoot != null) {" + NL + "\t\t\t\t\t\tboolean isDefaultNameSpaceAtRoot = \"\".equals(nestRoot.getQName().getNamespace().getPrefix());" + NL + "\t\t\t\t\t\tfor (org.dom4j.Element tmp: (java.util.List<org.dom4j.Element>) nestRoot.elements()) {" + NL + "\t\t\t\t\t\t\tif ((\"\").equals(tmp.getQName().getNamespace().getURI()) && (\"\").equals(tmp.getQName().getNamespace().getPrefix()) && isDefaultNameSpaceAtRoot) {" + NL + "\t\t\t\t\t\t\t\ttmp.setQName(org.dom4j.DocumentHelper.createQName(tmp.getName(), nestRoot.getQName().getNamespace()));" + NL + "\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t\treplaceDefaultNameSpace(tmp);" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t}" + NL + "" + NL + "\t\t\t\tpublic void removeEmptyElement(org.dom4j.Element root) {" + NL + "\t\t\t\t\tif (root != null) {" + NL + "\t\t\t\t\t\tfor (org.dom4j.Element tmp: (java.util.List<org.dom4j.Element>) root.elements()) {" + NL + "\t\t\t\t\t\t\tremoveEmptyElement(tmp);" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\tif (root.content().size() == 0 " + NL + "\t\t\t\t\t\t\t&& root.attributes().size() == 0 " + NL + "\t\t\t\t\t\t\t\t&& root.declaredNamespaces().size() == 0) {" + NL + "\t\t\t\t\t\t\tif (root.getParent() != null) {" + NL + "\t\t\t\t\t\t\t\troot.getParent().remove(root);" + NL + "\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t}" + NL + "\t\t\t}" + NL + "" + NL + "\t\t\tNestXMLTool_";
  protected final String TEXT_56 = " nestXMLTool_";
  protected final String TEXT_57 = " = new NestXMLTool_";
  protected final String TEXT_58 = "();" + NL + "\t\t\t// sort group root element for judgement of group" + NL + "\t\t\tjava.util.List<org.dom4j.Element> groupElementList_";
  protected final String TEXT_59 = " = new java.util.ArrayList<org.dom4j.Element>();" + NL + "\t\t\torg.dom4j.Element root4Group_";
  protected final String TEXT_60 = " = null;" + NL + "\t\t\torg.dom4j.Document doc_";
  protected final String TEXT_61 = "=null;" + NL + "" + NL + "\t\t\t";
  protected final String TEXT_62 = NL + "\t\t\t\tif (needRoot_";
  protected final String TEXT_63 = " == false) {" + NL + "\t\t\t\t\ttry {" + NL + "\t\t\t\t\t\torg.dom4j.io.SAXReader saxReader_";
  protected final String TEXT_64 = "= new org.dom4j.io.SAXReader();" + NL + "\t\t\t\t\t\tdoc_";
  protected final String TEXT_65 = " = saxReader_";
  protected final String TEXT_66 = ".read(createFile";
  protected final String TEXT_67 = ");" + NL + "\t\t\t\t\t} catch (Exception ex) {" + NL + "\t\t\t\t\t\tex.printStackTrace();" + NL + "\t\t\t\t\t\tthrow new Exception(\"can not find the file:\" + fileName_";
  protected final String TEXT_68 = ");" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t} else {" + NL + "\t\t\t\t\tdoc_";
  protected final String TEXT_69 = "=org.dom4j.DocumentHelper.createDocument();" + NL + "\t\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_70 = NL + "\t\t\t\tdoc_";
  protected final String TEXT_71 = "=org.dom4j.DocumentHelper.createDocument();" + NL + "\t\t\t";
  protected final String TEXT_72 = NL + "\t\t\t\t\t\tdoc_";
  protected final String TEXT_73 = ".addDocType(";
  protected final String TEXT_74 = ", null, ";
  protected final String TEXT_75 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_76 = NL + "\t\t\t\t\t\tjava.util.Map<String, String> inMap_";
  protected final String TEXT_77 = " = new java.util.HashMap<String, String>();" + NL + "\t\t\t\t\t\tinMap_";
  protected final String TEXT_78 = ".put(\"type\",";
  protected final String TEXT_79 = ");" + NL + "\t\t\t\t\t\tinMap_";
  protected final String TEXT_80 = ".put(\"href\",";
  protected final String TEXT_81 = ");" + NL + "\t\t\t\t\t\tdoc_";
  protected final String TEXT_82 = ".addProcessingInstruction(\"xml-stylesheet\", inMap_";
  protected final String TEXT_83 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_84 = NL + "\t\t\t\torg.dom4j.io.OutputFormat format_";
  protected final String TEXT_85 = " = org.dom4j.io.OutputFormat.createCompactFormat();" + NL + "\t\t\t";
  protected final String TEXT_86 = NL + "\t\t\t\torg.dom4j.io.OutputFormat format_";
  protected final String TEXT_87 = " = org.dom4j.io.OutputFormat.createPrettyPrint();" + NL + "\t\t\t";
  protected final String TEXT_88 = NL + "\t\t\t\tformat_";
  protected final String TEXT_89 = ".setTrimText(false);" + NL + "\t\t\t";
  protected final String TEXT_90 = NL + "\t\t\tformat_";
  protected final String TEXT_91 = ".setEncoding(";
  protected final String TEXT_92 = ");" + NL + "\t\t\t";
  protected final String TEXT_93 = NL + "\t\t\tint[] orders_";
  protected final String TEXT_94 = " = new int[";
  protected final String TEXT_95 = "];" + NL + "\t\t";
  protected final String TEXT_96 = NL + "\t\t\tjava.util.List<String> endTabStrs_";
  protected final String TEXT_97 = " = new java.util.ArrayList<String>();" + NL + "\t\t\tjava.util.List<String> startTabStrs_";
  protected final String TEXT_98 = " = new java.util.ArrayList<String>();" + NL + "" + NL + "\t\t\t";
  protected final String TEXT_99 = NL + "\t\t\t\t\tendTabStrs_";
  protected final String TEXT_100 = ".add(\"";
  protected final String TEXT_101 = "</";
  protected final String TEXT_102 = ">\");" + NL + "\t\t\t\t\tstartTabStrs_";
  protected final String TEXT_103 = ".add(\"";
  protected final String TEXT_104 = "<";
  protected final String TEXT_105 = ">\");" + NL + "" + NL + "\t\t\t\t\t";
  protected final String TEXT_106 = NL + "\t\t\tint preUnNullMaxIndex_";
  protected final String TEXT_107 = " = -1;" + NL + "\t\t\tint preNewTabIndex_";
  protected final String TEXT_108 = " = -1;" + NL + "\t\t\tString[] startTabs_";
  protected final String TEXT_109 = " = new String[endTabStrs_";
  protected final String TEXT_110 = ".size()];" + NL + "\t\t\tString[] endTabs_";
  protected final String TEXT_111 = " = new String[endTabStrs_";
  protected final String TEXT_112 = ".size()];" + NL + "\t\t\t";
  protected final String TEXT_113 = NL + "\t\t\t//String[] mainEndTabs_";
  protected final String TEXT_114 = " = new String[";
  protected final String TEXT_115 = "];" + NL + "" + NL + "\t\t\t";
  protected final String TEXT_116 = NL + "\t\t\t\tjava.io.BufferedWriter out_";
  protected final String TEXT_117 = " = new java.io.BufferedWriter(" + NL + "\t\t\t\tnew java.io.OutputStreamWriter(new java.io.FileOutputStream(fileName_";
  protected final String TEXT_118 = "), ";
  protected final String TEXT_119 = "));" + NL + "\t\t\t";
  protected final String TEXT_120 = NL + "\t\t\t\tjava.io.OutputStreamWriter outWriter_";
  protected final String TEXT_121 = " = new java.io.OutputStreamWriter(";
  protected final String TEXT_122 = ", ";
  protected final String TEXT_123 = ");" + NL + "\t\t\t\tjava.io.BufferedWriter out_";
  protected final String TEXT_124 = " = new java.io.BufferedWriter(outWriter_";
  protected final String TEXT_125 = ");" + NL + "\t\t\t";
  protected final String TEXT_126 = NL + "\t\t\tout_";
  protected final String TEXT_127 = ".write(\"<?xml version=\\\"1.0\\\" encoding=\\\"\"+";
  protected final String TEXT_128 = "+\"\\\"?>\");" + NL + "\t\t\tout_";
  protected final String TEXT_129 = ".newLine();" + NL + "\t\t\t" + NL + "\t\t\t";
  protected final String TEXT_130 = NL + "\t\t\t\t\tout_";
  protected final String TEXT_131 = ".write(\"<!DOCTYPE \"+";
  protected final String TEXT_132 = "+\" SYSTEM \\\"\" + ";
  protected final String TEXT_133 = " + \"\\\">\");" + NL + "\t\t\t\t\tout_";
  protected final String TEXT_134 = ".newLine();" + NL + "\t\t\t\t";
  protected final String TEXT_135 = NL + "\t\t\t\t\tout_";
  protected final String TEXT_136 = ".write(\"<?xml-stylesheet type=\\\"\"+";
  protected final String TEXT_137 = "+\"\\\" href=\\\"\"+";
  protected final String TEXT_138 = "+\"\\\">\");" + NL + "\t\t\t\t\tout_";
  protected final String TEXT_139 = ".newLine();" + NL + "\t\t\t\t";
  protected final String TEXT_140 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String cid_original = cid;
cid = cid_original.replace("tAdvancedFileOutputXML","tAFOX");
List< ? extends IConnection> conns = node.getIncomingConnections();

if (conns == null || conns.isEmpty()) {
	return "";			
} else {
	IConnection conn = conns.get(0);

	if (!conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) { 
		return "";
	}
}
List<IMetadataTable> metadatas = node.getMetadataList();

if ((metadatas != null) && (metadatas.size() > 0)) {
	IMetadataTable metadata = metadatas.get(0);

	if (metadata != null) {
		String encoding = ElementParameterParser.getValue(node, "__ENCODING__");
		String fileName = ElementParameterParser.getValue(node, "__FILENAME__");
		String mode = ElementParameterParser.getValue(node, "__GENERATION_MODE__");
		String outStream = ElementParameterParser.getValue(node,"__STREAMNAME__");
		String dtdRootName = ElementParameterParser.getValue(node, "__DTD_NAME__");
		String dtdFileName = ElementParameterParser.getValue(node, "__DTD_SYSTEMID__");
		String xslType = ElementParameterParser.getValue(node, "__XSL_TYPE__");
		String xslHref = ElementParameterParser.getValue(node, "__XSL_HREF__");
		boolean split = "true".equals(ElementParameterParser.getValue(node, "__SPLIT__"));
		boolean isMerge= ("true").equals(ElementParameterParser.getValue(node, "__MERGE__"));
		boolean isCompact = ("true").equals(ElementParameterParser.getValue(node, "__PRETTY_COMPACT__"));
		boolean needFileValid = ("true").equals(ElementParameterParser.getValue(node, "__FILE_VALID__"));
		boolean needDTDValid = ("true").equals(ElementParameterParser.getValue(node, "__DTD_VALID__"));
		boolean useStream = ("true").equals(ElementParameterParser.getValue(node,"__USESTREAM__"));
		boolean needXSLValid = ("true").equals(ElementParameterParser.getValue(node, "__XSL_VALID__"));
		List<Map<String, String>> rootTable = 
		(List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__ROOT__");
		List<Map<String, String>> groupTable = 
		(List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__GROUP__");
		List<Map<String, String>> loopTable = 
		(List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__LOOP__");

		if (!useStream) {
		
    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    if (isMerge == true) {
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    } else {
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    }
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(fileName);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    if (("true").equals(ElementParameterParser.getValue(node,"__CREATE__"))) {
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    }

			if (isMerge==true) {
			
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    
			}

			if (split) {
			
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    
			}
		} else {
		
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    
		}//***************************the part of the output stream end************************************************
		
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    
		// *** generation mode init ***
		if (("Dom4j").equals(mode)) {
		
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    if (!useStream && isMerge == true) {//append mode and the code of file path
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_69);
    } else {	
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_71);
    
			}

			if (!isMerge) {
				if (needFileValid) {
					if (needDTDValid) {
					
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(dtdRootName );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(dtdFileName );
    stringBuffer.append(TEXT_75);
    } else if (needXSLValid) {
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(xslType );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(xslHref );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_83);
    
					}
				}
			}

			if (isCompact==true) {//generate compact file
			
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_85);
    } else {	
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_87);
    
			}

			if (!isMerge || isCompact) {
			
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_89);
    }
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_92);
    
			int groupSize = 0;

			if (groupTable != null && groupTable.size() > 0) {
				for (java.util.Map<String, String> tmpMap : groupTable) {
					if (tmpMap.get("ATTRIBUTE").equals("main")) {
						groupSize++;
					}
				}
			}
			
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_94);
    stringBuffer.append(groupSize + 1);
    stringBuffer.append(TEXT_95);
    
		} else if (("Null").equals(mode)) {
		
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_98);
    
			if (loopTable.size() > 0) {
				String emptyspace = "";
				String endPath = loopTable.get(0).get("PATH");
				String[] endTabs = endPath.split("/");

				for (int len = 1; len < endTabs.length-1; len++) {
				
    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_100);
    stringBuffer.append(emptyspace);
    stringBuffer.append(TEXT_101);
    stringBuffer.append(endTabs[len]);
    stringBuffer.append(TEXT_102);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_103);
    stringBuffer.append(isCompact?"":"\\n");
    stringBuffer.append(emptyspace);
    stringBuffer.append(TEXT_104);
    stringBuffer.append(endTabs[len]);
    stringBuffer.append(TEXT_105);
    
					if (isCompact == false) {//generate pretty file
						emptyspace += "  ";
					}
				}
			}
			
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_112);
    
			int rootSize = 0;

			if (rootTable != null && rootTable.size() > 0) {
				for (java.util.Map<String, String> tmpMap : rootTable) {
					if (tmpMap.get("ATTRIBUTE").equals("main")) {
						rootSize++;
					}
				}
			}
			
    stringBuffer.append(TEXT_113);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_114);
    stringBuffer.append(rootSize);
    stringBuffer.append(TEXT_115);
    if (!useStream) {// the part of file path---the old code
    stringBuffer.append(TEXT_116);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_117);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_118);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_119);
    } else {// the part of output stream
    stringBuffer.append(TEXT_120);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_121);
    stringBuffer.append(outStream );
    stringBuffer.append(TEXT_122);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_123);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_124);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_125);
    }
    stringBuffer.append(TEXT_126);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_127);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_128);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_129);
    
			if (needFileValid) {
				if (needDTDValid) {
				
    stringBuffer.append(TEXT_130);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_131);
    stringBuffer.append(dtdRootName );
    stringBuffer.append(TEXT_132);
    stringBuffer.append(dtdFileName );
    stringBuffer.append(TEXT_133);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_134);
    } else if (needXSLValid) {
    stringBuffer.append(TEXT_135);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_136);
    stringBuffer.append(xslType );
    stringBuffer.append(TEXT_137);
    stringBuffer.append(xslHref);
    stringBuffer.append(TEXT_138);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_139);
    
				}
			}
		}
	}
}

    stringBuffer.append(TEXT_140);
    return stringBuffer.toString();
  }
}
