const ClickPositionDetector = {
	id: 'clickPositionDetector',
	afterEvent: function (chart, event, options) {
		if (event.event.type === 'click') {
			var datasets = chart.config.data.datasets;
			datasets.forEach(function (dataset, datasetIndex) {
				var meta = chart.getDatasetMeta(datasetIndex);
				if (meta.hidden) {
					return;
				}
				var index = 0;
				for (index = 0; index < meta.data.length - 1; index++) {

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
				rc([{name: 'selectedTimeIndex', value: index}]);
			});
		}
	}
};

function temperatureChartExtender() {
	//Register plugin datalabels
	jQuery.extend(true, this.cfg.config, {plugins: [ChartDataLabels, ClickPositionDetector]});
	let data = [...this.cfg.config.data.datasets[0].data];

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
					return value;
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

function precipitationChartExtender() {
	let data = [...this.cfg.config.data.datasets[0].data];

	let options = jQuery.extend(true, {}, this.cfg.config.options);
	options = {
		scales: {
			y: {
				display: false, // Hide y-axis
				min: 0,
				max: 100
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
			}
		}
	};

	// merge all options into the main chart options
	jQuery.extend(true, this.cfg.config.options, options);
}

function panChart(stepX, windowSize) {
	console.log(stepX);
	// Get the chart instance from the widgetVar
	var tempChart = PF('lineChartWidgetVar').chart;
	var precipitationChart = PF('barChartWidgetVar').chart;
	
	var xAxis = precipitationChart.scales.x;
	
	var newMinX = xAxis.min + stepX;
	console.log(xAxis.min);
	
	if (newMinX < 0) {
		newMinX = 0;
	}
	newMaxX = newMinX + windowSize - 1;
	
	// TempChart
	precipitationChart.options.scales.x.min = newMinX;
	precipitationChart.options.scales.x.max = newMaxX;
	
	var yAxis = precipitationChart.scales.y;
	precipitationChart.options.scales.y.min = yAxis.min;
	precipitationChart.options.scales.y.max = yAxis.max;
	
	precipitationChart.update();
	
	if (tempChart) {
		// TempChart
		tempChart.options.scales.x.min = newMinX;
		tempChart.options.scales.x.max = newMaxX;
	
		var yAxis = tempChart.scales.y;
		tempChart.options.scales.y.min = yAxis.min;
		tempChart.options.scales.y.max = yAxis.max;
	
		
		tempChart.update();
	}
}

function destroyChart() {
	if (window.barChartWidgetVar) {
		window.barChartWidgetVar.destroy();
	}
	if (window.lineChartWidgetVar) {
		window.lineChartWidgetVar.destroy();
	}
}