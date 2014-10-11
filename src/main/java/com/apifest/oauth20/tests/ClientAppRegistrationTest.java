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
 * @author Rossitsa Borissova
 *
 */
public class ClientAppRegistrationTest extends OAuth20BasicTest {

    @BeforeTest
    public void setup() {
        registerDefaultScope();
        String clientResponse = registerNewClient(DEFAULT_CLIENT_NAME, DEFAULT_SCOPE, DEFAULT_REDIRECT_URI);
        clientId = extractClientId(clientResponse);
        clientSecret = extractClientSecret(clientResponse);
    }
    @Test
    public void when_register_with_not_existing_scope_return_error() throws Exception {
        // WHEN
        String response = registerNewClient("some name", "not_existing_scope", "http://127.0.0.1");

        // THEN
        assertEquals(response, "{\"error\": \"scope does not exist\"}");
    }

    @Test
    public void when_register_with_invalid_redirect_uri_return_error() throws Exception {
        // GIVEN
        registerDefaultScope();

        // WHEN
        String response = registerNewClient("some name", DEFAULT_SCOPE, "htt://127.0.0.1");

        // THEN
        assertEquals(response, "{\"error\": \"name, scope or redirect_uri is missing or invalid\"}");
    }

    @Test
    public void register_client_app_with_predefined_client_id_and_client_secret() throws Exception {
        // GIVEN
        String newClientId = System.currentTimeMillis() + "abdeee";
        String newClientSecret ="d43a1249361e10e6";

        // WHEN
        String response = registerNewClientWithPredefinedClientId("some name", OAuth20BasicTest.DEFAULT_SCOPE,
                OAuth20BasicTest.DEFAULT_REDIRECT_URI, newClientId, newClientSecret);

        // THEN
        assertTrue(response.contains(newClientId));
        assertTrue(response.contains(newClientSecret));
    }

    @Test
    public void register_client_app_with_already_registered_client_id_and_client_secret() throws Exception {
        // WHEN
        String response = registerNewClientWithPredefinedClientId("some name", OAuth20BasicTest.DEFAULT_SCOPE,
                OAuth20BasicTest.DEFAULT_REDIRECT_URI, clientId, clientSecret);

        // THEN
        assertEquals(response, "{\"error\": \"already registered client application\"}");
    }

    @Test
    public void update_default_client_app_description() throws Exception {
        // GIVEN
        String defaultClientApp = getClientAppById(clientId);
        String defaultDescr = extractDescription(defaultClientApp);
        String newDescr = "new description " + defaultDescr;

        // WHEN
        String response = updateClientApp(clientId, DEFAULT_SCOPE, newDescr, 0, "http://127.0.0.1");

        // THEN
        assertEquals(response, "{\"status\":\"client application updated\"}");
        String updated = getClientAppById(clientId);
        assertTrue(updated.contains(newDescr));

        // cleanup
        response = updateClientApp(clientId, DEFAULT_SCOPE, DEFAULT_DESCRIPTION, 0, "http://127.0.0.1");
        assertEquals(response, "{\"status\":\"client application updated\"}");
        updated = getClientAppById(clientId);
        assertTrue(updated.contains(DEFAULT_DESCRIPTION));
    }

}
