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

import org.json.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Test cases for access token expiration time.
 *
 * @author Rossitsa Borissova
 *
 */
public class ExpiresInTest extends OAuth20BasicTest {

    @BeforeTest
    public void setup() {
        registerDefaultScope();
        String clientResponse = registerDefaultClient();
        clientId = extractClientId(clientResponse);
        clientSecret = extractClientSecret(clientResponse);
        updateClientAppStatus(clientId, 1);
    }

    @Test
    public void when_generate_password_token_set_DEFAULT_PASS_EXPIRES_IN_expires_in() throws Exception {
        // WHEN
        String response = obtainPasswordCredentialsAccessTokenResponse(clientId, clientSecret, username, password, DEFAULT_SCOPE, false);

        // THEN
        JSONObject json = new JSONObject(response);
        assertEquals(json.get("expires_in"), String.valueOf(DEFAULT_PASS_EXPIRES_IN));
    }

    @Test
    public void when_generate_client_credentials_token_set_DEFAULT_CC_EXPIRES_IN_expires_in() throws Exception {
        // WHEN
        String response = obtainClientCredentialsAccessTokenResponse(clientId, clientSecret, DEFAULT_SCOPE, false);

        // THEN
        JSONObject json = new JSONObject(response);
        assertEquals(json.get("expires_in"), String.valueOf(DEFAULT_CC_EXPIRES_IN));
    }

    @Test
    public void when_generate_refresh_type_token_set_DEFAULT_PASS_EXPIRES_IN_expires_in() throws Exception {
        // GIVEN
        String authCode = obtainAuthCode(clientId, DEFAULT_REDIRECT_URI);

        // WHEN
        String accessTokenResponse = obtainAccessTokenResponse("authorization_code", authCode, clientId, clientSecret, DEFAULT_REDIRECT_URI);
        String refreshToken = extractRefreshToken(accessTokenResponse);
        String response = obtainAccessTokenByRefreshTokenResponse("refresh_token", refreshToken, clientId, clientSecret, DEFAULT_SCOPE);

        // THEN
        JSONObject json = new JSONObject(response);
        assertEquals(json.get("expires_in"), String.valueOf(DEFAULT_PASS_EXPIRES_IN));
    }

}
