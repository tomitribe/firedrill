/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tomitribe.firedrill.util;

import java.util.ArrayList;
import java.util.List;

public class Users {

    private static WeightedRandomResult<String> allUsers;
    private static WeightedRandomResult<String> forgotPassword;
    private static WeightedRandomResult<String> lackPermissions;

    /**
     * Point of this is to show how to select a set of users out of the global set
     * and have them be the designated one to have certain behavior happen to them
     *
     * The fun thing is that some users may show up in a few different scenarios.
     * They might frequently lose their password and not have permissions
     *
     * This technique can work for anywhere we need to pick usernames, etc
     */
    static {

        // we first create a global list of users

        allUsers = new WeightedRandomResult<>(
                "chernandez",
                "tveronezi",
                "aeiras",
                "jgallimore",
                "jfisher",
                "jmonteiro",
                "rmannibucau",
                "osantana",
                "agumbrecht"
        );

        // Now let's pick some to be the ones who forget their password
        // Some more than others

        {
            final List<String> list = new ArrayList<String>();

            // Add a few forgetful users
            for (int i = 0; i < 5; i++) {
                list.add(allUsers.get());
            }

            // Make some more forgetful than others
            {
                final String user = list.get(0);
                for (int i = 0; i < 3; i++) {
                    list.add(user);
                }
            }
            {
                final String user = list.get(1);
                for (int i = 0; i < 5; i++) {
                    list.add(user);
                }
            }

            forgotPassword = new WeightedRandomResult<>(list);
        }

        // Now let's pick some who are going to get a lot of 403s
        // Some more than others

        {
            final List<String> list = new ArrayList<String>();

            // Add a few forgetful users
            for (int i = 0; i < 4; i++) {
                list.add(allUsers.get());
            }

            // Make some more forgetful than others
            {
                final String user = list.get(0);
                for (int i = 0; i < 4; i++) {
                    list.add(user);
                }
            }
            {
                final String user = list.get(1);
                for (int i = 0; i < 9; i++) {
                    list.add(user);
                }
            }
            {
                final String user = list.get(2);
                for (int i = 0; i < 3; i++) {
                    list.add(user);
                }
            }

            lackPermissions = new WeightedRandomResult<>(list);
        }


    }
}
