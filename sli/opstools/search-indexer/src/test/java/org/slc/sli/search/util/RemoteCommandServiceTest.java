/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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
package org.slc.sli.search.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slc.sli.search.process.Admin;

public class RemoteCommandServiceTest {

    private static final int port = 10024;
    private static RemoteCommandService remoteCommandService = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        remoteCommandService = new RemoteCommandService();
        remoteCommandService.setPort(port);
        remoteCommandService.setAdmin(new Admin() {

            public String getHealth() {
                return null;
            }

            public void reloadAll() {
            }

            public void reload(String tenantId) {
            }

            public void reconcileAll() {
            }

            public void reconcile(String tenantId) {
            }
        });
        remoteCommandService.init();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        remoteCommandService.destroy();
    }

    @Test
    public void testConnectionFail() {
        Socket clientSocket = null;
        try {
            clientSocket = new Socket("127.0.0.1", port + 1);
        } catch (Exception e) {
            clientSocket = null;
        }
        Assert.assertNull(clientSocket);
    }

    @Test(timeout = 60000)
    public void testCommands() throws Throwable {
        testHELP();
        testIncorrectCommand();
        testEXTRACT();
        testSTOP();
    }

    private void testIncorrectCommand() throws Throwable {
        Socket clientSocket = connect();
        Assert.assertNotNull(clientSocket);
        PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);

        BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        pw.println("whatever");

        String line = readLine(br);
        Assert.assertTrue(line.startsWith("Available Commands"));
        close(clientSocket);
    }

    private void testHELP() throws Throwable {
        Socket clientSocket = connect();
        Assert.assertNotNull(clientSocket);

        PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);

        BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        pw.println("help");

        String line = readLine(br);
        Assert.assertTrue(line.startsWith("Available Commands"));
        close(clientSocket);
    }

    private void testEXTRACT() throws Throwable {
        Socket clientSocket = connect();
        Assert.assertNotNull(clientSocket);

        PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);

        BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        pw.println("extract");
        String line = readLine(br);
        Assert.assertEquals("sent extract command", line);
        close(clientSocket);
    }

    private void testSTOP() throws Throwable {
        Socket clientSocket = connect();
        Assert.assertNotNull(clientSocket);

        PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);

        BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        pw.println("stop 2");
        String line = readLine(br);
        Assert.assertEquals("Shutting down in 2 second(s)", line);
        close(clientSocket);
        clientSocket = connect();
        Assert.assertNotNull(clientSocket);
        close(clientSocket);
        // wait for at least 1 seconds
        Thread.sleep(2500);

        clientSocket = connect();
        Assert.assertNull(clientSocket);
        close(clientSocket);
    }

    private String readLine(BufferedReader br) throws Exception {
        String line;
        StringBuffer sb = new StringBuffer();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private Socket connect() throws Throwable {
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", port);
        } catch (Throwable t) {
        }
        return socket;
    }

    private void close(Socket socket) throws Throwable {
        if (socket != null)
            socket.close();
    }
}
