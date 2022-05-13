//import {searchByQuery} from "./main.js";
//const searchByQuery = require('./main.js');

var linksList = ["https://www.reddit.com/r/books/comments/4lugqb/books_that_changed_your_life_as_an_adult/"
                ,"https://www.reddit.com"
                ,"https://www.reddit.com"
                ,"https://www.reddit.com"
                ,"https://www.reddit.com"
                ,"https://www.reddit.com"
                ,"https://www.reddit.com/r/books/comments/4lugqb/books_that_changed_your_life_as_an_adult/"
                ,"https://www.reddit.com"];

let queryInput = document.getElementById("search-query-id");
let searchQuery;
//searchByQuery(queryInput,searchQuery);

window.onload = function() {
    searchQuery = localStorage.getItem("SEARCHQUERY");
  //  localStorage.setItem("SEARCHQUERY",searchQuery);
    console.log("search query is " + searchQuery);
    queryInput.value = searchQuery;
};


//Execute a function when the user presses a key on the keyboard
queryInput.addEventListener("keypress", function(event) {
    if (event.key === "Enter") {
        // Get the value of the input field
        searchQuery = queryInput.value;
        event.preventDefault();
        console.log("search query is " + searchQuery);

        localStorage.setItem("SEARCHQUERY",searchQuery);
        window.open("search_results.html", "_self");
    }
});


////////////////////////////////////////////////////////////////////////////////////////////////////////






let websitesPerPage=10;
let currentPage=1;
let mainWebsiteDivision = document.getElementsByClassName("websites-container-class");
let paginationDivision = document.getElementsByClassName("pagination-class");


function displayWebsites(linksList, wrapper, websitesPerPage, currentPage) {
    wrapper.innerHTML= "";
    currentPage--;
    let start = currentPage*websitesPerPage;
    let end = start + websitesPerPage;
    let listItems = linksList.slice(start, end);
    for (let i = 0; i < listItems.length; i++) {
        let websiteDivision = document.createElement("div");
        let textNode = document.createTextNode(listItems[i]);
        let textNode1 = document.createTextNode("3aaaaa\naaaaaa\naaaaaaaa\naaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        let link = document.createElement("a");
        websiteDivision.className = "website-division-class";
        link.href = listItems[i];
        link.appendChild(textNode);
        websiteDivision.appendChild(link);
        websiteDivision.appendChild(textNode1);
        mainWebsiteDivision[0].appendChild(websiteDivision);
    }
}

displayWebsites(linksList, mainWebsiteDivision[0], websitesPerPage, currentPage);






