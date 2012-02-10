package org.talend.designer.codegen.translators.file.management;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;

public class TFileCompareMainJava
{
  protected static String nl;
  public static synchronized TFileCompareMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileCompareMainJava result = new TFileCompareMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "boolean result_";
  protected final String TEXT_3 = " = true;" + NL + "\t";
  protected final String TEXT_4 = NL + "java.nio.channels.FileChannel fileChannel_";
  protected final String TEXT_5 = " = new java.io.FileInputStream(";
  protected final String TEXT_6 = ").getChannel();" + NL + "long fileLength_";
  protected final String TEXT_7 = " = fileChannel_";
  protected final String TEXT_8 = ".size();" + NL + "fileChannel_";
  protected final String TEXT_9 = ".close();" + NL + "java.nio.channels.FileChannel fileChannelRef_";
  protected final String TEXT_10 = " = new java.io.FileInputStream(";
  protected final String TEXT_11 = ").getChannel();" + NL + "long fileRefLength_";
  protected final String TEXT_12 = " = fileChannelRef_";
  protected final String TEXT_13 = ".size();" + NL + "fileChannelRef_";
  protected final String TEXT_14 = ".close();" + NL + "if(fileLength_";
  protected final String TEXT_15 = " != fileRefLength_";
  protected final String TEXT_16 = ")" + NL + "{" + NL + "\tresult_";
  protected final String TEXT_17 = " = false;" + NL + "}";
  protected final String TEXT_18 = NL + "if (result_";
  protected final String TEXT_19 = ")" + NL + "{" + NL + "\t";
  protected final String TEXT_20 = NL + "\t\tjava.io.BufferedReader file_";
  protected final String TEXT_21 = " = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(";
  protected final String TEXT_22 = "),";
  protected final String TEXT_23 = "));" + NL + "\t\tjava.io.BufferedReader fileRef_";
  protected final String TEXT_24 = " = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(";
  protected final String TEXT_25 = "),";
  protected final String TEXT_26 = "));" + NL + "\t\tString content_";
  protected final String TEXT_27 = " = null,contentRef_";
  protected final String TEXT_28 = " = null;" + NL + "\t\twhile((content_";
  protected final String TEXT_29 = " = file_";
  protected final String TEXT_30 = ".readLine()) != null && (contentRef_";
  protected final String TEXT_31 = " = fileRef_";
  protected final String TEXT_32 = ".readLine()) != null)" + NL + "\t\t{" + NL + "\t\t\tif(content_";
  protected final String TEXT_33 = ".compareTo(contentRef_";
  protected final String TEXT_34 = ") != 0)" + NL + "\t\t\t{" + NL + "\t\t\t\tresult_";
  protected final String TEXT_35 = " = false;" + NL + "\t\t\t\tbreak;" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\t// Check if files has a different number of lines:" + NL + "\t\tif(content_";
  protected final String TEXT_36 = " == null){" + NL + "\t\t    // This step is done in case of the while upper ignore second part:" + NL + "\t\t    contentRef_";
  protected final String TEXT_37 = " = fileRef_";
  protected final String TEXT_38 = ".readLine();" + NL + "\t\t}" + NL + "\t\tif(content_";
  protected final String TEXT_39 = " != null || contentRef_";
  protected final String TEXT_40 = " != null){" + NL + "\t\t    result_";
  protected final String TEXT_41 = " = false;" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tfile_";
  protected final String TEXT_42 = ".close();" + NL + "\t\tfileRef_";
  protected final String TEXT_43 = ".close();" + NL + "\t\t";
  protected final String TEXT_44 = NL + "    \tjava.io.BufferedInputStream file_";
  protected final String TEXT_45 = " = new java.io.BufferedInputStream(new java.io.FileInputStream(";
  protected final String TEXT_46 = "));" + NL + "    \tjava.io.BufferedInputStream fileRef_";
  protected final String TEXT_47 = " = new java.io.BufferedInputStream(new java.io.FileInputStream(";
  protected final String TEXT_48 = "));" + NL + "    \tint content_";
  protected final String TEXT_49 = " = -1,contentRef_";
  protected final String TEXT_50 = " = -1;" + NL + "    \twhile((content_";
  protected final String TEXT_51 = " = file_";
  protected final String TEXT_52 = ".read()) != -1 && (contentRef_";
  protected final String TEXT_53 = " = fileRef_";
  protected final String TEXT_54 = ".read()) != -1)" + NL + "    \t{" + NL + "    \t\tif(content_";
  protected final String TEXT_55 = " != contentRef_";
  protected final String TEXT_56 = ")" + NL + "    \t\t{" + NL + "    \t\t\tresult_";
  protected final String TEXT_57 = " = false;" + NL + "    \t\t\tbreak;" + NL + "    \t\t}" + NL + "    \t}" + NL + "    \tfile_";
  protected final String TEXT_58 = ".close();" + NL + "    \tfileRef_";
  protected final String TEXT_59 = ".close();\t\t\t" + NL + "\t\t";
  protected final String TEXT_60 = NL + "}" + NL + "" + NL + "String message";
  protected final String TEXT_61 = " = \"\";" + NL + "if (result_";
  protected final String TEXT_62 = ") {" + NL + "\tmessage";
  protected final String TEXT_63 = " = ";
  protected final String TEXT_64 = ";" + NL + "\t" + NL + "} else {" + NL + "\tmessage";
  protected final String TEXT_65 = " = ";
  protected final String TEXT_66 = ";" + NL + "}" + NL + "globalMap.put(\"";
  protected final String TEXT_67 = "_DIFFERENCE\",result_";
  protected final String TEXT_68 = ");" + NL;
  protected final String TEXT_69 = NL + "    System.out.println(message";
  protected final String TEXT_70 = ");";
  protected final String TEXT_71 = NL + "\t\t";
  protected final String TEXT_72 = ".file = ";
  protected final String TEXT_73 = ";" + NL + "\t\t";
  protected final String TEXT_74 = ".file_ref = ";
  protected final String TEXT_75 = ";\t" + NL + "    \t";
  protected final String TEXT_76 = ".moment = java.util.Calendar.getInstance().getTime();" + NL + "    \t";
  protected final String TEXT_77 = ".job = jobName;" + NL + "\t\t";
  protected final String TEXT_78 = ".component = currentComponent;" + NL + "\t\t";
  protected final String TEXT_79 = ".differ = (result_";
  protected final String TEXT_80 = ")?0:1;" + NL + "\t\t";
  protected final String TEXT_81 = ".message = message";
  protected final String TEXT_82 = ";";
  protected final String TEXT_83 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String file = ElementParameterParser.getValue(node, "__FILE__");
Boolean print = new Boolean(ElementParameterParser.getValue(node, "__PRINT__"));
String differMessage = ElementParameterParser.getValue(node, "__DIFFER_MESSAGE__");
String noDifferMessage = ElementParameterParser.getValue(node, "__NO_DIFFER_MESSAGE__");
String fileRef = ElementParameterParser.getValue(node, "__FILE_REF__");
String cmpMode = ElementParameterParser.getValue(node, "__COMPARISON_MODE__");
String encoding = ElementParameterParser.getValue(node,"__ENCODING__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    
	if(!("TEXT_CMP").equals(cmpMode))
	{
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(file);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(fileRef);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    }
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    
	if(("TEXT_CMP").equals(cmpMode))
	{
		
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(file);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(fileRef);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(encoding );
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    
	}
	else
	{
		
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(file);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(fileRef);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_59);
    
	}
	
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(noDifferMessage);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(differMessage);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    
if (print) {

    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_70);
    
}

for (IConnection conn : node.getOutgoingConnections()) {
	if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_71);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(file );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(fileRef );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_80);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_82);
    
	}
}

    stringBuffer.append(TEXT_83);
    return stringBuffer.toString();
  }
}
