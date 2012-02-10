package org.talend.designer.codegen.translators.data_quality;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.metadata.types.JavaTypesManager;

public class TAddCRCRowMainJava
{
  protected static String nl;
  public static synchronized TAddCRCRowMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TAddCRCRowMainJava result = new TAddCRCRowMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "       Long crcComputedValue";
  protected final String TEXT_2 = " = null;";
  protected final String TEXT_3 = NL + "                StringBuilder strBuffer_";
  protected final String TEXT_4 = " = new StringBuilder();";
  protected final String TEXT_5 = NL + "                       strBuffer_";
  protected final String TEXT_6 = ".append(" + NL + "\t\t\t\t";
  protected final String TEXT_7 = NL + "\t\t\t\t\t\t\t\t(";
  protected final String TEXT_8 = ".";
  protected final String TEXT_9 = "==null)?null:(";
  protected final String TEXT_10 = ".";
  protected final String TEXT_11 = ".stripTrailingZeros())" + NL + "\t\t\t\t";
  protected final String TEXT_12 = NL + "\t\t\t\t                String.valueOf(";
  protected final String TEXT_13 = ".";
  protected final String TEXT_14 = ")\t\t\t" + NL + "\t\t\t\t";
  protected final String TEXT_15 = "\t\t\t\t" + NL + "\t\t\t\t);" + NL + "                \t\t";
  protected final String TEXT_16 = NL + "                    " + NL + "                    java.util.zip.CRC32 crc32";
  protected final String TEXT_17 = " = new java.util.zip.CRC32();  " + NL + "                    crc32";
  protected final String TEXT_18 = ".update(strBuffer_";
  protected final String TEXT_19 = ".toString().getBytes());" + NL + "                    crcComputedValue";
  protected final String TEXT_20 = " = new Long(crc32";
  protected final String TEXT_21 = ".getValue());";
  protected final String TEXT_22 = NL + "        " + NL + "                    crcComputedValue";
  protected final String TEXT_23 = " = new Long(com.dalsemi.onewire.utils.CRC8.compute(strBuffer_";
  protected final String TEXT_24 = ".toString().getBytes())); ";
  protected final String TEXT_25 = "      " + NL + "                    crcComputedValue";
  protected final String TEXT_26 = " = new Long(com.dalsemi.onewire.utils.CRC16.compute(strBuffer_";
  protected final String TEXT_27 = ".toString().getBytes()));        ";
  protected final String TEXT_28 = NL + "                                ";
  protected final String TEXT_29 = ".";
  protected final String TEXT_30 = "=";
  protected final String TEXT_31 = ".";
  protected final String TEXT_32 = ";";
  protected final String TEXT_33 = NL + "                            ";
  protected final String TEXT_34 = ".";
  protected final String TEXT_35 = "=crcComputedValue";
  protected final String TEXT_36 = ";";
  protected final String TEXT_37 = NL + "                nb_line_";
  protected final String TEXT_38 = "++;";
  protected final String TEXT_39 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String crcType = ElementParameterParser.getValue(node,"__CRC_TYPE__");
List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null)
     {
        String cid = node.getUniqueName();
        List<Map<String, String>> implication =(List<Map<String,String>>)ElementParameterParser.getObjectValue(node,"__IMPLICATION__");
        List keyCols = new ArrayList();
        for (int i=0; i<implication.size(); i++) {
            Map<String, String> col = implication.get(i);
            if (("true").equals(col.get("USE_IN_CRC"))) {
                keyCols.add(i);
            }
        }
       List< ? extends IConnection> conns = node.getIncomingConnections();
       
    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    
       if(conns!=null){
           if (conns.size()>0){
       
                IConnection conn =conns.get(0);
                List<IMetadataColumn> columns = metadata.getListColumns();
                
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    
                for (int i = 0; i < columns.size()-1; i++) {
                    IMetadataColumn column = columns.get(i);
                    JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
                    if(keyCols.contains(i))
                    {
                
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    
    				if (javaType == JavaTypesManager.BIGDECIMAL) {
				
    stringBuffer.append(TEXT_7);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_11);
    
					}  else {
				
    stringBuffer.append(TEXT_12);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_14);
    				
					}
				
    stringBuffer.append(TEXT_15);
      
                    }
         
                }
                if(("crc32").equals(crcType)){
                    
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
              
                }   
                if(("crc8").equals(crcType)){
          
                    
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
           
                }   
                if(("crc16").equals(crcType)){
          
                    
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
          
                }
                List< ? extends IConnection> connsout = node.getOutgoingConnections(); 
                if (connsout!=null) {
                    List<IMetadataColumn> columnsout = metadata.getListColumns();
                    for(int i=0;i<connsout.size();i++) {
                        IConnection connout = connsout.get(i);
                        if(connout.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA))
                        {
                            int columnSize=columnsout.size()-1;
                            for (int j = 0; j < columnSize; j++) {
                                IMetadataColumn columnout=columnsout.get(j);
                   
                                
    stringBuffer.append(TEXT_28);
    stringBuffer.append(connout.getName() );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(columnout.getLabel() );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(columnout.getLabel() );
    stringBuffer.append(TEXT_32);
                
                            }  
                            IMetadataColumn columnout=columnsout.get(columnSize); 
                            
    stringBuffer.append(TEXT_33);
    stringBuffer.append(connout.getName() );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(columnout.getLabel() );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
                      
                        }
                    }
                }
                
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    
           }   
       } 
                
    }
}

    stringBuffer.append(TEXT_39);
    return stringBuffer.toString();
  }
}
