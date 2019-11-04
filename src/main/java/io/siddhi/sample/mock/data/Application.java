/*
 *     Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *     WSO2 Inc. licenses this file to you under the Apache License,
 *     Version 2.0 (the "License"); you may not use this file except
 *     in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing,
 *    software distributed under the License is distributed on an
 *    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *    KIND, either express or implied.  See the License for the
 *    specific language governing permissions and limitations
 *    under the License.
 */
package io.siddhi.sample.mock.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class for mock data generator
 */
public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        LOGGER.info("Mock generator Started!");

        String uri = "0.0.0.0:8080";
        if (args.length > 0) {
            uri = args[0];
        }

        SalesStreamDataGenerator salesStreamDataGenerator = new SalesStreamDataGenerator(uri);
        salesStreamDataGenerator.generateData();
        salesStreamDataGenerator.sendData();

    }
}
