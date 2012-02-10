package org.talend.designer.codegen.translators.internet;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.List;
import org.talend.commons.utils.StringUtils;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnectionCategory;

public class TRSSInputBeginJava
{
  protected static String nl;
  public static synchronized TRSSInputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TRSSInputBeginJava result = new TRSSInputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "    int nb_line_";
  protected final String TEXT_2 = " = 0;" + NL + "    String rssurl";
  protected final String TEXT_3 = "=";
  protected final String TEXT_4 = ";" + NL + "    int aMount";
  protected final String TEXT_5 = "=";
  protected final String TEXT_6 = ";";
  protected final String TEXT_7 = NL + "    java.util.Properties props = System.getProperties();" + NL + "    " + NL + "    props.put(\"socksProxyHost\",";
  protected final String TEXT_8 = ");  " + NL + "    props.put(\"socksProxyPort\",";
  protected final String TEXT_9 = ");" + NL + "    props.put(\"java.net.socks.username\", ";
  protected final String TEXT_10 = ");" + NL + "    props.put(\"java.net.socks.password\", ";
  protected final String TEXT_11 = "); " + NL + "  " + NL + "    ";
  protected final String TEXT_12 = "   " + NL + "    java.net.URL url";
  protected final String TEXT_13 = " = new java.net.URL(rssurl";
  protected final String TEXT_14 = ");" + NL + "    it.sauronsoftware.feed4j.bean.Feed feed";
  protected final String TEXT_15 = "=null;" + NL + "    boolean ifInvalid";
  protected final String TEXT_16 = " = false;" + NL + "    " + NL + "  " + NL + "           " + NL + "    ";
  protected final String TEXT_17 = "    " + NL + "        try{";
  protected final String TEXT_18 = "  " + NL + "        feed";
  protected final String TEXT_19 = "= it.sauronsoftware.feed4j.FeedParser.parse(url";
  protected final String TEXT_20 = " );";
  protected final String TEXT_21 = NL + "        }catch(it.sauronsoftware.feed4j.FeedXMLParseException e";
  protected final String TEXT_22 = "){" + NL + "                System.err.print(e";
  protected final String TEXT_23 = ".getMessage());" + NL + "                ifInvalid";
  protected final String TEXT_24 = " = true;" + NL + "        }";
  protected final String TEXT_25 = NL + NL + "\tint items";
  protected final String TEXT_26 = " = feed";
  protected final String TEXT_27 = ".getItemCount();" + NL + "\t";
  protected final String TEXT_28 = NL + "      \t   \t\tif(aMount";
  protected final String TEXT_29 = "< items";
  protected final String TEXT_30 = ")" + NL + "      \t   \t\t{" + NL + "      \t   \t\t\titems";
  protected final String TEXT_31 = "=aMount";
  protected final String TEXT_32 = ";" + NL + "      \t   \t\t}" + NL + "      \t " + NL + "      \t ";
  protected final String TEXT_33 = NL + "      \t " + NL + "      \tif(!ifInvalid";
  protected final String TEXT_34 = "){" + NL + "          String row";
  protected final String TEXT_35 = "[]=new String [4];" + NL + "" + NL + "          for (int i_";
  protected final String TEXT_36 = " = 0; i_";
  protected final String TEXT_37 = "< items";
  protected final String TEXT_38 = "; i_";
  protected final String TEXT_39 = "++) {" + NL + "             it.sauronsoftware.feed4j.bean.FeedItem itm";
  protected final String TEXT_40 = " = feed";
  protected final String TEXT_41 = ".getItem(i_";
  protected final String TEXT_42 = ");" + NL + "              row";
  protected final String TEXT_43 = "[0]=itm";
  protected final String TEXT_44 = ".getTitle();" + NL + "              row";
  protected final String TEXT_45 = "[1]=itm";
  protected final String TEXT_46 = ".getDescriptionAsText();" + NL + "              row";
  protected final String TEXT_47 = "[2]=(itm";
  protected final String TEXT_48 = ".getPubDate()==null ? \"\" :itm";
  protected final String TEXT_49 = ".getPubDate().toGMTString());" + NL + "              row";
  protected final String TEXT_50 = "[3]=(itm";
  protected final String TEXT_51 = ".getLink()==null ? \"\" :itm";
  protected final String TEXT_52 = ".getLink().toString());";
  protected final String TEXT_53 = "  " + NL + "        if(!(\"\").equals(row";
  protected final String TEXT_54 = "[2])){     \t" + NL + "              \tjava.util.Date d";
  protected final String TEXT_55 = "=new java.util.Date();" + NL + "              \tjava.text.SimpleDateFormat formatterDate";
  protected final String TEXT_56 = "=new java.text.SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\");" + NL + "              \td";
  protected final String TEXT_57 = "=formatterDate";
  protected final String TEXT_58 = ".parse(";
  protected final String TEXT_59 = ");" + NL + "              \t java.util.Date pubDate";
  protected final String TEXT_60 = "=new java.util.Date(row";
  protected final String TEXT_61 = "[2]);" + NL + "             \t int value";
  protected final String TEXT_62 = "=pubDate";
  protected final String TEXT_63 = ".compareTo(d";
  protected final String TEXT_64 = ");" + NL + "             \t if(value";
  protected final String TEXT_65 = "<0)" + NL + "             \t {" + NL + "             \t \tcontinue;" + NL + "             \t }" + NL + "        }";
  protected final String TEXT_66 = "        " + NL + "    ";
  protected final String TEXT_67 = NL + "         \t\t\t" + NL + "         \t\t\t";
  protected final String TEXT_68 = ".";
  protected final String TEXT_69 = "=row";
  protected final String TEXT_70 = "[";
  protected final String TEXT_71 = "];";
  protected final String TEXT_72 = NL + NL + NL + NL;
  protected final String TEXT_73 = NL;

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
        String rss= ElementParameterParser.getValue(node,"__RSS__");
        boolean isDate= ("true").equals(ElementParameterParser.getValue(node,"__ARTICLE_DATE__"));
        boolean isAmount= ("true").equals(ElementParameterParser.getValue(node,"__ARTICLE_AMOUNT__"));
        String  dateTime=ElementParameterParser.getValue(node,"__DATE__");
        String  amount=ElementParameterParser.getValue(node,"__AMOUNT__");
        boolean dieOnError = ("true").equals(ElementParameterParser.getValue(node,"__DIE_ON_ERROR__"));
        boolean useProxy= ("true").equals(ElementParameterParser.getValue(node,"__USE_PROXY__"));
       	List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
       	
       	String host=ElementParameterParser.getValue(node,"__PROXY_HOST__");
       	String port=ElementParameterParser.getValue(node,"__PROXY_PORT__");
       	String username=ElementParameterParser.getValue(node,"__PROXY_USERNAME__");
       	String password=ElementParameterParser.getValue(node,"__PROXY_PASSWORD__");
  	

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(rss);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(amount);
    stringBuffer.append(TEXT_6);
    
if(useProxy){

    stringBuffer.append(TEXT_7);
    stringBuffer.append(host );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(port );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(username );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(password );
    stringBuffer.append(TEXT_11);
    
}

    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    if(!dieOnError){
    stringBuffer.append(TEXT_17);
    }
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    if(!dieOnError){
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    }
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
          	 if(isAmount){
      	 
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    
      	  }
      	 
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
            if(isDate)
              {
      
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_58);
    stringBuffer.append(dateTime);
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
           	
              }
      
    stringBuffer.append(TEXT_66);
    
    	if(conns!=null){
        	if (conns.size()>0){
           		for(IConnection conn:conns){
            		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.DATA)) {
        			List<IMetadataColumn> columns = metadata.getListColumns();
            			int sizeColumns = columns.size();
            			for (int j = 0; j < sizeColumns; j++) {
            			IMetadataColumn column = columns.get(j);
            			
         
    stringBuffer.append(TEXT_67);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_68);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(j);
    stringBuffer.append(TEXT_71);
    
            			}
        			}
           		}
    		
        	}
    	}
	}
}

    stringBuffer.append(TEXT_72);
    stringBuffer.append(TEXT_73);
    return stringBuffer.toString();
  }
}
