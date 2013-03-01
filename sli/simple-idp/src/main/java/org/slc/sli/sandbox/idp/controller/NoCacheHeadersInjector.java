package org.slc.sli.sandbox.idp.controller;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

/**
 * Injects instructions to not cache pages
 * 
 * @author dkornishev
 * 
 */
@Component
public class NoCacheHeadersInjector implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // nothing to do.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate"); // HTTP 1.1
        resp.setHeader("Pragma", "no-cache"); // HTTP 1.0
        resp.setDateHeader("Expires", -1); // prevents caching at the proxy server

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // nothing to do 
    }
}
