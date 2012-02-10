package org.talend.designer.codegen.translators.business.bonita;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;

public class TBonitaInstantiateProcessMainJava
{
  protected static String nl;
  public static synchronized TBonitaInstantiateProcessMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TBonitaInstantiateProcessMainJava result = new TBonitaInstantiateProcessMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "\t" + NL + "\ttry {";
  protected final String TEXT_3 = NL + "\t\t\t\t\t\t\tparameters_";
  protected final String TEXT_4 = ".put(\"";
  protected final String TEXT_5 = "\", ";
  protected final String TEXT_6 = ".";
  protected final String TEXT_7 = ");";
  protected final String TEXT_8 = NL + NL + "\t\torg.ow2.bonita.facade.uuid.ProcessInstanceUUID instanceUUID_";
  protected final String TEXT_9 = " = runtimeAPI_";
  protected final String TEXT_10 = ".instantiateProcess(processID_";
  protected final String TEXT_11 = ", parameters_";
  protected final String TEXT_12 = ");" + NL + "\t\tprocessInstanceUUID_";
  protected final String TEXT_13 = " = instanceUUID_";
  protected final String TEXT_14 = ".getValue();" + NL + "\t\t " + NL + "\t\tSystem.out.println(\"**** Instance \"+ processInstanceUUID_";
  protected final String TEXT_15 = " + \" created ****\");" + NL + "\t} catch (Exception e_";
  protected final String TEXT_16 = ") {";
  protected final String TEXT_17 = NL + "\t\tthrow e_";
  protected final String TEXT_18 = ";";
  protected final String TEXT_19 = NL + "\t\tSystem.err.println(e_";
  protected final String TEXT_20 = ".getMessage());";
  protected final String TEXT_21 = NL + "\t} finally {" + NL + "\t\tloginContext_";
  protected final String TEXT_22 = ".logout();\t" + NL + "\t}" + NL + "" + NL + "" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_23 = "_ProcessInstanceUUID\", processInstanceUUID_";
  protected final String TEXT_24 = "); ";
  protected final String TEXT_25 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();

	String processID = ElementParameterParser.getValue(node, "__PROCESS_ID__");
	
	boolean dieOnError = ("true").equals(ElementParameterParser.getValue(node, "__DIE_ON_ERROR__"));

    stringBuffer.append(TEXT_2);
    
		List<IMetadataTable> metadatas = node.getMetadataList();
		if ((metadatas!=null)&&(metadatas.size()>0)) {
			IMetadataTable metadata = metadatas.get(0);
			if (metadata!=null) {
				List< ? extends IConnection> conns = node.getIncomingConnections();
				for (IConnection conn : conns) {
					if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
						List<IMetadataColumn> columns = metadata.getListColumns();
						int sizeColumns = columns.size();
						for (int i = 0; i < sizeColumns; i++) {

    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append((columns.get(i)).getLabel());
    stringBuffer.append(TEXT_5);
    stringBuffer.append(conn.getName());
    stringBuffer.append(TEXT_6);
    stringBuffer.append((columns.get(i)).getLabel());
    stringBuffer.append(TEXT_7);
    
						}
					}
				}
			}
		}

    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    
	if (dieOnError) {

    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    
	} else {

    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    	
	}

    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(TEXT_25);
    return stringBuffer.toString();
  }
}
