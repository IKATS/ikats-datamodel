/**
 * LICENSE:
 * --------
 * Copyright 2017 CS SYSTEMES D'INFORMATION
 * 
 * Licensed to CS SYSTEMES D'INFORMATION under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. CS licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 * @author Fabien TORAL <fabien.toral@c-s.fr>
 * @author Fabien TORTORA <fabien.tortora@c-s.fr>
 * 
 */

package fr.cs.ikats.datamanager.client.opentsdb.importer;

import static org.junit.Assert.fail;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.junit.Test;

/**
 * test the internal DateFormat of the commonDatajsonizer.
 */
public class DateFormatTest {

    /**
     * test several input format to parse.
     */
    @Test
    public void testFormatIso() {
        DateFormat format = CommonDataJsonIzer.getDateFormat();
        try {
            Date date = null;

            date = format.parse("2012-07-04T12:00:00-0700");
            System.out.println(format.format(date));
            date = format.parse("2012-07-04T12:00:00+0200");
            System.out.println(format.format(date));
            date = format.parse("2012-07-04T12:00:00.500+0200");
            System.out.println(format.format(date));
            date = format.parse("2012-07-04T12:00:00.500+02:00");
            System.out.println(format.format(date));
            date = format.parse("2012-07-04T12:00+0200");
            System.out.println(format.format(date));
            // WITHOUT TIMEZONE
            date = format.parse("2012-07-04T12:00:00-0000");
            System.out.println(format.format(date));
            date = format.parse("2012-07-04T12:00:00.100");
            System.out.println(format.format(date));
            date = format.parse("2012-07-04T12:00:00.1");
            System.out.println(format.format(date));
            date = format.parse("2012-07-04T12:00:00");
            System.out.println(format.format(date));
        }
        catch (ParseException e) {
            e.printStackTrace();
            fail();
        }

    }

   
}

