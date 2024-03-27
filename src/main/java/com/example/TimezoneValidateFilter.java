package com.example;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebFilter(value = "/time")
public class TimezoneValidateFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        Map<String, String[]> parameters = req.getParameterMap();
        if (parameters.isEmpty()) {
            chain.doFilter(req, res);
        } else {
            String timeZoneParam = Util.getAndEncodeRequestParam(parameters.getOrDefault("timezone", new String[]{""})[0]);
            if (timeZoneParam.matches("^UTC[+-]\\d{1,2}$")) {
                int timezoneCode = Integer.parseInt(timeZoneParam.replace("UTC", ""));
                int maxTimezoneCode = 14;
                int minTimezoneCode = -12;
                if (timezoneCode >= minTimezoneCode && timezoneCode <= maxTimezoneCode) {
                    chain.doFilter(req, res);
                } else {
                    printInvalidTimezonePage(res);
                }
            } else {
                printInvalidTimezonePage(res);
            }
        }
    }

    private void printInvalidTimezonePage(HttpServletResponse res) throws IOException {
        res.setContentType("text/html");

        PrintWriter out = res.getWriter();
        out.println("<html>");
        out.println("<head><title>Current Time</title></head>");
        out.println("<body>");
        out.println("<p>Invalid timezone res</p>");
        out.println("</body>");
        out.println("</html>");
        res.setStatus(400);
        out.close();
    }
}
