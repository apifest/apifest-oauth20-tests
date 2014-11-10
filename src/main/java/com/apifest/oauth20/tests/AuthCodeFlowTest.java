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
 * Test cases for obtaining auth code.
 *
 * @author Rossitsa Borissova
 */
public class AuthCodeFlowTest extends OAuth20BasicTest {

    public AuthCodeFlowTest() {
        super();
    }

    @BeforeTest
    public void setup() {
        registerDefaultScope();
        String newClientResponse = registerNewClient(DEFAULT_CLIENT_NAME, DEFAULT_SCOPE, DEFAULT_REDIRECT_URI);
        clientId = extractClientId(newClientResponse);
        clientSecret = extractClientSecret(newClientResponse);
        updateClientAppStatus(clientId, 1);
    }

    @Test
    public void when_auth_code_obtained_with_invalid_client_id_return_error() throws Exception {
        // WHEN
        String response = obtainAuthCode(clientId + "_invalid", DEFAULT_REDIRECT_URI);

        // THEN
        assertEquals(response, "{\"error\": \"invalid client_id\"}");
        log.info("response: {}", response);
    }

    @Test
    public void when_auth_code_obtained_with_invalid_response_type_return_error() throws Exception {
        // WHEN
        String response = obtainAuthCode(clientId, DEFAULT_REDIRECT_URI, "unknown_type");

        // THEN
        assertEquals(response, "{\"error\": \"unsupported_response_type\"}");
        log.info("response: {}", response);
    }

    @Test
    public void when_auth_code_obtained_with_invalid_redirect_uri_return_error() throws Exception {
        // WHEN
        String response = obtainAuthCode(clientId, "htp:/127.0.0.1");

        // THEN
        assertEquals(response, "{\"error\": \"invalid redirect_uri\"}");
        log.info("response: {}", response);
    }

    @Test
    public void when_auth_code_obtained_with_valid_redirect_uri_return_code() throws Exception {
        // WHEN
        String response = obtainAuthCode(clientId, "http://127.0.0.1");

        // THEN
        assertTrue(!response.contains("error"));
        log.info(response);
    }
}
