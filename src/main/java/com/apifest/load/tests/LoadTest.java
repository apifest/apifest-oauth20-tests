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

package com.apifest.load.tests;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apifest.BasicTest;

/**
 * @author Rossitsa Borissova
 */
public class LoadTest {

    private static List<Class<?>> classes = new ArrayList<Class<?>>();
    private static final int DEFAULT_COUNT = 1;

    private static Logger log = LoggerFactory.getLogger(BasicTest.class);

    public static void main(String [] s) {
        readClasses();
        Class<?> [] classesArr = classes.toArray(new Class[0]);
        // start threads
        int count = DEFAULT_COUNT;
        if(s.length > 0){
            try {
                count = Integer.parseInt(s[0]);
            } catch (NumberFormatException e) {
                System.out.println("cannot parse value " + s[0] + " to int");
            }
        }

        log.info("START TESTS........");
        for(int i = 0; i < count; i++) {
          RunAllTest test = new RunAllTest(classesArr);
          test.run();
        }
    }

    private static void readClasses() {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(GenerateTestList.TESTS_FILENAME);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String className = null;
            while((className = reader.readLine()) != null) {
                Class<?> clazz = Class.forName(className);
                classes.add(clazz);
            }
        } catch (FileNotFoundException e) {
            log.error("cannot read classes", e);
        } catch (IOException e) {
            log.error("cannot read classes", e);
        } catch (ClassNotFoundException e) {
            log.error("cannot read classes", e);
        }
    }
}

