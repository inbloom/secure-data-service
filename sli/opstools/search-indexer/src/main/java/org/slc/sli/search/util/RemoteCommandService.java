package org.slc.sli.search.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slc.sli.search.process.Extractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RemoteCommandService implements ApplicationContextAware, Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final int DEFAULT_PORT = 10024;
    private int port = DEFAULT_PORT;

    private boolean stopRemoteCommandService;

    // server socket
    private ServerSocket serverSocket;

    // because we just want to shut down application
    private ClassPathXmlApplicationContext context;

    // Extractor object to run as batch program
    private Extractor extractor;

    // thread executor
    private static final ScheduledExecutorService scheduledService = Executors.newSingleThreadScheduledExecutor();

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
        thread.run();
    }

    // Thread run
    public void run() {
        // make infinite loop
        try {
            while (this.stopRemoteCommandService == false) {
                listen();
            }

        } catch (Exception e) {
            logger.error("Error detected stopping Remote Command Service...", e);
        } finally {
            scheduledService.shutdownNow();
            if (this.stopRemoteCommandService) {
                this.context.close();
            }
        }
    }

    // Main function, Listen server socket
    private void listen() throws Exception{

        try {
            // Wait for client to connect
            Socket socket = this.serverSocket.accept();

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Commands command = null;
            // read Input String
            String inputCommand = in.readLine();
            if (inputCommand != null) {
                StringTokenizer st = new StringTokenizer(inputCommand);
                int tokenCount = 0;

                // build Command

                while (st.hasMoreTokens()) {
                    String token = st.nextToken();

                    // this is the first read token.
                    // it means command
                    if (tokenCount == 0) {
                        if (token.toLowerCase().equals("extract")) {
                            command = Commands.Extract;
                        } else if (token.toLowerCase().equals("stop")) {
                            command = Commands.Stop;
                        } else {
                            command = Commands.Help;
                        }
                    } else {
                        // token is an option for a command
                        command.setOption(token.toLowerCase());
                    }
                    tokenCount++;
                }
            }

            // execute command
            switch (command) {
                case Extract:
                    logger.info("Remote Service received Extract command");
                    if (command.getOption().equals("sync")) {
                        this.extractor.execute();
                    } else {
                        scheduledService.schedule(new Runnable() {
                            public void run() {
                                extractor.execute();
                            }
                        }, 0, TimeUnit.SECONDS);
                    }
                    command.setReply("sent extract command");
                    break;
                case Stop:
                    
                    int delay = 5;
                    try {
                        String option = command.getOption();
                        if (option != null && !option.isEmpty()) {
                            delay = Integer.parseInt(option);
                        }
                    } finally {
                        logger.info("Remote Service received Stop command, shutting down in "+delay+" second(s)");
                        scheduledService.schedule(new Runnable() {
                            public void run() {
                                commandShutdown();
                            }
                        }, delay, TimeUnit.SECONDS);
                    }
                    command.setReply("Shutting down in "+delay+ " second(s)\n");
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
            socket.close();
        } catch (SocketException e) {
            // SocketException is thrown by calling close while socket is blocked by accept.
            // this is expected exception because search-indexer is about shutting down.
        }
    }

    private void commandShutdown() {
        try {
            logger.info("Shutting down search-indexer");
            this.stopRemoteCommandService = true;
            this.serverSocket.close();
        } catch (IOException e) {
            // something went wrong.
            logger.error("Exception while shutting down socket", e);
        }
    }

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = (ClassPathXmlApplicationContext) context;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setExtractor(Extractor extractor) {
        this.extractor = extractor;
    }

    private enum Commands {
        Extract, Stop, Help;

        private String reply;
        private String option;

        public String getReply() {
            return this.reply;
        }

        public void setReply(String reply) {
            this.reply = reply;
        }

        public void setOption(String option) {
            this.option = option;
        }

        public String getOption() {
            return this.option;
        }
    }

}
