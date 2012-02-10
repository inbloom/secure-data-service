package org.talend.designer.codegen.translators.processing.fields;

import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.utils.NodeUtil;

public class TNormalizeMainJava
{
  protected static String nl;
  public static synchronized TNormalizeMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TNormalizeMainJava result = new TNormalizeMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "            if(";
  protected final String TEXT_3 = ".";
  protected final String TEXT_4 = " != null && ";
  protected final String TEXT_5 = ".";
  protected final String TEXT_6 = ".trim().length() > 0) {";
  protected final String TEXT_7 = " \t\t\t\t" + NL + "                        com.csvreader.CsvReader reader_";
  protected final String TEXT_8 = " = new com.csvreader.CsvReader(new java.io.StringReader(";
  protected final String TEXT_9 = ".";
  protected final String TEXT_10 = "), '";
  protected final String TEXT_11 = "');" + NL + "                        reader_";
  protected final String TEXT_12 = ".setEscapeMode(com.csvreader.CsvReader.";
  protected final String TEXT_13 = ");" + NL + "                        reader_";
  protected final String TEXT_14 = ".setTextQualifier('";
  protected final String TEXT_15 = "');" + NL + "                        if (reader_";
  protected final String TEXT_16 = ".readRecord()) {" + NL + "                            normalizeRecord_";
  protected final String TEXT_17 = " = reader_";
  protected final String TEXT_18 = ".getValues();" + NL + "                        } else {" + NL + "                            normalizeRecord_";
  protected final String TEXT_19 = " = new String[1];" + NL + "                        }";
  protected final String TEXT_20 = NL + "                        \tnormalizeRecord_";
  protected final String TEXT_21 = " = ";
  protected final String TEXT_22 = ".";
  protected final String TEXT_23 = ".split(";
  protected final String TEXT_24 = ");";
  protected final String TEXT_25 = "               " + NL + "            } else {" + NL + "                normalizeRecord_";
  protected final String TEXT_26 = " = new String[1];" + NL + "            }";
  protected final String TEXT_27 = NL + "            for(int i_";
  protected final String TEXT_28 = " = 0 ; i_";
  protected final String TEXT_29 = " < normalizeRecord_";
  protected final String TEXT_30 = ".length ; i_";
  protected final String TEXT_31 = "++) {" + NL + "                currentRecord_";
  protected final String TEXT_32 = " = new StringBuilder();" + NL + "                nb_line_";
  protected final String TEXT_33 = "++;               ";
  protected final String TEXT_34 = NL + "                                tmp_";
  protected final String TEXT_35 = " = ";
  protected final String TEXT_36 = ".";
  protected final String TEXT_37 = " == null ? null : String.valueOf(";
  protected final String TEXT_38 = ".";
  protected final String TEXT_39 = ".getTime());";
  protected final String TEXT_40 = NL + "                                    tmp_";
  protected final String TEXT_41 = " = ";
  protected final String TEXT_42 = ".";
  protected final String TEXT_43 = " == null ? null : ";
  protected final String TEXT_44 = ".";
  protected final String TEXT_45 = ".toString();";
  protected final String TEXT_46 = NL + "                                    tmp_";
  protected final String TEXT_47 = " = String.valueOf(";
  protected final String TEXT_48 = ".";
  protected final String TEXT_49 = ");";
  protected final String TEXT_50 = NL + "                                tmp_";
  protected final String TEXT_51 = " = ";
  protected final String TEXT_52 = ".";
  protected final String TEXT_53 = " == null ? null : new String(";
  protected final String TEXT_54 = ".";
  protected final String TEXT_55 = ");";
  protected final String TEXT_56 = NL + "                                tmp_";
  protected final String TEXT_57 = " = ";
  protected final String TEXT_58 = ".";
  protected final String TEXT_59 = " == null ? null : String.valueOf(";
  protected final String TEXT_60 = ".";
  protected final String TEXT_61 = ");";
  protected final String TEXT_62 = NL + "                                tmp_";
  protected final String TEXT_63 = " = String.valueOf(";
  protected final String TEXT_64 = ".";
  protected final String TEXT_65 = ");";
  protected final String TEXT_66 = NL + "                            if(tmp_";
  protected final String TEXT_67 = " != null){" + NL + "                                currentRecord_";
  protected final String TEXT_68 = ".append(tmp_";
  protected final String TEXT_69 = " + tmp_";
  protected final String TEXT_70 = ".length());" + NL + "                            }";
  protected final String TEXT_71 = NL + "                            if(normalizeRecord_";
  protected final String TEXT_72 = "[i_";
  protected final String TEXT_73 = "] != null) {" + NL + "                                currentRecord_";
  protected final String TEXT_74 = ".append(normalizeRecord_";
  protected final String TEXT_75 = "[i_";
  protected final String TEXT_76 = "] + normalizeRecord_";
  protected final String TEXT_77 = "[i_";
  protected final String TEXT_78 = "].length());" + NL + "                            }";
  protected final String TEXT_79 = NL + "                    if(!recordSet_";
  protected final String TEXT_80 = ".contains(currentRecord_";
  protected final String TEXT_81 = ".toString())) {" + NL + "                        recordSet_";
  protected final String TEXT_82 = ".add(currentRecord_";
  protected final String TEXT_83 = ".toString());" + NL + "                    } else {" + NL + "                        continue;" + NL + "                    }                    ";
  protected final String TEXT_84 = NL + "                        ";
  protected final String TEXT_85 = ".";
  protected final String TEXT_86 = " = normalizeRecord_";
  protected final String TEXT_87 = "[i_";
  protected final String TEXT_88 = "];";
  protected final String TEXT_89 = NL + "                        ";
  protected final String TEXT_90 = ".";
  protected final String TEXT_91 = " = ";
  protected final String TEXT_92 = ".";
  protected final String TEXT_93 = ";";
  protected final String TEXT_94 = NL;
  protected final String TEXT_95 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {//1
    IMetadataTable metadata = metadatas.get(0);    
    if (metadata!=null) {//2
       
        String cid = node.getUniqueName();
        String normalizeColumn = ElementParameterParser.getValue(node, "__NORMALIZE_COLUMN__");
        String deduplicate = ElementParameterParser.getValue(node, "__DEDUPLICATE__");
        boolean useCSV = ("true").equals(ElementParameterParser.getValue(node, "__CSV_OPTION__"));
        String escapeMode = ElementParameterParser.getValue(node, "__ESCAPE_CHAR__");
        
        String delim = ElementParameterParser.getValue(node, "__ITEMSEPARATOR__");
        if(useCSV){
        	delim = delim.substring(1,delim.length()-1);
        	if(("'").equals(delim)){
        		delim = "\\'";
        	}
        }
        
    	String textEnclosure1 = ElementParameterParser.getValue(node, "__TEXT_ENCLOSURE__");
    	String textEnclosure = textEnclosure1.substring(1,textEnclosure1.length()-1);
    	if ("".equals(textEnclosure)) textEnclosure = "\0";
    	if(("'").equals(textEnclosure)){
    		textEnclosure = "\\'";
    	}        
        
        String incomingConnName = null;
        String outgoingConnName = null;

        List< ? extends IConnection> inConns = node.getIncomingConnections();
        if (inConns != null && !inConns.isEmpty()) {
            IConnection inConn = inConns.get(0);
            incomingConnName = inConn.getName();
        }
        
        List< ? extends IConnection> outConns = NodeUtil.getOutgoingConnections(node, IConnectionCategory.DATA);
        if (outConns != null && !outConns.isEmpty()) {
            IConnection outConn = outConns.get(0);
            outgoingConnName = outConn.getName();
        }
        List< ? extends IConnection> outIterate = NodeUtil.getOutgoingConnections(node, IConnectionCategory.USE_ITERATE);
        List<IMetadataColumn> metadataColumns = metadata.getListColumns();
        if(incomingConnName != null && 
                metadataColumns != null && !metadataColumns.isEmpty()) {//3
            
    stringBuffer.append(TEXT_2);
    stringBuffer.append(incomingConnName );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(normalizeColumn);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(incomingConnName );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(normalizeColumn);
    stringBuffer.append(TEXT_6);
    if(useCSV){
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(incomingConnName );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(normalizeColumn);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(delim );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(escapeMode );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(textEnclosure );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
     } else { 
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(incomingConnName );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(normalizeColumn);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(delim );
    stringBuffer.append(TEXT_24);
    
                    }
                    
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    
            if(!outIterate.isEmpty()||outgoingConnName != null){
            
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
    
                if(outgoingConnName != null ){
                if(("true").equals(deduplicate)) {//check deduplicate start
                    for(int i = 0 ; i < metadataColumns.size() ; i++) {
                        IMetadataColumn metadataColumn = (IMetadataColumn)metadataColumns.get(i);
                        JavaType javaType = JavaTypesManager.getJavaTypeFromId(metadataColumn.getTalendType());
                        String typeName = JavaTypesManager.getTypeToGenerate(metadataColumn.getTalendType(), metadataColumn.isNullable());
                        if(!metadataColumn.getLabel().equals(normalizeColumn)) {
                            if(javaType == JavaTypesManager.DATE) {
                                
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_37);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_39);
    
                            } else if(javaType == JavaTypesManager.BYTE || 
                                    javaType == JavaTypesManager.SHORT || 
                                    javaType == JavaTypesManager.FLOAT ||
                                    javaType == JavaTypesManager.DOUBLE ||
                                    javaType == JavaTypesManager.LONG ||
                                    javaType == JavaTypesManager.INTEGER || 
                                    javaType == JavaTypesManager.BOOLEAN) {
                                if(("Byte").equals(typeName) || 
                                        ("Short").equals(typeName) || 
                                        ("Float").equals(typeName) || 
                                        ("Double").equals(typeName) || 
                                        ("Long").equals(typeName) || 
                                        ("Integer").equals(typeName) || 
                                        ("Boolean").equals(typeName)) {                    
                                    
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_43);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_45);
    
                                } else {
                                    
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_49);
    
                                }
                            } else if(javaType == JavaTypesManager.BYTE_ARRAY) {
                                
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_53);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_55);
    
                            } else if(javaType == JavaTypesManager.STRING){
                                
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_59);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_61);
    
                            } else {
                                
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_63);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_65);
    
                            }
                            
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    
                        } else {
                            
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_78);
    
                        }
                    }
                    
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_83);
    
                }//check deduplicate end                
                for(IMetadataColumn metadataColumn : metadataColumns) {
                    if(metadataColumn.getLabel().equals(normalizeColumn)) {
                        
    stringBuffer.append(TEXT_84);
    stringBuffer.append(outgoingConnName);
    stringBuffer.append(TEXT_85);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_88);
    
                    } else {
                        
    stringBuffer.append(TEXT_89);
    stringBuffer.append(outgoingConnName);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_91);
    stringBuffer.append(incomingConnName);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_93);
    
                    }
                }
                }
                
    stringBuffer.append(TEXT_94);
    
            }
        }
        //3
    }
    //2    
}
//1

    stringBuffer.append(TEXT_95);
    return stringBuffer.toString();
  }
}
