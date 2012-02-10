package org.talend.designer.codegen.translators.file.output;

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

public class TFileOutputLDIFMainJava
{
  protected static String nl;
  public static synchronized TFileOutputLDIFMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputLDIFMainJava result = new TFileOutputLDIFMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t\t//////////////////////////";
  protected final String TEXT_2 = " " + NL + "\t\tStringBuilder sb_";
  protected final String TEXT_3 = " = new StringBuilder();" + NL + "\t\tboolean needSeparator_";
  protected final String TEXT_4 = " = false;" + NL + "\t\tboolean canOutput_";
  protected final String TEXT_5 = " = false;\t\t\t\t   \t\t";
  protected final String TEXT_6 = NL + "\t\tString value_";
  protected final String TEXT_7 = "_";
  protected final String TEXT_8 = " = \"\";";
  protected final String TEXT_9 = "   \t\t\t\t" + NL + "\t    if(";
  protected final String TEXT_10 = ".";
  protected final String TEXT_11 = " != null && !(\"\").equals(";
  protected final String TEXT_12 = ".";
  protected final String TEXT_13 = ")) {";
  protected final String TEXT_14 = NL + "    \tvalue_";
  protected final String TEXT_15 = "_";
  protected final String TEXT_16 = " =  \t\t\t\t\t\t";
  protected final String TEXT_17 = NL + "\t\tFormatterUtils.format_Date(";
  protected final String TEXT_18 = ".";
  protected final String TEXT_19 = ", ";
  protected final String TEXT_20 = ");";
  protected final String TEXT_21 = NL + "\t\t";
  protected final String TEXT_22 = ".";
  protected final String TEXT_23 = ";";
  protected final String TEXT_24 = NL + "\t\tString.valueOf(";
  protected final String TEXT_25 = ");";
  protected final String TEXT_26 = NL + "\t\tjava.nio.charset.Charset.forName(";
  protected final String TEXT_27 = ").decode(java.nio.ByteBuffer.wrap(";
  protected final String TEXT_28 = ".";
  protected final String TEXT_29 = ")).toString();";
  protected final String TEXT_30 = NL + "\t\tString.valueOf(";
  protected final String TEXT_31 = ".";
  protected final String TEXT_32 = ");";
  protected final String TEXT_33 = "\t\t" + NL + "\t\tutil_";
  protected final String TEXT_34 = ".breakString(sb_";
  protected final String TEXT_35 = ", dn_";
  protected final String TEXT_36 = " + value_";
  protected final String TEXT_37 = "_";
  protected final String TEXT_38 = ", wrap_";
  protected final String TEXT_39 = ");";
  protected final String TEXT_40 = "\t\t" + NL + "        util_";
  protected final String TEXT_41 = ".breakString(sb_";
  protected final String TEXT_42 = ", changetype_";
  protected final String TEXT_43 = " + \"";
  protected final String TEXT_44 = "\", wrap_";
  protected final String TEXT_45 = ");";
  protected final String TEXT_46 = NL;
  protected final String TEXT_47 = NL + "\tcanOutput_";
  protected final String TEXT_48 = " = true;        ";
  protected final String TEXT_49 = " " + NL;
  protected final String TEXT_50 = NL + "\t    }";
  protected final String TEXT_51 = "   ";
  protected final String TEXT_52 = "\t\t\t\t\t\t";
  protected final String TEXT_53 = NL + "\t    }";
  protected final String TEXT_54 = " \t\t" + NL + "\t";
  protected final String TEXT_55 = NL;
  protected final String TEXT_56 = NL + "\tif(needSeparator_";
  protected final String TEXT_57 = "){" + NL + "\t\tsb_";
  protected final String TEXT_58 = ".append(\"-\\n\");" + NL + "\t}  " + NL + "\tutil_";
  protected final String TEXT_59 = ".breakString(sb_";
  protected final String TEXT_60 = ", \"";
  protected final String TEXT_61 = ": \" + \"";
  protected final String TEXT_62 = "\", wrap_";
  protected final String TEXT_63 = ");" + NL + "\t";
  protected final String TEXT_64 = "\t\t" + NL + "\t\tString[] values_";
  protected final String TEXT_65 = "_";
  protected final String TEXT_66 = " = value_";
  protected final String TEXT_67 = "_";
  protected final String TEXT_68 = ".split(";
  protected final String TEXT_69 = ");" + NL + "\t\tfor(String item_";
  protected final String TEXT_70 = " : values_";
  protected final String TEXT_71 = "_";
  protected final String TEXT_72 = "){" + NL + "\t\t\tutil_";
  protected final String TEXT_73 = ".breakString(sb_";
  protected final String TEXT_74 = ", \"";
  protected final String TEXT_75 = ";binary";
  protected final String TEXT_76 = ":";
  protected final String TEXT_77 = ":";
  protected final String TEXT_78 = " \" + item_";
  protected final String TEXT_79 = ", wrap_";
  protected final String TEXT_80 = ");" + NL + "\t\t}\t\t\t\t" + NL + "\t";
  protected final String TEXT_81 = NL + "\t\tutil_";
  protected final String TEXT_82 = ".breakString(sb_";
  protected final String TEXT_83 = ", \"";
  protected final String TEXT_84 = ";binary";
  protected final String TEXT_85 = ":";
  protected final String TEXT_86 = ":";
  protected final String TEXT_87 = " \" + value_";
  protected final String TEXT_88 = "_";
  protected final String TEXT_89 = ", wrap_";
  protected final String TEXT_90 = "); " + NL + "\t";
  protected final String TEXT_91 = NL + "\tneedSeparator_";
  protected final String TEXT_92 = " = true;" + NL + "\tcanOutput_";
  protected final String TEXT_93 = " = true;   \t";
  protected final String TEXT_94 = NL + "sb_";
  protected final String TEXT_95 = ".append(\"-\\n\");";
  protected final String TEXT_96 = NL + "\t";
  protected final String TEXT_97 = NL + "\t\tString[] values_";
  protected final String TEXT_98 = "_";
  protected final String TEXT_99 = " = value_";
  protected final String TEXT_100 = "_";
  protected final String TEXT_101 = ".split(";
  protected final String TEXT_102 = ");" + NL + "\t\tfor(String item_";
  protected final String TEXT_103 = " : values_";
  protected final String TEXT_104 = "_";
  protected final String TEXT_105 = "){" + NL + "\t\t\tutil_";
  protected final String TEXT_106 = ".breakString(sb_";
  protected final String TEXT_107 = ", \"";
  protected final String TEXT_108 = ";binary";
  protected final String TEXT_109 = ":";
  protected final String TEXT_110 = ":";
  protected final String TEXT_111 = " \" + item_";
  protected final String TEXT_112 = ", wrap_";
  protected final String TEXT_113 = ");" + NL + "\t\t}\t\t\t\t" + NL + "\t";
  protected final String TEXT_114 = NL + "\t\tutil_";
  protected final String TEXT_115 = ".breakString(sb_";
  protected final String TEXT_116 = ", \"";
  protected final String TEXT_117 = ";binary";
  protected final String TEXT_118 = ":";
  protected final String TEXT_119 = ":";
  protected final String TEXT_120 = " \" + value_";
  protected final String TEXT_121 = "_";
  protected final String TEXT_122 = ", wrap_";
  protected final String TEXT_123 = ");" + NL + "\t";
  protected final String TEXT_124 = NL + "\tcanOutput_";
  protected final String TEXT_125 = " = true;";
  protected final String TEXT_126 = NL + "\t\tutil_";
  protected final String TEXT_127 = ".breakString(sb_";
  protected final String TEXT_128 = ", \"";
  protected final String TEXT_129 = ": \" + value_";
  protected final String TEXT_130 = "_";
  protected final String TEXT_131 = ", wrap_";
  protected final String TEXT_132 = ");" + NL + "\t\tcanOutput_";
  protected final String TEXT_133 = " = true;";
  protected final String TEXT_134 = NL;
  protected final String TEXT_135 = NL + "\t    }";
  protected final String TEXT_136 = "   \t\t\t\t";
  protected final String TEXT_137 = "\t\t\t\t\t\t" + NL + "\t\tsb_";
  protected final String TEXT_138 = ".append('\\n');\t\t" + NL + "\t\t" + NL + "\tif(canOutput_";
  protected final String TEXT_139 = "){\t\t" + NL + "\t\tpw_";
  protected final String TEXT_140 = ".write(sb_";
  protected final String TEXT_141 = ".toString());" + NL + "\t\t";
  protected final String TEXT_142 = NL + "            if(nb_line_";
  protected final String TEXT_143 = "%";
  protected final String TEXT_144 = " == 0) {\t\t" + NL + "    \t\tpw_";
  protected final String TEXT_145 = ".flush();" + NL + "    \t\t}" + NL + "\t\t";
  protected final String TEXT_146 = " \t\t" + NL + "\t\t" + NL + "    \tnb_line_";
  protected final String TEXT_147 = "++;" + NL + "    }\t";
  protected final String TEXT_148 = "    \t" + NL + "    \t//////////////////////////";
  protected final String TEXT_149 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String encoding = ElementParameterParser.getValue(node,"__ENCODING__");

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {
        String cid = node.getUniqueName();
        String changetype = ElementParameterParser.getValue(node, "__CHANGETYPE__");
        boolean flushOnRow = ("true").equals(ElementParameterParser.getValue(node, "__FLUSHONROW__"));		
        String flushMod = ElementParameterParser.getValue(node, "__FLUSHONROW_NUM__");
        
        List<Map<String, String>> multiValueColumns = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__MULTIVALUECOLUMNS__");
        List<Map<String, String>> modifyColumns = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__MODIFYCONFIG__");

    stringBuffer.append(TEXT_1);
    
    	List< ? extends IConnection> conns = node.getIncomingConnections();
    	for (IConnection conn : conns) {
    		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {    		

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    		
    			boolean generateOneAlready = false; //only for Modify append the char '-'
    			List<IMetadataColumn> columns = metadata.getListColumns();
    			int sizeColumns = columns.size();
    			boolean firstLoop = true;
    			for (int i = 0; i < sizeColumns; i++) {    				
    				IMetadataColumn column = columns.get(i);
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					boolean isPrimitive = JavaTypesManager.isJavaPrimitiveType( javaType, column.isNullable());					

    stringBuffer.append(TEXT_6);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    					
					if(!isPrimitive) {

    stringBuffer.append(TEXT_9);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_13);
    
} 

    stringBuffer.append(TEXT_14);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    
    				String pattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
    				if (javaType == JavaTypesManager.DATE && pattern != null && pattern.trim().length() != 0) {

    stringBuffer.append(TEXT_17);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append( pattern );
    stringBuffer.append(TEXT_20);
    				
					} else if(javaType == JavaTypesManager.STRING) {

    stringBuffer.append(TEXT_21);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_23);
    
					} else if (javaType == JavaTypesManager.BIGDECIMAL) {

    stringBuffer.append(TEXT_24);
    stringBuffer.append(column.getPrecision() == null? conn.getName() + "." + column.getLabel() : conn.getName() + "." + column.getLabel() + ".setScale(" + column.getPrecision() + ", java.math.RoundingMode.HALF_UP)" );
    stringBuffer.append(TEXT_25);
    
					} else if(javaType == JavaTypesManager.BYTE_ARRAY) {

    stringBuffer.append(TEXT_26);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_29);
    
					} else {

    stringBuffer.append(TEXT_30);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_32);
    
					}

     
	if(i==0) {

    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    if(!"none".equals(changetype)){
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(changetype );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    }
    stringBuffer.append(TEXT_46);
    if("delete".equals(changetype)){
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    }
    stringBuffer.append(TEXT_49);
    
	if(!isPrimitive) {

    stringBuffer.append(TEXT_50);
    					
	} 

    stringBuffer.append(TEXT_51);
    
	continue;
 } 

    stringBuffer.append(TEXT_52);
     if ("delete".equals(changetype)) {
	if(firstLoop){
	
    
	if(!isPrimitive) {

    stringBuffer.append(TEXT_53);
    					
	} 

    stringBuffer.append(TEXT_54);
    	
	}
	firstLoop = false; 
	break; 
} 
     if ("modify".equals(changetype)) {
		String operation = null;
		boolean isMultiValue = false;   
		String separator = null;
		boolean isBinary = false;
		boolean isBase64 = false;
		for(Map<String, String> line:modifyColumns){// search in the configuration table
				String columnName = line.get("SCHEMA_COLUMN");				
				if(column.getLabel().equals(columnName)){
					operation = line.get("OPERATION");					
					isMultiValue = "true".equals(line.get("ISMULTIVALUE"));
					separator = line.get("SEPARATOR");
					isBinary = "true".equals(line.get("BINARY"));
					isBase64 = "true".equals(line.get("BASE64"));
					break;
				}
		}	

    stringBuffer.append(TEXT_55);
    if(!"none".equals(operation)){
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(operation );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    if(isMultiValue){
    stringBuffer.append(TEXT_64);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(separator );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(column.getLabel() );
    if(isBinary){
    stringBuffer.append(TEXT_75);
    }
    stringBuffer.append(TEXT_76);
    if(isBase64){
    stringBuffer.append(TEXT_77);
    }
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    }else{
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(column.getLabel() );
    if(isBinary){
    stringBuffer.append(TEXT_84);
    }
    stringBuffer.append(TEXT_85);
    if(isBase64){
    stringBuffer.append(TEXT_86);
    }
    stringBuffer.append(TEXT_87);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_90);
    }
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_93);
    }
  if(i==sizeColumns - 1){

    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_95);
    }
}// here end the if modify

     if ("add".equals(changetype) || "none".equals(changetype)) {   
		boolean isMultiValue = false;
		String separator = null;
		boolean isBinary = false;
		boolean isBase64 = false;
		for(Map<String, String> line:multiValueColumns){// search in the configuration table
				String columnName = line.get("SCHEMA_COLUMN");				
				if(column.getLabel().equals(columnName)){
					isMultiValue = "true".equals(line.get("ISMULTIVALUE"));
					separator = line.get("SEPARATOR");
					isBinary = "true".equals(line.get("BINARY"));
					isBase64 = "true".equals(line.get("BASE64"));
					break;
				}
		}

    stringBuffer.append(TEXT_96);
    if(isMultiValue){
    stringBuffer.append(TEXT_97);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_101);
    stringBuffer.append(separator );
    stringBuffer.append(TEXT_102);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_103);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_105);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_107);
    stringBuffer.append(column.getLabel() );
    if(isBinary){
    stringBuffer.append(TEXT_108);
    }
    stringBuffer.append(TEXT_109);
    if(isBase64){
    stringBuffer.append(TEXT_110);
    }
    stringBuffer.append(TEXT_111);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_113);
    }else{
    stringBuffer.append(TEXT_114);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_116);
    stringBuffer.append(column.getLabel() );
    if(isBinary){
    stringBuffer.append(TEXT_117);
    }
    stringBuffer.append(TEXT_118);
    if(isBase64){
    stringBuffer.append(TEXT_119);
    }
    stringBuffer.append(TEXT_120);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_121);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_122);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_123);
    }
    stringBuffer.append(TEXT_124);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_125);
    
}// here end the if add

     if ("modrdn".equals(changetype)) {

    stringBuffer.append(TEXT_126);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_127);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_128);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_129);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_130);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_131);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_132);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_133);
    
}// here end the if modrdn

    stringBuffer.append(TEXT_134);
    
	if(!isPrimitive) {

    stringBuffer.append(TEXT_135);
    					
	} 

    stringBuffer.append(TEXT_136);
     
}//here end the last for, the List "columns"

    stringBuffer.append(TEXT_137);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_138);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_139);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_140);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_141);
     if(flushOnRow) { 
    stringBuffer.append(TEXT_142);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_143);
    stringBuffer.append(flushMod );
    stringBuffer.append(TEXT_144);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_145);
    
			}
		
    stringBuffer.append(TEXT_146);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_147);
    
	}
}//here end the first for, the List "conns"

    stringBuffer.append(TEXT_148);
    
  }
}  

    stringBuffer.append(TEXT_149);
    return stringBuffer.toString();
  }
}
