package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;

import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/test")
public class Demo extends HttpServlet {
    private TemplateEngine engine;

    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("./templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        Map<String, String[]> parameterMap = req.getParameterMap();

        Map<String, Object> params = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> keyValue : parameterMap.entrySet()) {
            params.put(keyValue.getKey(), keyValue.getValue()[0]);
        }

        Context simpleContext = new Context(
                req.getLocale(),
                Map.of("queryParams", params)
        );

        engine.process("test", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }
}