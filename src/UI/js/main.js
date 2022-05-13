
let queryInput = document.getElementById("search-query-id");
let searchQuery;


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




//    document.querySelector("h1").style.color = 'blue';

//getting contents
//let inputValue = document.getElementById("trial-id");
// console.log(inputValue);
// console.log(inputValue);
// console.log(inputValue.innerHTML);
//inputValue.className = "input-class";
//inputValue.setAttribute('name', "trial-name");

