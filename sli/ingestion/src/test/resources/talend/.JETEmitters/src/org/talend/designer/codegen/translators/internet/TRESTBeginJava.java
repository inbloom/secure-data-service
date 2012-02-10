package org.talend.designer.codegen.translators.internet;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.Map;
import java.util.List;

public class TRESTBeginJava
{
  protected static String nl;
  public static synchronized TRESTBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TRESTBeginJava result = new TRESTBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\tString endpoint_";
  protected final String TEXT_3 = " = ";
  protected final String TEXT_4 = ";" + NL + "\t\t" + NL + "\t\tString trustStoreFile_";
  protected final String TEXT_5 = " = System.getProperty(\"javax.net.ssl.trustStore\");" + NL + "\t\tString trustStoreType_";
  protected final String TEXT_6 = " = System.getProperty(\"javax.net.ssl.trustStoreType\");" + NL + "\t\tString trustStorePWD_";
  protected final String TEXT_7 = " = System.getProperty(\"javax.net.ssl.trustStorePassword\");" + NL + "\t\t" + NL + "\t\tString keyStoreFile_";
  protected final String TEXT_8 = " = System.getProperty(\"javax.net.ssl.keyStore\");" + NL + "\t\tString keyStoreType_";
  protected final String TEXT_9 = " = System.getProperty(\"javax.net.ssl.keyStoreType\");" + NL + "\t\tString keyStorePWD_";
  protected final String TEXT_10 = " = System.getProperty(\"javax.net.ssl.keyStorePassword\");" + NL + "\t\t" + NL + "\t\tcom.sun.jersey.api.client.config.ClientConfig config_";
  protected final String TEXT_11 = " = new com.sun.jersey.api.client.config.DefaultClientConfig();" + NL + "\t\tjavax.net.ssl.SSLContext ctx_";
  protected final String TEXT_12 = " = javax.net.ssl.SSLContext.getInstance(\"SSL\");" + NL + "\t\t" + NL + "\t\tjavax.net.ssl.TrustManager[] tms_";
  protected final String TEXT_13 = " = null;" + NL + "\t\tif(trustStoreFile_";
  protected final String TEXT_14 = "!=null && trustStoreType_";
  protected final String TEXT_15 = "!=null){" + NL + "\t\t\tchar[] password_";
  protected final String TEXT_16 = " = null;" + NL + "\t\t\tif(trustStorePWD_";
  protected final String TEXT_17 = "!=null)" + NL + "\t\t\t\tpassword_";
  protected final String TEXT_18 = " = trustStorePWD_";
  protected final String TEXT_19 = ".toCharArray();" + NL + "\t\t\tjava.security.KeyStore trustStore_";
  protected final String TEXT_20 = " = java.security.KeyStore.getInstance(trustStoreType_";
  protected final String TEXT_21 = ");" + NL + "\t\t\ttrustStore_";
  protected final String TEXT_22 = ".load(new java.io.FileInputStream(trustStoreFile_";
  protected final String TEXT_23 = "), password_";
  protected final String TEXT_24 = ");" + NL + "\t\t\t" + NL + "\t\t\tjavax.net.ssl.TrustManagerFactory tmf_";
  protected final String TEXT_25 = " = javax.net.ssl.TrustManagerFactory.getInstance(javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm());" + NL + "            tmf_";
  protected final String TEXT_26 = ".init(trustStore_";
  protected final String TEXT_27 = ");" + NL + "            tms_";
  protected final String TEXT_28 = " = tmf_";
  protected final String TEXT_29 = ".getTrustManagers();" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tjavax.net.ssl.KeyManager[] kms_";
  protected final String TEXT_30 = " = null;" + NL + "\t\tif(keyStoreFile_";
  protected final String TEXT_31 = "!=null && keyStoreType_";
  protected final String TEXT_32 = "!=null){" + NL + "\t\t\tchar[] password_";
  protected final String TEXT_33 = " = null;" + NL + "\t\t\tif(keyStorePWD_";
  protected final String TEXT_34 = "!=null)" + NL + "\t\t\t\tpassword_";
  protected final String TEXT_35 = " = keyStorePWD_";
  protected final String TEXT_36 = ".toCharArray();" + NL + "\t\t\tjava.security.KeyStore keyStore_";
  protected final String TEXT_37 = " = java.security.KeyStore.getInstance(keyStoreType_";
  protected final String TEXT_38 = ");" + NL + "\t\t\tkeyStore_";
  protected final String TEXT_39 = ".load(new java.io.FileInputStream(keyStoreFile_";
  protected final String TEXT_40 = "), password_";
  protected final String TEXT_41 = ");" + NL + "\t\t\t" + NL + "\t\t\tjavax.net.ssl.KeyManagerFactory kmf_";
  protected final String TEXT_42 = " = javax.net.ssl.KeyManagerFactory.getInstance(javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm());" + NL + "            kmf_";
  protected final String TEXT_43 = ".init(keyStore_";
  protected final String TEXT_44 = ",password_";
  protected final String TEXT_45 = ");" + NL + "            kms_";
  protected final String TEXT_46 = " = kmf_";
  protected final String TEXT_47 = ".getKeyManagers();" + NL + "\t\t}" + NL + "\t\t" + NL + "        ctx_";
  protected final String TEXT_48 = ".init(kms_";
  protected final String TEXT_49 = ", tms_";
  protected final String TEXT_50 = " , null);" + NL + "        config_";
  protected final String TEXT_51 = ".getProperties().put(com.sun.jersey.client.urlconnection.HTTPSProperties.PROPERTY_HTTPS_PROPERTIES," + NL + "                    new com.sun.jersey.client.urlconnection.HTTPSProperties(new javax.net.ssl.HostnameVerifier() {" + NL + "" + NL + "                        public boolean verify(String hostName, javax.net.ssl.SSLSession session) {" + NL + "                            return true;" + NL + "                        }" + NL + "                    }, ctx_";
  protected final String TEXT_52 = "));" + NL + "" + NL + "\t\tcom.sun.jersey.api.client.Client restClient_";
  protected final String TEXT_53 = " = com.sun.jersey.api.client.Client.create(config_";
  protected final String TEXT_54 = ");" + NL + "\t\tcom.sun.jersey.api.client.WebResource restResource_";
  protected final String TEXT_55 = ";" + NL + "\t\tif(endpoint_";
  protected final String TEXT_56 = "!=null && !(\"\").equals(endpoint_";
  protected final String TEXT_57 = ")){" + NL + "\t\t\trestResource_";
  protected final String TEXT_58 = " = restClient_";
  protected final String TEXT_59 = ".resource(endpoint_";
  protected final String TEXT_60 = ");" + NL + "\t\t}else{" + NL + "\t\t\tthrow new IllegalArgumentException(\"url can't be empty!\");" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tcom.sun.jersey.api.client.ClientResponse errorResponse_";
  protected final String TEXT_61 = " = null;" + NL + "\t\tString restResponse_";
  protected final String TEXT_62 = " = \"\";" + NL + "\ttry{" + NL + "\t\trestResponse_";
  protected final String TEXT_63 = " = restResource_";
  protected final String TEXT_64 = NL + "\t\t";
  protected final String TEXT_65 = NL + "        .header(";
  protected final String TEXT_66 = ",";
  protected final String TEXT_67 = ")";
  protected final String TEXT_68 = "  ";
  protected final String TEXT_69 = NL + "\t\t.get(String.class);";
  protected final String TEXT_70 = NL + "\t\t.post(String.class,";
  protected final String TEXT_71 = ");";
  protected final String TEXT_72 = NL + "\t\t.put(String.class,";
  protected final String TEXT_73 = ");";
  protected final String TEXT_74 = NL + "\t\t.delete(String.class);";
  protected final String TEXT_75 = NL + "\t}catch (com.sun.jersey.api.client.UniformInterfaceException ue) {" + NL + "        errorResponse_";
  protected final String TEXT_76 = " = ue.getResponse();" + NL + "    }" + NL + "\t\t// for output";
  protected final String TEXT_77 = "\t\t" + NL + "\t\t\t\t";
  protected final String TEXT_78 = " = new ";
  protected final String TEXT_79 = "Struct();" + NL + "\t\t\t\tif(errorResponse_";
  protected final String TEXT_80 = "!=null){" + NL + "\t\t\t\t\t";
  protected final String TEXT_81 = ".ERROR_CODE = errorResponse_";
  protected final String TEXT_82 = ".getStatus();" + NL + "\t\t\t\t}else{" + NL + "\t\t\t\t\t";
  protected final String TEXT_83 = ".Body = restResponse_";
  protected final String TEXT_84 = ";" + NL + "\t\t\t\t}";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

String url = ElementParameterParser.getValue(node,"__URL__");
String method = ElementParameterParser.getValue(node,"__METHOD__");
List<Map<String, String>> headers = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node,"__HEADERS__");
        
String body = ElementParameterParser.getValue(node,"__BODY__");
body = body.replaceAll("[\r\n]", " ");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(url);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    
        for (int i = 0; i < headers.size(); i++) {
            Map<String, String> line = headers.get(i);

    stringBuffer.append(TEXT_65);
    stringBuffer.append( line.get("NAME") );
    stringBuffer.append(TEXT_66);
    stringBuffer.append( line.get("VALUE") );
    stringBuffer.append(TEXT_67);
    
        }

    stringBuffer.append(TEXT_68);
    
		if("GET".equals(method)){

    stringBuffer.append(TEXT_69);
    
		}else if("POST".equals(method)){

    stringBuffer.append(TEXT_70);
    stringBuffer.append(body);
    stringBuffer.append(TEXT_71);
    		
		}else if("PUT".equals(method)){

    stringBuffer.append(TEXT_72);
    stringBuffer.append(body);
    stringBuffer.append(TEXT_73);
    
		}else if("DELETE".equals(method)){

    stringBuffer.append(TEXT_74);
    
		}	

    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    
	List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
	if (conns!=null) {//1
		if (conns.size()>0) {//2
			IConnection conn = conns.get(0); //the first connection
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {//3

    stringBuffer.append(TEXT_77);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_80);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_82);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_84);
    			
			}//3
		}//2
	}//1

    return stringBuffer.toString();
  }
}
