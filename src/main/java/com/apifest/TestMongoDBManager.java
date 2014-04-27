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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * @author Rossitsa Borissova
 */
public class TestMongoDBManager implements TestDBManager {

    protected static final String AUTH_CODE_COLLECTION_NAME = "authCodes";
    protected static final String AUTH_CODE_ID_NAME = "code";

    protected static MongoClient mongoClient;
    protected static DB db;

    Logger log = LoggerFactory.getLogger(TestMongoDBManager.class);

    public TestMongoDBManager() {
        mongoClient = DBUtil.getMongoClient();
        db = mongoClient.getDB("apifest");
    }

    public void updateAuthCodeValue(String authCode, String redirectUri, String newAuthCode) {
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put(AUTH_CODE_ID_NAME, authCode);
        dbObject.put("redirectUri", redirectUri);
        DBCollection coll = db.getCollection(AUTH_CODE_COLLECTION_NAME);
        List<DBObject> list = coll.find(dbObject).toArray();
        if (list.size() > 0) {
            DBObject newObject = list.get(0);
            newObject.put(AUTH_CODE_ID_NAME, newAuthCode);
            coll.findAndModify(dbObject, newObject);
        }
        db.cleanCursors(true);
    }
}
