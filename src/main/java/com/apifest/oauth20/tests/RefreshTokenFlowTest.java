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

import org.testng.annotations.Test;

/**
 * Test cases for refreshing of access token.
 *
 * @author Rossitsa Borissova
 */
public class RefreshTokenFlowTest extends OAuth20BasicTest {

    String redirectUri = "http://example.com";

    @Test
    public void when_refresh_token_valid_then_access_token_issued() throws Exception {
        // GIVEN
        String authCode = obtainAuthCode(clientId, redirectUri);

        // WHEN
        String accessTokenResponse = obtainAccessTokenResponse("authorization_code", authCode, clientId, redirectUri);
        String refreshToken = extractRefreshToken(accessTokenResponse);
        String newAccessToken = obtainAccessTokenByRefreshToken("refresh_token", refreshToken, clientId, "basic");

        // THEN
        assertNotNull(newAccessToken);
        assertTrue(!newAccessToken.contains("{\"error\""));
    }

    @Test
    public void when_obtain_access_token_with_already_used_refresh_token_return_error() throws Exception {
        // GIVEN
        String authCode = obtainAuthCode(clientId, redirectUri);

        // WHEN
        String accessTokenResponse = obtainAccessTokenResponse("authorization_code", authCode, clientId, redirectUri);
        String refreshToken = extractRefreshToken(accessTokenResponse);
        String newAccessToken = obtainAccessTokenByRefreshToken("refresh_token", refreshToken, clientId, "basic");
        String superNewAccessToken = obtainAccessTokenByRefreshToken("refresh_token", refreshToken, clientId, "basic");

        // THEN
        assertNotNull(newAccessToken);
        assertTrue(!newAccessToken.contains("{\"error\""));
        assertNotNull(superNewAccessToken);
        assertTrue(superNewAccessToken.contains("{\"error\""));
    }

    @Test
    public void when_refresh_token_invalid_return_error() throws Exception {
        // WHEN
        String accessToken = obtainAccessTokenByRefreshToken("refresh_token", "refreshToken",clientId, "basic");

        // THEN
        assertNotNull(accessToken);
        assertTrue(accessToken.contains("{\"error\""));
    }

    @Test
    public void when_refresh_token_for_another_client_id_return_error() throws Exception {
        // GIVEN
        String newClientResponse = registerNewClient();
        String newClientId = extractClientId(newClientResponse);
        String authCode = obtainAuthCode(clientId, redirectUri);

        // WHEN
        String accessTokenResponse = obtainAccessTokenResponse("authorization_code", authCode, clientId, redirectUri);
        String refreshToken = extractRefreshToken(accessTokenResponse);
        String accessToken = obtainAccessTokenByRefreshToken("refresh_token", refreshToken, newClientId, "basic");

        // THEN
        assertEquals(accessToken, "{\"error\": \"invalid client_id\"}");
    }

    @Test
    public void when_refresh_token_ok_return_new_token() throws Exception {
        // GIVEN
        String newClientResponse = registerNewClient();
        String authCode = obtainAuthCode(clientId, redirectUri);

        // WHEN
        String accessTokenResponse = obtainAccessTokenResponse("authorization_code", authCode, clientId, redirectUri);
        String refreshToken = extractRefreshToken(accessTokenResponse);
        String accessToken = obtainAccessTokenByRefreshToken("refresh_token", refreshToken, clientId, "basic");

        // THEN
        assertTrue(!accessToken.contains("{\"error\""));
        log.info(accessToken);
    }
}
