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

import java.util.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles comments from the user and returns a list of current comments **/
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private static final String BUTTON_PARAMETER = "button";
  private static final String COMMENT_BOX_PARAMETER = "comment-box";
  private static final String CLEAR_BUTTON_VALUE = "clear-all-comments";
  private static final String SUBMIT_BUTTON_VALUE = "submit";
  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp",SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);
    ArrayList<String> comments = new ArrayList<String>();
    for(Entity entity:results.asIterable()) {
      comments.add((String)entity.getProperty("content"));
    }
    String json = convertToJson(comments);
    response.setContentType("application/json");
    response.getWriter().println(json);
  }

  private String convertToJson(ArrayList<String> arr) {
    Gson gson = new Gson();
    String json = gson.toJson(arr);
    return json;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String choice = request.getParameter(BUTTON_PARAMETER);
    if (choice.equals(SUBMIT_BUTTON_VALUE)) {
      String content = request.getParameter(COMMENT_BOX_PARAMETER);
      if (content.length() > 0) {
        long timestamp = System.currentTimeMillis();
        Entity taskEntity = new Entity("Comment");
        taskEntity.setProperty("content", content);
        taskEntity.setProperty("timestamp", timestamp);
        datastore.put(taskEntity);
      } else {
        response.setContentType("text/html");
        response.getWriter().println("Please enter a non-empty comment.");
        return;
      }
    } else if (choice.equals(CLEAR_BUTTON_VALUE)) {
        Query mydeleteq = new Query();
        PreparedQuery pq = datastore.prepare(mydeleteq);
        for (Entity result : pq.asIterable()) {
          datastore.delete(result.getKey());      
        } 
    }
    response.sendRedirect("/index.html");
  }
}



