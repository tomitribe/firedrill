/*
 * Tomitribe Confidential
 *
 * Copyright Tomitribe Corporation. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.tomitribe.firedrill;

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
            for (int i = 0; i < 5; i++) list.add(allUsers.get());

            // Make some more forgetful than others
            {
                final String user = list.get(0);
                for (int i = 0; i < 3; i++) list.add(user);
            }
            {
                final String user = list.get(1);
                for (int i = 0; i < 5; i++) list.add(user);
            }

            forgotPassword = new WeightedRandomResult<>(list);
        }

        // Now let's pick some who are going to get a lot of 403s
        // Some more than others

        {
            final List<String> list = new ArrayList<String>();

            // Add a few forgetful users
            for (int i = 0; i < 4; i++) list.add(allUsers.get());

            // Make some more forgetful than others
            {
                final String user = list.get(0);
                for (int i = 0; i < 4; i++) list.add(user);
            }
            {
                final String user = list.get(1);
                for (int i = 0; i < 9; i++) list.add(user);
            }
            {
                final String user = list.get(2);
                for (int i = 0; i < 3; i++) list.add(user);
            }

            lackPermissions = new WeightedRandomResult<>(list);
        }


    }
}
