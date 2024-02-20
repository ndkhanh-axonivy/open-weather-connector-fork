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
					if (event.event.x <= middlePointX) {
						break;
					} else if (
						index == meta.data.length - 2
					) {
						break;
					}
				}
				updateByTimeIndex([{name: 'selectedTimeIndex', value: index}]);
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
				min: Math.min(...data) - 0.1 * (Math.max(...data) - Math.min(...data)),
				max: Math.max(...data) + 0.3 * (Math.max(...data) - Math.min(...data))
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
				align: 'top',
				formatter: function(value, context) {
					return value;
				},
				offset: 5
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
	//Register plugin datalabels
	jQuery.extend(true, this.cfg.config, {plugins: [ChartDataLabels, ClickPositionDetector]});
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
			},
			datalabels: {
				display: true,
				align: 'end',
				formatter: function(value, context) {
					return value + '%';
				},
				offset: 15,
				clamp: false
			}
		}
	};

	// merge all options into the main chart options
	jQuery.extend(true, this.cfg.config.options, options);
	
	let extendedData = {
			datasets: [
			{
				minBarLength: '20',
				borderWidth: 2,
				backgroundColor: '#e8f0fe',
				borderColor: '#1a73e8',
				barThickness: 80
			}
		]
	}

 	// merge data into the main chart data
	jQuery.extend(true, this.cfg.config.data, extendedData);
}

function panChartByCurrentValue() {
	var currentChartWindowStartX = parseInt(document.getElementById('form:currentChartWindowStartX').value);
	var currentChartWindowEndX = parseInt(document.getElementById('form:currentChartWindowEndX').value);
	console.log(typeof(currentChartWindowEndX));
	
	panChart(currentChartWindowStartX, currentChartWindowEndX);
}

function panChart(newMinX, newMaxX) {
	console.log(typeof(newMinX) + ': ' + newMinX);
	console.log(typeof(newMaxX) + ': ' + newMaxX);
	var tempChart = PF('tempChartWidgetVar').chart;
	var precipitationChart = PF('popChartWidgetVar').chart;
	
	if (tempChart && tempChart.canvas) {
		tempChart.options.scales.x.min = newMinX;
		tempChart.options.scales.x.max = newMaxX;
	
		var yAxis = tempChart.scales.y;
		tempChart.options.scales.y.min = yAxis.min;
		tempChart.options.scales.y.max = yAxis.max;
		
		tempChart.update();
	}

	if (precipitationChart && precipitationChart.canvas) {
		precipitationChart.options.scales.x.min = newMinX;
		precipitationChart.options.scales.x.max = newMaxX;
		
		var yAxis = precipitationChart.scales.y;
		precipitationChart.options.scales.y.min = yAxis.min;
		precipitationChart.options.scales.y.max = yAxis.max;
		
		precipitationChart.update();
	}
}

function updateChartWithNewData() {
	var newTemperatureData = document.getElementById('form:tempModelData').value;
	newTemperatureData = JSON.parse(newTemperatureData);
	
	var tempChart = PF('tempChartWidgetVar').chart;
	var temperatureData = tempChart.data.datasets[0].data;

	data = temperatureData.map(function(value, index) {
		
		if (index < newTemperatureData.length) {
			return newTemperatureData[index];
		} else {
			return value;
		}
	});
	tempChart.data.datasets[0].data = data;
	tempChart.options.scales.y.min = Math.min(...data) - 0.1 * (Math.max(...data) - Math.min(...data));
	tempChart.options.scales.y.max = Math.max(...data) + 0.3 * (Math.max(...data) - Math.min(...data));
	tempChart.update();
}