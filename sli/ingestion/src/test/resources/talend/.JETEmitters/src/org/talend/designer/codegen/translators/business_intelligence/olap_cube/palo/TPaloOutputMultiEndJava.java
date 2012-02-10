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

public class TPaloOutputMultiEndJava
{
  protected static String nl;
  public static synchronized TPaloOutputMultiEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TPaloOutputMultiEndJava result = new TPaloOutputMultiEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\ttry{" + NL + "\t\t\tplDT_";
  protected final String TEXT_3 = ".setData(pCB_";
  protected final String TEXT_4 = ", org.talend.jpalo.palodata.";
  protected final String TEXT_5 = ", ";
  protected final String TEXT_6 = ",  ";
  protected final String TEXT_7 = " );" + NL + "\t\t}catch(Exception plE_";
  protected final String TEXT_8 = "){" + NL + "\t\t";
  protected final String TEXT_9 = NL + "\t\t\t\tthrow(plE_";
  protected final String TEXT_10 = ");" + NL + "\t\t";
  protected final String TEXT_11 = NL + "\t\t\t\tSystem.err.print(plE_";
  protected final String TEXT_12 = ".getMessage());" + NL + "\t\t";
  protected final String TEXT_13 = NL + "\t\t}" + NL + "\t\t";
  protected final String TEXT_14 = NL;
  protected final String TEXT_15 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();

	String sCommitSize= ElementParameterParser.getValue(node,"__COMMITSIZE__");
	String sMeasureColumn= ElementParameterParser.getValue(node,"__MEASURE_COLUMN__");
	String sSplashMode= ElementParameterParser.getValue(node,"__SPLASH_MODE__");

	boolean bUseEventprocessor = "true".equals(ElementParameterParser.getValue(node,"__EVENTPROCESSOR__"));
	boolean bAddValues = "true".equals(ElementParameterParser.getValue(node,"__ADDVALUES__"));

	boolean bDieOnError = "true".equals(ElementParameterParser.getValue(node,"__DIE_ON_ERROR__"));



	String strInputConnectionName="";
	List< ? extends IConnection> conns = node.getIncomingConnections();
 	if(conns!=null){
		if (conns.size()>0){
                IConnection conn =conns.get(0);
		    strInputConnectionName=conn.getName();
		}
	}

	if(strInputConnectionName.length()>0){

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(sSplashMode);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(bAddValues);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(bUseEventprocessor);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    
			if(bDieOnError){
		
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    
			}else{
		
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    
			}
		
    stringBuffer.append(TEXT_13);
    
	}

    stringBuffer.append(TEXT_14);
    stringBuffer.append(TEXT_15);
    return stringBuffer.toString();
  }
}
