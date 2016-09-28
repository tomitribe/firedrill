/*
 * Tomitribe Confidential
 *
 * Copyright Tomitribe Corporation. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.tomitribe.firedrill.rs;

import com.tomitribe.firedrill.ScenarioExecutor;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.lang.annotation.Annotation;
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
        final HttpServletRequest request = (HttpServletRequest) servletRequest;

        final Map<String, String> context = new HashMap<>();

        context.put("username", request.getUserPrincipal().getName());
        context.put("method", request.getMethod());
        context.put("path", request.getRequestURI());
        context.put("applicationName", request.getContextPath());
        context.put("serverName", request.getServerName());

// TODO get these values from somewhere
//        context.put("grantType", grantType);
//        context.put("clientIp", clientIp);
//        context.put("clientId", clientId);
//        context.put("serverIp", serverIp);
//        context.put("authType", authType);
//        context.put("datacenter", datacenter);


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

}
