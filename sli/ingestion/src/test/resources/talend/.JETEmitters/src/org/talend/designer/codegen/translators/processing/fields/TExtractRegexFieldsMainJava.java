package org.talend.designer.codegen.translators.processing.fields;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.ArrayList;

public class TExtractRegexFieldsMainJava
{
  protected static String nl;
  public static synchronized TExtractRegexFieldsMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TExtractRegexFieldsMainJava result = new TExtractRegexFieldsMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\t\t";
  protected final String TEXT_3 = " = null;";
  protected final String TEXT_4 = NL + "    \t\tjava.util.regex.Matcher matcher_";
  protected final String TEXT_5 = " = pattern_";
  protected final String TEXT_6 = ".matcher(";
  protected final String TEXT_7 = ".";
  protected final String TEXT_8 = ");";
  protected final String TEXT_9 = NL + "\t\t\t    boolean isNotMatch_";
  protected final String TEXT_10 = " = false;" + NL + "\t\t\t    if(!matcher_";
  protected final String TEXT_11 = ".find()){" + NL + "\t\t\t        ";
  protected final String TEXT_12 = " = new ";
  protected final String TEXT_13 = "Struct();" + NL + "\t\t\t        ";
  protected final String TEXT_14 = ".errorMessage = ";
  protected final String TEXT_15 = ".";
  protected final String TEXT_16 = " + \" doesn't match regex:\" + ";
  protected final String TEXT_17 = ";" + NL + "\t\t\t        isNotMatch_";
  protected final String TEXT_18 = " = true;";
  protected final String TEXT_19 = NL + "                                ";
  protected final String TEXT_20 = ".";
  protected final String TEXT_21 = " = ";
  protected final String TEXT_22 = ".";
  protected final String TEXT_23 = ";";
  protected final String TEXT_24 = NL + "\t\t\t    }" + NL + "\t\t\t    matcher_";
  protected final String TEXT_25 = ".reset();";
  protected final String TEXT_26 = NL + "    " + NL + "\t\t    int groupCount_";
  protected final String TEXT_27 = " = matcher_";
  protected final String TEXT_28 = ".groupCount();" + NL + "\t\t    while(";
  protected final String TEXT_29 = " matcher_";
  protected final String TEXT_30 = ".find()){" + NL + "\t\t";
  protected final String TEXT_31 = NL;
  protected final String TEXT_32 = NL + "try{";
  protected final String TEXT_33 = NL + "    ";
  protected final String TEXT_34 = " = new ";
  protected final String TEXT_35 = "Struct();";
  protected final String TEXT_36 = NL + "    \t\t\t";
  protected final String TEXT_37 = ".";
  protected final String TEXT_38 = " = ";
  protected final String TEXT_39 = ".";
  protected final String TEXT_40 = ";";
  protected final String TEXT_41 = NL + NL + "String temp_";
  protected final String TEXT_42 = " = null;";
  protected final String TEXT_43 = NL + "\t\t\t";
  protected final String TEXT_44 = ".";
  protected final String TEXT_45 = " = groupCount_";
  protected final String TEXT_46 = " <= ";
  protected final String TEXT_47 = "? \"\" : matcher_";
  protected final String TEXT_48 = ".group(";
  protected final String TEXT_49 = ");";
  protected final String TEXT_50 = NL + "\t\t    temp_";
  protected final String TEXT_51 = " = groupCount_";
  protected final String TEXT_52 = " <= ";
  protected final String TEXT_53 = "? \"\" : matcher_";
  protected final String TEXT_54 = ".group(";
  protected final String TEXT_55 = " + 1);" + NL + "\t\t    if(temp_";
  protected final String TEXT_56 = ".length() > 0) {";
  protected final String TEXT_57 = NL + "        \t\t\t";
  protected final String TEXT_58 = ".";
  protected final String TEXT_59 = " = temp_";
  protected final String TEXT_60 = ".getBytes();";
  protected final String TEXT_61 = NL + "        \t\t\t\t";
  protected final String TEXT_62 = ".";
  protected final String TEXT_63 = " = ParserUtils.parseTo_Date(temp_";
  protected final String TEXT_64 = ", ";
  protected final String TEXT_65 = ", false);";
  protected final String TEXT_66 = NL + "        \t\t\t\t";
  protected final String TEXT_67 = ".";
  protected final String TEXT_68 = " = ParserUtils.parseTo_Date(temp_";
  protected final String TEXT_69 = ", ";
  protected final String TEXT_70 = ");";
  protected final String TEXT_71 = NL + "        \t\t\t";
  protected final String TEXT_72 = ".";
  protected final String TEXT_73 = " = ParserUtils.parseTo_";
  protected final String TEXT_74 = "(temp_";
  protected final String TEXT_75 = ");";
  protected final String TEXT_76 = NL + "    \t\t} else {";
  protected final String TEXT_77 = NL + "        \t\t\tthrow new RuntimeException(\"Value is empty for column : '";
  protected final String TEXT_78 = "' in '";
  protected final String TEXT_79 = "' connection, value is invalid or this column should be nullable or have a default value.\");";
  protected final String TEXT_80 = NL + "        \t\t\t";
  protected final String TEXT_81 = ".";
  protected final String TEXT_82 = " = ";
  protected final String TEXT_83 = ";";
  protected final String TEXT_84 = NL + "    \t\t}";
  protected final String TEXT_85 = NL + "     int filedsum_";
  protected final String TEXT_86 = " = groupCount_";
  protected final String TEXT_87 = ";" + NL + "     if(filedsum_";
  protected final String TEXT_88 = " < ";
  protected final String TEXT_89 = "){" + NL + "         throw new RuntimeException(\"Column(s) missing\");" + NL + "     } else if(filedsum_";
  protected final String TEXT_90 = " > ";
  protected final String TEXT_91 = ") {" + NL + "         throw new RuntimeException(\"Too many columns\");" + NL + "     }";
  protected final String TEXT_92 = NL + "    \t";
  protected final String TEXT_93 = " = null;";
  protected final String TEXT_94 = NL + "\t}catch(Exception ex_";
  protected final String TEXT_95 = "){";
  protected final String TEXT_96 = NL + "    \tthrow(ex_";
  protected final String TEXT_97 = ");";
  protected final String TEXT_98 = NL + "    \t\t";
  protected final String TEXT_99 = " = new ";
  protected final String TEXT_100 = "Struct();";
  protected final String TEXT_101 = NL + "    \t\t\t";
  protected final String TEXT_102 = ".";
  protected final String TEXT_103 = " = ";
  protected final String TEXT_104 = ".";
  protected final String TEXT_105 = ";";
  protected final String TEXT_106 = NL + "\t\t    ";
  protected final String TEXT_107 = ".errorMessage = ex_";
  protected final String TEXT_108 = ".getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_109 = ";" + NL + "\t\t    ";
  protected final String TEXT_110 = " = null;";
  protected final String TEXT_111 = NL + "\t\t    System.err.println(ex_";
  protected final String TEXT_112 = ".getMessage());" + NL + "\t\t    ";
  protected final String TEXT_113 = " = null;";
  protected final String TEXT_114 = NL + "    \t\t";
  protected final String TEXT_115 = ".errorMessage = ex_";
  protected final String TEXT_116 = ".getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_117 = ";";
  protected final String TEXT_118 = NL + "}";
  protected final String TEXT_119 = NL + "}" + NL + "\tisNotMatch_";
  protected final String TEXT_120 = " = false;";
  protected final String TEXT_121 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String field = ElementParameterParser.getValue(node, "__FIELD__");
String reg = ElementParameterParser.getValue(node,"__REGEX__");

String dieOnErrorStr = ElementParameterParser.getValue(node, "__DIE_ON_ERROR__");
boolean dieOnError = (dieOnErrorStr!=null&&!("").equals(dieOnErrorStr))?("true").equals(dieOnErrorStr):false;

String checkNumStr = ElementParameterParser.getValue(node, "__CHECK_FIELDS_NUM__");
boolean checkNum = (checkNumStr!=null&&!("").equals(checkNumStr))?("true").equals(checkNumStr):false;

IConnection inConn = null;
List< ? extends IConnection> inConns = node.getIncomingConnections();
if(inConns!=null){
    for (IConnection conn : inConns) {
        if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
            inConn = conn;
            break;
        }
    }
}

String rejectConnName = "";
List<IMetadataColumn> rejectColumnList = null;
List<? extends IConnection> rejectConns = node.getOutgoingConnections("REJECT");
if(rejectConns != null && rejectConns.size() > 0) {
    IConnection rejectConn = rejectConns.get(0);
    rejectColumnList = rejectConn.getMetadataTable().getListColumns();
    rejectConnName = rejectConn.getName();
}

IConnection outConn = null;
String firstConnName = "";
List< ? extends IConnection> outConns = node.getOutgoingConnections();
if(outConns!=null){
    for (IConnection conn : outConns) {
        if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
            outConn = conn;
            firstConnName = outConn.getName();
            break;
        }
    }
}
boolean needRejectLink = !("").equals(rejectConnName)&&!rejectConnName.equals(firstConnName)&&rejectColumnList != null && rejectColumnList.size() > 0;

if(outConns!=null){
    for (IConnection conn : outConns) {
        if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_3);
    
        }
    }
}

//get field column
//========= Bug 19723 add outConn!=null =========
if(inConn!=null && outConn!=null){
    IMetadataTable inputMetadataTable = inConn.getMetadataTable();
    IMetadataTable outputMetadataTable = outConn.getMetadataTable();
    //outCols below is nerver used after definition
    //int outCols = outputMetadataTable.getListColumns().size();
    for (IMetadataColumn inputCol : inputMetadataTable.getListColumns()) {
        if(inputCol.getLabel().equals(field)){

    stringBuffer.append(TEXT_4);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(inConn.getName());
    stringBuffer.append(TEXT_7);
    stringBuffer.append(field);
    stringBuffer.append(TEXT_8);
    
            if(!"".equals(rejectConnName)) {

    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(inConn.getName());
    stringBuffer.append(TEXT_15);
    stringBuffer.append(field);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(reg);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    
//========= Bug 15631 START =========
//output rows to reject
//when there's no output component,we can't get "outputMetadataTable" through "out conn"
                    for (IMetadataColumn outputRejCol : outputMetadataTable.getListColumns()) {
                        for (IMetadataColumn inputRejCol : inputMetadataTable.getListColumns()) {
                            if( outputRejCol.getLabel().equals( inputRejCol.getLabel()) ){

    stringBuffer.append(TEXT_19);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(outputRejCol.getLabel());
    stringBuffer.append(TEXT_21);
    stringBuffer.append(inConn.getName());
    stringBuffer.append(TEXT_22);
    stringBuffer.append(inputRejCol.getLabel());
    stringBuffer.append(TEXT_23);
    
                            	break;
                            }
                        }
                    }
//========= Bug 15631 END =========

    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    
            }

    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append("".equals(rejectConnName)?"":"isNotMatch_"+cid + "||" );
    stringBuffer.append(TEXT_29);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_30);
    			
            break;
        }
    }
}

//set original columns
List<IMetadataColumn> newColumnList = new ArrayList<IMetadataColumn>();
if(outConn!=null && inConn!=null){

    stringBuffer.append(TEXT_31);
    stringBuffer.append("".equals(rejectConnName)?"":"if(!isNotMatch_"+cid +"){" );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(outConn.getName() );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(outConn.getName() );
    stringBuffer.append(TEXT_35);
    
    IMetadataTable outputMetadataTable = outConn.getMetadataTable();
    IMetadataTable inputMetadataTable = inConn.getMetadataTable();
    for (IMetadataColumn outputCol : outputMetadataTable.getListColumns()) {
        if(outputCol.getLabel().equals(field)){
//========= Bug 15631 =========
//allow to keep copy of original field continue;
        }
        boolean isOirginalColumn = false;
        for (IMetadataColumn inputCol : inputMetadataTable.getListColumns()) {
            if( outputCol.getLabel().equals( inputCol.getLabel()) ){
                isOirginalColumn = true;

    stringBuffer.append(TEXT_36);
    stringBuffer.append(outConn.getName());
    stringBuffer.append(TEXT_37);
    stringBuffer.append(outputCol.getLabel());
    stringBuffer.append(TEXT_38);
    stringBuffer.append(inConn.getName());
    stringBuffer.append(TEXT_39);
    stringBuffer.append(inputCol.getLabel());
    stringBuffer.append(TEXT_40);
    
                break;
            }
        }
        if(!isOirginalColumn){
            if(!("").equals(rejectConnName)&&rejectConnName.equals(firstConnName)
                && (outputCol.getLabel().equals("errorMessage") || outputCol.getLabel().equals("errorCode"))){
            }else{
                newColumnList.add(outputCol);
            }
        }
    }

    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    
    for(int i=0; i<newColumnList.size();i++){
        IMetadataColumn column = newColumnList.get(i);
        String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
        String primitiveTypeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), false);
        JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
        String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
    
        if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT){


    stringBuffer.append(TEXT_43);
    stringBuffer.append(outConn.getName());
    stringBuffer.append(TEXT_44);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_47);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(i + 1 );
    stringBuffer.append(TEXT_49);
    
        }else{

    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_53);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(i );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_56);
    
	            if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {
	            } else if(javaType == JavaTypesManager.BYTE_ARRAY){

    stringBuffer.append(TEXT_57);
    stringBuffer.append(outConn.getName() );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    
	            }else if(javaType == JavaTypesManager.DATE) {
	                if(checkNum){

    stringBuffer.append(TEXT_61);
    stringBuffer.append(outConn.getName() );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_64);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_65);
    
                	}else{

    stringBuffer.append(TEXT_66);
    stringBuffer.append(outConn.getName() );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_69);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_70);
    
	                }
	            } else {

    stringBuffer.append(TEXT_71);
    stringBuffer.append(outConn.getName() );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_73);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_75);
    
            	}

    stringBuffer.append(TEXT_76);
    
	            String defaultValue = JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate, column.getDefault());
	            if(defaultValue == null) {

    stringBuffer.append(TEXT_77);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(outConn.getName() );
    stringBuffer.append(TEXT_79);
    
            	} else {

    stringBuffer.append(TEXT_80);
    stringBuffer.append(outConn.getName() );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(defaultValue );
    stringBuffer.append(TEXT_83);
    
            	}

    stringBuffer.append(TEXT_84);
    
    	}
	}
    
    if(checkNum) {

    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_88);
    stringBuffer.append(newColumnList.size() );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_90);
    stringBuffer.append(newColumnList.size() );
    stringBuffer.append(TEXT_91);
    
    }
    
    if(!("").equals(rejectConnName) && rejectConnName.equals(firstConnName)){

    stringBuffer.append(TEXT_92);
    stringBuffer.append(outConn.getName() );
    stringBuffer.append(TEXT_93);
    
    }

    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_95);
    
    if(dieOnError){

    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_97);
    
    }else{
        if(!("").equals(rejectConnName)&&!rejectConnName.equals(firstConnName)&&rejectColumnList != null && rejectColumnList.size() > 0) {

    stringBuffer.append(TEXT_98);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_100);
    
            for(IMetadataColumn column : outConn.getMetadataTable().getListColumns()) {

    stringBuffer.append(TEXT_101);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_102);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_103);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_104);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_105);
    
            }

    stringBuffer.append(TEXT_106);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_108);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_109);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_110);
    
        } else if(("").equals(rejectConnName)){

    stringBuffer.append(TEXT_111);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_113);
    
        } else if(rejectConnName.equals(firstConnName)){

    stringBuffer.append(TEXT_114);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_116);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_117);
    
        }
    }

    stringBuffer.append(TEXT_118);
    if(!"".equals(rejectConnName)){
    stringBuffer.append(TEXT_119);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_120);
    
}
}

    stringBuffer.append(TEXT_121);
    return stringBuffer.toString();
  }
}
