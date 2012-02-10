package org.talend.designer.codegen.translators.business.healthcare;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

/**
 * add by xzhang
 */
public class THL7OutputMainJava {

  protected static String nl;
  public static synchronized THL7OutputMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    THL7OutputMainJava result = new THL7OutputMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "   \t\t\t\t" + NL + "\tif(";
  protected final String TEXT_2 = ".";
  protected final String TEXT_3 = " != null) {";
  protected final String TEXT_4 = NL + "tmpValue_";
  protected final String TEXT_5 = " =";
  protected final String TEXT_6 = NL + "    \t\t\t\t\t\tFormatterUtils.format_Date(";
  protected final String TEXT_7 = ".";
  protected final String TEXT_8 = ", ";
  protected final String TEXT_9 = ");" + NL + "    \t\t\t\t\t\t";
  protected final String TEXT_10 = NL + "    \t\t\t\t\t\t";
  protected final String TEXT_11 = ".toPlainString();" + NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_12 = NL + "    \t\t\t\t\t\tjava.nio.charset.Charset.forName(";
  protected final String TEXT_13 = ").decode(java.nio.ByteBuffer.wrap(";
  protected final String TEXT_14 = ".";
  protected final String TEXT_15 = ")).toString();" + NL + "    \t\t\t\t\t\t";
  protected final String TEXT_16 = NL + "    \t\t\t\t\t\tString.valueOf(";
  protected final String TEXT_17 = ".";
  protected final String TEXT_18 = ");" + NL + "    \t\t\t\t\t\t";
  protected final String TEXT_19 = NL + NL + "terser_";
  protected final String TEXT_20 = ".set(\"";
  protected final String TEXT_21 = "(\"" + NL + " + i_";
  protected final String TEXT_22 = "_";
  protected final String TEXT_23 = " + \")";
  protected final String TEXT_24 = "\", tmpValue_";
  protected final String TEXT_25 = ");";
  protected final String TEXT_26 = "   \t\t\t\t" + NL + "\t}";
  protected final String TEXT_27 = NL + "if(msg_";
  protected final String TEXT_28 = "==null){" + NL + "\tmsg_";
  protected final String TEXT_29 = "= new ca.uhn.hl7v2.model.v";
  protected final String TEXT_30 = ".message.";
  protected final String TEXT_31 = "();" + NL + "\tterser_";
  protected final String TEXT_32 = " = new ca.uhn.hl7v2.util.Terser(msg_";
  protected final String TEXT_33 = ");" + NL + "}" + NL + "\t";
  protected final String TEXT_34 = NL + "\ti_";
  protected final String TEXT_35 = "_";
  protected final String TEXT_36 = "++;";
  protected final String TEXT_37 = NL;

    static class XMLNode {

        // table parameter of component
        public String name = null;

        public String path = null;

        public String type = null;

        public String column = null;
        
        public String defaultValue = null;
        
        public boolean hasDefaultValue = false;
        
        public String currConnName = null;
        
        public boolean isRepeatable = false;


        // column
        public IMetadataColumn relatedColumn = null;

        public List<IMetadataColumn> childrenColumnList = new ArrayList<IMetadataColumn>();

        // tree variable
        public XMLNode parent = null;

        public XMLNode attribute = null;

        public List<XMLNode> elements = new LinkedList<XMLNode>(); 

        public XMLNode(String path, String type, XMLNode parent, String column, String value) {
            this.path = path;
            this.parent = parent;
            this.type = type;
            this.column = column;
            if(value!=null && !"".equals(value)){
            	this.defaultValue = value;
            }
            if (type.equals("ELEMENT")) {
                this.name = path.substring(path.lastIndexOf("/") + 1);
            } else {
                this.name = path;
            }
        }
       
    }
    
    /**
    * return the table which is related to the connection name
    */
    public List<Map<String, String>> getTable(List<Map<String, String>> rootTable, String connName){
    	List<Map<String, String>> resultTable = new ArrayList<Map<String, String>>();
    	for(Map<String, String> map: rootTable){
    		if(map.get("COLUMN").contains(connName)){
    			resultTable.add(map);
    		}
    	}
    	return resultTable;
    }
    
    public List<String> getAllConnNameInTree(List<Map<String, String>> rootTable){
    	List<String> result = new ArrayList<String>();
    	for(Map<String, String> map: rootTable){
    		if(map.get("PATH").lastIndexOf("/")==0){
    			result.add(map.get("COLUMN"));
    		}
    	} 
    	return result;
    }

    
    // return [0] is root(XMLNode), [1] is groups(List<XMLNode>), [2] loop(XMLNode)
    public Object[] getTree(List<Map<String, String>> rootTable, List<IMetadataColumn> colList) {
        XMLNode root = null;
        XMLNode segment = null;
        XMLNode tmpParent = null;
        
            for (Map<String, String> tmpMap : rootTable) {
                XMLNode tmpNew = null;
                if (tmpMap.get("ATTRIBUTE").equals("attri")) {// Only segment could be able to have an attribute
                    tmpNew = new XMLNode(tmpMap.get("PATH"), "ATTRIBUTE", tmpParent, tmpMap.get("COLUMN"), tmpMap.get("VALUE"));
                    tmpParent.attribute = tmpNew;
                }else {
                    if (tmpParent == null) {
                        tmpNew = new XMLNode(tmpMap.get("PATH"), "ELEMENT", tmpParent, tmpMap.get("COLUMN"), tmpMap.get("VALUE"));
                        root = tmpNew;
                    } else {
                        String tmpParentPath = tmpMap.get("PATH").substring(0, tmpMap.get("PATH").lastIndexOf("/"));
                        while (tmpParent != null && !tmpParentPath.equals(tmpParent.path)) {
                            tmpParent = tmpParent.parent;
                        }
                        tmpNew = new XMLNode(tmpMap.get("PATH"), "ELEMENT", tmpParent, tmpMap.get("COLUMN"), tmpMap.get("VALUE"));
                        tmpParent.elements.add(tmpNew);
                        if (tmpMap.get("REPEATABLE").equals("true")) {
                        	tmpNew.isRepeatable = true;
                        }
                    }
                    tmpParent = tmpNew;
                }
	            if (tmpNew != root && tmpNew.name.indexOf("-") > 0
	                    && tmpNew.name.substring(0, tmpNew.name.indexOf("-")).equals(tmpNew.parent.name)) {
	                segment = tmpNew.parent;
	            }
                setIMetadataColumn(tmpNew, colList);
//                setDefaultValues(tmpNew);//add by wliu
            }
        buildSegmentPath(segment);        
        
        return new Object[] { root, segment };
    }
    
    private void buildSegmentPath(XMLNode segment){
        if (segment == null || segment.parent==null)
            return;
		XMLNode tmpNode = segment;
		// remove the root tage part
		String tmpPath =tmpNode.parent.path.substring(1);
		if(tmpPath.indexOf("/")>=0){
			tmpPath = tmpPath.substring(tmpPath.indexOf("/"));
		}else{
			tmpPath = "";
		}
		if(tmpNode.parent!=null && !tmpNode.parent.isRepeatable && tmpPath.indexOf("/")>=0){
			tmpPath = tmpPath.substring(0, tmpPath.lastIndexOf(tmpNode.name+"/") - 1);
		}
		setSubFieldPath(tmpNode, tmpPath);	
		tmpNode.currConnName = searchCurConnenctName(tmpNode);
    }
    
    private void setSubFieldPath(XMLNode node, String tmpPath){
    	if(node==null) return;
    	for(XMLNode tmpNode : node.elements){
    		tmpNode.path = tmpPath;
    		setSubFieldPath(tmpNode, tmpPath);
    	}
    }
    
    private String searchCurConnenctName(XMLNode segment){
    	for(XMLNode node : segment.elements){
    		if(node.column!=null && node.column.indexOf(":")>0){
    			return node.column.substring(0,node.column.indexOf(":"));
    		}else{
    			String tmpName = searchCurConnenctName(node);
    			if(tmpName!=null) return tmpName; 
    		}
    	}
    	
    	return null;
    }
    
    private void setDefaultValues(XMLNode node){
    	if(node.defaultValue != null && !"".equals(node.defaultValue)){
    		XMLNode tmp = node;
    		while(tmp !=null){
    			tmp.hasDefaultValue = true;
    			break;
    		}
    	}
    }

    private void setIMetadataColumn(XMLNode node, List<IMetadataColumn> colList) {
        String value = null;
        JavaType javaType = null;
        String[] str = node.column.split(":");
        if (node.column != null && node.column.length() > 0 && node.column.indexOf(":")>0) {
            for (IMetadataColumn column : colList) {
                if (column.getLabel().equals(node.column.substring(node.column.indexOf(":")+1))) {
                    node.relatedColumn = column;
                    XMLNode tmp = node;
                    while (tmp != null) {
                        if (!tmp.childrenColumnList.contains(column)) {
                            tmp.childrenColumnList.add(column);
                        }
                        break;
                    }
                }
            }
        }
    }

    public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
final String cid = node.getUniqueName();

String hl7Version = ElementParameterParser.getValue(node, "__HL7_VER__");
final String encoding = ElementParameterParser.getValue(node,"__ENCODING__");

class GenerateCode{
	private String incomingName = null;
	public void generate(XMLNode currSeg){
		for(XMLNode tmpNode : currSeg.elements){
			if(tmpNode.elements==null || tmpNode.elements.size()==0){
				IMetadataColumn column = tmpNode.relatedColumn;
				if(column==null) continue;
				JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
				boolean isPrimitive = JavaTypesManager.isJavaPrimitiveType( javaType, column.isNullable());
    			if(!isPrimitive) {

    stringBuffer.append(TEXT_1);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_2);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_3);
    
    			} 

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_5);
    
    			        String pattern = column.getPattern() == null || column.getPattern().trim().length() == 0 ? null : column.getPattern();
    			        if (javaType == JavaTypesManager.DATE && pattern != null && pattern.trim().length() != 0) {
    			            
    stringBuffer.append(TEXT_6);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_8);
    stringBuffer.append( pattern );
    stringBuffer.append(TEXT_9);
    	
						} else if(javaType == JavaTypesManager.BIGDECIMAL){
    						
    stringBuffer.append(TEXT_10);
    stringBuffer.append(column.getPrecision() == null? incomingName + "." + column.getLabel() : incomingName + "." + column.getLabel() + ".setScale(" + column.getPrecision() + ", java.math.RoundingMode.HALF_UP)" );
    stringBuffer.append(TEXT_11);
    
						} else if(javaType == JavaTypesManager.BYTE_ARRAY){
    						
    stringBuffer.append(TEXT_12);
    stringBuffer.append(encoding );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_15);
    
    			        } else {
    			            
    stringBuffer.append(TEXT_16);
    stringBuffer.append(incomingName );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_18);
    				
    			        }
    			        
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(tmpNode.path );
    if(currSeg.parent!=null && currSeg.parent.isRepeatable){
    stringBuffer.append(TEXT_21);
    stringBuffer.append(incomingName);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    }
    stringBuffer.append("/"+tmpNode.name );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    
				if(!isPrimitive) {

    stringBuffer.append(TEXT_26);
    
				}
			}else{
				generate(tmpNode);
			} // if
		} // for
		
	}
}

String incomingName = (String)codeGenArgument.getIncomingName();

List< ? extends IConnection> conns = node.getIncomingConnections();
boolean hasDataLink = false;
if(conns!=null){
	for(int i=0;i<conns.size();i++){
		IConnection connTemp = conns.get(i);
	    if (connTemp.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
	   		hasDataLink = true;
	   		break;
	    }
	}
}
    
if(hasDataLink){//HSS_____0
    
    List<Map<String, String>> rootTable = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__ROOT__");
    
	//get the right input connection and the previous input node and metadatas
	    
    List< ? extends IConnection> incomingConns = node.getIncomingConnections();

    if (incomingName == null && incomingConns.size() > 0) {
    	   incomingName = incomingConns.get(0).getName(); 
    }
    
	IMetadataTable preMetadataTable = null;
	for (IConnection incomingConn : incomingConns) {
		if ( incomingConn.getLineStyle().equals(EConnectionType.FLOW_MERGE) && incomingConn.getName().equals(incomingName)) {
			preMetadataTable = incomingConn.getMetadataTable();
		    break;
		}
	}
	
	if(preMetadataTable==null){
		return "";
	}

    List<Map<String, String>> currTable = getTable(rootTable, incomingName);
    
    Object[] treeObjs = getTree(currTable, preMetadataTable.getListColumns());
    
    if(treeObjs==null || treeObjs.length < 2){
    	return "";
    }
    
   
    XMLNode root = (XMLNode)treeObjs[0];
    // get all the segments in the xml structure
    XMLNode currSeg = (XMLNode)treeObjs[1];
    
    if(currSeg==null){
    	return "";
    	
    }
    
    List<String> existOrderConns = getAllConnNameInTree(rootTable);

    int i = 0;
    for(; i<existOrderConns.size(); i++){
    	if(existOrderConns.get(i).equals(incomingName)){
    		break;
    	}
    }
    if(i==0){

    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(hl7Version);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(root.name);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    
	}

    
	GenerateCode codeGen = new GenerateCode();
	codeGen.incomingName = incomingName;
	codeGen.generate(currSeg);


    stringBuffer.append(TEXT_34);
    stringBuffer.append(incomingName);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    
}//HSS_____0

    stringBuffer.append(TEXT_37);
    return stringBuffer.toString();
  }
}
