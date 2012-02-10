package org.talend.designer.codegen.translators.elt.map.oracle;

import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.ElementParameterParser;
import java.util.List;
import org.talend.core.model.process.AbstractExternalNode;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IExternalNode;
import org.talend.designer.dbmap.MapperMain;
import org.talend.designer.dbmap.external.data.ExternalDbMapData;
import org.talend.designer.dbmap.external.data.ExternalDbMapTable;
import org.talend.designer.dbmap.language.IDbLanguage;
import org.talend.designer.dbmap.DbMapComponent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class TELTOracleMapMainJava
{
  protected static String nl;
  public static synchronized TELTOracleMapMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TELTOracleMapMainJava result = new TELTOracleMapMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "\t\t\t\tglobalMap.put(" + NL + "\t\t\t\t\t\"";
  protected final String TEXT_2 = "\"+\"QUERY\" + \"";
  protected final String TEXT_3 = "\"," + NL + "\t\t\t\t\t";
  protected final String TEXT_4 = NL + "\t\t\t\t);" + NL + "\t\t\t\t";
  protected final String TEXT_5 = NL + "\t\t\t\t\tObject ";
  protected final String TEXT_6 = " = new Object();" + NL + "\t\t\t\t";
  protected final String TEXT_7 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    


	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	DbMapComponent node = (DbMapComponent) codeGenArgument.getArgument();
	boolean stats = codeGenArgument.isStatistics(); 
	
	///// hint options/////
	boolean useHintOptions = ("true").equals(ElementParameterParser.getValue(node,"__USE_HINT_OPTIONS__"));
	Map<String, String> hintsValues = null; 
	if (useHintOptions) {
		List<Map<String, String>> hintOptions = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__HINT_OPTIONS__");
		hintsValues = new HashMap<String, String>();
		String hintValue = null;
		boolean firstInsert = true;
		boolean firstUpdate = true;
		boolean firstDelete = true;
		boolean firstTableName = true;
		for(java.util.Map<String, String> option : hintOptions) {
			//get
			if(option.get("HINT").matches("\"/\\*NORMALIZED_HINT\\*/\"")) {
				//String id = cid.replace(node.getComponent().getName() + "_", "");
				//hintValue = "\"/*\"+" + table + "+\".\" + "  + "\"" + option.get("SQL_STMT")+ ".\"" + "+" +  id   +  "+\"*/\" " ;
			}else if (option.get("HINT").matches("\"/\\*+.*\\*/\"")) {
				hintValue = option.get("HINT");	
			}
			//set
			if ("SELECT".equalsIgnoreCase(option.get("SQL_STMT"))){
				if(firstInsert){
					hintsValues.put("SELECT", hintValue) ;
					firstInsert = false;
				}else {
					hintsValues.put("SELECT", hintsValues.get("SELECT") + "+" + hintValue) ;
				}									
			}
		}
	} 
	//// hint options end ////

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
        org.talend.designer.dbmap.language.oracle.OracleGenerationManager gm = new org.talend.designer.dbmap.language.oracle.OracleGenerationManager();
        String uniqueNameComponent = null;
        IDbLanguage currentLanguage = gm.getLanguage();

        List<IConnection> connections = (List<IConnection>) node.getIncomingConnections();
        List<IConnection> outputConnections = (List<IConnection>) node.getOutgoingConnections();
        ExternalDbMapData data = (ExternalDbMapData) node.getExternalData();
        uniqueNameComponent = node.getUniqueName();

        List<ExternalDbMapTable> outputTables = data.getOutputTables();

        Map<String, IConnection> nameToOutputConnection = new HashMap<String, IConnection>();
        for (IConnection connection : outputConnections) {
            nameToOutputConnection.put(connection.getUniqueName(), connection);
        }

		  Set tablesProcessed = new HashSet<String>();
        int lstOutputTablesSize = outputTables.size();
        for (int i = 0; i < lstOutputTablesSize; i++) {
            ExternalDbMapTable outputTable = outputTables.get(i);
            String outputTableName = outputTable.getName();

				IConnection connection = nameToOutputConnection.get(outputTableName);

            if (connection == null) {
            	continue;
            }

            String sqlQuery = gm.buildSqlSelect((DbMapComponent) node, outputTable.getName());
            
            if (useHintOptions && hintsValues.get("SELECT")!= null) {
	            StringBuffer tmpSqlQuery = new StringBuffer(sqlQuery);
	            tmpSqlQuery.insert("SELECT".length()+1,hintsValues.get("SELECT").substring(1,hintsValues.get("SELECT").length()-1)+" ");
	            sqlQuery = tmpSqlQuery.toString();
            }
            
				
    stringBuffer.append(TEXT_1);
    stringBuffer.append(uniqueNameComponent );
    stringBuffer.append(TEXT_2);
    stringBuffer.append( connection.getUniqueName() );
    stringBuffer.append(TEXT_3);
    stringBuffer.append( sqlQuery.replaceAll("[\r\n]", " ") );
    stringBuffer.append(TEXT_4);
    
				if(!tablesProcessed.contains(outputTable.getTableName())) {
				
    stringBuffer.append(TEXT_5);
    stringBuffer.append( outputTable.getTableName() );
    stringBuffer.append(TEXT_6);
    
				}
				tablesProcessed.add(outputTable.getTableName());
        }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    stringBuffer.append(TEXT_7);
    return stringBuffer.toString();
  }
}
