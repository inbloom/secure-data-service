package org.talend.designer.codegen.translators.xml;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.IMetadataTable;
import java.util.List;
import java.util.Map;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.EConnectionType;

public class TWriteXMLFieldOutBeginJava
{
  protected static String nl;
  public static synchronized TWriteXMLFieldOutBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TWriteXMLFieldOutBeginJava result = new TWriteXMLFieldOutBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "//tWriteXMLFieldOut_begin";
  protected final String TEXT_2 = NL + "int nb_line_";
  protected final String TEXT_3 = " = 0;" + NL + "boolean needRoot_";
  protected final String TEXT_4 = "  = true;" + NL + "" + NL + "String  strCompCache_";
  protected final String TEXT_5 = "= null;\t\t" + NL + "" + NL + "java.util.Queue<String> listGroupby_";
  protected final String TEXT_6 = " = new java.util.concurrent.ConcurrentLinkedQueue<String>();";
  protected final String TEXT_7 = NL + "java.util.List<java.util.Map<String,String>> flows_";
  protected final String TEXT_8 = " = java.util.Collections.synchronizedList(new java.util.ArrayList<java.util.Map<String,String>>());" + NL + "java.util.Map<String,String> flowValues_";
  protected final String TEXT_9 = " = null;";
  protected final String TEXT_10 = NL + NL + "class ThreadXMLField extends Thread {" + NL + "\tjava.util.Queue<String> queue;" + NL + "\tjava.util.List<java.util.Map<String,String>> flows;" + NL + "\t" + NL + "\tThreadXMLField(java.util.Queue q) {" + NL + "\t\tthis.queue = q;" + NL + "\t\tglobalMap.put(\"queue_";
  protected final String TEXT_11 = "\", queue);" + NL + "\t}" + NL + "\t" + NL + "\tThreadXMLField(java.util.Queue q, java.util.List<java.util.Map<String,String>> l) {" + NL + "\t\tthis.queue = q;" + NL + "\t\tthis.flows = l;" + NL + "\t\tglobalMap.put(\"queue_";
  protected final String TEXT_12 = "\", queue);" + NL + "\t\tglobalMap.put(\"flows_";
  protected final String TEXT_13 = "\", flows);" + NL + "\t}" + NL + "" + NL + "\t@Override" + NL + "\tpublic void run() {" + NL + "\t\ttry {" + NL + "\t\t\t";
  protected final String TEXT_14 = "Process(globalMap);" + NL + "\t\t} catch (TalendException e) {" + NL + "\t\t\te.printStackTrace();" + NL + "\t\t}" + NL + "\t}" + NL + "}";
  protected final String TEXT_15 = NL + "\tThreadXMLField txf_";
  protected final String TEXT_16 = " = new ThreadXMLField(listGroupby_";
  protected final String TEXT_17 = ");";
  protected final String TEXT_18 = NL + "\tThreadXMLField txf_";
  protected final String TEXT_19 = " = new ThreadXMLField(listGroupby_";
  protected final String TEXT_20 = ", flows_";
  protected final String TEXT_21 = ");";
  protected final String TEXT_22 = NL + "txf_";
  protected final String TEXT_23 = ".start();" + NL + "" + NL + "java.util.List<java.util.List<String>> groupbyList_";
  protected final String TEXT_24 = " = new java.util.ArrayList<java.util.List<String>>();" + NL + "java.util.Map<String,String> valueMap_";
  protected final String TEXT_25 = " = new java.util.HashMap<String,String>();" + NL;
  protected final String TEXT_26 = NL + NL + "class NestXMLTool_";
  protected final String TEXT_27 = "{" + NL + "\tpublic void parseAndAdd(org.dom4j.Element nestRoot, String value){" + NL + "\t\ttry {" + NL + "            org.dom4j.Document doc4Str = org.dom4j.DocumentHelper.parseText(\"<root>\"+ value + \"</root>\");" + NL + "    \t\tnestRoot.setContent(doc4Str.getRootElement().content());" + NL + "    \t} catch (Exception e) {" + NL + "    \t\te.printStackTrace();" + NL + "    \t\tnestRoot.setText(value);" + NL + "        }" + NL + "\t}" + NL + "\t" + NL + "\tpublic void setText(org.dom4j.Element element, String value){" + NL + "\t\tif (value.startsWith(\"<![CDATA[\") && value.endsWith(\"]]>\")) {" + NL + "\t\t\tString text = value.substring(9, value.length()-3);" + NL + "\t\t\telement.addCDATA(text);" + NL + "\t\t}else{" + NL + "\t\t\telement.setText(value);" + NL + "\t\t}" + NL + "\t}" + NL + "\t" + NL + "\tpublic void replaceDefaultNameSpace(org.dom4j.Element nestRoot){" + NL + "\t\tif (nestRoot!=null) {" + NL + "\t\t\tfor (org.dom4j.Element tmp: (java.util.List<org.dom4j.Element>) nestRoot.elements()) {" + NL + "        \t\tif ((\"\").equals(tmp.getQName().getNamespace().getURI()) && (\"\").equals(tmp.getQName().getNamespace().getPrefix())){" + NL + "        \t\t\ttmp.setQName(org.dom4j.DocumentHelper.createQName(tmp.getName(), nestRoot.getQName().getNamespace()));" + NL + "\t        \t}" + NL + "    \t    \treplaceDefaultNameSpace(tmp);" + NL + "       \t\t}" + NL + "       \t}" + NL + "\t}" + NL + "\t" + NL + "\tpublic void removeEmptyElement(org.dom4j.Element root){" + NL + "\t\tif (root!=null) {" + NL + "\t\t\tfor (org.dom4j.Element tmp: (java.util.List<org.dom4j.Element>) root.elements()) {" + NL + "\t\t\t\tremoveEmptyElement(tmp);" + NL + "\t\t\t}" + NL + "\t\t\tif (root.content().size() == 0 " + NL + "    \t\t\t&& root.attributes().size() == 0 " + NL + "    \t\t\t&& root.declaredNamespaces().size() == 0) {" + NL + "    \t\t\tif(root.getParent()!=null){" + NL + "                \troot.getParent().remove(root);" + NL + "                }" + NL + "            }" + NL + "\t\t}" + NL + "\t}" + NL + "}" + NL + "NestXMLTool_";
  protected final String TEXT_28 = " nestXMLTool_";
  protected final String TEXT_29 = " = new NestXMLTool_";
  protected final String TEXT_30 = "();" + NL + "" + NL + "// sort group root element for judgement of group" + NL + "java.util.List<org.dom4j.Element> groupElementList_";
  protected final String TEXT_31 = " = new java.util.ArrayList<org.dom4j.Element>();" + NL + "org.dom4j.Element root4Group_";
  protected final String TEXT_32 = " = null;" + NL + "org.dom4j.Document doc_";
  protected final String TEXT_33 = "  = org.dom4j.DocumentHelper.createDocument();";
  protected final String TEXT_34 = NL + "org.dom4j.io.OutputFormat format_";
  protected final String TEXT_35 = " = org.dom4j.io.OutputFormat.createCompactFormat();" + NL + "format_";
  protected final String TEXT_36 = ".setNewLineAfterDeclaration(false);";
  protected final String TEXT_37 = NL + "org.dom4j.io.OutputFormat format_";
  protected final String TEXT_38 = " = org.dom4j.io.OutputFormat.createPrettyPrint();";
  protected final String TEXT_39 = NL + "format_";
  protected final String TEXT_40 = ".setTrimText(false);" + NL + "format_";
  protected final String TEXT_41 = ".setEncoding(";
  protected final String TEXT_42 = ");";
  protected final String TEXT_43 = "format_";
  protected final String TEXT_44 = ".setExpandEmptyElements(true);";
  protected final String TEXT_45 = NL + "int[] orders_";
  protected final String TEXT_46 = " = new int[";
  protected final String TEXT_47 = "];";
  protected final String TEXT_48 = NL + "java.util.List<String> endTabStrs_";
  protected final String TEXT_49 = " = new java.util.ArrayList<String>();" + NL + "java.util.List<String> startTabStrs_";
  protected final String TEXT_50 = " = new java.util.ArrayList<String>();";
  protected final String TEXT_51 = NL + "endTabStrs_";
  protected final String TEXT_52 = ".add(\"";
  protected final String TEXT_53 = "</";
  protected final String TEXT_54 = ">\");" + NL + "startTabStrs_";
  protected final String TEXT_55 = ".add(\"";
  protected final String TEXT_56 = "<";
  protected final String TEXT_57 = ">\");";
  protected final String TEXT_58 = NL + "int preUnNullMaxIndex_";
  protected final String TEXT_59 = " = -1;" + NL + "int preNewTabIndex_";
  protected final String TEXT_60 = " = -1;" + NL + "String[] startTabs_";
  protected final String TEXT_61 = " = new String[endTabStrs_";
  protected final String TEXT_62 = ".size()];" + NL + "String[] endTabs_";
  protected final String TEXT_63 = " = new String[endTabStrs_";
  protected final String TEXT_64 = ".size()];" + NL + "java.io.StringWriter strWriter_";
  protected final String TEXT_65 = " = new java.io.StringWriter();" + NL + "java.io.BufferedWriter out_";
  protected final String TEXT_66 = " = new java.io.BufferedWriter(strWriter_";
  protected final String TEXT_67 = ");";
  protected final String TEXT_68 = NL + "out_";
  protected final String TEXT_69 = ".write(\"<?xml version=\\\"1.0\\\" encoding=\\\"\"+";
  protected final String TEXT_70 = "+\"\\\"?>\");" + NL + "out_";
  protected final String TEXT_71 = ".write(\"";
  protected final String TEXT_72 = "\");";
  protected final String TEXT_73 = NL;
  protected final String TEXT_74 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();


String virtualTargetCid = node.getOutgoingConnections(EConnectionType.ON_COMPONENT_OK).get(0).getTarget().getUniqueName();

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {
    	List< ? extends IConnection> conns = node.getIncomingConnections();
		if(conns!=null && conns.size()>0){
    		IConnection conn = conns.get(0);
    		if(conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)){ 
    			String removeHeader = ElementParameterParser.getValue(node, "__REMOVE_HEADER__"); // add for feature7788
        		String encoding = ElementParameterParser.getValue(node, "__ENCODING__");
				boolean isAllowEmpty = ("true").equals(ElementParameterParser.getValue(node, "__CREATE_EMPTY_ELEMENT__"));
                boolean expandEmptyElm = ("true").equals(ElementParameterParser.getValue(node, "__EXPAND_EMPTY_ELM__"));
        		String mode = ElementParameterParser.getValue(node, "__GENERATION_MODE__");
        		List<Map<String, String>> rootTable = 
                	(List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__ROOT__");
                List<Map<String, String>> groupTable = 
                	(List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__GROUP__");
                List<Map<String, String>> loopTable = 
                	(List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__LOOP__");
                boolean storeFlow = ("true").equals(ElementParameterParser.getValue(node, "__STORE_FLOW__"));
                boolean isCompactFormat = ("true").equals(ElementParameterParser.getValue(node, "__COMPACT_FORMAT__"));

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    if(storeFlow){
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    }
    stringBuffer.append(TEXT_10);
    stringBuffer.append(virtualTargetCid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(virtualTargetCid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(virtualTargetCid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(virtualTargetCid);
    stringBuffer.append(TEXT_14);
    if(!storeFlow){
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    } else {
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    }
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    
				// *** generation mode init ***
				if(("Dom4j").equals(mode)){

    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    if(isCompactFormat) {
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    } else {
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    }
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_42);
    if(isAllowEmpty && expandEmptyElm){
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    }
    
					int groupSize = 0;
					if(groupTable!=null && groupTable.size()>0){
						for(java.util.Map<String, String> tmpMap : groupTable){
							if(tmpMap.get("ATTRIBUTE").equals("main")){
								groupSize++;
							}
						}
					}

    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(groupSize + 1);
    stringBuffer.append(TEXT_47);
    
				}else if(("Null").equals(mode)){
				    String whiteSpace = "";
        			String rowSeparator = "";
        			if(!isCompactFormat) { // pretty format
        				whiteSpace = "  ";
        				rowSeparator = "\\n";
        			}

    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    
            		if(loopTable.size()>0){
            			String emptyspace = "";
        				String endPath = loopTable.get(0).get("PATH");
        				String[] endTabs = endPath.split("/");
        				for(int len = 1; len<endTabs.length-1;len++){

    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(emptyspace);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(endTabs[len]);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(rowSeparator);
    stringBuffer.append(emptyspace);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(endTabs[len]);
    stringBuffer.append(TEXT_57);
    
	         				emptyspace += whiteSpace;
            			}
        			}

    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    
					if(!("true").equals(removeHeader)){

    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(rowSeparator);
    stringBuffer.append(TEXT_72);
    
					}

    stringBuffer.append(TEXT_73);
    
				}
			}
		}
	}
}

    stringBuffer.append(TEXT_74);
    return stringBuffer.toString();
  }
}
