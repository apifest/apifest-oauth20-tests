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

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apifest.BasicTest;

/**
 * Basic test for mappings.
 *
 * @author Rossitsa Borissova
 *
 */
public class MappingBasicTest extends BasicTest {

    private static Logger log = LoggerFactory.getLogger(MappingBasicTest.class);

    public String getMe(String accessToken) {
        URIBuilder builder = null;
        String response = null;
        try {
            builder = new URIBuilder(apifestUri + "/v0.1/me");
            GetMethod get = new GetMethod(builder.build().toString());
            if (accessToken != null) {
                get.addRequestHeader("Authorization", "Bearer " + accessToken);
            }
            response = readResponse(get);
        } catch (IOException e) {
            log.error("cannot map", e);
        } catch (URISyntaxException e) {
            log.error("cannot map", e);
        }
        return response;
    }

    public String getMe() {
        return getMe(null);
    }
}
