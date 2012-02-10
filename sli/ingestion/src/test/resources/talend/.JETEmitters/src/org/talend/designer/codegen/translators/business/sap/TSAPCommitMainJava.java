package org.talend.designer.codegen.translators.business.sap;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TSAPCommitMainJava
{
  protected static String nl;
  public static synchronized TSAPCommitMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSAPCommitMainJava result = new TSAPCommitMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\t\t\t" + NL + "\tcom.sap.mw.jco.JCO.Client client_";
  protected final String TEXT_3 = " = (com.sap.mw.jco.JCO.Client)globalMap.get(\"";
  protected final String TEXT_4 = "\");\t" + NL + "\t" + NL + "\tif(client_";
  protected final String TEXT_5 = " != null)" + NL + "\t{" + NL + "\t\tcom.sap.mw.jco.IRepository repository_";
  protected final String TEXT_6 = " = com.sap.mw.jco.JCO.createRepository(\"REPOSITORY\", client_";
  protected final String TEXT_7 = ");" + NL + "\t" + NL + "\t\tcom.sap.mw.jco.IFunctionTemplate functionTemplate_";
  protected final String TEXT_8 = " = repository_";
  protected final String TEXT_9 = ".getFunctionTemplate(\"BAPI_TRANSACTION_COMMIT\");" + NL + "\t" + NL + "\t    com.sap.mw.jco.JCO.Function function_";
  protected final String TEXT_10 = " = functionTemplate_";
  protected final String TEXT_11 = ".getFunction();" + NL + "\t    " + NL + "\t\ttry{" + NL + "\t\t\tclient_";
  protected final String TEXT_12 = ".execute(function_";
  protected final String TEXT_13 = ");" + NL + "\t    }catch (Exception e_";
  protected final String TEXT_14 = ") {" + NL + "\t\t\tcom.sap.mw.jco.JCO.releaseClient(client_";
  protected final String TEXT_15 = ");" + NL + "\t\t    throw new RuntimeException(e_";
  protected final String TEXT_16 = ".getMessage());" + NL + "\t\t}\t" + NL + "\t";
  protected final String TEXT_17 = NL + "\tcom.sap.conn.jco.JCoDestination dest_";
  protected final String TEXT_18 = " = (com.sap.conn.jco.JCoDestination)globalMap.get(\"";
  protected final String TEXT_19 = "\");" + NL + "\tif(dest_";
  protected final String TEXT_20 = " != null)" + NL + "\t{" + NL + "\t\tcom.sap.conn.jco.JCoRepository repository_";
  protected final String TEXT_21 = " = dest_";
  protected final String TEXT_22 = ".getRepository();" + NL + "\t\t" + NL + "\t\tcom.sap.conn.jco.JCoFunctionTemplate functionTemplate_";
  protected final String TEXT_23 = " = repository_";
  protected final String TEXT_24 = ".getFunctionTemplate(\"BAPI_TRANSACTION_COMMIT\");" + NL + "\t\t" + NL + "\t\tcom.sap.conn.jco.JCoFunction function_";
  protected final String TEXT_25 = " = functionTemplate_";
  protected final String TEXT_26 = ".getFunction();" + NL + "\t\t" + NL + "\t\ttry{" + NL + "\t\t\tfunction_";
  protected final String TEXT_27 = ".execute(dest_";
  protected final String TEXT_28 = ");" + NL + "\t\t}catch (Exception e_";
  protected final String TEXT_29 = ") {" + NL + "\t\t\tcom.sap.conn.jco.JCoContext.end(dest_";
  protected final String TEXT_30 = ");" + NL + "\t\t    throw new RuntimeException(e_";
  protected final String TEXT_31 = ".getMessage());" + NL + "\t\t}";
  protected final String TEXT_32 = NL + "\t\t";
  protected final String TEXT_33 = NL + "\t\t\t";
  protected final String TEXT_34 = NL + "\t\t    \tcom.sap.mw.jco.JCO.releaseClient(client_";
  protected final String TEXT_35 = ");" + NL + "\t\t    ";
  protected final String TEXT_36 = NL + "\t\t\t\tcom.sap.conn.jco.JCoContext.end(dest_";
  protected final String TEXT_37 = ");" + NL + "\t\t\t";
  protected final String TEXT_38 = NL + "\t\t";
  protected final String TEXT_39 = NL + "\t}";
  protected final String TEXT_40 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();

    String cid = node.getUniqueName();

    String connection = ElementParameterParser.getValue(node,"__CONNECTION__");

    boolean close = ("true").equals(ElementParameterParser.getValue(node,"__CLOSE__"));

    String conn = "conn_" + connection;
    
    String VERSION_SAP2 = "sapjco.jar";
    String VERSION_SAP3 = "sapjco3.jar";
    
    String version = VERSION_SAP2;
    List<? extends INode> nodes = node.getProcess().getGraphicalNodes();
    for(INode targetNode : nodes){
    	if (targetNode.getUniqueName().equals(connection)) {
	      version = ElementParameterParser.getValue(targetNode, "__DB_VERSION__");
	    }
    }

    if(VERSION_SAP2.equals(version)){
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(conn);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    }else if(VERSION_SAP3.equals(version)){
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(conn);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    }
    stringBuffer.append(TEXT_32);
    if(close){
    stringBuffer.append(TEXT_33);
    if(VERSION_SAP2.equals(version)){
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    }else if(VERSION_SAP3.equals(version)){
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    }
    stringBuffer.append(TEXT_38);
    }
    stringBuffer.append(TEXT_39);
    stringBuffer.append(TEXT_40);
    return stringBuffer.toString();
  }
}
