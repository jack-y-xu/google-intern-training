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

google.charts.load('current',{'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);

function setup() {
  createMap();
  getComments();
}

// getComments function fetches the comments from /data and appends them to a list
async function getComments() {
  fetch('/data').then(response => response.json()).then((comments) => {
    const commentListElement = document.getElementById('comments-list');
    commentListElement.innerHTML = "";
    for(commentID=0;commentID<comments.length;commentID++) {
      var curComment = document.createElement("Li");
      curComment.innerHTML = comments[commentID];
      commentListElement.append(curComment);
    } 
  });
}

/** Fetches bigfoot sightings data and uses it to create a chart. */
function drawChart() {
  fetch('/bigfoot-data').then(response => response.json())
  .then((bigfootSightings) => {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Year');
    data.addColumn('number', 'Sightings');
    Object.keys(bigfootSightings).forEach((year) => {
      data.addRow([year, bigfootSightings[year]]);
    });

    const options = {
      'title': 'Bigfoot Sightings',
      'width':600,
      'height':500
    };
    
    const chart = new google.visualization.LineChart(
        document.getElementById('chart-container'));
    chart.draw(data, options);
  });
}

function createMap() {
  const map = new google.maps.Map(
      document.getElementById('map'),
      {center: {lat: 37.422, lng: -122.084}, zoom: 16});
  console.log("RUNNING");
  const trexMarker = new google.maps.Marker({
    position: {lat: 37.421903, lng: -122.084674},
    map: map,
    title: 'Stan the T-Rex'
  });
}