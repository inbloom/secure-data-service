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

public class TPaloDimensionMainJava
{
  protected static String nl;
  public static synchronized TPaloDimensionMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TPaloDimensionMainJava result = new TPaloDimensionMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\tthPDims_";
  protected final String TEXT_3 = ".addMainInToTransformList(new String[]{";
  protected final String TEXT_4 = "});" + NL + "\t\tiCommitCounter_";
  protected final String TEXT_5 = "++;" + NL + "" + NL + "\t\tif(iCommitCounter_";
  protected final String TEXT_6 = " > ";
  protected final String TEXT_7 = "){" + NL + "\t\t\tthPDims_";
  protected final String TEXT_8 = ".buildParentChildRelationShipNormal();" + NL + "\t\t\tfor(org.talend.jpalo.talendHelpers.tPaloDimensionElements thPElem_";
  protected final String TEXT_9 = " : thPDims_";
  protected final String TEXT_10 = ".getParentChildList()){" + NL + "" + NL + "\t\t\t\t//if(pELMs_";
  protected final String TEXT_11 = ".getElement(thPElem_";
  protected final String TEXT_12 = ".getElementName())==null){" + NL + "" + NL + "\t\t\t\tswitch (thPElem_";
  protected final String TEXT_13 = ".getLevel()){" + NL + "\t\t";
  protected final String TEXT_14 = NL + "\t\t\t\t\tcase ";
  protected final String TEXT_15 = ":" + NL + "\t\t\t\t\t\tpELMs_";
  protected final String TEXT_16 = ".deleteElement(thPElem_";
  protected final String TEXT_17 = ".getElementName());\t\t\t" + NL + "\t\t\t\t\tbreak;" + NL + "\t\t";
  protected final String TEXT_18 = NL + "\t\t\t\t\tcase ";
  protected final String TEXT_19 = ":" + NL + "\t\t\t\t\t\t//if(pELMs_";
  protected final String TEXT_20 = ".getElement(thPElem_";
  protected final String TEXT_21 = ".getElementName())==null){" + NL + "\t\t\t\t\t\tpELMs_";
  protected final String TEXT_22 = ".createElement(thPElem_";
  protected final String TEXT_23 = ".getElementName(),org.talend.jpalo.paloelements.";
  protected final String TEXT_24 = ",org.talend.jpalo.paloelements.";
  protected final String TEXT_25 = ");" + NL + "\t\t\t\t\t//\t}" + NL + "\t\t\t\t\tbreak;" + NL + "\t\t";
  protected final String TEXT_26 = "\t\t\t" + NL + "\t\t\t\t}//}" + NL + "\t\t\t}" + NL + "\t\t";
  protected final String TEXT_27 = "\t" + NL + "\t\t\t\tfor(org.talend.jpalo.talendHelpers.tPaloDimensionElements thPElem_";
  protected final String TEXT_28 = " : thPDims_";
  protected final String TEXT_29 = ".getParentChildList()){" + NL + "\t\t\t\t\tthPDims_";
  protected final String TEXT_30 = ".buildConsolidationNormal(pELMs_";
  protected final String TEXT_31 = ", thPElem_";
  protected final String TEXT_32 = ".getElementName(), thPElem_";
  protected final String TEXT_33 = ".getPosition());" + NL + "\t\t\t\t}" + NL + "\t\t";
  protected final String TEXT_34 = NL + "\t\t\tthPDims_";
  protected final String TEXT_35 = " = new org.talend.jpalo.talendHelpers.tPaloDimensions();" + NL + "\t\t\tiCommitCounter_";
  protected final String TEXT_36 = "=0;" + NL + "\t\t}" + NL + "" + NL + "\t\t";
  protected final String TEXT_37 = NL + "\t\tthPDims_";
  protected final String TEXT_38 = ".addMainInToTransformList(new String[]{";
  protected final String TEXT_39 = ",";
  protected final String TEXT_40 = ",";
  protected final String TEXT_41 = "});" + NL + "\t\tiCommitCounter_";
  protected final String TEXT_42 = "++;" + NL + "\t\tif(iCommitCounter_";
  protected final String TEXT_43 = " > ";
  protected final String TEXT_44 = "){" + NL + "\t\t\tthPDims_";
  protected final String TEXT_45 = ".buildParentChildRelationShipReferenced();" + NL + "\t\t\tfor(org.talend.jpalo.talendHelpers.tPaloDimensionElements thPElem_";
  protected final String TEXT_46 = " : thPDims_";
  protected final String TEXT_47 = ".getParentChildListSorted()){" + NL + "\t\t\t\tpELMs_";
  protected final String TEXT_48 = ".createElement(thPElem_";
  protected final String TEXT_49 = ".getElementName(),org.talend.jpalo.paloelements.";
  protected final String TEXT_50 = ",org.talend.jpalo.paloelements.";
  protected final String TEXT_51 = ");" + NL + "\t\t\t}" + NL + "" + NL + "\t\t";
  protected final String TEXT_52 = NL + "\t\t\t\tfor(org.talend.jpalo.talendHelpers.tPaloDimensionElements thPElem_";
  protected final String TEXT_53 = " : thPDims_";
  protected final String TEXT_54 = ".getParentChildList()){" + NL + "\t\t\t\t\tthPDims_";
  protected final String TEXT_55 = ".buildConsolidationNormal(pELMs_";
  protected final String TEXT_56 = ", thPElem_";
  protected final String TEXT_57 = ".getElementName(), thPElem_";
  protected final String TEXT_58 = ".getPosition());" + NL + "\t\t\t\t}\t" + NL + "\t\t";
  protected final String TEXT_59 = NL + "\t\t\tthPDims_";
  protected final String TEXT_60 = " = new org.talend.jpalo.talendHelpers.tPaloDimensions();" + NL + "\t\t\tiCommitCounter_";
  protected final String TEXT_61 = "++;" + NL + "\t\t}" + NL + "\t\t";
  protected final String TEXT_62 = NL;
  protected final String TEXT_63 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();

	String sDimensionName = ElementParameterParser.getValue(node,"__DIMENSION__");
	// Further usage
	//String sDimensionType = ElementParameterParser.getValue(node,"__DIMENSION_TYPE__")
	String sDimensionType="NORMAL";
	boolean bCreateElements = "true".equals(ElementParameterParser.getValue(node,"__CREATE_ELEMENTS_BASED_ON_INPUT__"));
	boolean bDeleteElements = "true".equals(ElementParameterParser.getValue(node,"__DELETE_ALL_ELEMENTS_BEFORE__"));

	String strSRDimensionElementType = ElementParameterParser.getValue(node,"__ELEMENT_TYPE__");
	String strSRDimensionUpdateMode = ElementParameterParser.getValue(node,"__CREATION_MODE__");

	boolean bHierachieNone = "true".equals(ElementParameterParser.getValue(node,"__HIERACHIE_NONE__"));
	boolean bHierachieNormal = "true".equals(ElementParameterParser.getValue(node,"__HIERACHIE_NORMAL__"));
	boolean bHierachieSelfReferenced = "true".equals(ElementParameterParser.getValue(node,"__SELF_REFERENCED__"));
     
     String sCommitSize = ElementParameterParser.getValue(node,"__COMMIT_SIZE__");

	String strInputConnectionName="";
	List< ? extends IConnection> conns = node.getIncomingConnections();
 	if(conns!=null){
		if (conns.size()>0){
                IConnection con =conns.get(0);
		    strInputConnectionName=con.getName();
		}
	}

	
	List<Map<String, String>> tDimensionElements = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__ELEMENT_HIERACHIE__");
	List<Map<String, String>> tDimensionElementsSelfReferenced = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__ELEMENT_HIERACHIE_SELFREFERENCED__");

	

	if(bCreateElements && (bHierachieNone || bHierachieNormal) ){
		int iNbOfDimensionElements = tDimensionElements.size();
		StringBuilder sbX=new StringBuilder();
		for(int i=0; i<iNbOfDimensionElements; i++){
			Map<String, String> DimensionElement = tDimensionElements.get(i);
			String strDimensionElement = DimensionElement.get("SOURCE_COLUMN");
			String strDimensionElementType = DimensionElement.get("ELEMENT_TYPE");
			String strDimensionUpdateMode = DimensionElement.get("CREATION_MODE");

			if(i>0) sbX.append(",");
			sbX.append("String.valueOf("+strInputConnectionName+"."+strDimensionElement+")");
		}
		
		for (IConnection conn : conns) {
    		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {    		
    		  
		
		
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(sbX.toString());
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(sCommitSize);
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
    
				iNbOfDimensionElements = tDimensionElements.size();
				for(int i=0; i<iNbOfDimensionElements; i++){
					Map<String, String> DimensionElement = tDimensionElements.get(i);
					String strDimensionElement = DimensionElement.get("SOURCE_COLUMN");
					String strDimensionElementType = DimensionElement.get("ELEMENT_TYPE");
					String strDimensionUpdateMode = DimensionElement.get("CREATION_MODE");
					if(strDimensionUpdateMode.equals("DELETE")){
		
    stringBuffer.append(TEXT_14);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    
		
					}else{
		
    stringBuffer.append(TEXT_18);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(strDimensionElementType);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(strDimensionUpdateMode);
    stringBuffer.append(TEXT_25);
    
				}
			}
		
    stringBuffer.append(TEXT_26);
    
			if(bHierachieNormal){
		
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
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
    
			}
		
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    
				}
			}
			
	    }else if(bCreateElements && bHierachieSelfReferenced){
	    
	    	for (IConnection conn : conns) {
    		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {    		
    		  
		
		int iNbOfDimensionElements = tDimensionElementsSelfReferenced.size();
		
		String strParentColumn="\"\"";
		String strChildColumn="\"\"";
		String strFactorColumn="";
		
		for(int i=0; i<iNbOfDimensionElements; i++){
			Map<String, String> DimensionElement = tDimensionElementsSelfReferenced.get(i);
			String strDimensionElement = DimensionElement.get("SOURCE_COLUMN");
			String strDimensionHierachieType = DimensionElement.get("ELEMENT_HIERACHIE_TYPE");
	
			if(strDimensionHierachieType.equals("ELEMENT_PARENT")){
				strParentColumn="String.valueOf("+strInputConnectionName+"."+strDimensionElement+")";
			}else if(strDimensionHierachieType.equals("ELEMENT_CHILD")){
				strChildColumn="String.valueOf("+strInputConnectionName+"."+strDimensionElement+")";
			}else if(strDimensionHierachieType.equals("ELEMENT_CHILD_FACTOR")){
				strFactorColumn="String.valueOf("+strInputConnectionName+"."+strDimensionElement+")";
			}
		}
		if(strFactorColumn.equals("")) strFactorColumn="\"1\"";
		
		
		
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(strParentColumn);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(strChildColumn);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(strFactorColumn);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(sCommitSize);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(strSRDimensionElementType);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(strSRDimensionUpdateMode);
    stringBuffer.append(TEXT_51);
    
			if(bHierachieSelfReferenced){
		
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
    
			}
		
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    

	    }
	   }
	}
	

    stringBuffer.append(TEXT_62);
    stringBuffer.append(TEXT_63);
    return stringBuffer.toString();
  }
}
