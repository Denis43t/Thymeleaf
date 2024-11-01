package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;

import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;


@WebServlet("/time")
public class Demo extends HttpServlet {
    ZonedDateTime date;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private TemplateEngine engine;


    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int checkOnPresentCookies = 0;
        resp.setContentType("text/html");

        String timezone = req.getParameter("timezone");
        Cookie[] cookies = req.getCookies();
        if (cookies != null && timezone == null) {
            checkOnPresentCookies = 1;
        }

        if (checkOnPresentCookies == 0) {
            if (timezone == null) {
                date = ZonedDateTime.now();
                timezone = String.valueOf(date.getOffset());
            } else if (timezone.contains("-")) {
                timezone = timezone.replaceAll("UTC", "");
                resp.addCookie(new Cookie("lastTimezone", timezone));
                ZoneOffset zoneOffSet = ZoneOffset.of(timezone);
                date = OffsetDateTime.now(zoneOffSet).toZonedDateTime();
            } else {
                timezone = timezone.replaceAll("UTC ", "");
                timezone = "+".concat(timezone);

                resp.addCookie(new Cookie("lastTimezone", timezone));

                ZoneOffset zoneOffSet = ZoneOffset.of(timezone);
                date = OffsetDateTime.now(zoneOffSet).toZonedDateTime();
            }
        } else {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("lastTimezone")) {
                    timezone = cookie.getValue();
                    ZoneOffset zoneOffSet = ZoneOffset.of(timezone);
                    date = OffsetDateTime.now(zoneOffSet).toZonedDateTime();
                }
            }
        }

        Context simpleContext = new Context(
                req.getLocale(),
                Map.of("queryParams", date.format(formatter) + " UTC" + timezone)
        );

        engine.process("test", simpleContext, resp.getWriter());
        timezone=null;
        resp.getWriter().close();
    }
}