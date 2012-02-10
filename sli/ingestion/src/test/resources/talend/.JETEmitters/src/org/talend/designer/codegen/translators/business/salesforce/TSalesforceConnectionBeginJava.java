package org.talend.designer.codegen.translators.business.salesforce;

import java.util.List;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.utils.TalendTextUtils;

public class TSalesforceConnectionBeginJava
{
  protected static String nl;
  public static synchronized TSalesforceConnectionBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSalesforceConnectionBeginJava result = new TSalesforceConnectionBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\torg.talend.salesforceBulk.SalesforceBulkAPI sforceBulk_";
  protected final String TEXT_2 = " = new org.talend.salesforceBulk.SalesforceBulkAPI();" + NL + "\t";
  protected final String TEXT_3 = NL + "\tsforceBulk_";
  protected final String TEXT_4 = ".setProxy(true,";
  protected final String TEXT_5 = ",";
  protected final String TEXT_6 = ",";
  protected final String TEXT_7 = ",";
  protected final String TEXT_8 = ");" + NL + "\t";
  protected final String TEXT_9 = NL + "\tsforceBulk_";
  protected final String TEXT_10 = ".login(";
  protected final String TEXT_11 = ",";
  protected final String TEXT_12 = ",";
  protected final String TEXT_13 = ",";
  protected final String TEXT_14 = ");" + NL + "\tglobalMap.put(\"conn_";
  protected final String TEXT_15 = "\", sforceBulk_";
  protected final String TEXT_16 = ".getConnection());";
  protected final String TEXT_17 = NL + "    java.util.Properties props_";
  protected final String TEXT_18 = " = System.getProperties();" + NL + "    props_";
  protected final String TEXT_19 = ".put(\"socksProxyHost\",";
  protected final String TEXT_20 = ");  " + NL + "    props_";
  protected final String TEXT_21 = ".put(\"socksProxyPort\",";
  protected final String TEXT_22 = ");" + NL + "    props_";
  protected final String TEXT_23 = ".put(\"java.net.socks.username\", ";
  protected final String TEXT_24 = ");" + NL + "    props_";
  protected final String TEXT_25 = ".put(\"java.net.socks.password\", ";
  protected final String TEXT_26 = "); " + NL + "    ";
  protected final String TEXT_27 = NL + NL + NL + "\torg.talend.salesforce.SforceManagement sfMgr_";
  protected final String TEXT_28 = " = new org.talend.salesforce.SforceManagementImpl();" + NL + "\t";
  protected final String TEXT_29 = NL + "\tsfMgr_";
  protected final String TEXT_30 = ".setClientID(";
  protected final String TEXT_31 = ");" + NL + "\tglobalMap.put(\"callOptions_";
  protected final String TEXT_32 = "\", sfMgr_";
  protected final String TEXT_33 = ".getCallOptions());" + NL + "\t";
  protected final String TEXT_34 = NL + "\tboolean result_";
  protected final String TEXT_35 = " = sfMgr_";
  protected final String TEXT_36 = ".login(";
  protected final String TEXT_37 = ",";
  protected final String TEXT_38 = ", ";
  protected final String TEXT_39 = ",";
  protected final String TEXT_40 = ",";
  protected final String TEXT_41 = ");" + NL + "\tif(!result_";
  protected final String TEXT_42 = "){" + NL + "\t\tthrow new RuntimeException(\"Login failed! Please check the username,password and endpoint\");" + NL + "\t}" + NL + "\t" + NL + "\tglobalMap.put(\"stub_";
  protected final String TEXT_43 = "\", sfMgr_";
  protected final String TEXT_44 = ".getStub());" + NL + "\tglobalMap.put(\"sessionHeader_";
  protected final String TEXT_45 = "\", sfMgr_";
  protected final String TEXT_46 = ".getSessionHeader());" + NL + "\t";
  protected final String TEXT_47 = "\t\t\t" + NL + "\t\t\t" + NL;
  protected final String TEXT_48 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String endpoint = ElementParameterParser.getValue(node, "__ENDPOINT__");
String username = ElementParameterParser.getValue(node, "__USER__");
String password = ElementParameterParser.getValue(node, "__PASS__");
String timeout = ElementParameterParser.getValue(node,"__TIMEOUT__");
boolean needCompression = ("true").equals(ElementParameterParser.getValue(node,"__NEED_COMPRESSION__"));

boolean useProxy = ("true").equals(ElementParameterParser.getValue(node,"__USE_PROXY__"));
String proxyHost = ElementParameterParser.getValue(node,"__PROXY_HOST__");
String proxyPort = ElementParameterParser.getValue(node,"__PROXY_PORT__");
String proxyUsername = ElementParameterParser.getValue(node,"__PROXY_USERNAME__");
String proxyPassword = ElementParameterParser.getValue(node,"__PROXY_PASSWORD__");

boolean bulkConnection = ("true").equals(ElementParameterParser.getValue(node,"__BULK_CONNECTION__"));
String apiVersion = ElementParameterParser.getValue(node,"__API_VERSION__");
String clientID = ElementParameterParser.getValue(node,"__CLIENT_ID__");
if(bulkConnection){

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    if(useProxy){
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(proxyHost);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(TalendTextUtils.removeQuotes(proxyPort));
    stringBuffer.append(TEXT_6);
    stringBuffer.append(proxyUsername);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(proxyPassword);
    stringBuffer.append(TEXT_8);
    }
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(endpoint);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(username);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(apiVersion);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    
}else{
	if(useProxy){

    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(proxyHost );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(proxyPort );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(proxyUsername );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(proxyPassword );
    stringBuffer.append(TEXT_26);
    
	}

    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    if(clientID!=null && !"".equals(clientID) && !"\"\"".equals(clientID)){
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(clientID);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    }
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(endpoint);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(username);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(timeout);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(needCompression);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    }
    stringBuffer.append(TEXT_47);
    stringBuffer.append(TEXT_48);
    return stringBuffer.toString();
  }
}
