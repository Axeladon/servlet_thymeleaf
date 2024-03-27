package com.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        String param;
        Map<String, String[]> parameters = req.getParameterMap();
        if (parameters.isEmpty()) {
            param = "UTC";
        } else {
            param = Util.getAndEncodeRequestParam(req.getParameter("timezone"));
        }
        String formattedTime = getFormattedTime(param);
        showWebPage(out, formattedTime);
    }

    private void showWebPage(PrintWriter out, String formattedTime) {
        out.println("<html>");
        out.println("<head><title>Current Time</title></head>");
        out.println("<body>");
        out.println("<p>Time: " + formattedTime + "</p>");
        out.println("</body>");
        out.println("</html>");
    }

    private String getFormattedTime(String timeZoneStr) {
        int timeZoneCode = 0;
        if (!timeZoneStr.equals("UTC")) {
            timeZoneCode = Integer.parseInt(timeZoneStr.replace("UTC", ""));
        }

        ZoneOffset zoneOffset = ZoneOffset.ofHours(timeZoneCode);
        ZoneId zoneId = ZoneId.ofOffset("UTC", zoneOffset);
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'");

        if (timeZoneCode == 0) {
            return zonedDateTime.format(formatter);
        } else {
            String sign = "";
            if (timeZoneCode > 0) {
                sign = "+";
            }
            return zonedDateTime.format(formatter) + sign + timeZoneCode;
        }
    }


}
