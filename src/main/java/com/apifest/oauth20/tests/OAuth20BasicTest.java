/*
 * Copyright 2013-2014, ApiFest project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apifest.oauth20.tests;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apifest.BasicTest;

/**
 *
 * @author Rossitsa Borissova
 *
 */
public class OAuth20BasicTest extends BasicTest {

    public static final String RESPONSE_TYPE_CODE = "code";
    public static final String DEFAULT_REDIRECT_URI = "http://127.0.0.1:8080";
    public static final String DEFAULT_SCOPE = "basic";
    public static final String DEFAULT_DESCRIPTION = "some descr";
    public static final String DEFAULT_CLIENT_NAME = "default_client";
    public static final String DEFAULT_CLIENT_DESCR = "test description";
    int DEFAULT_CC_EXPIRES_IN = 1800;
    int DEFAULT_PASS_EXPIRES_IN = 900;

    public static final String CLIENT_ID_PARAM = "client_id";
    public static final String REDIRECT_URI_PARAM = "redirect_uri";
    public static final String RESPONSE_TYPE_PARAM = "response_type";
    public static final String SCOPE_PARAM = "scope";
    public static final String GRANT_TYPE_PARAM = "grant_type";
    public static final String CODE_PARAM = "code";

    public static final String REFRESH_TOKEN_PARAM = "refresh_token";
    public static final String ACCESS_TOKEN_PARAM = "access_token";

    public static final String GRANT_TYPE_PASSWORD = "password";
    public static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
    public static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    public static final String GRANT_TYPE_AUTH_CODE = "authorization_code";

    public static final String EXPIRES_IN = "expires_in";

    public static final String AUTHORIZE_ENDPOINT = "/oauth20/authorize";
    public static final String TOKEN_ENDPOINT = "/oauth20/token";
    public static final String APPLICATION_ENDPOINT = "/oauth20/application";
    public static final String SCOPE_ENDPOINT = "/oauth20/scope";
    public static final String TOKEN_REVOKE_ENDPOINT = "/oauth20/token/revoke";
    public static final String TOKEN_VALIDATE_ENDPOINT = "/oauth20/token/validate";

    Logger log = LoggerFactory.getLogger(OAuth20BasicTest.class);

    public void registerDefaultScope() {
        registerNewScope(DEFAULT_SCOPE, DEFAULT_CLIENT_DESCR, DEFAULT_CC_EXPIRES_IN, DEFAULT_PASS_EXPIRES_IN);
    }

    public String registerDefaultClient() {
        return registerNewClient(DEFAULT_CLIENT_NAME, DEFAULT_SCOPE, DEFAULT_REDIRECT_URI);
    }

    public String obtainAuthCode(String clientId, String uri) {
        return obtainAuthCode(clientId, uri, RESPONSE_TYPE_CODE, DEFAULT_SCOPE);
    }

    public String obtainAuthCode(String clientId, String uri, String responseType) {
        return obtainAuthCode(clientId, uri, responseType, DEFAULT_SCOPE);
    }

    // GET /oauth20/authorize?client_id={clientId}&redirect_uri={uri}&response_type=code
    public String obtainAuthCode(String clientId, String uri, String responseType, String scope) {
        URIBuilder builder = null;
        String authCode = null;
        try {
            builder = new URIBuilder(baseOAuth20Uri + AUTHORIZE_ENDPOINT);
            builder.setParameter(CLIENT_ID_PARAM, clientId);
            builder.setParameter(REDIRECT_URI_PARAM, uri);
            builder.setParameter(RESPONSE_TYPE_PARAM, responseType);
            builder.setParameter(SCOPE_PARAM, scope);
            builder.setParameter("user_id", "12345");

            GetMethod get = new GetMethod(builder.build().toString());
            String response = readResponse(get);
            authCode = extractAuthCode(response);
        } catch (IOException e) {
            log.error("cannot obtain auth code", e);
        } catch (URISyntaxException e) {
            log.error("cannot obtain auth code", e);
        }
        return authCode;
    }

    protected String extractAuthCode(String response) {
        String authCode = null;
        try {
            JSONObject json = new JSONObject(response);
            String redirectUri = json.getString(REDIRECT_URI_PARAM);
            if (redirectUri != null) {
                URIBuilder builder = new URIBuilder(redirectUri);
                List<org.apache.http.NameValuePair> params = builder.getQueryParams();
                for (org.apache.http.NameValuePair pair : params) {
                    if (CODE_PARAM.equals(pair.getName())) {
                        authCode = pair.getValue();
                        break;
                    }
                }
            }
            log.info(redirectUri);
        } catch (JSONException e) {
            //log.error("cannot extract auth code", e);
            authCode = response;
        } catch (URISyntaxException e) {
            log.error("cannot extract auth code", e);
        }
        return authCode;
    }

    public String obtainAccessToken(String authCode, String clientId, String uri) {
        return obtainAccessToken(GRANT_TYPE_AUTH_CODE, authCode, clientId, uri);
    }

    // POST /oauth20/token
    // grant_type=authorization_code&redirect_uri=dada&client_id=815424409865735&code=XOPeAaFTDNdHEeRCvbA%23IaN%23pggldgbgYnAxQvVYyyG%23zteYTuxSLcz%3DTWWXTLLxPtuviPLTspxkZgeJKsyxXgZPSAEPWYQuIUCccAeQibfmWdxQiWiezNbhGaKbHlSZzZDZAAQ-ujfUouiDZHdVrlVDMFFJJQo%3DAfJemEPRZmZ-wFTNBb-Rwni%3DaRbKRKLSzWkPLAgw
    public String obtainAccessToken(String grantType, String authCode, String clientId, String uri) {
        PostMethod post = new PostMethod(baseOAuth20Uri + TOKEN_ENDPOINT);
        String accessToken = null;
        String response = null;
        try {
            NameValuePair[] requestBody = { new NameValuePair(GRANT_TYPE_PARAM, grantType),
                    new NameValuePair(CODE_PARAM, authCode), new NameValuePair(REDIRECT_URI_PARAM, uri) };
            post.setRequestHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
            post.setRequestHeader(HttpHeaders.AUTHORIZATION, createBasicAuthorization(clientId));
            post.setRequestBody(requestBody);
            response = readResponse(post);
            log.info(response);
            if (response != null) {
                accessToken = extractAccessToken(response);
            }
        } catch (IOException e) {
            log.error("cannot obtain access token", e);
        }
        return accessToken;
    }

    public String obtainAccessTokenResponse(String grantType, String authCode, String clientId, String uri) {
        PostMethod post = new PostMethod(baseOAuth20Uri + TOKEN_ENDPOINT);
        String response = null;
        try {
            NameValuePair[] requestBody = { new NameValuePair(GRANT_TYPE_PARAM, grantType),
                    new NameValuePair(CODE_PARAM, authCode), new NameValuePair(REDIRECT_URI_PARAM, uri) };
            post.setRequestHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
            post.setRequestHeader(HttpHeaders.AUTHORIZATION, createBasicAuthorization(clientId));
            post.setRequestBody(requestBody);
            response = readResponse(post);
            log.info(response);
        } catch (IOException e) {
            log.error("cannot obtain access token response", e);
        }
        return response;
    }

    public String obtainAccessTokenByRefreshToken(String grantType, String refreshToken, String clientId, String scope) {
        String accessToken = null;
        String response = null;
        try {
            response = obtainAccessTokenByRefreshTokenResponse(grantType, refreshToken, clientId, scope);
            log.info(response);
            if (response != null) {
                JSONObject json = new JSONObject(response);
                if (json.get(ACCESS_TOKEN_PARAM) != null) {
                    accessToken = json.getString(ACCESS_TOKEN_PARAM);
                }
            }
        } catch (JSONException e) {
            log.error("cannot obtain access token by refresh token", e);
            accessToken = response;
        }
        return accessToken;
    }

    public String obtainAccessTokenByRefreshTokenResponse(String grantType, String refreshToken, String clientId,
            String scope) {
        PostMethod post = new PostMethod(baseOAuth20Uri + TOKEN_ENDPOINT);
        String response = null;
        try {

            post.setRequestHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
            post.setRequestHeader(HttpHeaders.AUTHORIZATION, createBasicAuthorization(clientId));
            if (scope != null) {
                NameValuePair[] requestBody = { new NameValuePair(GRANT_TYPE_PARAM, grantType),
                        new NameValuePair(REFRESH_TOKEN_PARAM, refreshToken), new NameValuePair(SCOPE_PARAM, scope) };
                post.setRequestBody(requestBody);
            } else {
                NameValuePair[] requestBody = { new NameValuePair(GRANT_TYPE_PARAM, grantType),
                        new NameValuePair(REFRESH_TOKEN_PARAM, refreshToken) };
                post.setRequestBody(requestBody);
            }
            response = readResponse(post);
            log.info(response);
        } catch (IOException e) {
            log.error("cannot obtain access token by refresh token response", e);
        }
        return response;
    }

    public String obtainClientCredentialsAccessToken(String currentClientId, String scope, boolean addAuthHeader) {
        String response = obtainClientCredentialsAccessTokenResponse(currentClientId, scope, addAuthHeader);
        log.info(response);
        if (response != null) {
            return extractAccessToken(response);
        }
        return null;
    }

    public String obtainClientCredentialsAccessTokenResponse(String currentClientId, String scope, boolean addAuthHeader) {
        PostMethod post = new PostMethod(baseOAuth20Uri + TOKEN_ENDPOINT);
        String response = null;
        try {
            NameValuePair[] requestBody = { new NameValuePair(GRANT_TYPE_PARAM, GRANT_TYPE_CLIENT_CREDENTIALS),
                    new NameValuePair(SCOPE_PARAM, scope) };
            post.setRequestHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
            if (addAuthHeader) {
                post.setRequestHeader(HttpHeaders.AUTHORIZATION, createBasicAuthorization(currentClientId));
            }
            post.setRequestBody(requestBody);
            response = readResponse(post);
        } catch (IOException e) {
            log.error("cannot obtain client credentials acces token response", e);
        }
        return response;
    }

    public String obtainPasswordCredentialsAccessTokenResponse(String currentClientId, String username, String password,
            String scope, boolean addAuthHeader) {
        PostMethod post = new PostMethod(baseOAuth20Uri + TOKEN_ENDPOINT);
        String response = null;
        try {
            NameValuePair[] requestBody = { new NameValuePair(GRANT_TYPE_PARAM, GRANT_TYPE_PASSWORD),
                    new NameValuePair("username", username), new NameValuePair("password", password),
                    new NameValuePair(SCOPE_PARAM, scope) };
            post.setRequestHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
            if (addAuthHeader) {
                post.setRequestHeader(HttpHeaders.AUTHORIZATION, createBasicAuthorization(currentClientId));
            }
            post.setRequestBody(requestBody);
            response = readResponse(post);
            log.info(response);
        } catch (IOException e) {
            log.error("cannot obtain password acces token response", e);
        }
        return response;
    }

    public String registerNewClient(String clientName, String scope, String redirectUri) {
        PostMethod post = new PostMethod(baseOAuth20Uri + APPLICATION_ENDPOINT);
        String response = null;
        try {
            JSONObject json = new JSONObject();
            json.put("name", clientName);
            json.put("description", DEFAULT_DESCRIPTION);
            json.put("scope", scope);
            json.put("redirect_uri", redirectUri);

            String requestBody = json.toString();
            RequestEntity requestEntity = new StringRequestEntity(requestBody, "application/json", "UTF-8");
            post.setRequestHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            post.setRequestEntity(requestEntity);
            response = readResponse(post);
            log.info(response);
        } catch (IOException e) {
            log.error("cannot register new client app", e);
        } catch (JSONException e) {
            log.error("cannot register new client app", e);
        }
        return response;
    }

    public String registerNewClientWithPredefinedClientId(String clientName, String scope, String redirectUri, String clientId, String clientSecret) {
        PostMethod post = new PostMethod(baseOAuth20Uri + APPLICATION_ENDPOINT);
        String response = null;
        try {
            JSONObject json = new JSONObject();
            json.put("name", clientName);
            json.put("description", DEFAULT_DESCRIPTION);
            json.put("scope", scope);
            json.put("redirect_uri", redirectUri);
            json.put("client_id", clientId);
            json.put("client_secret", clientSecret);

            String requestBody = json.toString();
            RequestEntity requestEntity = new StringRequestEntity(requestBody, "application/json", "UTF-8");
            post.setRequestHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            post.setRequestEntity(requestEntity);
            response = readResponse(post);
            log.info(response);
        } catch (IOException e) {
            log.error("cannot register new client app", e);
        } catch (JSONException e) {
            log.error("cannot register new client app", e);
        }
        post.releaseConnection();
        return response;
    }

    public String registerNewScope(String scope, String description, int ccExpiresIn, int passExpiresIn) {
        PostMethod post = new PostMethod(baseOAuth20Uri + SCOPE_ENDPOINT);
        String response = null;
        try {
            JSONObject json = new JSONObject();
            json.put("scope", scope);
            json.put("description", description);
            json.put("cc_expires_in", ccExpiresIn);
            json.put("pass_expires_in", passExpiresIn);
            String requestBody = json.toString();
            RequestEntity requestEntity = new StringRequestEntity(requestBody, "application/json", "UTF-8");
            post.setRequestHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            post.setRequestEntity(requestEntity);
            response = readResponse(post);
            log.info(response);
        } catch (IOException e) {
            log.error("cannot register new scope", e);
        } catch (JSONException e) {
            log.error("cannot register new scope", e);
        }
        return response;
    }

    public String getAllScopes() {
        GetMethod get = new GetMethod(baseOAuth20Uri + SCOPE_ENDPOINT);
        String response = null;
        try {
            response = readResponse(get);
            log.info(response);
        } catch (IOException e) {
            log.error("cannot get all scopes", e);
        }
        return response;
    }

    public String getClientDefaultScope(String currentClientId) {
        URIBuilder builder = null;
        String scope = null;
        try {
            builder = new URIBuilder(baseOAuth20Uri + APPLICATION_ENDPOINT + "/" + currentClientId);
            //builder.setParameter(CLIENT_ID_PARAM, currentClientId);
            GetMethod get = new GetMethod(builder.build().toString());
            String response = readResponse(get);
            scope = extractAccessTokenScope(response);
        } catch (IOException e) {
            log.error("cannot obtain client default scope", e);
        } catch (URISyntaxException e) {
            log.error("cannot obtain client default scope", e);
        }
        return scope;
    }

    public String updateScope(String scope, String description, Integer ccExpiresIn, Integer passExpiresIn) {
        PutMethod put = new PutMethod(baseOAuth20Uri + SCOPE_ENDPOINT + "/" + scope);
        String response = null;
        try {
            JSONObject json = new JSONObject();
            json.put("description", description);
            json.put("cc_expires_in", ccExpiresIn);
            json.put("pass_expires_in", passExpiresIn);
            String requestBody = json.toString();
            RequestEntity requestEntity = new StringRequestEntity(requestBody, "application/json", "UTF-8");
            put.setRequestHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            put.setRequestEntity(requestEntity);
            response = readResponse(put);
            log.info(response);
        } catch (IOException e) {
            log.error("cannot update scope", e);
        } catch (JSONException e) {
            log.error("cannot update scope", e);
        }
        return response;
    }

    public String deleteScope(String scope) {
        DeleteMethod delete = new DeleteMethod(baseOAuth20Uri + SCOPE_ENDPOINT + "/" + scope);
        String response = null;
        try {
            response = readResponse(delete);
            log.info(response);
        } catch (IOException e) {
            log.error("cannot delete scope", e);
        }
        return response;
    }

    public String getAllClientApps() {
        GetMethod get = new GetMethod(baseOAuth20Uri + APPLICATION_ENDPOINT);
        String response = null;
        try {
            response = readResponse(get);
            log.info(response);
        } catch (IOException e) {
            log.error("cannot get all client apps", e);
        }
        return response;
    }

    public String getClientAppById(String clientId) {
        GetMethod get = new GetMethod(baseOAuth20Uri + APPLICATION_ENDPOINT + "/" + clientId);
        String response = null;
        try {
            response = readResponse(get);
            log.info(response);
        } catch (IOException e) {
            log.error("cannot client app", e);
        }
        return response;
    }

    public String updateClientApp(String clientId, String scope, String description, Integer status, String redirectUri) {
        PutMethod put = new PutMethod(baseOAuth20Uri + APPLICATION_ENDPOINT + "/" + clientId);
        String response = null;
        try {
            //put.setRequestHeader(HttpHeaders.AUTHORIZATION, createBasicAuthorization(clientId));
            JSONObject json = new JSONObject();
            json.put("status", status);
            json.put("description", description);
            json.put("redirect_uri", redirectUri);
            String requestBody = json.toString();
            RequestEntity requestEntity = new StringRequestEntity(requestBody, "application/json", "UTF-8");
            put.setRequestHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            put.setRequestEntity(requestEntity);
            response = readResponse(put);
            log.info(response);
        } catch (IOException e) {
            log.error("cannot update client app", e);
        } catch (JSONException e) {
            log.error("cannot update client app", e);
        }
        return response;
    }

    public boolean revokeAccessToken(String token, String currentClientId) {
        PostMethod post = new PostMethod(baseOAuth20Uri + TOKEN_REVOKE_ENDPOINT);
        String response = null;
        boolean revoked = false;
        try {
            JSONObject reqJson = new JSONObject();
            reqJson.put(ACCESS_TOKEN_PARAM, token);
            String requestBody = reqJson.toString();
            RequestEntity requestEntity = new StringRequestEntity(requestBody, "application/json", "UTF-8");
            post.setRequestHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            post.setRequestHeader(HttpHeaders.AUTHORIZATION, createBasicAuthorization(currentClientId));
            post.setRequestEntity(requestEntity);
            response = readResponse(post);
            log.info(response);
            if (response != null) {
                JSONObject json;
                try {
                    json = new JSONObject(response);
                    revoked = json.getBoolean("revoked");
                } catch (JSONException e) {
                    log.error("cannot revoke access token", e);
                }
            }
        } catch (IOException e) {
            log.error("cannot revoke access token", e);
        } catch (JSONException e) {
            log.error("cannot revoke access token", e);
        }
        return revoked;
    }

    public String validateToken(String token) {
        URIBuilder builder = null;
        String response = null;
        try {
            builder = new URIBuilder(baseOAuth20Uri + TOKEN_VALIDATE_ENDPOINT);
            GetMethod get = new GetMethod(builder.build().toString());
            get.setQueryString("token=" + token);
            response = readResponse(get);
        } catch (IOException e) {
            log.error("cannot obtain client default scope", e);
        } catch (URISyntaxException e) {
            log.error("cannot obtain client default scope", e);
        }
        return response;
    }

    protected String createBasicAuthorization(String newClientId) {
        String value = newClientId + ":" + clientSecret;
        String encodedValue = new String(Base64.encodeBase64(value.getBytes(Charset.forName("UTF-8"))));
        return "Basic " + encodedValue;
    }

    protected String extractClientId(String json) {
        String clientId = null;
        try {
            JSONObject jsonObj = new JSONObject(json);
            clientId = jsonObj.getString("client_id");
        } catch (JSONException e) {
            // do not log
        }
        return clientId;
    }

    protected String extractClientSecret(String json) {
        String clientSecret = null;
        try {
            JSONObject jsonObj = new JSONObject(json);
            clientSecret = jsonObj.getString("client_secret");
        } catch (JSONException e) {
            // do not log
        }
        return clientSecret;
    }

    protected String extractRefreshToken(String json) {
        String refreshToken = null;
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(json);
            if (jsonObj.get(REFRESH_TOKEN_PARAM) != null) {
                refreshToken = jsonObj.getString(REFRESH_TOKEN_PARAM);
            }
        } catch (JSONException e) {
            // do not log
        }
        return refreshToken;
    }

    protected String extractAccessToken(String json) {
        String accessToken = json;
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(json);
            if (jsonObj.get(ACCESS_TOKEN_PARAM) != null) {
                accessToken = jsonObj.getString(ACCESS_TOKEN_PARAM);
            }
        } catch (JSONException e) {
            // do not log
        }
        return accessToken;
    }

    protected String extractAccessTokenScope(String json) {
        String accessTokenScope = json;
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(json);
            if (jsonObj.get(SCOPE_PARAM) != null) {
                accessTokenScope = jsonObj.getString(SCOPE_PARAM);
            }
        } catch (JSONException e) {
            // do not log
        }
        return accessTokenScope;
    }

    protected String extractAccessTokenExpiresIn(String json) {
        String expiresIn = json;
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(json);
            if (jsonObj.get(EXPIRES_IN) != null) {
                expiresIn = jsonObj.getString(EXPIRES_IN);
            }
        } catch (JSONException e) {
            // do not log
        }
        return expiresIn;
    }

    protected String extractDescription(String json) {
        String clientId = null;
        try {
            JSONObject jsonObj = new JSONObject(json);
            clientId = jsonObj.getString("description");
        } catch (JSONException e) {
            // do not log
        }
        return clientId;
    }
}
