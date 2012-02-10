package org.talend.designer.codegen.translators.databases.ldap;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TLDAPAttributesInputEndJava
{
  protected static String nl;
  public static synchronized TLDAPAttributesInputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TLDAPAttributesInputEndJava result = new TLDAPAttributesInputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t" + NL + "\t}//a" + NL + "\t";
  protected final String TEXT_2 = NL + "\t// examine the response controls" + NL + "\tjavax.naming.ldap.Control[] responseControls_";
  protected final String TEXT_3 = " = ctx_";
  protected final String TEXT_4 = ".getResponseControls();" + NL + "\tif (responseControls_";
  protected final String TEXT_5 = " != null) {" + NL + "\t\tfor (int i_";
  protected final String TEXT_6 = " = 0; i_";
  protected final String TEXT_7 = " < responseControls_";
  protected final String TEXT_8 = ".length; i_";
  protected final String TEXT_9 = "++) {" + NL + "\t\t\tif (responseControls_";
  protected final String TEXT_10 = "[i_";
  protected final String TEXT_11 = "] instanceof javax.naming.ldap.PagedResultsResponseControl) {" + NL + "\t\t\t\tjavax.naming.ldap.PagedResultsResponseControl prrc_";
  protected final String TEXT_12 = " = (javax.naming.ldap.PagedResultsResponseControl) responseControls_";
  protected final String TEXT_13 = "[i_";
  protected final String TEXT_14 = "];   \t\t\t" + NL + "\t\t\t\tcookie_";
  protected final String TEXT_15 = " = prrc_";
  protected final String TEXT_16 = ".getCookie();" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t}" + NL + "\t// pass the cookie back to the server for the next page" + NL + "\tctx_";
  protected final String TEXT_17 = ".setRequestControls(new javax.naming.ldap.Control[] { new javax.naming.ldap.PagedResultsControl(pageSize_";
  protected final String TEXT_18 = ", cookie_";
  protected final String TEXT_19 = ", javax.naming.ldap.Control.CRITICAL) });" + NL + "" + NL + "} while ((cookie_";
  protected final String TEXT_20 = " != null) && (cookie_";
  protected final String TEXT_21 = ".length != 0));";
  protected final String TEXT_22 = NL + NL + "rootSchema_";
  protected final String TEXT_23 = ".close();";
  protected final String TEXT_24 = NL + "\tctx_";
  protected final String TEXT_25 = ".close();";
  protected final String TEXT_26 = NL + "globalMap.put(\"";
  protected final String TEXT_27 = "_NB_LINE\", ";
  protected final String TEXT_28 = "_NB_LINE);" + NL;
  protected final String TEXT_29 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String useExistingConn = ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__");
boolean paging =("true").equals(ElementParameterParser.getValue(node, "__ISPAGING__"));

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)&&(metadatas.get(0) != null)) {

    stringBuffer.append(TEXT_1);
    if(paging){
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
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
    stringBuffer.append(cid);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    }
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    
if(("false").equals(useExistingConn)){

    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    
}

    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    
 }

    stringBuffer.append(TEXT_29);
    return stringBuffer.toString();
  }
}
