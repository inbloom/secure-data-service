package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import java.util.Map;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;

public class TFileOutputPositionalMainJava
{
  protected static String nl;
  public static synchronized TFileOutputPositionalMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileOutputPositionalMainJava result = new TFileOutputPositionalMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "\t\t\t\tString tempStringM";
  protected final String TEXT_2 = "= null;" + NL + "\t\t\t\t" + NL + "\t\t\t\tint tempLengthM";
  protected final String TEXT_3 = "= 0;" + NL + "\t\t\t\t";
  protected final String TEXT_4 = NL + "\t\t\t\tStringBuilder sb_";
  protected final String TEXT_5 = " = new StringBuilder();";
  protected final String TEXT_6 = NL + "    \t\t\t\tpositionUtil_";
  protected final String TEXT_7 = ".setValue_";
  protected final String TEXT_8 = "(";
  protected final String TEXT_9 = ",sb_";
  protected final String TEXT_10 = ",tempStringM";
  protected final String TEXT_11 = ",tempLengthM";
  protected final String TEXT_12 = ");";
  protected final String TEXT_13 = NL + "\t\t\tsb_";
  protected final String TEXT_14 = ".append(";
  protected final String TEXT_15 = ");" + NL + "\t\t\t";
  protected final String TEXT_16 = NL + "\t\t\tsynchronized (multiThreadLockWrite) {" + NL + "\t\t\t";
  protected final String TEXT_17 = NL + "\t\t\tsynchronized (lockWrite) {" + NL + "\t\t\t";
  protected final String TEXT_18 = NL + "\t\t\tObject[] pLockWrite = (Object[])globalMap.get(\"PARALLEL_LOCK_WRITE\");" + NL + "\t\t\tsynchronized (pLockWrite) {" + NL + "\t\t\t";
  protected final String TEXT_19 = NL + "\t\t\tout";
  protected final String TEXT_20 = ".write(sb_";
  protected final String TEXT_21 = ".toString());" + NL + "    \t\t";
  protected final String TEXT_22 = NL + "        \t\tif(nb_line_";
  protected final String TEXT_23 = "%";
  protected final String TEXT_24 = " == 0) {" + NL + "        \t\tout";
  protected final String TEXT_25 = ".flush();" + NL + "        \t\t}" + NL + "    \t\t";
  protected final String TEXT_26 = NL + "    \t\t";
  protected final String TEXT_27 = NL + "    \t\t\t} " + NL + "\t\t\t";
  protected final String TEXT_28 = NL + "\t\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_29 = NL + "    \t\t\t}" + NL + "    \t\t";
  protected final String TEXT_30 = " \t\t\t\t\t\t" + NL + "\t\t\tnb_line_";
  protected final String TEXT_31 = "++;";
  protected final String TEXT_32 = NL;

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
        
        String separator = ElementParameterParser.getValue(
            node,
            "__ROWSEPARATOR__"
        );

        String encoding = ElementParameterParser.getValue(
            node,
            "__ENCODING__"
        );

        List<Map<String, String>> formats =
            (List<Map<String,String>>)ElementParameterParser.getObjectValue(
                node,
                "__FORMATS__"
            );            
            
        boolean useByte = ("true").equals(ElementParameterParser.getValue(node, "__USE_BYTE__"));
        
        boolean flushOnRow = ("true").equals(ElementParameterParser.getValue(node, "__FLUSHONROW__")); 
        String flushMod = ElementParameterParser.getValue(node, "__FLUSHONROW_NUM__");
     
		String parallelize = ElementParameterParser.getValue(node,"__PARALLELIZE__");
		boolean isParallelize = (parallelize!=null&&!("").equals(parallelize))?("true").equals(parallelize):false;

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    
	List< ? extends IConnection> conns = node.getIncomingConnections();
	for (IConnection conn : conns) {
		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    
			List<IMetadataColumn> columns = metadata.getListColumns();
			int sizeColumns = columns.size();
			for (int i = 0; i < sizeColumns; i++) {
				IMetadataColumn column = columns.get(i);
				Map<String,String> format=formats.get(i);
				JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
				String patternValue = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
				if(i%100==0){

    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(i/100);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_12);
    
				}
			}

    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(separator);
    stringBuffer.append(TEXT_15);
     
				if(codeGenArgument.getIsRunInMultiThread()){
			
    stringBuffer.append(TEXT_16);
    
				}
				if (codeGenArgument.subTreeContainsParallelIterate()) {
			
    stringBuffer.append(TEXT_17);
     
				}
				if (isParallelize) {
			
    stringBuffer.append(TEXT_18);
     
				}
			
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
     if(flushOnRow) { 
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(flushMod );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    
    			}
    		
    stringBuffer.append(TEXT_26);
    
    			if ( isParallelize) {
			
    stringBuffer.append(TEXT_27);
    
    		    }
				if (codeGenArgument.subTreeContainsParallelIterate()) {
			
    stringBuffer.append(TEXT_28);
     
				}
				if(codeGenArgument.getIsRunInMultiThread()){
    		
    stringBuffer.append(TEXT_29);
    
    			}
			
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    
		}
	}
    }
}

    stringBuffer.append(TEXT_32);
    return stringBuffer.toString();
  }
}
