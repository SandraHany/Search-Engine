//import {searchByQuery} from "./main.js";
//const searchByQuery = require('./main.js');

var linksList = ["https://www.reddit.com/r/books/comments/4lugqb/books_that_changed_your_life_as_an_adult/"
                ,"https://www.reddit.com"
                ,"https://www.computerhope.com/jargon/u/url.htm"
                ,"https://stackoverflow.com/questions/21293456/scroll-horizontally-starting-from-right-to-left-with-css-overflowscroll"
                ,"https://www.reddit.com"
                ,"https://developer.mozilla.org/en-US/docs/Learn/HTML/Introduction_to_HTML/Creating_hyperlinks"
                ,"https://www.reddit.com/r/books/comments/4lugqb/books_that_changed_your_life_as_an_adult/"
                ,"https://www.reddit.com"
                ,"https://css-tricks.com/scroll-fix-content/"
                ,"https://www.computerhope.com/jargon/u/url.htm"
                ,"https://www.producthunt.com/posts/copy-all-urls"];

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






let maxWebsitesPerPage=10;
let currentPage=1;
let mainWebsiteDivision = document.getElementsByClassName("websites-container-class");
let paginationDivision = document.getElementsByClassName("pagination-class");


function createWebsite(item){
    let websiteDivision = document.createElement("div");
    let textNode = document.createTextNode(item);
    let textNode1 = document.createTextNode("3aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    let link = document.createElement("a");
    websiteDivision.className = "website-division-class";
    link.href = item;
    link.appendChild(textNode);
    websiteDivision.appendChild(link);
    websiteDivision.appendChild(textNode1);
    mainWebsiteDivision[0].appendChild(websiteDivision);
}

function displayWebsites(linksList, wrapper, maxWebsitesPerPage, currentPage) {
    wrapper.innerHTML= "";
    currentPage--;
    let start = currentPage*maxWebsitesPerPage;
    let end = start + maxWebsitesPerPage;
    let listItems = linksList.slice(start, end);
    for (let i = 0; i < listItems.length; i++) {
        createWebsite(listItems[i]);
    }
}
var flag = true;

function pagenation(linksList, wrapper, maxWebsitesPerPage){
    wrapper.innerHTML= "";
    let pageCount = Math.ceil(linksList.length/maxWebsitesPerPage);
    for (let i = 1; i < pageCount+1; i++) {
        let button = document.createElement("button");
        button.className = "pagination-button-class";
        button.innerText = i;
        if(currentPage==i){
            button.classList.add("active");
        }
        button.addEventListener("click", function(){
            currentPage = i;
            displayWebsites(linksList, mainWebsiteDivision[0], maxWebsitesPerPage, currentPage);
    
        });
        wrapper.appendChild(button);


    }
}



displayWebsites(linksList, mainWebsiteDivision[0], maxWebsitesPerPage, currentPage);
pagenation(linksList, paginationDivision[0], maxWebsitesPerPage);
console.log("heellloo");





