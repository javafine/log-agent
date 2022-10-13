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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @Author: javafine
 * @Description:
 * @Date: Created in 17:52 2022/9/27
 */

@Service
public class RemoteService {
    private CloseableHttpClient httpClient = HttpClients.custom().build();
    public String process() throws IOException {
        HttpResponse response = httpClient.execute(doGet().build());
        return EntityUtils.toString(response.getEntity());
    }

    public static RequestBuilder doGet(){
        String uri = "http://httpbin.org/get";
        RequestBuilder requestBuilder = RequestBuilder.get(uri);
        return requestBuilder;
    }
//    public static RequestBuilder makePost(){
//        String uri = "http://tbaas.changyou.com/v1/classes/book";
//        String body = "{\"name\":\"数据结构与算法分析\",\"price\":38,\"desc\":\"数组、链表和排序\",\"type\":100,\"key\":2}";
//        RequestBuilder requestBuilder = RequestBuilder.post(uri).setEntity(new StringEntity(body, "utf-8"));
//        requestBuilder.addHeader("Content-Type", "application/json;charset=utf-8");
//        requestBuilder.addHeader("X-CY-AppId","demo112");
//        requestBuilder.addHeader("X-CY-APIKey","112358");
//        requestBuilder.addParameter("param1","p1");
//        return requestBuilder;
//    }
}
