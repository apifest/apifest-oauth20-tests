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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * @author Rossitsa Borissova
 *
 */
public class ScopeTest extends OAuth20BasicTest {

    private static final String SCOPE_NOT_VALID = "{\"status\":\"scope not valid\"}";

    String newScope = "newScope";

    @BeforeTest
    public void setup() throws Exception {
//        String scopes = getAllScopes();
//        boolean newScopeExists = false;
//        JSONArray array = new JSONArray(scopes);
//        for (int i = 0; i < array.length(); i++) {
//            JSONObject obj = array.getJSONObject(i);
//            if (newScope.equals(obj.getString("scope"))) {
//                newScopeExists = true;
//                break;
//            }
//        }
//        if (!newScopeExists) {
//            newScope = registerNewScope("newScope", "new test scope", 1800, 900);
//        }
    }

    @Test
    public void when_get_auth_code_with_non_existing_scope_return_error() throws Exception {
        // WHEN
        String response = obtainAuthCode(clientId, REDIRECT_URI, RESPONSE_TYPE_CODE, "non-existing");

        // THEN
        assertEquals(response, SCOPE_NOT_VALID);
    }

    @Test
    public void when_get_auth_code_with_not_client_scope_return_error() throws Exception {
        // WHEN
        String response = obtainAuthCode(clientId, REDIRECT_URI, RESPONSE_TYPE_CODE, newScope);

        // THEN
        assertEquals(response, SCOPE_NOT_VALID);
    }

    @Test
    public void when_get_auth_code_with_null_scope_use_client_default_scope() throws Exception {
        // WHEN
        String code = obtainAuthCode(clientId, REDIRECT_URI, RESPONSE_TYPE_CODE, null);
        String accessTokenResponse = obtainAccessTokenResponse(GRANT_TYPE_AUTH_CODE, code, clientId, REDIRECT_URI);
        String scope = extractAccessTokenScope(accessTokenResponse);
        String clientDefaultScope = getClientDefaultScope(clientId);

        // THEN
        assertEquals(scope, clientDefaultScope);
    }

    @Test
    public void when_get_auth_code_with_valid_scope_return_auth_code() throws Exception {
        // WHEN
        String code = obtainAuthCode(clientId, REDIRECT_URI, RESPONSE_TYPE_CODE, DEFAULT_SCOPE);
        String accessTokenResponse = obtainAccessTokenResponse(GRANT_TYPE_AUTH_CODE, code, clientId, REDIRECT_URI);
        String scope = extractAccessTokenScope(accessTokenResponse);

        // THEN
        assertEquals(scope, DEFAULT_SCOPE);
    }

    @Test
    public void when_obtain_password_access_token_with_non_existing_scope_return_error() throws Exception {
        // WHEN
        String response = obtainPasswordCredentialsAccessTokenResponse(clientId, "rossi", "nevermind", "non-existing",
                true);

        // THEN
        assertEquals(response, SCOPE_NOT_VALID);
    }

    @Test
    public void when_obtain_password_access_token_with_null_scope_use_client_default_scope() throws Exception {
        // WHEN
        String response = obtainPasswordCredentialsAccessTokenResponse(clientId, "rossi", "nevermind", null, true);
        String scope = extractAccessTokenScope(response);
        String clientDefaultScope = getClientDefaultScope(clientId);

        // THEN
        assertEquals(scope, clientDefaultScope);
    }

    @Test
    public void when_obtain_password_access_token_with_not_client_scope_return_error() throws Exception {
        // WHEN
        String response = obtainPasswordCredentialsAccessTokenResponse(clientId, "rossi", "nevermind", newScope, true);

        // THEN
        assertEquals(response, SCOPE_NOT_VALID);
    }

    @Test
    public void when_obtain_password_access_token_with_valid_scope_return_access_token_with_that_scope()
            throws Exception {
        // GIVEN
        String defaultScope = getClientDefaultScope(clientId);
        String accessScope = defaultScope.split(" ")[0];

        // WHEN
        String response = obtainPasswordCredentialsAccessTokenResponse(clientId, "rossi", "nevermind", accessScope,
                true);
        String scope = extractAccessTokenScope(response);

        // THEN
        assertEquals(scope, accessScope);
    }

    @Test
    public void when_obtain_client_credentials_access_token_with_non_existing_scope_return_error() throws Exception {
        // WHEN
        String response = obtainClientCredentialsAccessTokenResponse(clientId, "non-existing", true);

        // THEN
        assertEquals(response, SCOPE_NOT_VALID);
    }

    @Test
    public void when_obtain_client_credentials_access_token_with_null_scope_use_client_default_scope() throws Exception {
        // WHEN
        String response = obtainClientCredentialsAccessTokenResponse(clientId, null, true);
        String scope = extractAccessTokenScope(response);
        String clientDefaultScope = getClientDefaultScope(clientId);

        // THEN
        assertEquals(scope, clientDefaultScope);
    }

    @Test
    public void when_obtain_client_credentials_access_token_with_not_client_scope_return_error() throws Exception {
        // WHEN
        String response = obtainClientCredentialsAccessTokenResponse(clientId, newScope, true);

        // THEN
        assertEquals(response, SCOPE_NOT_VALID);
    }

    @Test
    public void when_obtain_client_credentials_access_token_with_valid_scope_return_access_token_with_that_scope()
            throws Exception {
        // GIVEN
        String defaultScope = getClientDefaultScope(clientId);
        String accessScope = defaultScope.split(" ")[0];

        // WHEN
        String response = obtainClientCredentialsAccessTokenResponse(clientId, accessScope, true);
        String scope = extractAccessTokenScope(response);

        // THEN
        assertEquals(scope, accessScope);
    }

    @Test
    public void when_obtain_refresh_access_token_with_non_existing_scope_return_error() throws Exception {
        // WHEN
        String accessTokenResponse = obtainPasswordCredentialsAccessTokenResponse(clientId, "rossi", "nevermind",
                "non-existing", true);
        String refreshToken = extractRefreshToken(accessTokenResponse);
        String response = obtainAccessTokenByRefreshTokenResponse(GRANT_TYPE_REFRESH_TOKEN, refreshToken, clientId,
                "non-existing");

        // THEN
        assertEquals(response, SCOPE_NOT_VALID);
    }

    @Test
    public void when_obtain_refresh_access_token_with_null_scope_use_client_default_scope() throws Exception {
        // WHEN
        String clientDefaultScope = getClientDefaultScope(clientId);
        String accessTokenResponse = obtainPasswordCredentialsAccessTokenResponse(clientId, "rossi", "nevermind",
                clientDefaultScope, true);
        String refreshToken = extractRefreshToken(accessTokenResponse);
        String response = obtainAccessTokenByRefreshTokenResponse(GRANT_TYPE_REFRESH_TOKEN, refreshToken, clientId,
                null);
        String scope = extractAccessTokenScope(response);

        // THEN
        assertEquals(scope, clientDefaultScope);
    }

    @Test
    public void when_obtain_refresh_access_token_with_not_client_scope_return_error() throws Exception {
        // GIVEN
        String newScope = registerNewScope("newScope", "new test scope", 1800, 900);

        // WHEN
        String response = obtainClientCredentialsAccessTokenResponse(clientId, newScope, true);

        // THEN
        assertEquals(response, SCOPE_NOT_VALID);
    }

    @Test
    public void when_obtain_refresh_access_token_with_valid_scope_return_access_token_with_that_scope()
            throws Exception {
        // GIVEN
        String defaultScope = getClientDefaultScope(clientId);
        String accessScope = defaultScope.split(",")[0];

        // WHEN
        String response = obtainClientCredentialsAccessTokenResponse(clientId, accessScope, true);
        String scope = extractAccessTokenScope(response);

        // THEN
        assertEquals(scope, accessScope);
    }

    @Test
    public void when_obtain_password_access_token_with_several_scopes_use_min_expires_in() throws Exception {
        // GIVEN
        String clientDefaultScope = getClientDefaultScope(clientId);
        int minExpiresIn = getMinExpiresIn(clientDefaultScope, "password");

        // WHEN
        String response = obtainPasswordCredentialsAccessTokenResponse(clientId, "rossi", "nevermind",
                clientDefaultScope, true);

        // THEN
        String expiresIn = extractAccessTokenExpiresIn(response);
        assertEquals(expiresIn, String.valueOf(minExpiresIn));
    }

    @Test
    public void when_obtain_cc_access_token_with_several_scopes_use_min_expires_in() throws Exception {
        // GIVEN
        String clientDefaultScope = getClientDefaultScope(clientId);
        int minExpiresIn = getMinExpiresIn(clientDefaultScope, "client_credentials");

        // WHEN
        String response = obtainClientCredentialsAccessTokenResponse(clientId, clientDefaultScope, true);

        // THEN
        String expiresIn = extractAccessTokenExpiresIn(response);
        assertEquals(expiresIn, String.valueOf(minExpiresIn));
    }

    private int getMinExpiresIn(String scopes, String granType) throws JSONException {
        List<String> allowedScopes = Arrays.asList(scopes.split(" "));
        String allScopes = getAllScopes();
        int min = Integer.MAX_VALUE;
        JSONArray array = new JSONArray(allScopes);
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            if (!allowedScopes.contains(obj.getString("scope"))) {
                continue;
            }

            int expiresIn;
            if ("password".equals(granType)) {
                expiresIn = obj.getInt("pass_expires_in");
            } else {
                expiresIn = obj.getInt("cc_expires_in");
            }

            if (expiresIn < min) {
                min = expiresIn;
            }
        }
        return min;
    }
}
