
//function that takes query input and redirects to search results page
//Uses local storage to store/update the search query
function searchByQuery(queryInput,searchQuery){
    // Execute a function when the user presses a key on the keyboard
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
}

let queryInput = document.getElementById("search-query-id");
let searchQuery;

searchByQuery(queryInput,searchQuery);
module.exports = searchByQuery;





//    document.querySelector("h1").style.color = 'blue';

//getting contents
//let inputValue = document.getElementById("trial-id");
// console.log(inputValue);
// console.log(inputValue);
// console.log(inputValue.innerHTML);
//inputValue.className = "input-class";
//inputValue.setAttribute('name', "trial-name");

