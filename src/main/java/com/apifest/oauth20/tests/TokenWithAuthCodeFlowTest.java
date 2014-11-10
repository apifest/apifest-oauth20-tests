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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Test cases for obtaining access token with auth code.
 *
 * @author Rossitsa Borissova
 *
 */
public class TokenWithAuthCodeFlowTest extends OAuth20BasicTest {

    @BeforeTest
    public void setup() throws Exception {
        registerDefaultScope();
        String clientResponse = registerDefaultClient();
        clientId = extractClientId(clientResponse);
        clientSecret = extractClientSecret(clientResponse);
        updateClientAppStatus(clientId, 1);
    }

    @Test
    public void when_invalid_auth_code_is_used_to_generate_token_return_error() throws Exception {
        // GIVEN
        String authCode = obtainAuthCode(clientId, DEFAULT_REDIRECT_URI);

        // WHEN
        String accessTokenResponse = obtainAccessToken(authCode + "_invalid", clientId, clientSecret, DEFAULT_REDIRECT_URI);

        // THEN
        assertEquals(accessTokenResponse, "{\"error\": \"invalid auth_code\"}");
    }

    @Test
    public void when_auth_code_is_used_with_another_client_id_to_generate_token_return_error() throws Exception {
        // GIVEN
        String authCode = obtainAuthCode(clientId, DEFAULT_REDIRECT_URI);

        // WHEN
        String accessTokenResponse = obtainAccessToken(authCode, clientId + "_invalid", clientSecret, DEFAULT_REDIRECT_URI);

        // THEN
        assertEquals(accessTokenResponse, "{\"error\": \"invalid client_id/client_secret\"}");
    }

    @Test
    public void when_auth_code_is_used_with_another_valid_client_id_to_generate_token_return_error() throws Exception {
        // GIVEN
        String authCode = obtainAuthCode(clientId, DEFAULT_REDIRECT_URI);
        String newClientResponse = registerNewClient("NewTestClient", DEFAULT_SCOPE, DEFAULT_REDIRECT_URI);
        String newClientId = extractClientId(newClientResponse);

        // WHEN
        String accessTokenResponse = obtainAccessToken(authCode, newClientId + "_invalid", clientSecret, DEFAULT_REDIRECT_URI);

        // THEN
        assertEquals(accessTokenResponse, "{\"error\": \"invalid client_id/client_secret\"}");
    }

    @Test
    public void when_auth_code_is_used_with_another_redirect_id_to_generate_token_return_error() throws Exception {
        // GIVEN
        String authCode = obtainAuthCode(clientId, DEFAULT_REDIRECT_URI);

        // WHEN
        String accessTokenResponse = obtainAccessToken(authCode, clientId, clientSecret, DEFAULT_REDIRECT_URI + "_invalid");

        // THEN
        assertEquals(accessTokenResponse, "{\"error\": \"invalid auth_code\"}");
    }

    @Test
    public void when_auth_code_is_used_with_redirect_id_but_generation_of_token_with_no_DEFAULT_REDIRECT_URI_return_error()
            throws Exception {
        // GIVEN
        String authCode = obtainAuthCode(clientId, DEFAULT_REDIRECT_URI);

        // WHEN
        String accessTokenResponse = obtainAccessToken(authCode, clientId, clientSecret, null + "_invalid");

        // THEN
        assertEquals(accessTokenResponse, "{\"error\": \"invalid auth_code\"}");
    }

    @Test
    public void when_auth_code_is_valid_but_grant_type_is_invalid_return_error() throws Exception {
        // GIVEN
        String authCode = obtainAuthCode(clientId, DEFAULT_REDIRECT_URI);

        // WHEN
        String accessTokenResponse = obtainAccessToken("invalid grant_type", authCode, clientId, clientSecret, DEFAULT_REDIRECT_URI);

        // THEN
        assertEquals(accessTokenResponse, "{\"error\": \"unsupported_grant_type\"}");
    }

    @Test
    public void when_auth_code_is_already_used_then_return_error() throws Exception {
        // GIVEN
        String authCode = obtainAuthCode(clientId, DEFAULT_REDIRECT_URI);

        // WHEN
        obtainAccessToken(authCode, clientId, clientSecret, DEFAULT_REDIRECT_URI);
        String accessTokenResponse = obtainAccessToken(authCode, clientId, clientSecret, DEFAULT_REDIRECT_URI);
        // validate access token

        // THEN
        assertEquals(accessTokenResponse, "{\"error\": \"invalid auth_code\"}");
    }

    @Test
    public void when_auth_code_is_used_with_same_client_id_then_access_token_issued() throws Exception {
        // GIVEN
        String authCode = obtainAuthCode(clientId, DEFAULT_REDIRECT_URI);

        // WHEN
        String accessTokenResponse = obtainAccessToken(authCode, clientId, clientSecret, DEFAULT_REDIRECT_URI);
        // validate access token

        // THEN
        assertNotNull(accessTokenResponse);
        assertTrue(!accessTokenResponse.contains("{\"error\""));
    }

}
