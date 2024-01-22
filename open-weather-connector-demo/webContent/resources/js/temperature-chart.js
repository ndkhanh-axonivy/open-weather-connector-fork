function chartExtender() {
	let data = [...this.cfg.config.data.datasets[0].data];
	console.log(data);
    // copy the config options into a variable
    let options = jQuery.extend(true, {}, this.cfg.config.options);
    options = {
    	scales: {
            y: {
                display: false, // Hide y-axis
                beginAtZero: true,
                min: Math.min(...data) - 0.1 * (Math.max(...data) - Math.min(...data)),
                max: Math.max(...data) + 0.1 * (Math.max(...data) - Math.min(...data))
            },
            x: {
            	display: false,
                min: 0, // Set minimum x-axis value
                max: 2, // Set maximum x-axis value
                grid: {
                    drawOnChartArea: false
                }
            },
        },
        plugins: {
        	legend: {
		        display: false
		    },
            zoom: {
                pan: {
                    enabled: true,
                    threshold: 1,
                    mode: 'x'
                }
            }
        }
    };

    // merge all options into the main chart options
    jQuery.extend(true, this.cfg.config.options, options);
    
    let extendedData = {
       datasets: [
       {
         borderColor: 'black',
         backgroundColor: 'orange',
       }
    ]
  }
  
   // merge data into the main chart data
  jQuery.extend(true, this.cfg.config.data, extendedData);
}

function panChart(offset) {
    var chart = PF('lineChartWidgetVar').chart;
 	console.log(chart);
    // Log the original options
    console.log(chart.config);

    // Modify the copied options
    var xAxis = chart.cfg.options.scales.x;
    var currentMinX = xAxis.min;
    var currentMaxX = xAxis.max;
    var newMinX = currentMinX + offset;
    var newMaxX = currentMaxX + offset;

    // Check if the new values are within valid range
    if (newMinX < 0 || newMaxX > 7) {
        console.log('No action');
        return;
    }

    chart.cfg.options.scales.x.min = newMinX;
    chart.cfg.options.scales.x.max = newMaxX;

    var yAxis = chart.cfg.options.scales.y;

    chart.cfg.options.scales.y.min = yAxis.min;
    chart.cfg.options.scales.y.max = yAxis.max;

    // Log the modified options using JSON.stringify for a cleaner representation
    console.log('Modified Options:', JSON.stringify(chart.cfg, null, 2));
    chart.pan({
		x: offset,
		y: 0
	});
}