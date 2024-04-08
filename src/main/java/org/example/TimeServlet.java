package org.example;
import jakarta.servlet.http.Cookie;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;

    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

        JakartaServletWebApplication jswa =
                JakartaServletWebApplication.buildApplication(this.getServletContext());
        WebApplicationTemplateResolver
                resolver = new WebApplicationTemplateResolver(jswa);

        resolver.setPrefix("/WEB-INF/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss zzz").withZone(ZoneId.of("UTC"));
        Date actualDate = new Date();

        String timezoneParam = req.getParameter("timezone");

        String lastTimezone = null;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("lastTimezone")) {
                    lastTimezone = cookie.getValue();
                }
            }
        }

        if (timezoneParam != null && !timezoneParam.isEmpty()) {
            timezoneParam = timezoneParam.replace(" ", "+");
            dateFormat = dateFormat.withZone(ZoneId.of(timezoneParam));
        }

        if (lastTimezone != null && !lastTimezone.isEmpty()) {
            dateFormat = dateFormat.withZone(ZoneId.of(lastTimezone));
        }


        resp.addCookie(new Cookie("lastTimezone", timezoneParam));

        String formattedTime = dateFormat.format(actualDate.toInstant());

        Context simpleContext = new Context(req.getLocale(),
                Map.of("time", formattedTime));

        engine.process("time", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }
}
