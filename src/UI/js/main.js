const PORT = 3000;
//function that takes query input and redirects to search results page
//Uses local storage to store/update the search query
function searchByQuery(queryInput,searchQuery){
    // Execute a function when the user presses a key on the keyboard
    queryInput.addEventListener("keypress", function(event) {
        if (event.key === "Enter") {
            // Get the value of the input field
            searchQuery = queryInput.value;
            event.preventDefault();
            searchQuery = removeSpecialCharacters(searchQuery);
            console.log("search query is: " + searchQuery);

            var myRequest = new XMLHttpRequest();
            myRequest.onreadystatechange = function(){
                console.log(myRequest.status + " readyState");
                if(myRequest.readyState == 4 && myRequest.status == 200){
                    var response = myRequest.responseText;
                    if(response == "false"){
                        console.log("no results were found");
                    }
                    else{
                        var jsonResponse = JSON.parse(response);
                        console.log(response);
                        console.log(jsonResponse);
                        console.log(jsonResponse.links);
                        sessionStorage.setItem("jSONRESPONSE", JSON.stringify(jsonResponse));
                        localStorage.setItem("SEARCHQUERY",searchQuery);
                        window.open("search_results.html", "_self");
                    }
                }
            };
            myRequest.open("GET", "http://localhost:3000/sites/" + searchQuery, true);
            myRequest.send();
        }
    });
}

let queryInput = document.getElementById("search-query-id");
let searchQuery;

searchByQuery(queryInput,searchQuery);



function removeSpecialCharacters(string){
    var specialCharacters = /[^a-zA-Z0-9\s]/g;
    return string.replace(specialCharacters, "");
}



//    document.querySelector("h1").style.color = 'blue';

//getting contents
//let inputValue = document.getElementById("trial-id");
// console.log(inputValue);
// console.log(inputValue);
// console.log(inputValue.innerHTML);
//inputValue.className = "input-class";
//inputValue.setAttribute('name', "trial-name");

