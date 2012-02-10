package org.talend.designer.codegen.translators.file.input;

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

public class TFileInputARFFBeginJava
{
  protected static String nl;
  public static synchronized TFileInputARFFBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileInputARFFBeginJava result = new TFileInputARFFBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "\t\t" + NL + "\t\tint nb_line_";
  protected final String TEXT_3 = " = 0;" + NL + "\t\tint totalLine";
  protected final String TEXT_4 = " = 0;" + NL + "\t\tString filename";
  protected final String TEXT_5 = "=";
  protected final String TEXT_6 = ";" + NL + "\t\tweka.core.Instances data";
  protected final String TEXT_7 = " = new weka.core.Instances(new java.io.BufferedReader(new java.io.FileReader(filename";
  protected final String TEXT_8 = ")));" + NL + "        data";
  protected final String TEXT_9 = ".setClassIndex(data";
  protected final String TEXT_10 = ".numAttributes()-1);" + NL + "        //read line" + NL + "        for(int lineNo=0;lineNo<data";
  protected final String TEXT_11 = ".numInstances();lineNo++){" + NL + "           //read column" + NL + "            " + NL + "            " + NL + "            ";
  protected final String TEXT_12 = "          ";
  protected final String TEXT_13 = NL + "                     ";
  protected final String TEXT_14 = ".";
  protected final String TEXT_15 = " = data";
  protected final String TEXT_16 = ".instance(lineNo).stringValue(";
  protected final String TEXT_17 = ");";
  protected final String TEXT_18 = NL + "                     ";
  protected final String TEXT_19 = ".";
  protected final String TEXT_20 = " = new java.text.SimpleDateFormat(";
  protected final String TEXT_21 = ").parse(data";
  protected final String TEXT_22 = ".instance(lineNo).stringValue(";
  protected final String TEXT_23 = "));";
  protected final String TEXT_24 = NL + "                     ";
  protected final String TEXT_25 = ".";
  protected final String TEXT_26 = " = (";
  protected final String TEXT_27 = ")data";
  protected final String TEXT_28 = ".instance(lineNo).value(";
  protected final String TEXT_29 = ");";
  protected final String TEXT_30 = NL + NL + NL + NL + NL + NL + "             " + NL + "        //}";
  protected final String TEXT_31 = NL + NL + "\t\t" + NL + "\t\t" + NL + "\t\t" + NL + "\t\t" + NL + "\t\t" + NL + "\t\t";
  protected final String TEXT_32 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null) {
        String cid = node.getUniqueName();      
        String filename = ElementParameterParser.getValue(node,"__FILENAME__");
    	String encoding = ElementParameterParser.getValue(node,"__ENCODING__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(filename);
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
    
 
 List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
 String firstConnName = "";
 if (conns!=null) {
     if (conns.size()>0) {
         for(IConnection conn:conns){
         firstConnName = conn.getName();
         
         if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
      
             List<IMetadataColumn> listColumns = metadata.getListColumns();
             int colNo = 0;
             for (IMetadataColumn column: listColumns) {
                 if(("id_String").equals(column.getTalendType())){

    stringBuffer.append(TEXT_12);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(colNo);
    stringBuffer.append(TEXT_17);
                   }else if(("id_Date").equals(column.getTalendType())){
    stringBuffer.append(TEXT_18);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(column.getPattern());
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(colNo);
    stringBuffer.append(TEXT_23);
                   }else{
    stringBuffer.append(TEXT_24);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_25);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_26);
    stringBuffer.append(JavaTypesManager.getJavaTypeFromId(column.getTalendType()).getPrimitiveClass());
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(colNo);
    stringBuffer.append(TEXT_29);
                   }
                 colNo++;
             }       
         }
        } 
     }
 }

    stringBuffer.append(TEXT_30);
    
    }// end if (metadata!=null) 
}// end if ((metadatas!=null)&&(metadatas.size()>0)) 

    stringBuffer.append(TEXT_31);
    stringBuffer.append(TEXT_32);
    return stringBuffer.toString();
  }
}
