// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.*;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    ArrayList<TimeRange> availableTimes = new ArrayList<TimeRange>();
    Collection<String> mandatoryAttendees = request.getAttendees();
    Collection<String> optionalAttendees = request.getOptionalAttendees();
    Integer meetingDuration = (int) request.getDuration();

    //if mandatory attendees exist, return optimal schedule if can include optionals. Otherwise return just the schedule for optional attendees.
    if (mandatoryAttendees.size()>0) {
      Collection<String> allAttendees = new HashSet<String>();
      allAttendees.addAll(mandatoryAttendees);
      allAttendees.addAll(optionalAttendees);
      Collection<TimeRange> optionalTimes = subgroupQuery(events,allAttendees,meetingDuration);
      if (optionalTimes.size()>0) return optionalTimes;
      return subgroupQuery(events,mandatoryAttendees,meetingDuration);
    } 
    return subgroupQuery(events,optionalAttendees,meetingDuration);
  }

  private Collection<TimeRange> subgroupQuery(Collection<Event> events, Collection<String> meetingAttendees, Integer meetingDuration) {
    Map<Integer,Integer> eventMarkers = new TreeMap<Integer,Integer>();
    ArrayList<TimeRange> availableTimes = new ArrayList<TimeRange>();
    int busyScore = 0;

    //Get all the attendees of meeting and check them through the events
    eventMarkers.put(TimeRange.START_OF_DAY,0);
    eventMarkers.put(TimeRange.END_OF_DAY+1,0);
    for(Event event: events) {
      for(String meetingAttendee:meetingAttendees) {
        if (event.getAttendees().contains(meetingAttendee)) {
          //Conflict detected, add the markers to the eventMarkers map
          int eventStart = event.getWhen().start();
          int eventEnd = event.getWhen().end();
          eventMarkers.computeIfAbsent(eventStart,k -> 0);
          eventMarkers.computeIfAbsent(eventEnd,k -> 0);
          eventMarkers.put(eventStart,eventMarkers.get(eventStart)+1);
          eventMarkers.put(eventEnd,eventMarkers.get(eventEnd)-1);
          break;
        }
      }
    }

    //Loop over events, and if attendee present in meeting, add the start and end interval to array list
    //We will then sort this array to keep track of start and ending points
    //For start, it will add one to the busyness score, for end, it will subtract one
    //We can schedule meetings only when the busyness score is 0

    Integer currentKey = null;
    Integer lastKey = null;

    for(Map.Entry<Integer,Integer> marker: eventMarkers.entrySet()) {
      lastKey = currentKey;
      currentKey = marker.getKey();
      if (lastKey == null) continue;
      busyScore += eventMarkers.get(lastKey);
      if (busyScore > 0) continue;
      TimeRange intervalConcerned = TimeRange.fromStartEnd(lastKey,currentKey,false);
      if (meetingDuration <= intervalConcerned.duration()) availableTimes.add(intervalConcerned);
    }

    return availableTimes;
  }
}
