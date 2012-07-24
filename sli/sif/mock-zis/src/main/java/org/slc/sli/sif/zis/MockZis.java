package org.slc.sli.sif.zis;

import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import openadk.library.ADK;
import openadk.library.SIFElement;
import openadk.library.SIFParser;
import openadk.library.SIFWriter;
import openadk.library.impl.SIF_Message;
import openadk.library.infra.SIF_Ack;
import openadk.library.infra.SIF_Event;
import openadk.library.infra.SIF_Register;

import org.springframework.stereotype.Component;

@Component
public class MockZis {
    private List<String> agentCallbackUrls = new ArrayList<String>();
    
    public void addAgent(String agentCallbackUrl) {
        agentCallbackUrls.add(agentCallbackUrl);
    }
    
    public void broadcastMessage(String xmlMessage) throws Exception {
        xmlMessage = "<SIF_Ack>Jon Test</SIF_Ack>";
        
        System.out.println("\n\n\nbroadcasting to: http://localhost:8087/mock-zis/zis\n\n\n");
        
        postMessage(new URL("http://localhost:8087/mock-zis/zis"), xmlMessage);
        
        /*
         * for (String callbackUrl : agentCallbackUrls) {
         * System.out.println("broadcasting to: " + callbackUrl);
         * URL url = new URL(callbackUrl);
         * URLConnection conn = url.openConnection();
         * conn.setDoOutput(true);
         * OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
         * wr.write(xmlMessage);
         * wr.flush();
         * wr.close();
         * }
         */
    }
    
    private void postMessage(URL url, String xmlMessage) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/xml");
            connection.setDoOutput(true);
            
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(xmlMessage);
            wr.flush();
            wr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @PostConstruct
    public void setup() throws Exception {
        ADK.initialize();
    }
    
    public String createAckString() {
        SIF_Message message = new SIF_Message();
        SIF_Ack ack = message.ackStatus(0);
        return sifElementToString(ack);
    }
    
    public String sifElementToString(SIFElement sifElem) {
        StringWriter sw = new StringWriter();
        SIFWriter writer = new SIFWriter(sw);
        writer.suppressNamespace(true);
        writer.write(sifElem);
        writer.flush();
        writer.close();
        return sw.toString();
    }
    
    public void parseSIFMessage(String sifString) {
        try {
            SIFParser parser = SIFParser.newInstance();
            SIFElement sifElem = parser.parse(sifString);
            
            if (sifElem instanceof SIF_Register) {
                System.out.println("sif register");
                processRegisterMessage((SIF_Register) sifElem);
            } else if (sifElem instanceof SIF_Event) {
                System.out.println("sif event");
                // TODO this is just for testing
                broadcastMessage(sifElementToString(sifElem));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void processRegisterMessage(SIF_Register sifReg) {
        String agentCallbackUrl = sifReg.getSIF_Protocol().getSIF_URL();
        agentCallbackUrls.add(agentCallbackUrl);
    }
}
