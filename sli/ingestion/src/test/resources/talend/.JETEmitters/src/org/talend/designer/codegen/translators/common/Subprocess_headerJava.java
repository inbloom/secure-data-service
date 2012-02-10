package org.talend.designer.codegen.translators.common;

import org.talend.designer.codegen.config.NodesSubTree;
import org.talend.core.model.process.INode;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.metadata.types.JavaType;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IHashableInputConnections;
import org.talend.core.model.process.IHashConfiguration;
import org.talend.core.model.process.IHashableColumn;
import org.talend.core.model.utils.NodeUtil;

public class Subprocess_headerJava
{
  protected static String nl;
  public static synchronized Subprocess_headerJava create(String lineSeparator)
  {
    nl = lineSeparator;
    Subprocess_headerJava result = new Subprocess_headerJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "public static class ";
  protected final String TEXT_3 = "Struct";
  protected final String TEXT_4 = " implements routines.system.";
  protected final String TEXT_5 = " {" + NL + "    final static byte[] commonByteArrayLock = new byte[0];" + NL + "    static byte[] commonByteArray = new byte[0];";
  protected final String TEXT_6 = NL + "\tprivate static final int DEFAULT_HASHCODE = 1;" + NL + "    private static final int PRIME = 31;" + NL + "    private int hashCode = DEFAULT_HASHCODE;" + NL + "    public boolean hashCodeDirty = true;" + NL + "    " + NL + "    ";
  protected final String TEXT_7 = NL + "    " + NL + "\t";
  protected final String TEXT_8 = NL + "\t\t\t    public ";
  protected final String TEXT_9 = " ";
  protected final String TEXT_10 = " = ' '";
  protected final String TEXT_11 = ";" + NL + "" + NL + "\t\t\t\tpublic ";
  protected final String TEXT_12 = " get";
  protected final String TEXT_13 = " () {" + NL + "\t\t\t\t\treturn this.";
  protected final String TEXT_14 = ";" + NL + "\t\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_15 = NL + "\t\t\t\tpublic java.util.Map<String,Object> documentLookupResult =  new java.util.HashMap<String,Object>();" + NL + "\t\t\t\t";
  protected final String TEXT_16 = NL + "    ";
  protected final String TEXT_17 = NL + "    " + NL + "\t@Override" + NL + "\tpublic int hashCode() {" + NL + "\t\tif (this.hashCodeDirty) {" + NL + "\t\t\tfinal int prime = PRIME;" + NL + "\t\t\tint result = DEFAULT_HASHCODE;" + NL + "\t";
  protected final String TEXT_18 = NL + "\t\t        \t\t\tresult = prime * result + (this.";
  protected final String TEXT_19 = " ? 1231 : 1237);" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_20 = NL + "\t\t\t\t\t\t\tresult = prime * result + (int) this.";
  protected final String TEXT_21 = ";" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_22 = NL + "\t\t\t\t\t\tresult = prime * result + java.util.Arrays.hashCode(this.";
  protected final String TEXT_23 = ");" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_24 = NL + "\t\t\t\t\t\tresult = prime * result + ((this.";
  protected final String TEXT_25 = " == null) ? 0 : this.";
  protected final String TEXT_26 = ".hashCode());" + NL + "\t\t\t\t\t";
  protected final String TEXT_27 = NL + "    \t\tthis.hashCode = result;" + NL + "    \t\tthis.hashCodeDirty = false;\t\t" + NL + "\t\t}" + NL + "\t\treturn this.hashCode;" + NL + "\t}" + NL + "\t" + NL + "\t@Override" + NL + "\tpublic boolean equals(Object obj) {" + NL + "\t\tif (this == obj) return true;" + NL + "\t\tif (obj == null) return false;" + NL + "\t\tif (getClass() != obj.getClass()) return false;" + NL + "\t\tfinal ";
  protected final String TEXT_28 = "Struct";
  protected final String TEXT_29 = " other = (";
  protected final String TEXT_30 = "Struct";
  protected final String TEXT_31 = ") obj;" + NL + "\t\t";
  protected final String TEXT_32 = NL + "\t\t\t\t\t\tif (this.";
  protected final String TEXT_33 = " != other.";
  protected final String TEXT_34 = ") " + NL + "\t\t\t\t\t\t\treturn false;" + NL + "\t\t\t\t\t";
  protected final String TEXT_35 = NL + "\t\t\t\t\t\tif(!java.util.Arrays.equals(this.";
  protected final String TEXT_36 = ", other.";
  protected final String TEXT_37 = ")) {" + NL + "\t\t\t\t\t\t\treturn false;" + NL + "\t\t\t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_38 = NL + "\t\t\t\t\t\tif (this.";
  protected final String TEXT_39 = " == null) {" + NL + "\t\t\t\t\t\t\tif (other.";
  protected final String TEXT_40 = " != null) " + NL + "\t\t\t\t\t\t\t\treturn false;" + NL + "\t\t\t\t\t\t} else if (!this.";
  protected final String TEXT_41 = ".equals(other.";
  protected final String TEXT_42 = ")) " + NL + "\t\t\t\t\t\t\treturn false;" + NL + "\t\t\t\t\t";
  protected final String TEXT_43 = NL + "    \t" + NL + "\t\treturn true;" + NL + "    }" + NL + "" + NL + "\tpublic void copyDataTo(";
  protected final String TEXT_44 = "Struct";
  protected final String TEXT_45 = " other) {" + NL + "" + NL + "\t\t";
  protected final String TEXT_46 = "other.";
  protected final String TEXT_47 = " = this.";
  protected final String TEXT_48 = ";" + NL + "\t            ";
  protected final String TEXT_49 = NL + "\t}" + NL + "" + NL + "\tpublic void copyKeysDataTo(";
  protected final String TEXT_50 = "Struct";
  protected final String TEXT_51 = " other) {" + NL + "" + NL + "\t\t";
  protected final String TEXT_52 = "other.";
  protected final String TEXT_53 = " = this.";
  protected final String TEXT_54 = ";" + NL + "\t            \t";
  protected final String TEXT_55 = NL + "\t}" + NL + "    " + NL + "    ";
  protected final String TEXT_56 = NL;
  protected final String TEXT_57 = NL + NL + "\tprivate String readString(ObjectInputStream dis) throws IOException{" + NL + "\t\tString strReturn = null;" + NL + "\t\tint length = 0;" + NL + "        length = dis.readInt();" + NL + "\t\tif (length == -1) {" + NL + "\t\t\tstrReturn = null;" + NL + "\t\t} else {" + NL + "\t\t\tif(length > commonByteArray.length) {" + NL + "\t\t\t\tif(length < 1024 && commonByteArray.length == 0) {" + NL + "   \t\t\t\t\tcommonByteArray = new byte[1024];" + NL + "\t\t\t\t} else {" + NL + "   \t\t\t\t\tcommonByteArray = new byte[2 * length];" + NL + "   \t\t\t\t}" + NL + "\t\t\t}" + NL + "\t\t\tdis.readFully(commonByteArray, 0, length);" + NL + "\t\t\tstrReturn = new String(commonByteArray, 0, length, utf8Charset);" + NL + "\t\t}" + NL + "\t\treturn strReturn;" + NL + "\t}" + NL + "\t" + NL + "    private void writeString(String str, ObjectOutputStream dos) throws IOException{" + NL + "\t\tif(str == null) {" + NL + "            dos.writeInt(-1);" + NL + "\t\t} else {" + NL + "            byte[] byteArray = str.getBytes(utf8Charset);" + NL + "\t    \tdos.writeInt(byteArray.length);" + NL + "\t\t\tdos.write(byteArray);" + NL + "    \t}" + NL + "    }";
  protected final String TEXT_58 = NL + NL + "\tprivate String readString(DataInputStream dis, ObjectInputStream ois) throws IOException{" + NL + "\t\tString strReturn = null;" + NL + "\t\tint length = 0;" + NL + "        length = dis.readInt();" + NL + "\t\tif (length == -1) {" + NL + "\t\t\tstrReturn = null;" + NL + "\t\t} else {" + NL + "\t\t\tbyte[] byteArray = new byte[length];" + NL + "\t\t\tdis.read(byteArray);" + NL + "\t\t\tstrReturn = new String(byteArray, utf8Charset);" + NL + "\t\t}" + NL + "\t\treturn strReturn;" + NL + "\t}" + NL + "\t" + NL + "\tprivate void writeString(String str, DataOutputStream dos, ObjectOutputStream oos) throws IOException{" + NL + "\t\tif(str == null) {" + NL + "            dos.writeInt(-1);" + NL + "\t\t} else {" + NL + "            byte[] byteArray = str.getBytes(utf8Charset);" + NL + "\t    \tdos.writeInt(byteArray.length);" + NL + "\t\t\tdos.write(byteArray);" + NL + "    \t}" + NL + "\t}";
  protected final String TEXT_59 = NL + NL + "\tprivate java.util.Date readDate(ObjectInputStream dis) throws IOException{" + NL + "\t\tjava.util.Date dateReturn = null;" + NL + "        int length = 0;" + NL + "        length = dis.readByte();" + NL + "\t\tif (length == -1) {" + NL + "\t\t\tdateReturn = null;" + NL + "\t\t} else {" + NL + "\t    \tdateReturn = new Date(dis.readLong());" + NL + "\t\t}" + NL + "\t\treturn dateReturn;" + NL + "\t}" + NL + "    " + NL + "    private void writeDate(java.util.Date date1, ObjectOutputStream dos) throws IOException{" + NL + "\t\tif(date1 == null) {" + NL + "            dos.writeByte(-1);" + NL + "\t\t} else {" + NL + "\t\t\tdos.writeByte(0);" + NL + "\t    \tdos.writeLong(date1.getTime());" + NL + "    \t}" + NL + "    }";
  protected final String TEXT_60 = NL + NL + "\tprivate java.util.Date readDate(DataInputStream dis, ObjectInputStream ois) throws IOException{" + NL + "\t\tjava.util.Date dateReturn = null;" + NL + "\t\tint length = 0;" + NL + "        length = dis.readByte();" + NL + "\t\tif (length == -1) {" + NL + "\t\t\tdateReturn = null;" + NL + "\t\t} else {" + NL + "\t    \tdateReturn = new Date(dis.readLong());" + NL + "\t\t}" + NL + "\t\treturn dateReturn;" + NL + "\t}" + NL + "\t" + NL + "\tprivate void writeDate(java.util.Date date1, DataOutputStream dos, ObjectOutputStream oos) throws IOException{" + NL + "\t\tif(date1 == null) {" + NL + "            dos.writeByte(-1);" + NL + "\t\t} else {" + NL + "\t\t\tdos.writeByte(0);" + NL + "\t    \tdos.writeLong(date1.getTime());" + NL + "    \t}" + NL + "\t}";
  protected final String TEXT_61 = NL + "\tprivate byte[] readByteArray(ObjectInputStream dis) throws IOException{" + NL + "\t\tbyte[] byteArrayReturn;" + NL + "\t\tint length = 0;" + NL + "        length = dis.readInt();" + NL + "\t\tif (length == -1) {" + NL + "\t\t\tbyteArrayReturn = null;" + NL + "\t\t} else {" + NL + "\t\t\tbyte[] byteArray = new byte[length];" + NL + "\t\t\tdis.readFully(byteArray);" + NL + "\t\t\tbyteArrayReturn = byteArray;" + NL + "\t\t}" + NL + "\t\treturn byteArrayReturn;" + NL + "\t}" + NL + "    " + NL + "    private void writeByteArray(byte[] byteArray, ObjectOutputStream dos) throws IOException{" + NL + "\t\tif(byteArray == null) {" + NL + "            dos.writeInt(-1);" + NL + "\t\t} else {" + NL + "\t    \tdos.writeInt(byteArray.length);" + NL + "\t\t\tdos.write(byteArray);" + NL + "    \t}" + NL + "    }";
  protected final String TEXT_62 = NL + "\tprivate byte[] readByteArray(DataInputStream dis, ObjectInputStream ois) throws IOException{" + NL + "\t\tbyte[] byteArrayReturn;" + NL + "        int length = 0;" + NL + "        length = dis.readInt();" + NL + "\t\tif (length == -1) {" + NL + "\t\t\tbyteArrayReturn = null;" + NL + "\t\t} else {" + NL + "\t\t\tbyte[] byteArray = new byte[length];" + NL + "\t\t\tdis.read(byteArray);" + NL + "\t\t\tbyteArrayReturn = byteArray;" + NL + "\t\t}" + NL + "\t\treturn byteArrayReturn;" + NL + "\t}" + NL + "" + NL + "\tprivate void writeByteArray(byte[] byteArray, DataOutputStream dos, ObjectOutputStream oos) throws IOException{\t" + NL + "\t\tif(byteArray == null) {" + NL + "            dos.writeInt(-1);" + NL + "\t\t} else {" + NL + "\t    \tdos.writeInt(byteArray.length);" + NL + "\t\t\tdos.write(byteArray);" + NL + "    \t}" + NL + "\t}";
  protected final String TEXT_63 = NL + "\tprivate Integer readInteger(ObjectInputStream dis) throws IOException{" + NL + "\t\tInteger intReturn;" + NL + "        int length = 0;" + NL + "        length = dis.readByte();" + NL + "\t\tif (length == -1) {" + NL + "\t\t\tintReturn = null;" + NL + "\t\t} else {" + NL + "\t    \tintReturn = dis.read";
  protected final String TEXT_64 = "();" + NL + "\t\t}" + NL + "\t\treturn intReturn;" + NL + "\t}" + NL + "\t" + NL + "\tprivate void writeInteger(Integer intNum, ObjectOutputStream dos) throws IOException{" + NL + "\t\tif(intNum == null) {" + NL + "            dos.writeByte(-1);" + NL + "\t\t} else {" + NL + "\t\t\tdos.writeByte(0);" + NL + "\t    \tdos.write";
  protected final String TEXT_65 = "(intNum);" + NL + "    \t}" + NL + "\t}";
  protected final String TEXT_66 = NL + "\tprivate Integer readInteger(DataInputStream dis, ObjectInputStream ois) throws IOException{" + NL + "\t\tInteger intReturn;" + NL + "        int length = 0;" + NL + "        length = dis.readByte();" + NL + "\t\tif (length == -1) {" + NL + "\t\t\tintReturn = null;" + NL + "\t\t} else {" + NL + "\t\t\tintReturn = dis.read";
  protected final String TEXT_67 = "();" + NL + "\t\t}" + NL + "\t\treturn intReturn;" + NL + "\t}" + NL + "" + NL + "\tprivate void writeInteger(Integer intNum, DataOutputStream dos, ObjectOutputStream oos) throws IOException{\t" + NL + "\t\tif(intNum == null) {" + NL + "            dos.writeByte(-1);" + NL + "\t\t} else {" + NL + "\t\t\tdos.writeByte(0);" + NL + "\t    \tdos.write";
  protected final String TEXT_68 = "(intNum);" + NL + "    \t}" + NL + "\t}";
  protected final String TEXT_69 = "\t" + NL + "\t   " + NL + "    public void ";
  protected final String TEXT_70 = "Data(ObjectInputStream dis) {" + NL + "" + NL + "\t\tsynchronized(commonByteArrayLock) {" + NL + "" + NL + "        \ttry {" + NL + "        \t" + NL + "        \t\tint length = 0;" + NL + "\t\t";
  protected final String TEXT_71 = NL + "\t\t\t        this.";
  protected final String TEXT_72 = " = dis.read";
  protected final String TEXT_73 = "();" + NL + "\t\t\t\t\t";
  protected final String TEXT_74 = NL + "\t\t\t\t\tthis.";
  protected final String TEXT_75 = " = readString(dis);" + NL + "\t\t\t\t\t";
  protected final String TEXT_76 = NL + "\t\t\t\t\tthis.";
  protected final String TEXT_77 = " = readDate(dis);" + NL + "\t\t\t\t\t";
  protected final String TEXT_78 = NL + "\t\t\t\t\tthis.";
  protected final String TEXT_79 = " = readByteArray(dis);" + NL + "\t\t\t\t\t";
  protected final String TEXT_80 = NL + "\t\t\t\t\t\tthis.";
  protected final String TEXT_81 = " = (";
  protected final String TEXT_82 = ") dis.readObject();" + NL + "\t\t\t\t\t";
  protected final String TEXT_83 = NL + "\t\t\t\t\t\tthis.";
  protected final String TEXT_84 = " = readInteger(dis);" + NL + "\t\t\t\t\t";
  protected final String TEXT_85 = NL + "\t\t\t            length = dis.readByte();" + NL + "           \t\t\t\tif (length == -1) {" + NL + "           \t    \t\t\tthis.";
  protected final String TEXT_86 = " = null;" + NL + "           \t\t\t\t} else {" + NL + "           \t\t\t    \tthis.";
  protected final String TEXT_87 = " = dis.read";
  protected final String TEXT_88 = "();" + NL + "           \t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_89 = NL + "        \t} catch (IOException e) {" + NL + "\t            throw new RuntimeException(e);";
  protected final String TEXT_90 = NL + "        " + NL + "\t\t";
  protected final String TEXT_91 = NL + "\t\t\t} catch(ClassNotFoundException eCNFE) {" + NL + "\t\t\t\t throw new RuntimeException(eCNFE);" + NL + "\t\t";
  protected final String TEXT_92 = NL + "        " + NL + "        }" + NL + "        " + NL + "\t\t";
  protected final String TEXT_93 = NL + "\t\t\tfinally {}" + NL + "\t\t";
  protected final String TEXT_94 = NL + "        " + NL + "      }" + NL + "        " + NL + "        " + NL + "    }" + NL + "" + NL + "    public void ";
  protected final String TEXT_95 = "Data(ObjectOutputStream dos) {" + NL + "        try {" + NL + "" + NL + "\t\t";
  protected final String TEXT_96 = NL + "\t\t\t\t\t// ";
  protected final String TEXT_97 = NL + "\t\t\t\t";
  protected final String TEXT_98 = NL + "\t\t            \tdos.write";
  protected final String TEXT_99 = "(this.";
  protected final String TEXT_100 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_101 = NL + "\t\t\t\t\t\twriteString(this.";
  protected final String TEXT_102 = ",dos);" + NL + "\t\t\t\t\t";
  protected final String TEXT_103 = NL + "\t\t\t\t\t\twriteDate(this.";
  protected final String TEXT_104 = ",dos);" + NL + "\t\t\t\t\t";
  protected final String TEXT_105 = NL + "\t\t\t\t\t\twriteByteArray(this.";
  protected final String TEXT_106 = ",dos);" + NL + "\t\t\t\t\t";
  protected final String TEXT_107 = NL + "       \t\t\t    \tdos.writeObject(this.";
  protected final String TEXT_108 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_109 = NL + "\t\t\t\t\t\twriteInteger(this.";
  protected final String TEXT_110 = ",dos);" + NL + "\t\t\t\t\t";
  protected final String TEXT_111 = NL + "\t\t\t\t\t\tif(this.";
  protected final String TEXT_112 = " == null) {" + NL + "\t\t\t                dos.writeByte(-1);" + NL + "\t\t\t\t\t\t} else {" + NL + "               \t\t\t\tdos.writeByte(0);" + NL + "           \t\t\t    \tdos.write";
  protected final String TEXT_113 = "(this.";
  protected final String TEXT_114 = ");" + NL + "\t\t            \t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_115 = NL + "        \t} catch (IOException e) {" + NL + "\t            throw new RuntimeException(e);";
  protected final String TEXT_116 = NL + "        }" + NL + "        ";
  protected final String TEXT_117 = NL + "\t\t\tfinally {}" + NL + "\t\t";
  protected final String TEXT_118 = NL + "        " + NL + "    }" + NL;
  protected final String TEXT_119 = NL + NL + NL + "    /** " + NL + "     * Fill Values data by reading ObjectInputStream." + NL + "     */" + NL + "    public void readValuesData(DataInputStream dis, ObjectInputStream ois) {" + NL + "        try {" + NL + "            " + NL + "\t\t\tint length = 0;" + NL + "\t\t";
  protected final String TEXT_120 = NL + "\t\t\t            this.";
  protected final String TEXT_121 = " = dis.read";
  protected final String TEXT_122 = "();" + NL + "\t\t\t\t\t";
  protected final String TEXT_123 = NL + "\t\t\t\t\t\tthis.";
  protected final String TEXT_124 = " = readString(dis,ois);" + NL + "\t\t\t\t\t";
  protected final String TEXT_125 = NL + "\t\t\t\t\t\tthis.";
  protected final String TEXT_126 = " = readByteArray(dis,ois);" + NL + "\t\t\t\t\t";
  protected final String TEXT_127 = NL + "\t\t\t\t\t\tthis.";
  protected final String TEXT_128 = " = readDate(dis,ois);" + NL + "\t\t\t\t\t";
  protected final String TEXT_129 = NL + "       \t\t\t    \tthis.";
  protected final String TEXT_130 = " = (";
  protected final String TEXT_131 = ") ois.readObject();" + NL + "\t\t\t\t\t";
  protected final String TEXT_132 = NL + "\t\t\t\t\t\tthis.";
  protected final String TEXT_133 = " = readInteger(dis,ois);" + NL + "\t\t\t\t\t";
  protected final String TEXT_134 = NL + "\t\t\t            length = dis.readByte();" + NL + "           \t\t\t\tif (length == -1) {" + NL + "           \t    \t\t\tthis.";
  protected final String TEXT_135 = " = null;" + NL + "           \t\t\t\t} else {" + NL + "           \t\t\t    \tthis.";
  protected final String TEXT_136 = " = dis.read";
  protected final String TEXT_137 = "();" + NL + "           \t\t\t\t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_138 = NL + "        \t} catch (IOException e) {" + NL + "\t            throw new RuntimeException(e);";
  protected final String TEXT_139 = NL + "        " + NL + "\t\t";
  protected final String TEXT_140 = NL + "\t\t\t} catch(ClassNotFoundException eCNFE) {" + NL + "\t\t\t\t throw new RuntimeException(eCNFE);" + NL + "\t\t";
  protected final String TEXT_141 = NL + NL + "        }" + NL + "" + NL + "\t\t";
  protected final String TEXT_142 = NL + "        \tfinally {}";
  protected final String TEXT_143 = NL + "        " + NL + "    }" + NL + "" + NL + "    /** " + NL + "     * Return a byte array which represents Values data." + NL + "     */" + NL + "    public void writeValuesData(DataOutputStream dos, ObjectOutputStream oos) {" + NL + "        try {" + NL + "        " + NL + "\t\t";
  protected final String TEXT_144 = NL + "\t\t            \tdos.write";
  protected final String TEXT_145 = "(this.";
  protected final String TEXT_146 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_147 = NL + "\t\t\t\t\t\twriteString(this.";
  protected final String TEXT_148 = ", dos, oos);" + NL + "\t\t\t\t\t";
  protected final String TEXT_149 = NL + "\t\t\t\t\t\twriteByteArray(this.";
  protected final String TEXT_150 = ", dos, oos);" + NL + "\t\t\t\t\t";
  protected final String TEXT_151 = NL + "\t\t\t\t\t\twriteDate(this.";
  protected final String TEXT_152 = ", dos, oos);" + NL + "\t\t\t\t\t";
  protected final String TEXT_153 = NL + "       \t\t\t    \toos.writeObject(this.";
  protected final String TEXT_154 = ");" + NL + "\t\t\t\t\t";
  protected final String TEXT_155 = NL + "\t\t\t\t\twriteInteger(this.";
  protected final String TEXT_156 = ", dos, oos);" + NL + "\t\t\t\t\t";
  protected final String TEXT_157 = NL + "\t\t\t\t\t\tif(this.";
  protected final String TEXT_158 = " == null) {" + NL + "\t\t\t                dos.writeByte(-1);" + NL + "\t\t\t\t\t\t} else {" + NL + "               \t\t\t\tdos.writeByte(0);" + NL + "           \t\t\t    \tdos.write";
  protected final String TEXT_159 = "(this.";
  protected final String TEXT_160 = ");" + NL + "\t\t            \t}" + NL + "\t\t\t\t\t";
  protected final String TEXT_161 = NL + "        \t} catch (IOException e) {" + NL + "\t            throw new RuntimeException(e);";
  protected final String TEXT_162 = NL + "        \t}";
  protected final String TEXT_163 = NL + "        \tfinally {}";
  protected final String TEXT_164 = NL + "        " + NL + "    }" + NL;
  protected final String TEXT_165 = NL + NL + "    public String toString() {" + NL + "\t\t" + NL + "\t\tStringBuilder sb = new StringBuilder();" + NL + "\t\tsb.append(super.toString());" + NL + "\t\tsb.append(\"[\");";
  protected final String TEXT_166 = NL + "\t\tsb.append(\"";
  protected final String TEXT_167 = "=\"+";
  protected final String TEXT_168 = ");";
  protected final String TEXT_169 = NL + "\t\tsb.append(\"";
  protected final String TEXT_170 = "=\"+String.valueOf(";
  protected final String TEXT_171 = "));";
  protected final String TEXT_172 = NL + "\t    sb.append(\"]\");" + NL + "\t    " + NL + "\t    return sb.toString();" + NL + "    }" + NL + "    " + NL + "    /** " + NL + "     * Compare keys" + NL + "     */" + NL + "    public int compareTo(";
  protected final String TEXT_173 = "Struct";
  protected final String TEXT_174 = " other) {" + NL + "\t\t" + NL + "\t\tint returnValue = -1;" + NL + "\t\t";
  protected final String TEXT_175 = NL + "\t\t\t\t\t\treturnValue = checkNullsAndCompare(this.";
  protected final String TEXT_176 = ", other.";
  protected final String TEXT_177 = ");" + NL + "\t\t\t\t\t\tif(returnValue != 0) {" + NL + "\t\t\t\t\t\t\treturn returnValue;" + NL + "\t\t\t\t\t\t}" + NL + "" + NL + "\t\t\t\t\t";
  protected final String TEXT_178 = NL + "\t    return returnValue;" + NL + "    }" + NL + "    " + NL + "    " + NL + "    private int checkNullsAndCompare(Object object1, Object object2) {" + NL + "        int returnValue = 0;" + NL + "\t\tif (object1 instanceof Comparable && object2 instanceof Comparable) {" + NL + "            returnValue = ((Comparable) object1).compareTo(object2);" + NL + "        } else if (object1 != null && object2 != null) {" + NL + "            returnValue = compareStrings(object1.toString(), object2.toString());" + NL + "        } else if (object1 == null && object2 != null) {" + NL + "            returnValue = 1;" + NL + "        } else if (object1 != null && object2 == null) {" + NL + "            returnValue = -1;" + NL + "        } else {" + NL + "            returnValue = 0;" + NL + "        }" + NL + "" + NL + "        return returnValue;" + NL + "    }" + NL + "" + NL + "    private int compareStrings(String string1, String string2) {" + NL + "        return string1.compareTo(string2);" + NL + "    }" + NL + "" + NL + "    " + NL + "}";
  protected final String TEXT_179 = NL + "/**" + NL + " * Start of Function: ";
  protected final String TEXT_180 = "Process." + NL + " * Byte Length of Source Code: ?SIZE? " + NL + " */";
  protected final String TEXT_181 = NL + "public void ";
  protected final String TEXT_182 = "Process(final java.util.Map<String, Object> globalMap) throws TalendException {" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_183 = "_SUBPROCESS_STATE\", 0);" + NL + "" + NL + " final boolean execStat = this.execStat;";
  protected final String TEXT_184 = NL + "\t" + NL + "\ttry {" + NL + "\t\tfinal ParallelThreadPool pool = (ParallelThreadPool) globalMap" + NL + "\t\t\t\t.get(\"PARALLEL_FLOW_POOL_";
  protected final String TEXT_185 = "\");" + NL + "\t\tclass ";
  protected final String TEXT_186 = "_ParallelThread extends ParallelThread {" + NL + "" + NL + "\t\t\tpublic ";
  protected final String TEXT_187 = "_ParallelThread(" + NL + "\t\t\t\t\tjava.util.Map<String, Object> globalMap," + NL + "\t\t\t\t\tObject[] lockWrite) {" + NL + "\t\t\t\tsuper(globalMap, lockWrite);" + NL + "\t\t\t}" + NL + "\t\t\tboolean isRunning = false;" + NL + "\t\t\tString iterateId = \"\";" + NL + "\t\t\t" + NL + "\t\t\tpublic void run() {" + NL + "\t\t\t\tjava.util.Map threadRunResultMap = new java.util.HashMap();" + NL + "\t\t\t\tthreadRunResultMap.put(\"errorCode\", null);" + NL + "\t\t\t\tthreadRunResultMap.put(\"status\", \"\");" + NL + "\t\t\t\tthreadLocal.set(threadRunResultMap);" + NL + "\t\t\t\t" + NL + "\t\t\t\tthis.isRunning = true;" + NL + "\t\t\t\tString currentComponent = \"\";" + NL + "\t\t\t\ttry{" + NL + "\t";
  protected final String TEXT_188 = NL + NL + "\tString iterateId = \"\";" + NL + "\tString currentComponent = \"\";" + NL + "\t\t\t\t" + NL + "\ttry {" + NL + "\t" + NL + "\t\t\tString currentMethodName = new Exception().getStackTrace()[0].getMethodName();" + NL + "\t\t\tboolean resumeIt = currentMethodName.equals(resumeEntryMethodName);" + NL + "\t\t\tif( resumeEntryMethodName == null || resumeIt || globalResumeTicket){//start the resume" + NL + "\t\t\t\tglobalResumeTicket = true;" + NL;
  protected final String TEXT_189 = NL + "    \t\t\t//resumeUtil.addLog(\"CHECKPOINT\", \"CONNECTION:";
  protected final String TEXT_190 = ":";
  protected final String TEXT_191 = ":";
  protected final String TEXT_192 = "\", \"\", Thread.currentThread().getId() + \"\", \"\", \"\", \"\", \"\", \"\");" + NL + "    \t\t";
  protected final String TEXT_193 = NL + "\t\t\t\t\t";
  protected final String TEXT_194 = NL + "\t\t";
  protected final String TEXT_195 = "Process(globalMap);";
  protected final String TEXT_196 = NL;
  protected final String TEXT_197 = NL + "\t\t";
  protected final String TEXT_198 = "\t\t  " + NL + "\t\t";
  protected final String TEXT_199 = NL + "\t\t";
  protected final String TEXT_200 = NL;
  protected final String TEXT_201 = NL;

    // add the list of the connection names to avoid to declare two times the same name.
    public String createPrivateClassInstance(INode node, String parentLastRoot, boolean force) {
    	return createPrivateClassInstance(node, parentLastRoot, force, new HashSet<String>());
    }

    public String createPrivateClassInstance(INode n, String parentLastRoot, boolean force, Set<String> names) {
        String toReturn = "";
        // declare root structs (all main outgoing connections)
        for (IConnection conn : n.getOutgoingConnections()) {
            if (conn.getLineStyle().equals(EConnectionType.FLOW_MAIN) || conn.getLineStyle().equals(EConnectionType.FLOW_MERGE)) {
                if ((force)||(n.isSubProcessStart() || !(NodeUtil.isDataAutoPropagated(n)))) {
                    // new Instance
                    if (!names.contains(conn.getName())) {
	                    toReturn += conn.getName() + "Struct " + conn.getName() + " = new " + conn.getName()
	                            + "Struct();\n";
	                    names.add(conn.getName());
	                    parentLastRoot = conn.getName();
                    }
	            } else {
                    // copy
                    if (!names.contains(conn.getName())) {
	                    toReturn += parentLastRoot + "Struct " + conn.getName() + " = " + parentLastRoot + ";\n";
	                    names.add(conn.getName());
                    }
                }
                if(!conn.getTarget().getComponent().useMerge()) {
                   toReturn += createPrivateClassInstance(conn.getTarget(), parentLastRoot, false, names);
                }
            } else if (conn.getLineStyle().equals(EConnectionType.ITERATE)||conn.getLineStyle().equals(EConnectionType.ON_ROWS_END)) {
            	toReturn += createPrivateClassInstance(conn.getTarget(), parentLastRoot, true, names);
            }
        }
        return toReturn;
    }
    
    public String createPriveClassMethodDeclaration(INode n, String parentLastRoot, boolean force, Set<String> names) {
    	String toReturn = "";
        // declare root structs (all main outgoing connections)
        for (IConnection conn : n.getOutgoingConnections()) {
            if (conn.getLineStyle().equals(EConnectionType.FLOW_MAIN) || conn.getLineStyle().equals(EConnectionType.FLOW_MERGE)) {
                if ((force)||(n.isSubProcessStart() || !(NodeUtil.isDataAutoPropagated(n)))) {
                    // new Instance
                    if (!names.contains(conn.getName())) {
	                    toReturn += conn.getName() + "Struct " + conn.getName() + ",";
	                    names.add(conn.getName());
	                    parentLastRoot = conn.getName();
                    }
	            } else {
                    // copy
                    if (!names.contains(conn.getName())) {
	                    toReturn += parentLastRoot + "Struct " + conn.getName() + ",";
	                    names.add(conn.getName());
                    }
                }
                if(!conn.getTarget().getComponent().useMerge()) {
                   toReturn += createPriveClassMethodDeclaration(conn.getTarget(), parentLastRoot, false, names);
                }
            } else if (conn.getLineStyle().equals(EConnectionType.ITERATE)||conn.getLineStyle().equals(EConnectionType.ON_ROWS_END)) {
            	toReturn += createPriveClassMethodDeclaration(conn.getTarget(), parentLastRoot, true, names);
            }
        }
        return toReturn;
    }
    
    public String createPrivateClassMethodInstance(INode n, String parentLastRoot, boolean force, Set<String> names) {
        String toReturn = "";
        // declare root structs (all main outgoing connections)
        for (IConnection conn : n.getOutgoingConnections()) {
            if (conn.getLineStyle().equals(EConnectionType.FLOW_MAIN) || conn.getLineStyle().equals(EConnectionType.FLOW_MERGE)) {
                if ((force) || (n.isSubProcessStart() || !(NodeUtil.isDataAutoPropagated(n)))) {
                    // new Instance
                    if (!names.contains(conn.getName())) {
                        toReturn += conn.getName() + ",";
                        names.add(conn.getName());
                        parentLastRoot = conn.getName();
                    }
                } else {
                    // copy
                    if (!names.contains(conn.getName())) {
                        toReturn += conn.getName() + ",";
                        names.add(conn.getName());
                    }
                }
                if (!conn.getTarget().getComponent().useMerge()) {
                    toReturn += createPrivateClassMethodInstance(conn.getTarget(), parentLastRoot, false, names);
                }
            } else if (conn.getLineStyle().equals(EConnectionType.ITERATE)
                    || conn.getLineStyle().equals(EConnectionType.ON_ROWS_END)) {
                toReturn += createPrivateClassMethodInstance(conn.getTarget(), parentLastRoot, true, names);
            }
        }
        return toReturn;
    }
		 
    public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	NodesSubTree subTree = (NodesSubTree) codeGenArgument.getArgument();
	boolean isRunInMultiThread = codeGenArgument.getIsRunInMultiThread();
	boolean stat = codeGenArgument.isStatistics();

    stringBuffer.append(TEXT_1);
    
	HashSet<String> hSet = new HashSet<String>();
	for (INode node : subTree.getNodes()) {
		List<IMetadataTable> metadatas = node.getMetadataList();
			List< ? extends IConnection> conns = node.getOutgoingConnections();
			for (IConnection conn : conns) {
				// templateOrigin is used for Virtual COmponents
				String templateOrigin = "";
				if (conn.getTarget()!=null) {
					templateOrigin = ElementParameterParser.getValue(conn.getTarget(), "__ORIGIN__");
				}
				boolean isLookup = false;
				if (
					(templateOrigin.compareTo("")!=0) ||
					(conn.getLineStyle().equals(EConnectionType.FLOW_MAIN))||
					(conn.getLineStyle().equals(EConnectionType.FLOW_REF)) ||
					(conn.getLineStyle().equals(EConnectionType.FLOW_MERGE))||
					(conn.getLineStyle().equals(EConnectionType.RUN_AFTER)) 
				) {

					if(conn.getLineStyle().equals(EConnectionType.RUN_AFTER)){
						templateOrigin = ElementParameterParser.getValue(conn.getSource(), "__ORIGIN__");
					}				
					if (!hSet.contains(conn.getName() + templateOrigin)) {
						hSet.add(conn.getName() + templateOrigin);
						IMetadataTable metadata = conn.getMetadataTable();
						
						
						Set<String> hashableColumnsNames = new HashSet<String>();
						if(conn.getTarget() instanceof IHashableInputConnections) {
							IHashableInputConnections target = (IHashableInputConnections) conn.getTarget();
							IHashConfiguration hashConfiguration = target.getHashConfiguration(conn.getName());
							if(hashConfiguration != null) {
								for(IHashableColumn hashableColumn : hashConfiguration.getHashableColumns()) {
									isLookup = true;
									hashableColumnsNames.add(hashableColumn.getName());
								}
							}
						} else {
						
							//only for the lookup branch of the tJoin
							boolean isLookOftJoin = false;
							boolean isLookOftRecordMatching = true;
							if(conn.getLineStyle().equals(EConnectionType.FLOW_REF)){
								IProcess process = conn.getTarget().getProcess();
								for (INode tjoinNode : process.getNodesOfType("tJoin")) {
									if(conn.getTarget().getUniqueName().equals(tjoinNode.getUniqueName())){
										List<Map<String, String>> joinKeys = (List<Map<String,String>>)ElementParameterParser.getObjectValue(conn.getTarget(), "__JOIN_KEY__");
										for(Map<String, String> joinKeyLine : joinKeys){       
											String lookupKey = joinKeyLine.get("LOOKUP_COLUMN");
											hashableColumnsNames.add(lookupKey);
										}
										isLookOftJoin = true;
										isLookup = true;
									}
								}
								
								// for tRecordMatching    			
								if(conn.getTarget().getUniqueName().startsWith("tRecordMatching")){
									List<Map<String, String>> joinKeys = (List<Map<String,String>>)ElementParameterParser.getObjectValue(conn.getTarget(), "__BLOCKING_DEFINITION__");
									for(Map<String, String> joinKeyLine : joinKeys){ 
										String lookupKey = joinKeyLine.get("LOOKUP_COLUMN");
										hashableColumnsNames.add(lookupKey);
										isLookup = true;
									}
									isLookOftRecordMatching = true;
								}
							}
													
							if (!isLookOftJoin && !isLookOftRecordMatching && metadata!=null) { 
								for (IMetadataColumn column: metadata.getListColumns()) {
									if (column.isKey()) {
										hashableColumnsNames.add(column.getLabel());
									}
								}
							}
						}
						boolean hasAtLeastOneKey = hashableColumnsNames.size() > 0;
						

    stringBuffer.append(TEXT_2);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(templateOrigin );
    stringBuffer.append(TEXT_4);
    stringBuffer.append( isLookup ? "IPersistableComparableLookupRow<"+conn.getName()+"Struct>" : "IPersistableRow<"+conn.getName()+"Struct"+ templateOrigin + ">" );
    stringBuffer.append(TEXT_5);
    
	if(hasAtLeastOneKey) {

    stringBuffer.append(TEXT_6);
    
	}

    stringBuffer.append(TEXT_7);
    
		if (metadata !=null) {
			for (IMetadataColumn column: metadata.getListColumns()) {
   				JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
			    
    stringBuffer.append(TEXT_8);
    stringBuffer.append( JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable()) );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(column.getLabel() );
    
			    	
			    	if(javaType == JavaTypesManager.CHARACTER && !column.isNullable()) {
			    		
    stringBuffer.append(TEXT_10);
    
			    	}
			    	
			    	
    stringBuffer.append(TEXT_11);
    stringBuffer.append( JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable()) );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(column.getLabel().substring(0, 1).toUpperCase());
    stringBuffer.append(column.getLabel().substring(1));
    stringBuffer.append(TEXT_13);
    stringBuffer.append(column.getLabel());
    stringBuffer.append(TEXT_14);
    
				if("id_Document".equals(javaType.getId())) {
				
    stringBuffer.append(TEXT_15);
    
				}		    	
			}
		}
    
    stringBuffer.append(TEXT_16);
    
	if(hasAtLeastOneKey) { // T_SH_001
	
    stringBuffer.append(TEXT_17);
    
		if (metadata !=null) {
			for (IMetadataColumn column: metadata.getListColumns()) {
				if (hashableColumnsNames.contains(column.getLabel())) {

					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());

					if (JavaTypesManager.isJavaPrimitiveType(column.getTalendType(), column.isNullable())) {
					
					 	String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
					 	if(javaType == JavaTypesManager.BOOLEAN) {
						
    stringBuffer.append(TEXT_18);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_19);
    
						} else {	
						
    stringBuffer.append(TEXT_20);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_21);
    
						}
						
					} else if(javaType == JavaTypesManager.BYTE_ARRAY) {

						
    stringBuffer.append(TEXT_22);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_23);
    
					
					} else {
					
    stringBuffer.append(TEXT_24);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_26);
    
					}
					
				}
			}
		}
    
    stringBuffer.append(TEXT_27);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(templateOrigin );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(templateOrigin );
    stringBuffer.append(TEXT_31);
    	
		if (metadata !=null) {
			for (IMetadataColumn column: metadata.getListColumns()) {
				if (hashableColumnsNames.contains(column.getLabel())) {

					JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());

					if (JavaTypesManager.isJavaPrimitiveType(column.getTalendType(), column.isNullable())) {
					
    stringBuffer.append(TEXT_32);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_34);
    
					} else if(javaType == JavaTypesManager.BYTE_ARRAY) {
					
    stringBuffer.append(TEXT_35);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_37);
    
					} else {
					
    stringBuffer.append(TEXT_38);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_42);
    
					}

				}
			}
    	}
    	
    stringBuffer.append(TEXT_43);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(templateOrigin );
    stringBuffer.append(TEXT_45);
    
		if (metadata !=null) {
			for (IMetadataColumn column: metadata.getListColumns()) {
        	
				
    stringBuffer.append(TEXT_46);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_47);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_48);
    
			}
		}
		
    stringBuffer.append(TEXT_49);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(templateOrigin );
    stringBuffer.append(TEXT_51);
    
		if (metadata !=null) {
			for (IMetadataColumn column: metadata.getListColumns()) {
        		if (hashableColumnsNames.contains(column.getLabel())) {
        	
					
    stringBuffer.append(TEXT_52);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_53);
    stringBuffer.append( column.getLabel() );
    stringBuffer.append(TEXT_54);
    
	            }
			}
		}
		
    stringBuffer.append(TEXT_55);
    
	} // T_SH_001
	
    stringBuffer.append(TEXT_56);
    
		if (metadata !=null) {
			boolean hasString1 = false;
			boolean hasString2 = false;
			boolean hasDate1 = false;
			boolean hasDate2 = false;
			boolean hasByteArray1 = false;
			boolean hasByteArray2 = false;
			boolean hasInteger1 = false;
			boolean hasInteger2 = false;
			for (IMetadataColumn column: metadata.getListColumns()) {
			 	String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
			 	if(JavaTypesManager.isJavaPrimitiveType(column.getTalendType(), column.isNullable())) continue;
				if(typeToGenerate.equals("String")) {
					if (!hasString1 && (!isLookup || hashableColumnsNames.contains(column.getLabel()))) {
						hasString1 =true;

    stringBuffer.append(TEXT_57);
    
					} else if(!hasString2 && (isLookup && !hashableColumnsNames.contains(column.getLabel()))){
						hasString2 = true;

    stringBuffer.append(TEXT_58);
    
					}
				} else if(typeToGenerate.equals("java.util.Date")) {
					if(!hasDate1 && (!isLookup || hashableColumnsNames.contains(column.getLabel()))) {
						hasDate1 = true;

    stringBuffer.append(TEXT_59);
    
					}else if (!hasDate2 && (isLookup && !hashableColumnsNames.contains(column.getLabel()))) {
						hasDate2 = true;

    stringBuffer.append(TEXT_60);
    
					}
				} else if(typeToGenerate.equals("byte[]")) {
					if(!hasByteArray1 && (!isLookup || hashableColumnsNames.contains(column.getLabel()))) {
						hasByteArray1 = true;

    stringBuffer.append(TEXT_61);
    
					}else if (!hasByteArray2 && (isLookup && !hashableColumnsNames.contains(column.getLabel()))) {
						hasByteArray2 = true;

    stringBuffer.append(TEXT_62);
    
					}
				}else {
					typeToGenerate =JavaTypesManager.getTypeToGenerate(column.getTalendType(), false);
					typeToGenerate=typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
					if(typeToGenerate.equals("Int")){
						if(!hasInteger1 && (!isLookup || hashableColumnsNames.contains(column.getLabel()))) {
							hasInteger1 = true;

    stringBuffer.append(TEXT_63);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_64);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_65);
    
						} else if(!hasInteger2 && (isLookup && !hashableColumnsNames.contains(column.getLabel()))){
							hasInteger2 = true;

    stringBuffer.append(TEXT_66);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_67);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_68);
    
						}
					}
				}
			}
    	}

    stringBuffer.append(TEXT_69);
    stringBuffer.append( isLookup ? "readKeys" : "read" );
    stringBuffer.append(TEXT_70);
    	
		boolean hasAtLeastOneRead = false;
		boolean hasAtLeastOneObjectType = false;
		if (metadata !=null) {
			for (IMetadataColumn column: metadata.getListColumns()) {
			  if (!isLookup || hashableColumnsNames.contains(column.getLabel())) {
			  	hasAtLeastOneRead = true;
				JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
			 	String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());

				if (JavaTypesManager.isJavaPrimitiveType(column.getTalendType(), column.isNullable())) {
					typeToGenerate=typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
					
    stringBuffer.append(TEXT_71);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_72);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_73);
    
				} else if(typeToGenerate.equals("String")) {
					
    stringBuffer.append(TEXT_74);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_75);
    
				} else if(typeToGenerate.equals("java.util.Date")) {
					
    stringBuffer.append(TEXT_76);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_77);
    
				} else if(typeToGenerate.equals("byte[]")) {
					
    stringBuffer.append(TEXT_78);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_79);
    
				} else if(javaType.isObjectBased() || typeToGenerate.equals("Geometry") || typeToGenerate.equals("Dynamic")) {
			 		hasAtLeastOneObjectType = true;
					
    stringBuffer.append(TEXT_80);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_81);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_82);
    
				} else {
					typeToGenerate =JavaTypesManager.getTypeToGenerate(column.getTalendType(), false);
					typeToGenerate=typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
					if(typeToGenerate.equals("Int")){
					
    stringBuffer.append(TEXT_83);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_84);
    
					} else {
					
    stringBuffer.append(TEXT_85);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_86);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_87);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_88);
    
					}
				}
			  }
			}
    	}
	    if(hasAtLeastOneRead) {
	    
    stringBuffer.append(TEXT_89);
    
        }
        
    stringBuffer.append(TEXT_90);
    
		if(hasAtLeastOneObjectType) {
		
    stringBuffer.append(TEXT_91);
    
		}
		
    stringBuffer.append(TEXT_92);
    
		if(!hasAtLeastOneRead) {
		
    stringBuffer.append(TEXT_93);
    
		}
		
    stringBuffer.append(TEXT_94);
    stringBuffer.append( isLookup ? "writeKeys" : "write" );
    stringBuffer.append(TEXT_95);
    	
		boolean hasAtLeastOneWrite = false;
		hasAtLeastOneObjectType = false;
		if (metadata !=null) {
			for (IMetadataColumn column: metadata.getListColumns()) {
			  if (!isLookup || hashableColumnsNames.contains(column.getLabel())) {
				JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
				hasAtLeastOneWrite = true;
			 	String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
				
    stringBuffer.append(TEXT_96);
    stringBuffer.append(typeToGenerate );
    stringBuffer.append(TEXT_97);
    
				if (JavaTypesManager.isJavaPrimitiveType(column.getTalendType(), column.isNullable())) {
					typeToGenerate=typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
					
    stringBuffer.append(TEXT_98);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_99);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_100);
    
				} else if(typeToGenerate.equals("String")) {
					
    stringBuffer.append(TEXT_101);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_102);
    
				} else if(typeToGenerate.equals("java.util.Date")) {
					
    stringBuffer.append(TEXT_103);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_104);
    
				} else if(typeToGenerate.equals("byte[]")) {
					
    stringBuffer.append(TEXT_105);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_106);
    
				} else if(javaType.isObjectBased() || typeToGenerate.equals("Geometry") || typeToGenerate.equals("Dynamic")) {
					
    stringBuffer.append(TEXT_107);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_108);
    
				} else {
					typeToGenerate =JavaTypesManager.getTypeToGenerate(column.getTalendType(), false);
					typeToGenerate = typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
					if(typeToGenerate.equals("Int")){
					
    stringBuffer.append(TEXT_109);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_110);
    
					} else {
					
    stringBuffer.append(TEXT_111);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_112);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_113);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_114);
    
					}
				}
			  }
			}
    	}
	    if(hasAtLeastOneWrite) {
	    
    stringBuffer.append(TEXT_115);
    
        }
        
    stringBuffer.append(TEXT_116);
    
		if(!hasAtLeastOneWrite) {
		
    stringBuffer.append(TEXT_117);
    
		}
		
    stringBuffer.append(TEXT_118);
    
	if(isLookup) { // T SH 002
	
    stringBuffer.append(TEXT_119);
    	
		hasAtLeastOneRead = false;
		hasAtLeastOneObjectType = false;
		if (metadata !=null) {
			for (IMetadataColumn column: metadata.getListColumns()) {
			  if (!hashableColumnsNames.contains(column.getLabel())) {
				JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
				hasAtLeastOneRead = true;
			 	String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());

				if (JavaTypesManager.isJavaPrimitiveType(column.getTalendType(), column.isNullable())) {
					typeToGenerate=typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
					
    stringBuffer.append(TEXT_120);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_121);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_122);
    
				} else if(typeToGenerate.equals("String")) {
					
    stringBuffer.append(TEXT_123);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_124);
    
				} else if(typeToGenerate.equals("byte[]")) {
					
    stringBuffer.append(TEXT_125);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_126);
    
				} else if(typeToGenerate.equals("java.util.Date")) {
					
    stringBuffer.append(TEXT_127);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_128);
    
				} else if(typeToGenerate.equals("Object") || typeToGenerate.equals("Geometry") || typeToGenerate.equals("BigDecimal") || typeToGenerate.equals("List") || typeToGenerate.equals("Dynamic") || typeToGenerate.equals("Document")) {
			 		hasAtLeastOneObjectType = true;
					
    stringBuffer.append(TEXT_129);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_130);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_131);
    
				} else {
					typeToGenerate =JavaTypesManager.getTypeToGenerate(column.getTalendType(), false);
					typeToGenerate=typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
					if(typeToGenerate.equals("Int")){
					
    stringBuffer.append(TEXT_132);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_133);
    
					} else {
					
    stringBuffer.append(TEXT_134);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_135);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_136);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_137);
    
					}
				}
			  }
			}
    	}
    	if(hasAtLeastOneRead) {
	    
    stringBuffer.append(TEXT_138);
    
        }
        
    stringBuffer.append(TEXT_139);
    
		if(hasAtLeastOneObjectType) {
		
    stringBuffer.append(TEXT_140);
    
		}
		
    stringBuffer.append(TEXT_141);
    
    	if(!hasAtLeastOneRead) {
	    
    stringBuffer.append(TEXT_142);
    
        }
        
    stringBuffer.append(TEXT_143);
    	
		hasAtLeastOneWrite = false;
		if (metadata !=null) {
			for (IMetadataColumn column: metadata.getListColumns()) {
			  if (!hashableColumnsNames.contains(column.getLabel())) {
				JavaType javaType = JavaTypesManager.getJavaTypeFromId(column.getTalendType());
				hasAtLeastOneWrite = true;
			 	String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
				if (JavaTypesManager.isJavaPrimitiveType(column.getTalendType(), column.isNullable())) {
					typeToGenerate=typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
					
    stringBuffer.append(TEXT_144);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_145);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_146);
    
				} else if(typeToGenerate.equals("String")) {
					
    stringBuffer.append(TEXT_147);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_148);
    
				} else if(typeToGenerate.equals("byte[]")) {
					
    stringBuffer.append(TEXT_149);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_150);
    
				} else if(typeToGenerate.equals("java.util.Date")) {
					
    stringBuffer.append(TEXT_151);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_152);
    
				} else if(typeToGenerate.equals("Object") || typeToGenerate.equals("Geometry") || typeToGenerate.equals("BigDecimal") || typeToGenerate.equals("List") || typeToGenerate.equals("Dynamic") || typeToGenerate.equals("Document")) {
					
    stringBuffer.append(TEXT_153);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_154);
    
				} else {
					typeToGenerate =JavaTypesManager.getTypeToGenerate(column.getTalendType(), false);
					typeToGenerate = typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
					if(typeToGenerate.equals("Int")){
					
    stringBuffer.append(TEXT_155);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_156);
    
					} else {
					
    stringBuffer.append(TEXT_157);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_158);
    stringBuffer.append( typeToGenerate );
    stringBuffer.append(TEXT_159);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_160);
    
					}
				}
			  }
			}
    	}

    	if(hasAtLeastOneRead) {
	    
    stringBuffer.append(TEXT_161);
    
        }
        
    stringBuffer.append(TEXT_162);
    
    	if(!hasAtLeastOneRead) {
	    
    stringBuffer.append(TEXT_163);
    
        }
        
    stringBuffer.append(TEXT_164);
    
	} // T SH 002
	
    stringBuffer.append(TEXT_165);
    	
		if (metadata !=null) {
			List<IMetadataColumn> clmlist = metadata.getListColumns();
			for(int i=0; i< clmlist.size(); i++) {
				IMetadataColumn column = clmlist.get(i);
				String typeToGenerate = JavaTypesManager.getTypeToGenerate(column.getTalendType(), column.isNullable());
				if(typeToGenerate.equals("String")) {

    stringBuffer.append(TEXT_166);
    stringBuffer.append(i==0?"":"," );
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_167);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_168);
    
				}else{

    stringBuffer.append(TEXT_169);
    stringBuffer.append(i==0?"":"," );
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_170);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_171);
    
				}
			}
    	}

    stringBuffer.append(TEXT_172);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(TEXT_173);
    stringBuffer.append(templateOrigin );
    stringBuffer.append(TEXT_174);
    	
		if (metadata !=null) {
			for (IMetadataColumn column: metadata.getListColumns()) {
				if (hashableColumnsNames.contains(column.getLabel())) {
					
    stringBuffer.append(TEXT_175);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_176);
    stringBuffer.append(column.getLabel() );
    stringBuffer.append(TEXT_177);
    
				}
			}
    	}
	    
    stringBuffer.append(TEXT_178);
    
						}
					}
				}
	}

     if (subTree.isMethodSizeNeeded()){ 
    stringBuffer.append(TEXT_179);
    stringBuffer.append(subTree.getName() );
    stringBuffer.append(TEXT_180);
     } 
    stringBuffer.append(TEXT_181);
    stringBuffer.append(subTree.getName() );
    stringBuffer.append(TEXT_182);
    stringBuffer.append(subTree.getName() );
    stringBuffer.append(TEXT_183);
    
	boolean isParallelize = false;
	for (INode node : subTree.getNodes()) {
		if(node!=null){
			String parallelize = ElementParameterParser.getValue(node, "__PARALLELIZE__");
			if(parallelize!=null &&parallelize.equals("true")){
				// temporary modification : if tAsyncIn is available, this is always a parallelized process
				isParallelize = true;
			}
		}
	}
	if(isParallelize){//isParallelize

    stringBuffer.append(TEXT_184);
    stringBuffer.append((subTree.getName()).replaceAll("tAsyncIn", "tAsyncOut"));
    stringBuffer.append(TEXT_185);
    stringBuffer.append(subTree.getName() );
    stringBuffer.append(TEXT_186);
    stringBuffer.append(subTree.getName() );
    stringBuffer.append(TEXT_187);
    
	}else{//isParallelize

    stringBuffer.append(TEXT_188);
    
//this part for "Resume Management"
{
    INode firstNode = subTree.getNode(subTree.getName());
    
    //System.out.println(firstNode.getUniqueName());
    List<? extends IConnection> inConns = firstNode.getIncomingConnections();
    
    //System.out.println(inConns.size());
    for(IConnection conn : inConns){
    
    	boolean activeResume = "true".equals(ElementParameterParser.getValue(conn, "__RESUMING_CHECKPOINT__"));
    	
    	//System.out.println("Test:" + ElementParameterParser.getValue(conn, "__RESUMING_CHECKPOINT__"));
    	
    	if(activeResume){
    		EConnectionType lineStyle = conn.getLineStyle();
    		 INode sourceNode = conn.getSource();
    		 String sourceNodeName = sourceNode.getUniqueName();
    		
    stringBuffer.append(TEXT_189);
    stringBuffer.append(lineStyle.getName() );
    stringBuffer.append(TEXT_190);
    stringBuffer.append(sourceNodeName );
    stringBuffer.append(TEXT_191);
    stringBuffer.append(conn.getName() );
    stringBuffer.append(conn.getOutputId() > 0 ? conn.getOutputId() : "" );
    stringBuffer.append(TEXT_192);
    
    	}
    }
}

    stringBuffer.append(TEXT_193);
    
	// Call for RUN AFTER links
	for (String after : subTree.getAfterSubProcesses()) {

    stringBuffer.append(TEXT_194);
    stringBuffer.append(after );
    stringBuffer.append(TEXT_195);
    
	}
	}//isParallelize

    stringBuffer.append(TEXT_196);
    
	if(!subTree.isMergeSubTree()) {
	List< ? extends IConnection> rootConns = subTree.getRootNode().getOutgoingConnections();
	if ((rootConns!=null)&&(rootConns.size()>0)) {

    stringBuffer.append(TEXT_197);
    stringBuffer.append(createPrivateClassInstance(subTree.getRootNode(), rootConns.get(0).getName(), false));
    
	}
  } else {  
    List<INode> sortedMergeBranchStarts = subTree.getSortedMergeBranchStarts();    
    for (INode startNode : sortedMergeBranchStarts) {
       List< ? extends IConnection> rootConns = startNode.getOutgoingConnections();
       if ((rootConns!=null)&&(rootConns.size()>0)) {

    stringBuffer.append(TEXT_198);
    stringBuffer.append(createPrivateClassInstance(startNode, rootConns.get(0).getName(), false));
    
      }
    }
    
	List< ? extends IConnection> rootConns = subTree.getMergeNode().getOutgoingConnections();
	if ((rootConns!=null)&&(rootConns.size()>0)) {

    stringBuffer.append(TEXT_199);
    stringBuffer.append(createPrivateClassInstance(subTree.getMergeNode(), rootConns.get(0).getName(), false));
    
	}
    
  }

    stringBuffer.append(TEXT_200);
    stringBuffer.append(TEXT_201);
    return stringBuffer.toString();
  }
}