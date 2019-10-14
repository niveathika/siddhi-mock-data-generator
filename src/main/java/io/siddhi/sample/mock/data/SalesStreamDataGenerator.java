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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Generate data for http source attached to ,
 * define stream SalesStream(timestamp long, categoryName string, productName string, sellerName string, quantity int);.
 */
public class SalesStreamDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SalesStreamDataGenerator.class);

    private String urlTemplate = "http://0.0.0.0:{{port}}/sales";
    private URL sinkURL;
    private boolean isConfigValid = true;
    private boolean isDataGenerated = false;

    SalesStreamDataGenerator(String port) {
        try {
            String url = urlTemplate.replace("{{port}}", port);
            this.sinkURL = new URL(url);
        } catch (MalformedURLException e) {
            LOGGER.error("Invalid URL given as second parameter! Cannot start generator");
            isConfigValid = false;
        }
    }

    public void generateData() {
        if (!this.isConfigValid) {
            return;
        }

        this.isDataGenerated = true;
        LOGGER.info("Generate Data  successfully! ");
    }


    public void sendData() {
        if (!this.isDataGenerated) {
            return;
        }

        LOGGER.info("Send success! " + this.sinkURL);
    }

}
