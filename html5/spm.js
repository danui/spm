$(document).ready(function () {
    var spm = spmData;

    function fixupTitle() {
        $("title").html("Hello");
    }

    
    /*
    <div id="stories" class="padtop">
        <h1>stories</h1>
        <div id="stories-chart" class="chartarea"></div>
        <pre id="stories-text">
        </pre>
    </div>
    */
    
    function insertDropdownMenu(targetId, targetName) {
        var myDropdownMenu = document.getElementById("myDropdownMenu");
        var listElement = document.createElement("li");
        var anchorElement = document.createElement("a");
        anchorElement.setAttribute("href", "#"+targetId);
        anchorElement.innerHTML = targetName;
        listElement.appendChild(anchorElement);
        myDropdownMenu.appendChild(listElement);
    }
    
    function insertEntry(entry) {
        var myContents,
            content,
            entryHeader,
            entryChart,
            entryText;
        
        var id = entry.id;
        var chartId = "chart" + id;
        var preId = "text" + id;
        
        myContents = document.getElementById("myContents");
        content = document.createElement("div");
        content.setAttribute("id", entry.id);
        content.setAttribute("class", "padtop");
        
        entryHeader = document.createElement("h1");
        entryHeader.innerHTML = entry.name;
        
        entryChart = document.createElement("div");
        entryChart.setAttribute("id", chartId);
        entryChart.setAttribute("class", "chartarea");

        if (false) {
            entryText = document.createElement("pre");
            entryText.setAttribute("id", preId);
            var s = "";
            var i, n;
            n = entry.text.length;
            for (i = 0; i < n; ++i) {
                s += entry.text[i] + "\n";
            }
            entryText.innerHTML = s;
        } else {
            entryText = document.getElementById(preId);
        }

        content.appendChild(entryHeader);
        content.appendChild(entryChart);
        content.appendChild(entryText);
        myContents.appendChild(content);
        
        var plot = $.jqplot(chartId,  [ entry.data ]);
        $(window).resize(function() {
            plot.replot( { resetAxes: true } );
        });

        insertDropdownMenu(entry.id, entry.name);
    }
    
    function main() {
        var i;
        fixupTitle();
        for (i = 0; i < spm.entries.length; ++i) {
            insertEntry(spm.entries[i]);
        }
    }
    
    main();
});