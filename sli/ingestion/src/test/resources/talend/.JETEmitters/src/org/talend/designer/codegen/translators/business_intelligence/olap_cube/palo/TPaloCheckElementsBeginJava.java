package org.talend.designer.codegen.translators.business_intelligence.olap_cube.palo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TPaloCheckElementsBeginJava
{
  protected static String nl;
  public static synchronized TPaloCheckElementsBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TPaloCheckElementsBeginJava result = new TPaloCheckElementsBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "org.talend.jpalo.palo p_";
  protected final String TEXT_3 = " = null;" + NL + "org.talend.jpalo.paloconnection pConn_";
  protected final String TEXT_4 = " = null;" + NL;
  protected final String TEXT_5 = NL + "\t// Use Existing" + NL + "\tp_";
  protected final String TEXT_6 = " = (org.talend.jpalo.palo)globalMap.get(\"";
  protected final String TEXT_7 = "\");" + NL + "\tpConn_";
  protected final String TEXT_8 = " =  (org.talend.jpalo.paloconnection)globalMap.get(\"";
  protected final String TEXT_9 = "\");";
  protected final String TEXT_10 = NL + "\t// Initialize jpalo" + NL + "\tp_";
  protected final String TEXT_11 = " = new org.talend.jpalo.palo(";
  protected final String TEXT_12 = ");" + NL + "\t// Open the connection" + NL + "\tpConn_";
  protected final String TEXT_13 = " = p_";
  protected final String TEXT_14 = ".connect(";
  protected final String TEXT_15 = ", ";
  protected final String TEXT_16 = ", ";
  protected final String TEXT_17 = ", ";
  protected final String TEXT_18 = ");";
  protected final String TEXT_19 = NL + NL + "org.talend.jpalo.palodatabases pDBs_";
  protected final String TEXT_20 = " = pConn_";
  protected final String TEXT_21 = ".getDatabases();" + NL + "org.talend.jpalo.palodatabase pDB_";
  protected final String TEXT_22 = "= pDBs_";
  protected final String TEXT_23 = ".getDatabase(";
  protected final String TEXT_24 = ");" + NL + "" + NL + " " + NL + "org.talend.jpalo.palocube pCB_";
  protected final String TEXT_25 = " = pDB_";
  protected final String TEXT_26 = ".getCubes(org.talend.jpalo.palocubes.CUBE_NORMAL).getCube(";
  protected final String TEXT_27 = ");" + NL;
  protected final String TEXT_28 = NL + "\t\t\t\tList<String>[] lsElements_";
  protected final String TEXT_29 = ";" + NL + "\t\t\t\tList<String>[] lsElementsReplace_";
  protected final String TEXT_30 = ";" + NL + "\t\t\t\torg.talend.jpalo.palodimensions pDIMs_";
  protected final String TEXT_31 = " = pCB_";
  protected final String TEXT_32 = ".getDimensions();" + NL + "\t\t\t\tlsElements_";
  protected final String TEXT_33 = " = new List[pCB_";
  protected final String TEXT_34 = ".getDimensions().getNumberOfDimensions()];" + NL + "\t\t\t\tfor(int i=0;i<pDIMs_";
  protected final String TEXT_35 = ".getNumberOfDimensions();i++){" + NL + "\t\t\t\t\torg.talend.jpalo.palodimension pDIM_";
  protected final String TEXT_36 = " = pDIMs_";
  protected final String TEXT_37 = ".getDimension(i);" + NL + "\t\t\t\t\tlsElements_";
  protected final String TEXT_38 = "[i] = new java.util.ArrayList<String>();" + NL + "\t\t\t\t\tfor(org.talend.jpalo.paloelement pELM_";
  protected final String TEXT_39 = " : pDIM_";
  protected final String TEXT_40 = ".getElements().getElements()){" + NL + "\t\t\t\t\t\tlsElements_";
  protected final String TEXT_41 = "[i].add(pELM_";
  protected final String TEXT_42 = ".getName());" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t}";
  protected final String TEXT_43 = NL + "int iNbOfCheckedRows_";
  protected final String TEXT_44 = " = 0;" + NL + "boolean bRejectRow_";
  protected final String TEXT_45 = " = false;" + NL + "StringBuilder sbErrorText_";
  protected final String TEXT_46 = " = new StringBuilder();" + NL + "int iErrorCounter_";
  protected final String TEXT_47 = " = 0;";
  protected final String TEXT_48 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();

	boolean useExistingConnection = "true".equals(ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__"));


	String sServer = ElementParameterParser.getValue(node, "__SERVER__");
	String sServerport = ElementParameterParser.getValue(node, "__SERVERPORT__");
	String sUsername = ElementParameterParser.getValue(node, "__USERNAME__");
	String sPassword = ElementParameterParser.getValue(node, "__PASS__");

	String sDatabaseName = ElementParameterParser.getValue(node,"__DATABASE__");
	String sCubeName = ElementParameterParser.getValue(node,"__CUBE__");

	 //String sMeasureColumn= ElementParameterParser.getValue(node,"__MEASURE_COLUMN__");

    String sDeploypalolibs = ElementParameterParser.getValue(node, "__DEPLOY_PALO_LIBS__");
    boolean bDeploypalolibs=false;
    if(sDeploypalolibs.equals("true"))bDeploypalolibs=true;


    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    
if(useExistingConnection){
	String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
	String p = "p_" + connection;
	String pConn = "pConn_" + connection;


    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(p);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(pConn);
    stringBuffer.append(TEXT_9);
    
}else{

    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(bDeploypalolibs);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(sUsername);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(sPassword);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(sServer);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(sServerport);
    stringBuffer.append(TEXT_18);
    
}

    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(sDatabaseName);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(sCubeName);
    stringBuffer.append(TEXT_27);
    
	String strInputConnectionName="";
	List< ? extends IConnection> conns = node.getIncomingConnections();
 	if(conns!=null){
		if (conns.size()>0){
                IConnection conn =conns.get(0);
		    strInputConnectionName=conn.getName();
		}
	}

	if(strInputConnectionName.length()>0){
		List<IMetadataTable> metadatas = node.getMetadataList();
		if ((metadatas!=null)&&(metadatas.size()>0)) {
			IMetadataTable metadata = metadatas.get(0);
			if (metadata!=null) {


    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
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
    
			}
		}
	}	

    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(TEXT_48);
    return stringBuffer.toString();
  }
}
