package org.example;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.zone.ZoneRulesException;

@WebFilter("/time")
public class TimezoneValidateFilter implements Filter {

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String timezoneParam = request.getParameter("timezone");

        if (timezoneParam != null && !timezoneParam.isEmpty()) {
            timezoneParam = timezoneParam.replace(" ", "+");
            try {
                DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss zzz").withZone(ZoneId.of(timezoneParam));
            }catch (ZoneRulesException e){
                response.sendError(400, "Invalid timezone");
                return;
            }
        }

        chain.doFilter(request, response);
    }

}
