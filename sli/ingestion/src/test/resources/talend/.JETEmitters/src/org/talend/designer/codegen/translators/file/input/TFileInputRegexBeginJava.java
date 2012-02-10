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

public class TFileInputRegexBeginJava
{
  protected static String nl;
  public static synchronized TFileInputRegexBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileInputRegexBeginJava result = new TFileInputRegexBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "\t" + NL + "\t";
  protected final String TEXT_2 = NL + "\t\tint nb_line_";
  protected final String TEXT_3 = " = 0;" + NL + "        " + NL + "        int footer_";
  protected final String TEXT_4 = "  = ";
  protected final String TEXT_5 = ";" + NL + "        boolean removeEmptyRow";
  protected final String TEXT_6 = " = ";
  protected final String TEXT_7 = ";" + NL + "        Object source_";
  protected final String TEXT_8 = " = ";
  protected final String TEXT_9 = ";" + NL + "        " + NL + "        org.talend.fileprocess.TOSDelimitedReader in";
  protected final String TEXT_10 = "=null;" + NL + "        if(source_";
  protected final String TEXT_11 = " instanceof String || source_";
  protected final String TEXT_12 = " instanceof java.io.InputStream){" + NL + "        \tin";
  protected final String TEXT_13 = " = new org.talend.fileprocess.TOSDelimitedReader(";
  protected final String TEXT_14 = ", ";
  protected final String TEXT_15 = ", \"\", ";
  protected final String TEXT_16 = ", removeEmptyRow";
  protected final String TEXT_17 = ");" + NL + "        }else{" + NL + "        \tthrow new Exception(\"The source data should be specified as File Path or InputStream or java.io.Reader!\");" + NL + "        }" + NL + "        String str";
  protected final String TEXT_18 = ";" + NL + "        int totalLine";
  protected final String TEXT_19 = "=0,currentLine";
  protected final String TEXT_20 = "=0,beginLine";
  protected final String TEXT_21 = "=0,lastLine";
  protected final String TEXT_22 = "=-1,validRowCount";
  protected final String TEXT_23 = "=0;" + NL + "        int limit";
  protected final String TEXT_24 = "=";
  protected final String TEXT_25 = ";" + NL + "        ";
  protected final String TEXT_26 = NL + "\t\tint header";
  protected final String TEXT_27 = " = ";
  protected final String TEXT_28 = ";" + NL + "\t\tif(header";
  protected final String TEXT_29 = " > 0){" + NL + "\t\t\tbeginLine";
  protected final String TEXT_30 = "=header";
  protected final String TEXT_31 = "+1;" + NL + "\t\t}" + NL + "    \t" + NL + "        if(footer_";
  protected final String TEXT_32 = " > 0){" + NL + "\t\t\twhile (in";
  protected final String TEXT_33 = ".readRecord()) {" + NL + "                str";
  protected final String TEXT_34 = " =in";
  protected final String TEXT_35 = ".getRowRecord();        " + NL + "\t\t\t\ttotalLine";
  protected final String TEXT_36 = "++;" + NL + "\t\t\t}" + NL + "\t\t\tint lastLineTemp";
  protected final String TEXT_37 = " = totalLine";
  protected final String TEXT_38 = " - footer_";
  protected final String TEXT_39 = "   < 0? 0 : totalLine";
  protected final String TEXT_40 = " - footer_";
  protected final String TEXT_41 = " ;" + NL + "\t\t\tif(lastLine";
  protected final String TEXT_42 = " > 0){" + NL + "\t\t\t\tlastLine";
  protected final String TEXT_43 = " = lastLine";
  protected final String TEXT_44 = " < lastLineTemp";
  protected final String TEXT_45 = " ? lastLine";
  protected final String TEXT_46 = " : lastLineTemp";
  protected final String TEXT_47 = "; " + NL + "\t\t\t}else {" + NL + "\t\t\t\tlastLine";
  protected final String TEXT_48 = " = lastLineTemp";
  protected final String TEXT_49 = ";" + NL + "\t\t\t}" + NL + "\t\t  \tin";
  protected final String TEXT_50 = ".close();" + NL + "        \tin";
  protected final String TEXT_51 = " = new org.talend.fileprocess.TOSDelimitedReader(";
  protected final String TEXT_52 = ", ";
  protected final String TEXT_53 = ", \"\", ";
  protected final String TEXT_54 = ", removeEmptyRow";
  protected final String TEXT_55 = ");" + NL + "\t\t}" + NL + "        java.util.StringTokenizer strToken";
  protected final String TEXT_56 = ";" + NL + "        java.util.regex.Pattern pattern";
  protected final String TEXT_57 = " = java.util.regex.Pattern.compile(";
  protected final String TEXT_58 = ");" + NL + "        java.util.regex.Matcher matcher";
  protected final String TEXT_59 = " = null;" + NL + "        while (in";
  protected final String TEXT_60 = ".readRecord()) {" + NL + "            str";
  protected final String TEXT_61 = " =in";
  protected final String TEXT_62 = ".getRowRecord(); " + NL + "        \t" + NL + "       \t\tcurrentLine";
  protected final String TEXT_63 = "++;" + NL + "        \tif(currentLine";
  protected final String TEXT_64 = " < beginLine";
  protected final String TEXT_65 = ") {" + NL + "        \t\tcontinue;" + NL + "        \t}" + NL + "        \tif(lastLine";
  protected final String TEXT_66 = " > -1 && currentLine";
  protected final String TEXT_67 = " > lastLine";
  protected final String TEXT_68 = ") {" + NL + "        \t\tbreak;" + NL + "        \t}" + NL + "        \tif(removeEmptyRow";
  protected final String TEXT_69 = " && (\"\").equals(str";
  protected final String TEXT_70 = ")){" + NL + "        \t\tcontinue;" + NL + "        \t}" + NL + "        \tif(limit";
  protected final String TEXT_71 = "!=-1&& validRowCount";
  protected final String TEXT_72 = " >= limit";
  protected final String TEXT_73 = "){" + NL + "        \t\tbreak;" + NL + "        \t}" + NL + "        \t" + NL + "        \tmatcher";
  protected final String TEXT_74 = " = pattern";
  protected final String TEXT_75 = ".matcher(str";
  protected final String TEXT_76 = ");" + NL + "        \tint groupCount";
  protected final String TEXT_77 = " = 0;" + NL + "        \tboolean isMatch";
  protected final String TEXT_78 = " = matcher";
  protected final String TEXT_79 = ".find(); " + NL + "        \tif(isMatch";
  protected final String TEXT_80 = "){" + NL + "        \tgroupCount";
  protected final String TEXT_81 = "=matcher";
  protected final String TEXT_82 = ".groupCount();" + NL + "\t\t\t}";
  protected final String TEXT_83 = NL + "    \t\t";
  protected final String TEXT_84 = " = null;\t\t\t";
  protected final String TEXT_85 = "\t\t\t" + NL + "\t\t\t" + NL + "\t\t\tboolean lineIsEmpty";
  protected final String TEXT_86 = " = str";
  protected final String TEXT_87 = ".length() == 0;" + NL + "\t\t\t" + NL + "\t\t\tString[] value";
  protected final String TEXT_88 = " = new String[";
  protected final String TEXT_89 = "];" + NL + "\t\t\tString frontChar";
  protected final String TEXT_90 = ",behindChar";
  protected final String TEXT_91 = ";" + NL + "\t\t\tfor(int i=0;i<";
  protected final String TEXT_92 = ";i++){" + NL + "\t\t\t\tvalue";
  protected final String TEXT_93 = "[i] = \"\";" + NL + "\t\t\t\tif(lineIsEmpty";
  protected final String TEXT_94 = "){" + NL + "\t\t\t\t\tcontinue;" + NL + "\t\t\t\t}" + NL + "\t\t\t\tif(i < groupCount";
  protected final String TEXT_95 = "){" + NL + "\t\t\t\t\tvalue";
  protected final String TEXT_96 = "[i] = matcher";
  protected final String TEXT_97 = ".group(i+1);" + NL + "\t\t\t\t}" + NL + "\t\t\t}" + NL + "\t\t\tvalidRowCount";
  protected final String TEXT_98 = "++;" + NL + "\t\t\t" + NL + "\t\t\tboolean whetherReject_";
  protected final String TEXT_99 = " = false;" + NL + "\t\t\t";
  protected final String TEXT_100 = " = new ";
  protected final String TEXT_101 = "Struct();" + NL + "\t\t\ttry {\t\t\t" + NL + "\t\t\tif(!isMatch";
  protected final String TEXT_102 = "){//line data not matched with given regex parameter" + NL + "        \t\tthrow new Exception(\"Line doesn't match: \" + str";
  protected final String TEXT_103 = ");" + NL + "        \t}" + NL + "\t\t\t";
  protected final String TEXT_104 = "\t\t\t\t\t" + NL + "\t\t\t\t\t\tif(value";
  protected final String TEXT_105 = "[";
  protected final String TEXT_106 = "].length() > 0) {" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_107 = ".";
  protected final String TEXT_108 = " = value";
  protected final String TEXT_109 = "[";
  protected final String TEXT_110 = "];";
  protected final String TEXT_111 = ".";
  protected final String TEXT_112 = " = ParserUtils.parseTo_Date(value";
  protected final String TEXT_113 = "[";
  protected final String TEXT_114 = "], ";
  protected final String TEXT_115 = ");";
  protected final String TEXT_116 = ".";
  protected final String TEXT_117 = " = value";
  protected final String TEXT_118 = "[";
  protected final String TEXT_119 = "].getBytes(";
  protected final String TEXT_120 = ");";
  protected final String TEXT_121 = ".";
  protected final String TEXT_122 = " = ParserUtils.parseTo_";
  protected final String TEXT_123 = "(value";
  protected final String TEXT_124 = "[";
  protected final String TEXT_125 = "]);";
  protected final String TEXT_126 = "\t\t\t\t\t" + NL + "\t\t\t\t\t\t} else {" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_127 = "throw new RuntimeException(\"Value is empty for column : '";
  protected final String TEXT_128 = "' in '";
  protected final String TEXT_129 = "' connection, value is invalid or this column should be nullable or have a default value.\");";
  protected final String TEXT_130 = ".";
  protected final String TEXT_131 = " = ";
  protected final String TEXT_132 = ";";
  protected final String TEXT_133 = "}" + NL + "\t";
  protected final String TEXT_134 = NL + "\t\t\t\t\t";
  protected final String TEXT_135 = " ";
  protected final String TEXT_136 = " = null; ";
  protected final String TEXT_137 = "\t\t\t\t\t" + NL + "\t\t\t\t\t" + NL + "    } catch (Exception e) {" + NL + "        whetherReject_";
  protected final String TEXT_138 = " = true;";
  protected final String TEXT_139 = NL + "            if(isMatch";
  protected final String TEXT_140 = "){" + NL + "            \tthrow(e);" + NL + "            }else{" + NL + "\t\t\t\t System.err.println(e.getMessage());" + NL + "          \t\tcontinue;" + NL + "            }";
  protected final String TEXT_141 = NL + "                    ";
  protected final String TEXT_142 = " = new ";
  protected final String TEXT_143 = "Struct();";
  protected final String TEXT_144 = NL + "                    ";
  protected final String TEXT_145 = ".";
  protected final String TEXT_146 = " = ";
  protected final String TEXT_147 = ".";
  protected final String TEXT_148 = ";";
  protected final String TEXT_149 = NL + "                ";
  protected final String TEXT_150 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_151 = ";";
  protected final String TEXT_152 = NL + "                ";
  protected final String TEXT_153 = " = null;";
  protected final String TEXT_154 = NL + "                System.err.println(e.getMessage());";
  protected final String TEXT_155 = NL + "                ";
  protected final String TEXT_156 = " = null;";
  protected final String TEXT_157 = NL + "            \t";
  protected final String TEXT_158 = ".errorMessage = e.getMessage() + \" - Line: \" + tos_count_";
  protected final String TEXT_159 = ";";
  protected final String TEXT_160 = NL + "    }\t\t\t\t\t" + NL + "\t\t\t\t\t" + NL + "\t\t\t\t\t";
  protected final String TEXT_161 = NL + "\t\t";
  protected final String TEXT_162 = "if(!whetherReject_";
  protected final String TEXT_163 = ") { ";
  protected final String TEXT_164 = "      " + NL + "             if(";
  protected final String TEXT_165 = " == null){ " + NL + "            \t ";
  protected final String TEXT_166 = " = new ";
  protected final String TEXT_167 = "Struct();" + NL + "             }\t\t\t\t";
  protected final String TEXT_168 = NL + "\t    \t ";
  protected final String TEXT_169 = ".";
  protected final String TEXT_170 = " = ";
  protected final String TEXT_171 = ".";
  protected final String TEXT_172 = ";    \t\t\t\t";
  protected final String TEXT_173 = NL + "\t\t";
  protected final String TEXT_174 = " } ";
  protected final String TEXT_175 = "\t";
  protected final String TEXT_176 = NL + "\t\t\tnb_line_";
  protected final String TEXT_177 = "++;";
  protected final String TEXT_178 = NL;

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
    String myHeader = ElementParameterParser.getValue(node, "__HEADER__");
    String myLimit = ElementParameterParser.getValue(node, "__LIMIT__");
    String row_separator = ElementParameterParser.getValue(node, "__ROWSEPARATOR__");
    String regex = ElementParameterParser.getValue(node, "__REGEX__");
	String dieOnErrorStr = ElementParameterParser.getValue(node, "__DIE_ON_ERROR__");
	boolean dieOnError = (dieOnErrorStr!=null&&!("").equals(dieOnErrorStr))?("true").equals(dieOnErrorStr):false;  

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
     String myFooter = ElementParameterParser.getValue(node, "__FOOTER__"); if (("").equals(myFooter)) myFooter = "0"; 
    stringBuffer.append(myFooter);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append( ElementParameterParser.getValue(node, "__REMOVE_EMPTY_ROW__") );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_14);
    stringBuffer.append( encoding);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(row_separator);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    if (("").equals(myLimit)) {myLimit = "-1";}
    stringBuffer.append(myLimit );
    stringBuffer.append(TEXT_25);
     
         	if (("").equals(myHeader)) myHeader = "0"; 
        
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(myHeader );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_52);
    stringBuffer.append( encoding);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(row_separator);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_56);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_57);
    stringBuffer.append( regex );
    stringBuffer.append(TEXT_58);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_73);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_74);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_78);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_81);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_82);
    
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

    stringBuffer.append(TEXT_83);
    stringBuffer.append(connTemp.getName() );
    stringBuffer.append(TEXT_84);
    
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
    stringBuffer.append(TEXT_85);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_88);
    stringBuffer.append(metadata.getListColumns().size());
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(metadata.getListColumns().size());
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_93);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_96);
    stringBuffer.append( cid );
    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_100);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_101);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_102);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_103);
    	
				List<IMetadataColumn> listColumns = metadata.getListColumns();
				int sizeListColumns = listColumns.size();
				for (int valueN=0; valueN<sizeListColumns; valueN++) {
					IMetadataColumn column = listColumns.get(valueN);
					String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();

					
    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_105);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_106);
    
							if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {
								/** end **/
								
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_107);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_109);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_110);
    
								/** begin **/
							} else if(javaType == JavaTypesManager.DATE) { 
								/** end **/
								
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_114);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_115);
    
								/** begin **/
							} else if(javaType == JavaTypesManager.BYTE_ARRAY) { 
								/** end **/
								
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_116);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_117);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_118);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_119);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_120);
    
								/** begin **/
							} else {
								/** end **/
								
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_121);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_122);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_123);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_124);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_125);
    
								/** begin **/
							}
								/** end **/
						
    stringBuffer.append(TEXT_126);
    
							/** begin **/
							String defaultValue = JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate, column.getDefault());
							if(defaultValue == null) {
							/** end **/
								
    stringBuffer.append(TEXT_127);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_128);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_129);
    
							/** begin **/
							} else {
							/** end **/
								
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_130);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_131);
    stringBuffer.append(defaultValue);
    stringBuffer.append(TEXT_132);
    
							/** begin **/
							}
							/** end **/
						
						
    stringBuffer.append(TEXT_133);
    					
	/** begin **/
					}
    stringBuffer.append(TEXT_134);
    if(rejectConnName.equals(firstConnName)) {
    stringBuffer.append(TEXT_135);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_136);
    }
    stringBuffer.append(TEXT_137);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_138);
    
        if (dieOnError) {
            
    stringBuffer.append(TEXT_139);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_140);
    
        } else {
            if(!("").equals(rejectConnName)&&!rejectConnName.equals(firstConnName)&&rejectColumnList != null && rejectColumnList.size() > 0) {

                
    stringBuffer.append(TEXT_141);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_142);
    stringBuffer.append(rejectConnName );
    stringBuffer.append(TEXT_143);
    
                for(IMetadataColumn column : metadata.getListColumns()) {
                    
    stringBuffer.append(TEXT_144);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_145);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_146);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_147);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_148);
    
                }
                
    stringBuffer.append(TEXT_149);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_150);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_151);
    stringBuffer.append(TEXT_152);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_153);
    
            } else if(("").equals(rejectConnName)){
                
    stringBuffer.append(TEXT_154);
    stringBuffer.append(TEXT_155);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_156);
    
            } else if(rejectConnName.equals(firstConnName)){
    stringBuffer.append(TEXT_157);
    stringBuffer.append(rejectConnName);
    stringBuffer.append(TEXT_158);
    stringBuffer.append(node.getUniqueName() );
    stringBuffer.append(TEXT_159);
    }
        } 
        
    stringBuffer.append(TEXT_160);
    
				}
			}
		}
		
		if (conns.size()>0) {	
			boolean isFirstEnter = true;
			for (int i=0;i<conns.size();i++) {
				IConnection conn = conns.get(i);
				if ((conn.getName().compareTo(firstConnName)!=0)&&(conn.getName().compareTo(rejectConnName)!=0)&&(conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA))) {

    stringBuffer.append(TEXT_161);
     if(isFirstEnter) {
    stringBuffer.append(TEXT_162);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_163);
     isFirstEnter = false; } 
    stringBuffer.append(TEXT_164);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_165);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_166);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_167);
    
			    	 for (IMetadataColumn column: metadata.getListColumns()) {

    stringBuffer.append(TEXT_168);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_169);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_170);
    stringBuffer.append(firstConnName );
    stringBuffer.append(TEXT_171);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_172);
    
				 	}
				}
			}

    stringBuffer.append(TEXT_173);
     if(!isFirstEnter) {
    stringBuffer.append(TEXT_174);
     } 
    stringBuffer.append(TEXT_175);
    
		}
	/** end **/
		

    stringBuffer.append(TEXT_176);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_177);
    
		}
	}

    stringBuffer.append(TEXT_178);
    return stringBuffer.toString();
  }
}
