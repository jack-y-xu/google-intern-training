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

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['The bartender says: "We don\'t serve faster than light particles here. A tachyon walks into a bar.', 'Why couldn\'t the bike stand on its own legs? It is two tired', 'cats are great'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

async function start() {
  getIntro();
  getComments();
}
async function getIntro() {
  //const response = await fetch('/data');
  //const quote = await response.text();
  document.getElementById('intro-container').innerText = "This is Jack Xu's portfolio";
}

async function getComments() {
  fetch('/data').then(response => response.json()).then((comments) => {
    console.log(comments.length);
    const commentListElement = document.getElementById('comments-list');
    commentListElement.innerHTML = '';
    for(commentID=0;commentID<comments.length;commentID++) {
      var curComment = document.createElement("Li");
      curComment.innerHTML = comments[commentID];
      commentListElement.append(curComment);
    }
      
  });
}

