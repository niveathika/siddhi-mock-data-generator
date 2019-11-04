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
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

/**
 * Generate data for http source attached to ,
 * define stream SalesStream(timestamp long, categoryName string, productName string, sellerName string, quantity int);.
 */
public class SalesStreamDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SalesStreamDataGenerator.class);
    private static final String URL_TEMPLATE = "http://{{uri}}/sales";
    private static final String[] category = {"Electronics", "Toys", "Clothes", "Accessories"};
    private static final String[] electronicProducts = {"Phone", "Earphone", "Ear-buds", "Tablet", "Watch", "Camera"};
    private static final String[] toysProducts = {"Building-Blocks", "Pulshie", "Softball", "Rattle", "Play-doh"};
    private static final String[] clothesProducts = {"Shirt", "Trousers", "Skirt", "Blouse", "Shorts"};
    private static final String[] accessoriesProducts = {"Earring", "Watch", "Necklace", "Hairband", "Clips"};
    private static final String[] sellerNames = {"Harry", "Ron", "Harmonie", "Fred", "George", "Malfoy", "Dumbledore",
                                                "Lucius", "Molly", "Lupin"};

    private String sinkURL;
    private ArrayList<Object[]> generatedEvents;
    private boolean isConfigValid = true;

    SalesStreamDataGenerator(String uri) {
        try {
            String url = URL_TEMPLATE.replace("{{uri}}", uri);
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

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        calendar.set(year, month, day, hour, 0);

        long gmtTimestamp = calendar.getTimeInMillis();
        Random categoryRandom = new Random();
        Random productRandom = new Random();
        Random sellerRandom = new Random();

        // 5 days before
        long mockDataStart = gmtTimestamp - 432000000;


        int eventCount = 0;
        while (mockDataStart <= gmtTimestamp) {
            int categoryIndex = categoryRandom.nextInt(4);
            int productIndex = productRandom.nextInt(5);
            int sellerIndex = sellerRandom.nextInt(10);

            String sellerName = sellerNames[sellerIndex];
            String categoryValue = category[categoryIndex];
            String productValue;
            switch (categoryIndex) {
                case 0:
                    productValue = electronicProducts[productIndex];
                    break;
                case 1:
                    productValue = toysProducts[productIndex];
                    break;
                case 2:
                    productValue = clothesProducts[productIndex];
                    break;
                case 3:
                    productValue = accessoriesProducts[productIndex];
                    break;
                default:
                    productValue = "";
            }

            generatedEvents.add(
                        new Object[]{mockDataStart, categoryValue, productValue, sellerName, (productIndex + 1) * 10});
            eventCount++;
            mockDataStart = mockDataStart + 3600000;
            LOGGER.info("Event '" + eventCount + "' generated successfully");
        }
        LOGGER.info("Generate Data successfully! ");
    }


    public void sendData() {
        if (this.generatedEvents.size() == 0) {
            return;
        }

        LOGGER.info("Sending data to '" + this.sinkURL + "'.");

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

        LOGGER.info("Successfully send data!");
    }

}
