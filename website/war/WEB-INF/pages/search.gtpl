<% include '/WEB-INF/includes/header.gtpl?title=Search' %>

<h1>Search the Caelyf website</h1>

<div>
    From this page, you will be able to search the website, the GitHub project page, and the Google Group archive.
</div>


<div id="cse">Loading...</div>

<script src="http://www.google.com/jsapi" type="text/javascript"></script>
<script type="text/javascript">
    google.load('search', '1', {language:'en'});
    google.setOnLoadCallback(function () {
        var customSearchControl = new google.search.CustomSearchControl('013939896723962546743:illkwwhpfk0');
        customSearchControl.setResultSetSize(google.search.Search.FILTERED_CSE_RESULTSET);
        customSearchControl.draw('cse');
    }, true);
</script>

<style type="text/css">
table tr td {
    border: none;
}

table.gsc-search-box {
    width: 500px;
}

table.gsc-branding {
    width: 500px;
}

table.gsc-branding {
    padding-top: 10px;
}

input.gsc-search-button {
    color: white;
    background-color: #8C5CCB;

    border: 3px gray ridge;

       -moz-box-shadow: 4px 4px 5px #888;
    -webkit-box-shadow: 4px 4px 5px #888;
            box-shadow: 4px 4px 5px #888;
}

#cse {
    margin: 20px;
}

.gsc-control-cse {
    font-family: Arial, sans-serif;
    font-size: 1em;
}

input.gsc-input {
    border: 3px solid #8C5CCB;

    width: 400px;
    height: 30px;
    font-size: 1.2em;

       -moz-box-shadow: 4px 4px 5px #888;
    -webkit-box-shadow: 4px 4px 5px #888;
            box-shadow: 4px 4px 5px #888;

    -webkit-border-radius: 10px;
       -moz-border-radius: 10px;
            border-radius: 10px;

}

input.gsc-search-button {
    border-color: #666666;
}

.gsc-tabHeader.gsc-tabhInactive {
    border-color: #E9E9E9;
}

.gsc-tabHeader.gsc-tabhActive {
    border-top-color: #FF9900;
    border-left-color: #E9E9E9;
    border-right-color: #E9E9E9;
}

.gsc-tabsArea {
    border-color: #E9E9E9;
}

.gsc-webResult.gsc-result {
    border-color: #FFFFFF;
}

.gsc-webResult.gsc-result:hover {
    border-color: #FFFFFF;
}

.gs-webResult.gs-result a.gs-title:link,
.gs-webResult.gs-result a.gs-title:link b {
    color: #8C5CCB;
}

.gs-webResult.gs-result a.gs-title:visited,
.gs-webResult.gs-result a.gs-title:visited b {
    color: #8C5CCB;
}

.gs-webResult.gs-result a.gs-title:hover,
.gs-webResult.gs-result a.gs-title:hover b {
    color: #8C5CCB;
}

.gs-webResult.gs-result a.gs-title:active,
.gs-webResult.gs-result a.gs-title:active b {
    color: #8C5CCB;
}

.gsc-cursor-page {
    color: #0000CC;
}

a.gsc-trailing-more-results:link {
    color: #8C5CCB;
}

.gs-webResult.gs-result .gs-snippet {
    color: #000000;
}

.gs-webResult.gs-result .gs-visibleUrl {
    color: #008000;
}

.gs-webResult.gs-result .gs-visibleUrl-short {
    color: #008000;
}

.gs-webResult.gs-result .gs-visibleUrl-short {
    display: none;
}

.gs-webResult.gs-result .gs-visibleUrl-long {
    display: block;
}

.gsc-cursor-box {
    border-color: #FFFFFF;
}

.gsc-results .gsc-cursor-page {
    border-color: #E9E9E9;
}

.gsc-results .gsc-cursor-page.gsc-cursor-current-page {
    border-color: #FF9900;
}

.gs-promotion.gs-result {
    border-color: #336699;
}

.gsc-resultsHeader {
    border: 1px solid #8C5CCB;
}

.gsc-cursor-box {
    border-top: 2px solid #8C5CCB;
    padding-top: 2px;
}

.gs-promotion.gs-result a.gs-title:link,
.gs-promotion.gs-result a.gs-title:visited,
.gs-promotion.gs-result a.gs-title:hover,
.gs-promotion.gs-result a.gs-title:active {
    color: #8C5CCB;
}

.gs-promotion.gs-result .gs-snippet {
    color: #000000;
}

.gs-promotion.gs-result .gs-visibleUrl,
.gs-promotion.gs-result .gs-visibleUrl-short {
    color: #008000;
}
</style>

<% include '/WEB-INF/includes/footer.gtpl' %>