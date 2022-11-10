/*
 * Copyright 2014 - Present Rafael Winterhalter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fine.example.log.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @Author: javafine
 * @Description:
 * @Date: Created in 20:24 2022/9/23
 */

@Service
public class SimpleService {
    Logger log = LoggerFactory.getLogger(SimpleService.class);
    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public void save(){
        Map<String, String> map = new HashMap<String, String>(){{put("Android","a"); put("iOS","i");}};
        reactiveMongoTemplate.insert(map, "log-agent-test").subscribe(new Consumer(){

            @Override
            public void accept(Object o) {
                log.info("result of mongo upsert : "+o);
            }
        });
    }

    public String whatYouSay(){
        try {
            Thread.sleep(2000l);
            save();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "I am the guy with a song on my lips and love in my heart";
    }
}
