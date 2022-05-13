//import searchByQuery from './main.js';

let queryInput = document.getElementById("search-query-id");
let searchQuery;

// searchByQuery(queryInput,searchQuery);

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
