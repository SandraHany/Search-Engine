var linksList = JSON.parse(sessionStorage.getItem("jSONRESPONSE")).links;
// var linksList = ["https://stackoverflow.com/questions/7901760/how-can-i-get-the-title-of-a-webpage-given-the-url-an-external-url-using-jquer",
//                  "https://windows.php.net/download#php-8.1"];

console.log(...linksList);
console.log("hello");

let queryInput = document.getElementById("search-query-id");
let searchQuery;

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




function httpGetHTML(theUrl)
{
    afterLoad('https://google.com', function(html){
       console.log(html);
       return html;
    });   
}


function getTitle(url){
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = async function(){
       if (this.readyState == 4 && this.status == 200) {

            var result =  this.responseText;
            console.log(result);
            //console.log("yay");
            return result;
        }
      };
 
    xhttp.open("GET", "http://textance.herokuapp.com/title/" + url, true);
    xhttp.send();
}



async function fetchText(url) {
    let response = await fetch("http://textance.herokuapp.com/title/" + url).then(response => response.text());
    console.log(response);
    return response;
}



async function createWebsite(item){
    var title =  await fetchText(item);
    console.log(title);
    //var htmlResult = httpGetHTML(item);
    //console.log(htmlResult);
    let websiteDivision = document.createElement("div");
    websiteDivision.className = "website-division-class";

    let textLink = document.createTextNode(item);
    let textTitleLink = document.createTextNode(title);

    let link = document.createElement("a");

    link.href = item;

    link.appendChild(textLink);

    websiteDivision.appendChild(link);
    websiteDivision.appendChild(textTitleLink);
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





