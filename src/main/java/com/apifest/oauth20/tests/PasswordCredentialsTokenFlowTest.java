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
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Test cases for obtaining access token with username and password.
 *
 * @author Rossitsa Borissova
 *
 */
public class PasswordCredentialsTokenFlowTest extends OAuth20BasicTest {

    @BeforeTest
    public void setup() {
        registerDefaultScope();
        String clientResponse = registerNewClient(DEFAULT_CLIENT_NAME, DEFAULT_SCOPE, DEFAULT_REDIRECT_URI);
        clientId = extractClientId(clientResponse);
        clientSecret = extractClientSecret(clientResponse);
    }

    @Test
    public void when_username_and_password_valid_generate_token() throws Exception {
        // WHEN
        String response = obtainPasswordCredentialsAccessTokenResponse(clientId, username, password, DEFAULT_SCOPE, true);

        // THEN
        assertTrue(response.contains("access_token"));
    }

    @Test
    public void when_username_and_password_invalid_return_error() throws Exception {
        // GIVEN
        String username = "some_invalid_username";
        String password = "nevermind";

        // WHEN
        String response = obtainPasswordCredentialsAccessTokenResponse(clientId, username, password, DEFAULT_SCOPE, true);

        // THEN
        assertEquals(response, "{\"error\": \"invalid username/password\"}");
    }

    @Test
    public void when_username_and_password_valid_but_client_id_invalid_return_error() throws Exception {
        // GIVEN
        String username = "rossi";
        String password = "nevermind";

        // WHEN
        String response = obtainPasswordCredentialsAccessTokenResponse("invalid_client_id", username, password,
                DEFAULT_SCOPE, true);

        // THEN
        assertEquals(response, "{\"error\": \"invalid client_id\"}");
    }

}
