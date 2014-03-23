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
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
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

    String RESPONSE_TYPE_CODE = "code";
    String REDIRECT_URI = "http://example.com";
    String DEFAULT_SCOPE = "basic";

    Logger log = LoggerFactory.getLogger(OAuth20BasicTest.class);

    public OAuth20BasicTest() {
        // TODO Auto-generated constructor stub
    }

    public String obtainAuthCode(String clientId, String uri) {
        return obtainAuthCode(clientId, uri, RESPONSE_TYPE_CODE, DEFAULT_SCOPE);
    }

    public String obtainAuthCode(String clientId, String uri, String responseType) {
        return obtainAuthCode(clientId, uri, responseType, DEFAULT_SCOPE);
    }

    //GET /oauth20/authorize?client_id={clientId}&redirect_uri={uri}&response_type=code
    public String obtainAuthCode(String clientId, String uri, String responseType, String scope) {
        URIBuilder builder = null;
        String authCode = null;
        try {
            builder = new URIBuilder(baseOAuth20Uri + "/oauth20/authorize");
            builder.setParameter("client_id", clientId);
            builder.setParameter("redirect_uri", uri);
            builder.setParameter("response_type", responseType);
            builder.setParameter("scope", scope);
            builder.setParameter("user_id", "12345");

            GetMethod get = new GetMethod(builder.build().toString());
            String response = readResponse(get);
            authCode = extractAuthCode(response);
        } catch(IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return authCode;
    }

    protected String extractAuthCode(String response) {
        String authCode = null;
        try {
            JSONObject json = new JSONObject(response);
            String redirectUri = json.getString("redirect_uri");
            if(redirectUri != null) {
                URIBuilder builder = new URIBuilder(redirectUri);
                List<NameValuePair> params = builder.getQueryParams();
                for(NameValuePair pair : params){
                    if("code".equals(pair.getName())){
                        authCode = pair.getValue();
                        break;
                    }
                }
            }
            log.info(redirectUri);
        } catch (JSONException e) {
            // cannot extract authCode, return response
            return response;
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return authCode;
    }

    public String obtainAccessToken(String authCode, String clientId, String uri) {
        return obtainAccessToken("authorization_code", authCode, clientId, uri);
    }

    // POST /oauth20/token
    //grant_type=authorization_code&redirect_uri=dada&client_id=815424409865735&code=XOPeAaFTDNdHEeRCvbA%23IaN%23pggldgbgYnAxQvVYyyG%23zteYTuxSLcz%3DTWWXTLLxPtuviPLTspxkZgeJKsyxXgZPSAEPWYQuIUCccAeQibfmWdxQiWiezNbhGaKbHlSZzZDZAAQ-ujfUouiDZHdVrlVDMFFJJQo%3DAfJemEPRZmZ-wFTNBb-Rwni%3DaRbKRKLSzWkPLAgw
    public String obtainAccessToken(String grantType, String authCode, String clientId, String uri) {
        PostMethod post = new PostMethod(baseOAuth20Uri + "/oauth20/token");
        String accessToken = null;
        String response = null;
        try {
            String requestBody = createBody(grantType, authCode, clientId, uri);
            post.setRequestHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
            post.setRequestHeader(HttpHeaders.AUTHORIZATION, createBasicAuthorization(clientId));
            post.setRequestBody(requestBody);
            response = readResponse(post);
            log.info(response);
            if(response != null) {
                accessToken = extractAccessToken(response);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return accessToken;
    }

    public String obtainAccessTokenResponse(String grantType, String authCode, String clientId, String uri) {
        PostMethod post = new PostMethod(baseOAuth20Uri + "/oauth20/token");
        String response = null;
        try {
            String requestBody = createBody(grantType, authCode, clientId, uri);
            post.setRequestHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
            post.setRequestHeader(HttpHeaders.AUTHORIZATION, createBasicAuthorization(clientId));
            post.setRequestBody(requestBody);
            response = readResponse(post);
            log.info(response);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return response;
    }

    public String obtainAccessTokenByRefreshToken(String grantType, String refreshToken, String clientId, String scope) {
        String accessToken = null;
        String response = null;
        try {
            response = obtainAccessTokenByRefreshTokenResponse(grantType, refreshToken, clientId, scope);
            log.info(response);
            if(response != null) {
                JSONObject json = new JSONObject(response);
                if(json.get("access_token") != null) {
                    accessToken = json.getString("access_token");
                }
            }
        } catch (JSONException e) {
            //e.printStackTrace();
            accessToken = response;
        }
        return accessToken;
    }

    public String obtainAccessTokenByRefreshTokenResponse(String grantType, String refreshToken, String clientId, String scope) {
        PostMethod post = new PostMethod(baseOAuth20Uri + "/oauth20/token");
        String response = null;
        try {
            String requestBody = createBodyRefreshToken(grantType, refreshToken, scope);
            post.setRequestHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
            post.setRequestHeader(HttpHeaders.AUTHORIZATION, createBasicAuthorization(clientId));
            post.setRequestBody(requestBody);
            response = readResponse(post);
            log.info(response);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return response;
    }

    public String obtainClientCredentialsAccessToken(String clientId, String scope, boolean addAuthHeader) {
        String response =  obtainClientCredentialsAccessTokenResponse(clientId, scope, addAuthHeader);
        log.info(response);
        if(response != null) {
            return extractAccessToken(response);
        }
        return null;
    }

    public String obtainClientCredentialsAccessTokenResponse(String clientId, String scope, boolean addAuthHeader) {
        PostMethod post = new PostMethod(baseOAuth20Uri + "/oauth20/token");
        String response = null;
        try {
            String requestBody = "grant_type=client_credentials&" + "scope=" + scope;
            post.setRequestHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
            if(addAuthHeader) {
                post.setRequestHeader(HttpHeaders.AUTHORIZATION, createBasicAuthorization(clientId));
            }
            post.setRequestBody(requestBody);
            response = readResponse(post);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return response;
    }

    public String obtainPasswordCredentialsAccessTokenResponse(String clientId, String username,
            String password, String scope, boolean addAuthHeader) {
        PostMethod post = new PostMethod(baseOAuth20Uri + "/oauth20/token");
        String response = null;
        try {
            String requestBody = "grant_type=password&" + "username=" + username + "&password=" + password + "&scope=" + scope;
            post.setRequestHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
            if(addAuthHeader) {
                post.setRequestHeader(HttpHeaders.AUTHORIZATION, createBasicAuthorization(clientId));
            }
            post.setRequestBody(requestBody);
            response = readResponse(post);
            log.info(response);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return response;
    }


    public String registerNewClient() {
        URIBuilder builder = null;
        String registerResponse = null;
        try {
            builder = new URIBuilder(baseOAuth20Uri + "/oauth20/register");
            builder.setParameter("app_name", "NewTestClient");
            GetMethod get = new GetMethod(builder.build().toString());
            String response = readResponse(get);
            registerResponse = extractAuthCode(response);
        } catch(IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return registerResponse;
    }

    // POST /oauth20/token/revoked
    //{"access_token":""}
    public boolean revokeAccessToken(String token, String clientId) {
        PostMethod post = new PostMethod(baseOAuth20Uri + "/oauth20/token/revoke");
        String response = null;
        boolean revoked = false;
        try {
            String requestBody = "{\"access_token\":\""+ token + "\"}";
            post.setRequestHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            post.setRequestHeader(HttpHeaders.AUTHORIZATION, createBasicAuthorization(clientId));
            post.setRequestBody(requestBody);
            response = readResponse(post);
            log.info(response);
            if(response != null) {
                JSONObject json;
                try {
                    json = new JSONObject(response);
                    revoked = json.getBoolean("revoked");
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return revoked;
    }

    protected String createBody(String grantType, String authCode, String clientId, String redirectUri) throws IOException {
        //grant_type=authorization_code&redirect_uri=dada&client_id=815424409865735&code=XOPeAaFTDNdHEeRCvbA%23IaN%23pggldgbgYnAxQvVYyyG%23zteYTuxSLcz%3DTWWXTLLxPtuviPLTspxkZgeJKsyxXgZPSAEPWYQuIUCccAeQibfmWdxQiWiezNbhGaKbHlSZzZDZAAQ-ujfUouiDZHdVrlVDMFFJJQo%3DAfJemEPRZmZ-wFTNBb-Rwni%3DaRbKRKLSzWkPLAgw
        StringBuffer buf = new StringBuffer();
        buf.append("grant_type=" + grantType + "&");
        //buf.append("client_id=" + clientId + "&");
        buf.append("redirect_uri=" + redirectUri + "&");
        buf.append("code=" + authCode);
        return buf.toString();
    }

    protected String createBodyRefreshToken(String grantType, String refreshToken, String scope) throws IOException {
        StringBuffer buf = new StringBuffer();
        buf.append("grant_type=" + grantType + "&");
        //buf.append("client_id=" + clientId + "&");
        buf.append("refresh_token=" + refreshToken + "&");
        if(scope != null) {
            buf.append("scope=" + scope);
        }
        return buf.toString();
    }

    protected String createBasicAuthorization(String newClientId) {
        String value = (newClientId == null) ? clientId : newClientId + ":" + clientSecret;
        String encodedValue = new String(Base64.encodeBase64(value.getBytes()));
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

    protected String extractRefreshToken(String json) {
        String refreshToken = null;
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(json);
            if(jsonObj.get("refresh_token") != null) {
                refreshToken = jsonObj.getString("refresh_token");
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
            if(jsonObj.get("access_token") != null) {
                accessToken = jsonObj.getString("access_token");
            }
        } catch (JSONException e) {
            // do not log
        }
        return accessToken;
    }
}
