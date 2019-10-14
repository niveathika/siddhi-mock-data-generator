/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.siddhi.sample.mock.data;

import io.siddhi.core.SiddhiAppRuntime;
import io.siddhi.core.SiddhiManager;
import io.siddhi.core.stream.input.InputHandler;
import io.siddhi.extension.io.http.sink.HttpSink;
import io.siddhi.extension.map.json.sinkmapper.JsonSinkMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Generate data for http source attached to ,
 * define stream SalesStream(timestamp long, categoryName string, productName string, sellerName string, quantity int);.
 */
public class SalesStreamDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SalesStreamDataGenerator.class);
    private static final String URL_TEMPLATE = "http://0.0.0.0:{{port}}/sales";

    private String sinkURL;
    private ArrayList<Object[]> generatedEvents;
    private boolean isConfigValid = true;

    SalesStreamDataGenerator(String port) {
        try {
            String url = URL_TEMPLATE.replace("{{port}}", port);
            this.sinkURL = new URL(url).toString();
            generatedEvents = new ArrayList<>();
        } catch (MalformedURLException e) {
            LOGGER.error("Invalid URL given as first parameter! Cannot start generator.");
            isConfigValid = false;
        }
    }

    public void generateData() {
        if (!this.isConfigValid) {
            return;
        }

        generatedEvents.add(new Object[]{System.currentTimeMillis(), "clothes", "shirt", "seller1", 1});
        generatedEvents.add(new Object[]{System.currentTimeMillis(), "clothes", "blouse", "seller1", 2});
        LOGGER.info("Generate Data successfully! ");
    }


    public void sendData() {
        if (this.generatedEvents.size() == 0) {
            return;
        }

        SiddhiManager siddhiManager = new SiddhiManager();

        siddhiManager.setExtension("sink:http", HttpSink.class);
        siddhiManager.setExtension("sinkMapper:json", JsonSinkMapper.class);

        String streams =
                "define stream InputStream" +
                "(timestamp long, categoryName string, productName string, sellerName string, quantity int);" +
                "@sink(type='http',publisher.url='" + this.sinkURL + "', " +
                "@map(type='json')) " +
                "define stream PublisherStream" +
                "(timestamp long, categoryName string, productName string, sellerName string, quantity int);";

        String queries =
                "from InputStream " +
                "insert into PublisherStream;";

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + queries);
        InputHandler inputStream = siddhiAppRuntime.getInputHandler("InputStream");


        siddhiAppRuntime.start();
        try {
            for (Object[] generatedEvent : generatedEvents) {
                inputStream.send(generatedEvent);
            }
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {
            //ignored
        }

        siddhiAppRuntime.shutdown();
        siddhiManager.shutdown();

        LOGGER.info("Send success! " + this.sinkURL);
    }

}
