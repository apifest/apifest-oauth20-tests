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

package com.apifest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Rossitsa Borissova
 */
public class BasicTest {

    public static String clientId;
    public static String clientSecret;
    public static String username;
    public static String password;
    public static String baseOAuth20Uri;
    public static String apifestUri;

    Logger log = LoggerFactory.getLogger(BasicTest.class);

    public BasicTest() {
        InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("apifest-oauth.properties");
        try {
            loadProperties(in);
        } catch (IOException e) {
            log.error("Cannot load properties");
        }
    }

    protected void loadProperties(InputStream in) throws IOException {
        Properties props = new Properties();
        props.load(in);
        username = props.getProperty("username");
        password = props.getProperty("password");
        baseOAuth20Uri = props.getProperty("base_oauth20_uri");
        apifestUri = props.getProperty("apifest_uri");
    }

    public String readResponse(HttpMethod method) throws IOException {
        HttpClient client = new HttpClient();
        String response = null;
        InputStream in = null;
        try {
            int status = client.executeMethod(method);
            if (status >= HttpStatus.SC_OK) {
                in = method.getResponseBodyAsStream();
                response = readInputStream(in);
            }
        } catch (HttpException e) {
            log.error("cannot read response", e);
        } catch (IOException e) {
            log.error("cannot read response", e);
        } finally {
            in.close();
            method.releaseConnection();
        }
        return response;
    }

    public String readInputStream(InputStream in) throws IOException {
        byte[] buf = new byte[4096];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String res = "";
        try {
            int c;
            while ((c = in.read(buf)) > 0) {
                out.write(buf);
            }
            out.flush();
            res = out.toString("UTF-8");
        } catch (IOException e) {
            log.error("cannot read input", e);
        } finally {
            out.close();
        }
        return res.trim();
    }
}
