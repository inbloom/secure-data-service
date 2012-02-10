package org.talend.designer.codegen.translators.file.output;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.Map;

public class TExternalSortOutputEndJava
{
  protected static String nl;
  public static synchronized TExternalSortOutputEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TExternalSortOutputEndJava result = new TExternalSortOutputEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t///////////////////////////////////////////" + NL + "\tout_";
  protected final String TEXT_2 = ".close();" + NL + "" + NL + "" + NL + "String[] sb_";
  protected final String TEXT_3 = " = new String[] {" + NL;
  protected final String TEXT_4 = " ";
  protected final String TEXT_5 = ",   ";
  protected final String TEXT_6 = " \"sort\", ";
  protected final String TEXT_7 = NL + NL + "\"--buffer-size=\"+";
  protected final String TEXT_8 = ",";
  protected final String TEXT_9 = "\"--temporary-directory=\"+";
  protected final String TEXT_10 = ",";
  protected final String TEXT_11 = NL + "\"--field-separator=\"+";
  protected final String TEXT_12 = ",";
  protected final String TEXT_13 = NL + "\"--key=";
  protected final String TEXT_14 = ",";
  protected final String TEXT_15 = "r";
  protected final String TEXT_16 = "n";
  protected final String TEXT_17 = "\",";
  protected final String TEXT_18 = NL + "\"--output=\"+";
  protected final String TEXT_19 = "," + NL + "\"\"+tempFile_";
  protected final String TEXT_20 = ".getAbsolutePath()+\"\"" + NL + "};" + NL + "" + NL + "Runtime runtime_";
  protected final String TEXT_21 = " = Runtime.getRuntime();" + NL + "final Process ps_";
  protected final String TEXT_22 = " = runtime_";
  protected final String TEXT_23 = ".exec(sb_";
  protected final String TEXT_24 = ");" + NL + "" + NL + "\t\tThread normal_";
  protected final String TEXT_25 = " = new Thread() {" + NL + "\t\t\tpublic void run() {" + NL + "\t\t\t\ttry {" + NL + "\t\t\t\t\tjava.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(ps_";
  protected final String TEXT_26 = ".getInputStream()));" + NL + "\t\t\t\t\tString line = \"\";" + NL + "\t\t\t\t\ttry {" + NL + "\t\t\t\t\t\twhile ((line = reader.readLine()) != null) {" + NL + "\t\t\t\t\t\t\tSystem.out.println(line);" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t} finally {" + NL + "\t\t\t\t\t\treader.close();" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t} catch (java.io.IOException ioe) {" + NL + "\t\t\t\t\tioe.printStackTrace();" + NL + "\t\t\t\t}" + NL + "\t\t\t}" + NL + "\t\t};" + NL + "\t\tnormal_";
  protected final String TEXT_27 = ".start();" + NL + "" + NL + "\t\tThread error_";
  protected final String TEXT_28 = " = new Thread() {" + NL + "\t\t\tpublic void run() {" + NL + "\t\t\t\ttry {" + NL + "\t\t\t\t\tjava.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(ps_";
  protected final String TEXT_29 = ".getErrorStream()));" + NL + "\t\t\t\t\tString line = \"\";" + NL + "\t\t\t\t\ttry {" + NL + "\t\t\t\t\t\twhile ((line = reader.readLine()) != null) {" + NL + "\t\t\t\t\t\t\tSystem.err.println(line);" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t} finally {" + NL + "\t\t\t\t\t\treader.close();" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t} catch (java.io.IOException ioe) {" + NL + "\t\t\t\t\tioe.printStackTrace();" + NL + "\t\t\t\t}" + NL + "\t\t\t}" + NL + "\t\t};" + NL + "\t\terror_";
  protected final String TEXT_30 = ".start();" + NL + "" + NL + "\t\tps_";
  protected final String TEXT_31 = ".waitFor();" + NL + "\t\tnormal_";
  protected final String TEXT_32 = ".interrupt();" + NL + "\t\terror_";
  protected final String TEXT_33 = ".interrupt();" + NL + "\t\t";
  protected final String TEXT_34 = "final java.io.BufferedWriter re_out_";
  protected final String TEXT_35 = " = new java.io.BufferedWriter(new java.io.OutputStreamWriter(" + NL + "        \t\tnew java.io.FileOutputStream(";
  protected final String TEXT_36 = ", true)));";
  protected final String TEXT_37 = "re_out_";
  protected final String TEXT_38 = ".write(\"false\");";
  protected final String TEXT_39 = "re_out_";
  protected final String TEXT_40 = ".write(String.valueOf(Byte.MIN_VALUE));";
  protected final String TEXT_41 = "re_out_";
  protected final String TEXT_42 = ".write(\"T\");";
  protected final String TEXT_43 = "re_out_";
  protected final String TEXT_44 = ".write(String.valueOf(Integer.MIN_VALUE));";
  protected final String TEXT_45 = "re_out_";
  protected final String TEXT_46 = ".write(String.valueOf(Long.MIN_VALUE));";
  protected final String TEXT_47 = "re_out_";
  protected final String TEXT_48 = ".write(String.valueOf(Short.MIN_VALUE));";
  protected final String TEXT_49 = "re_out_";
  protected final String TEXT_50 = ".write(String.valueOf(Float.MIN_VALUE));";
  protected final String TEXT_51 = "re_out_";
  protected final String TEXT_52 = ".write(String.valueOf(Double.MIN_VALUE));";
  protected final String TEXT_53 = "re_out_";
  protected final String TEXT_54 = ".write(OUT_DELIM_";
  protected final String TEXT_55 = ");";
  protected final String TEXT_56 = "re_out_";
  protected final String TEXT_57 = ".write(OUT_DELIM_ROWSEP_";
  protected final String TEXT_58 = ");" + NL + "re_out_";
  protected final String TEXT_59 = ".close();";
  protected final String TEXT_60 = NL + "\t\tglobalMap.put(\"";
  protected final String TEXT_61 = "_NB_LINE\",nb_line_";
  protected final String TEXT_62 = ");\t\t" + NL + "/////////////////////////////////" + NL;
  protected final String TEXT_63 = NL;

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
			List<Map<String, String>> criteria =
            (List<Map<String,String>>)ElementParameterParser.getObjectValue(
                node,
                "__CRITERIA__"
            );

        String filename = ElementParameterParser.getValue(
            node,
            "__FILENAME__"
        );

        String fieldSeparator = ElementParameterParser.getValue(
            node,
            "__FIELDSEPARATOR__"
        );

        String tempDir = ElementParameterParser.getValue(
            node,
            "__TEMP_DIR__"
        );

        String maxMemory = ElementParameterParser.getValue(
            node,
            "__MAX_MEMORY__"
        );
        
        String SortCMDPath = ElementParameterParser.getValue(
            node,
            "__SORT_CMDPATH__"
        );

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
     if (!("").equals(SortCMDPath)) { 
    stringBuffer.append(TEXT_4);
    stringBuffer.append(SortCMDPath );
    stringBuffer.append(TEXT_5);
     } else { 
    stringBuffer.append(TEXT_6);
     } 
    stringBuffer.append(TEXT_7);
    stringBuffer.append(maxMemory);
    stringBuffer.append(TEXT_8);
     if (!("").equals(tempDir)) { 
    stringBuffer.append(TEXT_9);
    stringBuffer.append(tempDir );
    stringBuffer.append(TEXT_10);
     } 
    stringBuffer.append(TEXT_11);
    stringBuffer.append(fieldSeparator );
    stringBuffer.append(TEXT_12);
    
        for(int i=0; i<criteria.size(); i++) {
            Map<String, String> line = criteria.get(i);
            
            String colname = line.get("COLNAME");
            int colnum = 0;

            int j = 1;
            for (IMetadataColumn column: metadata.getListColumns()) {
                if (colname.equals(column.getLabel())) {
                    colnum = j;
                }
                j++;
            }	

    stringBuffer.append(TEXT_13);
    stringBuffer.append(colnum );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(colnum );
     if (("desc").equals(line.get("ORDER").toLowerCase())) { 
    stringBuffer.append(TEXT_15);
     } 
     if (line.get("SORT").toLowerCase().startsWith("num")) { 
    stringBuffer.append(TEXT_16);
     } 
    stringBuffer.append(TEXT_17);
     } 
    stringBuffer.append(TEXT_18);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
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
    stringBuffer.append(cid );
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
    	if(("true").equals(ElementParameterParser.getValue(node, "__ADD_EOF__"))){

    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_36);
    	List< ? extends IConnection> conns = node.getIncomingConnections();
    	for (IConnection conn : conns) {
    		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
    			List<IMetadataColumn> columns = metadata.getListColumns();
    			int sizeColumns = columns.size();
    			for (int i = 0; i < sizeColumns; i++) {
    				IMetadataColumn column = columns.get(i);
					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
					if(javaType == JavaTypesManager.STRING || javaType == JavaTypesManager.DATE || javaType == JavaTypesManager.BYTE_ARRAY || javaType == JavaTypesManager.OBJECT) {
					} else if(javaType == JavaTypesManager.BOOLEAN){

    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    					}else if(javaType == JavaTypesManager.BYTE){

    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    					}else if(javaType == JavaTypesManager.CHARACTER){

    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    					}else if(javaType == JavaTypesManager.INTEGER){

    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    					}else if(javaType == JavaTypesManager.LONG){

    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    					}else if(javaType == JavaTypesManager.SHORT){

    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    					}else if(javaType == JavaTypesManager.FLOAT){

    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    					}else if(javaType == JavaTypesManager.DOUBLE){

    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    					}
					if(i != sizeColumns - 1) {

    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    
    				}
				}

    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_59);
    
    		}
    	}
    
	}
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    
		}
	}

    stringBuffer.append(TEXT_63);
    return stringBuffer.toString();
  }
}
