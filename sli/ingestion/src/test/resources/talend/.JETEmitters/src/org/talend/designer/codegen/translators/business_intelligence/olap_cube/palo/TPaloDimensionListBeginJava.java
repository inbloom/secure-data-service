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

public class TPaloDimensionListBeginJava
{
  protected static String nl;
  public static synchronized TPaloDimensionListBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TPaloDimensionListBeginJava result = new TPaloDimensionListBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\tint NB_DIMENSIONS";
  protected final String TEXT_3 = " = 0;  " + NL + "\torg.talend.jpalo.palo p_";
  protected final String TEXT_4 = " = null;" + NL + "\torg.talend.jpalo.paloconnection pConn_";
  protected final String TEXT_5 = " = null;" + NL;
  protected final String TEXT_6 = NL + "\t// Use Existing" + NL + "\tp_";
  protected final String TEXT_7 = " = (org.talend.jpalo.palo)globalMap.get(\"";
  protected final String TEXT_8 = "\");" + NL + "\tpConn_";
  protected final String TEXT_9 = " =  (org.talend.jpalo.paloconnection)globalMap.get(\"";
  protected final String TEXT_10 = "\");";
  protected final String TEXT_11 = NL + "\t// Initialize jpalo" + NL + "\tp_";
  protected final String TEXT_12 = " = new org.talend.jpalo.palo(";
  protected final String TEXT_13 = ");" + NL + "\t// Open the connection" + NL + "\tpConn_";
  protected final String TEXT_14 = " = p_";
  protected final String TEXT_15 = ".connect(";
  protected final String TEXT_16 = ", ";
  protected final String TEXT_17 = ", ";
  protected final String TEXT_18 = ", ";
  protected final String TEXT_19 = ");";
  protected final String TEXT_20 = NL + NL;
  protected final String TEXT_21 = NL + NL + "org.talend.jpalo.palodatabases pDBs_";
  protected final String TEXT_22 = " = pConn_";
  protected final String TEXT_23 = ".getDatabases();" + NL + "org.talend.jpalo.palodatabase pDB_";
  protected final String TEXT_24 = " = pDBs_";
  protected final String TEXT_25 = ".getDatabase(";
  protected final String TEXT_26 = ");" + NL + "if(pDB_";
  protected final String TEXT_27 = " == null){" + NL + "\t throw new RuntimeException (\"Database '\" + ";
  protected final String TEXT_28 = " + \"' not found. exiting...\");" + NL + "}" + NL + "" + NL + "org.talend.jpalo.palocube pCUB_";
  protected final String TEXT_29 = " = null;" + NL + "org.talend.jpalo.palodimension pDIM_";
  protected final String TEXT_30 = " = null;" + NL;
  protected final String TEXT_31 = NL + "\t\t//pCUB_";
  protected final String TEXT_32 = " = pDB_";
  protected final String TEXT_33 = ".getCubes(org.talend.jpalo.palocubes.CUBE_NORMAL).getCube();";
  protected final String TEXT_34 = NL + NL;
  protected final String TEXT_35 = NL + "\tfor(int i_";
  protected final String TEXT_36 = "=0;i_";
  protected final String TEXT_37 = "<4;i_";
  protected final String TEXT_38 = "++){" + NL + "\t\torg.talend.jpalo.palodimensions pDIMs_";
  protected final String TEXT_39 = "=null;";
  protected final String TEXT_40 = NL + "\t\torg.talend.jpalo.palocube pCB_";
  protected final String TEXT_41 = " = pDB_";
  protected final String TEXT_42 = ".getCubes(i_";
  protected final String TEXT_43 = ").getCube(";
  protected final String TEXT_44 = ");" + NL + "\t\t" + NL + "\t\tif(null!=pCB_";
  protected final String TEXT_45 = ")\t{" + NL + "\t\t    pDIMs_";
  protected final String TEXT_46 = " = pCB_";
  protected final String TEXT_47 = ".getDimensions();" + NL + "\t\t";
  protected final String TEXT_48 = NL + "\t\tpDIMs_";
  protected final String TEXT_49 = " = pDB_";
  protected final String TEXT_50 = ".getDimensions(i_";
  protected final String TEXT_51 = ");";
  protected final String TEXT_52 = "\t\t" + NL + "" + NL + "\t\tif(null!=pDIMs_";
  protected final String TEXT_53 = "){" + NL + "\t\tfor(int j_";
  protected final String TEXT_54 = "=0;j_";
  protected final String TEXT_55 = "< pDIMs_";
  protected final String TEXT_56 = ".getNumberOfDimensions();j_";
  protected final String TEXT_57 = "++){" + NL + "\t\t\tpDIM_";
  protected final String TEXT_58 = "=pDIMs_";
  protected final String TEXT_59 = ".getDimension(j_";
  protected final String TEXT_60 = ");" + NL + "\t\t\tNB_DIMENSIONS";
  protected final String TEXT_61 = "++;";
  protected final String TEXT_62 = "\t\t" + NL + "\t\t\t";
  protected final String TEXT_63 = ".dimension_id=pDIM_";
  protected final String TEXT_64 = ".getDimensionId();" + NL + "\t\t\t";
  protected final String TEXT_65 = ".dimension_name=pDIM_";
  protected final String TEXT_66 = ".getName();" + NL + "\t\t\t";
  protected final String TEXT_67 = ".dimension_attribut_cube=pDIM_";
  protected final String TEXT_68 = ".getAttributCube();" + NL + "\t\t\t";
  protected final String TEXT_69 = ".dimension_rights_cube=pDIM_";
  protected final String TEXT_70 = ".getRightsCube();" + NL + "\t\t\t";
  protected final String TEXT_71 = ".dimension_elements=pDIM_";
  protected final String TEXT_72 = ".getNumberOfElements();" + NL + "\t\t\t";
  protected final String TEXT_73 = ".dimension_max_level=pDIM_";
  protected final String TEXT_74 = ".getMaximumLevel();" + NL + "\t\t\t";
  protected final String TEXT_75 = ".dimension_max_indent=pDIM_";
  protected final String TEXT_76 = ".getMaximumIndent();" + NL + "\t\t" + NL + "\t\t\t";
  protected final String TEXT_77 = ".dimension_type=pDIM_";
  protected final String TEXT_78 = ".getDimensionType();";
  protected final String TEXT_79 = NL + "\t\t\tglobalMap.put(\"";
  protected final String TEXT_80 = "_DIMENSIONNAME\", pDIM_";
  protected final String TEXT_81 = ".getName());" + NL + "\t\t";
  protected final String TEXT_82 = NL;
  protected final String TEXT_83 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();

    boolean useExistingConnection = "true".equals(ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__"));
    boolean useRetriveFromCube = "true".equals(ElementParameterParser.getValue(node,"__RETRIEVE_DIMENSIONS_FROM_CUBE__"));

    String sServer = ElementParameterParser.getValue(node, "__SERVER__");
    String sServerport = ElementParameterParser.getValue(node, "__SERVERPORT__");
    String sUsername = ElementParameterParser.getValue(node, "__USERNAME__");
    String sPassword = ElementParameterParser.getValue(node, "__PASS__");

    String sDatabaseName = ElementParameterParser.getValue(node,"__DATABASE__");
    String sCubeName = ElementParameterParser.getValue(node,"__CUBE__");
    

    String sDeploypalolibs = ElementParameterParser.getValue(node, "__DEPLOY_PALO_LIBS__");
    boolean bDeploypalolibs=false;
    if(sDeploypalolibs.equals("true"))bDeploypalolibs=true;


    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    
if(useExistingConnection){
	String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
	String p = "p_" + connection;
	String pConn = "pConn_" + connection;


    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(p);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(pConn);
    stringBuffer.append(TEXT_10);
    
}else{

    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(bDeploypalolibs);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(sUsername);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(sPassword);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(sServer);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(sServerport);
    stringBuffer.append(TEXT_19);
    
}

    stringBuffer.append(TEXT_20);
    
	String outputConnName = null;
	boolean bIterate=false;
	boolean bData=false;
	List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
	if (conns!=null) {
		if (conns.size()>0) {
			for (int i=0;i<conns.size();i++) {
				IConnection connTemp = conns.get(i);
				if (connTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
					outputConnName = connTemp.getName();
					bData=true;
					//break;
				}
			}
			for (int i=0;i<conns.size();i++) {
				IConnection connTemp = conns.get(i);
				if(connTemp.getLineStyle().toString().equals("ITERATE")) {
					bIterate=true;
					//break;
				}
			}
	
		}
	}

    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(sDatabaseName);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(sDatabaseName);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    
	if(useRetriveFromCube){

    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    
	}

    stringBuffer.append(TEXT_34);
    
if (outputConnName != null || bIterate){

    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    
	if(useRetriveFromCube){

    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(sCubeName);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    
	}else{

    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    
	}

    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    
		if(bData){

    stringBuffer.append(TEXT_62);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_72);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(outputConnName);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_78);
    
		}
		if(bIterate){

    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_81);
    
		}
}

    stringBuffer.append(TEXT_82);
    stringBuffer.append(TEXT_83);
    return stringBuffer.toString();
  }
}
