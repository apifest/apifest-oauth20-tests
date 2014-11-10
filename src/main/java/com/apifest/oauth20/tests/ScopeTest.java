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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

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
        registerDefaultScope();
        String clientResponse = registerDefaultClient();
        clientId = extractClientId(clientResponse);
        clientSecret = extractClientSecret(clientResponse);
        updateClientAppStatus(clientId, 1);
    }

    @Test
    public void when_get_auth_code_with_non_existing_scope_return_error() throws Exception {
        // WHEN
        String response = obtainAuthCode(clientId, DEFAULT_REDIRECT_URI, RESPONSE_TYPE_CODE, "non-existing");

        // THEN
        assertEquals(response, SCOPE_NOT_VALID);
    }

    @Test
    public void when_get_auth_code_with_not_client_scope_return_error() throws Exception {
        // WHEN
        String response = obtainAuthCode(clientId, DEFAULT_REDIRECT_URI, RESPONSE_TYPE_CODE, newScope);

        // THEN
        assertEquals(response, SCOPE_NOT_VALID);
    }

    @Test
    public void when_get_auth_code_with_null_scope_use_client_default_scope() throws Exception {
        // WHEN
        String code = obtainAuthCode(clientId, DEFAULT_REDIRECT_URI, RESPONSE_TYPE_CODE, null);
        String accessTokenResponse = obtainAccessTokenResponse(GRANT_TYPE_AUTH_CODE, code, clientId, clientSecret, DEFAULT_REDIRECT_URI);
        String scope = extractAccessTokenScope(accessTokenResponse);
        String clientDefaultScope = getClientDefaultScope(clientId);

        // THEN
        assertEquals(scope, clientDefaultScope);
    }

    @Test
    public void when_get_auth_code_with_valid_scope_return_auth_code() throws Exception {
        // WHEN
        String code = obtainAuthCode(clientId, DEFAULT_REDIRECT_URI, RESPONSE_TYPE_CODE, DEFAULT_SCOPE);
        String accessTokenResponse = obtainAccessTokenResponse(GRANT_TYPE_AUTH_CODE, code, clientId, clientSecret, DEFAULT_REDIRECT_URI);
        String scope = extractAccessTokenScope(accessTokenResponse);

        // THEN
        assertEquals(scope, DEFAULT_SCOPE);
    }

    @Test
    public void when_obtain_password_access_token_with_non_existing_scope_return_error() throws Exception {
        // WHEN
        String response = obtainPasswordCredentialsAccessTokenResponse(clientId, clientSecret, username, password,
                "non-existing", false);

        // THEN
        assertEquals(response, SCOPE_NOT_VALID);
    }

    @Test
    public void when_obtain_password_access_token_with_null_scope_use_client_default_scope() throws Exception {
        // WHEN
        String response = obtainPasswordCredentialsAccessTokenResponse(clientId, clientSecret, username, password, null, false);
        String scope = extractAccessTokenScope(response);
        String clientDefaultScope = getClientDefaultScope(clientId);

        // THEN
        assertEquals(scope, clientDefaultScope);
    }

    @Test
    public void when_obtain_password_access_token_with_not_client_scope_return_error() throws Exception {
        // WHEN
        String response = obtainPasswordCredentialsAccessTokenResponse(clientId, clientSecret, username, password, newScope, false);

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
        String response = obtainPasswordCredentialsAccessTokenResponse(clientId, clientSecret, username, password, accessScope, false);
        String scope = extractAccessTokenScope(response);

        // THEN
        assertEquals(scope, accessScope);
    }

    @Test
    public void when_obtain_client_credentials_access_token_with_non_existing_scope_return_error() throws Exception {
        // WHEN
        String response = obtainClientCredentialsAccessTokenResponse(clientId, clientSecret, "non-existing", false);

        // THEN
        assertEquals(response, SCOPE_NOT_VALID);
    }

    @Test
    public void when_obtain_client_credentials_access_token_with_null_scope_use_client_default_scope() throws Exception {
        // WHEN
        String response = obtainClientCredentialsAccessTokenResponse(clientId, clientSecret, null, false);
        String scope = extractAccessTokenScope(response);
        String clientDefaultScope = getClientDefaultScope(clientId);

        // THEN
        assertEquals(scope, clientDefaultScope);
    }

    @Test
    public void when_obtain_client_credentials_access_token_with_not_client_scope_return_error() throws Exception {
        // WHEN
        String response = obtainClientCredentialsAccessTokenResponse(clientId, clientSecret, newScope, false);

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
        String response = obtainClientCredentialsAccessTokenResponse(clientId, clientSecret, accessScope, false);
        String scope = extractAccessTokenScope(response);

        // THEN
        assertEquals(scope, accessScope);
    }

    @Test
    public void when_obtain_refresh_access_token_with_non_existing_scope_return_error() throws Exception {
        // WHEN
        String accessTokenResponse = obtainPasswordCredentialsAccessTokenResponse(clientId, clientSecret, username, password,
                DEFAULT_SCOPE, false);
        String refreshToken = extractRefreshToken(accessTokenResponse);
        String response = obtainAccessTokenByRefreshTokenResponse(GRANT_TYPE_REFRESH_TOKEN, refreshToken, clientId,
                clientSecret, "non-existing");

        // THEN
        assertEquals(response, SCOPE_NOT_VALID);
    }

    @Test
    public void when_obtain_refresh_access_token_with_null_scope_use_client_default_scope() throws Exception {
        // WHEN
        String clientDefaultScope = getClientDefaultScope(clientId);
        String accessTokenResponse = obtainPasswordCredentialsAccessTokenResponse(clientId, clientSecret, username, password,
                clientDefaultScope, false);
        String refreshToken = extractRefreshToken(accessTokenResponse);
        String response = obtainAccessTokenByRefreshTokenResponse(GRANT_TYPE_REFRESH_TOKEN, refreshToken, clientId,
                clientSecret, null);
        String scope = extractAccessTokenScope(response);

        // THEN
        assertEquals(scope, clientDefaultScope);
    }

    @Test
    public void when_obtain_refresh_access_token_with_not_client_scope_return_error() throws Exception {
        // GIVEN
        String newScope = registerNewScope("newScope", "new test scope", 1800, 900);

        // WHEN
        String response = obtainClientCredentialsAccessTokenResponse(clientId, clientSecret, newScope, false);

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
        String response = obtainClientCredentialsAccessTokenResponse(clientId, clientSecret, accessScope, false);
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
        String response = obtainPasswordCredentialsAccessTokenResponse(clientId, clientSecret, username, password,
                clientDefaultScope, false);

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
        String response = obtainClientCredentialsAccessTokenResponse(clientId, clientSecret, clientDefaultScope, false);

        // THEN
        String expiresIn = extractAccessTokenExpiresIn(response);
        assertEquals(expiresIn, String.valueOf(minExpiresIn));
    }

    @Test
    public void update_existing_scope() throws Exception {
        // GIVEN
        String scopeName = "newScopeForUpdate2";
        String description = "descr";
        int ccExpiresIn = 1800;
        int passExpiresIn = 900;
        registerNewScope(scopeName, description, ccExpiresIn, passExpiresIn);
        String newDescription = "new updated description";

        // WHEN
        String ok = updateScope(scopeName, newDescription, ccExpiresIn, passExpiresIn);

        // THEN
        assertEquals(ok, "{\"status\":\"scope successfully updated\"}");
        String allScopes = getAllScopes();
        String scopeDescr = extractScopeField(allScopes, scopeName, "description");
        assertEquals(scopeDescr, newDescription);

        // revert the description
        ok = updateScope(scopeName, description, ccExpiresIn, passExpiresIn);
        assertEquals(ok, "{\"status\":\"scope successfully updated\"}");
    }

    @Test
    public void update_existing_scope_description_only() throws Exception {
        // GIVEN
        String scopeName = "newScopeForUpdate2";
        String description = "descr";
        int ccExpiresIn = 1800;
        int passExpiresIn = 900;
        registerNewScope(scopeName, description, ccExpiresIn, passExpiresIn);
        String newDescription = "new updated description";

        // WHEN
        String ok = updateScope(scopeName, newDescription, null, null);

        // THEN
        assertEquals(ok, "{\"status\":\"scope successfully updated\"}");
        String allScopes = getAllScopes();
        String scopeDescr = extractScopeField(allScopes, scopeName, "description");
        assertEquals(scopeDescr, newDescription);
        int resCcExpiresIn = Integer.valueOf(extractScopeField(allScopes, scopeName, "cc_expires_in"));
        assertTrue(resCcExpiresIn == ccExpiresIn);
        int resPassExpiresIn = Integer.valueOf(extractScopeField(allScopes, scopeName, "pass_expires_in"));
        assertTrue(resPassExpiresIn == passExpiresIn);

        // revert the description
        ok = updateScope(scopeName, description, ccExpiresIn, passExpiresIn);
        assertEquals(ok, "{\"status\":\"scope successfully updated\"}");
    }

    @Test
    public void when_register_scope_with_space_return_error() throws Exception {
        // GIVEN
        String scopeName = "scope with space";
        String description = "descr";
        int ccExpiresIn = 1800;
        int passExpiresIn = 900;

        // WHEN
        String response = registerNewScope(scopeName, description, ccExpiresIn, passExpiresIn);

        // THEN
        assertEquals(response, "{\"error\":\"scope name not valid - it may contain aplha-numeric, - and _\"}");
    }

    @Test
    public void when_delete_scope_it_is_not_listed_anymore() throws Exception {
        // GIVEN
        String tempScope = "new_scope_for_delete";
        registerNewScope(tempScope, "temporary", 1000, 500);
        String allScopes = getAllScopes();
        assertTrue(allScopes.contains(tempScope));

        // WHEN
        String deleted = deleteScope(tempScope);

        // THEN
        assertEquals(deleted, "{\"status\":\"scope successfully deleted\"}");
        allScopes = getAllScopes();
        assertFalse(allScopes.contains(tempScope));
    }

    @Test
    public void when_try_to_delete_non_existing_scope_return_error() throws Exception {
        // GIVEN
        String tempScope = "new_scope_for_delete";

        // WHEN
        String deleted = deleteScope(tempScope);

        // THEN
        assertEquals(deleted, "{\"status\":\"scope does not exist\"}");
    }

    private String extractScopeField(String allScopes, String scopeName, String field) throws JSONException {
        String description = null;
        JSONArray array = new JSONArray(allScopes);
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            if (obj.getString("scope").equals(scopeName)) {
                description = obj.getString(field);
            }
        }
        return description;
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
