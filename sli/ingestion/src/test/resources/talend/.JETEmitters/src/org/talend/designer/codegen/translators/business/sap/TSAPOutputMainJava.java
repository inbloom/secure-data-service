package org.talend.designer.codegen.translators.business.sap;

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
import org.talend.core.model.metadata.types.JavaType;

public class TSAPOutputMainJava
{
  protected static String nl;
  public static synchronized TSAPOutputMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TSAPOutputMainJava result = new TSAPOutputMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t";
  protected final String TEXT_3 = NL;
  protected final String TEXT_4 = "        " + NL + "\ttry{" + NL + "\t";
  protected final String TEXT_5 = "              " + NL + "\t\t\t";
  protected final String TEXT_6 = NL + "\t\t\t\timportParameterList_";
  protected final String TEXT_7 = ".setValue(";
  protected final String TEXT_8 = ".";
  protected final String TEXT_9 = ", ";
  protected final String TEXT_10 = "); // \"input_single\"--";
  protected final String TEXT_11 = NL + "\t\t\t";
  protected final String TEXT_12 = NL + "\t\t\t\timportParameterList_";
  protected final String TEXT_13 = ".setValue(";
  protected final String TEXT_14 = ",";
  protected final String TEXT_15 = ".";
  protected final String TEXT_16 = "); // \"input_single\"--";
  protected final String TEXT_17 = NL + "\t\t\t";
  protected final String TEXT_18 = NL + "\t\t";
  protected final String TEXT_19 = NL + NL + "\t";
  protected final String TEXT_20 = "          " + NL + "\t\tinput_structure_";
  protected final String TEXT_21 = " = importParameterList_";
  protected final String TEXT_22 = ".getStructure(";
  protected final String TEXT_23 = ");" + NL + "\t\t";
  protected final String TEXT_24 = "              " + NL + "\t\t\t";
  protected final String TEXT_25 = NL + "\t\t\t\tinput_structure_";
  protected final String TEXT_26 = ".setValue(";
  protected final String TEXT_27 = ".";
  protected final String TEXT_28 = ", ";
  protected final String TEXT_29 = "); // \"input_structure\"--";
  protected final String TEXT_30 = NL + "\t\t\t";
  protected final String TEXT_31 = NL + "\t\t\t\tinput_structure_";
  protected final String TEXT_32 = ".setValue(";
  protected final String TEXT_33 = ", ";
  protected final String TEXT_34 = ".";
  protected final String TEXT_35 = "); // \"input_structure\"--";
  protected final String TEXT_36 = NL + "\t\t\t";
  protected final String TEXT_37 = NL + "\t\t";
  protected final String TEXT_38 = NL + NL + "\t";
  protected final String TEXT_39 = NL + "\t\t";
  protected final String TEXT_40 = "          " + NL + "\t\t\tinput_table_";
  protected final String TEXT_41 = " = importParameterList_";
  protected final String TEXT_42 = ".getTable(";
  protected final String TEXT_43 = ");" + NL + "\t        input_table_";
  protected final String TEXT_44 = ".appendRow();" + NL + "\t\t\t";
  protected final String TEXT_45 = "\t" + NL + "\t\t\t\t\tif(input_table_";
  protected final String TEXT_46 = ".getNumRows() < ";
  protected final String TEXT_47 = ".";
  protected final String TEXT_48 = ".size()){" + NL + "\t\t\t\t\t\tinput_table_";
  protected final String TEXT_49 = ".appendRows(";
  protected final String TEXT_50 = ".";
  protected final String TEXT_51 = ".size() - input_table_";
  protected final String TEXT_52 = ".getNumRows());" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\tinput_table_";
  protected final String TEXT_53 = ".firstRow();" + NL + "\t\t\t\t\tfor(int i = 0; i < ";
  protected final String TEXT_54 = ".";
  protected final String TEXT_55 = ".size(); i++){" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_56 = NL + "\t\t\t\t\t\t\tinput_table_";
  protected final String TEXT_57 = ".setValue(";
  protected final String TEXT_58 = ".";
  protected final String TEXT_59 = ".get(i).toString(), ";
  protected final String TEXT_60 = "); // \"input_table\"--";
  protected final String TEXT_61 = NL + "\t\t\t\t\t\t";
  protected final String TEXT_62 = NL + "\t\t\t\t\t\t\tinput_table_";
  protected final String TEXT_63 = ".setValue(";
  protected final String TEXT_64 = ", ";
  protected final String TEXT_65 = ".";
  protected final String TEXT_66 = ".get(i).toString()); // \"input_table\"--";
  protected final String TEXT_67 = NL + "\t\t\t\t\t\t";
  protected final String TEXT_68 = NL + "\t\t\t\t\t\tinput_table_";
  protected final String TEXT_69 = ".nextRow();" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_70 = NL + "\t\t\t\t\tinput_table_";
  protected final String TEXT_71 = ".firstRow();" + NL + "\t\t\t\t\t";
  protected final String TEXT_72 = NL + "\t\t\t\t\t\tinput_table_";
  protected final String TEXT_73 = ".setValue(";
  protected final String TEXT_74 = ".";
  protected final String TEXT_75 = ", ";
  protected final String TEXT_76 = "); // \"input_table\"--";
  protected final String TEXT_77 = NL + "\t\t\t\t\t";
  protected final String TEXT_78 = NL + "\t\t\t\t\t\tinput_table_";
  protected final String TEXT_79 = ".setValue(";
  protected final String TEXT_80 = ",";
  protected final String TEXT_81 = ".";
  protected final String TEXT_82 = "); // \"input_table\"--";
  protected final String TEXT_83 = NL + "\t\t\t\t\t";
  protected final String TEXT_84 = NL + "\t\t\t\t";
  protected final String TEXT_85 = "              " + NL + "\t\t\t" + NL + "\t\t\t";
  protected final String TEXT_86 = "        " + NL + "\t\t" + NL + "\t";
  protected final String TEXT_87 = NL + "\t\t";
  protected final String TEXT_88 = "          " + NL + "\t\t\ttable_input_";
  protected final String TEXT_89 = " = tableParameterList_";
  protected final String TEXT_90 = ".getTable(";
  protected final String TEXT_91 = ");" + NL + "\t        table_input_";
  protected final String TEXT_92 = ".appendRow();" + NL + "\t\t\t";
  protected final String TEXT_93 = NL + "\t\t\t\t\tif(table_input_";
  protected final String TEXT_94 = ".getNumRows() < ";
  protected final String TEXT_95 = ".";
  protected final String TEXT_96 = ".size()){" + NL + "\t\t\t\t\t\ttable_input_";
  protected final String TEXT_97 = ".appendRows(";
  protected final String TEXT_98 = ".";
  protected final String TEXT_99 = ".size() - table_input_";
  protected final String TEXT_100 = ".getNumRows());" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\ttable_input_";
  protected final String TEXT_101 = ".firstRow();" + NL + "\t\t\t\t\tfor(int i = 0; i < ";
  protected final String TEXT_102 = ".";
  protected final String TEXT_103 = ".size(); i++){" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_104 = NL + "\t\t\t\t\t\t\ttable_input_";
  protected final String TEXT_105 = ".setValue(";
  protected final String TEXT_106 = ".";
  protected final String TEXT_107 = ".get(i).toString(), ";
  protected final String TEXT_108 = "); // \"table_input\"--";
  protected final String TEXT_109 = NL + "\t\t\t\t\t\t";
  protected final String TEXT_110 = NL + "\t\t\t\t\t\t\ttable_input_";
  protected final String TEXT_111 = ".setValue(";
  protected final String TEXT_112 = ", ";
  protected final String TEXT_113 = ".";
  protected final String TEXT_114 = ".get(i).toString()); // \"table_input\"--";
  protected final String TEXT_115 = NL + "\t\t\t\t\t\t";
  protected final String TEXT_116 = NL + "\t\t\t\t\t\ttable_input_";
  protected final String TEXT_117 = ".nextRow();" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_118 = NL + "\t\t\t\t\ttable_input_";
  protected final String TEXT_119 = ".firstRow();" + NL + "\t\t\t\t\t";
  protected final String TEXT_120 = NL + "\t\t\t\t\t\ttable_input_";
  protected final String TEXT_121 = ".setValue(";
  protected final String TEXT_122 = ".";
  protected final String TEXT_123 = ", ";
  protected final String TEXT_124 = "); // \"table_input\"--";
  protected final String TEXT_125 = NL + "\t\t\t\t\t";
  protected final String TEXT_126 = NL + "\t\t\t\t\t\ttable_input_";
  protected final String TEXT_127 = ".setValue(";
  protected final String TEXT_128 = ", ";
  protected final String TEXT_129 = ".";
  protected final String TEXT_130 = "); // \"table_input\"--";
  protected final String TEXT_131 = NL + "\t\t\t\t\t";
  protected final String TEXT_132 = "\t\t\t                 \t" + NL + "\t\t\t\t";
  protected final String TEXT_133 = "        ";
  protected final String TEXT_134 = NL + "\t\tclient_";
  protected final String TEXT_135 = ".execute(function_";
  protected final String TEXT_136 = ");" + NL + "\t}catch (Exception e_";
  protected final String TEXT_137 = ") {" + NL + "\t\tcom.sap.mw.jco.JCO.releaseClient(client_";
  protected final String TEXT_138 = ");" + NL + "\t    throw new RuntimeException(e_";
  protected final String TEXT_139 = ".getMessage());" + NL + "\t}\t";
  protected final String TEXT_140 = NL + "\t\tfunction_";
  protected final String TEXT_141 = ".execute(dest_";
  protected final String TEXT_142 = ");" + NL + "\t}catch (Exception e_";
  protected final String TEXT_143 = ") {" + NL + "\t\tcom.sap.conn.jco.JCoContext.end(dest_";
  protected final String TEXT_144 = ");" + NL + "\t    throw new RuntimeException(e_";
  protected final String TEXT_145 = ".getMessage());" + NL + "\t}\t";
  protected final String TEXT_146 = NL + "        ";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();

    stringBuffer.append(TEXT_2);
    	
	IConnection firstDataConn = null;
	List<IMetadataColumn> firstColumnList = null;

	//1. get first DATA Link
	List< ? extends IConnection> conns = node.getIncomingConnections();	
	if(conns != null && conns.size() > 0){
		for(IConnection conn : conns){
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
				firstDataConn = conn;
				break;
			}
		}
	}

	//2. get first columnList (with real columns data) 	
	List<IMetadataTable> metadatas = node.getMetadataList();
	IMetadataTable metadata = null;
	if ( metadatas != null && metadatas.size() > 0 ) {
		metadata = metadatas.get(0);
		if(metadata != null){
			firstColumnList = metadata.getListColumns(); 
			if ( firstColumnList == null || firstColumnList.size() == 0 ) {
				firstColumnList = null;
			}
		}
	}
	
	//3. check the config Link and Schema
	if(firstDataConn == null || firstColumnList == null)
	{
		return "";
	}

    stringBuffer.append(TEXT_3);
    
            //this is a inner class to process the schema issue.
            class SchemaUtil {
				
				public boolean isList(String columnName,IMetadataTable metadata){
					List<IMetadataColumn> columns = metadata.getListColumns();
            		int sizeColumns = columns.size();
            		for (int i = 0; i < sizeColumns; i++) {
                		IMetadataColumn column = columns.get(i);
                		JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
                		if(columnName.equals(column.getLabel())&&javaType == JavaTypesManager.LIST){
                			return true;
                		}
                	}
                	return false;
				}
				
                // keep all schema configuration
                private List<SAPSchema> sapSchemaList = new ArrayList<SAPSchema>();

                class SAPSchema {

                    public String getSchemaName() {
                        return schemaName;
                    }

                    public String getSapParameterType() {
                        return sapParameterType;
                    }

                    public String getSapTableName() {
                        return sapTableName;
                    }

                    public String getSapParameterName() {
                        return sapParameterName;
                    }

                    public String getSapParameterValue() {
                        return sapParameterValue;
                    }

                    String schemaName;

                    String sapParameterType;

                    String sapTableName;

                    String sapParameterName;

                    String sapParameterValue;

                    public SAPSchema(String schemaName, String sapParameterType, String sapTableName, String sapParameterName,
                            String sapParameterValue) {

                        this.schemaName = schemaName;

                        this.sapParameterType = sapParameterType;

                        this.sapTableName = sapTableName;

                        this.sapParameterName = sapParameterName;

                        this.sapParameterValue = sapParameterValue;
                    }

                }

                // step_1
                public void addSAPSchema(String schemaName, String sapParameterType, String sapTableName,
                        String sapParameterName, String sapParameterValue) {
                    SAPSchema newSAPSchema = new SAPSchema(schemaName, sapParameterType, sapTableName, sapParameterName,
                            sapParameterValue);

                    // process List
                    sapSchemaList.add(newSAPSchema);
                }

                // step_2
                // <tableName, sameType_sameTable_schema>
                public Map<String, List<SAPSchema>> extractSchemaMap(String sapParameterType) {
                    Map<String, List<SAPSchema>> map = new HashMap<String, List<SAPSchema>>();
                    for (SAPSchema sapSchema : sapSchemaList) {

                        // filter the sapParameterType first
                        if (!sapSchema.getSapParameterType().equals(sapParameterType)) {
                            continue;
                        }

                        String key = sapSchema.getSapTableName();
                        List<SAPSchema> tableNamekeyList = map.get(key);
                        if (tableNamekeyList == null) {
                            List<SAPSchema> newTableNamekeyList = new ArrayList<SAPSchema>();
                            newTableNamekeyList.add(sapSchema);
                            map.put(key, newTableNamekeyList);

                        } else {
                            tableNamekeyList.add(sapSchema);
                        }
                    }
                    return map;
                }
            }
            
            //intial the schema in buffer
            SchemaUtil schemaUtil = new SchemaUtil(); 
            
			List<Map<String, String>> sapMapping = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__MAPPING__");            
            
			for(Map<String, String> configRow : sapMapping){			
				schemaUtil.addSAPSchema(configRow.get("SCHEMA_COLUMN"), configRow.get("SAP_PARAMETER_TYPE"), configRow.get("SAP_TABLE_NAME"), configRow.get("SAP_PARAMETER_NAME"), configRow.get("SAP_PARAMETER_VALUE"));
				if(true){
				System.out.println(configRow.get("SCHEMA_COLUMN") + " " + configRow.get("SAP_PARAMETER_TYPE") + " " + configRow.get("SAP_TABLE_NAME") + " " + configRow.get("SAP_PARAMETER_NAME") + " " + configRow.get("SAP_PARAMETER_VALUE"));
				}				
			}

    
boolean startIterate = false;
String firstDataConnName = firstDataConn.getName();

String client = ElementParameterParser.getValue(node, "__CLIENT__");
String userid = ElementParameterParser.getValue(node, "__USERID__");
String password = ElementParameterParser.getValue(node, "__PASSWORD__");
String language = ElementParameterParser.getValue(node, "__LANGUAGE__");
String hostname = ElementParameterParser.getValue(node, "__HOSTNAME__");
String systemnumber = ElementParameterParser.getValue(node, "__SYSTEMNUMBER__");

String functionName = ElementParameterParser.getValue(node, "__SAP_FUNCTION__");

String iterate_out_type = ElementParameterParser.getValue(node, "__SAP_ITERATE_OUT_TYPE__");
String iterate_out_tablename = ElementParameterParser.getValue(node, "__SAP_ITERATE_OUT_TABLENAME__");

boolean useExistingConn = ("true").equals(ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__"));
String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
String version = "sapjco.jar";
if(useExistingConn){
    List<? extends INode> nodes = node.getProcess().getGraphicalNodes();
    for(INode targetNode : nodes){
    	if (targetNode.getUniqueName().equals(connection)) {
	      version = ElementParameterParser.getValue(targetNode, "__DB_VERSION__");
	    }
    }
}else{
	version = ElementParameterParser.getValue(node, "__DB_VERSION__");
}

    stringBuffer.append(TEXT_4);
    
    /* the following is output part, it will output data to SAP. */
    // "input_single"
    Map<String, List<SchemaUtil.SAPSchema>> input_single_map = schemaUtil.extractSchemaMap("input_single");
    for (String key : input_single_map.keySet()) {
        List<SchemaUtil.SAPSchema> oneTableNamelist = input_single_map.get(key);
        for (SchemaUtil.SAPSchema schema : oneTableNamelist) {
		
    stringBuffer.append(TEXT_5);
    if("sapjco.jar".equals(version)){
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(firstDataConnName );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(schema.getSapParameterName() );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_11);
    }else if("sapjco3.jar".equals(version)){
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(schema.getSapParameterName() );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(firstDataConnName );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_17);
    }
    stringBuffer.append(TEXT_18);
    
      	}
    }
	
    stringBuffer.append(TEXT_19);
    
    // "input_structure"
    Map<String, List<SchemaUtil.SAPSchema>> input_structure_map = schemaUtil.extractSchemaMap("input_structure");
    for (String key : input_structure_map.keySet()) {
	
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(key );
    stringBuffer.append(TEXT_23);
    
		List<SchemaUtil.SAPSchema> oneTableNamelist = input_structure_map.get(key);
        for (SchemaUtil.SAPSchema schema : oneTableNamelist) {
		
    stringBuffer.append(TEXT_24);
    if("sapjco.jar".equals(version)){
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(firstDataConnName );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(schema.getSapParameterName() );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_30);
    }else if("sapjco3.jar".equals(version)){
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(schema.getSapParameterName() );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(firstDataConnName );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_36);
    }
    stringBuffer.append(TEXT_37);
              
		}
	}
	
    stringBuffer.append(TEXT_38);
    
    // "input_table"
    Map<String, List<SchemaUtil.SAPSchema>> input_table_map = schemaUtil.extractSchemaMap("input_table");
    if(input_table_map.size()>0){
	
    stringBuffer.append(TEXT_39);
    
        for (String key : input_table_map.keySet()) {
		
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(key );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    
			List<SchemaUtil.SAPSchema> oneTableNamelist = input_table_map.get(key);
	        for (SchemaUtil.SAPSchema schema : oneTableNamelist) {
	        	if(schemaUtil.isList(schema.getSchemaName(),metadata)){
				
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(firstDataConnName );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(firstDataConnName );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(firstDataConnName );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_55);
    if("sapjco.jar".equals(version)){
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(firstDataConnName );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(schema.getSapParameterName() );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_61);
    }else if("sapjco3.jar".equals(version)){
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(schema.getSapParameterName() );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(firstDataConnName );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_67);
    }
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_69);
    
	            }else{
				
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_71);
    if("sapjco.jar".equals(version)){
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(firstDataConnName );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(schema.getSapParameterName() );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_77);
    }else if("sapjco3.jar".equals(version)){
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(schema.getSapParameterName() );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(firstDataConnName );
    stringBuffer.append(TEXT_81);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_83);
    }
    stringBuffer.append(TEXT_84);
    
	            }
				
    stringBuffer.append(TEXT_85);
    
			}
		}
	}
	
    stringBuffer.append(TEXT_86);
           
    // "table_input"
    Map<String, List<SchemaUtil.SAPSchema>> table_input_map = schemaUtil.extractSchemaMap("table_input");
    if(table_input_map.size()>0){
	
    stringBuffer.append(TEXT_87);
    
        for (String key : table_input_map.keySet()) {
		
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_90);
    stringBuffer.append(key );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_92);
    
			List<SchemaUtil.SAPSchema> oneTableNamelist = table_input_map.get(key);
            for (SchemaUtil.SAPSchema schema : oneTableNamelist) {
            	if(schemaUtil.isList(schema.getSchemaName(),metadata)){
				
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_94);
    stringBuffer.append(firstDataConnName );
    stringBuffer.append(TEXT_95);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_97);
    stringBuffer.append(firstDataConnName );
    stringBuffer.append(TEXT_98);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_101);
    stringBuffer.append(firstDataConnName );
    stringBuffer.append(TEXT_102);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_103);
    if("sapjco.jar".equals(version)){
    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_105);
    stringBuffer.append(firstDataConnName );
    stringBuffer.append(TEXT_106);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_107);
    stringBuffer.append(schema.getSapParameterName() );
    stringBuffer.append(TEXT_108);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_109);
    }else if("sapjco3.jar".equals(version)){
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_111);
    stringBuffer.append(schema.getSapParameterName() );
    stringBuffer.append(TEXT_112);
    stringBuffer.append(firstDataConnName );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_114);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_115);
    }
    stringBuffer.append(TEXT_116);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_117);
    
            	}else{
				
    stringBuffer.append(TEXT_118);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_119);
    if("sapjco.jar".equals(version)){
    stringBuffer.append(TEXT_120);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_121);
    stringBuffer.append(firstDataConnName );
    stringBuffer.append(TEXT_122);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_123);
    stringBuffer.append(schema.getSapParameterName() );
    stringBuffer.append(TEXT_124);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_125);
    }else if("sapjco3.jar".equals(version)){
    stringBuffer.append(TEXT_126);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_127);
    stringBuffer.append(schema.getSapParameterName() );
    stringBuffer.append(TEXT_128);
    stringBuffer.append(firstDataConnName );
    stringBuffer.append(TEXT_129);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_130);
    stringBuffer.append(schema.getSchemaName() );
    stringBuffer.append(TEXT_131);
    }
    stringBuffer.append(TEXT_132);
    
            	}
			}
        }
	}
	
    stringBuffer.append(TEXT_133);
    if("sapjco.jar".equals(version)){
    stringBuffer.append(TEXT_134);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_135);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_136);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_137);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_138);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_139);
    }else if("sapjco3.jar".equals(version)){
    stringBuffer.append(TEXT_140);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_141);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_142);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_143);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_144);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_145);
    }
    stringBuffer.append(TEXT_146);
    return stringBuffer.toString();
  }
}
