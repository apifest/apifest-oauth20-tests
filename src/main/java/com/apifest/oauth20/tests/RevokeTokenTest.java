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

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


/**
 * Test cases for access token revocation.
 *
 * @author Rossitsa Borissova
 *
 */
public class RevokeTokenTest extends OAuth20BasicTest {

    @BeforeTest
    public void setup() {
        registerDefaultScope();
        String clientResponse = registerDefaultClient();
        clientId = extractClientId(clientResponse);
        clientSecret = extractClientSecret(clientResponse);
    }

    @Test
    public void when_token_revoked_cannot_use_it() throws Exception {
        // GIVEN
        String tokenResponse = obtainPasswordCredentialsAccessTokenResponse(clientId, username, password, DEFAULT_SCOPE, true);
        String token = extractAccessToken(tokenResponse);

        // WHEN
        boolean revoked = revokeAccessToken(token, clientId);

        // THEN
        assertTrue(revoked);

        // check token validate
        String response = validateToken(token);
        assertTrue(response.contains("\"error\":\"invalid access token\""));
    }
}
