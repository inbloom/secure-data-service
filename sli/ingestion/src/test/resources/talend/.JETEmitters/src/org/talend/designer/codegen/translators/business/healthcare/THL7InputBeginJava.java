package org.talend.designer.codegen.translators.business.healthcare;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.Map;

public class THL7InputBeginJava
{
  protected static String nl;
  public static synchronized THL7InputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    THL7InputBeginJava result = new THL7InputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = " " + NL + "System.setProperty(\"org.apache.commons.logging.Log\", \"org.apache.commons.logging.impl.NoOpLog\");" + NL + "" + NL + "class TalendHL7Reader_";
  protected final String TEXT_2 = " implements ca.uhn.hl7v2.llp.HL7Reader{" + NL + "" + NL + "//    private static final ca.uhn.log.HapiLog log = ca.uhn.log.HapiLogFactory.getHapiLog(MinLLPReader.class);" + NL + "    " + NL + "    private java.io.BufferedReader myReader;" + NL + "" + NL + "    private char endMsg = '\\u001c'; //character indicating the" + NL + "                                              //termination of an HL7 message" + NL + "    private char startMsg = '\\u000b';//character indicating the" + NL + "                                               //start of an HL7 message" + NL + "" + NL + "    //NB: The above is as per the minimal lower layer protocol." + NL + "    " + NL + "    /** Creates a MinLLPReader which reads from the given InputStream. The stream" + NL + "        is assumed to be an ASCII bit stream." + NL + "    */" + NL + "    public TalendHL7Reader_";
  protected final String TEXT_3 = "(java.io.InputStream in, String charsetName) throws IOException" + NL + "    {" + NL + "        setInputStream(in, charsetName);" + NL + "    }" + NL + "    " + NL + "    public void setEndMsgChar(char endMsg){" + NL + "    \tthis.endMsg= endMsg;" + NL + "    }" + NL + "    " + NL + "    public void setStartMsgChar(char startMsg){" + NL + "    \tthis.startMsg = startMsg;" + NL + "    }" + NL + "" + NL + "    /**" + NL + "     * Sets the InputStream from which to read messages.  The InputStream must be set " + NL + "     * before any calls to <code>getMessage()</code>." + NL + "     */" + NL + "    public synchronized void setInputStream(java.io.InputStream in, String charsetName) throws IOException " + NL + "    {" + NL + "     \tmyReader = new java.io.BufferedReader(new java.io.InputStreamReader(in, charsetName));" + NL + "    }" + NL + "" + NL + "\tpublic void setInputStream(java.io.InputStream arg0)" + NL + "\t\t\tthrows IOException {" + NL + "\t}" + NL + "" + NL + "    /** Calls the same method in the underlying BufferedReader. */" + NL + "    /**private int read(char[] parm1, int parm2, int parm3) throws java.io.IOException" + NL + "    {" + NL + "        return myReader.read(parm1, parm2, parm3);" + NL + "    }*/" + NL + "" + NL + "    /** Reads an HL7 encoded message from this Reader's InputStream." + NL + "        @return The message, in string format, without the lower level" + NL + "        protocol delimeters. Returns null if a -1 is received on the initial" + NL + "         read." + NL + "     */" + NL + "     private boolean isFirst = true;" + NL + "    public synchronized String getMessage() throws java.io.IOException " + NL + "    {" + NL + "        StringBuffer s_buffer = new StringBuffer();" + NL + "" + NL + "        boolean end_of_message = false;" + NL + "" + NL + "        int c = 0;" + NL + "        try {" + NL + "            do {" + NL + "                c = myReader.read();" + NL + "            } while (c == ' ' || c == '\\t' || c == '\\r' || c == '\\n');" + NL + "        } catch (Exception e) {" + NL + "            return null;" + NL + "        }" + NL + "" + NL + "        // trying to read when there is no data (stream may have" + NL + "        // been closed at other end)" + NL + "        if (c == -1) {" + NL + "            return null;" + NL + "        }" + NL + "" + NL + "        if (c != startMsg && !isFirst) {" + NL + "            //throw new java.io.IOException(\"no start of message indicator was found.\");" + NL + "            return null;" + NL + "        }" + NL + "" + NL + "        if (isFirst && c != startMsg) {" + NL + "            s_buffer.append((char) c);" + NL + "        }" + NL + "" + NL + "        while (!end_of_message) {" + NL + "            c = myReader.read();" + NL + "" + NL + "            if (c == -1) {" + NL + "                break;" + NL + "            }" + NL + "" + NL + "            if (c == endMsg) {" + NL + "                end_of_message = true;" + NL + "            } else {" + NL + "                s_buffer.append((char) c);" + NL + "            }" + NL + "        } // end while" + NL + "        isFirst = false;" + NL + "        if (s_buffer.length() > 0) {" + NL + "            return s_buffer.toString();" + NL + "        } else {" + NL + "            return null;" + NL + "        }" + NL + "    }" + NL + "" + NL + "    /** " + NL + "     * Closes the underlying BufferedReader." + NL + "     */" + NL + "    public synchronized void close() throws java.io.IOException" + NL + "    {" + NL + "    \tif(myReader!=null){" + NL + "        \tmyReader.close();" + NL + "        }" + NL + "    }" + NL + "}" + NL + "" + NL + "class TalendSegment_";
  protected final String TEXT_4 = "{" + NL + "" + NL + "\tpublic List<ca.uhn.hl7v2.model.Segment> result = new java.util.ArrayList<ca.uhn.hl7v2.model.Segment>();" + NL + "\t" + NL + "\tpublic void findAllSegment(ca.uhn.hl7v2.model.Group messParent){" + NL + "        String[] childNames = messParent.getNames();" + NL + "        for (int i = 0; i < childNames.length; i++) {" + NL + "            try {" + NL + "                ca.uhn.hl7v2.model.Structure[] childReps = messParent.getAll(childNames[i]);" + NL + "                for (int j = 0; j < childReps.length; j++) {" + NL + "                \tif (childReps[j] instanceof ca.uhn.hl7v2.model.Group) {" + NL + "                \t\tfindAllSegment((ca.uhn.hl7v2.model.Group)childReps[j]);" + NL + "                \t}else if (childReps[j] instanceof ca.uhn.hl7v2.model.Segment) {" + NL + "                \t\tresult.add((ca.uhn.hl7v2.model.Segment)childReps[j]);" + NL + "                \t}" + NL + "                }" + NL + "            } catch (ca.uhn.hl7v2.HL7Exception e) {" + NL + "                e.printStackTrace();" + NL + "            }" + NL + "        }" + NL + "\t}" + NL + "\tpublic List<ca.uhn.hl7v2.model.Segment> getResult(){" + NL + "\t\treturn this.result;" + NL + "\t}" + NL + "" + NL + "\tpublic int[] getIndices(String strSpec){" + NL + "\t\tint[] result = new int[4];" + NL + "\t\tjava.util.StringTokenizer tok = new java.util.StringTokenizer(strSpec, \"-\", false);" + NL + "\t\tif(tok.hasMoreTokens())" + NL + "\t\t\ttok.nextToken();" + NL + "\t\tint i=1;" + NL + "\t\twhile(tok.hasMoreTokens()){" + NL + "\t\t\tString tmp = tok.nextToken();" + NL + "\t\t\tif(i==1){ // find the rep" + NL + "\t\t\t\tif(tmp.indexOf(\"(\")>0 && tmp.indexOf(\")\")>tmp.indexOf(\"(\")){" + NL + "\t\t\t\t\tresult[0] = Integer.parseInt(tmp.substring(0, tmp.indexOf(\"(\")));" + NL + "\t\t\t\t\tresult[1] = Integer.parseInt(tmp.substring(tmp.indexOf(\"(\")+1, tmp.indexOf(\")\")))-1;" + NL + "\t\t\t\t}else{" + NL + "\t\t\t\t\tresult[0] = Integer.parseInt(tmp);" + NL + "\t\t\t\t\tresult[1] = 0;" + NL + "\t\t\t\t}" + NL + "\t\t\t}else{\t\t\t\t\t" + NL + "\t\t\t\tresult[i]=Integer.parseInt(tmp);" + NL + "\t\t\t}" + NL + "\t\t\ti++;" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tif(result[2]==0){" + NL + "\t\t\tresult[2]=1;" + NL + "\t\t}" + NL + "\t\tif(result[3]==0){" + NL + "\t\t\tresult[3]=1;" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\treturn result;" + NL + "\t}" + NL + "\t" + NL + "} " + NL + "" + NL + "int nb_line_";
  protected final String TEXT_5 = "=0;   \t" + NL + "ca.uhn.hl7v2.parser.Parser parser_";
  protected final String TEXT_6 = " = new ca.uhn.hl7v2.parser.PipeParser();";
  protected final String TEXT_7 = NL + "ca.uhn.hl7v2.validation.impl.NoValidation noValid_";
  protected final String TEXT_8 = " = new ca.uhn.hl7v2.validation.impl.NoValidation();" + NL + "parser_";
  protected final String TEXT_9 = ".setValidationContext(noValid_";
  protected final String TEXT_10 = ");";
  protected final String TEXT_11 = NL + "      //READ DATA" + NL + "    java.io.InputStream is_";
  protected final String TEXT_12 = " = null;" + NL + "    Object file_";
  protected final String TEXT_13 = " = ";
  protected final String TEXT_14 = ";    " + NL + "    if(file_";
  protected final String TEXT_15 = " instanceof java.io.InputStream){" + NL + "    \tis_";
  protected final String TEXT_16 = " = (java.io.InputStream)file_";
  protected final String TEXT_17 = ";" + NL + "    }else{" + NL + "    \tis_";
  protected final String TEXT_18 = " = new java.io.FileInputStream((String)file_";
  protected final String TEXT_19 = ");" + NL + "    }" + NL + "\tTalendHL7Reader_";
  protected final String TEXT_20 = " reader_";
  protected final String TEXT_21 = " = new TalendHL7Reader_";
  protected final String TEXT_22 = "(is_";
  protected final String TEXT_23 = ", ";
  protected final String TEXT_24 = ");" + NL + "" + NL + "\treader_";
  protected final String TEXT_25 = ".setStartMsgChar(";
  protected final String TEXT_26 = ");" + NL + "\treader_";
  protected final String TEXT_27 = ".setEndMsgChar(";
  protected final String TEXT_28 = ");" + NL + "" + NL + "\tString str_";
  protected final String TEXT_29 = " = null;" + NL + "\tint[] index_";
  protected final String TEXT_30 = " = null;" + NL + "\t" + NL + "\twhile((str_";
  protected final String TEXT_31 = " = reader_";
  protected final String TEXT_32 = ".getMessage())!=null){" + NL + "\t\tstr_";
  protected final String TEXT_33 = " = str_";
  protected final String TEXT_34 = ".replace('\\n', '\\r');" + NL + "    \tca.uhn.hl7v2.model.Message message_";
  protected final String TEXT_35 = " = parser_";
  protected final String TEXT_36 = ".parse(str_";
  protected final String TEXT_37 = ");" + NL + "    \tca.uhn.hl7v2.util.Terser terser_";
  protected final String TEXT_38 = " = new ca.uhn.hl7v2.util.Terser(message_";
  protected final String TEXT_39 = ");" + NL + "    \t" + NL + "    \tTalendSegment_";
  protected final String TEXT_40 = " talendSeg_";
  protected final String TEXT_41 = " = new TalendSegment_";
  protected final String TEXT_42 = "();" + NL + "    \ttalendSeg_";
  protected final String TEXT_43 = ".findAllSegment(message_";
  protected final String TEXT_44 = ");" + NL + "    \tjava.util.Iterator<ca.uhn.hl7v2.model.Segment> iterSeg_";
  protected final String TEXT_45 = " = talendSeg_";
  protected final String TEXT_46 = ".getResult().iterator();" + NL + "    \t" + NL + "    \twhile(iterSeg_";
  protected final String TEXT_47 = ".hasNext()){" + NL + "\t\t\tca.uhn.hl7v2.model.Segment seg_";
  protected final String TEXT_48 = " = iterSeg_";
  protected final String TEXT_49 = ".next();";
  protected final String TEXT_50 = NL + "    \t\t";
  protected final String TEXT_51 = " = null;" + NL + "    \t\t" + NL + "    \t\tif(\"";
  protected final String TEXT_52 = "\".equals(seg_";
  protected final String TEXT_53 = ".getName())){" + NL + "    \t\t\t";
  protected final String TEXT_54 = " = new ";
  protected final String TEXT_55 = "Struct();";
  protected final String TEXT_56 = NL + "\tString temp_";
  protected final String TEXT_57 = " = \"\"; ";
  protected final String TEXT_58 = NL + "\tindex_";
  protected final String TEXT_59 = " = talendSeg_";
  protected final String TEXT_60 = ".getIndices(";
  protected final String TEXT_61 = ");" + NL + "\ttemp_";
  protected final String TEXT_62 = "=ca.uhn.hl7v2.util.Terser.get(seg_";
  protected final String TEXT_63 = ",index_";
  protected final String TEXT_64 = "[0],index_";
  protected final String TEXT_65 = "[1],index_";
  protected final String TEXT_66 = "[2],index_";
  protected final String TEXT_67 = "[3]);" + NL + "\tif(temp_";
  protected final String TEXT_68 = ".length() > 0){" + NL + "\t\t";
  protected final String TEXT_69 = ".";
  protected final String TEXT_70 = " = temp_";
  protected final String TEXT_71 = ";" + NL + "\t}else{" + NL + "\t";
  protected final String TEXT_72 = ".";
  protected final String TEXT_73 = " = ";
  protected final String TEXT_74 = ";" + NL + "\t}";
  protected final String TEXT_75 = NL + "\tindex_";
  protected final String TEXT_76 = " = talendSeg_";
  protected final String TEXT_77 = ".getIndices(";
  protected final String TEXT_78 = ");" + NL + "\t";
  protected final String TEXT_79 = ".";
  protected final String TEXT_80 = "=ca.uhn.hl7v2.util.Terser.get(seg_";
  protected final String TEXT_81 = ",index_";
  protected final String TEXT_82 = "[0],index_";
  protected final String TEXT_83 = "[1],index_";
  protected final String TEXT_84 = "[2],index_";
  protected final String TEXT_85 = "[3]);";
  protected final String TEXT_86 = NL + "\tindex_";
  protected final String TEXT_87 = " = talendSeg_";
  protected final String TEXT_88 = ".getIndices(";
  protected final String TEXT_89 = ");" + NL + "\ttemp_";
  protected final String TEXT_90 = "=ca.uhn.hl7v2.util.Terser.get(seg_";
  protected final String TEXT_91 = ",index_";
  protected final String TEXT_92 = "[0],index_";
  protected final String TEXT_93 = "[1],index_";
  protected final String TEXT_94 = "[2],index_";
  protected final String TEXT_95 = "[3]);" + NL + "\tif(temp_";
  protected final String TEXT_96 = ".length() > 0) {";
  protected final String TEXT_97 = ".";
  protected final String TEXT_98 = " = temp_";
  protected final String TEXT_99 = ".getBytes(";
  protected final String TEXT_100 = ");";
  protected final String TEXT_101 = ".";
  protected final String TEXT_102 = " = ParserUtils.parseTo_Date(temp_";
  protected final String TEXT_103 = ", ";
  protected final String TEXT_104 = ");";
  protected final String TEXT_105 = ".";
  protected final String TEXT_106 = " = ParserUtils.parseTo_";
  protected final String TEXT_107 = "(ParserUtils.parseTo_Number(temp_";
  protected final String TEXT_108 = ", ";
  protected final String TEXT_109 = ", ";
  protected final String TEXT_110 = "));";
  protected final String TEXT_111 = ".";
  protected final String TEXT_112 = " = ParserUtils.parseTo_";
  protected final String TEXT_113 = "(temp_";
  protected final String TEXT_114 = ");";
  protected final String TEXT_115 = NL + "\t} else {";
  protected final String TEXT_116 = "throw new RuntimeException(\"Value is empty for column : '";
  protected final String TEXT_117 = "' in '";
  protected final String TEXT_118 = "' connection, value is invalid or this column should be nullable or have a default value.\");";
  protected final String TEXT_119 = ".";
  protected final String TEXT_120 = " = ";
  protected final String TEXT_121 = ";";
  protected final String TEXT_122 = NL + "\t}";
  protected final String TEXT_123 = NL + "\t\t}";
  protected final String TEXT_124 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String fileName = ElementParameterParser.getValue(node,"__FILENAME__");
String encoding = ElementParameterParser.getValue(node,"__ENCODING__");
String startMsg = ElementParameterParser.getValue(node,"__START_MSG__");
String endMsg = ElementParameterParser.getValue(node,"__END_MSG__");

boolean isNoValidate = ("true").equals(ElementParameterParser.getValue(node,"__NOVALIDATE__"));

String advancedSeparatorStr = ElementParameterParser.getValue(node, "__ADVANCED_SEPARATOR__");
boolean advancedSeparator = (advancedSeparatorStr!=null&&!("").equals(advancedSeparatorStr))?("true").equals(advancedSeparatorStr):false;
String thousandsSeparator = ElementParameterParser.getValueWithJavaType(node, "__THOUSANDS_SEPARATOR__", JavaTypesManager.CHARACTER);
String decimalSeparator = ElementParameterParser.getValueWithJavaType(node, "__DECIMAL_SEPARATOR__", JavaTypesManager.CHARACTER);
List<Map<String, String>> schemas = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__SCHEMAS__");

List< ? extends IConnection> connections = node.getOutgoingSortedConnections();

if(connections!=null && connections.size()>0){

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    
if(isNoValidate){

    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    
}

    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(fileName );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
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
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(startMsg);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(endMsg);
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
    
for(IConnection conn : connections){
	if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)){
		Map<String, String> mapping = null;
		for(Map<String, String> schemaMap : schemas){
//			System.out.println("schema:"+schemaMap.get("SCHEMA")+"\tconn:"+conn.getName());
			if(conn.getName().contains(schemaMap.get("SCHEMA"))){
				mapping = schemaMap;
				break;
			}
		}
		IMetadataTable metadata = conn.getMetadataTable();
		if(metadata!=null){

    stringBuffer.append(TEXT_50);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(metadata.getLabel() );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_55);
    
			List<IMetadataColumn> columns = metadata.getListColumns();
			int sizeListColumns = columns.size();
			String[] arrXpath = ((String)mapping.get("MAPPING")).split(",");

			boolean noStringTypeExist = false;

    		for (int valueN=0; valueN<sizeListColumns; valueN++) {
    			IMetadataColumn column = columns.get(valueN);
    			JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
    			if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT){
    			}else{
    				noStringTypeExist = true;
    				break;
    			}
    		}
    		boolean hasStringDefault = false;
    		for (int valueM=0; valueM<sizeListColumns; valueM++) {
    			IMetadataColumn column = columns.get(valueM);
    			JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
    			if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT){
    				if(hasStringDefault==false && column.getDefault()!=null && column.getDefault().length() > 0 ){
    					hasStringDefault = true;
    					break;
    				}
    			}
    		}
    		if(noStringTypeExist || hasStringDefault){

    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_57);
    
			}
			for (int i=0;i < arrXpath.length;i++) {
				String query = arrXpath[i];
				if(i < sizeListColumns){
					IMetadataColumn column = columns.get(i);
					
					String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
					
                    if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT){
                    	String defaultValue = column.getDefault();
                    	if(defaultValue!=null && defaultValue.length()>0){

    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(arrXpath[i] );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
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
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(defaultValue );
    stringBuffer.append(TEXT_74);
    
                    	}else{
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(arrXpath[i] );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_85);
    
						}
					}else{

    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_88);
    stringBuffer.append(arrXpath[i] );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_96);
    
					if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT) {
					} else if(javaType == JavaTypesManager.BYTE_ARRAY){ 

    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_97);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(encoding);
    stringBuffer.append(TEXT_100);
    
					}else if(javaType == JavaTypesManager.DATE) { 

    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_101);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_102);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_103);
    stringBuffer.append( patternValue );
    stringBuffer.append(TEXT_104);
    
					}else if(advancedSeparator && JavaTypesManager.isNumberType(javaType, column.isNullable())) { 

    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_105);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_106);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_108);
    stringBuffer.append( thousandsSeparator );
    stringBuffer.append(TEXT_109);
    stringBuffer.append( decimalSeparator );
    stringBuffer.append(TEXT_110);
    
					} else { 

    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_112);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_114);
    
					}

    stringBuffer.append(TEXT_115);
    
					String defaultValue = JavaTypesManager.getDefaultValueFromJavaType(typeToGenerate, column.getDefault());
					if(defaultValue == null) {

    stringBuffer.append(TEXT_116);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_117);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_118);
    
					} else {

    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_119);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_120);
    stringBuffer.append(defaultValue );
    stringBuffer.append(TEXT_121);
    
					}

    stringBuffer.append(TEXT_122);
    
                    }
				}
			}
		}

    stringBuffer.append(TEXT_123);
    
	}
}

    
}

    stringBuffer.append(TEXT_124);
    return stringBuffer.toString();
  }
}
