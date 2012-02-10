package org.talend.designer.codegen.translators.databases.db_jdbc;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.types.JavaTypesManager;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class TJDBCSPMainJava
{
  protected static String nl;
  public static synchronized TJDBCSPMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TJDBCSPMainJava result = new TJDBCSPMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\t\t// No code generated: IN or INOUT arguments defined, whereas ";
  protected final String TEXT_3 = " has no input" + NL + "\t\t\t";
  protected final String TEXT_4 = NL + "\t\t\t\t\tif (";
  protected final String TEXT_5 = ".";
  protected final String TEXT_6 = " == null) {" + NL + "\t\t\t\t\t\tstatement_";
  protected final String TEXT_7 = ".setNull(";
  protected final String TEXT_8 = ", java.sql.Types.";
  protected final String TEXT_9 = ");" + NL + "\t\t\t\t\t} else {" + NL + "\t\t\t\t\t";
  protected final String TEXT_10 = NL + "\t\t\t\t\tif (";
  protected final String TEXT_11 = ".";
  protected final String TEXT_12 = " == '\\0') {" + NL + "\t\t\t\t\t\tstatement_";
  protected final String TEXT_13 = ".setString(";
  protected final String TEXT_14 = ", \"\");" + NL + "\t\t\t\t\t} else {" + NL + "\t\t\t\t\t\tstatement_";
  protected final String TEXT_15 = ".setString(";
  protected final String TEXT_16 = ", String.valueOf(";
  protected final String TEXT_17 = ".";
  protected final String TEXT_18 = "));" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_19 = NL + "\t\t\t\t\tstatement_";
  protected final String TEXT_20 = ".setDate(";
  protected final String TEXT_21 = ", new java.sql.Date(";
  protected final String TEXT_22 = ".";
  protected final String TEXT_23 = ".getTime()));" + NL + "\t\t\t\t\t";
  protected final String TEXT_24 = NL + "\t\t\t\t\tstatement_";
  protected final String TEXT_25 = ".set";
  protected final String TEXT_26 = "(";
  protected final String TEXT_27 = ", ";
  protected final String TEXT_28 = ".";
  protected final String TEXT_29 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_30 = NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_31 = NL + "\t\t\t\tstatement_";
  protected final String TEXT_32 = ".registerOutParameter(";
  protected final String TEXT_33 = ", java.sql.Types.";
  protected final String TEXT_34 = ");" + NL + "\t  \t\t\t";
  protected final String TEXT_35 = NL + "\t\tstatement_";
  protected final String TEXT_36 = ".execute();" + NL + "\t\t";
  protected final String TEXT_37 = NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_38 = ".";
  protected final String TEXT_39 = " = ";
  protected final String TEXT_40 = ".";
  protected final String TEXT_41 = ";" + NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_42 = NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_43 = ".";
  protected final String TEXT_44 = " = statement_";
  protected final String TEXT_45 = ".getResultSet();";
  protected final String TEXT_46 = NL + "\t\t\t\t\t\t\t \t\ttmpString_";
  protected final String TEXT_47 = " = statement_";
  protected final String TEXT_48 = ".getString(";
  protected final String TEXT_49 = ");" + NL + "\t\t\t\t\t\t  \t\t\tif (tmpString_";
  protected final String TEXT_50 = " != null && tmpString_";
  protected final String TEXT_51 = ".length() > 0) {" + NL + "\t\t\t\t\t\t  \t\t\t\t";
  protected final String TEXT_52 = ".";
  protected final String TEXT_53 = " = tmpString_";
  protected final String TEXT_54 = ".charAt(0);" + NL + "\t\t\t\t\t\t\t  \t\t}" + NL + "\t\t\t\t\t\t\t  \t\t";
  protected final String TEXT_55 = NL + "\t\t\t\t\t\t\t\t\ttmpDate_";
  protected final String TEXT_56 = " = statement_";
  protected final String TEXT_57 = ".getDate(";
  protected final String TEXT_58 = ");" + NL + "\t\t\t\t\t\t\t\t\tif (tmpDate_";
  protected final String TEXT_59 = " != null)" + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_60 = ".";
  protected final String TEXT_61 = " = new java.util.Date(tmpDate_";
  protected final String TEXT_62 = ".getTime());" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_63 = NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_64 = ".";
  protected final String TEXT_65 = " = statement_";
  protected final String TEXT_66 = ".get";
  protected final String TEXT_67 = "(";
  protected final String TEXT_68 = ");" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_69 = NL + "\t\t\t\t\t\t\t\t\tif (statement_";
  protected final String TEXT_70 = ".wasNull()) {" + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_71 = ".";
  protected final String TEXT_72 = " = null;" + NL + "\t\t\t\t\t\t\t\t\t}" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_73 = NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_74 = ".";
  protected final String TEXT_75 = " = ";
  protected final String TEXT_76 = ".";
  protected final String TEXT_77 = ";" + NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_78 = NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_79 = ".";
  protected final String TEXT_80 = " = ";
  protected final String TEXT_81 = ".";
  protected final String TEXT_82 = ";" + NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_83 = NL + "\t\t// No code generated: define a schema for ";
  protected final String TEXT_84 = NL + "\t\t";
  protected final String TEXT_85 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
/* Algorithm:
 * For each procedure arguments
 *   If argument type is IN or IN OUT
 *     Bind parameter to procedure call
 *   If argument type is OUT or IN OUT
 *     Register output parameter in procedure call
 * 
 * Execute procedure
 * 
 * For each column in first output schema
 *   If column match a procedure argument
 *     If argument type is IN
 *       Copy argument value from input flow
 *     If argument type is OUT or IN OUT
 *       Retrieve argument value from procedure call
 *     Copy argument value from first output schema to each output schema
 *   Else
 *     If column is defined in input schema
 *       Copy column value from input schema to each output schema
 */

CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode) codeGenArgument.getArgument();
String cid = node.getUniqueName();

List<Map<String, String>> spArgs =
	(List<Map<String, String>>) ElementParameterParser.getObjectValue(node, "__SP_ARGS__");

// Functions have an additionnal output parameter
boolean isFunction = ("true").equals(ElementParameterParser.getValue(node, "__IS_FUNCTION__"));
if (isFunction) {
	// Add return value at first position of parameters list
	String returnField = ElementParameterParser.getValue(node, "__RETURN__");
	
	HashMap<String, String> returnValue = new HashMap<String, String>();
	returnValue.put("COLUMN", returnField);
	returnValue.put("TYPE", "OUT");
	spArgs.add(0, returnValue);
}

class JavaToDbType {
	public String convert(String javaType) {
		if (("String").equals(javaType)) {
			return "VARCHAR";
		} else if (javaType.equalsIgnoreCase("char") || ("Character").equals(javaType)) {
			return "CHAR";
		} else if (("byte[]").equals(javaType)) {
			return "RAW";
		} else if (("java.util.Date").equals(javaType)) {
			return "DATE";
		} else if (javaType.equalsIgnoreCase("boolean")) {
			return "BOOLEAN";
		} else if (javaType.equalsIgnoreCase("byte")) {
			return "TINYINT";
		} else if (javaType.equalsIgnoreCase("short")) {
			return "SMALLINT";
		} else if (javaType.equalsIgnoreCase("int") || ("Integer").equals(javaType)) {
			return "INTEGER";
		} else if (javaType.equalsIgnoreCase("long")) {
			return "BIGINT";
		} else if (javaType.equalsIgnoreCase("float")) {
			return "FLOAT";
		} else if (javaType.equalsIgnoreCase("double")) {
			return "DOUBLE";
		} else {
			return "OTHER";
		}
	}
}
JavaToDbType converter = new JavaToDbType();

// Search incoming schema
IMetadataTable inMetadata = null;
IConnection inConnection = null;
String inConnectionName = null;

List<? extends IConnection> inConnections = node.getIncomingConnections();
if (inConnections != null) {
	for (int i = 0; i < inConnections.size(); i++) {
		IConnection connection = inConnections.get(i);
    	if (connection.getLineStyle().hasConnectionCategory(
    			IConnectionCategory.DATA)) {
		    inConnection = connection;
		    inConnectionName = inConnection.getName();
		    inMetadata = inConnection.getMetadataTable();
    	}
	}
}

// If there is an IN or an INOUT argument, the component must have an input
// connection
boolean canGenerate = true;
if (inConnection == null) {
	for (int i = 0; i < spArgs.size(); i++) {
		String argType = spArgs.get(i).get("TYPE");
		if (("IN").equals(argType) || ("INOUT").equals(argType)) {
			
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    
			canGenerate = false;
			break;
		}
	}
}

if (canGenerate) {
	// Search outgoing schema
	IMetadataTable metadata = null;
	
	List<IMetadataTable> metadatas = node.getMetadataList();
	if (metadatas != null && metadatas.size() > 0) {
	    metadata = metadatas.get(0);
	}
	
	// We only generate output if outgoing schema is defined
	if (metadata != null) {
		List<IMetadataColumn> columnList = metadata.getListColumns();
		
		// Iterate over procedure arguments
		for (int i = 0; i < spArgs.size(); i++) {
			Map<String, String> spArg = spArgs.get(i);
			String argName = spArg.get("COLUMN");
			String argType = spArg.get("TYPE");
			
			// Search Java type of argument, based on schema column
			String typeToGenerate = null;
			boolean nullable = false;
			for (IMetadataColumn column : columnList) {
				if (column.getLabel().equals(argName)) {
					typeToGenerate = JavaTypesManager.getTypeToGenerate(
						column.getTalendType(), column.isNullable());
					nullable = column.isNullable();
					break;
				}
			}
			
			if (typeToGenerate == null)
				continue;
			
			// Note: first argument in JDBC is arg #1, and for functions, arg #1 is return value
			int argIndex = i + 1;
			
			// Input argument
			if (("IN").equals(argType) || ("INOUT").equals(argType)) {
				if (nullable) {
					
    stringBuffer.append(TEXT_4);
    stringBuffer.append(inConnectionName);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(argName);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(argIndex);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(converter.convert(typeToGenerate));
    stringBuffer.append(TEXT_9);
    
				}
				
				String method;
				if (("byte[]").equals(typeToGenerate)) {
 			  		method = "Bytes";
 	  			} else if (("Integer").equals(typeToGenerate)) {
		 	   		method = "Int";
		 	   	} else {
					method = typeToGenerate.substring(0, 1).toUpperCase() + typeToGenerate.substring(1);
				}
				
				// Bind parameter to CallableStatement
				if (("char").equals(typeToGenerate) || ("Character").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_10);
    stringBuffer.append(inConnectionName);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(argName);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(argIndex);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(argIndex);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(inConnectionName);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(argName);
    stringBuffer.append(TEXT_18);
    
				} else if (("java.util.Date").equals(typeToGenerate)) {
					
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(argIndex);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(inConnectionName);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(argName);
    stringBuffer.append(TEXT_23);
    
				} else {
					
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(method);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(argIndex);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(inConnectionName);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(argName);
    stringBuffer.append(TEXT_29);
    
				}
				
				if (nullable) {
					
    stringBuffer.append(TEXT_30);
    
				}
			}
			
			// Output argument
			if (("OUT").equals(argType) || ("INOUT").equals(argType)) {
				
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(argIndex);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(converter.convert(typeToGenerate));
    stringBuffer.append(TEXT_34);
    
			}
		}
		
		
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    
		
		List<? extends IConnection> outConnections = node.getOutgoingConnections();
		IConnection firstOutConnection = null;
		
		if (outConnections != null) {
			// Search first outgoing connection, if exists
			int connectionIndex = -1;
			for (int i = 0; i < outConnections.size(); i++) {
				IConnection connection = outConnections.get(i);
    			if (connection.getLineStyle().hasConnectionCategory(
    					IConnectionCategory.DATA)) {
					firstOutConnection = connection;
					connectionIndex = i;
					break;
				}
			}
			
			if (firstOutConnection != null) {
				columns:for (IMetadataColumn column : columnList) {
					// Retrieve OUT arguments value, and copy IN arguments from input flow
					for (int i = 0; i < spArgs.size(); i++) {
						Map<String, String> spArg = spArgs.get(i);
						String argName = spArg.get("COLUMN");
						
						if (column.getLabel().equals(argName)) {
							String argType = spArg.get("TYPE");
							String typeToGenerate = JavaTypesManager.getTypeToGenerate(
								column.getTalendType(), column.isNullable());
							boolean nullable = column.isNullable();
							int argIndex = i + 1;
							
							if (("IN").equals(argType)) {
								// Copy parameter value from input flow
								
    stringBuffer.append(TEXT_37);
    stringBuffer.append(firstOutConnection.getName());
    stringBuffer.append(TEXT_38);
    stringBuffer.append(argName);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(inConnectionName);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(argName);
    stringBuffer.append(TEXT_41);
    
							} else if(("RECORDSET").equals(argType)){

    stringBuffer.append(TEXT_42);
    stringBuffer.append(firstOutConnection.getName());
    stringBuffer.append(TEXT_43);
    stringBuffer.append(argName);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    
							} else {
								// Retrieve parameter value (INOUT or OUT)
								String method;
								if (("byte[]").equals(typeToGenerate)) {
					  	   			method = "Bytes";
					  			} else if (("java.util.Date").equals(typeToGenerate)) {
					   	  			method = "Date";
					  			} else if (("Integer").equals(typeToGenerate)) {
					  				method = "Int";
					 			} else {
									method = typeToGenerate.substring(0, 1).toUpperCase() + typeToGenerate.substring(1);
					  			}
					  			
					  			if (method.equalsIgnoreCase("char") || ("Character").equals(method)) {
							 		
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(argIndex);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(firstOutConnection.getName());
    stringBuffer.append(TEXT_52);
    stringBuffer.append(argName);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    
						  		} else if (("Date").equals(method)) {
									
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(argIndex);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_59);
    stringBuffer.append(firstOutConnection.getName());
    stringBuffer.append(TEXT_60);
    stringBuffer.append(argName);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_62);
    
								} else {
									
    stringBuffer.append(TEXT_63);
    stringBuffer.append(firstOutConnection.getName());
    stringBuffer.append(TEXT_64);
    stringBuffer.append(argName);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(method);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(argIndex);
    stringBuffer.append(TEXT_68);
    
								}
								
								if (nullable) {
									
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(firstOutConnection.getName());
    stringBuffer.append(TEXT_71);
    stringBuffer.append(argName);
    stringBuffer.append(TEXT_72);
    
								}
							}
							
							for (int j = connectionIndex + 1; j < outConnections.size(); j++) {
								IConnection connection = outConnections.get(j);
		    					if (connection.getLineStyle().hasConnectionCategory(
				    					IConnectionCategory.DATA)) {
									
    stringBuffer.append(TEXT_73);
    stringBuffer.append(connection.getName());
    stringBuffer.append(TEXT_74);
    stringBuffer.append(argName);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(firstOutConnection.getName());
    stringBuffer.append(TEXT_76);
    stringBuffer.append(argName);
    stringBuffer.append(TEXT_77);
    
								}
							}
							
							continue columns;
						}
					}
					
					// Copy columns defined in input and output schema which are not arguments
					if (inMetadata != null && inMetadata.getListColumns() != null) {
						for (IMetadataColumn inColumn : inMetadata.getListColumns()) {
							if (column.getLabel().equals(inColumn.getLabel())) {
								for (int j = 0; j < outConnections.size(); j++) {
									IConnection connection = outConnections.get(j);
			    					if (connection.getLineStyle().hasConnectionCategory(
					    					IConnectionCategory.DATA)) {
										
    stringBuffer.append(TEXT_78);
    stringBuffer.append(connection.getName());
    stringBuffer.append(TEXT_79);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_80);
    stringBuffer.append(inConnectionName);
    stringBuffer.append(TEXT_81);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_82);
    
									}
								}
							}
						}
					}
				}
			}
		}
	} else {
		
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_84);
    
	}
}

    stringBuffer.append(TEXT_85);
    return stringBuffer.toString();
  }
}
