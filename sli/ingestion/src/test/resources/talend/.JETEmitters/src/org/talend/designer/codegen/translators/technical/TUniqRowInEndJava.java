package org.talend.designer.codegen.translators.technical;

import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.Map;
import java.util.List;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;

public class TUniqRowInEndJava
{
  protected static String nl;
  public static synchronized TUniqRowInEndJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TUniqRowInEndJava result = new TUniqRowInEndJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "            }" + NL + "            globalMap.put(\"";
  protected final String TEXT_2 = "_NB_UNIQUES\",nb_uniq_";
  protected final String TEXT_3 = ");" + NL + "            globalMap.put(\"";
  protected final String TEXT_4 = "_NB_DUPLICATES\",nb_";
  protected final String TEXT_5 = " - nb_uniq_";
  protected final String TEXT_6 = ");";
  protected final String TEXT_7 = NL + "            }" + NL + "            globalMap.put(\"";
  protected final String TEXT_8 = "_NB_UNIQUES\", nb_uniq_";
  protected final String TEXT_9 = ");" + NL + "            globalMap.put(\"";
  protected final String TEXT_10 = "_NB_DUPLICATES\", nb_duplicate_";
  protected final String TEXT_11 = ");";
  protected final String TEXT_12 = NL + "            }" + NL + "            globalMap.put(\"";
  protected final String TEXT_13 = "_NB_UNIQUES\", nb_";
  protected final String TEXT_14 = " - nb_duplicate_";
  protected final String TEXT_15 = ");" + NL + "            globalMap.put(\"";
  protected final String TEXT_16 = "_NB_DUPLICATES\", nb_duplicate_";
  protected final String TEXT_17 = ");";
  protected final String TEXT_18 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = ElementParameterParser.getValue(node, "__CID__");

int UNIQUE = 1;
int UNIQUE_AND_DUPLICATE = 2;
int UNIQUE_AND_DUPLICATE_ONCE = 3;
int DUPLICATE = 4;
int DUPLICATE_ONCE = 5;

int mode = 0;
String connUniqName = null;
String connDuplicateName = null;
boolean onlyOnceEachDuplicatedKey = ("true").equals(ElementParameterParser.getValue(node, "__ONLY_ONCE_EACH_DUPLICATED_KEY__"));
List<? extends IConnection> connsUnique = node.getOutgoingConnections("UNIQUE");
List<? extends IConnection> connsDuplicate = node.getOutgoingConnections("DUPLICATE");
if(connsUnique.size() > 0){
	connUniqName = connsUnique.get(0).getName();
	if(connsDuplicate.size() > 0){
		connDuplicateName = connsDuplicate.get(0).getName();
		if(onlyOnceEachDuplicatedKey){
			mode =3;
		}else{
			mode = 2;
		}
	}else{
		mode = 1;
	}
}else{
	if(connsDuplicate.size() > 0){
		connDuplicateName = connsDuplicate.get(0).getName();
		if(onlyOnceEachDuplicatedKey){
			mode =5;
		}else{
			mode = 4;
		}
	}
}

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
}else{
	mode = 0;
}

if(mode == UNIQUE){//HSS_____0

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
    
}else if(mode == UNIQUE_AND_DUPLICATE_ONCE || mode == UNIQUE_AND_DUPLICATE){//HSS_____0

    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    
}else if(mode == DUPLICATE_ONCE || mode == DUPLICATE){//HSS_____0

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
    
}//HSS_____0


    stringBuffer.append(TEXT_18);
    return stringBuffer.toString();
  }
}
