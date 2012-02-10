package org.talend.designer.codegen.translators.file.input;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;

public class TApacheLogInputBeginJava
{
  protected static String nl;
  public static synchronized TApacheLogInputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TApacheLogInputBeginJava result = new TApacheLogInputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "\t" + NL + "\t";
  protected final String TEXT_2 = NL + "\t\tint nb_line_";
  protected final String TEXT_3 = " = 0;" + NL + "        org.talend.fileprocess.TOSDelimitedReader in";
  protected final String TEXT_4 = "=null;" + NL + "        in";
  protected final String TEXT_5 = " = new org.talend.fileprocess.TOSDelimitedReader(";
  protected final String TEXT_6 = ", ";
  protected final String TEXT_7 = ", \"\", \"\\n\", true);" + NL + "        String str";
  protected final String TEXT_8 = ";" + NL + "        int validRowCount";
  protected final String TEXT_9 = "=0;" + NL + "        java.util.StringTokenizer strToken";
  protected final String TEXT_10 = ";" + NL + "        java.util.regex.Pattern pattern";
  protected final String TEXT_11 = " = java.util.regex.Pattern.compile(";
  protected final String TEXT_12 = ");" + NL + "        java.util.regex.Matcher matcher";
  protected final String TEXT_13 = " = null;" + NL + "        while (in";
  protected final String TEXT_14 = ".readRecord()) {" + NL + "            str";
  protected final String TEXT_15 = " =in";
  protected final String TEXT_16 = ".getRowRecord();" + NL + "        \tif((\"\").equals(str";
  protected final String TEXT_17 = ")){" + NL + "        \t\tcontinue;" + NL + "        \t}" + NL + "        \tmatcher";
  protected final String TEXT_18 = " = pattern";
  protected final String TEXT_19 = ".matcher(str";
  protected final String TEXT_20 = ");" + NL + "        \tif(!matcher";
  protected final String TEXT_21 = ".find()){//line data not matched with given regex parameter" + NL + "        \t\tcontinue;" + NL + "        \t}" + NL + "        \tint groupCount";
  protected final String TEXT_22 = "=matcher";
  protected final String TEXT_23 = ".groupCount();" + NL;
  protected final String TEXT_24 = NL + "    \t\t";
  protected final String TEXT_25 = " = null;\t\t\t";
  protected final String TEXT_26 = "\t\t\t" + NL + "\t\t\t" + NL + "\t\t\tboolean lineIsEmpty";
  protected final String TEXT_27 = " = str";
  protected final String TEXT_28 = ".length() == 0;" + NL + "\t\t\t" + NL + "\t\t\tString[] value";
  protected final String TEXT_29 = " = new String[";
  protected final String TEXT_30 = "];" + NL + "\t\t\tString frontChar";
  protected final String TEXT_31 = ",behindChar";
  protected final String TEXT_32 = ";" + NL + "\t\t\tfor(int i=0;i<";
  protected final String TEXT_33 = ";i++){" + NL + "\t\t\t\tvalue";
  protected final String TEXT_34 = "[i] = \"\";" + NL + "\t\t\t\tif(lineIsEmpty";
  protected final String TEXT_35 = "){" + NL + "\t\t\t\t\tcontinue;" + NL + "\t\t\t\t}" + NL + "\t\t\t\tif(i < groupCount";
  protected final String TEXT_36 = "){" + NL + "\t\t\t\t\tvalue";
  protected final String TEXT_37 = "[i] = matcher";
  protected final String TEXT_38 = ".group(i+1);" + NL + "\t\t\t\t}" + NL + "\t\t\t}" + NL + "\t\t\tvalidRowCount";
  protected final String TEXT_39 = "++;" + NL + "\t\t\t" + NL + "\t\t\tboolean whetherReject_";
  protected final String TEXT_40 = " = false;" + NL + "\t\t\t";
  protected final String TEXT_41 = " = new ";
  protected final String TEXT_42 = "Struct();" + NL + "\t\t\ttry {\t\t\t" + NL + "\t\t\t" + NL + "\t\t\t";
  protected final String TEXT_43 = "\t\t\t\t\t" + NL + "\t\t\t\t\t\tif(value";
  protected final String TEXT_44 = "[";
  protected final String TEXT_45 = "].length() > 0) {" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_46 = ".";
  protected final String TEXT_47 = " = value";
  protected final String TEXT_48 = "[";
  protected final String TEXT_49 = "];";
  protected final String TEXT_50 = ".";
  protected final String TEXT_51 = " = ParserUtils.parseTo_Date(value";
  protected final String TEXT_52 = "[";
  protected final String TEXT_53 = "], ";
  protected final String TEXT_54 = ");";
  protected final String TEXT_55 = ".";
  protected final String TEXT_56 = " = value";
  protected final String TEXT_57 = "[";
  protected final String TEXT_58 = "].getBytes(";
  protected final String TEXT_59 = ");";
  protected final String TEXT_60 = ".";
  protected final String TEXT_61 = " = ParserUtils.parseTo_";
  protected final String TEXT_62 = "(value";
  protected final String TEXT_63 = "[";
  protected final String TEXT_64 = "]);";
  protected final String TEXT_65 = "\t\t\t\t\t" + NL + "\t\t\t\t\t\t} else {" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_66 = "throw new RuntimeException(\"Value is empty for column : '";
  protected final String TEXT_67 = "' in '";
  protected final String TEXT_68 = "' connection, value is invalid or this column should be nullable or have a default value.\");";
  protected final String TEXT_69 = ".";
  protected final String TEXT_70 = " = ";
  protected final String TEXT_71 = ";";
  protected final String TEXT_72 = "}" + NL + "\t";
  protected final String TEXT_73 = NL + "\t\t\t\t\t";
  protected final String TEXT_74 = " ";
  protected final String TEXT_75 = " = null; ";
  protected final String TEXT_76 = "\t\t\t\t\t" + NL + "\t\t\t\t\t" + NL + "    } catch (Exception e) {" + NL + "        whetherReject_";
  protected final String TEXT_77 = " = true;";
  protected final String TEXT_78 = NL + "            throw(e);";
  protected final String TEXT_79 = NL + "                    ";
  protected final String TEXT_80 = " = new ";
  protected final String TEXT_81 = "Struct();";
  protected final String TEXT_82 = NL + "                    ";
  protected final String TEXT_83 = ".";
  protected final String TEXT_84 = " = ";
  protected final String TEXT_85 = ".";
  protected final String TEXT_86 = ";";
  protected final String TEXT_87 = NL + "                ";
  protected final String TEXT_88 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_89 = ";";
  protected final String TEXT_90 = NL + "                ";
  protected final String TEXT_91 = " = null;";
  protected final String TEXT_92 = NL + "                System.err.println(e.getMessage());";
  protected final String TEXT_93 = NL + "                ";
  protected final String TEXT_94 = " = null;";
  protected final String TEXT_95 = NL + "            \t";
  protected final String TEXT_96 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_97 = ";";
  protected final String TEXT_98 = NL + "    }\t\t\t\t\t" + NL + "\t\t\t\t\t" + NL + "\t\t\t\t\t";
  protected final String TEXT_99 = NL + "\t\t";
  protected final String TEXT_100 = "if(!whetherReject_";
  protected final String TEXT_101 = ") { ";
  protected final String TEXT_102 = "      " + NL + "             if(";
  protected final String TEXT_103 = " == null){ " + NL + "            \t ";
  protected final String TEXT_104 = " = new ";
  protected final String TEXT_105 = "Struct();" + NL + "             }\t\t\t\t";
  protected final String TEXT_106 = NL + "\t    \t ";
  protected final String TEXT_107 = ".";
  protected final String TEXT_108 = " = ";
  protected final String TEXT_109 = ".";
  protected final String TEXT_110 = ";    \t\t\t\t";
  protected final String TEXT_111 = NL + "\t\t";
  protected final String TEXT_112 = " } ";
  protected final String TEXT_113 = "\t";
  protected final String TEXT_114 = NL + "\t\t\tnb_line_";
  protected final String TEXT_115 = "++;";
  protected final String TEXT_116 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	
	List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas!=null)&&(metadatas.size()>0)) {
		IMetadataTable metadata = metadatas.get(0);
		if (metadata!=null) {

	
	String filename = ElementParameterParser.getValueWithUIFieldKey(node,"__FILENAME__", "FILENAME");
    String encoding = ElementParameterParser.getValue(node,"__ENCODING__");
    String regex = ElementParameterParser.getValue(node, "__REGEX__");
	String dieOnErrorStr = ElementParameterParser.getValue(node, "__DIE_ON_ERROR__");
	boolean dieOnError = (dieOnErrorStr!=null&&!("").equals(dieOnErrorStr))?("true").equals(dieOnErrorStr):false;  

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_6);
    stringBuffer.append( encoding);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append( regex );
    stringBuffer.append(TEXT_12);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_23);
    
	List< ? extends IConnection> conns = node.getOutgoingSortedConnections();

    String rejectConnName = "";
    List<? extends IConnection> rejectConns = node.getOutgoingConnections("REJECT");
    if(rejectConns != null && rejectConns.size() > 0) {
        IConnection rejectConn = rejectConns.get(0);
        rejectConnName = rejectConn.getName();
    }
    List<IMetadataColumn> rejectColumnList = null;
    IMetadataTable metadataTable = node.getMetadataFromConnector("REJECT");
    if(metadataTable != null) {
        rejectColumnList = metadataTable.getListColumns();      
    }

    	if (conns!=null) {
    		if (conns.size()>0) {
    			for (int i=0;i<conns.size();i++) {
    				IConnection connTemp = conns.get(i);
    				if (connTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_24);
    stringBuffer.append(connTemp.getName() );
    stringBuffer.append(TEXT_25);
    
    				}
    			}
    		}
    	}
    	
	String firstConnName = "";
	if (conns!=null) {
		if (conns.size()>0) {
			IConnection conn = conns.get(0);
			firstConnName = conn.getName();
			
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
    stringBuffer.append(TEXT_26);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(metadata.getListColumns().size());
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(metadata.getListColumns().size());
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_42);
    	
				List<IMetadataColumn> listColumns = metadata.getListColumns();
				int sizeListColumns = listColumns.size();
				for (int valueN=0; valueN<sizeListColumns; valueN++) {
					IMetadataColumn column = listColumns.get(valueN);
					String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();

					
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_45);
    
							if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {
								/** end **/
								
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_49);
    
								/** begin **/
							} else if(javaType == JavaTypesManager.DATE) { 
								/** end **/
								
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_53);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_54);
    
								/** begin **/
							} else if(javaType == JavaTypesManager.BYTE_ARRAY) { 
								/** end **/
								
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_59);
    
								/** begin **/
							} else {
								/** end **/
								
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_61);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_64);
    
								/** begin **/
							}
								/** end **/
						
    stringBuffer.append(TEXT_65);
    
							/** begin **/
							String defaultValue = JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate, column.getDefault());
							if(defaultValue == null) {
							/** end **/
								
    stringBuffer.append(TEXT_66);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_68);
    
							/** begin **/
							} else {
							/** end **/
								
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(defaultValue);
    stringBuffer.append(TEXT_71);
    
							/** begin **/
							}
							/** end **/
						
						
    stringBuffer.append(TEXT_72);
    					
	/** begin **/
					}
    stringBuffer.append(TEXT_73);
    if(rejectConnName.equals(firstConnName)) {
    stringBuffer.append(TEXT_74);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_75);
    }
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_77);
    
        if (dieOnError) {
            
    stringBuffer.append(TEXT_78);
    
        } else {
            if(!("").equals(rejectConnName)&&!rejectConnName.equals(firstConnName)&&rejectColumnList != null && rejectColumnList.size() > 0) {

                
    stringBuffer.append(TEXT_79);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_81);
    
                for(IMetadataColumn column : metadata.getListColumns()) {
                    
    stringBuffer.append(TEXT_82);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_83);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_84);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_85);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_86);
    
                }
                
    stringBuffer.append(TEXT_87);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_88);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_91);
    
            } else if(("").equals(rejectConnName)){
                
    stringBuffer.append(TEXT_92);
    stringBuffer.append(TEXT_93);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_94);
    
            } else if(rejectConnName.equals(firstConnName)){
    stringBuffer.append(TEXT_95);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_96);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_97);
    }
        }
        
    stringBuffer.append(TEXT_98);
    
				}
			}
		}
		
		if (conns.size()>0) {	
			boolean isFirstEnter = true;
			for (int i=0;i<conns.size();i++) {
				IConnection conn = conns.get(i);
				if ((conn.getName().compareTo(firstConnName)!=0)&&(conn.getName().compareTo(rejectConnName)!=0)&&(conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA))) {

    stringBuffer.append(TEXT_99);
     if(isFirstEnter) {
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_101);
     isFirstEnter = false; } 
    stringBuffer.append(TEXT_102);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_103);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_104);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_105);
    
			    	 for (IMetadataColumn column: metadata.getListColumns()) {

    stringBuffer.append(TEXT_106);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_107);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_108);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_109);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_110);
    
				 	}
				}
			}

    stringBuffer.append(TEXT_111);
     if(!isFirstEnter) {
    stringBuffer.append(TEXT_112);
     } 
    stringBuffer.append(TEXT_113);
    
		}
	/** end **/
		

    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_115);
    
		}
	}

    stringBuffer.append(TEXT_116);
    return stringBuffer.toString();
  }
}
