package org.talend.designer.codegen.translators.file.input;

import java.util.List;
import java.util.Map;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TFileInputEBCDICBeginJava
{
  protected static String nl;
  public static synchronized TFileInputEBCDICBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileInputEBCDICBeginJava result = new TFileInputEBCDICBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "\t// open file" + NL + "\tjavax.xml.bind.JAXBContext jaxbContext_";
  protected final String TEXT_2 = " = javax.xml.bind.JAXBContext.newInstance(\"net.sf.cobol2j\");" + NL + "\tjavax.xml.bind.Unmarshaller unmarshaller_";
  protected final String TEXT_3 = " = jaxbContext_";
  protected final String TEXT_4 = ".createUnmarshaller();" + NL + "\tObject o_";
  protected final String TEXT_5 = " = unmarshaller_";
  protected final String TEXT_6 = ".unmarshal(new java.io.FileInputStream(";
  protected final String TEXT_7 = "));" + NL + "\tnet.sf.cobol2j.FileFormat fF_";
  protected final String TEXT_8 = " = (net.sf.cobol2j.FileFormat) o_";
  protected final String TEXT_9 = ";" + NL + "\tnet.sf.cobol2j.RecordSet rset_";
  protected final String TEXT_10 = " = new net.sf.cobol2j.RecordSet(new java.io.FileInputStream(";
  protected final String TEXT_11 = "), fF_";
  protected final String TEXT_12 = ");" + NL + "\tjava.util.Map recdefs_";
  protected final String TEXT_13 = " = new net.sf.cobol2j.RecordsMap(fF_";
  protected final String TEXT_14 = ");" + NL + "\t" + NL + "\t// read every record, for each record split into column definition" + NL + "\tList inrecord_";
  protected final String TEXT_15 = ";" + NL + "\twhile (rset_";
  protected final String TEXT_16 = ".hasNext()){" + NL + "\t\tinrecord_";
  protected final String TEXT_17 = " = rset_";
  protected final String TEXT_18 = ".next();" + NL + "\t\t{" + NL + "\t\t\t";
  protected final String TEXT_19 = NL + "\t\t\t\t\t";
  protected final String TEXT_20 = " = null;\t\t\t";
  protected final String TEXT_21 = NL + "\tif(inrecord_";
  protected final String TEXT_22 = ".get(0).equals(\"";
  protected final String TEXT_23 = "\") || recdefs_";
  protected final String TEXT_24 = ".size() == 1 ){" + NL + "\t" + NL + "\t\t";
  protected final String TEXT_25 = " = new ";
  protected final String TEXT_26 = "Struct();";
  protected final String TEXT_27 = "\t\t\t\t" + NL + "\t\t";
  protected final String TEXT_28 = ".";
  protected final String TEXT_29 = " = (";
  protected final String TEXT_30 = ")inrecord_";
  protected final String TEXT_31 = ".get(";
  protected final String TEXT_32 = ");";
  protected final String TEXT_33 = "\t" + NL + "\t}\t";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
    String cid = node.getUniqueName();
    
	String filename = ElementParameterParser.getValue(node,"__FILENAME__");
	String copybook = ElementParameterParser.getValue(node,"__COPYBOOK__");

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append( copybook );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append( filename );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    			List< ? extends IConnection> conns = node.getOutgoingSortedConnections();

    		if (conns!=null && conns.size()>0) {
				for (int i=0;i<conns.size();i++) {
					IConnection connTemp = conns.get(i);
					if (connTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_19);
    stringBuffer.append(connTemp.getName() );
    stringBuffer.append(TEXT_20);
    
					}
				}
    		}
    		
List<Map<String, String>> schemas = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__SCHEMAS__");

for(Map<String, String> schemaMap : schemas) {//------AAA
	String schemaName = schemaMap.get("SCHEMA");
	String code = schemaMap.get("CODE");
	
	IConnection justConn = null; //------->get the right output connection--->to get the columns info
	if(conns != null && conns.size() > 0){
		for(IConnection conn : conns){
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
				if(schemaName.equals(conn.getMetadataTable().getLabel())){
					justConn = conn;
					break;
				}
			}
		}
	}

if(justConn != null){//------BBB
	
	IMetadataTable justMetadata = justConn.getMetadataTable();
	List<IMetadataColumn> justColumnList = justMetadata.getListColumns();

    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(code );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(justConn.getName() );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(justConn.getName() );
    stringBuffer.append(TEXT_26);
    		
		int sizeListColumns = justColumnList.size();
		for (int valueN=0; valueN<sizeListColumns; valueN++) {
			IMetadataColumn column = justColumnList.get(valueN);
			JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());

    stringBuffer.append(TEXT_27);
    stringBuffer.append(justConn.getName() );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_29);
    stringBuffer.append(javaType.getLabel() );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(valueN);
    stringBuffer.append(TEXT_32);
    
		}

    stringBuffer.append(TEXT_33);
    
	}//------BBB
}//------AAA

    return stringBuffer.toString();
  }
}
