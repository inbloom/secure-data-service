package org.talend.designer.codegen.translators.data_quality;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.utils.NodeUtil;
import org.talend.core.model.process.EConnectionType;

public class TSchemaComplianceCheckBeginJava
{
  protected static String nl;
  public static synchronized TSchemaComplianceCheckBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSchemaComplianceCheckBeginJava result = new TSchemaComplianceCheckBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "        try {" + NL + "          if (" + NL + "      \t";
  protected final String TEXT_2 = NL + "        \t";
  protected final String TEXT_3 = ".";
  protected final String TEXT_4 = " != null  && (!\"\".equals(";
  protected final String TEXT_5 = ".";
  protected final String TEXT_6 = ")) ";
  protected final String TEXT_7 = " ";
  protected final String TEXT_8 = NL + "            ";
  protected final String TEXT_9 = ".";
  protected final String TEXT_10 = " != null";
  protected final String TEXT_11 = " " + NL + "            true";
  protected final String TEXT_12 = "        " + NL + "          ) {" + NL + "\t\t\t";
  protected final String TEXT_13 = NL + "\t\t\t\tif(!(\"true\".equals(";
  protected final String TEXT_14 = ".";
  protected final String TEXT_15 = ") || \"false\".equals(";
  protected final String TEXT_16 = ".";
  protected final String TEXT_17 = "))){" + NL + "\t\t\t\t\tthrow new Exception(\"Wrong Boolean type!\");" + NL + "\t\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_18 = NL + "\t\t\t\tif(";
  protected final String TEXT_19 = ".";
  protected final String TEXT_20 = ".toCharArray().length != 1){" + NL + "\t\t\t\t\tthrow new Exception(\"Wrong Character type!\");" + NL + "\t\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_21 = NL + "\t\t\t\t";
  protected final String TEXT_22 = " tester_";
  protected final String TEXT_23 = " = new ";
  protected final String TEXT_24 = "(";
  protected final String TEXT_25 = ".";
  protected final String TEXT_26 = ");" + NL + "\t\t\t";
  protected final String TEXT_27 = NL + "        \t\t";
  protected final String TEXT_28 = " tester_";
  protected final String TEXT_29 = " = new ";
  protected final String TEXT_30 = "();" + NL + "\t\t\t";
  protected final String TEXT_31 = NL + "        \t\t";
  protected final String TEXT_32 = " tester_";
  protected final String TEXT_33 = " = ";
  protected final String TEXT_34 = ".valueOf(";
  protected final String TEXT_35 = ".";
  protected final String TEXT_36 = ");" + NL + "\t\t\t";
  protected final String TEXT_37 = NL + "          }" + NL + "             " + NL + "        } catch(Exception e) {" + NL + "          ifPassedThrough = false;" + NL + "          errorCodeThrough += 2;" + NL + "          errorMessageThrough += \"|wrong type\";" + NL + "        }";
  protected final String TEXT_38 = NL + "        if (";
  protected final String TEXT_39 = ".";
  protected final String TEXT_40 = " != null){";
  protected final String TEXT_41 = NL + "\t\t\thandleBigdecimalPrecision((";
  protected final String TEXT_42 = ".";
  protected final String TEXT_43 = ").toPlainString(), ";
  protected final String TEXT_44 = ", ";
  protected final String TEXT_45 = ");";
  protected final String TEXT_46 = NL + "\t\t  \thandleBigdecimalPrecision(String.valueOf(";
  protected final String TEXT_47 = ".";
  protected final String TEXT_48 = "), ";
  protected final String TEXT_49 = ", ";
  protected final String TEXT_50 = ");";
  protected final String TEXT_51 = NL + "          ifPassedThrough = ifPassedThrough?ifPassed:false;" + NL + "          errorCodeThrough += errorCode;" + NL + "          errorMessageThrough += errorMessage;" + NL + "" + NL + "        }";
  protected final String TEXT_52 = NL + "        if (";
  protected final String TEXT_53 = NL + "          ";
  protected final String TEXT_54 = ".";
  protected final String TEXT_55 = " != null  && (!\"\".equals(";
  protected final String TEXT_56 = ".";
  protected final String TEXT_57 = ")) ";
  protected final String TEXT_58 = " ";
  protected final String TEXT_59 = NL + "          ";
  protected final String TEXT_60 = ".";
  protected final String TEXT_61 = " != null";
  protected final String TEXT_62 = " " + NL + "          true";
  protected final String TEXT_63 = "        " + NL + "        ) {";
  protected final String TEXT_64 = NL + "              if( ";
  protected final String TEXT_65 = ".";
  protected final String TEXT_66 = ".length() > ";
  protected final String TEXT_67 = " )";
  protected final String TEXT_68 = NL + "                ";
  protected final String TEXT_69 = ".";
  protected final String TEXT_70 = " = ";
  protected final String TEXT_71 = ".";
  protected final String TEXT_72 = ".substring(0, ";
  protected final String TEXT_73 = ");";
  protected final String TEXT_74 = NL + "                tmpContentThrough = String.valueOf(";
  protected final String TEXT_75 = ".";
  protected final String TEXT_76 = ");";
  protected final String TEXT_77 = "               " + NL + "                tmpContentThrough = ";
  protected final String TEXT_78 = ".";
  protected final String TEXT_79 = ".toString();";
  protected final String TEXT_80 = NL + "              " + NL + "              if (tmpContentThrough.length() > ";
  protected final String TEXT_81 = ")";
  protected final String TEXT_82 = NL + "                ";
  protected final String TEXT_83 = ".";
  protected final String TEXT_84 = " = ";
  protected final String TEXT_85 = ".";
  protected final String TEXT_86 = ".substring(0, ";
  protected final String TEXT_87 = ");";
  protected final String TEXT_88 = "                   " + NL + "              if (";
  protected final String TEXT_89 = ".";
  protected final String TEXT_90 = ".length() > ";
  protected final String TEXT_91 = ") {" + NL + "                ifPassedThrough = false;" + NL + "                errorCodeThrough += 8;" + NL + "              errorMessageThrough += \"|exceed max length\";" + NL + "              }";
  protected final String TEXT_92 = NL + "                tmpContentThrough = String.valueOf(";
  protected final String TEXT_93 = ".";
  protected final String TEXT_94 = ");              ";
  protected final String TEXT_95 = "               " + NL + "                tmpContentThrough = ";
  protected final String TEXT_96 = ".";
  protected final String TEXT_97 = ".toString();  ";
  protected final String TEXT_98 = NL + "              " + NL + "              if (tmpContentThrough.length() > ";
  protected final String TEXT_99 = ") {" + NL + "                ifPassedThrough = false;" + NL + "                errorCodeThrough += 8;" + NL + "                errorMessageThrough += \"|exceed max length\";" + NL + "              }";
  protected final String TEXT_100 = NL + "        }";
  protected final String TEXT_101 = "  " + NL + "        ifPassedThrough = false;" + NL + "        errorCodeThrough += 2;" + NL + "        errorMessageThrough += \"|Date format not defined\";";
  protected final String TEXT_102 = NL + "          try{                    " + NL + "            if (";
  protected final String TEXT_103 = NL + "              ";
  protected final String TEXT_104 = ".";
  protected final String TEXT_105 = " != null  && (!\"\".equals(";
  protected final String TEXT_106 = ".";
  protected final String TEXT_107 = ")) ";
  protected final String TEXT_108 = " ";
  protected final String TEXT_109 = NL + "              ";
  protected final String TEXT_110 = ".";
  protected final String TEXT_111 = " != null";
  protected final String TEXT_112 = " " + NL + "              true";
  protected final String TEXT_113 = NL + "             ) {";
  protected final String TEXT_114 = "            " + NL + "                 if (!TalendDate.isDate((";
  protected final String TEXT_115 = ".";
  protected final String TEXT_116 = ").toString(), ";
  protected final String TEXT_117 = "))" + NL + "                   throw new IllegalArgumentException(\"Data format not matches\");";
  protected final String TEXT_118 = NL + "                 FastDateParser.getInstance(";
  protected final String TEXT_119 = ", false).parse(";
  protected final String TEXT_120 = ".";
  protected final String TEXT_121 = ");            ";
  protected final String TEXT_122 = NL + "             }" + NL + "          } catch(Exception e){" + NL + "            ifPassedThrough = false;" + NL + "            errorCodeThrough += 2;" + NL + "            errorMessageThrough += \"|wrong DATE pattern or wrong DATE data\";" + NL + "          }";
  protected final String TEXT_123 = NL + "            ifPassedThrough = false;" + NL + "            errorCodeThrough += 2;" + NL + "            errorMessageThrough += \"|wrong DATE pattern or wrong DATE data\";";
  protected final String TEXT_124 = NL + "          ifPassedThrough = false;" + NL + "          errorCodeThrough += 2;" + NL + "          errorMessageThrough += \"|The TYPE of inputting data is error. (one of OBJECT, STRING, DATE)\";";
  protected final String TEXT_125 = NL + "\t\t        // validate nullable (empty as null)" + NL + "\t\t        if ((";
  protected final String TEXT_126 = ".";
  protected final String TEXT_127 = " == null) || (\"\".equals(";
  protected final String TEXT_128 = ".";
  protected final String TEXT_129 = "))) {";
  protected final String TEXT_130 = NL + "\t\t        // validate nullable" + NL + "\t\t        if (";
  protected final String TEXT_131 = ".";
  protected final String TEXT_132 = " == null) {";
  protected final String TEXT_133 = NL + "\t\t        // validate nullable (empty as null)" + NL + "\t\t        if ((";
  protected final String TEXT_134 = ".";
  protected final String TEXT_135 = " == null) || (\"\".equals(";
  protected final String TEXT_136 = ".";
  protected final String TEXT_137 = "))) {";
  protected final String TEXT_138 = NL + "\t\t        // validate nullable (empty as null)" + NL + "\t\t        if ((";
  protected final String TEXT_139 = ".";
  protected final String TEXT_140 = " == null) || (\"\".equals(";
  protected final String TEXT_141 = ".";
  protected final String TEXT_142 = "))) {";
  protected final String TEXT_143 = NL + "\t\t        // validate nullable" + NL + "\t\t        if (";
  protected final String TEXT_144 = ".";
  protected final String TEXT_145 = " == null) {";
  protected final String TEXT_146 = NL + "\t\tifPassedThrough = false;" + NL + "\t\terrorCodeThrough += 4;" + NL + "\t\terrorMessageThrough += \"|empty or null\";                        " + NL + "        }";
  protected final String TEXT_147 = NL + "class RowSetValueUtil_";
  protected final String TEXT_148 = " {" + NL + "" + NL + "  boolean ifPassedThrough = true;" + NL + "  int errorCodeThrough = 0;" + NL + "  String errorMessageThrough = \"\";" + NL + "  int resultErrorCodeThrough = 0;" + NL + "  String resultErrorMessageThrough = \"\";" + NL + "  String tmpContentThrough = null;" + NL + "  " + NL + "\tboolean ifPassed = true;" + NL + "\tint errorCode = 0;" + NL + "\tString errorMessage = \"\";" + NL + "\tvoid handleBigdecimalPrecision(String data, int iPrecision, int maxLength){" + NL + "\t\t//number of digits before the decimal point(ignoring frontend zeroes)" + NL + "\t\tint len1 = 0;" + NL + "\t\tint len2 = 0;" + NL + "\t\tifPassed = true;" + NL + "\t\terrorCode = 0;" + NL + "\t\terrorMessage = \"\";" + NL + "\t\tif(data.startsWith(\"-\")){" + NL + "\t\t\tdata = data.substring(1);" + NL + "\t\t}" + NL + "\t\tdata = org.apache.commons.lang.StringUtils.stripStart(data, \"0\");" + NL + "\t\t  " + NL + "\t\tif(data.indexOf(\".\") >= 0){" + NL + "\t\t\tlen1 = data.indexOf(\".\");    " + NL + "\t\t    data = org.apache.commons.lang.StringUtils.stripEnd(data, \"0\");" + NL + "\t\t    len2 = data.length() - (len1 + 1);" + NL + "\t\t}else{" + NL + "\t\t    len1 = data.length();" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tif (iPrecision < len2) {" + NL + "\t\t\tifPassed = false;" + NL + "\t\t\terrorCode += 8;" + NL + "\t\t\terrorMessage += \"|precision Non-matches\";" + NL + "\t\t} else if (maxLength < len1 + iPrecision) {" + NL + "\t\t\tifPassed = false;" + NL + "\t\t\terrorCode += 8;" + NL + "\t\t\terrorMessage += \"|invalid Length setting is unsuitable for Precision\";" + NL + "\t\t}" + NL + "\t}" + NL + "\tint handleErrorCode(int errorCode, int resultErrorCode){" + NL + "\t\tif (errorCode > 0) {" + NL + "\t\t\tif (resultErrorCode > 0) {" + NL + "\t\t\t\tresultErrorCode = 16;" + NL + "\t\t\t} else {" + NL + "\t\t\t\tresultErrorCode = errorCode;" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t\treturn resultErrorCode;" + NL + "\t}" + NL + "\tString handleErrorMessage(String errorMessage, String resultErrorMessage, String columnLabel){" + NL + "\t\tif (errorMessage.length() > 0) {" + NL + "\t\t\tif (resultErrorMessage.length() > 0) {" + NL + "\t\t\t\tresultErrorMessage += \";\"+ errorMessage.replaceFirst(\"\\\\|\", columnLabel);" + NL + "\t\t\t} else {" + NL + "\t\t\t\tresultErrorMessage = errorMessage.replaceFirst(\"\\\\|\", columnLabel);" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t\treturn resultErrorMessage;" + NL + "\t}" + NL + "\t  " + NL + "  void reset(){" + NL + "      ifPassedThrough = true;" + NL + "      errorCodeThrough = 0;" + NL + "      errorMessageThrough = \"\";" + NL + "      resultErrorCodeThrough = 0;" + NL + "      resultErrorMessageThrough = \"\";" + NL + "      tmpContentThrough = null;" + NL + "      " + NL + "      ifPassed = true;" + NL + "\t  errorCode = 0;" + NL + "\t  errorMessage = \"\";" + NL + "  }";
  protected final String TEXT_149 = NL + "\tvoid setRowValue_";
  protected final String TEXT_150 = "(";
  protected final String TEXT_151 = "Struct ";
  protected final String TEXT_152 = ") {";
  protected final String TEXT_153 = NL + "    resultErrorCodeThrough = handleErrorCode(errorCodeThrough,resultErrorCodeThrough);" + NL + "    errorCodeThrough = 0;" + NL + "    resultErrorMessageThrough = handleErrorMessage(errorMessageThrough,resultErrorMessageThrough,\"";
  protected final String TEXT_154 = ":\");" + NL + "    errorMessageThrough = \"\";";
  protected final String TEXT_155 = NL + "\t}";
  protected final String TEXT_156 = NL + "\t}";
  protected final String TEXT_157 = NL + "}" + NL + "RowSetValueUtil_";
  protected final String TEXT_158 = " rsvUtil_";
  protected final String TEXT_159 = " = new RowSetValueUtil_";
  protected final String TEXT_160 = "();";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
final INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

    /*in shema:*/
    List<? extends IConnection> listInConns = node.getIncomingConnections();
    String sInConnName = null;
    IConnection inConn = null;
    List<IMetadataColumn> listInColumns = null;
    
    if (listInConns != null && listInConns.size() > 0) {
      IConnection inConnTemp = listInConns.get(0);
      sInConnName = inConnTemp.getName();
      if (inConnTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)){
      	inConn = inConnTemp;
        listInColumns = inConnTemp.getMetadataTable().getListColumns();
      }
	}
	String inConnName = null;
	
	class FindConnStartConn{
		IConnection findStartConn(IConnection conn){
			INode node = conn.getSource();
			if(node.isSubProcessStart() || !(NodeUtil.isDataAutoPropagated(node))){
				return conn;
			}
			List<? extends IConnection> listInConns = node.getIncomingConnections();
			IConnection inConnTemp = null;
			if (listInConns != null && listInConns.size() > 0) {
              inConnTemp = listInConns.get(0);
              if (inConnTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)){
                return findStartConn(inConnTemp);
              }
        	}
        	return null;
		}
	}
	if(inConn != null){
		FindConnStartConn finder = new FindConnStartConn();
    	IConnection startConn = finder.findStartConn(inConn);
    	if(startConn!=null){
    		inConnName = startConn.getName();
    	}
	}
	
  /* get the schema of itself (maybe no output flow)*/
  List<IMetadataColumn> listColumsToTest = node.getMetadataList().get(0).getListColumns();

  String anotherChecked = ElementParameterParser.getValue(node, "__CHECK_ANOTHER__");
  String checkAll = ElementParameterParser.getValue(node, "__CHECK_ALL__");    
  final boolean bIsTrim = "true".equals( ElementParameterParser.getValue(node, "__SUB_STRING__") );    
  final boolean useFasteDateChecker = "true".equals( ElementParameterParser.getValue(node, "__FAST_DATE_CHECK__") );
  final boolean emptyIsNull = "true".equals(ElementParameterParser.getValue(node, "__EMPTY_IS_NULL__"));
  final boolean allEmptyAreNull = "true".equals(ElementParameterParser.getValue(node, "__ALL_EMPTY_ARE_NULL__"));

  class SchemaChecker {
  	boolean anotherChecked = "true".equals(ElementParameterParser.getValue(node, "__CHECK_ANOTHER__"));
    public void  testDataType(boolean _bNullable, String _sInConnName, IMetadataColumn metadataColumn, String typeSelected, String cid) {
      JavaType javaType = JavaTypesManager.getJavaTypeFromId(metadataColumn.getTalendType());
      boolean isPrimitive = JavaTypesManager.isJavaPrimitiveType( javaType, metadataColumn.isNullable());
      String colName = metadataColumn.getLabel();
      
      if (javaType == JavaTypesManager.OBJECT || javaType == JavaTypesManager.STRING) {
      
    stringBuffer.append(TEXT_1);
    if (_bNullable){
    stringBuffer.append(TEXT_2);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_6);
    }else if (!isPrimitive){
    stringBuffer.append(TEXT_7);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_10);
    }else{
    stringBuffer.append(TEXT_11);
    }
    stringBuffer.append(TEXT_12);
     if(typeSelected.equals("Boolean") ) {
    stringBuffer.append(TEXT_13);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_17);
     }else if(typeSelected.equals("Character") ) {
    stringBuffer.append(TEXT_18);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_20);
     }else if(typeSelected.equals("BigDecimal") ) {
    stringBuffer.append(TEXT_21);
    stringBuffer.append(typeSelected);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(typeSelected);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_26);
     } else if(typeSelected.equals("Object")){
    stringBuffer.append(TEXT_27);
    stringBuffer.append(typeSelected);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(typeSelected);
    stringBuffer.append(TEXT_30);
     } else {
    stringBuffer.append(TEXT_31);
    stringBuffer.append(typeSelected);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(typeSelected);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_36);
    }
    stringBuffer.append(TEXT_37);
    
      }
    }
    
    public void testPrecision(int _maxLength, int iPrecision, String _sInConnName, IMetadataColumn metadataColumn, String typeSelected, String cid) {
      JavaType javaType = JavaTypesManager.getJavaTypeFromId(metadataColumn.getTalendType());
      String colName = metadataColumn.getLabel();
      boolean needCheck = false;
      if(anotherChecked) {
      	if("BigDecimal".equalsIgnoreCase(typeSelected)) {
      		needCheck = true;
      	}
      } else if (javaType == JavaTypesManager.BIGDECIMAL) {
        /* NULLable, in case input value is Null, do nothing... 
           Non-NULLable, 
             (1) in case input value is Non-null, go into...; 
             (2) in case input value is Null, do nothing and warning by NULL-CHECKER.
        */
        /*
          if precision value is not empty or Null, checking "Precision" at first, if passed then checking "Length"
        */
        	needCheck = true;
       }
       if(needCheck) {

    stringBuffer.append(TEXT_38);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_40);
    
			if(javaType == JavaTypesManager.BIGDECIMAL) {

    stringBuffer.append(TEXT_41);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(iPrecision);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(_maxLength);
    stringBuffer.append(TEXT_45);
    
			} else {

    stringBuffer.append(TEXT_46);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(iPrecision);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(_maxLength);
    stringBuffer.append(TEXT_50);
    			}
    stringBuffer.append(TEXT_51);
    
      }
    }
    
    public void testDataLength(boolean _bNullable, String _sInConnName, IMetadataColumn metadataColumn, int maxLength, String cid) {
      JavaType javaType = JavaTypesManager.getJavaTypeFromId(metadataColumn.getTalendType());
      boolean isPrimitive = JavaTypesManager.isJavaPrimitiveType(javaType, metadataColumn.isNullable());
      boolean bIsStringType = (javaType == JavaTypesManager.STRING), bIsIntegerType = (javaType == JavaTypesManager.INTEGER);
      String colName = metadataColumn.getLabel();
      
      if (maxLength > 0 && ( bIsStringType || bIsIntegerType )){
      
    stringBuffer.append(TEXT_52);
    if (_bNullable){
    stringBuffer.append(TEXT_53);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_57);
    }else if (!isPrimitive){
    stringBuffer.append(TEXT_58);
    stringBuffer.append(TEXT_59);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_61);
    }else {
    stringBuffer.append(TEXT_62);
    }
    stringBuffer.append(TEXT_63);
    
          if ( bIsTrim ){
            if (bIsStringType) {
            
    stringBuffer.append(TEXT_64);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(maxLength);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(maxLength);
    stringBuffer.append(TEXT_73);
    
            } else if ( bIsIntegerType ){
              String generatedType = JavaTypesManager.getTypeToGenerate(metadataColumn.getTalendType(), metadataColumn.isNullable());
              if ("int".equals(generatedType)) {
              
    stringBuffer.append(TEXT_74);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_76);
    
              } else{
              
    stringBuffer.append(TEXT_77);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_79);
    
              }
              
    stringBuffer.append(TEXT_80);
    stringBuffer.append(maxLength);
    stringBuffer.append(TEXT_81);
    stringBuffer.append(TEXT_82);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_83);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_84);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_85);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_86);
    stringBuffer.append(maxLength);
    stringBuffer.append(TEXT_87);
    
            }
          } else{          
            if (bIsStringType) {
            
    stringBuffer.append(TEXT_88);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_89);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(maxLength);
    stringBuffer.append(TEXT_91);
    
            } else if (bIsIntegerType) {
              String generatedType = JavaTypesManager.getTypeToGenerate(metadataColumn.getTalendType(), metadataColumn.isNullable());
              if ("int".equals(generatedType)) {
              
    stringBuffer.append(TEXT_92);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_93);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_94);
    
              } else {
              
    stringBuffer.append(TEXT_95);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_96);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_97);
    
              }
              
    stringBuffer.append(TEXT_98);
    stringBuffer.append(maxLength);
    stringBuffer.append(TEXT_99);
    
            }
          }
          
    stringBuffer.append(TEXT_100);
     
      }
    }
  
    public void  testDate(boolean _bNullable, String _sInConnName, IMetadataColumn metadataColumn, String pattern, String cid) {
      JavaType javaType = JavaTypesManager.getJavaTypeFromId(metadataColumn.getTalendType());
      boolean isPrimitive = JavaTypesManager.isJavaPrimitiveType( javaType, metadataColumn.isNullable());
      String colName = metadataColumn.getLabel();

      if ("".equals(pattern)){
      
    stringBuffer.append(TEXT_101);
    
      } else {
      
        if (javaType == JavaTypesManager.OBJECT || javaType == JavaTypesManager.STRING) {
        
    stringBuffer.append(TEXT_102);
    if (_bNullable){
    stringBuffer.append(TEXT_103);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_104);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_105);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_106);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_107);
    }else if (!isPrimitive){
    stringBuffer.append(TEXT_108);
    stringBuffer.append(TEXT_109);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_110);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_111);
    }else {
    stringBuffer.append(TEXT_112);
    }
    stringBuffer.append(TEXT_113);
    if (!useFasteDateChecker) {
    stringBuffer.append(TEXT_114);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_115);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_116);
    stringBuffer.append(pattern);
    stringBuffer.append(TEXT_117);
    } else {
    stringBuffer.append(TEXT_118);
    stringBuffer.append(pattern);
    stringBuffer.append(TEXT_119);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_120);
    stringBuffer.append(colName);
    stringBuffer.append(TEXT_121);
    }
    stringBuffer.append(TEXT_122);
    
        // date type need check also (some inputting data not legal, beacause original data is not suite with pattern and has be converted)
        } else if (javaType == JavaTypesManager.DATE){
          if (!metadataColumn.getPattern().equals(pattern)){
          
    stringBuffer.append(TEXT_123);
    
          }
        } else{
        
    stringBuffer.append(TEXT_124);
    
        }
      }
    }

    public void testNull(String _sInConnName, IMetadataColumn metadataColumn, String cid, List<Map<String, String>> list){
	  List<String> listEmptyAsNull = new ArrayList<String>();
	  for(Map<String, String> map : list){
	  	if("true".equals(map.get("EMPTY_NULL"))){
	  		listEmptyAsNull.add(map.get("SCHEMA_COLUMN"));
	  	}
	  }
      boolean isPrimitive = JavaTypesManager.isJavaPrimitiveType(metadataColumn.getTalendType(), metadataColumn.isNullable());
      if (!isPrimitive){
      	if(emptyIsNull && !allEmptyAreNull){ //for the migration task
      		if(listEmptyAsNull.contains(metadataColumn.getLabel())){

    stringBuffer.append(TEXT_125);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_126);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_127);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_128);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_129);
    
			}else{

    stringBuffer.append(TEXT_130);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_131);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_132);
    
			}
		}else{
      		if(allEmptyAreNull){

    stringBuffer.append(TEXT_133);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_134);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_135);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_136);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_137);
    
			}else if(listEmptyAsNull.contains(metadataColumn.getLabel())){

    stringBuffer.append(TEXT_138);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_139);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_140);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_141);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_142);
    
			}else{

    stringBuffer.append(TEXT_143);
    stringBuffer.append(_sInConnName);
    stringBuffer.append(TEXT_144);
    stringBuffer.append(metadataColumn.getLabel());
    stringBuffer.append(TEXT_145);
    
			}
		}

    stringBuffer.append(TEXT_146);
    
      }
    }
  }

  SchemaChecker checker = new SchemaChecker();    
  List<Map<String, String>> listCheckedColumns = (List<Map<String, String>>)ElementParameterParser.getObjectValue(node, "__CHECKCOLS__");
  boolean bNeedReferSchema = false;
  
  if ("true".equals(anotherChecked)){
    if (node.getMetadataFromConnector("OTHER") != null)
      listColumsToTest = node.getMetadataFromConnector("OTHER").getListColumns();
  } else if ("true".equals(checkAll)){
    ;
  } else{
    bNeedReferSchema = true;
  }

    stringBuffer.append(TEXT_147);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_148);
    
  for (IMetadataColumn inColumn : listInColumns) {
    int iInColIndex = listInColumns.indexOf(inColumn);
    
    if(iInColIndex % 100 == 0){

    stringBuffer.append(TEXT_149);
    stringBuffer.append((iInColIndex/100) );
    stringBuffer.append(TEXT_150);
    stringBuffer.append(inConnName!=null?inConnName:sInConnName );
    stringBuffer.append(TEXT_151);
    stringBuffer.append(sInConnName );
    stringBuffer.append(TEXT_152);
    
    }
    // when using another schema, it's size may less than listInColumns
    if (iInColIndex >= listColumsToTest.size()){
      break;
    }
    
    Object pre_iPrecision = null;
    String sInColumnName = inColumn.getLabel(), sTestColName = null, sTestColType = null, sTestColPattern = null;
    boolean bNullable = true, bMaxLenLimited = true;    
    /* use setting of tSchemaComplianceCheck schema (it is synchronize with inputting schema, but length value can be different) */    
    Object pre_maxLength = listColumsToTest.get(iInColIndex).getLength();
    int maxLength = (pre_maxLength == null) ? 0 : Integer.parseInt(pre_maxLength.toString());
      
    if (bNeedReferSchema) {
      Map<String, String> checkedColumn = listCheckedColumns.get(iInColIndex);
      sTestColName = checkedColumn.get("SCHEMA_COLUMN");
      sTestColType = checkedColumn.get("SELECTED_TYPE");
      sTestColPattern = checkedColumn.get("DATEPATTERN");
      bNullable = "true".equals(checkedColumn.get("NULLABLE"));
      bMaxLenLimited = "true".equals(checkedColumn.get("MAX_LENGTH"));
       
    } else{
      IMetadataColumn schemaColumn = listColumsToTest.get(iInColIndex);
      sTestColName = schemaColumn.getLabel();
      sTestColType = JavaTypesManager.getTypeToGenerate(schemaColumn.getTalendType(), true);
      sTestColPattern = schemaColumn.getPattern();
      bNullable = schemaColumn.isNullable();
      pre_iPrecision = schemaColumn.getPrecision();
    }

    // NULL checking
    if (!bNullable){
      List<Map<String, String>> list = (List<Map<String, String>>)ElementParameterParser.getObjectValue(node, "__EMPTY_NULL_TABLE__");
      checker.testNull(sInConnName, inColumn, cid, list);
    }
      
    // type checking
    if (sTestColType != null){
      if (sTestColType.indexOf("Date") >= 0){
        checker.testDate(bNullable, sInConnName, inColumn, sTestColPattern, cid); 
      } else{         
        checker.testDataType(bNullable, sInConnName, inColumn, sTestColType, cid);
      }
    }
    // length checking
    if (bMaxLenLimited){
      checker.testDataLength(bNullable, sInConnName, inColumn, maxLength, cid);
    }
    
    // precision checking
    if (pre_iPrecision != null){
      checker.testPrecision(maxLength, Integer.parseInt(pre_iPrecision.toString()), sInConnName, inColumn, sTestColType, cid);
    }
    
    stringBuffer.append(TEXT_153);
    stringBuffer.append(inColumn.getLabel());
    stringBuffer.append(TEXT_154);
    
    if((iInColIndex + 1) % 100 == 0){

    stringBuffer.append(TEXT_155);
    
    }
  } // end for
  if(listInColumns.size() > 0 && listInColumns.size() % 100 > 0){

    stringBuffer.append(TEXT_156);
    
  }

    stringBuffer.append(TEXT_157);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_158);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_159);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_160);
    return stringBuffer.toString();
  }
}
