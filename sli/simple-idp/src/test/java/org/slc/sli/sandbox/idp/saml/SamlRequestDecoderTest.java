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


package org.slc.sli.sandbox.idp.saml;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slc.sli.sandbox.idp.saml.SamlRequestDecoder.SamlRequest;

/**
 * Unit tests
 */
@RunWith(JUnit4.class)
public class SamlRequestDecoderTest {
    
    @Test
    public void testLoadSamlRequest() throws UnsupportedEncodingException, TransformerException {
        String samlRequestBase64 = URLDecoder
                .decode("nZPBbtswDIZfxdDdsePZXSrEAbIEwwJsqxsbO%2Bym2nQrQJY0kW6zt5%2FkpJs3bDnsaPHnT%2FIjvUYxKMu3Iz3pI3wbASk6DUojnwIlG53mRqBErsUAyKnl9fbTR54tUm6dIdMaxaK9z5NakDS6ZE9EFnmSdPBsLPi05QKV9F8L4x75Kk1kZ7Okru%2BO0EkHLSUDkNgqKTCEWPTeuBamlkrWC4XAosO%2BZN4kzrI8z6Do44fizSrO39708Spd3cYizZd5ftumXSu8GiuBKJ%2FhVz7iCAeNJDSVLEuXWZzmcVo02ZIXS57li%2BKm%2BMqi6jLSO6k7qR%2Bvz%2F9wFiH%2F0DRVXN3VDYu%2BgMOJghewzTpQ5FNxN%2BN63da3Di6gZJsZSmHtnOM6mVmf61j%2B2Xsd9pVRsv0ebZUyLzsHgjwGciNMYAdB16uHF9nF%2FSTl5IRGCZpYVFfB%2Fn4USvYS3G97%2FqM5lrx2dLkp6KZ17owmOFG0M4MVTmLgBCfR0iupuWqnPIgj9P%2FD7aqs5W2w9s%2FhSl6M68LW%2FR1C14RxrXF0wfu3fjbn2D9m%2Bxmd%2F1KbHw%3D%3D",
                        "UTF8");
        
        SamlRequestDecoder decoder = new SamlRequestDecoder();
        decoder.setCotString("https://devapp1.slidev.org=https://devopenam1.slidev.org:80/idp2/SSORedirect/metaAlias/idp");
        decoder.initialize();
        SamlRequest request = decoder.decode(samlRequestBase64);
        assertEquals("https://devopenam1.slidev.org:80/idp2/SSORedirect/metaAlias/idp", request.getSpDestination());
        assertEquals("sli-22442e5f-b538-476f-8089-a041449c0dca", request.getId());
        assertEquals(false, request.isForceAuthn());
    }
}
