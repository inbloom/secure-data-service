package org.talend.designer.codegen.translators.business_intelligence.olap_cube.palo;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;

public class TPaloOutputMainJava
{
  protected static String nl;
  public static synchronized TPaloOutputMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TPaloOutputMainJava result = new TPaloOutputMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t\t\tnb_line_";
  protected final String TEXT_2 = "=nb_line_";
  protected final String TEXT_3 = " + 1;";
  protected final String TEXT_4 = NL + "\t\t\t\t\t\tif(plDims_";
  protected final String TEXT_5 = ".getDimensionAtPos(";
  protected final String TEXT_6 = ").getElements().getElementByName(String.valueOf(";
  protected final String TEXT_7 = ".";
  protected final String TEXT_8 = ")) == null){" + NL + "\t\t\t\t\t\t\tplDims_";
  protected final String TEXT_9 = ".getDimensionAtPos(";
  protected final String TEXT_10 = ").getElements().createElement(plDims_";
  protected final String TEXT_11 = ".getDimensionAtPos(";
  protected final String TEXT_12 = ").getName(),String.valueOf(";
  protected final String TEXT_13 = ".";
  protected final String TEXT_14 = "));" + NL + "\t\t\t\t\t\t}";
  protected final String TEXT_15 = NL + NL + "\t\t\t\t" + NL + "\t\t\t\tstrArrTalendQuery[";
  protected final String TEXT_16 = "]=String.valueOf(";
  protected final String TEXT_17 = ".";
  protected final String TEXT_18 = ");";
  protected final String TEXT_19 = NL + "\t\t\torg.talend.palo.paloIXData.setData(strArrTalendQuery,";
  protected final String TEXT_20 = ".";
  protected final String TEXT_21 = ",database_";
  protected final String TEXT_22 = ",cube_";
  protected final String TEXT_23 = ");" + NL + "\t\t\tnb_commit_count_";
  protected final String TEXT_24 = "=nb_commit_count_";
  protected final String TEXT_25 = " + 1;" + NL + "\t\t\tif(nb_commit_count_";
  protected final String TEXT_26 = " >= ";
  protected final String TEXT_27 = "){" + NL + "\t\t\t\tplDb_";
  protected final String TEXT_28 = ".commitCube(cube_";
  protected final String TEXT_29 = ");" + NL + "\t\t\t\tnb_commit_count_";
  protected final String TEXT_30 = " =0;" + NL + "\t\t\t}\t\t\t";
  protected final String TEXT_31 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	List< ? extends IConnection> conns = node.getIncomingConnections();
	List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas!=null)&&(metadatas.size()>0)) {
		IMetadataTable metadata = metadatas.get(0);
		if (metadata!=null && conns.size()>0) {
			String measureColumn = ElementParameterParser.getValue(node, "__MEASURE_COLUMN__");
			boolean isCreateElement = ("true").equals(ElementParameterParser.getValue(node,"__CREATEELEM__"));
			String commitsize = ElementParameterParser.getValue(node, "__COMMITSIZE__");
			IConnection conn =conns.get(0);
			List<IMetadataColumn> columns = metadata.getListColumns();
			int sizeColumns = columns.size();
			int iArrPos=0;

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    
	    		for (int i = 0; i < sizeColumns; i++) {
	    			IMetadataColumn column = columns.get(i);
				String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
				JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
				String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
	      		if(!column.getLabel().equals(measureColumn)){
					if(isCreateElement){

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(iArrPos );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(iArrPos );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(iArrPos );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_14);
    
					}

    stringBuffer.append(TEXT_15);
    stringBuffer.append(iArrPos++ );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_18);
    
				}
	     		}

    stringBuffer.append(TEXT_19);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(measureColumn );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(commitsize );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    
		}
	}

    stringBuffer.append(TEXT_31);
    return stringBuffer.toString();
  }
}
