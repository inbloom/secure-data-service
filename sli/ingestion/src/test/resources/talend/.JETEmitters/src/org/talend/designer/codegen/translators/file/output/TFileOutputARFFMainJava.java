package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;

public class TFileOutputARFFMainJava
{
  protected static String nl;
  public static synchronized TFileOutputARFFMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputARFFMainJava result = new TFileOutputARFFMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "        vals";
  protected final String TEXT_2 = "=new double[m_Data";
  protected final String TEXT_3 = ".numAttributes()];" + NL + "        weka.core.Instance instt";
  protected final String TEXT_4 = ";";
  protected final String TEXT_5 = NL + "                                    //insert String value" + NL + "                                    vals";
  protected final String TEXT_6 = "[";
  protected final String TEXT_7 = "]=m_Data";
  protected final String TEXT_8 = ".attribute(";
  protected final String TEXT_9 = ").addStringValue(";
  protected final String TEXT_10 = ".";
  protected final String TEXT_11 = ");";
  protected final String TEXT_12 = NL + "                                    //insert Numeric value" + NL + "                                    vals";
  protected final String TEXT_13 = "[";
  protected final String TEXT_14 = "]=";
  protected final String TEXT_15 = ".";
  protected final String TEXT_16 = ";";
  protected final String TEXT_17 = NL + "                                    //insert Date value" + NL + "                                    vals";
  protected final String TEXT_18 = "[";
  protected final String TEXT_19 = "]=m_Data";
  protected final String TEXT_20 = ".attribute(";
  protected final String TEXT_21 = ").parseDate(new java.text.SimpleDateFormat(";
  protected final String TEXT_22 = ").format(";
  protected final String TEXT_23 = ".";
  protected final String TEXT_24 = "));";
  protected final String TEXT_25 = NL + "                                    //insert Nominal value          " + NL + "                                    vals";
  protected final String TEXT_26 = "[";
  protected final String TEXT_27 = "]=classValues";
  protected final String TEXT_28 = ".indexOf(";
  protected final String TEXT_29 = ".";
  protected final String TEXT_30 = ");";
  protected final String TEXT_31 = "  " + NL + "             instt";
  protected final String TEXT_32 = "=new weka.core.Instance(1.0,vals";
  protected final String TEXT_33 = ");" + NL + "             m_Data";
  protected final String TEXT_34 = ".add(instt";
  protected final String TEXT_35 = ");\t\t\t";
  protected final String TEXT_36 = NL + "        " + NL + "        nb_line_";
  protected final String TEXT_37 = "++;     " + NL + "        " + NL + "        ";
  protected final String TEXT_38 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {
        String cid = node.getUniqueName();
        String filename = ElementParameterParser.getValue(node,"__FILENAME__");
    	boolean isAppend = ("true").equals(ElementParameterParser.getValue(node,"__APPEND__"));
    	List<Map<String, String>> colDef = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__COLDEFINE__");
    	List< ? extends IConnection> conns = node.getIncomingConnections();
    	List<IMetadataColumn> columnList;

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    
    	//get data from IncomingConnections
        for (IConnection conn : conns) {
            if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
                INode inNode=conn.getSource();
                List<IMetadataTable> inMetaTables=inNode.getMetadataList();           
                if(inMetaTables !=null && inMetaTables.size()>0){
                    IMetadataTable inMetaTable=inMetaTables.get(0);
                    if(inMetaTable != null){
                        columnList = inMetaTable.getListColumns();
                int colNum=0;
                //for(Map<String, String> colD:colDef){
                        for(IMetadataColumn column:columnList){
                           // if(colDef.get(colNum).get("REFCOL").equals(column.getLabel())){
                                if(("String").equals(colDef.get(colNum).get("TYPE"))){
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(colNum);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(colNum);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_11);
                        }else if(("Numeric").equals(colDef.get(colNum).get("TYPE"))){
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(colNum);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_16);
                        }else if(("Date").equals(colDef.get(colNum).get("TYPE"))){
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(colNum);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(colNum);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(colDef.get(colNum).get("PATTERN"));
    stringBuffer.append(TEXT_22);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_24);
                        }else if(("Nominal").equals(colDef.get(colNum).get("TYPE"))){
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(colNum);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(colNum);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_30);
                        }
                         //   }
                        //}
                        colNum++;
                 }
                }
              }
                
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
       		
            }//end if connection is DATA
        }//end for Iconnection conn:conns

    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
           
	}//end if metadata!=null

}

    stringBuffer.append(TEXT_38);
    return stringBuffer.toString();
  }
}
