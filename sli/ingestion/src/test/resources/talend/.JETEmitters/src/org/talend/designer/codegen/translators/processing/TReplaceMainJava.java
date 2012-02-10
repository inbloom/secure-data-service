package org.talend.designer.codegen.translators.processing;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;

public class TReplaceMainJava
{
  protected static String nl;
  public static synchronized TReplaceMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TReplaceMainJava result = new TReplaceMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\t\t\t\t\tString searchStr_";
  protected final String TEXT_3 = "_";
  protected final String TEXT_4 = " = ";
  protected final String TEXT_5 = ";";
  protected final String TEXT_6 = NL + "\t\t\t\t\t\tString searchStr_";
  protected final String TEXT_7 = "_";
  protected final String TEXT_8 = " = ";
  protected final String TEXT_9 = " + \"\";";
  protected final String TEXT_10 = NL + "                    \tsearchStr_";
  protected final String TEXT_11 = "_";
  protected final String TEXT_12 = " = org.apache.oro.text.GlobCompiler.globToPerl5(searchStr_";
  protected final String TEXT_13 = "_";
  protected final String TEXT_14 = ".toCharArray(), org.apache.oro.text.GlobCompiler.DEFAULT_MASK);";
  protected final String TEXT_15 = NL + "                    \t\tsearchStr_";
  protected final String TEXT_16 = "_";
  protected final String TEXT_17 = " = \"^\" + searchStr_";
  protected final String TEXT_18 = "_";
  protected final String TEXT_19 = " + \"$\";";
  protected final String TEXT_20 = NL + "                    \t\tsearchStr_";
  protected final String TEXT_21 = "_";
  protected final String TEXT_22 = " = \"(?i)\" + searchStr_";
  protected final String TEXT_23 = "_";
  protected final String TEXT_24 = ";";
  protected final String TEXT_25 = NL + "\t                \t\t";
  protected final String TEXT_26 = ".";
  protected final String TEXT_27 = " = StringUtils.replaceAll(";
  protected final String TEXT_28 = ".";
  protected final String TEXT_29 = ", searchStr_";
  protected final String TEXT_30 = "_";
  protected final String TEXT_31 = ", ";
  protected final String TEXT_32 = ");";
  protected final String TEXT_33 = NL + "\t\t                    ";
  protected final String TEXT_34 = ".";
  protected final String TEXT_35 = " = StringUtils.replaceAll(";
  protected final String TEXT_36 = ".";
  protected final String TEXT_37 = ", searchStr_";
  protected final String TEXT_38 = "_";
  protected final String TEXT_39 = ", ";
  protected final String TEXT_40 = " + \"\");";
  protected final String TEXT_41 = NL + "\t              \t\t\t";
  protected final String TEXT_42 = ".";
  protected final String TEXT_43 = " = StringUtils.replaceAllStrictly(";
  protected final String TEXT_44 = ".";
  protected final String TEXT_45 = ", searchStr_";
  protected final String TEXT_46 = "_";
  protected final String TEXT_47 = ", ";
  protected final String TEXT_48 = ", ";
  protected final String TEXT_49 = ", ";
  protected final String TEXT_50 = ");";
  protected final String TEXT_51 = NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_52 = ".";
  protected final String TEXT_53 = " = StringUtils.replaceAllStrictly(";
  protected final String TEXT_54 = ".";
  protected final String TEXT_55 = ", searchStr_";
  protected final String TEXT_56 = "_";
  protected final String TEXT_57 = ", ";
  protected final String TEXT_58 = " + \"\", ";
  protected final String TEXT_59 = ", ";
  protected final String TEXT_60 = ");";
  protected final String TEXT_61 = NL + "            nb_line_ok_";
  protected final String TEXT_62 = "++;";
  protected final String TEXT_63 = NL + "\t\t\t\t\t";
  protected final String TEXT_64 = ".";
  protected final String TEXT_65 = " = StringUtils.replaceAll(";
  protected final String TEXT_66 = ".";
  protected final String TEXT_67 = ", ";
  protected final String TEXT_68 = ", ";
  protected final String TEXT_69 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_70 = NL + "                    ";
  protected final String TEXT_71 = ".";
  protected final String TEXT_72 = " = StringUtils.replaceAll(";
  protected final String TEXT_73 = ".";
  protected final String TEXT_74 = ", ";
  protected final String TEXT_75 = ", ";
  protected final String TEXT_76 = " + \"\");";
  protected final String TEXT_77 = "                ";
  protected final String TEXT_78 = NL + "                ";
  protected final String TEXT_79 = ".";
  protected final String TEXT_80 = " = ";
  protected final String TEXT_81 = ".";
  protected final String TEXT_82 = ";";
  protected final String TEXT_83 = NL + "    nb_line_";
  protected final String TEXT_84 = "++;";
  protected final String TEXT_85 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
boolean strictMatch = ("true").equals(ElementParameterParser.getValue( node, "__STRICT_MATCH__" ));
String incomingConnName = null;
List<? extends IConnection> inConns = node.getIncomingConnections();
if(inConns != null && inConns.size() > 0) {
    IConnection inConn = inConns.get(0);
    incomingConnName = inConn.getName();
}
List<IMetadataColumn> columnList = null;
List<IMetadataTable> metadataTables = node.getMetadataList();
if(metadataTables != null && metadataTables.size() > 0) {
    IMetadataTable metadataTable = metadataTables.get(0);
    columnList = metadataTable.getListColumns();
}
List<? extends IConnection> outgoingConns = node.getOutgoingConnections();


if(incomingConnName != null && columnList != null && columnList.size() > 0) {
    String advancedMode = ElementParameterParser.getValue( node, "__ADVANCED_MODE__" );
    String simpleMode = ElementParameterParser.getValue( node, "__SIMPLE_MODE__" );
    
    List<Map<String, String>> patterns = null;
          
    //simple mode Replacement    
    if(("true").equals(simpleMode)) {
        List<Map<String, String>> substitutions = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node,"__SUBSTITUTIONS__");
        if(substitutions != null && substitutions.size() > 0) {
        	int i = 0;
            for(Map<String,String> substitution : substitutions) {            	
                String replaceStr = substitution.get("REPLACE_STRING");
                if(replaceStr != null && !("").equals(replaceStr)) {                	
                    String searchStr = substitution.get("SEARCH_PATTERN");
                    i++;
                    if(("null").equals(searchStr.toLowerCase())){

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(searchStr );
    stringBuffer.append(TEXT_5);
    
					}else{

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(searchStr );
    stringBuffer.append(TEXT_9);
    
					}
					if(("true").equals(substitution.get("USE_GLOB"))){

    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_14);
    
					}
					//this component only support strict match from now on (Bug 10232).
					//for old behaviour support
                	if(!strictMatch){
						if(("true").equals(substitution.get("WHOLE_WORD"))){

    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_19);
    						}
						if(("false").equals(substitution.get("CASE_SENSITIVE"))){

    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_24);
    
						}
						if(("null").equals(replaceStr.toLowerCase())){

    stringBuffer.append(TEXT_25);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(substitution.get("INPUT_COLUMN"));
    stringBuffer.append(TEXT_27);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(substitution.get("INPUT_COLUMN"));
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(replaceStr);
    stringBuffer.append(TEXT_32);
    
						}else{

    stringBuffer.append(TEXT_33);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(substitution.get("INPUT_COLUMN"));
    stringBuffer.append(TEXT_35);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(substitution.get("INPUT_COLUMN"));
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(replaceStr);
    stringBuffer.append(TEXT_40);
    
						}
					//for strict match
					}else{
						if(("null").equals(replaceStr.toLowerCase())){
    stringBuffer.append(TEXT_41);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(substitution.get("INPUT_COLUMN"));
    stringBuffer.append(TEXT_43);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(substitution.get("INPUT_COLUMN"));
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(replaceStr);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(("true").equals(substitution.get("WHOLE_WORD")));
    stringBuffer.append(TEXT_49);
    stringBuffer.append(("true").equals(substitution.get("CASE_SENSITIVE")));
    stringBuffer.append(TEXT_50);
    
						}else{

    stringBuffer.append(TEXT_51);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(substitution.get("INPUT_COLUMN"));
    stringBuffer.append(TEXT_53);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(substitution.get("INPUT_COLUMN"));
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(replaceStr);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(("true").equals(substitution.get("WHOLE_WORD")));
    stringBuffer.append(TEXT_59);
    stringBuffer.append(("true").equals(substitution.get("CASE_SENSITIVE")));
    stringBuffer.append(TEXT_60);
    
						}
					}
                }
            }

    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_62);
    
        }
    }
    
    
    
    //advanced mode Replacement
    if(("true").equals(advancedMode)) {
        patterns = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node,"__ADVANCED_SUBST__");
        if(patterns != null && patterns.size() > 0) {
            for(Map<String,String> pattern:patterns){
                String replacePat = pattern.get("REPLACE_COLUMN");
                if(replacePat != null && !("").equals(replacePat)) {
                	if(("null").equals(replacePat.toLowerCase())){

    stringBuffer.append(TEXT_63);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(pattern.get("INPUT_COLUMN"));
    stringBuffer.append(TEXT_65);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(pattern.get("INPUT_COLUMN"));
    stringBuffer.append(TEXT_67);
    stringBuffer.append(pattern.get("SEARCH_COLUMN"));
    stringBuffer.append(TEXT_68);
    stringBuffer.append(pattern.get("REPLACE_COLUMN"));
    stringBuffer.append(TEXT_69);
    }else{
    stringBuffer.append(TEXT_70);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(pattern.get("INPUT_COLUMN"));
    stringBuffer.append(TEXT_72);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(pattern.get("INPUT_COLUMN"));
    stringBuffer.append(TEXT_74);
    stringBuffer.append(pattern.get("SEARCH_COLUMN"));
    stringBuffer.append(TEXT_75);
    stringBuffer.append(pattern.get("REPLACE_COLUMN"));
    stringBuffer.append(TEXT_76);
    }
    stringBuffer.append(TEXT_77);
    
                }
            }
        }
    }  
    
    
    if(outgoingConns != null && outgoingConns.size() > 0) {
        for(IConnection outgoingConn : outgoingConns) {
            for(IMetadataColumn metadataColumn : columnList) {
                
    stringBuffer.append(TEXT_78);
    stringBuffer.append(outgoingConn.getName());
    stringBuffer.append(TEXT_79);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_80);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_81);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_82);
    
            }
        }
    }
    
    
    
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_84);
    
}

    stringBuffer.append(TEXT_85);
    return stringBuffer.toString();
  }
}
