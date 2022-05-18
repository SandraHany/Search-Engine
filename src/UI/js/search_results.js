var linksList = JSON.parse(sessionStorage.getItem("jSONRESPONSE")).links;
// var linksList = ["https://stackoverflow.com/questions/7901760/how-can-i-get-the-title-of-a-webpage-given-the-url-an-external-url-using-jquer",
//                  "https://windows.php.net/download#php-8.1"];


let queryInput = document.getElementById("search-query-id");
let searchQuery;
const first = "first";
const last = "last";

window.onload = function() {
    searchQuery = localStorage.getItem("SEARCHQUERY");
    console.log("search query is " + searchQuery);
    queryInput.value = searchQuery;
};


//Execute a function when the user presses a key on the keyboard
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
                    mainWebsiteDivision[0].innerHTML = "No results were found";
                    var skipLine = document.createElement("br");
                    mainWebsiteDivision[0].appendChild(skipLine);
                }
                else{
                    var jsonResponse = JSON.parse(response);
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

function removeSpecialCharacters(string){
    var specialCharacters = /[^a-zA-Z0-9\s]/g;
    return string.replace(specialCharacters, "");
}


////////////////////////////////////////////////////////////////////////////////////////////////////////






let maxWebsitesPerPage=10;
let currentPage=1;
let mainWebsiteDivision = document.getElementsByClassName("websites-container-class");
let paginationDivision = document.getElementsByClassName("pagination-class");




function httpGetHTML(theUrl)
{
    afterLoad('https://google.com', function(html){
       console.log(html);
       return html;
    });   
}


function getTitle(url){
    var xhttp = new XMLHttpRequest();
    var result;
    xhttp.onreadystatechange = async function(){
       if (this.readyState == 4 && this.status == 200) {

            result =  this.responseText;
            console.log(result);
            return result;
        }
        else
        {
            result = "";
            return result;
        }
      };
 
    xhttp.open("GET", "http://textance.herokuapp.com/title/" + url, true);
    xhttp.send();
}



async function fetchTitle(url) {
    let response = await fetch("http://textance.herokuapp.com/title/" + url).then(response => response.text());
    //console.log(response);
    return response;
}



async function createWebsite(item){
    var title =  await fetchTitle(item);
    //console.log(title);
    //var htmlResult = httpGetHTML(item);
    //console.log(htmlResult);
    let websiteDivision = document.createElement("div");
    websiteDivision.className = "website-division-class";

    let websiteSubDivision = document.createElement("div");
    let textLink = document.createTextNode(item);
    let textTitleLink = document.createTextNode(title);
    websiteSubDivision.className = "title-link-class";

    let link = document.createElement("a");

    link.href = item;
    textTitleLink.href = item;

    link.appendChild(textLink);

    websiteSubDivision.appendChild(textTitleLink);
    websiteDivision.appendChild(link);
    websiteDivision.appendChild(websiteSubDivision);
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



function pagenation(linksList, wrapper, maxWebsitesPerPage){
    wrapper.innerHTML= "";
    let pageCount = Math.ceil(linksList.length/maxWebsitesPerPage);

    let startPage = currentPage - Math.floor(maxWebsitesPerPage/2);
    let endPage = currentPage + Math.floor(maxWebsitesPerPage/2);

    if(startPage < 1){
        startPage = 1;
        endPage = maxWebsitesPerPage;
    }
    if(endPage > pageCount){
        endPage = pageCount;
        startPage = pageCount - maxWebsitesPerPage + 1;
        if(startPage < 1){
            startPage = 1;
        }
    }

    for (let i = startPage; i <= endPage; i++) {
        if(i == startPage){
            createButton(first,linksList ,wrapper, maxWebsitesPerPage);
        }
        createButton(i,linksList ,wrapper, maxWebsitesPerPage);
        if(i == endPage){
            createButton(last,linksList ,wrapper, maxWebsitesPerPage);
        }
    }
}

function createButton(i , linksList, wrapper, maxWebsitesPerPage){
    let button = document.createElement("button");
    button.innerText = i;
    if(i != first && i != last){
        button.className = "pagination-button-class";
        if(currentPage==i){
            button.classList.add("active");
        }
    }

    button.addEventListener("click", function(){
        if (i == first){
            button.className = "first-last-button-class";
            currentPage = 1;
        }
        else if(i == last){
            button.className = "first-last-button-class";
            currentPage = Math.ceil(linksList.length/maxWebsitesPerPage);
        }
        else{
            currentPage = i;
        }
        displayWebsites(linksList, mainWebsiteDivision[0], maxWebsitesPerPage, currentPage);
        pagenation(linksList, paginationDivision[0], maxWebsitesPerPage);

    });
    wrapper.appendChild(button);
}



displayWebsites(linksList, mainWebsiteDivision[0], maxWebsitesPerPage, currentPage);
pagenation(linksList, paginationDivision[0], maxWebsitesPerPage);





