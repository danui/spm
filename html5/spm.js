$(document).ready(function () {
    var spm = spmData;

    function fixupTitle() {
        if (spm.hasOwnProperty("projectName")) {
            $("title").html(spm.projectName);
            $("#myBrand").html(spm.projectName);
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
    
    function createKeyValue(key, value) {
        var span = document.createElement("span");
        var keyElement = document.createElement("b");
        keyElement.innerHTML = key + ": ";
        var valueElement = document.createTextNode(value);
        span.appendChild(keyElement);
        span.appendChild(valueElement);
        return span;
    }
    
    function humanDays(days) {
        var t = days;
        var u = "day";
        if (t < 1) {
            // If less than a day, count in hours.
            t *= 24;
            u = "hour";
        } else if (t > 14) {
            // If more than 14 days, count in weeks.
            t /= 7;
            u = "week";
        } else if (t > 365/4) {
            // If more than 1/4 year, count in months.
            t = t * 12.0 / 365;
            u = "month";
        }

        t += 0.5;
        t = t.toFixed(0);
        if (t != 1) {
            u += "s";
        }
        return t + " " + u;
    }
    
    function insertEntry(entry) {
        var myContents,
            content,
            entryHeader,
            entryChart,
            entryText,
            entryStats;
        
        var id = entry.id;
        var chartId = "chart" + id;
        var preId = "text" + id;
        
        myContents = document.getElementById("myContents");
        content = document.createElement("div");
        content.setAttribute("id", entry.id);
        content.setAttribute("class", "padtop");
        
        entryHeader = document.createElement("h1");
        entryHeader.innerHTML = entry.name;
        
        entryStats = document.createElement("p");
        (function () {
            var msg;
            var n = entry.finalTotal;
            var k = n - entry.finalCount;
            if (n > 0) {
                var p = 100.0 * k / n;
                p = p.toFixed(0);
                msg = k + " of " + n + " items ("+p+"%)";
            } else {
                msg = "n/a";
            }
            entryStats.appendChild(createKeyValue("Completed", msg));
        }());
        (function () {
            if (entryStats.hasChildNodes()) {
                entryStats.appendChild(document.createTextNode(", "));
            }
            entryStats.appendChild(createKeyValue("Duration", humanDays(entry.duration)));
        }());
        if (entry.finalTotal > 0 &&
            entry.finalTotal > entry.finalCount &&
            entry.finalCount > 0 &&
            entry.duration > 0)
        {
            (function () {
                var k = entry.finalCount;
                var n = entry.finalTotal;
                var t = entry.duration;
                var eta = t * n / (n-k);
                if (entryStats.hasChildNodes()) {
                    entryStats.appendChild(document.createTextNode(", "));
                }
                entryStats.appendChild(createKeyValue("ETA", humanDays(eta)));
            }());
        }
        
        entryChart = document.createElement("div");
        entryChart.setAttribute("id", chartId);
        entryChart.setAttribute("class", "chartarea");

        entryText = document.getElementById(preId);
        if (entryText !== null) {
            entryText.setAttribute("class", "textarea");
        }

        content.appendChild(entryHeader);
        content.appendChild(entryStats);
        content.appendChild(entryChart);
        if (entryText !== null) {
            content.appendChild(entryText);
        }
        myContents.appendChild(content);
        
        var plot = $.jqplot(chartId,  [ entry.todoPercents, entry.todoCounts ], {
            axes: {
                yaxis: { min: 0, numberTicks: 11 },
                y2axis: { min: 0, max: 1, numberTicks: 11}
            },
            series: [
                { 
                    yaxis: "y2axis", fill: true, fillToZero: true,
                    color: "#888", fillAlpha: 0.25
                },
                {
                    color: "#555"
                }
            ]
        });
        $(window).resize(function() {
            plot.replot( { resetAxes: true } );
        });

        var dropDownName = entry.name;
        if (entry.finalCount > 0) {
            dropDownName += " (" + entry.finalCount + ")";
        }
        insertDropdownMenu(entry.id, dropDownName);
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