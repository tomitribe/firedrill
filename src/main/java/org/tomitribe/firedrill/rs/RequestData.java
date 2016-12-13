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

public class RequestData {

    private final String servicePath;
    private final String uri;
    private final Integer requestBytes;
    private final String contentType;
    private final String httpMethod;
    private final String serviceName;
    private final String protocol;
    private final String protocolVersion;
    private final String authResult;
    private final String authErrorCode;
    private final String username;
    private final String path;
    private final String grantType;
    private final String applicationName;
    private final String clientIp;
    private final String clientId;
//    private final String serverIp;
//    private final String serverName;
    private final String authType;
//    private final String datacenter;

    public RequestData(final Request request) {
        final String[] protocolParts = request.getProtocol().split("/");

        if (protocolParts.length >= 1) {
            this.protocol = (protocolParts[0]);
        } else {
            this.protocol = null;
        }

        if (protocolParts.length >= 2) {
            this.protocolVersion = (protocolParts[1]);
        } else {
            this.protocolVersion = null;
        }

        this.httpMethod = (request.getMethod());

        this.clientIp = (request.getRemoteAddr());
        this.contentType = (request.getContentType());
        this.path = (request.getContextPath());
        final int contentLength = request.getContentLength();
        this.requestBytes = (contentLength < 0 ? 0 : contentLength);
        this.applicationName = (request.getContext() != null ? request.getContext().getName() : null);
        this.servicePath = (request.getRequestURI());
        this.serviceName = (request.getMethod().toUpperCase() + " " + request.getRequestURI());
        this.uri = (request.getRequestURI());
        this.authType = (request.getAuthType());

        String username = null;
        if (username == null) {
            final Object userName = request.getAttribute("username");
            username = (userName == null ? null : userName.toString());
        }

        if (username == null && request.getPrincipal() != null) {
            username = (request.getPrincipal().getName());
        }

        this.username = username;

        {
            final Object clientId = request.getAttribute("client_id");
            this.clientId = (clientId == null ? null : clientId.toString());
        }

        {
            final Object grantType = request.getAttribute("grant_type");
            this.grantType = (grantType == null ? null : grantType.toString());
        }

        {
            final Object authResult = request.getAttribute("auth_result");
            this.authResult = (authResult == null ? null : authResult.toString());
        }

        {
            final Object authErrorCode = request.getAttribute("auth_error_code");
            this.authErrorCode = (authErrorCode == null ? null : authErrorCode.toString());
        }
    }

    public String getClientIp() {
        return clientIp;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthType() {
        return authType;
    }

    public String getClientId() {
        return clientId;
    }

    public String getServicePath() {
        return servicePath;
    }

    public String getUri() {
        return uri;
    }

    public String getPath() {
        return path;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public Integer getRequestBytes() {
        return requestBytes;
    }

    public String getContentType() {
        return contentType;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getAuthResult() {
        return authResult;
    }

    public String getAuthErrorCode() {
        return authErrorCode;
    }
}
