/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.test.edfi.entities.meta;

//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class IdTransformer {

    private static final Pattern p1 = Pattern.compile("([a-zA-Z])([a-zA-Z]+)(\\d*)");
    private static final Pattern p2 = Pattern.compile("(\\d+)");
//    static PrintWriter idTransformsFile = null;
//
//    static {
//        try{
//            idTransformsFile = new PrintWriter(new BufferedWriter(new FileWriter("data/idTransforms.txt")));
//        }catch(Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static String transformId(String id) {
        try {
            String transformed =  transformNumbers(transformWords(id));
            //if(idTransformsFile != null)
                //idTransformsFile.println(id + " > " + transformed);
            return transformed.replaceAll("-", "");
        } catch (Exception e) {
//            if(idTransformsFile != null)
//                idTransformsFile.println(id + " > " + "failed");
//            failed to transform. returning as is.            
            return id;
        }
    }

    //sea1-lea1-school1    ->     s1-l1-s1
    private static String transformWords(String unTransformed) throws RuntimeException{
        String transformed = unTransformed;
        Matcher m = p1.matcher(unTransformed);
        if(m.find()) {
            transformed = m.replaceAll("$1$3");
        }
        return transformed;
    }

    //sea123-lea234-school456-session678  ->       sea7B-leaEA-school1C8-session2A6
    private static String transformNumbers(String unTransformed) throws RuntimeException{
        StringBuffer transformed = new StringBuffer();
        Matcher m = p2.matcher(unTransformed);
        while(m.find()) {
            String base10Str = m.group(1);
            Long base10 = Long.parseLong(base10Str);
            String base16Str = Long.toHexString(base10.longValue());
            m.appendReplacement(transformed, base16Str.toUpperCase());
        }
        m.appendTail(transformed);
        return transformed.toString();
    }


    public static void main(String [] args) {
        String id1 = "sea1-lea2-school-session4-10-assessment3";
        System.out.println(id1 + "  > " + IdTransformer.transformId(id1));

        String id2 = "s123-l234-s456-session10";
        System.out.println(id2 + "  > " + IdTransformer.transformId(id2));

        String id3 = "sea123-lea234-school456-item678";
        System.out.println(id3 + "  > " + IdTransformer.transformId(id3));
    }
}
