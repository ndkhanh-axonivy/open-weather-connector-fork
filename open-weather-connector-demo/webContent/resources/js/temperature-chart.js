const ClickPositionDetector = {
	id: 'clickPositionDetector',
    afterEvent: function (chart, event, options) {
        if (event.event.type === 'click') {
            var datasets = chart.config.data.datasets;
            datasets.forEach(function (dataset, datasetIndex) {
                var meta = chart.getDatasetMeta(datasetIndex);
                if (meta.hidden) {
                    return; // Skip hidden datasets
                }
				
               	for (var index = 0; index < meta.data.length - 1; index++) {

                    var middlePointX = (meta.data[index + 1].x + meta.data[index].x) / 2;
					console.log(middlePointX)
                    if (event.event.x <= middlePointX) {
                        console.log('Hovered or clicked on hidden column:', datasetIndex, index);
                        break;
                    } else if (
                        index == meta.data.length - 2
                    ) {
                        console.log('END Hovered or clicked on hidden column:', datasetIndex, index + 1);
                        break;
                    }
                }
            });
        }
    }
};

function chartExtender() {
	//Register plugin datalabels
	jQuery.extend(true, this.cfg.config, {plugins: [ChartDataLabels, ClickPositionDetector]});
	let data = [...this.cfg.config.data.datasets[0].data];
    // copy the config options into a variable
    let options = jQuery.extend(true, {}, this.cfg.config.options);
    options = {
    	scales: {
            y: {
                display: false, // Hide y-axis
                min: Math.min(...data) - 0.2 * (Math.max(...data) - Math.min(...data)),
                max: Math.max(...data) + 0.2 * (Math.max(...data) - Math.min(...data))
            },
            x: {
            	min: 0,
            	max: 7,
                grid: {
                    drawOnChartArea: false
                }
            },
        },
        plugins: {
        	legend: {
		        display: false
		    },
		    datalabels: {
	            display: true,
	            align: 'top', // Adjust the alignment as needed
	            formatter: function(value, context) {
	                return value; // You can customize the label content here
            }
        }
        }
    };

    // merge all options into the main chart options
    jQuery.extend(true, this.cfg.config.options, options);
    
    let extendedData = {
			datasets: [
       		{
       			fill: 'start',
        		borderColor: 'orange',
         		backgroundColor: 'lightyellow'
       		}
    	]
	}

 	// merge data into the main chart data
	jQuery.extend(true, this.cfg.config.data, extendedData);
}

function panChart(offset) {
	// Get the chart instance from the widgetVar
	var chart = PF('lineChartWidgetVar').chart;
	console.log(PF('lineChartWidgetVar'));
	var xAxis = chart.scales.x;
	var yAxis = chart.scales.y;
	
	var newMinX = xAxis.min + offset;
	var newMaxX = xAxis.max + offset;

	console.log(xAxis.min);
	console.log(xAxis.max);
	console.log(PF('lineChartWidgetVar').cfg.config.data.datasets[0].data.length);
	
	if (newMinX < 0 || newMaxX > PF('lineChartWidgetVar').cfg.config.data.datasets[0].data.length-1) {
	    console.log('no action');
	    return;
	}

	chart.options.scales.x.min = newMinX;
	chart.options.scales.x.max = newMaxX;
	
	// Preserve the current Y-axis range
	chart.options.scales.y.min = yAxis.min;
	chart.options.scales.y.max = yAxis.max;
	
	chart.update();
}