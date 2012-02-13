package org.slc.sli.util.transform;

import java.util.HashMap;

/**
 * 
 * @author dwilliams
 * 
 */
public class Base64 {
    private static HashMap<String, Integer> idMap = new HashMap<String, Integer>();
    
    public static String toBase64(String uuid) {
        String base64Digits = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        String hex = uuid.replaceAll("-", ""); // remove extra characters
        String msb = hex.substring(0, 16);
        String lsb = hex.substring(16, 32);
        msb = msb.substring(14, 16) + msb.substring(12, 14) + msb.substring(10, 12) + msb.substring(8, 10)
                + msb.substring(6, 8) + msb.substring(4, 6) + msb.substring(2, 4) + msb.substring(0, 2);
        lsb = lsb.substring(14, 16) + lsb.substring(12, 14) + lsb.substring(10, 12) + lsb.substring(8, 10)
                + lsb.substring(6, 8) + lsb.substring(4, 6) + lsb.substring(2, 4) + lsb.substring(0, 2);
        hex = msb + lsb;
        StringBuffer base64 = new StringBuffer();
        
        for (int i = 0; i < 30; i += 6) {
            String sub = hex.substring(i, i + 6);
            int j = Integer.parseInt(sub, 16);
            base64.append(base64Digits.charAt((j >> 18) & 0x3f));
            base64.append(base64Digits.charAt((j >> 12) & 0x3f));
            base64.append(base64Digits.charAt((j >> 6) & 0x3f));
            base64.append(base64Digits.charAt(j & 0x3f));
        }
        int group = Integer.parseInt(hex.substring(30, 32), 16);
        base64.append(base64Digits.charAt((group >> 2) & 0x3f));
        base64.append(base64Digits.charAt((group << 4) & 0x3f));
        base64.append("==");
        return base64.toString();
    }
    
    /**
     * 
     * @param typeCode
     *            MUST be a 4 character string
     * @return
     */
    public static String nextUuid(String typeCode) {
        String fixed = "67ce204b-9999-4a11-" + typeCode + "-";
        
        Integer idCount = idMap.get(typeCode);
        if (idCount == null) {
            idCount = Integer.valueOf(0);
            idMap.put(typeCode, idCount);
        }
        String variable = "000000000000" + idCount;  // start with 12 zeroes
        String mungedVariable = variable.substring(variable.length() - 12);
        
        idMap.put(typeCode, Integer.valueOf(idCount.intValue() + 1));
        
        return fixed + mungedVariable;
    }
}
