package org.talend.designer.codegen.translators.databases.ldap;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;

public class TLDAPInputEndJava
{
  protected static String nl;
  public static synchronized TLDAPInputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TLDAPInputEndJava result = new TLDAPInputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t" + NL + "\t}//a" + NL + "\t";
  protected final String TEXT_2 = NL + "    // examine the response controls" + NL + "    javax.naming.ldap.Control[] responseControls_";
  protected final String TEXT_3 = " = ctx_";
  protected final String TEXT_4 = ".getResponseControls();" + NL + "    if (responseControls_";
  protected final String TEXT_5 = " != null) {" + NL + "        for (int i_";
  protected final String TEXT_6 = " = 0; i_";
  protected final String TEXT_7 = " < responseControls_";
  protected final String TEXT_8 = ".length; i_";
  protected final String TEXT_9 = "++) {" + NL + "            if (responseControls_";
  protected final String TEXT_10 = "[i_";
  protected final String TEXT_11 = "] instanceof javax.naming.ldap.PagedResultsResponseControl) {" + NL + "                javax.naming.ldap.PagedResultsResponseControl prrc_";
  protected final String TEXT_12 = " = (javax.naming.ldap.PagedResultsResponseControl) responseControls_";
  protected final String TEXT_13 = "[i_";
  protected final String TEXT_14 = "];               " + NL + "                cookie_";
  protected final String TEXT_15 = " = prrc_";
  protected final String TEXT_16 = ".getCookie();" + NL + "            }" + NL + "        }" + NL + "    }" + NL + "    // pass the cookie back to the server for the next page" + NL + "    ctx_";
  protected final String TEXT_17 = ".setRequestControls(new javax.naming.ldap.Control[] { new javax.naming.ldap.PagedResultsControl(pageSize_";
  protected final String TEXT_18 = ", cookie_";
  protected final String TEXT_19 = ", javax.naming.ldap.Control.CRITICAL) });" + NL + "" + NL + "} while ((cookie_";
  protected final String TEXT_20 = " != null) && (cookie_";
  protected final String TEXT_21 = ".length != 0));";
  protected final String TEXT_22 = NL + "\t}catch (Exception e){";
  protected final String TEXT_23 = NL + "        \tthrow new Exception(e);";
  protected final String TEXT_24 = NL + "           \t System.err.println(e.getMessage());";
  protected final String TEXT_25 = NL + "\t}finally{";
  protected final String TEXT_26 = "  " + NL + "\t\tif(ctx_";
  protected final String TEXT_27 = "!=null){" + NL + "\t\t\tctx_";
  protected final String TEXT_28 = ".close();" + NL + "\t\t}";
  protected final String TEXT_29 = NL + "\t}" + NL + "globalMap.put(\"";
  protected final String TEXT_30 = "_NB_LINE\", ";
  protected final String TEXT_31 = "_NB_LINE);" + NL;
  protected final String TEXT_32 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String useExistingConn = ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__");
boolean paging =("true").equals(ElementParameterParser.getValue(node, "__ISPAGING__"));
boolean dieOnError = ("true").equals(ElementParameterParser.getValue(node, "__DIE_ON_ERROR__"));

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
    
        if (dieOnError) {

    stringBuffer.append(TEXT_23);
    
        } else {

    stringBuffer.append(TEXT_24);
    
		}

    stringBuffer.append(TEXT_25);
    
if(("false").equals(useExistingConn)){

    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    
}

    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    
 }

    stringBuffer.append(TEXT_32);
    return stringBuffer.toString();
  }
}
