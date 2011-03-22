/*
 * Copyright (c) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.silenciador.gcalendar;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Key;


/**
 * @author Yaniv Inbar
 */
public class CalendarFeed extends Feed {

  @Key("entry")
  public List<CalendarEntry> calendars = new ArrayList<CalendarEntry>();

  public static CalendarFeed executeGet(HttpTransport transport, CalendarUrl url)
      throws IOException {
    return (CalendarFeed) Feed.executeGet(transport, url, CalendarFeed.class);
  }
}
