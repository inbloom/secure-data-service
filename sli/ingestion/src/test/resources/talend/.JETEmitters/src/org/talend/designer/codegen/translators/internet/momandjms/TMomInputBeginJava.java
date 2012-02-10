package org.talend.designer.codegen.translators.internet.momandjms;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.process.IConnection;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import java.util.List;
import java.util.Map;

public class TMomInputBeginJava
{
  protected static String nl;
  public static synchronized TMomInputBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMomInputBeginJava result = new TMomInputBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\t\tjava.util.Hashtable props_";
  protected final String TEXT_3 = "=new java.util.Hashtable();" + NL + "\t\t\tprops_";
  protected final String TEXT_4 = ".put(javax.naming.Context.INITIAL_CONTEXT_FACTORY,\"org.jnp.interfaces.NamingContextFactory\");" + NL + "\t\t\tprops_";
  protected final String TEXT_5 = ".put(javax.naming.Context.PROVIDER_URL, ";
  protected final String TEXT_6 = "+\":\"+";
  protected final String TEXT_7 = ");" + NL + "\t\t\tprops_";
  protected final String TEXT_8 = ".put(\"java.naming.rmi.security.manager\", \"yes\");" + NL + "\t\t\tprops_";
  protected final String TEXT_9 = ".put(javax.naming.Context.URL_PKG_PREFIXES, \"org.jboss.naming\");" + NL + "\t\t\tprops_";
  protected final String TEXT_10 = ".put(\"java.naming.factory.url.pkgs\",\"org.jboss.naming:org.jnp.interfaces\");\t" + NL + "\t\t\t" + NL + "\t\t\tjavax.naming.Context context_";
  protected final String TEXT_11 = "=new javax.naming.InitialContext(props_";
  protected final String TEXT_12 = ");" + NL + "\t\t\tjavax.jms.ConnectionFactory factory_";
  protected final String TEXT_13 = "=(javax.jms.ConnectionFactory)context_";
  protected final String TEXT_14 = ".lookup(\"ConnectionFactory\");\t";
  protected final String TEXT_15 = NL + "\t\t\tString url_";
  protected final String TEXT_16 = " = \"tcp://\"+";
  protected final String TEXT_17 = "+\":\"+";
  protected final String TEXT_18 = ";" + NL + "\t\t\tlong timeout_";
  protected final String TEXT_19 = " =";
  protected final String TEXT_20 = ";";
  protected final String TEXT_21 = NL + "\t\t\torg.apache.activemq.broker.BrokerService broker_";
  protected final String TEXT_22 = " = new org.apache.activemq.broker.BrokerService();" + NL + "\t\t\tbroker_";
  protected final String TEXT_23 = ".setUseJmx(true);" + NL + "\t\t\tbroker_";
  protected final String TEXT_24 = ".addConnector(url_";
  protected final String TEXT_25 = ");" + NL + "\t\t\tbroker_";
  protected final String TEXT_26 = ".start();";
  protected final String TEXT_27 = NL + "\t\t\tSystem.out.println(\"Connecting to URL: \" + url_";
  protected final String TEXT_28 = ");" + NL + "\t\t\tSystem.out.println(\"Consuming \" + (";
  protected final String TEXT_29 = " ? \"topic\" : \"queue\") + \": \" + ";
  protected final String TEXT_30 = ");" + NL + "\t\t\t" + NL + "\t\t\torg.apache.activemq.ActiveMQConnectionFactory factory_";
  protected final String TEXT_31 = " = " + NL + "\t\t\tnew org.apache.activemq.ActiveMQConnectionFactory(url_";
  protected final String TEXT_32 = ");";
  protected final String TEXT_33 = "\t\t";
  protected final String TEXT_34 = NL + "\t\t\tjavax.jms.Connection connection_";
  protected final String TEXT_35 = " = factory_";
  protected final String TEXT_36 = ".createConnection();";
  protected final String TEXT_37 = NL + "\t\t\tjavax.jms.Connection connection_";
  protected final String TEXT_38 = " = factory_";
  protected final String TEXT_39 = ".createConnection(";
  protected final String TEXT_40 = ",";
  protected final String TEXT_41 = ");";
  protected final String TEXT_42 = NL + "\t\t\tconnection_";
  protected final String TEXT_43 = ".start();" + NL + "\t\t\tjavax.jms.Session session_";
  protected final String TEXT_44 = " = connection_";
  protected final String TEXT_45 = ".createSession(";
  protected final String TEXT_46 = ", javax.jms.Session.";
  protected final String TEXT_47 = ");" + NL + "\t\t\tjavax.jms.Destination des_";
  protected final String TEXT_48 = " = null;";
  protected final String TEXT_49 = NL + "\t\t\tdes_";
  protected final String TEXT_50 = " = session_";
  protected final String TEXT_51 = ".createQueue(";
  protected final String TEXT_52 = ");";
  protected final String TEXT_53 = NL + "\t    \tdes_";
  protected final String TEXT_54 = " = session_";
  protected final String TEXT_55 = ".createTopic(";
  protected final String TEXT_56 = ");";
  protected final String TEXT_57 = NL + "\t\t\tjavax.jms.MessageProducer replyProducer_";
  protected final String TEXT_58 = " = session_";
  protected final String TEXT_59 = ".createProducer(null);" + NL + "\t\t\treplyProducer_";
  protected final String TEXT_60 = ".setDeliveryMode(javax.jms.DeliveryMode.NON_PERSISTENT);" + NL + "\t\t\t" + NL + "\t\t\tjavax.jms.MessageConsumer consumer_";
  protected final String TEXT_61 = " = session_";
  protected final String TEXT_62 = ".createConsumer(des_";
  protected final String TEXT_63 = ");" + NL + "\t\t\t";
  protected final String TEXT_64 = "\t\t\t\t\t" + NL + "\t\t\tSystem.out.println(\"Ready to receive message\");" + NL + "\t\t\tSystem.out.println(\"Waiting...\");\t" + NL + "\t\t\tjavax.jms.Message message_";
  protected final String TEXT_65 = ";" + NL + "\t\t" + NL + "\t\t\twhile((message_";
  protected final String TEXT_66 = "=consumer_";
  protected final String TEXT_67 = ".receive())!=null){";
  protected final String TEXT_68 = NL + "\t\t\tSystem.out.println(\"We will consume messages while they continue to be delivered \");" + NL + "\t\t\tjavax.jms.Message message_";
  protected final String TEXT_69 = ";" + NL + "\t\t\twhile ((message_";
  protected final String TEXT_70 = " = consumer_";
  protected final String TEXT_71 = ".receive()) != null) {" + NL + "\t\t";
  protected final String TEXT_72 = NL + "\t\t\tint maxMsg_";
  protected final String TEXT_73 = " = ";
  protected final String TEXT_74 = ";" + NL + "\t\t\tSystem.out.println(\"We are about to wait until we consume: \" + maxMsg_";
  protected final String TEXT_75 = " + \" message(s) then we will shutdown\");" + NL + "\t\t\tfor (int i_";
  protected final String TEXT_76 = " = 0; i_";
  protected final String TEXT_77 = " < maxMsg_";
  protected final String TEXT_78 = " ;) {" + NL + "\t\t        javax.jms.Message message_";
  protected final String TEXT_79 = " = consumer_";
  protected final String TEXT_80 = ".receive();" + NL + "\t\t        if (message_";
  protected final String TEXT_81 = " != null) {" + NL + "\t            \ti_";
  protected final String TEXT_82 = "++;";
  protected final String TEXT_83 = NL + "\t\t\t    System.out.println(\"We will wait for messages within: \" + ";
  protected final String TEXT_84 = "*1000 + \" ms, and then we will shutdown\");" + NL + "\t\t\t    javax.jms.Message message_";
  protected final String TEXT_85 = ";" + NL + "\t\t\t    while ((message_";
  protected final String TEXT_86 = " = consumer_";
  protected final String TEXT_87 = ".receive(";
  protected final String TEXT_88 = "*1000)) != null) {";
  protected final String TEXT_89 = NL;
  protected final String TEXT_90 = NL + "\t\t\tif (message_";
  protected final String TEXT_91 = " instanceof javax.jms.MapMessage) {" + NL + "\t\t\t\tjavax.jms.MapMessage txtMsg_";
  protected final String TEXT_92 = " = (javax.jms.MapMessage) message_";
  protected final String TEXT_93 = ";" + NL + "\t\t\t\tString msg_";
  protected final String TEXT_94 = " = txtMsg_";
  protected final String TEXT_95 = ".getString(";
  protected final String TEXT_96 = ");" + NL + "\t\t\t\tif(msg_";
  protected final String TEXT_97 = " !=null){" + NL;
  protected final String TEXT_98 = NL + "\t\t\t\tjavax.jms.TextMessage txtMsg_";
  protected final String TEXT_99 = " = (javax.jms.TextMessage) message_";
  protected final String TEXT_100 = ";" + NL + "\t\t\t\tString msg_";
  protected final String TEXT_101 = " = txtMsg_";
  protected final String TEXT_102 = ".getText();" + NL + "\t\t\t";
  protected final String TEXT_103 = NL + "\t\t\t\tjavax.jms.BytesMessage bytesMsg_";
  protected final String TEXT_104 = " = (javax.jms.BytesMessage) message_";
  protected final String TEXT_105 = ";" + NL + "\t\t\t\tbyte[] bytesMsgBody_";
  protected final String TEXT_106 = " = new byte[(int)bytesMsg_";
  protected final String TEXT_107 = ".getBodyLength()];" + NL + "\t\t\t\tbytesMsg_";
  protected final String TEXT_108 = ".readBytes(bytesMsgBody_";
  protected final String TEXT_109 = ");" + NL + "\t\t\t\tString msg_";
  protected final String TEXT_110 = " = new String(bytesMsgBody_";
  protected final String TEXT_111 = ");";
  protected final String TEXT_112 = NL + "\t\t\t\tjavax.jms.MapMessage  msg_";
  protected final String TEXT_113 = "  = (javax.jms.MapMessage) message_";
  protected final String TEXT_114 = ";";
  protected final String TEXT_115 = NL + "\t\t\t\t\t";
  protected final String TEXT_116 = ".from=";
  protected final String TEXT_117 = ";\t" + NL + "\t\t\t\t\t";
  protected final String TEXT_118 = ".message=msg_";
  protected final String TEXT_119 = ";\t";
  protected final String TEXT_120 = NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_121 = ".";
  protected final String TEXT_122 = " = msg_";
  protected final String TEXT_123 = ".get";
  protected final String TEXT_124 = "(\"";
  protected final String TEXT_125 = "\");" + NL;
  protected final String TEXT_126 = NL + "\t\t\t\t\t\t";
  protected final String TEXT_127 = ".";
  protected final String TEXT_128 = " = message_";
  protected final String TEXT_129 = ".get";
  protected final String TEXT_130 = "();";
  protected final String TEXT_131 = NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_132 = ".";
  protected final String TEXT_133 = " = message_";
  protected final String TEXT_134 = ".get";
  protected final String TEXT_135 = "Property(";
  protected final String TEXT_136 = ");";
  protected final String TEXT_137 = "\t\t" + NL + "\t\t\t\t\t";
  protected final String TEXT_138 = ".messageid=";
  protected final String TEXT_139 = ";" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t}";
  protected final String TEXT_140 = NL + "\t\t\t}";
  protected final String TEXT_141 = "\t    ";
  protected final String TEXT_142 = NL + "\t\tjava.util.Hashtable properties";
  protected final String TEXT_143 = "=new java.util.Hashtable();" + NL + "\t\tproperties";
  protected final String TEXT_144 = ".put(\"hostname\", ";
  protected final String TEXT_145 = ");" + NL + "\t\tproperties";
  protected final String TEXT_146 = ".put(\"port\", Integer.valueOf(";
  protected final String TEXT_147 = "));" + NL + "\t\tproperties";
  protected final String TEXT_148 = ".put(\"channel\", ";
  protected final String TEXT_149 = ");" + NL + "\t\tproperties";
  protected final String TEXT_150 = ".put(\"CCSID\", new Integer(1208));" + NL + "\t\tproperties";
  protected final String TEXT_151 = ".put(\"transport\",\"MQSeries\");";
  protected final String TEXT_152 = NL + "\t\tproperties";
  protected final String TEXT_153 = ".put(\"userID\",";
  protected final String TEXT_154 = ");" + NL + "\t\tproperties";
  protected final String TEXT_155 = ".put(\"password\",";
  protected final String TEXT_156 = ");";
  protected final String TEXT_157 = NL + NL + "\t\tcom.ibm.mq.MQQueueManager qMgr";
  protected final String TEXT_158 = "=null;" + NL + "\t\tcom.ibm.mq.MQQueue remoteQ";
  protected final String TEXT_159 = "=null;";
  protected final String TEXT_160 = NL + "\t\tString msgId_";
  protected final String TEXT_161 = " = ";
  protected final String TEXT_162 = ";" + NL + "\t    if (msgId_";
  protected final String TEXT_163 = " != null & !(\"\").equals(msgId_";
  protected final String TEXT_164 = ")) {" + NL + "\t\t\tString padding_";
  protected final String TEXT_165 = " = new String();" + NL + "\t       \tint padlen_";
  protected final String TEXT_166 = " = 24;" + NL + "\t " + NL + "\t       \tint len_";
  protected final String TEXT_167 = " = Math.abs(padlen_";
  protected final String TEXT_168 = ") - msgId_";
  protected final String TEXT_169 = ".toString().length();" + NL + "\t       \tif (len_";
  protected final String TEXT_170 = " > 0) {" + NL + "\t        \tfor (int i = 0 ; i < len_";
  protected final String TEXT_171 = " ; i++) {" + NL + "\t           \t\tpadding_";
  protected final String TEXT_172 = " = padding_";
  protected final String TEXT_173 = " + \" \";" + NL + "\t         \t}" + NL + "\t        \tmsgId_";
  protected final String TEXT_174 = " = msgId_";
  protected final String TEXT_175 = " + padding_";
  protected final String TEXT_176 = ";" + NL + "\t        }" + NL + "\t\t}";
  protected final String TEXT_177 = NL + "\t\ttry{" + NL + "\t" + NL + "\t\t\tint openOptions";
  protected final String TEXT_178 = "=com.ibm.mq.MQC.MQOO_INPUT_SHARED | com.ibm.mq.MQC.MQOO_FAIL_IF_QUIESCING | com.ibm.mq.MQC.MQOO_INQUIRE";
  protected final String TEXT_179 = " | com.ibm.mq.MQC.MQOO_BROWSE";
  protected final String TEXT_180 = ";" + NL + "\t\t" + NL + "\t\t\tcom.ibm.mq.MQGetMessageOptions gmo";
  protected final String TEXT_181 = "=new com.ibm.mq.MQGetMessageOptions();" + NL + "\t\t\t";
  protected final String TEXT_182 = NL + "\t\t\tgmo";
  protected final String TEXT_183 = ".options=gmo";
  protected final String TEXT_184 = ".options+com.ibm.mq.MQC.MQGMO_BROWSE_FIRST;" + NL + "\t\t\tint browseCursor_";
  protected final String TEXT_185 = " = 0;" + NL + "\t\t\t";
  protected final String TEXT_186 = NL + "\t\t\tgmo";
  protected final String TEXT_187 = ".options=gmo";
  protected final String TEXT_188 = ".options+com.ibm.mq.MQC.MQGMO_SYNCPOINT;" + NL + "\t\t\t";
  protected final String TEXT_189 = NL + "\t\t\tgmo";
  protected final String TEXT_190 = ".options=gmo";
  protected final String TEXT_191 = ".options+com.ibm.mq.MQC.";
  protected final String TEXT_192 = "MQGMO_NO_WAIT";
  protected final String TEXT_193 = "MQGMO_WAIT";
  protected final String TEXT_194 = ";" + NL + "\t\t\tgmo";
  protected final String TEXT_195 = ".options=gmo";
  protected final String TEXT_196 = ".options+com.ibm.mq.MQC.MQGMO_FAIL_IF_QUIESCING;" + NL + "\t\t\tgmo";
  protected final String TEXT_197 = ".waitInterval=com.ibm.mq.MQC.MQWI_UNLIMITED;" + NL + "\t\t\tcom.ibm.mq.MQException.log = null;" + NL + "\t\t\tboolean flag";
  protected final String TEXT_198 = "=true;" + NL + "\t\t" + NL + "\t\t\tqMgr";
  protected final String TEXT_199 = "=new com.ibm.mq.MQQueueManager(";
  protected final String TEXT_200 = ",properties";
  protected final String TEXT_201 = ");" + NL + "\t\t\tremoteQ";
  protected final String TEXT_202 = "=qMgr";
  protected final String TEXT_203 = ".accessQueue(";
  protected final String TEXT_204 = ",openOptions";
  protected final String TEXT_205 = ");" + NL + "\t";
  protected final String TEXT_206 = NL + "\t\t\tif(Integer.valueOf(remoteQ";
  protected final String TEXT_207 = ".getCurrentDepth()).equals(0))" + NL + "\t\t\t{" + NL + "\t\t\t\tflag";
  protected final String TEXT_208 = "= false;" + NL + "\t\t\t} \t";
  protected final String TEXT_209 = "\t" + NL + "\t\t\tSystem.out.println(\"Ready to receive message\");" + NL + "\t\t\tSystem.out.println(\"Waiting...\");\t" + NL + "\t\t\twhile(flag";
  protected final String TEXT_210 = "){";
  protected final String TEXT_211 = NL + "\t\t\tif(Integer.valueOf(remoteQ";
  protected final String TEXT_212 = ".getCurrentDepth()).equals(1))" + NL + "\t\t\t{" + NL + "\t\t\t\tflag";
  protected final String TEXT_213 = "= false;" + NL + "\t\t\t} \t\t\t";
  protected final String TEXT_214 = NL + "\t\t";
  protected final String TEXT_215 = NL + "\t\t\tif(browseCursor_";
  protected final String TEXT_216 = " > 0){" + NL + "\t\t\t\tgmo";
  protected final String TEXT_217 = ".options=com.ibm.mq.MQC.MQGMO_BROWSE_NEXT; " + NL + "\t\t\t\tgmo";
  protected final String TEXT_218 = ".options=gmo";
  protected final String TEXT_219 = ".options+com.ibm.mq.MQC.";
  protected final String TEXT_220 = "MQGMO_NO_WAIT";
  protected final String TEXT_221 = "MQGMO_WAIT";
  protected final String TEXT_222 = ";" + NL + "\t\t\t\tgmo";
  protected final String TEXT_223 = ".options=gmo";
  protected final String TEXT_224 = ".options+com.ibm.mq.MQC.MQGMO_FAIL_IF_QUIESCING;" + NL + "\t\t\t}" + NL + "\t\t\tbrowseCursor_";
  protected final String TEXT_225 = "++;" + NL + "\t\t";
  protected final String TEXT_226 = NL + "\t\t\tcom.ibm.mq.MQMessage inMessage";
  protected final String TEXT_227 = "=new com.ibm.mq.MQMessage();";
  protected final String TEXT_228 = NL + "\t\t\tinMessage";
  protected final String TEXT_229 = ".messageId = msgId_";
  protected final String TEXT_230 = ".getBytes(\"ISO-8859-15\");";
  protected final String TEXT_231 = "\t\t" + NL + "\t\t\ttry{" + NL + "\t\t\t\tremoteQ";
  protected final String TEXT_232 = ".get(inMessage";
  protected final String TEXT_233 = ",gmo";
  protected final String TEXT_234 = ");" + NL + "\t\t\t}catch (com.ibm.mq.MQException me_";
  protected final String TEXT_235 = ") {   " + NL + "\t            if (me_";
  protected final String TEXT_236 = ".reasonCode == com.ibm.mq.MQException.MQRC_NO_MSG_AVAILABLE) {" + NL + "\t            \tbreak;   " + NL + "\t            }else{" + NL + "\t            \tthrow me_";
  protected final String TEXT_237 = ";" + NL + "\t            }   " + NL + "\t     \t}";
  protected final String TEXT_238 = NL + "\t\t\tremoteQ";
  protected final String TEXT_239 = ".get(inMessage";
  protected final String TEXT_240 = ",gmo";
  protected final String TEXT_241 = ");";
  protected final String TEXT_242 = NL;
  protected final String TEXT_243 = NL + "\t\t    String msg_";
  protected final String TEXT_244 = "=inMessage";
  protected final String TEXT_245 = ".readStringOfByteLength(inMessage";
  protected final String TEXT_246 = ".getMessageLength());";
  protected final String TEXT_247 = NL + "\t\t\tbyte[] bytesMsgBody_";
  protected final String TEXT_248 = " = new byte[inMessage";
  protected final String TEXT_249 = ".getMessageLength()];" + NL + "\t\t\tinMessage";
  protected final String TEXT_250 = ".readFully(bytesMsgBody_";
  protected final String TEXT_251 = ");" + NL + "\t\t\tString msg_";
  protected final String TEXT_252 = " = new String(bytesMsgBody_";
  protected final String TEXT_253 = ");";
  protected final String TEXT_254 = NL + "\t\t\tjava.util.Map msg_";
  protected final String TEXT_255 = " = (java.util.Map)inMessage";
  protected final String TEXT_256 = ".readObject();\t\t\t  ";
  protected final String TEXT_257 = NL + "\t\t\tqMgr";
  protected final String TEXT_258 = ".commit();";
  protected final String TEXT_259 = NL + "\t\t\t\t\t";
  protected final String TEXT_260 = ".from=";
  protected final String TEXT_261 = ";\t" + NL + "\t\t\t\t\t";
  protected final String TEXT_262 = ".message=msg_";
  protected final String TEXT_263 = ";\t";
  protected final String TEXT_264 = NL + "\t\t\t\t\t\t";
  protected final String TEXT_265 = ".";
  protected final String TEXT_266 = " = (";
  protected final String TEXT_267 = ")msg_";
  protected final String TEXT_268 = ".get(\"";
  protected final String TEXT_269 = "\");" + NL;
  protected final String TEXT_270 = NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_271 = ".";
  protected final String TEXT_272 = " = inMessage";
  protected final String TEXT_273 = ".";
  protected final String TEXT_274 = ";";
  protected final String TEXT_275 = NL + "\t\t\t\t";
  protected final String TEXT_276 = ".messageid=new String(inMessage";
  protected final String TEXT_277 = ".messageId,\"ISO-8859-15\");\t";
  protected final String TEXT_278 = NL + NL + NL + "\t\t";
  protected final String TEXT_279 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    

	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	String serverType=ElementParameterParser.getValue(node, "__SERVER__");
	String host=ElementParameterParser.getValue(node, "__SERVERADDRESS__");
	String port=ElementParameterParser.getValue(node, "__SERVERPORT__");
	String kListen=ElementParameterParser.getValue(node, "__KEEPLISTENING__");
	String timeOut=ElementParameterParser.getValue(node, "__TIMEOUT__");
	String msgBobyType =  ElementParameterParser.getValue(node, "__MESSAGE_BODY_TYPE__");
	String msgId=ElementParameterParser.getValue(node, "__MSG_ID__");
	String useMsgId=ElementParameterParser.getValue(node, "__IS_USE_MESSAGE_ID__");
	String from=ElementParameterParser.getValue(node, "__FROM__");
	boolean useMax = ("true").equals(ElementParameterParser.getValue(node,"__USEMAX__"));
	String msgType = ElementParameterParser.getValue(node, "__MSGTYPE__");
	
	boolean transacted = "true".equals(ElementParameterParser.getValue(node, "__IS_TRANSACTED__"));
	String acknowledgmentMode = ElementParameterParser.getValue(node, "__ACKNOWLEDGMENT_MODE__");
	
	boolean getJmsHeader =  ("true").equals(ElementParameterParser.getValue(node, "__GET_JMS_HEADER__"));
	List<Map<String,String>> jmsHeaders = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__JMS_HEADERS__");
	
	boolean getJmsProp =  ("true").equals(ElementParameterParser.getValue(node, "__GET_JMS_PROPERTIES__"));
	List<Map<String,String>> jmsProps = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__JMS_PROPERTIES__");
	
	boolean getMQMDField =  ("true").equals(ElementParameterParser.getValue(node, "__GET_MQMD_FIELDS__"));
	List<Map<String,String>> mqmdFields = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__MQMD_FIELDS__");
	
	String dbuser= ElementParameterParser.getValue(node, "__USER__");
	String dbpwd= ElementParameterParser.getValue(node, "__PASS__");
    	
	IMetadataTable metadata=null;
	List<IMetadataColumn> columns = null;
	List<IMetadataTable> metadatas = node.getMetadataList();
	if ((metadatas!=null)&&(metadatas.size()>0)) {
		metadata = metadatas.get(0);
		columns = metadata.getListColumns();
	}
	if (("JBoss").equals(serverType)||("ActiveMQ").equals(serverType)) {
	
		/*---------------------------------------1.initial jms connection factry---------------------------------*/ 
		if(("JBoss").equals(serverType) ){ // server judgement

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(host);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(port);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
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
    		
		}else if(("ActiveMQ").equals(serverType)){
			boolean startServer = ("true").equals(ElementParameterParser.getValue(node, "__STARTSERVER__"));
			String maxiumMessages = ElementParameterParser.getValue(node,"__MAXMSG__");
			String receiveTimeOut = ElementParameterParser.getValue(node,"__TIMEOUT__");
			
			if(("").equals(maxiumMessages)|| maxiumMessages == null){
				maxiumMessages = "0";
			}
			if(("").equals(receiveTimeOut)|| receiveTimeOut == null){
				receiveTimeOut = "0L";
			}

    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(host);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(port);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(receiveTimeOut );
    stringBuffer.append(TEXT_20);
    
			if(startServer){

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
    
			}

    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append("Topic".equals(msgType));
    stringBuffer.append(TEXT_29);
    stringBuffer.append(from);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    
		}
		
	/*---------------------------------------2.create Queue Or Topic from connection ---------------------------------*/ 

    stringBuffer.append(TEXT_33);
    
		if(dbuser == null || ("\"\"").equals(dbuser) || ("").equals(dbuser)) {

    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    
		} else {

    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(dbuser);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(dbpwd);
    stringBuffer.append(TEXT_41);
    
		}

    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(transacted);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(acknowledgmentMode);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    
		if (("Queue").equals(msgType)) {

    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(from );
    stringBuffer.append(TEXT_52);
    
		} else {

    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_55);
    stringBuffer.append(from );
    stringBuffer.append(TEXT_56);
    
		}

    stringBuffer.append(TEXT_57);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    		
		/*---------------------------------------3.recevice message form server ---------------------------------*/ 	
		if(("JBoss").equals(serverType) ){ 

    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_67);
    		
		}else if(("ActiveMQ").equals(serverType)){
			String maxiumMessages = ElementParameterParser.getValue(node,"__MAXMSG__");
			String receiveTimeOut = ElementParameterParser.getValue(node,"__TIMEOUT__");
			if(("true").equals(kListen)){

    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_71);
    
			}else if (useMax) {

    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_73);
    stringBuffer.append(maxiumMessages );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_80);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_82);
    
				}else {

    stringBuffer.append(TEXT_83);
    stringBuffer.append(receiveTimeOut);
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_87);
    stringBuffer.append(receiveTimeOut);
    stringBuffer.append(TEXT_88);
    
				}
		}

    stringBuffer.append(TEXT_89);
    
		/*-----------------------------------------------------------4.judge message body type---------------------------------------*/
		if(("true").equals(useMsgId) && !"Map".equals(msgBobyType)){

    stringBuffer.append(TEXT_90);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_93);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_95);
    stringBuffer.append(msgId);
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_97);
    
		}else{
			if ("Text".equals(msgBobyType)) {

    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_99);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_101);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_102);
    
			} else if ("Bytes".equals(msgBobyType)) {

    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_105);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_109);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_111);
    
			} else if ("Map".equals(msgBobyType)) {

    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_114);
    
			}
		}
		//out put
		List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
		List<IMetadataColumn> columnList = metadata.getListColumns();
		for(IConnection conn:conns){
			if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.MAIN)) {
		 		String firstConnName = conn.getName();
		 		
		 		if ("Text".equals(msgBobyType) || "Bytes".equals(msgBobyType)) {

    stringBuffer.append(TEXT_115);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_116);
    stringBuffer.append(from);
    stringBuffer.append(TEXT_117);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_118);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_119);
    
				} else {
					for(IMetadataColumn column : columns) {
						 String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
						  if(("byte[]").equals(typeToGenerate)) {
				                typeToGenerate = "Bytes";
				            }else if(("Character").equals(typeToGenerate)) {
				            	 typeToGenerate = "Char";
				            }else if(("Integer").equals(typeToGenerate)) {
				            	 typeToGenerate = "Int";
				            } else if(("Java.util.Date").equals(typeToGenerate)||"BigDecimal".equals(typeToGenerate)
				            			||"List".equals(typeToGenerate)) {
				            	 typeToGenerate = "Object";
				            }else {
				                typeToGenerate = typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
				            }

    stringBuffer.append(TEXT_120);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_121);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_122);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_123);
    stringBuffer.append(typeToGenerate);
    stringBuffer.append(TEXT_124);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_125);
    
					}
				}
				/*---------------------------------------------get message headers------------------------------------------------------*/
				if (getJmsHeader) {
					for(Map<String,String> header:jmsHeaders) {

    stringBuffer.append(TEXT_126);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_127);
    stringBuffer.append(header.get("REFERENCE_COLUMN"));
    stringBuffer.append(TEXT_128);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_129);
    stringBuffer.append(header.get("JMS_HEADER_NAME"));
    stringBuffer.append(TEXT_130);
    				
					}
				}	
								
				/*---------------------------------------------4.set message headers------------------------------------------------------*/
				if (getJmsProp) {
					for(Map<String,String> prop:jmsProps) {

    stringBuffer.append(TEXT_131);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_132);
    stringBuffer.append(prop.get("REFERENCE_COLUMN"));
    stringBuffer.append(TEXT_133);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_134);
    stringBuffer.append(prop.get("JMS_PROPERTIES_TYPE"));
    stringBuffer.append(TEXT_135);
    stringBuffer.append(prop.get("JMS_PROPERTIES_NAME"));
    stringBuffer.append(TEXT_136);
    				
					}
				}					


				if(("true").equals(useMsgId) && !"Map".equals(msgBobyType)){

    stringBuffer.append(TEXT_137);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_138);
    stringBuffer.append(msgId);
    stringBuffer.append(TEXT_139);
    
				}
			}
		}	
		
		if (useMax) {

    stringBuffer.append(TEXT_140);
    
		}
		/*---------------------------------------------------------end 4----------------------------------------------------------------*/	

    stringBuffer.append(TEXT_141);
    
	} else { //server judgement   /***WebSphere MQ*****/
		String channel=ElementParameterParser.getValue(node, "__CHANNEL__");
		String qm=ElementParameterParser.getValue(node, "__QM__");
		String queue = ElementParameterParser.getValue(node, "__QUEUE__");
		boolean isRollback = ("true").equals(ElementParameterParser.getValue(node, "__ROLLBACK__"));
		boolean isCommit = ("true").equals(ElementParameterParser.getValue(node, "__COMMIT__"));
		boolean isBrowse = ("true").equals(ElementParameterParser.getValue(node,"__BROWSE__"));
		isCommit = isCommit && !isBrowse;
		isBrowse = isBrowse && !isRollback && !isCommit;

    stringBuffer.append(TEXT_142);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_143);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_144);
    stringBuffer.append(host);
    stringBuffer.append(TEXT_145);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_146);
    stringBuffer.append(port);
    stringBuffer.append(TEXT_147);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_148);
    stringBuffer.append(channel);
    stringBuffer.append(TEXT_149);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_150);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_151);
    
		if(!(dbuser == null) && !("\"\"").equals(dbuser) && !("").equals(dbuser)) {

    stringBuffer.append(TEXT_152);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_153);
    stringBuffer.append(dbuser);
    stringBuffer.append(TEXT_154);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_155);
    stringBuffer.append(dbpwd);
    stringBuffer.append(TEXT_156);
    
		}

    stringBuffer.append(TEXT_157);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_158);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_159);
    
		if(("true").equals(useMsgId)){

    stringBuffer.append(TEXT_160);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_161);
    stringBuffer.append(msgId);
    stringBuffer.append(TEXT_162);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_163);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_164);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_165);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_166);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_167);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_168);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_169);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_170);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_171);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_172);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_173);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_174);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_175);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_176);
    
		}

    stringBuffer.append(TEXT_177);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_178);
    if(isBrowse){
    stringBuffer.append(TEXT_179);
    }
    stringBuffer.append(TEXT_180);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_181);
    if(isBrowse){
    stringBuffer.append(TEXT_182);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_183);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_184);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_185);
    }else{
    stringBuffer.append(TEXT_186);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_187);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_188);
    }
    stringBuffer.append(TEXT_189);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_190);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_191);
    if(("false").equals(kListen)){
    stringBuffer.append(TEXT_192);
    }else{
    stringBuffer.append(TEXT_193);
    }
    stringBuffer.append(TEXT_194);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_195);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_196);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_197);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_198);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_199);
    stringBuffer.append(qm);
    stringBuffer.append(TEXT_200);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_201);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_202);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_203);
    stringBuffer.append(queue);
    stringBuffer.append(TEXT_204);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_205);
    
		if(("false").equals(kListen)) {

    stringBuffer.append(TEXT_206);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_207);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_208);
    
		}	

    stringBuffer.append(TEXT_209);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_210);
    
		if(("false").equals(kListen)){

    stringBuffer.append(TEXT_211);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_212);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_213);
    
		}

    stringBuffer.append(TEXT_214);
    
		if(isBrowse){
		
    stringBuffer.append(TEXT_215);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_216);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_217);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_218);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_219);
    if(("false").equals(kListen)){
    stringBuffer.append(TEXT_220);
    }else{
    stringBuffer.append(TEXT_221);
    }
    stringBuffer.append(TEXT_222);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_223);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_224);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_225);
    }
    stringBuffer.append(TEXT_226);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_227);
    
		if(("true").equals(useMsgId)&& !"Map".equals(msgBobyType)){

    stringBuffer.append(TEXT_228);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_229);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_230);
    
		}
		if(("false").equals(kListen) && ("true").equals(useMsgId) && !"Map".equals(msgBobyType) ){

    stringBuffer.append(TEXT_231);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_232);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_233);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_234);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_235);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_236);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_237);
    
		} else {

    stringBuffer.append(TEXT_238);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_239);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_240);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_241);
    
		}

    stringBuffer.append(TEXT_242);
    		
		if ("Text".equals(msgBobyType)) {

    stringBuffer.append(TEXT_243);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_244);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_245);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_246);
    
		} else if ("Bytes".equals(msgBobyType)) {

    stringBuffer.append(TEXT_247);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_248);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_249);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_250);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_251);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_252);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_253);
    
		} else if ("Map".equals(msgBobyType)) {

    stringBuffer.append(TEXT_254);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_255);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_256);
    
		}
		if(isCommit){

    stringBuffer.append(TEXT_257);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_258);
    
		}
	List< ? extends IConnection> conns = node.getOutgoingSortedConnections();
	List<IMetadataColumn> columnList = metadata.getListColumns();
	for(IConnection conn:conns){
		if (conn.getLineStyle().hasConnectionCategory(IConnectionCategory.MAIN)) {
 			String firstConnName = conn.getName();
		 		if ("Text".equals(msgBobyType) || "Bytes".equals(msgBobyType)) {

    stringBuffer.append(TEXT_259);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_260);
    stringBuffer.append(queue);
    stringBuffer.append(TEXT_261);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_262);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_263);
    
				} else {
					for(IMetadataColumn column : columns) {
						String columType = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());


    stringBuffer.append(TEXT_264);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_265);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_266);
    stringBuffer.append(columType);
    stringBuffer.append(TEXT_267);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_268);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_269);
    
					}
				}
					
				/*---------------------------------------------get MQMD Fields------------------------------------------------------*/
					
				if (getMQMDField) {
					for(Map<String,String> field:mqmdFields) {

    stringBuffer.append(TEXT_270);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_271);
    stringBuffer.append(field.get("REFERENCE_COLUMN"));
    stringBuffer.append(TEXT_272);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_273);
    stringBuffer.append(field.get("MQMD_FIELD_NAME"));
    stringBuffer.append(TEXT_274);
    				
					}
				}
				
			if(("true").equals(useMsgId)&& !"Map".equals(msgBobyType)){

    stringBuffer.append(TEXT_275);
    stringBuffer.append(firstConnName);
    stringBuffer.append(TEXT_276);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_277);
    
			}
		}
	}
}

    stringBuffer.append(TEXT_278);
    stringBuffer.append(TEXT_279);
    return stringBuffer.toString();
  }
}
