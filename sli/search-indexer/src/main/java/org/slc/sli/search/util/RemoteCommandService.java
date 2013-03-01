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
package org.slc.sli.search.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.slc.sli.search.process.Admin;

public class RemoteCommandService implements ApplicationContextAware, Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(RemoteCommandService.class);
    private static final int DEFAULT_PORT = 10024;
    private int port = DEFAULT_PORT;

    private boolean stopRemoteCommandService;

    // server socket
    private ServerSocket serverSocket;

    // because we just want to shut down application
    private ClassPathXmlApplicationContext context;

    // Extractor object to run as batch program
    private Admin admin;

    // thread executor
    private final ScheduledExecutorService scheduledService = Executors.newSingleThreadScheduledExecutor();

    /**
     * initial method run by Spring init-method
     *
     * @throws IOException
     */
    public void init() throws IOException {
        this.serverSocket = new ServerSocket(port);
        // this.serverSocket.setSoTimeout(socketTimeout);

        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    public void destroy() throws Exception {
        scheduledService.shutdownNow();
        closeSocket();
    }

    private void closeSocket() throws Exception {
        if (this.serverSocket != null) {
            this.serverSocket.close();
            this.serverSocket = null;
        }
    }

    // Thread run
    @Override
    public void run() {
        // make loop
        while (this.stopRemoteCommandService == false) {
            try {
                listen();
            } catch (SocketException e) {
                // Most likely Socket is closed because of "stop" command
                LOG.info("There was a socket exception", e);
            } catch (Exception e) {
                LOG.error("Error detected in Remote Command Service...", e);
            }
        }

        scheduledService.shutdownNow();
        if (this.stopRemoteCommandService) {
            if (this.context != null) {
                this.context.close();
            }
        }

    }

    // Main function, Listen server socket
    private void listen() throws Exception {
        Socket socket = null;
        try {
            // Wait for client to connect
            socket = this.serverSocket.accept();

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String inputCommand = in.readLine();
            RemoteCommand command = null;
            if (inputCommand != null) {
                command = new RemoteCommand(inputCommand);
            }

            // if command is null, it is invalid socket request. just ignore it
            if (command != null) {
                List<String> options = null;
                // execute command
                switch (command.getCommands()) {
                    case RELOAD:
                    case EXTRACT:
                        LOG.info("Remote Service received RELOAD/EXTRACT command");
                        options = command.getOptions();
                        if (options.contains("sync")) {
                            this.admin.reloadAll();
                        } else if (!options.isEmpty()) {
                            this.admin.reload(options.get(0));
                        } else {
                            final Admin admin = this.admin;
                            scheduledService.schedule(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        admin.reloadAll();
                                    } catch (Exception e) {
                                        LOG.error("Error detected in Remote Command Service...", e);
                                    }
                                }
                            }, 0, TimeUnit.SECONDS);
                        }
                        command.setReply("sent extract command");
                        break;
                    case RECONCILE:
                        LOG.info("Remote Service received RECONCILE command");
                        options = command.getOptions();
                        if (options.contains("sync")) {
                            this.admin.reconcileAll();
                        } else if (!options.isEmpty()) {
                            this.admin.reconcile(options.get(0));
                        } else {
                            final Admin admin = this.admin;
                            scheduledService.schedule(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        admin.reconcileAll();
                                    } catch (Exception e) {
                                        LOG.error("Error detected in Remote Command Service...", e);
                                    }
                                }
                            }, 0, TimeUnit.SECONDS);
                        }
                        command.setReply("sent extract command");
                        break;
                    case STOP:

                        int delay = 5;
                        try {
                            options = command.getOptions();
                            if (!options.isEmpty()) {
                                delay = Integer.parseInt(options.get(0));
                            }
                        } finally {
                            LOG.info("Remote Service received Stop command, shutting down in " + delay
                                    + " second(s)");
                            scheduledService.schedule(new Runnable() {
                                @Override
                                public void run() {
                                    commandShutdown();
                                }
                            }, delay, TimeUnit.SECONDS);
                        }
                        command.setReply("Shutting down in " + delay + " second(s)\n");
                        break;
                    default:
                        command.setReply("Available Commands:\n" + "extract (start Extractor job)\n"
                                + "extract sync(start Extract job with synchronization)\n"
                                + "stop (stop search-indexer with 5 seconds delay)\n"
                                + "stop # (stop search-indexer with # second(s) delay\n");
                        break;
                }

                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println(command.getReply());
                out.close();
            }
            // SocketException is thrown by calling close while socket is blocked by accept.
            // this is expected exception because search-indexer is about shutting down.
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    private void commandShutdown() {
        try {
            LOG.info("Shutting down search-indexer");
            this.stopRemoteCommandService = true;
            closeSocket();
        } catch (Exception e) {
            // something went wrong.
            LOG.error("Exception while shutting down socket", e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = (ClassPathXmlApplicationContext) context;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    private class RemoteCommand {
        private Command command;
        private String reply;
        private List<String> options;

        public RemoteCommand(String line) {
            String[] commandLine = null;
            try {
                commandLine = line.split("\\s+");
                if (commandLine != null && commandLine.length != 0) {
                    this.command = Command.valueOf(commandLine[0].toUpperCase());
                }
            } catch (Exception e) {
                LOG.info("There was an exception", e);
            }

            if (this.command == null) {
                this.command = Command.HELP;
            }

            if (commandLine == null) {
                options = Collections.emptyList();
            } else {
                // Removes the element at the specified position in this list (optional operation)
                // This prevents throwing exception by removing an element when a List is created
                // from an array.
                options = new ArrayList<String>(Arrays.asList(commandLine));
                if (!options.isEmpty()) {
                    options.remove(0);
                }
            }
        }

        public String getReply() {
            return this.reply;
        }

        public void setReply(String reply) {
            this.reply = reply;
        }

        public List<String> getOptions() {
            return this.options;
        }

        public Command getCommands() {
            return this.command;
        }
    }

    private enum Command {
        RELOAD, EXTRACT, RECONCILE, STOP, HELP, HEALTH;
    }

}
