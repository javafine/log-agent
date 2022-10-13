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
package com.fine.example.log.controller;

import com.fine.example.log.service.RemoteService;
import com.fine.example.log.service.SimpleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @Author: javafine
 * @Description:
 * @Date: Created in 19:43 2022/9/21
 */

@RestController
public class SimpleController {
    @Autowired
    public SimpleService simpleService;

    @Autowired
    public RemoteService remoteService;

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name, HttpServletRequest request) {
//        System.out.println("--------------request "+request.getClass().getClassLoader()+" | "+request.getClass().hashCode());
        return String.format("Hello %s! %s", name, simpleService.whatYouSay());
    }

    @PostMapping("/hi")
    public String hi(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hi %s! %s", name, simpleService.whatYouSay());
    }

    @RequestMapping(value = "/morning", method = RequestMethod.POST)
    public String morning(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Morning %s! %s", name, simpleService.whatYouSay());
    }

    @GetMapping("/remote")
    public String remote() throws IOException {
        return remoteService.process();
    }

}
