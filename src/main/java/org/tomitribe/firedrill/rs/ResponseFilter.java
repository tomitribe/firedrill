/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tomitribe.firedrill.rs;

import org.apache.catalina.connector.Request;
import org.tomitribe.firedrill.ScenarioExecutor;
import org.tomitribe.firedrill.Scenarios;
import org.tomitribe.firedrill.ScenariosXml;
import org.tomitribe.util.PrintString;
import org.tomitribe.util.collect.ObjectMap;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@WebFilter("/*")
public class ResponseFilter implements Filter {

    @Inject
    private BeanManager beanManager;

    @Inject
    private ResponseScenarios scenarios;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        final HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        if (is(httpServletRequest, "GET", "/firedrill/scenarios")) {
            downloadScenarios(httpServletRequest, httpServletResponse);
            return;
        }

        if (is(httpServletRequest, "POST", "/firedrill/scenarios")) {
            addScenarios(httpServletRequest, httpServletResponse);
            return;
        }

        if (is(httpServletRequest, "PUT", "/firedrill/scenarios")) {
            setScenarios(httpServletRequest, httpServletResponse);
            return;
        }

        final Map<String, String> context = new HashMap<>();

        try {
            Field request = servletRequest.getClass().getDeclaredField("request");
            request.setAccessible(true);
            final Object o = request.get(servletRequest);
            final RequestData requestData = new RequestData((Request) o);
            final ObjectMap map = new ObjectMap(requestData);
            for (final Map.Entry<String, Object> entry : map.entrySet()) {
                final String value = String.valueOf(entry.getValue()).replace("null", "");
                context.put(entry.getKey(), value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // We set ScenarioExecutor as a Function in what is effectively a thread local
        // The code being called may or may not execute it.
        final Function<Response.ResponseBuilder, Response.ResponseBuilder> function =
                new ScenarioExecutor<>(beanManager, this.scenarios.getScenarios(), context);

        // This @RequestScoped bean helps us avoid a thread local
        final ResponseFunction reference = getReference(ResponseFunction.class, beanManager);

        // Ok, the code has a way to get our Function.  Let's hope they use it.
        reference.setFunction(function);

        // Go forth and process
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public boolean is(HttpServletRequest httpServletRequest, final String method, final String s) {
        final String s1 = httpServletRequest.getContextPath() + s;
        return httpServletRequest.getRequestURI().equals(s1) && method.equalsIgnoreCase(httpServletRequest.getMethod());
    }

    @Override
    public void destroy() {
    }

    public static <T> T getReference(Class<T> type, final BeanManager beanManager, Annotation... qualifiers) {

        final Set<Bean<?>> beans = beanManager.getBeans(type, qualifiers);

        if (beans == null || beans.isEmpty()) {
            throw new IllegalStateException("Could not find beans for Type=" + type
                    + " and qualifiers:" + Arrays.toString(qualifiers));
        }

        final Bean<?> bean = beanManager.resolve(beans);

        final CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);

        return (T) beanManager.getReference(bean, type, creationalContext);
    }

    private void downloadScenarios(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/xml");
            final ServletOutputStream outputStream = response.getOutputStream();
            final ScenariosXml scenariosXml = new ScenariosXml();
            scenariosXml.marshal(scenarios.getScenarios(), outputStream);
        } catch (JAXBException e) {
            final PrintString printString = new PrintString();
            e.printStackTrace(printString);
            response.setContentType("text/plain");
            response.getOutputStream().write(printString.toByteArray());
        }
    }

    private void addScenarios(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            final ScenariosXml scenariosXml = new ScenariosXml();
            final Scenarios<Response.ResponseBuilder> scenarios = scenariosXml.unmarshal(request.getInputStream());
            this.scenarios.getScenarios().add(scenarios);
        } catch (JAXBException e) {
            final PrintString printString = new PrintString();
            e.printStackTrace(printString);
            response.setContentType("text/plain");
            response.getOutputStream().write(printString.toByteArray());
        }
    }

    private void setScenarios(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            final ScenariosXml scenariosXml = new ScenariosXml();
            final Scenarios<Response.ResponseBuilder> scenarios = scenariosXml.unmarshal(request.getInputStream());
            this.scenarios.getScenarios().set(scenarios);
        } catch (JAXBException e) {
            final PrintString printString = new PrintString();
            e.printStackTrace(printString);
            response.setContentType("text/plain");
            response.getOutputStream().write(printString.toByteArray());
        }
    }

}
