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
import org.testng.annotations.Test;

/**
 * Test cases for access token expiration time.
 *
 * @author Rossitsa Borissova
 *
 */
public class ExpiresInTest extends OAuth20BasicTest {

    @Test
    public void when_generate_password_token_set_900_expires_in() throws Exception {
        // GIVEN
        String username = "rossi";
        String password = "nevermind";

        // WHEN
        String response = obtainPasswordCredentialsAccessTokenResponse(clientId, username, password,"basic", true);

        // THEN
        JSONObject json = new JSONObject(response);
        assertEquals(json.get("expires_in"), "900");
    }


    @Test
    public void when_generate_client_credentials_token_set_1800_expires_in() throws Exception {
        // WHEN
        String response = obtainClientCredentialsAccessTokenResponse(clientId, "basic", true);

        // THEN
        JSONObject json = new JSONObject(response);
        assertEquals(json.get("expires_in"), "1800");
    }

    @Test
    public void when_generate_refresh_type_token_set_900_expires_in() throws Exception {
        // GIVEN
        String redirectUri = "http://example.com";
        String authCode = obtainAuthCode(clientId, redirectUri);

        // WHEN
        String accessTokenResponse = obtainAccessTokenResponse("authorization_code", authCode, clientId, redirectUri);
        String refreshToken = extractRefreshToken(accessTokenResponse);
        String response = obtainAccessTokenByRefreshTokenResponse("refresh_token", refreshToken, clientId, null);

        // THEN
        JSONObject json = new JSONObject(response);
        // TODO: use method to get scope expires_in
        assertEquals(json.get("expires_in"), "900");
    }

}
