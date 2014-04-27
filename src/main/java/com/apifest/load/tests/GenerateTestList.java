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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rossitsa Borissova
 */
public class GenerateTestList {

    private static Logger log = LoggerFactory.getLogger(GenerateTestList.class);

    private static List<Class<?>> classes = new ArrayList<Class<?>>();
    private static String fs = System.getProperty("file.separator");
    private static String ls = System.getProperty("line.separator");
    public static final String TESTS_FILENAME = "tests_classes.txt";

    public static void main(String[] args) {
        getAllClasses();
        // write classes to output file
        String resourcesDir = "src" + fs + "main" + fs + "resources" + fs;
        File file = new File(resourcesDir + TESTS_FILENAME);
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            for (Class<?> clazz : classes) {
                out.write(clazz.getCanonicalName().getBytes());
                out.write(ls.getBytes());
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                log.error("Cannot close file");
            }
        }
    }

    private static void getAllClasses() {
        String dir = "com" + fs + "apifest";
        URL url = Thread.currentThread().getContextClassLoader().getResource(dir);
        String path = url.getPath();
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("cannot get all classes", e);
        }
        String rootPath = path.replace(fs + dir, "");
        rootPath = rootPath.replace("/", fs);
        if (rootPath.startsWith(fs)) {
            rootPath = rootPath.substring(1, rootPath.length());
        }
        addClasses(path, rootPath);
    }

    private static void addClasses(String path, String rootPath) {
        File f = new File(path);
        File[] files = null;
        if (f.isDirectory()) {
            files = f.listFiles();
        }
        if (files != null) {
            for (File curFile : files) {
                log.debug("curFile: " + curFile);
                if (curFile.isFile()) {
                    Class<?> clazz;
                    try {
                        String curFilePath = curFile.getPath().replace(rootPath + fs, "");
                        curFilePath = curFilePath.replace(fs, ".");
                        log.debug("curFilePath: " + curFilePath);
                        String className = "com.apifest."
                                + (new String(curFilePath)).replace(".class", "");
                        clazz = Class.forName(className);
                        classes.add(clazz);
                    } catch (ClassNotFoundException e) {
                        log.error("cannot load class, " + e.getException());
                    }
                } else {
                    addClasses(curFile.getAbsolutePath(), rootPath);
                }
            }
        }
    }
}
