package org.talend.designer.codegen.translators.business.alfresco;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;

public class TAlfrescoOutputEndJava
{
  protected static String nl;
  public static synchronized TAlfrescoOutputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TAlfrescoOutputEndJava result = new TAlfrescoOutputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "        " + NL + "      try {" + NL + "         talendAcpXmlWriter_";
  protected final String TEXT_3 = ".close();" + NL + "      } catch (fr.openwide.talendalfresco.acpxml.AcpXmlException e) {" + NL + "         throw new fr.openwide.talendalfresco.rest.client.RestClientException(\"Error creating XML result\", e);" + NL + "      }" + NL + "      " + NL + "      String content_";
  protected final String TEXT_4 = " = talendAcpXmlWriter_";
  protected final String TEXT_5 = ".toString();" + NL + "      ByteArrayInputStream acpXmlIs_";
  protected final String TEXT_6 = " = new ByteArrayInputStream(content_";
  protected final String TEXT_7 = ".getBytes());" + NL + "      " + NL + "      fr.openwide.talendalfresco.rest.client.ClientImportCommand importCmd_";
  protected final String TEXT_8 = " = new fr.openwide.talendalfresco.rest.client.ClientImportCommand(\"";
  protected final String TEXT_9 = "\", acpXmlIs_";
  protected final String TEXT_10 = ");" + NL + "      importCmd_";
  protected final String TEXT_11 = ".setDocumentMode(\"";
  protected final String TEXT_12 = "\");" + NL + "      importCmd_";
  protected final String TEXT_13 = ".setContainerMode(\"";
  protected final String TEXT_14 = "\");" + NL + "   " + NL + "      // Execute the command." + NL + "      alfrescoRestClient_";
  protected final String TEXT_15 = ".execute(importCmd_";
  protected final String TEXT_16 = ");" + NL + "    " + NL + "      // logout" + NL + "      try {" + NL + "      \talfrescoRestClient_";
  protected final String TEXT_17 = ".logout();" + NL + "      } catch (fr.openwide.talendalfresco.rest.client.RestClientException e) {" + NL + "         throw new fr.openwide.talendalfresco.rest.client.RestClientException(\"Error initing client\", e);" + NL + "      }" + NL + "      " + NL + "      // setting global var NB_LINE for further components" + NL + "      globalMap.put(\"";
  protected final String TEXT_18 = "_NB_LINE\", nbLine_";
  protected final String TEXT_19 = ");" + NL + "      " + NL + "      " + NL + "      // handling result logs" + NL + "      System.out.println(importCmd_";
  protected final String TEXT_20 = ".toString() + \" \" + importCmd_";
  protected final String TEXT_21 = ".getResultMessage() + \" \" + importCmd_";
  protected final String TEXT_22 = ".getResultError());" + NL + "      " + NL + "    java.io.File resultLogFile_";
  protected final String TEXT_23 = " = new java.io.File(";
  protected final String TEXT_24 = ");" + NL + "    java.io.BufferedWriter resultLogOut_";
  protected final String TEXT_25 = " = new java.io.BufferedWriter(" + NL + "            new java.io.OutputStreamWriter(new java.io.FileOutputStream(resultLogFile_";
  protected final String TEXT_26 = ")));" + NL + "      for (String[] resultLog_";
  protected final String TEXT_27 = " : importCmd_";
  protected final String TEXT_28 = ".getResultLogs()) {" + NL + "         String successOrError_";
  protected final String TEXT_29 = " = resultLog_";
  protected final String TEXT_30 = "[0];" + NL + "         resultLogOut_";
  protected final String TEXT_31 = ".write(successOrError_";
  protected final String TEXT_32 = "); // success or error" + NL + "         resultLogOut_";
  protected final String TEXT_33 = ".write(\";\");" + NL + "         resultLogOut_";
  protected final String TEXT_34 = ".write(resultLog_";
  protected final String TEXT_35 = "[1]); // namepath" + NL + "         resultLogOut_";
  protected final String TEXT_36 = ".write(\";\");" + NL + "         String message_";
  protected final String TEXT_37 = " = resultLog_";
  protected final String TEXT_38 = "[2];" + NL + "         if (message_";
  protected final String TEXT_39 = " != null) {" + NL + "         \tif (!\"success\".equals(successOrError_";
  protected final String TEXT_40 = ")) {" + NL + "         \t\tmessage_";
  protected final String TEXT_41 = " = message_";
  protected final String TEXT_42 = ".replaceAll(\";\", \",\").replaceAll(\"\\n\", \"   \");" + NL + "         \t}" + NL + "         \tresultLogOut_";
  protected final String TEXT_43 = ".write(message_";
  protected final String TEXT_44 = "); // message" + NL + "         \tresultLogOut_";
  protected final String TEXT_45 = ".write(\";\");" + NL + "         }" + NL + "         resultLogOut_";
  protected final String TEXT_46 = ".write(resultLog_";
  protected final String TEXT_47 = "[3]); // date" + NL + "         resultLogOut_";
  protected final String TEXT_48 = ".write(\";\");" + NL + "         if (resultLog_";
  protected final String TEXT_49 = "[4] != null) {" + NL + "         \tresultLogOut_";
  protected final String TEXT_50 = ".write(resultLog_";
  protected final String TEXT_51 = "[4]); // noderef" + NL + "         \tresultLogOut_";
  protected final String TEXT_52 = ".write(\";\");" + NL + "         }" + NL + "         if (resultLog_";
  protected final String TEXT_53 = "[5] != null) {" + NL + "         \tresultLogOut_";
  protected final String TEXT_54 = ".write(resultLog_";
  protected final String TEXT_55 = "[5]); // doctype" + NL + "         }" + NL + "         resultLogOut_";
  protected final String TEXT_56 = ".write(\"\\n\");" + NL + "      }" + NL + "    resultLogOut_";
  protected final String TEXT_57 = ".close();" + NL + "    " + NL + "    System.out.println(\"Result (log file \" + ";
  protected final String TEXT_58 = " + \") :\\n\");" + NL + "    java.io.BufferedReader resultLogIn_";
  protected final String TEXT_59 = " = new java.io.BufferedReader(" + NL + "            new java.io.InputStreamReader(new java.io.FileInputStream(resultLogFile_";
  protected final String TEXT_60 = ")));" + NL + "    String resultLogInLine_";
  protected final String TEXT_61 = ";" + NL + "    while  ((resultLogInLine_";
  protected final String TEXT_62 = " = resultLogIn_";
  protected final String TEXT_63 = ".readLine()) != null) {" + NL + "    \tSystem.out.println(resultLogInLine_";
  protected final String TEXT_64 = ");" + NL + "    }" + NL + "    resultLogIn_";
  protected final String TEXT_65 = ".close();";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
// 3. in end.javajet, we output the last part of the acp xml after to its documents (i.e. rows),
// then call the alfresco server with it as a parameter, then handle the returned result
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {
        
        // getting useful parameters
	    String targetLocationBase = ElementParameterParser.getValue(node, "__TARGET_LOCATION_BASE__");
	    
	    String documentMode = ElementParameterParser.getValue(node, "__DOCUMENT_MODE__");
	    String containerMode = ElementParameterParser.getValue(node, "__CONTAINER_MODE__");
        
	    String resultLogFilenameString = ElementParameterParser.getValue(node, "__RESULT_LOG_FILENAME__");
        
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
    stringBuffer.append(targetLocationBase);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(documentMode);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(containerMode);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
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
    stringBuffer.append(resultLogFilenameString);
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
    stringBuffer.append(resultLogFilenameString);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    
        
        
    }
}

    return stringBuffer.toString();
  }
}
