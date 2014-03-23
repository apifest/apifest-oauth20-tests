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

package com.apifest.mapping.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.json.JSONObject;
import org.testng.annotations.Test;

import com.apifest.oauth20.tests.OAuth20BasicTest;

/**
 * Mapping test that uses access token.
 *
 * @author Rossitsa Borissova
 *
 */
public class MappingWithAccessTokenFlowTest extends MappingBasicTest {

    @Test
    public void when_mapping_without_access_token_return_error() throws Exception {
        // WHEN
        // normal response looks like: {"balance":"1234.34","customerId":"1223","email":"rossi.test@apifest.com"}
        String response = getMe();

        // THEN
        assertEquals(response,"{\"error\":\"access token required\"}");
    }

    @Test
    public void when_mapping_with_invalid_access_token_return_error() throws Exception {
        // WHEN
        String response = getMe("invalid_access_token");

        // THEN
        assertEquals(response,"{\"error\":\"access token not valid\"}");
    }

    @Test
    public void when_mapping_with_valid_access_token_with_invalid_scope_return_error() throws Exception {
        // GIVEN
        OAuth20BasicTest oauth = new OAuth20BasicTest();
        String redirectUri = "http://example.com";
        String authCode = oauth.obtainAuthCode(clientId, redirectUri, "code", "friends");
        String accessTokenResponse = oauth.obtainAccessToken("authorization_code", authCode, clientId, redirectUri);

        // WHEN
        String response = getMe(accessTokenResponse);

        // THEN
        assertEquals(response,"{\"error\":\"scope not valid\"}");
    }

    @Test
    public void when_mapping_with_valid_access_token_return_response() throws Exception {
        // GIVEN
        OAuth20BasicTest oauth = new OAuth20BasicTest();
        String redirectUri = "http://example.com";
        String authCode = oauth.obtainAuthCode(clientId, redirectUri);
        String accessTokenResponse = oauth.obtainAccessToken("authorization_code", authCode, clientId, redirectUri);

        // WHEN
        String response = getMe(accessTokenResponse);
        JSONObject json = new JSONObject(response);
        String email = json.getString("email");

        // THEN
        assertTrue(email != null);
    }
}
