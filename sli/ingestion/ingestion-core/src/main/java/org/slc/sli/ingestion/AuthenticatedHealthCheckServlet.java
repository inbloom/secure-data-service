package org.slc.sli.ingestion;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.yammer.metrics.reporting.HealthCheckServlet;

public class AuthenticatedHealthCheckServlet extends HealthCheckServlet {

    public String healthCheckUser;
    public String healthCheckPassword;

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        healthCheckUser = config.getInitParameter("healthCheckUser");
        healthCheckPassword = config.getInitParameter("healthCheckPassword");
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean askForAuthorization = false;

        //will contain "Basic Ym9iOnNlY3JldA=="
        String header = request.getHeader("Authorization");
        if (header != null) {
            //always wise to assert your assumptions
            assert header.substring(0, 6).equals("Basic ");
            //will contain "Ym9iOnNlY3JldA=="
            String basicAuthEncoded = header.substring(6);
            //will contain "bob:secret"
            String basicAuthAsString = new String(
                    new Base64().decode(basicAuthEncoded.getBytes()));
            String[] stringSplit = basicAuthAsString.split(":");
            if ( (stringSplit.length == 2)
                    && stringSplit[0].equals(healthCheckUser)
                    && stringSplit[1].equals(healthCheckPassword))
                super.doGet(request, response);
            else
                askForAuthorization = true;
        }
        else
            askForAuthorization = true;


        if (askForAuthorization) {
            response.setHeader("WWW-Authenticate","Basic realm=\"SLI Realm\"");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "");
        }

    }
}