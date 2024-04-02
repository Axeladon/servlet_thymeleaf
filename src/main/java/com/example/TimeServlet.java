package com.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;

    @Override
    public void init() {
        engine = new TemplateEngine();

        JakartaServletWebApplication jswa = JakartaServletWebApplication.buildApplication(this.getServletContext());
        WebApplicationTemplateResolver resolver = new WebApplicationTemplateResolver(jswa);
        resolver.setPrefix ("/template/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");

        String param;

        if (req.getParameterMap().isEmpty()) {
            Optional<Cookie> cookie = Optional.empty();
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                cookie = Arrays.stream(cookies)
                        .filter(c -> "lastTimezone".equals(c.getName()))
                        .findFirst();
            }

            if (!cookie.isPresent()) {
                param = "UTC";
            } else {
                param = cookie.get().getValue();
            }
        } else {
            param = Util.getAndEncodeRequestParam(req.getParameter("timezone"));
            resp.addCookie(new Cookie("lastTimezone", param));
        }

        String formattedTime = getFormattedTime(param);
        Context data = new Context(req.getLocale(), Map.of("formattedTime", formattedTime));
        engine.process("timePage", data, resp.getWriter());
        resp.getWriter().close();
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
