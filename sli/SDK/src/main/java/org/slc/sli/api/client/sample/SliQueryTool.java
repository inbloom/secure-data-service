package org.slc.sli.api.client.sample;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.jersey.api.client.ClientResponse;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.EntityCollection;
import org.slc.sli.api.client.EntityType;
import org.slc.sli.api.client.Link;
import org.slc.sli.api.client.SLIClient;
import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.api.client.impl.BasicQuery;
import org.slc.sli.api.client.impl.BasicQuery.Builder;

/**
 * A sample application that utilizes the SLI SDK to connect to a SLI API ReSTful server,
 * logs the user into their IDP realm, and executes a query against the API.
 * 
 * @author asaarela
 */
public class SliQueryTool {
    
    @SuppressWarnings("static-access")
    protected static Options setCommandLineOptions() {
        Options options = new Options();
        
        Option option = OptionBuilder.hasArg().isRequired().withArgName("username")
                .withDescription("The username to log in as").withLongOpt("user").create('u');
        options.addOption(option);
        
        option = OptionBuilder.hasArg().isRequired(false).withArgName("host").withDescription("SLI API Hostname")
                .withLongOpt("host").create('h');
        options.addOption(option);
        
        option = OptionBuilder.hasArg().isRequired(false).withArgName("port").withType(Integer.class)
                .withDescription("SLI API port number").withLongOpt("port").create('p');
        options.addOption(option);
        
        option = OptionBuilder.hasArg().isRequired(false).withArgName("realm").withDescription("Identity realm name")
                .withLongOpt("realm").create('r');
        options.addOption(option);
        
        return options;
    }
    
    private SLIClient client;
    
    protected int showMenu(String title, String[] items) {
        System.err.println("\n" + title);
        System.err.println("\n");
        
        int i = 0;
        for (String item : items) {
            System.err.println(++i + ".) " + item);
        }
        System.err.print("> ");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String line = "";
        try {
            line = bufferedReader.readLine();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        int val = Integer.parseInt(line);
        
        if (val < 0 || val > i) {
            showMenu(title, items);
        }
        
        return val;
    }
    
    public int loginMenu(CommandLine cmdLine) throws MalformedURLException, URISyntaxException {
        String[] menuItems = { "Login", "Quit" };
        int rval = showMenu("Login", menuItems);
        
        if (rval == 1) {
            // Connect to local host.
            String usr = cmdLine.getOptionValue('u');
            String pwd = "";
            
            Console c = System.console();
            if (c != null) {
                pwd = new String(c.readPassword("Password >"));
            } else {
                System.err.print("Password >");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                try {
                    pwd = bufferedReader.readLine();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
            BasicClient.Builder builder = BasicClient.Builder.create().user(usr).password(pwd);
            
            if (cmdLine.hasOption('h')) {
                builder.host(cmdLine.getOptionValue('h'));
            }
            if (cmdLine.hasOption('p')) {
                String portStr = cmdLine.getOptionValue('p');
                builder.port(Integer.parseInt(portStr));
            }
            if (cmdLine.hasOption('r')) {
                builder.realm(cmdLine.getOptionValue('r'));
            }
            
            Logger.getLogger("org.apache.http").setLevel(Level.SEVERE);
            
            client = builder.build();
            
            homeMenu();
            
        } else {
            System.exit(0);
        }
        
        return 0;
    }
    
    public void homeMenu() throws MalformedURLException, URISyntaxException {
        
        while (true) {
            EntityCollection collection = new EntityCollection();
            ClientResponse response = client.read(collection, EntityType.HOME, BasicQuery.FULL_ENTITIES_QUERY);
            
            if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
                System.err.println(response.getEntity(String.class));
                System.exit(response.getStatus());
            }
            
            showLinks(0, collection.get(0).getLinks());
            
            selectLink(collection.get(0).getLinks());
        }
        
    }
    
    private int showLinks(int start, List<Link> links) {
        for (Link entry : links) {
            System.err.println("\t" + ++start + ".) rel:" + entry.getLinkName() + "     href:"
                    + entry.getResourceURL().toString());
        }
        return start;
    }
    
    private void selectLink(List<Link> allLinks) throws MalformedURLException, URISyntaxException {
        int next = allLinks.size();
        System.err.println(++next + ".) Home");
        System.err.println(++next + ".) Goto Resource");
        System.err.println(++next + ".) Quit");
        
        System.err.print("> ");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String line = "";
        try {
            line = bufferedReader.readLine();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        int idx = Integer.parseInt(line);
        
        if (idx == next) {
            System.exit(0);
        } else if (idx == next - 1) {
            System.err.print("Resource URL> ");
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String url = "";
            try {
                url = bufferedReader.readLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            if (!url.isEmpty()) {
                EntityCollection newCollection = new EntityCollection();
                ClientResponse response = client.getResource(newCollection, new URL(url),
                        BasicQuery.FULL_ENTITIES_QUERY);
                if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
                    System.err.println(response.getEntity(String.class));
                    return;
                }
                showEntities(newCollection);
            }
            
        }
        
        if (idx == next - 2 || idx >= next) {
            return;
        }
        
        followLinkMenu(allLinks.get(idx - 1));
    }
    
    protected void followLinkMenu(Link link) throws MalformedURLException, URISyntaxException {
        
        List<String> options = new LinkedList<String>();
        
        options.add("Follow Link");
        options.add("Set Filter");
        options.add("Set Sort Field");
        options.add("Set Sort Order");
        options.add("Set Start Index");
        options.add("Set Max Results");
        options.add("Home");
        options.add("Quit");
        
        Builder builder = BasicQuery.Builder.create().fullEntities();
        String tmp = "?";
        
        while (true) {
            int idx = showMenu("Link: " + link.getLinkName() + "(" + link.getResourceURL().toString() + ")" + tmp,
                    options.toArray(new String[0]));
            
            if (idx == options.size() - 1) {
                return;
            } else if (idx == options.size()) {
                System.exit(0);
            } else if (idx == 1) {
                // Follow the link, display the results.
                EntityCollection results = new EntityCollection();
                ClientResponse response = client.getResource(results, link.getResourceURL(), builder.build());
                
                if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
                    System.err.println("Failed to follow link (" + link.getLinkName() + "): "
                            + response.getEntity(String.class));
                    return;
                } else {
                    showEntities(results);
                }
                break;
                
            } else if (idx == 2) {
                System.err.print("Field > ");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                try {
                    String queryField = bufferedReader.readLine();
                    System.err.print("Value > ");
                    String value = bufferedReader.readLine();
                    builder.filterEqual(queryField, value);
                    tmp += "queryField=" + value + "&";
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (idx == 3) {
                System.err.print("Sort on > ");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                try {
                    String sortField = bufferedReader.readLine();
                    builder.sortBy(sortField);
                    tmp += "sort-by=" + sortField + "&";
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (idx == 4) {
                System.err.print("1. Ascending, 2. Descending > ");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                try {
                    int order = Integer.parseInt(bufferedReader.readLine());
                    
                    if (order == 1) {
                        builder.sortAscending();
                    } else {
                        builder.sortDescending();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (idx == 5) {
                System.err.print("Start Index > ");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                try {
                    int startIndex = Integer.parseInt(bufferedReader.readLine());
                    builder.startIndex(startIndex);
                    tmp += "start-index=" + startIndex + "&";
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (idx == 6) {
                System.err.print("Max Results > ");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                try {
                    int maxResults = Integer.parseInt(bufferedReader.readLine());
                    builder.maxResults(maxResults);
                    tmp += "max-results=" + maxResults + "&";
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * @param results
     */
    private void showEntities(EntityCollection collection) throws MalformedURLException, URISyntaxException {
        
        List<Link> allLinks = new LinkedList<Link>();
        int count = 0;
        
        for (int i = 0; i < collection.size(); ++i) {
            
            Entity entityEntry = collection.get(i);
            
            System.err.println("Entity type:" + entityEntry.getEntityType().toString());
            
            System.err.println("Attributes: {");
            printMapOfMaps(1, entityEntry.getData());
            System.err.println("}");
            
            allLinks.addAll(entityEntry.getLinks());
            showLinks(count, entityEntry.getLinks());
            count = count + entityEntry.getLinks().size();
        }
        selectLink(allLinks);
    }
    
    /**
     * @param args
     */
    public static void main(final String[] args) throws Exception {
        
        CommandLine cmdLine = null;
        
        try {
            PosixParser parser = new PosixParser();
            cmdLine = parser.parse(setCommandLineOptions(), args);
        } catch (MissingOptionException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter
            .printHelp(
                    80,
                    " ",
                    "SliQueryTool\n\nSample application for the SLI SDK."
                            + " This application logs the provided teacher user into the SLI API and performs a series of queries.",
                            setCommandLineOptions(), "", true);
            return;
        }
        
        SliQueryTool tool = new SliQueryTool();
        
        tool.loginMenu(cmdLine);
    }
    
    
    private static void printMapOfMaps(int depth, final Map<String, Object> map) {
        
        if (map == null) {
            return;
        }
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            
            if (entry.getKey().equals(Entity.LINKS_KEY)) {
                continue;
            }
            
            for (int i = 0; i < depth; ++i) {
                System.err.print("\t");
            }
            
            System.err.print(entry.getKey() + "=");
            
            Object obj = entry.getValue();
            if (obj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> tmp = (Map<String, Object>) obj;
                
                System.err.println("{");
                for (int i = 0; i < depth; ++i) {
                    System.err.print("\t");
                }
                
                printMapOfMaps(depth + 1, tmp);
                System.err.println("}");
            }
            System.err.print((obj != null ? obj.toString() : "null") + "\n ");
        }
    }
}
