window.onload = function() {
    let queryInput = document.getElementById("search-query-id");
    let searchQuery = localStorage.getItem("SEARCHQUERY");
    console.log("search query is " + searchQuery);
    queryInput.value = searchQuery;
};