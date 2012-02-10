package org.talend.designer.codegen.translators.data_quality;

import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.IConnection;
import java.util.List;
import java.util.Map;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;

public class TUniqRowMainJava
{
  protected static String nl;
  public static synchronized TUniqRowMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TUniqRowMainJava result = new TUniqRowMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = " = null;\t\t\t";
  protected final String TEXT_3 = NL + "finder_";
  protected final String TEXT_4 = ".";
  protected final String TEXT_5 = " = ";
  protected final String TEXT_6 = ".";
  protected final String TEXT_7 = ";";
  protected final String TEXT_8 = NL + "if(";
  protected final String TEXT_9 = ".";
  protected final String TEXT_10 = " == null){" + NL + "\tfinder_";
  protected final String TEXT_11 = ".";
  protected final String TEXT_12 = " = null;" + NL + "}else{" + NL + "\tfinder_";
  protected final String TEXT_13 = ".";
  protected final String TEXT_14 = " = ";
  protected final String TEXT_15 = ".";
  protected final String TEXT_16 = ".toLowerCase();" + NL + "}";
  protected final String TEXT_17 = NL + "finder_";
  protected final String TEXT_18 = ".";
  protected final String TEXT_19 = " = ";
  protected final String TEXT_20 = ".";
  protected final String TEXT_21 = ";";
  protected final String TEXT_22 = "\t" + NL + "finder_";
  protected final String TEXT_23 = ".hashCodeDirty = true;" + NL + "if (!keys";
  protected final String TEXT_24 = ".contains(finder_";
  protected final String TEXT_25 = ")) {" + NL + "\t\tKeyStruct_";
  protected final String TEXT_26 = " new_";
  protected final String TEXT_27 = " = new KeyStruct_";
  protected final String TEXT_28 = "();" + NL + "" + NL + "\t\t";
  protected final String TEXT_29 = NL + "new_";
  protected final String TEXT_30 = ".";
  protected final String TEXT_31 = " = ";
  protected final String TEXT_32 = ".";
  protected final String TEXT_33 = ";";
  protected final String TEXT_34 = NL + "if(";
  protected final String TEXT_35 = ".";
  protected final String TEXT_36 = " == null){" + NL + "\tnew_";
  protected final String TEXT_37 = ".";
  protected final String TEXT_38 = " = null;" + NL + "}else{" + NL + "\tnew_";
  protected final String TEXT_39 = ".";
  protected final String TEXT_40 = " = ";
  protected final String TEXT_41 = ".";
  protected final String TEXT_42 = ".toLowerCase();" + NL + "}";
  protected final String TEXT_43 = NL + "new_";
  protected final String TEXT_44 = ".";
  protected final String TEXT_45 = " = ";
  protected final String TEXT_46 = ".";
  protected final String TEXT_47 = ";";
  protected final String TEXT_48 = NL + "\t\t" + NL + "\t\tkeys";
  protected final String TEXT_49 = ".add(new_";
  protected final String TEXT_50 = ");";
  protected final String TEXT_51 = "if(";
  protected final String TEXT_52 = " == null){ " + NL + "\t";
  protected final String TEXT_53 = " = new ";
  protected final String TEXT_54 = "Struct();" + NL + "}";
  protected final String TEXT_55 = ".";
  protected final String TEXT_56 = " = ";
  protected final String TEXT_57 = ".";
  protected final String TEXT_58 = ";\t\t\t";
  protected final String TEXT_59 = "\t\t" + NL + "\t\tnb_uniques_";
  protected final String TEXT_60 = "++;" + NL + "\t} else {";
  protected final String TEXT_61 = NL + "if (!keysForDuplicated";
  protected final String TEXT_62 = ".contains(finder_";
  protected final String TEXT_63 = ")) {" + NL + "\t\tKeyStruct_";
  protected final String TEXT_64 = " new_";
  protected final String TEXT_65 = " = new KeyStruct_";
  protected final String TEXT_66 = "();" + NL + "" + NL + "\t\t";
  protected final String TEXT_67 = NL + "new_";
  protected final String TEXT_68 = ".";
  protected final String TEXT_69 = " = ";
  protected final String TEXT_70 = ".";
  protected final String TEXT_71 = ";";
  protected final String TEXT_72 = NL + "if(";
  protected final String TEXT_73 = ".";
  protected final String TEXT_74 = " == null){" + NL + "\tnew_";
  protected final String TEXT_75 = ".";
  protected final String TEXT_76 = " = null;" + NL + "}else{" + NL + "\tnew_";
  protected final String TEXT_77 = ".";
  protected final String TEXT_78 = " = ";
  protected final String TEXT_79 = ".";
  protected final String TEXT_80 = ".toLowerCase();" + NL + "}";
  protected final String TEXT_81 = NL + "new_";
  protected final String TEXT_82 = ".";
  protected final String TEXT_83 = " = ";
  protected final String TEXT_84 = ".";
  protected final String TEXT_85 = ";";
  protected final String TEXT_86 = NL + "\tkeysForDuplicated";
  protected final String TEXT_87 = ".add(new_";
  protected final String TEXT_88 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_89 = NL + "if(";
  protected final String TEXT_90 = " == null){ " + NL + "\t";
  protected final String TEXT_91 = " = new ";
  protected final String TEXT_92 = "Struct();" + NL + "}\t\t\t\t";
  protected final String TEXT_93 = ".";
  protected final String TEXT_94 = " = ";
  protected final String TEXT_95 = ".";
  protected final String TEXT_96 = ";\t\t\t";
  protected final String TEXT_97 = NL + "}" + NL + "\t\t\t\t\t";
  protected final String TEXT_98 = NL + "\t  nb_duplicates_";
  protected final String TEXT_99 = "++;" + NL + "\t}";
  protected final String TEXT_100 = ".";
  protected final String TEXT_101 = " = ";
  protected final String TEXT_102 = ".";
  protected final String TEXT_103 = ";\t\t\t";
  protected final String TEXT_104 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String connName = null;
if (node.getIncomingConnections().size()==1) {
	IConnection conn = node.getIncomingConnections().get(0);
	connName = conn.getName();
}

List<IMetadataTable> metadatas = node.getMetadataList();
if ((metadatas!=null)&&(metadatas.size()>0)) {
    IMetadataTable metadata = metadatas.get(0);
    if (metadata!=null && connName != null) {
		List<? extends IConnection> conns = node.getOutgoingSortedConnections();
		List<? extends IConnection> connsUnique = node.getOutgoingConnections("UNIQUE");
		List<? extends IConnection> connsDuplicate = node.getOutgoingConnections("DUPLICATE");
		String onlyOnceEachDuplicatedKey = ElementParameterParser.getValue(node, "__ONLY_ONCE_EACH_DUPLICATED_KEY__");
		List<Map<String, String>> keyColumns = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__UNIQUE_KEY__");
		boolean hasKey = false;
		for(Map<String, String> keyColumn:keyColumns){
			if(("true").equals(keyColumn.get("KEY_ATTRIBUTE"))){
				hasKey = true;
				break;
			}
		}
		int ii = 0;
		if(hasKey){

    
	if (conns!=null) {
		if (conns.size()>0) {
			for (int i=0;i<conns.size();i++) {
				IConnection conn = conns.get(i);
				if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

			    	 
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_2);
    					 
				}
			}
		}
	}
	
	for (int i=0; i<metadata.getListColumns().size();i++) {
		IMetadataColumn column = metadata.getListColumns().get(i);
		Map<String,String> keyColumn = keyColumns.get(i);
		if(("true").equals(keyColumn.get("KEY_ATTRIBUTE"))){
			boolean caseSensitive = ("true").equals(keyColumn.get("CASE_SENSITIVE"));
			JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
			if(javaType == JavaTypesManager.STRING) {
				if(caseSensitive){
		
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_7);
    
				}else{

    stringBuffer.append(TEXT_8);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_16);
    
				}
			}else{

    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_21);
    
			}
		}
	}

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
    
	for (int ij=0; ij<metadata.getListColumns().size();ij++) {
		IMetadataColumn column = metadata.getListColumns().get(ij);
		Map<String,String> keyColumn = keyColumns.get(ij);
		if(("true").equals(keyColumn.get("KEY_ATTRIBUTE"))){
			boolean caseSensitive = ("true").equals(keyColumn.get("CASE_SENSITIVE"));
			JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
			if(javaType == JavaTypesManager.STRING) {
				if(caseSensitive){
		
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_33);
    
				}else{

    stringBuffer.append(TEXT_34);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_42);
    
				}
			}else{

    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_47);
    
			}
		}
	}
		
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    
	if (connsUnique!=null) {
		if (connsUnique.size()>0) {
			for (int i=0;i<connsUnique.size();i++) {
				IConnection conn = connsUnique.get(i);
				if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {

    stringBuffer.append(TEXT_51);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_54);
    
			    	 for (IMetadataColumn column: metadata.getListColumns()) {
			    	 
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_57);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_58);
    					 }
				}
			}
		}
	}

    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    
	if (connsDuplicate!=null) {
		if (connsDuplicate.size()>0) {
			for (int i=0;i<connsDuplicate.size();i++) {
				IConnection conn = connsDuplicate.get(i);
				if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
					if(("true").equals(onlyOnceEachDuplicatedKey)){
					
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_66);
    
	for (int ik=0; ik<metadata.getListColumns().size();ik++) {
		IMetadataColumn column = metadata.getListColumns().get(ik);
		Map<String,String> keyColumn = keyColumns.get(ik);
		if(("true").equals(keyColumn.get("KEY_ATTRIBUTE"))){
			boolean caseSensitive = ("true").equals(keyColumn.get("CASE_SENSITIVE"));
			JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
			if(javaType == JavaTypesManager.STRING) {
				if(caseSensitive){
		
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_70);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_71);
    
				}else{

    stringBuffer.append(TEXT_72);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_80);
    
				}
			}else{

    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_82);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_83);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_84);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_85);
    
			}
		}
	}
		
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_88);
    
					}

    stringBuffer.append(TEXT_89);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_90);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_91);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_92);
    
			    	 for (IMetadataColumn column: metadata.getListColumns()) {
			    	 
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_93);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_94);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_95);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_96);
    					 }
					if(("true").equals(onlyOnceEachDuplicatedKey)){
					
    stringBuffer.append(TEXT_97);
    
					}
				}
			}
		}
	}

    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_99);
    
	}else{
		if (conns!=null) {
			if (conns.size()>0) {
				for (int i=0;i<conns.size();i++) {
					IConnection conn = conns.get(i);
			    	if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
			    		for (IMetadataColumn column: metadata.getListColumns()) {
			    	 
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_100);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_101);
    stringBuffer.append(connName );
    stringBuffer.append(TEXT_102);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_103);
    						}
					}
				}
			}
		}
	}
	}
}

    stringBuffer.append(TEXT_104);
    return stringBuffer.toString();
  }
}
