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

import org.testng.annotations.Test;

import com.apifest.BasicTest;
import com.apifest.TestDBFactory;
import com.apifest.TestDBManager;
import com.apifest.TestMongoDBManager;

/**
 * Test cases for obtaining access token with auth code.
 *
 * @author Rossitsa Borissova
 *
 */
public class TokenWithAuthCodeFlowTest extends OAuth20BasicTest {

    String redirectUri = "http://example.com";

    @Test
    public void when_invalid_auth_code_is_used_to_generate_token_return_error() throws Exception {
        // GIVEN
        String authCode = obtainAuthCode(clientId, redirectUri);

        // WHEN
        String accessTokenResponse = obtainAccessToken(authCode + "_invalid", clientId, redirectUri);

        // THEN
        assertEquals(accessTokenResponse,  "{\"error\": \"invalid auth_code\"}");
    }

    @Test
    public void when_auth_code_is_used_with_another_client_id_to_generate_token_return_error() throws Exception {
        // GIVEN
        String authCode = obtainAuthCode(clientId, redirectUri);

        // WHEN
        String accessTokenResponse = obtainAccessToken(authCode, clientId + "_invalid", redirectUri);

        // THEN
        assertEquals(accessTokenResponse,  "{\"error\": \"invalid client_id\"}");
    }

    @Test
    public void when_auth_code_is_used_with_another_valid_client_id_to_generate_token_return_error() throws Exception {
        // GIVEN
        String authCode = obtainAuthCode(clientId, redirectUri);
        String newClientResponse = registerNewClient();
        String newClientId = extractClientId(newClientResponse);

        // WHEN
        String accessTokenResponse = obtainAccessToken(authCode, newClientId + "_invalid", redirectUri);

        // THEN
        assertEquals(accessTokenResponse,  "{\"error\": \"invalid client_id\"}");
    }

    @Test
    public void when_auth_code_is_used_with_another_redirect_id_to_generate_token_return_error() throws Exception {
        // GIVEN
        String authCode = obtainAuthCode(clientId, redirectUri);

        // WHEN
        String accessTokenResponse = obtainAccessToken(authCode, clientId, redirectUri + "_invalid");

        // THEN
        assertEquals(accessTokenResponse,  "{\"error\": \"invalid auth_code\"}");
    }

    @Test
    public void when_auth_code_is_used_with_redirect_id_but_generation_of_token_with_no_redirect_uri_return_error() throws Exception {
        // GIVEN
        String authCode = obtainAuthCode(clientId, redirectUri);

        // WHEN
        String accessTokenResponse = obtainAccessToken(authCode, clientId, null + "_invalid");

        // THEN
        assertEquals(accessTokenResponse,  "{\"error\": \"invalid auth_code\"}");
    }

    @Test
    public void when_auth_code_is_valid_but_grant_type_is_invalid_return_error() throws Exception {
        // GIVEN
        String authCode = obtainAuthCode(clientId, redirectUri);

        // WHEN
        String accessTokenResponse = obtainAccessToken("invalid grant_type", authCode, clientId, redirectUri);

        // THEN
        assertEquals(accessTokenResponse,  "{\"error\": \"unsupported_grant_type\"}");
    }

    @Test
    public void when_auth_code_is_already_used_then_return_error() throws Exception {
        // GIVEN
        String authCode = obtainAuthCode(clientId, redirectUri);

        // WHEN
        obtainAccessToken(authCode, clientId, redirectUri);
        String accessTokenResponse = obtainAccessToken(authCode, clientId, redirectUri);
        // validate access token

        // THEN
        assertEquals(accessTokenResponse,  "{\"error\": \"invalid auth_code\"}");
    }

    @Test
    public void when_auth_code_is_already_used_but_another_redirect_uri_then_issue_token() throws Exception {
        // GIVEN
        String redirectUri2 = redirectUri + "_another";
        String authCode1 = obtainAuthCode(clientId, redirectUri);
        String authCode2 = obtainAuthCode(clientId, redirectUri2);
        // update value of authCode2 to be same as authCode1
        TestDBManager db = TestDBFactory.getInstance(BasicTest.oauth20Database);
        db.updateAuthCodeValue(authCode2, redirectUri2, authCode1);

        // WHEN
        // use authCode1
        obtainAccessToken(authCode1, clientId, redirectUri);
        String accessTokenResponse = obtainAccessToken(authCode1, clientId, redirectUri2);
        // validate access token

        // THEN
        assertNotNull(accessTokenResponse);
        assertTrue(!accessTokenResponse.contains("{\"error\""));
    }

    @Test
    public void when_auth_code_is_used_with_same_client_id_then_access_token_issued() throws Exception {
        // GIVEN
        String authCode = obtainAuthCode(clientId, redirectUri);

        // WHEN
        String accessTokenResponse = obtainAccessToken(authCode, clientId, redirectUri);
        // validate access token

        // THEN
        assertNotNull(accessTokenResponse);
        assertTrue(!accessTokenResponse.contains("{\"error\""));
    }

}
