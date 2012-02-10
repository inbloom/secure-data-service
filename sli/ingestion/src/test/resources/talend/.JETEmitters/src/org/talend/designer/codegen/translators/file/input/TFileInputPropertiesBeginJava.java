package org.talend.designer.codegen.translators.file.input;

import java.util.List;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TFileInputPropertiesBeginJava
{
  protected static String nl;
  public static synchronized TFileInputPropertiesBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileInputPropertiesBeginJava result = new TFileInputPropertiesBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\tjava.io.File file_";
  protected final String TEXT_2 = " = new java.io.File(";
  protected final String TEXT_3 = ");";
  protected final String TEXT_4 = NL + "\tjava.util.Properties properties_";
  protected final String TEXT_5 = " = new java.util.Properties();" + NL + "\tproperties_";
  protected final String TEXT_6 = ".load(new java.io.FileInputStream(file_";
  protected final String TEXT_7 = "));" + NL + "\tjava.util.Enumeration enumeration_";
  protected final String TEXT_8 = " = properties_";
  protected final String TEXT_9 = ".propertyNames();" + NL + "\twhile (enumeration_";
  protected final String TEXT_10 = ".hasMoreElements()) {" + NL + "\t\t";
  protected final String TEXT_11 = ".key = (String)enumeration_";
  protected final String TEXT_12 = ".nextElement();" + NL + "\t\t";
  protected final String TEXT_13 = ".value = (String)properties_";
  protected final String TEXT_14 = ".getProperty(";
  protected final String TEXT_15 = ".key);";
  protected final String TEXT_16 = NL + "    org.ini4j.Wini ini_";
  protected final String TEXT_17 = " = new org.ini4j.Wini(new java.io.File(";
  protected final String TEXT_18 = "));" + NL + "    ini_";
  protected final String TEXT_19 = ".getConfig().setFileEncoding(java.nio.charset.Charset.forName(";
  protected final String TEXT_20 = "));";
  protected final String TEXT_21 = NL + "\t\tfor(org.ini4j.Profile.Section section_";
  protected final String TEXT_22 = " : ini_";
  protected final String TEXT_23 = ".values()){" + NL + "\t    \tfor(String key_";
  protected final String TEXT_24 = " : section_";
  protected final String TEXT_25 = ".keySet()){" + NL + "\t\t\t\t";
  protected final String TEXT_26 = ".key = key_";
  protected final String TEXT_27 = ";" + NL + "\t\t\t\t";
  protected final String TEXT_28 = ".value = section_";
  protected final String TEXT_29 = ".get(key_";
  protected final String TEXT_30 = ");" + NL + "\t\t\t\t";
  protected final String TEXT_31 = ".section = section_";
  protected final String TEXT_32 = ".getName();";
  protected final String TEXT_33 = NL + "\t\tfor(String names_";
  protected final String TEXT_34 = " : ini_";
  protected final String TEXT_35 = ".keySet()){" + NL + "\t\t    ";
  protected final String TEXT_36 = ".section = names_";
  protected final String TEXT_37 = ";";
  protected final String TEXT_38 = NL + "\t    org.ini4j.Profile.Section section_";
  protected final String TEXT_39 = " = ini_";
  protected final String TEXT_40 = ".get(";
  protected final String TEXT_41 = ");" + NL + "\t    for(String key_";
  protected final String TEXT_42 = " : section_";
  protected final String TEXT_43 = ".keySet()){" + NL + "\t\t\t";
  protected final String TEXT_44 = ".key = key_";
  protected final String TEXT_45 = ";" + NL + "\t\t\t";
  protected final String TEXT_46 = ".value = section_";
  protected final String TEXT_47 = ".get(key_";
  protected final String TEXT_48 = ");";
  protected final String TEXT_49 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String fileFormat = ElementParameterParser.getValue(node,"__FILE_FORMAT__");
String filename = ElementParameterParser.getValue(node,"__FILENAME__");
String outConnName = null;
String encoding = ElementParameterParser.getValue(node,"__ENCODING__");
List< ? extends IConnection> outConns = node.getOutgoingConnections();
for(IConnection conn : outConns){
	if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
		outConnName = conn.getName();
	}
}
if(outConnName == null){
    return ""; //return immediately, generate nothing
}

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_3);
    
if(("PROPERTIES_FORMAT").equals(fileFormat)){

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(outConnName );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(outConnName );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(outConnName );
    stringBuffer.append(TEXT_15);
    
}else if(("INI_FORMAT").equals(fileFormat)){
	String sectionName = ElementParameterParser.getValue(node,"__SECTION_NAME__");
	String retriveMode = ElementParameterParser.getValue(node,"__RETRIVE_MODE__");

    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_20);
    
	if(("RETRIVE_ALL").equals(retriveMode)){

    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(outConnName );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(outConnName );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(outConnName );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    
	}else if(("RETRIVE_SECTIONS").equals(retriveMode)){

    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(outConnName );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    
	}else if(("RETRIVE_BY_SECTION").equals(retriveMode)){

    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(sectionName);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(outConnName );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(outConnName );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    
	}
}

    stringBuffer.append(TEXT_49);
    return stringBuffer.toString();
  }
}
