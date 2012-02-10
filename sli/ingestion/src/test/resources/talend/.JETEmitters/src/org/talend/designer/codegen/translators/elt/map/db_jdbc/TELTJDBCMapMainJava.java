package org.talend.designer.codegen.translators.elt.map.db_jdbc;

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

public class TELTJDBCMapMainJava
{
  protected static String nl;
  public static synchronized TELTJDBCMapMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TELTJDBCMapMainJava result = new TELTJDBCMapMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "\t\t\tglobalMap.put(" + NL + "\t\t\t\t\"";
  protected final String TEXT_2 = "\"+\"QUERY\" + \"";
  protected final String TEXT_3 = "\"," + NL + "\t\t\t\t";
  protected final String TEXT_4 = NL + "\t\t\t);" + NL + "\t\t\tglobalMap.put(" + NL + "\t\t\t\t\t\"";
  protected final String TEXT_5 = "\"+\"QUERY_COLUMNS_NAME\" + \"";
  protected final String TEXT_6 = "\"," + NL + "\t\t\t\t\t";
  protected final String TEXT_7 = NL + "\t\t\t);" + NL + "\t\t\t";
  protected final String TEXT_8 = NL + "\t\t\t\tObject ";
  protected final String TEXT_9 = " = new Object();" + NL + "\t\t\t";
  protected final String TEXT_10 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    


	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	DbMapComponent node=(DbMapComponent)codeGenArgument.getArgument();
	boolean stats = codeGenArgument.isStatistics(); 

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
    org.talend.designer.dbmap.language.generation.GenericDbGenerationManager gm = new org.talend.designer.dbmap.language.generation.GenericDbGenerationManager();
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
        
	
    stringBuffer.append(TEXT_1);
    stringBuffer.append(uniqueNameComponent );
    stringBuffer.append(TEXT_2);
    stringBuffer.append( connection.getUniqueName() );
    stringBuffer.append(TEXT_3);
    stringBuffer.append( sqlQuery.replaceAll("[\r\n]", " ") );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(uniqueNameComponent );
    stringBuffer.append(TEXT_5);
    stringBuffer.append( connection.getUniqueName() );
    stringBuffer.append(TEXT_6);
    stringBuffer.append( gm.getQueryColumnsName() );
    stringBuffer.append(TEXT_7);
    
			if(!tablesProcessed.contains(outputTable.getTableName())) {
			
    stringBuffer.append(TEXT_8);
    stringBuffer.append( outputTable.getTableName() );
    stringBuffer.append(TEXT_9);
    
			}
			tablesProcessed.add(outputTable.getTableName());
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    stringBuffer.append(TEXT_10);
    return stringBuffer.toString();
  }
}
