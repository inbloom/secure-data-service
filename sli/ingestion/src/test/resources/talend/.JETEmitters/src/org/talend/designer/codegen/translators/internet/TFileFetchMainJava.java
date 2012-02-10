package org.talend.designer.codegen.translators.internet;

import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TFileFetchMainJava
{
  protected static String nl;
  public static synchronized TFileFetchMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TFileFetchMainJava result = new TFileFetchMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "java.io.InputStream retIS_";
  protected final String TEXT_2 = " = null;" + NL;
  protected final String TEXT_3 = NL + "\t\tclass SocketFactory_";
  protected final String TEXT_4 = " implements org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory {" + NL + "" + NL + "\t\t\tprivate javax.net.ssl.SSLContext sslcontext = null;" + NL + "" + NL + "\t\t\tprivate javax.net.ssl.SSLContext createSSLContext() {" + NL + "\t\t\t\tjavax.net.ssl.SSLContext sslcontext = null;" + NL + "" + NL + "\t\t\t\ttry {" + NL + "\t\t\t\t\tsslcontext = javax.net.ssl.SSLContext.getInstance(\"SSL\");" + NL + "\t\t\t\t\tsslcontext.init(null, new javax.net.ssl.TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());" + NL + "\t\t\t\t} catch (java.security.NoSuchAlgorithmException e) {" + NL + "\t\t\t\t\te.printStackTrace();" + NL + "\t\t\t\t} catch (java.security.KeyManagementException e) {" + NL + "\t\t\t\t\te.printStackTrace();" + NL + "\t\t\t\t}" + NL + "\t\t\t\treturn sslcontext;" + NL + "\t\t\t}" + NL + "" + NL + "\t\t\tprivate javax.net.ssl.SSLContext getSSLContext() {" + NL + "" + NL + "\t\t\t\tif (this.sslcontext == null) {" + NL + "\t\t\t\t\tthis.sslcontext = createSSLContext();" + NL + "\t\t\t\t}" + NL + "\t\t\t\treturn this.sslcontext;" + NL + "\t\t\t}" + NL + "" + NL + "\t\t\tpublic java.net.Socket createSocket(java.net.Socket socket, String host, int port, boolean autoClose)" + NL + "\t\t\t\tthrows java.io.IOException, java.net.UnknownHostException {" + NL + "\t\t\t\treturn getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);" + NL + "\t\t\t}" + NL + "" + NL + "\t\t\tpublic java.net.Socket createSocket(String host, int port) throws java.io.IOException, java.net.UnknownHostException {" + NL + "\t\t\t\treturn getSSLContext().getSocketFactory().createSocket(host, port);" + NL + "\t\t\t}" + NL + "" + NL + "\t\t\tpublic java.net.Socket createSocket(String host, int port, java.net.InetAddress clientHost, int clientPort)" + NL + "\t\t\t\tthrows java.io.IOException, java.net.UnknownHostException {" + NL + "\t\t\t\treturn getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort);" + NL + "\t\t\t}" + NL + "" + NL + "\t\t\tpublic java.net.Socket createSocket(String host, int port, java.net.InetAddress localAddress, int localPort, org.apache.commons.httpclient.params.HttpConnectionParams params) " + NL + "\t\t\t\tthrows java.io.IOException, java.net.UnknownHostException, org.apache.commons.httpclient.ConnectTimeoutException {" + NL + "" + NL + "\t\t\t\tif (params == null) {" + NL + "\t\t\t\t\tthrow new IllegalArgumentException(\"Parameters may not be null\");" + NL + "\t\t\t\t}" + NL + "\t\t\t\tint timeout = params.getConnectionTimeout();" + NL + "\t\t\t\tjavax.net.SocketFactory socketfactory = getSSLContext().getSocketFactory();" + NL + "" + NL + "\t\t\t\tif (timeout == 0) {" + NL + "\t\t\t\t\treturn socketfactory.createSocket(host, port, localAddress, localPort);" + NL + "\t\t\t\t} else {" + NL + "\t\t\t\t\tjava.net.Socket socket = socketfactory.createSocket();" + NL + "\t\t\t\t\tjava.net.SocketAddress localaddr = new java.net.InetSocketAddress(localAddress, localPort);" + NL + "\t\t\t\t\tjava.net.SocketAddress remoteaddr = new java.net.InetSocketAddress(host, port);" + NL + "\t\t\t\t\tsocket.bind(localaddr);" + NL + "\t\t\t\t\tsocket.connect(remoteaddr, timeout);" + NL + "\t\t\t\t\treturn socket;" + NL + "\t\t\t\t}" + NL + "\t\t\t}" + NL + "" + NL + "\t\t\tclass TrustAnyTrustManager implements javax.net.ssl.X509TrustManager {" + NL + "\t\t\t\tpublic void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)" + NL + "\t\t\t\t\tthrows java.security.cert.CertificateException {" + NL + "\t\t\t\t}" + NL + "\t\t\t\tpublic void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)" + NL + "\t\t\t\t\tthrows java.security.cert.CertificateException {" + NL + "\t\t\t\t}" + NL + "\t\t\t\tpublic java.security.cert.X509Certificate[] getAcceptedIssuers() {" + NL + "\t\t\t\t\treturn new java.security.cert.X509Certificate[] {};" + NL + "\t\t\t\t}" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "" + NL + "\t\tif ((";
  protected final String TEXT_5 = ").toLowerCase().startsWith(\"https://\")) {" + NL + "\t\t\torg.apache.commons.httpclient.protocol.Protocol myhttps = new org.apache.commons.httpclient.protocol.Protocol(\"https\", new SocketFactory_";
  protected final String TEXT_6 = "(), 443);" + NL + "\t\t\torg.apache.commons.httpclient.protocol.Protocol.registerProtocol(\"https\", myhttps);" + NL + "\t\t}" + NL + "\t";
  protected final String TEXT_7 = NL + "\torg.apache.commons.httpclient.HttpClient client_";
  protected final String TEXT_8 = " = new org.apache.commons.httpclient.HttpClient();" + NL + "\tclient_";
  protected final String TEXT_9 = ".getHttpConnectionManager().getParams().setConnectionTimeout(";
  protected final String TEXT_10 = ");" + NL + "\tclient_";
  protected final String TEXT_11 = ".getParams().setCookiePolicy(org.apache.commons.httpclient.cookie.CookiePolicy.";
  protected final String TEXT_12 = ");" + NL + "\t";
  protected final String TEXT_13 = NL + "\tclient_";
  protected final String TEXT_14 = ".getParams().setBooleanParameter(org.apache.commons.httpclient.params.HttpMethodParams.SINGLE_COOKIE_HEADER, true);" + NL + "\t";
  protected final String TEXT_15 = NL + "\t";
  protected final String TEXT_16 = NL + "\t\tList<org.apache.commons.httpclient.Cookie> cookieList_";
  protected final String TEXT_17 = " = null;" + NL + "\t\tjava.io.FileInputStream fis_";
  protected final String TEXT_18 = " = null;" + NL + "\t\tjava.io.ObjectInputStream is_";
  protected final String TEXT_19 = " = null;" + NL + "\t\t" + NL + "\t\ttry {" + NL + "\t\t\tcookieList_";
  protected final String TEXT_20 = " = new java.util.ArrayList<org.apache.commons.httpclient.Cookie>();" + NL + "\t\t\tfis_";
  protected final String TEXT_21 = " = new java.io.FileInputStream(new java.io.File(";
  protected final String TEXT_22 = "));" + NL + "\t\t\tis_";
  protected final String TEXT_23 = " = new java.io.ObjectInputStream(fis_";
  protected final String TEXT_24 = ");" + NL + "\t\t\tObject obj_";
  protected final String TEXT_25 = " = is_";
  protected final String TEXT_26 = ".readObject();" + NL + "" + NL + "\t\t\twhile (obj_";
  protected final String TEXT_27 = " != null) {" + NL + "\t\t\t\tcookieList_";
  protected final String TEXT_28 = ".add((org.apache.commons.httpclient.Cookie) obj_";
  protected final String TEXT_29 = ");" + NL + "" + NL + "\t\t\t\ttry {" + NL + "\t\t\t\t\tobj_";
  protected final String TEXT_30 = " = is_";
  protected final String TEXT_31 = ".readObject();" + NL + "\t\t\t\t} catch (java.io.IOException e) {" + NL + "\t\t\t\t\tobj_";
  protected final String TEXT_32 = " = null;" + NL + "\t\t\t\t}" + NL + "\t\t\t}" + NL + "\t\t} catch (java.io.FileNotFoundException e1) {" + NL + "\t\t\te1.printStackTrace();" + NL + "\t\t} catch (java.io.IOException e) {" + NL + "\t\t\te.printStackTrace();" + NL + "\t\t} catch (java.lang.ClassNotFoundException e) {" + NL + "\t\t\te.printStackTrace();" + NL + "\t\t} finally {" + NL + "\t\t\tif (is_";
  protected final String TEXT_33 = " != null) is_";
  protected final String TEXT_34 = ".close();" + NL + "\t\t\tif (fis_";
  protected final String TEXT_35 = " != null) fis_";
  protected final String TEXT_36 = ".close();" + NL + "\t\t}" + NL + "" + NL + "\t\tif (cookieList_";
  protected final String TEXT_37 = " != null)" + NL + "\t\t\tclient_";
  protected final String TEXT_38 = ".getState().addCookies(cookieList_";
  protected final String TEXT_39 = ".toArray(new org.apache.commons.httpclient.Cookie[0]));" + NL + "\t";
  protected final String TEXT_40 = " " + NL + "\t\tclient_";
  protected final String TEXT_41 = ".getHostConfiguration().setProxy(";
  protected final String TEXT_42 = ", Integer.parseInt(";
  protected final String TEXT_43 = "));" + NL + "\t\t" + NL + "\t\t";
  protected final String TEXT_44 = NL + "\t\t\tclient_";
  protected final String TEXT_45 = ".getState().setProxyCredentials(" + NL + "\t\t\tnew org.apache.commons.httpclient.auth.AuthScope( ";
  protected final String TEXT_46 = ", Integer.parseInt(";
  protected final String TEXT_47 = "), null )," + NL + "\t\t\tnew org.apache.commons.httpclient.NTCredentials( ";
  protected final String TEXT_48 = "," + NL + "\t\t\t";
  protected final String TEXT_49 = ", ";
  protected final String TEXT_50 = ", ";
  protected final String TEXT_51 = " ));" + NL + "\t\t";
  protected final String TEXT_52 = NL + "\t\t\tclient_";
  protected final String TEXT_53 = ".getState().setProxyCredentials(" + NL + "\t\t\tnew org.apache.commons.httpclient.auth.AuthScope(";
  protected final String TEXT_54 = ", Integer.parseInt(";
  protected final String TEXT_55 = "), null)," + NL + "\t\t\tnew org.apache.commons.httpclient.UsernamePasswordCredentials(";
  protected final String TEXT_56 = ", ";
  protected final String TEXT_57 = "));" + NL + "\t\t";
  protected final String TEXT_58 = NL + "\t\t\tclient_";
  protected final String TEXT_59 = ".getState().setCredentials(org.apache.commons.httpclient.auth.AuthScope.ANY, new org.apache.commons.httpclient.NTCredentials(";
  protected final String TEXT_60 = ", ";
  protected final String TEXT_61 = ", new java.net.URL(";
  protected final String TEXT_62 = ").getHost(), ";
  protected final String TEXT_63 = "));" + NL + "\t\t";
  protected final String TEXT_64 = NL + "\t\t\tclient_";
  protected final String TEXT_65 = ".getState().setCredentials(org.apache.commons.httpclient.auth.AuthScope.ANY, new org.apache.commons.httpclient.UsernamePasswordCredentials(";
  protected final String TEXT_66 = ", ";
  protected final String TEXT_67 = "));" + NL + "\t\t";
  protected final String TEXT_68 = NL + "\t\torg.apache.commons.httpclient.methods.PostMethod method_";
  protected final String TEXT_69 = " = new org.apache.commons.httpclient.methods.PostMethod(";
  protected final String TEXT_70 = ");" + NL + "\t\t";
  protected final String TEXT_71 = NL + "\t\t\torg.apache.commons.httpclient.methods.multipart.StringPart common_";
  protected final String TEXT_72 = "_";
  protected final String TEXT_73 = " = new org.apache.commons.httpclient.methods.multipart.StringPart(";
  protected final String TEXT_74 = ", ";
  protected final String TEXT_75 = ");" + NL + "\t\t\t";
  protected final String TEXT_76 = NL + "\t\t\t\torg.apache.commons.httpclient.methods.multipart.FilePart file_";
  protected final String TEXT_77 = "_";
  protected final String TEXT_78 = "  = new org.apache.commons.httpclient.methods.multipart.FilePart(";
  protected final String TEXT_79 = ", new java.io.File(";
  protected final String TEXT_80 = "));  " + NL + "\t\t\t";
  protected final String TEXT_81 = NL + "\t\torg.apache.commons.httpclient.methods.multipart.Part[] parts_";
  protected final String TEXT_82 = " = new org.apache.commons.httpclient.methods.multipart.Part[]{";
  protected final String TEXT_83 = ",";
  protected final String TEXT_84 = "};    " + NL + "\t\tmethod_";
  protected final String TEXT_85 = ".setRequestEntity(new org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity(parts_";
  protected final String TEXT_86 = ", method_";
  protected final String TEXT_87 = ".getParams()));          " + NL + "\t";
  protected final String TEXT_88 = NL + "\t\torg.apache.commons.httpclient.methods.GetMethod method_";
  protected final String TEXT_89 = " = new org.apache.commons.httpclient.methods.GetMethod(";
  protected final String TEXT_90 = ");" + NL + "\t";
  protected final String TEXT_91 = NL + "\t\t\tmethod_";
  protected final String TEXT_92 = ".addRequestHeader(";
  protected final String TEXT_93 = ",";
  protected final String TEXT_94 = ");" + NL + "\t\t";
  protected final String TEXT_95 = NL + "\tboolean isContinue_";
  protected final String TEXT_96 = " = true;" + NL + "\tint status_";
  protected final String TEXT_97 = ";" + NL + "\tString finalURL_";
  protected final String TEXT_98 = " = ";
  protected final String TEXT_99 = ";" + NL + "" + NL + "\ttry { // B_01" + NL + "\t\t";
  protected final String TEXT_100 = NL + "\t\t\tboolean redirect_";
  protected final String TEXT_101 = " = true;" + NL + "" + NL + "\t\t\twhile (redirect_";
  protected final String TEXT_102 = ") {" + NL + "\t\t\t\tstatus_";
  protected final String TEXT_103 = " = client_";
  protected final String TEXT_104 = ".executeMethod(method_";
  protected final String TEXT_105 = ");" + NL + "" + NL + "\t\t\t\tif ((status_";
  protected final String TEXT_106 = " == org.apache.commons.httpclient.HttpStatus.SC_MOVED_TEMPORARILY) " + NL + "\t\t\t\t\t|| (status_";
  protected final String TEXT_107 = " == org.apache.commons.httpclient.HttpStatus.SC_MOVED_PERMANENTLY) " + NL + "\t\t\t\t\t\t|| (status_";
  protected final String TEXT_108 = " == org.apache.commons.httpclient.HttpStatus.SC_SEE_OTHER) " + NL + "\t\t\t\t\t\t\t|| (status_";
  protected final String TEXT_109 = " == org.apache.commons.httpclient.HttpStatus.SC_TEMPORARY_REDIRECT)) {" + NL + "\t\t\t\t\tmethod_";
  protected final String TEXT_110 = ".releaseConnection();" + NL + "" + NL + "\t\t\t\t\tif (method_";
  protected final String TEXT_111 = ".getResponseHeader(\"location\").getValue().substring(0, 4).equals(\"http\")) {" + NL + "\t\t\t\t\t\tmethod_";
  protected final String TEXT_112 = " = new org.apache.commons.httpclient.methods.";
  protected final String TEXT_113 = "Post";
  protected final String TEXT_114 = "Get";
  protected final String TEXT_115 = "Method(method_";
  protected final String TEXT_116 = ".getResponseHeader(\"location\").getValue());" + NL + "\t\t\t\t\t} else {" + NL + "\t\t\t\t\t\tmethod_";
  protected final String TEXT_117 = " = new org.apache.commons.httpclient.methods.";
  protected final String TEXT_118 = "Post";
  protected final String TEXT_119 = "Get";
  protected final String TEXT_120 = "Method(\"http://\" + method_";
  protected final String TEXT_121 = ".getURI().getHost() + method_";
  protected final String TEXT_122 = ".getResponseHeader(\"location\").getValue());" + NL + "\t\t\t\t\t\t//method_";
  protected final String TEXT_123 = ".setURI(new org.apache.commons.httpclient.URI(\"/\" + method_";
  protected final String TEXT_124 = ".getResponseHeader(\"location\").getValue(), false));" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\tSystem.out.println(\"Redirect to: \" + method_";
  protected final String TEXT_125 = ".getURI());" + NL + "\t\t\t\t\tfinalURL_";
  protected final String TEXT_126 = " = method_";
  protected final String TEXT_127 = ".getURI().toString();" + NL + "\t\t\t\t} else if (status_";
  protected final String TEXT_128 = " == org.apache.commons.httpclient.HttpStatus.SC_OK) {" + NL + "\t\t\t\t\tredirect_";
  protected final String TEXT_129 = " = false;" + NL + "\t\t\t\t} else {" + NL + "\t\t\t\t\tthrow new Exception(\"Method failed: \" + method_";
  protected final String TEXT_130 = ".getStatusLine());" + NL + "\t\t\t\t}" + NL + "\t\t\t}" + NL + "\t\t";
  protected final String TEXT_131 = NL + "\t\t\tstatus_";
  protected final String TEXT_132 = "  = client_";
  protected final String TEXT_133 = ".executeMethod(method_";
  protected final String TEXT_134 = ");" + NL + "\t\t";
  protected final String TEXT_135 = NL + "\t\t\torg.apache.commons.httpclient.Cookie[] cookies_";
  protected final String TEXT_136 = " = client_";
  protected final String TEXT_137 = ".getState().getCookies();" + NL + "\t\t\tjava.io.FileOutputStream fos_";
  protected final String TEXT_138 = " = new java.io.FileOutputStream(new java.io.File(";
  protected final String TEXT_139 = "));" + NL + "\t\t\tjava.io.ObjectOutputStream os_";
  protected final String TEXT_140 = " = new java.io.ObjectOutputStream(fos_";
  protected final String TEXT_141 = ");" + NL + "" + NL + "\t\t\tfor (org.apache.commons.httpclient.Cookie c : cookies_";
  protected final String TEXT_142 = ") {" + NL + "\t\t\t\tos_";
  protected final String TEXT_143 = ".writeObject(c);" + NL + "\t\t\t}" + NL + "\t\t\tos_";
  protected final String TEXT_144 = ".close();" + NL + "\t\t\tfos_";
  protected final String TEXT_145 = ".close();" + NL + "\t\t";
  protected final String TEXT_146 = NL + "\t\t\tif (status_";
  protected final String TEXT_147 = " != org.apache.commons.httpclient.HttpStatus.SC_OK) {      " + NL + "\t\t\t\tthrow new Exception(\"Method failed: \" + method_";
  protected final String TEXT_148 = ".getStatusLine());" + NL + "\t\t\t}" + NL + "\t\t";
  protected final String TEXT_149 = NL + "\t} catch(Exception e) {" + NL + "\t\t";
  protected final String TEXT_150 = NL + "\t\t\tthrow(e);" + NL + "\t\t";
  protected final String TEXT_151 = "   " + NL + "\t\t\tSystem.err.println(\"There isContinue_";
  protected final String TEXT_152 = " an exception on: \" + ";
  protected final String TEXT_153 = ");" + NL + "\t\t\te.printStackTrace();" + NL + "\t\t\tSystem.out.println(\"\\r\\n\");" + NL + "\t\t\tisContinue_";
  protected final String TEXT_154 = " = false;" + NL + "\t\t";
  protected final String TEXT_155 = NL + "\t}" + NL + "" + NL + "\tif (isContinue_";
  protected final String TEXT_156 = ") {    " + NL + "\t\t";
  protected final String TEXT_157 = NL + "\t\t\tSystem.out.println(\"Status Line: \" + method_";
  protected final String TEXT_158 = ".getStatusLine());  " + NL + "\t\t\tSystem.out.println(\"*** Response Header ***\");  " + NL + "\t\t\torg.apache.commons.httpclient.Header[] responseHeaders_";
  protected final String TEXT_159 = " = method_";
  protected final String TEXT_160 = ".getResponseHeaders();  " + NL + "" + NL + "\t\t\tfor (int i = 0; i < responseHeaders_";
  protected final String TEXT_161 = ".length; i++) {" + NL + "\t\t\t\tSystem.out.print(responseHeaders_";
  protected final String TEXT_162 = "[i]);" + NL + "\t\t\t}" + NL + "\t\t";
  protected final String TEXT_163 = NL + "\t\t\tretIS_";
  protected final String TEXT_164 = " = method_";
  protected final String TEXT_165 = ".getResponseBodyAsStream();" + NL + "\t\t";
  protected final String TEXT_166 = NL + "\t\t\tjava.io.InputStream in_";
  protected final String TEXT_167 = " = method_";
  protected final String TEXT_168 = ".getResponseBodyAsStream();" + NL + "\t\t\tString sDir_";
  protected final String TEXT_169 = " = (";
  protected final String TEXT_170 = ").trim();" + NL + "\t\t\tString fileName_";
  protected final String TEXT_171 = " = (";
  protected final String TEXT_172 = ").trim();    " + NL + "\t\t\t//open directory" + NL + "\t\t\tjava.net.URL url_";
  protected final String TEXT_173 = " = new java.net.URL(finalURL_";
  protected final String TEXT_174 = ");" + NL + "\t\t\tString sURIPath_";
  protected final String TEXT_175 = " = \"\";" + NL + "\t\t\tint iLastSlashIndex_";
  protected final String TEXT_176 = " = 0;" + NL + "\t\t\tsURIPath_";
  protected final String TEXT_177 = " = url_";
  protected final String TEXT_178 = ".getFile();" + NL + "\t\t\tiLastSlashIndex_";
  protected final String TEXT_179 = " = sURIPath_";
  protected final String TEXT_180 = ".lastIndexOf(\"/\");" + NL + "" + NL + "\t\t\t";
  protected final String TEXT_181 = NL + "\t\t\t\tsDir_";
  protected final String TEXT_182 = " = sDir_";
  protected final String TEXT_183 = ".concat(iLastSlashIndex_";
  protected final String TEXT_184 = " > 0 ? sURIPath_";
  protected final String TEXT_185 = ".substring(0, iLastSlashIndex_";
  protected final String TEXT_186 = ") : \"\");" + NL + "\t\t\t";
  protected final String TEXT_187 = NL + NL + "\t\t\t// if not input file name, get the name from URI" + NL + "\t\t\tif (\"\".equals(fileName_";
  protected final String TEXT_188 = ")) {      " + NL + "\t\t\t\tif (iLastSlashIndex_";
  protected final String TEXT_189 = " > 0 && (!sURIPath_";
  protected final String TEXT_190 = ".endsWith(\"/\"))) {" + NL + "\t\t\t\t\tfileName_";
  protected final String TEXT_191 = " = sURIPath_";
  protected final String TEXT_192 = ".substring(iLastSlashIndex_";
  protected final String TEXT_193 = " + 1);" + NL + "\t\t\t\t} else {" + NL + "\t\t\t\t\tfileName_";
  protected final String TEXT_194 = " = \"defaultfilename.txt\";" + NL + "\t\t\t\t}" + NL + "\t\t\t}" + NL + "\t\t\tjava.io.File dir_";
  protected final String TEXT_195 = " = new java.io.File(sDir_";
  protected final String TEXT_196 = ");" + NL + "" + NL + "\t\t\t// pretreatment" + NL + "\t\t\ttry {" + NL + "\t\t\t\tjava.io.File test_file_";
  protected final String TEXT_197 = " = new java.io.File(dir_";
  protected final String TEXT_198 = ", fileName_";
  protected final String TEXT_199 = ");" + NL + "\t\t\t\ttest_file_";
  protected final String TEXT_200 = ".getParentFile().mkdirs();" + NL + "" + NL + "\t\t\t\tif (test_file_";
  protected final String TEXT_201 = ".createNewFile()) {" + NL + "\t\t\t\t\ttest_file_";
  protected final String TEXT_202 = ".delete();" + NL + "\t\t\t\t}" + NL + "\t\t\t} catch(Exception e) {" + NL + "\t\t\t\tfileName_";
  protected final String TEXT_203 = " = \"defaultfilename.txt\";" + NL + "\t\t\t}" + NL + "\t\t\tjava.io.File file_";
  protected final String TEXT_204 = " = new java.io.File(dir_";
  protected final String TEXT_205 = ", fileName_";
  protected final String TEXT_206 = ");" + NL + "\t\t\tfile_";
  protected final String TEXT_207 = ".getParentFile().mkdirs();    " + NL + "\t\t\tjava.io.FileOutputStream out_";
  protected final String TEXT_208 = " = new java.io.FileOutputStream(file_";
  protected final String TEXT_209 = ");" + NL + "\t\t\tbyte[] buffer_";
  protected final String TEXT_210 = " = new byte[1024];" + NL + "\t\t\tint count_";
  protected final String TEXT_211 = " = 0;" + NL + "" + NL + "\t\t\twhile ((count_";
  protected final String TEXT_212 = " = in_";
  protected final String TEXT_213 = ".read(buffer_";
  protected final String TEXT_214 = ")) > 0) {" + NL + "\t\t\t\tout_";
  protected final String TEXT_215 = ".write(buffer_";
  protected final String TEXT_216 = ", 0, count_";
  protected final String TEXT_217 = ");" + NL + "\t\t\t}" + NL + "\t\t\t// close opened object" + NL + "\t\t\tin_";
  protected final String TEXT_218 = ".close();   " + NL + "\t\t\tout_";
  protected final String TEXT_219 = ".close(); " + NL + "\t\t\tmethod_";
  protected final String TEXT_220 = ".releaseConnection();" + NL + "\t\t";
  protected final String TEXT_221 = "    " + NL + "\t} // B_01";
  protected final String TEXT_222 = NL + "\t\tjava.util.Properties props = System.getProperties();" + NL + "\t\tprops.put(\"socksProxyPort\", ";
  protected final String TEXT_223 = ");" + NL + "\t\tprops.put(\"socksProxyHost\", ";
  protected final String TEXT_224 = ");" + NL + "\t\tprops.put(\"java.net.socks.username\", ";
  protected final String TEXT_225 = ");" + NL + "\t\tprops.put(\"java.net.socks.password\", ";
  protected final String TEXT_226 = ");        " + NL + "\t";
  protected final String TEXT_227 = NL + "\t//open url stream" + NL + "\tjava.net.URL url_";
  protected final String TEXT_228 = " = new java.net.URL(";
  protected final String TEXT_229 = ");    " + NL + "\tjava.net.URLConnection conn_";
  protected final String TEXT_230 = " = url_";
  protected final String TEXT_231 = ".openConnection();" + NL + "" + NL + "\t";
  protected final String TEXT_232 = NL + "\t\tretIS_";
  protected final String TEXT_233 = " = conn_";
  protected final String TEXT_234 = ".getInputStream();" + NL + "\t";
  protected final String TEXT_235 = NL + "\t\tjava.io.DataInputStream in_";
  protected final String TEXT_236 = " = null;" + NL + "\t\tString sDir_";
  protected final String TEXT_237 = " = (";
  protected final String TEXT_238 = ").trim();" + NL + "\t\tString fileName_";
  protected final String TEXT_239 = " = (";
  protected final String TEXT_240 = ").trim();" + NL + "" + NL + "\t\tString sURIPath_";
  protected final String TEXT_241 = " = \"\";" + NL + "\t\tint iLastSlashIndex_";
  protected final String TEXT_242 = " = 0;" + NL + "\t\tsURIPath_";
  protected final String TEXT_243 = " = url_";
  protected final String TEXT_244 = ".getFile();" + NL + "\t\tiLastSlashIndex_";
  protected final String TEXT_245 = " = sURIPath_";
  protected final String TEXT_246 = ".lastIndexOf(\"/\");" + NL + "" + NL + "\t\t";
  protected final String TEXT_247 = NL + "\t\t\tsDir_";
  protected final String TEXT_248 = " = sDir_";
  protected final String TEXT_249 = ".concat(iLastSlashIndex_";
  protected final String TEXT_250 = " > 0 ? sURIPath_";
  protected final String TEXT_251 = ".substring(0, iLastSlashIndex_";
  protected final String TEXT_252 = ") : \"\");" + NL + "\t\t";
  protected final String TEXT_253 = NL + NL + "\t\t//if not input file name, get the name from URI" + NL + "\t\tif (\"\".equals(fileName_";
  protected final String TEXT_254 = ")) {      " + NL + "\t\t\tif (iLastSlashIndex_";
  protected final String TEXT_255 = " > 0 && (!sURIPath_";
  protected final String TEXT_256 = ".endsWith(\"/\"))) {" + NL + "\t\t\t\tfileName_";
  protected final String TEXT_257 = " = sURIPath_";
  protected final String TEXT_258 = ".substring(iLastSlashIndex_";
  protected final String TEXT_259 = " + 1);" + NL + "\t\t\t} else {" + NL + "\t\t\t\tfileName_";
  protected final String TEXT_260 = " = \"defaultfilename.txt\";" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t\tjava.io.File dir_";
  protected final String TEXT_261 = " = new java.io.File(sDir_";
  protected final String TEXT_262 = ");" + NL + "" + NL + "\t\ttry {" + NL + "\t\t\tjava.io.File testfile_";
  protected final String TEXT_263 = " = new java.io.File(dir_";
  protected final String TEXT_264 = ", fileName_";
  protected final String TEXT_265 = ");" + NL + "\t\t\ttestfile_";
  protected final String TEXT_266 = ".getParentFile().mkdirs();" + NL + "" + NL + "\t\t\tif (testfile_";
  protected final String TEXT_267 = ".createNewFile()) {" + NL + "\t\t\t\ttestfile_";
  protected final String TEXT_268 = ".delete();" + NL + "\t\t\t}" + NL + "\t\t} catch(Exception e) {" + NL + "\t\t\tfileName_";
  protected final String TEXT_269 = " = \"defaultfilename.txt\";" + NL + "\t\t}" + NL + "" + NL + "\t\t// copy file" + NL + "\t\ttry {" + NL + "\t\t\tin_";
  protected final String TEXT_270 = " = new java.io.DataInputStream(conn_";
  protected final String TEXT_271 = ".getInputStream());" + NL + "\t\t} catch(Exception e) {" + NL + "\t\t\t";
  protected final String TEXT_272 = "    " + NL + "\t\t\t\tthrow(e);" + NL + "\t\t\t";
  protected final String TEXT_273 = NL + "\t\t\t\tSystem.err.println(\"There is an exception on: \" + ";
  protected final String TEXT_274 = ");" + NL + "\t\t\t\te.printStackTrace();" + NL + "\t\t\t\tSystem.out.println(\"\\r\\n\");" + NL + "\t\t\t\tin_";
  protected final String TEXT_275 = " = null;      " + NL + "\t\t\t";
  protected final String TEXT_276 = NL + "\t\t}  " + NL + "\t\tfinal java.io.DataOutputStream out_";
  protected final String TEXT_277 = "= new java.io.DataOutputStream(new java.io.FileOutputStream(new java.io.File(dir_";
  protected final String TEXT_278 = ", fileName_";
  protected final String TEXT_279 = ")));" + NL + "" + NL + "\t\tif (in_";
  protected final String TEXT_280 = " != null) {" + NL + "\t\t\tbyte[] buffer_";
  protected final String TEXT_281 = " = new byte[1024];" + NL + "\t\t\tint count_";
  protected final String TEXT_282 = " = 0;" + NL + "" + NL + "\t\t\twhile ((count_";
  protected final String TEXT_283 = " = in_";
  protected final String TEXT_284 = ".read(buffer_";
  protected final String TEXT_285 = ")) > 0) {" + NL + "\t\t\t\tout_";
  protected final String TEXT_286 = ".write(buffer_";
  protected final String TEXT_287 = ", 0, count_";
  protected final String TEXT_288 = ");" + NL + "\t\t\t}    " + NL + "\t\t\tin_";
  protected final String TEXT_289 = ".close();" + NL + "\t\t}    " + NL + "\t\tout_";
  protected final String TEXT_290 = ".close();" + NL + "\t";
  protected final String TEXT_291 = "     " + NL + "\tString srcurl_";
  protected final String TEXT_292 = " = ";
  protected final String TEXT_293 = ";" + NL + "\tString fileName_";
  protected final String TEXT_294 = " = ";
  protected final String TEXT_295 = ";" + NL + "\tString username_";
  protected final String TEXT_296 = " = ";
  protected final String TEXT_297 = ";" + NL + "\tString password_";
  protected final String TEXT_298 = " = ";
  protected final String TEXT_299 = ";" + NL + "" + NL + "\tif (fileName_";
  protected final String TEXT_300 = ".compareTo(\"\") == 0) {" + NL + "\t\tfileName_";
  protected final String TEXT_301 = " = srcurl_";
  protected final String TEXT_302 = ".substring(srcurl_";
  protected final String TEXT_303 = ".lastIndexOf(\"/\"));" + NL + "\t}" + NL + "\t" + NL + "\tif (username_";
  protected final String TEXT_304 = ".compareTo(\"\") == 0) {" + NL + "\t\tusername_";
  protected final String TEXT_305 = " = null;" + NL + "\t}" + NL + "\t" + NL + "\tif (password_";
  protected final String TEXT_306 = ".compareTo(\"\") == 0) {" + NL + "\t\tpassword_";
  protected final String TEXT_307 = " = null;" + NL + "\t}" + NL + "\t" + NL + "\ttry {        " + NL + "\t\tjcifs.smb.NtlmPasswordAuthentication auth_";
  protected final String TEXT_308 = " = new jcifs.smb.NtlmPasswordAuthentication(";
  protected final String TEXT_309 = ", username_";
  protected final String TEXT_310 = ", password_";
  protected final String TEXT_311 = ");" + NL + "\t\tjcifs.smb.SmbFile sf_";
  protected final String TEXT_312 = " = new jcifs.smb.SmbFile(srcurl_";
  protected final String TEXT_313 = ", auth_";
  protected final String TEXT_314 = ");" + NL + "\t" + NL + "\t\t";
  protected final String TEXT_315 = NL + "\t\t\tretIS_";
  protected final String TEXT_316 = " = new jcifs.smb.SmbFileInputStream(sf_";
  protected final String TEXT_317 = ");" + NL + "\t\t";
  protected final String TEXT_318 = NL + "\t\t\tjcifs.smb.SmbFileInputStream in_";
  protected final String TEXT_319 = " = new jcifs.smb.SmbFileInputStream(sf_";
  protected final String TEXT_320 = ");" + NL + "\t\t\tjava.io.File destFile_";
  protected final String TEXT_321 = " = new java.io.File(";
  protected final String TEXT_322 = ", fileName_";
  protected final String TEXT_323 = ");" + NL + "\t\t\tdestFile_";
  protected final String TEXT_324 = ".getParentFile().mkdirs();" + NL + "\t\t\tjava.io.OutputStream out_";
  protected final String TEXT_325 = " = new java.io.FileOutputStream(destFile_";
  protected final String TEXT_326 = ");" + NL + "\t\t\tbyte[] buf_";
  protected final String TEXT_327 = " = new byte[1024];" + NL + "\t\t\tint len_";
  protected final String TEXT_328 = ";" + NL + "\t" + NL + "\t\t\twhile ((len_";
  protected final String TEXT_329 = " = in_";
  protected final String TEXT_330 = ".read(buf_";
  protected final String TEXT_331 = ")) > 0) {" + NL + "\t\t\t\tout_";
  protected final String TEXT_332 = ".write(buf_";
  protected final String TEXT_333 = ", 0, len_";
  protected final String TEXT_334 = ");" + NL + "\t\t\t}" + NL + "\t\t\tin_";
  protected final String TEXT_335 = ".close();" + NL + "\t\t\tout_";
  protected final String TEXT_336 = ".close();" + NL + "\t\t\t} catch (java.io.FileNotFoundException ex) {" + NL + "\t\t\t\tSystem.err.println(ex.getMessage());" + NL + "\t\t";
  protected final String TEXT_337 = " " + NL + "\t} catch (Exception e) {" + NL + "\t\tSystem.err.println(e.getMessage());" + NL + "\t}";
  protected final String TEXT_338 = NL + "\tjava.io.InputStream stream";
  protected final String TEXT_339 = " = (java.io.InputStream)globalMap.get(\"";
  protected final String TEXT_340 = "_INPUT_STREAM\");" + NL + "\tif(stream";
  protected final String TEXT_341 = "!=null){" + NL + "\t\tstream";
  protected final String TEXT_342 = ".close();" + NL + "\t}";
  protected final String TEXT_343 = NL + "globalMap.put(\"";
  protected final String TEXT_344 = "_INPUT_STREAM\", retIS_";
  protected final String TEXT_345 = ");";
  protected final String TEXT_346 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String protocol = ElementParameterParser.getValue(node, "__PROTO__");
String directory = ElementParameterParser.getValue(node, "__DIRECTORY__");
String filename = ElementParameterParser.getValue(node, "__FILENAME__");
boolean bMakeDirs = "true".equals(ElementParameterParser.getValue(node, "__MAKEDIRS__"));
boolean bUseCache = "true".equals(ElementParameterParser.getValue(node, "__USE_CACHE__"));
boolean bDieOnError = "true".equals(ElementParameterParser.getValue(node, "__DIE_ON_ERROR__"));

    stringBuffer.append(TEXT_1);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_2);
    
if ("http".equals(protocol) || "https".equals(protocol)) {
	String uri = ElementParameterParser.getValue(node, "__URI__");
	String username = ElementParameterParser.getValue(node, "__AUTH_USERNAME__");
	String password = ElementParameterParser.getValue(node, "__AUTH_PASSWORD__");
	String timeout = ElementParameterParser.getValue(node, "__TIMEOUT__");
	String cookieDir = ElementParameterParser.getValue(node, "__COOKIE_DIR__");
	String cookiePolicy = ElementParameterParser.getValue(node,"__COOKIE_POLICY__");
	boolean singleCookie = "true".equals(ElementParameterParser.getValue(node, "__SINGLE_COOKIE__"));
	String proxyHost = ElementParameterParser.getValue(node, "__PROXY_HOST__");
	String proxyPort = ElementParameterParser.getValue(node, "__PROXY_PORT__");
	String proxyUser = ElementParameterParser.getValue(node, "__PROXY_USERNAME__");
	String proxyPassword = ElementParameterParser.getValue(node, "__PROXY_PASSWORD__");
	String proxyDomain = ElementParameterParser.getValue(node, "__PROXY_DOMAIN__");
	boolean post = "true".equals(ElementParameterParser.getValue(node, "__POST__"));
	boolean printResponse = "true".equals(ElementParameterParser.getValue(node, "__PRINT__"));
	boolean needAuth = "true".equals(ElementParameterParser.getValue(node, "__NEED_AUTH__"));
	boolean saveCookie = "true".equals(ElementParameterParser.getValue(node, "__SAVE_COOKIE__"));
	boolean readCookie = "true".equals(ElementParameterParser.getValue(node, "__READ_COOKIE__"));
	boolean redirect = "true".equals(ElementParameterParser.getValue(node, "__REDIRECT__"));
	boolean useProxy = "true".equals(ElementParameterParser.getValue(node, "__USE_PROXY__"));
	boolean useProxyNTLM = "true".equals(ElementParameterParser.getValue(node, "__PROXY_NTLM__"));
	boolean addHeader = "true".equals(ElementParameterParser.getValue(node, "__ADD_HEADER__"));

	if ("https".equals(protocol)) {
	
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(uri );
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    }
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(timeout );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cookiePolicy );
    stringBuffer.append(TEXT_12);
    if(singleCookie){
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    }
    stringBuffer.append(TEXT_15);
    if (readCookie) {
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cookieDir);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    
	}

	if (useProxy) {
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(proxyHost );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(proxyPort);
    stringBuffer.append(TEXT_43);
    if (useProxyNTLM) {
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(proxyHost );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(proxyPort);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(proxyUser );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(proxyPassword );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(proxyHost );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(proxyDomain );
    stringBuffer.append(TEXT_51);
    } else {
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_53);
    stringBuffer.append(proxyHost );
    stringBuffer.append(TEXT_54);
    stringBuffer.append(proxyPort);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(proxyUser );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(proxyPassword );
    stringBuffer.append(TEXT_57);
    }
	}

	if (needAuth) {
		if ((!useProxy) && useProxyNTLM && (!"".equals(proxyDomain))) {
		
    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_59);
    stringBuffer.append(username );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(password );
    stringBuffer.append(TEXT_61);
    stringBuffer.append(uri);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(proxyDomain);
    stringBuffer.append(TEXT_63);
    } else {
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_65);
    stringBuffer.append(username );
    stringBuffer.append(TEXT_66);
    stringBuffer.append(password );
    stringBuffer.append(TEXT_67);
    }
	}

	if (post) {
	
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(uri );
    stringBuffer.append(TEXT_70);
    
		List<Map<String, String>> commonParams = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__COMMON_PARAMS__");	
		List<String> varPartList = new ArrayList<String>();	//common string parameter part	
		int i = 0;

		for (Map<String, String> param : commonParams) {
			i++;
			String name = param.get("COMMON_PARAMS_NAME");
			String value = param.get("COMMON_PARAMS_VALUE");
			varPartList.add("common_" + i + "_" + cid);
			
    stringBuffer.append(TEXT_71);
    stringBuffer.append(i);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(name );
    stringBuffer.append(TEXT_74);
    stringBuffer.append(value );
    stringBuffer.append(TEXT_75);
    
		}

		if ("true".equals(ElementParameterParser.getValue(node,"__UPLOAD__"))) {
			List<Map<String, String>> fileParams = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__FILE_PARAMS__");
			int j = 0;

			for (Map<String, String> param : fileParams) {
				j++;
				String name = param.get("FILE_PARAMS_NAME");
				String value = param.get("FILE_PARAMS_VALUE");
				varPartList.add("file_" + j + "_" + cid);
				
    stringBuffer.append(TEXT_76);
    stringBuffer.append(j);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(name );
    stringBuffer.append(TEXT_79);
    stringBuffer.append(value );
    stringBuffer.append(TEXT_80);
    
			}
		}
		
    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_82);
    for(String var: varPartList){
    stringBuffer.append(var );
    stringBuffer.append(TEXT_83);
    }
    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_87);
    } else {
    stringBuffer.append(TEXT_88);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_89);
    stringBuffer.append(uri );
    stringBuffer.append(TEXT_90);
    }

	if (addHeader) {
		List<Map<String, String>> headers = (List<Map<String,String>>)ElementParameterParser.getObjectValue(node, "__HEADERS__");
		
		for (Map<String, String> header : headers) {
		
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(header.get("HEADER_NAME"));
    stringBuffer.append(TEXT_93);
    stringBuffer.append(header.get("HEADER_VALUE"));
    stringBuffer.append(TEXT_94);
    
		}
	}
	
    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_96);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_97);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_98);
    stringBuffer.append(uri);
    stringBuffer.append(TEXT_99);
    if (redirect) { //Bug13155, add support of redirection
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_101);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_102);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_103);
    stringBuffer.append(cid );
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_111);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_112);
    if(post){
    stringBuffer.append(TEXT_113);
    }else{
    stringBuffer.append(TEXT_114);
    }
    stringBuffer.append(TEXT_115);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_116);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_117);
    if(post){
    stringBuffer.append(TEXT_118);
    }else{
    stringBuffer.append(TEXT_119);
    }
    stringBuffer.append(TEXT_120);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_121);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_122);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_123);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_124);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_125);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_126);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_127);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_128);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_129);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_130);
    } else {
    stringBuffer.append(TEXT_131);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_132);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_133);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_134);
    
		}

		if (saveCookie) {
		
    stringBuffer.append(TEXT_135);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_136);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_137);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_138);
    stringBuffer.append(cookieDir);
    stringBuffer.append(TEXT_139);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_140);
    stringBuffer.append(cid);
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

		if (!redirect) {
		
    stringBuffer.append(TEXT_146);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_147);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_148);
    
		}
		
    stringBuffer.append(TEXT_149);
    if (bDieOnError) {
    stringBuffer.append(TEXT_150);
    } else {
    stringBuffer.append(TEXT_151);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_152);
    stringBuffer.append(uri );
    stringBuffer.append(TEXT_153);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_154);
    }
    stringBuffer.append(TEXT_155);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_156);
    if (printResponse) {
    stringBuffer.append(TEXT_157);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_158);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_159);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_160);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_161);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_162);
    
		}

		if (bUseCache) {
    stringBuffer.append(TEXT_163);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_164);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_165);
    } else {
    stringBuffer.append(TEXT_166);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_167);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_168);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_169);
    stringBuffer.append(directory);
    stringBuffer.append(TEXT_170);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_171);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_172);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_173);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_174);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_175);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_176);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_177);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_178);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_179);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_180);
    if (bMakeDirs) {
    stringBuffer.append(TEXT_181);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_182);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_183);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_184);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_185);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_186);
    }
    stringBuffer.append(TEXT_187);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_188);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_189);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_190);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_191);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_192);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_193);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_194);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_195);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_196);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_197);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_198);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_199);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_200);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_201);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_202);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_203);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_204);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_205);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_206);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_207);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_208);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_209);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_210);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_211);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_212);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_213);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_214);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_215);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_216);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_217);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_218);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_219);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_220);
    }
    stringBuffer.append(TEXT_221);
    
} else if (("ftp").equals(protocol)) {
	String uri = ElementParameterParser.getValue(node, "__URI__");
	boolean useProxy = ("true").equals(ElementParameterParser.getValue(node,"__USE_PROXY__"));
	String proxyHost = ElementParameterParser.getValue(node,"__PROXY_HOST__");
	String proxyPort = ElementParameterParser.getValue(node,"__PROXY_PORT__");
	String proxyUser = ElementParameterParser.getValue(node,"__PROXY_USERNAME__");
	String proxyPassword = ElementParameterParser.getValue(node,"__PROXY_PASSWORD__");

	//The following part support the socks proxy for FTP and SFTP (Socks V4 or V5, they are all OK). 
	//And it can not work with the FTP proxy directly, only support the socks proxy.
	if (useProxy) {
	
    stringBuffer.append(TEXT_222);
    stringBuffer.append(proxyPort );
    stringBuffer.append(TEXT_223);
    stringBuffer.append(proxyHost );
    stringBuffer.append(TEXT_224);
    stringBuffer.append(proxyUser );
    stringBuffer.append(TEXT_225);
    stringBuffer.append(proxyPassword );
    stringBuffer.append(TEXT_226);
     }
    stringBuffer.append(TEXT_227);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_228);
    stringBuffer.append(uri );
    stringBuffer.append(TEXT_229);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_230);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_231);
    if (bUseCache) {
    stringBuffer.append(TEXT_232);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_233);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_234);
    } else {
    stringBuffer.append(TEXT_235);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_236);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_237);
    stringBuffer.append(directory);
    stringBuffer.append(TEXT_238);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_239);
    stringBuffer.append(filename);
    stringBuffer.append(TEXT_240);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_241);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_242);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_243);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_244);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_245);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_246);
    if (bMakeDirs) {
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
    }
    stringBuffer.append(TEXT_253);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_254);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_255);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_256);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_257);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_258);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_259);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_260);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_261);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_262);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_263);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_264);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_265);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_266);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_267);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_268);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_269);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_270);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_271);
     if (bDieOnError) {
    stringBuffer.append(TEXT_272);
    } else {
    stringBuffer.append(TEXT_273);
    stringBuffer.append(uri );
    stringBuffer.append(TEXT_274);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_275);
    }
    stringBuffer.append(TEXT_276);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_277);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_278);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_279);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_280);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_281);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_282);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_283);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_284);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_285);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_286);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_287);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_288);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_289);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_290);
    
	}
} else if ("smb".equals(protocol)) {
	String srcurl = ElementParameterParser.getValue(node, "__SMB_URI__"); 
	String domain = ElementParameterParser.getValue(node,"__SMB_DOMAIN__");
	String username = ElementParameterParser.getValue(node,"__SMB_USERNAME__");
	String password = ElementParameterParser.getValue(node,"__SMB_PASSWORD__");
	
    stringBuffer.append(TEXT_291);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_292);
    stringBuffer.append(srcurl );
    stringBuffer.append(TEXT_293);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_294);
    stringBuffer.append(filename );
    stringBuffer.append(TEXT_295);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_296);
    stringBuffer.append(username );
    stringBuffer.append(TEXT_297);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_298);
    stringBuffer.append(password );
    stringBuffer.append(TEXT_299);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_300);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_301);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_302);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_303);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_304);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_305);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_306);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_307);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_308);
    stringBuffer.append(domain );
    stringBuffer.append(TEXT_309);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_310);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_311);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_312);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_313);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_314);
    if (bUseCache) {
    stringBuffer.append(TEXT_315);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_316);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_317);
    } else {
    stringBuffer.append(TEXT_318);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_319);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_320);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_321);
    stringBuffer.append(directory );
    stringBuffer.append(TEXT_322);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_323);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_324);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_325);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_326);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_327);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_328);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_329);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_330);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_331);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_332);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_333);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_334);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_335);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_336);
    }
    stringBuffer.append(TEXT_337);
    }
    if (bUseCache) {
    stringBuffer.append(TEXT_338);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_339);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_340);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_341);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_342);
    }
    stringBuffer.append(TEXT_343);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_344);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_345);
    stringBuffer.append(TEXT_346);
    return stringBuffer.toString();
  }
}
