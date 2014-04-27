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

import org.testng.annotations.Test;

import com.apifest.mapping.tests.MappingBasicTest;

/**
 * Test cases for access token revocation.
 *
 * @author Rossitsa Borissova
 *
 */
public class RevokeTokenTest extends OAuth20BasicTest {

    @Test
    public void when_token_revoked_cannot_use_it() throws Exception {
        // GIVEN
        String username = "rossi";
        String password = "nevermind";
        String tokenResponse = obtainPasswordCredentialsAccessTokenResponse(clientId, username, password, "basic", true);
        String token = extractAccessToken(tokenResponse);

        // WHEN
        boolean revoked = revokeAccessToken(token, clientId);

        // THEN
        assertTrue(revoked);

        // try mapping GET /me
        MappingBasicTest mappingTest = new MappingBasicTest();
        String response = mappingTest.getMe(token);
        assertTrue(response.contains("access token not valid"));
    }
}
