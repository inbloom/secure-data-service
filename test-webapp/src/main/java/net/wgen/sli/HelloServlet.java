package net.wgen.sli;

/**
 * Hello world!
 *
 */
import java.io.*;

import javax.servlet.http.*;
import javax.servlet.*;

public class HelloServlet extends HttpServlet {
  public void doGet (HttpServletRequest req,
                     HttpServletResponse res)
    throws ServletException, IOException
  {
    PrintWriter out = res.getWriter();

    out.println("<html><head><title>Hello World</title></head><body>");
    out.println("<p>Hello, world!<p>");
    out.println("<p><a href=\"j_spring_security_logout\">Logout</a>");
    out.println("</body></html>");
    out.close();
  }
}

