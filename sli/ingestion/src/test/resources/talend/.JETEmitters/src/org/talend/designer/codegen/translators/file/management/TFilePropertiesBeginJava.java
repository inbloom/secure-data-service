package org.talend.designer.codegen.translators.file.management;

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

public class TFilePropertiesBeginJava
{
  protected static String nl;
  public static synchronized TFilePropertiesBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFilePropertiesBeginJava result = new TFilePropertiesBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "java.io.File file_";
  protected final String TEXT_3 = " = new java.io.File(";
  protected final String TEXT_4 = ");";
  protected final String TEXT_5 = NL;
  protected final String TEXT_6 = " = new ";
  protected final String TEXT_7 = "Struct();" + NL + "" + NL + "if(file_";
  protected final String TEXT_8 = ".exists()) {";
  protected final String TEXT_9 = NL + "    ";
  protected final String TEXT_10 = ".abs_path = file_";
  protected final String TEXT_11 = ".getAbsolutePath();";
  protected final String TEXT_12 = NL + "    ";
  protected final String TEXT_13 = ".dirname = file_";
  protected final String TEXT_14 = ".getParent();";
  protected final String TEXT_15 = NL + "    ";
  protected final String TEXT_16 = ".basename = file_";
  protected final String TEXT_17 = ".getName();" + NL + "    String r_";
  protected final String TEXT_18 = " = (file_";
  protected final String TEXT_19 = ".canRead())?\"r\":\"-\";" + NL + "\tString w_";
  protected final String TEXT_20 = " = (file_";
  protected final String TEXT_21 = ".canWrite())?\"w\":\"-\";" + NL + "\t//String x_ = (file_.canExecute())?\"x\":\"-\"; /*since JDK1.6*/";
  protected final String TEXT_22 = NL + "    ";
  protected final String TEXT_23 = ".mode_string = r_";
  protected final String TEXT_24 = " + w_";
  protected final String TEXT_25 = ";";
  protected final String TEXT_26 = NL + "    ";
  protected final String TEXT_27 = ".size = file_";
  protected final String TEXT_28 = ".length();";
  protected final String TEXT_29 = NL + "    ";
  protected final String TEXT_30 = ".mtime = file_";
  protected final String TEXT_31 = ".lastModified();";
  protected final String TEXT_32 = NL + "    ";
  protected final String TEXT_33 = ".mtime_string =(new java.util.Date(file_";
  protected final String TEXT_34 = ".lastModified())).toString();" + NL + "\t" + NL + "\t";
  protected final String TEXT_35 = NL + "\t\t// Calculation of the Message Digest MD5" + NL + "\t\tjava.io.InputStream is_";
  protected final String TEXT_36 = " = new java.io.FileInputStream(file_";
  protected final String TEXT_37 = ");" + NL + "\t\tbyte[] buffer_";
  protected final String TEXT_38 = " = new byte[8192];" + NL + "\t\tint read_";
  protected final String TEXT_39 = " = 0;" + NL + "\t\tjava.security.MessageDigest dgs_";
  protected final String TEXT_40 = " = java.security.MessageDigest.getInstance(\"MD5\");" + NL + "\t\twhile( (read_";
  protected final String TEXT_41 = " = is_";
  protected final String TEXT_42 = ".read(buffer_";
  protected final String TEXT_43 = ")) > 0) {" + NL + "\t\t\tdgs_";
  protected final String TEXT_44 = ".update(buffer_";
  protected final String TEXT_45 = ", 0, read_";
  protected final String TEXT_46 = ");" + NL + "\t\t}" + NL + "\t\t";
  protected final String TEXT_47 = ".md5 =String.format(\"%032x\", new java.math.BigInteger(1, dgs_";
  protected final String TEXT_48 = ".digest()));" + NL + "\t\tis_";
  protected final String TEXT_49 = ".close();" + NL + "\t";
  protected final String TEXT_50 = NL + "}" + NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String filename = ElementParameterParser.getValue(node, "__FILENAME__");

boolean MD5 = new Boolean(ElementParameterParser.getValue(node, "__MD5__"));


String outputConnName = null;
List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
if (conns!=null) {
	if (conns.size()>0) {
		for (int i=0;i<conns.size();i++) {
			IConnection connTemp = conns.get(i);
			if (connTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
				outputConnName = connTemp.getName();
				break;
			}
		}
	}
}

if (outputConnName != null){

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(outputConnName );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(outputConnName );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(outputConnName );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(outputConnName );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(outputConnName );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(outputConnName );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(outputConnName );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(outputConnName );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(outputConnName );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    if(MD5){
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
    stringBuffer.append(outputConnName );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    }
    stringBuffer.append(TEXT_50);
    
}

    return stringBuffer.toString();
  }
}
