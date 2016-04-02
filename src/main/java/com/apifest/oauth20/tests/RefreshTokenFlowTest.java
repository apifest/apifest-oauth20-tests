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

import static org.testng.Assert.*;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Test cases for refreshing of access token.
 *
 * @author Rossitsa Borissova
 */
public class RefreshTokenFlowTest extends OAuth20BasicTest {

    @BeforeTest
    public void setup() {
        registerDefaultScope();
        String clientResponse = registerDefaultClient();
        clientId = extractClientId(clientResponse);
        clientSecret = extractClientSecret(clientResponse);
        updateClientAppStatus(clientId, 1);
    }

    @Test
    public void when_refresh_token_valid_then_access_token_issued() throws Exception {
        // GIVEN
        String authCode = obtainAuthCode(clientId, DEFAULT_REDIRECT_URI);

        // WHEN
        String accessTokenResponse = obtainAccessTokenResponse("authorization_code", authCode, clientId, clientSecret, DEFAULT_REDIRECT_URI);
        String refreshToken = extractRefreshToken(accessTokenResponse);
        String newAccessToken = obtainAccessTokenByRefreshToken("refresh_token", refreshToken, clientId, clientSecret, DEFAULT_SCOPE);

        // THEN
        assertNotNull(newAccessToken);
        assertTrue(!newAccessToken.contains("{\"error\""));
    }

    @Test
    public void when_refresh_token_invalid_return_error() throws Exception {
        // WHEN
        String accessToken = obtainAccessTokenByRefreshToken("refresh_token", "refreshToken", clientId, clientSecret, DEFAULT_SCOPE);

        // THEN
        assertNotNull(accessToken);
        assertTrue(accessToken.contains("{\"error\""));
    }

    @Test
    public void when_refresh_token_for_another_client_id_return_error() throws Exception {
        // GIVEN
        String newClientResponse = registerNewClient("NewTestClient", DEFAULT_SCOPE, DEFAULT_REDIRECT_URI);
        String newClientId = extractClientId(newClientResponse);
        String newClientSecret = extractClientSecret(newClientResponse);
        String authCode = obtainAuthCode(clientId, DEFAULT_REDIRECT_URI);

        // WHEN
        String accessTokenResponse = obtainAccessTokenResponse("authorization_code", authCode, clientId, clientSecret, DEFAULT_REDIRECT_URI);
        String refreshToken = extractRefreshToken(accessTokenResponse);
        String accessToken = obtainAccessTokenByRefreshToken("refresh_token", refreshToken, newClientId, newClientSecret, DEFAULT_SCOPE);

        // THEN
        assertEquals(accessToken, "{\"error\": \"invalid client_id/client_secret\"}");
    }

    @Test
    public void when_refresh_token_ok_return_new_token() throws Exception {
        // GIVEN
        //registerNewClient("NewTestClient", DEFAULT_SCOPE, DEFAULT_REDIRECT_URI);
        String authCode = obtainAuthCode(clientId, DEFAULT_REDIRECT_URI);

        // WHEN
        String accessTokenResponse = obtainAccessTokenResponse("authorization_code", authCode, clientId, clientSecret, DEFAULT_REDIRECT_URI);
        String refreshToken = extractRefreshToken(accessTokenResponse);
        String accessToken = obtainAccessTokenByRefreshToken("refresh_token", refreshToken, clientId, clientSecret, DEFAULT_SCOPE);

        // THEN
        assertTrue(!accessToken.contains("{\"error\""));
        log.info(accessToken);
    }
}
