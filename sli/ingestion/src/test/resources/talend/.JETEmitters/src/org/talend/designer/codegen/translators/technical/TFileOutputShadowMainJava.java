package org.talend.designer.codegen.translators.technical;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;

public class TFileOutputShadowMainJava
{
  protected static String nl;
  public static synchronized TFileOutputShadowMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputShadowMainJava result = new TFileOutputShadowMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "if(";
  protected final String TEXT_3 = ".";
  protected final String TEXT_4 = " != null){" + NL + "\t";
  protected final String TEXT_5 = ".";
  protected final String TEXT_6 = " = TalendString.replaceSpecialCharForXML(";
  protected final String TEXT_7 = ".";
  protected final String TEXT_8 = ");" + NL + "}else{" + NL + "\t";
  protected final String TEXT_9 = ".";
  protected final String TEXT_10 = " = \"\";" + NL + "}";
  protected final String TEXT_11 = "out_";
  protected final String TEXT_12 = ".write(\"<row>\");" + NL + "out_";
  protected final String TEXT_13 = ".newLine();";
  protected final String TEXT_14 = "out_";
  protected final String TEXT_15 = ".write(\"<field>\"+";
  protected final String TEXT_16 = ".";
  protected final String TEXT_17 = "+\"</field>\");" + NL + "out_";
  protected final String TEXT_18 = ".newLine();";
  protected final String TEXT_19 = "out_";
  protected final String TEXT_20 = ".write(\"</row>\");" + NL + "out_";
  protected final String TEXT_21 = ".newLine();" + NL + "nb_line_";
  protected final String TEXT_22 = "++;";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
List< ? extends IConnection> conns = node.getIncomingConnections();
if(conns!=null && conns.size()>0){
	IConnection conn = conns.get(0);
	IMetadataTable metadata = conn.getMetadataTable();
	if (metadata!=null) {
		for(IMetadataColumn column:metadata.getListColumns()) {
			JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
			if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.OBJECT){

    stringBuffer.append(TEXT_2);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_10);
    
			}
		}

    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    		
		for(IMetadataColumn column:metadata.getListColumns()) {

    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    		
		}

    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    
	}
}
	
    return stringBuffer.toString();
  }
}
