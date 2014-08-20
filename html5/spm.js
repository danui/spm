$(document).ready(function () {
    var spm = spmData;

    function fixupTitle() {
        if (spm.hasOwnProperty("projectName")) {
            $("title").html(spm.projectName);
        }
    }

    
    /*
    <div id="stories" class="padtop">
        <h1>stories (finalCount) 12 days</h1>
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
        entryHeader.innerHTML = entry.name 
            + " (" + entry.finalCount + ") "
            + (entry.duration+0.5).toFixed(0) + " days";
        
        entryChart = document.createElement("div");
        entryChart.setAttribute("id", chartId);
        entryChart.setAttribute("class", "chartarea");

        entryText = document.getElementById(preId);
        entryText.setAttribute("class", "textarea");

        content.appendChild(entryHeader);
        content.appendChild(entryChart);
        content.appendChild(entryText);
        myContents.appendChild(content);
        
        var plot = $.jqplot(chartId,  [ entry.todoPercents, entry.todoCounts ], {
            axes: {
                yaxis: { min: 0, numberTicks: 11 },
                y2axis: { min: 0, max: 1, numberTicks: 11}
            },
            series: [
                { 
                    yaxis: "y2axis", fill: true, fillToZero: true,
                    color: "#888", fillAlpha: 0.5
                },
                {
                    color: "#555"
                }
            ]
        });
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