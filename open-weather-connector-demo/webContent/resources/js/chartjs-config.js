const ClickPositionDetector = {
  id: "clickPositionDetector",
  afterEvent: function (chart, event, options) {
    if (event.event.type === "click") {
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
          } else if (index == meta.data.length - 2) {
            break;
          }
        }
        updateByTimeIndex([{ name: "selectedTimeIndex", value: index }]);
      });
    }
  },
};

function temperatureChartExtender() {
  //Register plugin datalabels
  jQuery.extend(true, this.cfg.config, {
    plugins: [ChartDataLabels, ClickPositionDetector],
  });
  let data = [...this.cfg.config.data.datasets[0].data];

  let options = jQuery.extend(true, {}, this.cfg.config.options);
  options = {
    scales: {
      y: {
        display: false, // Hide y-axis
        min: Math.min(...data) - 0.1 * (Math.max(...data) - Math.min(...data)),
        max: Math.max(...data) + 0.3 * (Math.max(...data) - Math.min(...data)),
      },
      x: {
        min: 0,
        max: 7,
        grid: {
          drawOnChartArea: false,
        },
        ticks: {
          color: fontColor,
        },
      },
    },
    plugins: {
      legend: {
        display: false,
      },
      datalabels: {
        display: true,
        align: "top",
        formatter: function (value, context) {
          return value;
        },
        offset: 5,
        color: fontColor,
      },
    },
  };

  // merge all options into the main chart options
  jQuery.extend(true, this.cfg.config.options, options);

  let extendedData = {
    datasets: [
      {
        fill: "start",
        borderColor: "orange",
        backgroundColor: "lightyellow",
      },
    ],
  };

  // merge data into the main chart data
  jQuery.extend(true, this.cfg.config.data, extendedData);
}

function precipitationChartExtender() {
  //Register plugin datalabels
  jQuery.extend(true, this.cfg.config, {
    plugins: [ChartDataLabels, ClickPositionDetector],
  });
  let data = [...this.cfg.config.data.datasets[0].data];

  let options = jQuery.extend(true, {}, this.cfg.config.options);
  options = {
    scales: {
      y: {
        display: false, // Hide y-axis
        min: 0,
        max: 120,
      },
      x: {
        min: 0,
        max: 7,
        grid: {
          drawOnChartArea: false,
        },
        ticks: {
          color: fontColor,
        },
      },
    },
    plugins: {
      legend: {
        display: false,
      },
      datalabels: {
        display: "true",
        anchor: "end",
        align: "top",
        formatter: function (value, context) {
          return value + "%";
        },
        offset: 2,
        color: fontColor,
      },
    },
  };

  // merge all options into the main chart options
  jQuery.extend(true, this.cfg.config.options, options);

  let extendedData = {
    datasets: [
      {
        borderWidth: 2,
        backgroundColor: tempBackGroundColor,
        borderColor: tempBorderColor,
        barThickness: 80,
      },
    ],
  };

  // merge data into the main chart data
  jQuery.extend(true, this.cfg.config.data, extendedData);
}

function windChartExtender() {
  let windData = JSON.parse(
    this.cfg.config.data.datasets[0].cubicInterpolationMode
  );
  const customLabelPlugin = {
    id: "customLabel",
    afterDraw: (chart) => {
      const data = JSON.parse(
        this.cfg.config.data.datasets[0].cubicInterpolationMode
      );
      const min = this.cfg.config.options.scales.x.min;
      const max = this.cfg.config.options.scales.x.max;
      let position = 0;
      for (let i = min; i <= max; i++) {
        chart.ctx.fillStyle = fontColor;
        chart.ctx.fillText(`${data[i].speed}`, position, 50);
        position += 95;
      }
    },
  };
  //Register plugin datalabels
  jQuery.extend(true, this.cfg.config, {
    plugins: [ChartDataLabels, ClickPositionDetector, customLabelPlugin],
  });

  let options = jQuery.extend(true, {}, this.cfg.config.options);
  options = {
    scales: {
      yAxes: [
        {
          gridLines: {
            drawBorder: false,
          },
        },
      ],

      y: {
        grid: {
          display: false,
        },
        display: false, // Hide y-axis
        min: 0,
        max: 120,
        ticks: {
          callback: function (value, index, values) {
            return value;
          },
        },
      },
      x: {
        min: 0,
        max: 7,
        grid: {
          drawOnChartArea: false,
        },
        ticks: {
          color: fontColor,
        },
      },
    },
    plugins: {
      legend: {
        display: false,
      },
      datalabels: {
        display: "true",
        anchor: "end",
        align: "top",
        color: fontColor,
        rotation: (context) => {
          return windData[context.dataIndex].deg;
        },
        formatter: function (value, context) {
          return "⬇";
        },
        offset: 2,
        font: (context) => {
          const data = windData[context.dataIndex].speed.split(" ");
          const speed = data[0];
          const unit = data[1];
          let level = 0;
          if (unit.trim() === "m/s") {
            level = 5;
          } else {
            level = 10;
          }

          return {
            weight: "bold",
            size: speed > level ? 50 : 30,
          };
        },
      },
    },
  };

  // merge all options into the main chart options
  jQuery.extend(true, this.cfg.config.options, options);
  let extendedData = {
    datasets: [
      {
        showLine: false,
        borderWidth: 2,
        backgroundColor: "transparent",
        borderColor: "transparent",
        barThickness: 80,
      },
    ],
  };

  // merge data into the main chart data
  jQuery.extend(true, this.cfg.config.data, extendedData);
}

function panChartByCurrentValue() {
  var currentChartWindowStartX = parseInt(
    document.getElementById("form:currentChartWindowStartX").value
  );
  var currentChartWindowEndX = parseInt(
    document.getElementById("form:currentChartWindowEndX").value
  );

  panChart(currentChartWindowStartX, currentChartWindowEndX);
}

function panChart(newMinX, newMaxX) {
  var tempChart = PF("tempChartWidgetVar").chart;
  var precipitationChart = PF("popChartWidgetVar").chart;
  var windChart = PF("windChartWidgetVar").chart;

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

  if (windChart && windChart.canvas) {
    windChart.options.scales.x.min = newMinX;
    windChart.options.scales.x.max = newMaxX;

    var yAxis = windChart.scales.y;
    windChart.options.scales.y.min = yAxis.min;
    windChart.options.scales.y.max = yAxis.max;

    windChart.update();
  }
}

function updateChartWithNewData() {
  var newTemperatureData = document.getElementById("form:tempModelData").value;
  newTemperatureData = JSON.parse(newTemperatureData);

  var tempChart = PF("tempChartWidgetVar").chart;
  var temperatureData = tempChart.data.datasets[0].data;
  var windChart = PF("windChartWidgetVar").chart;
  if (windChart) {
    updateWindChartData(windChart);
  }

  data = temperatureData.map(function (value, index) {
    if (index < newTemperatureData.length) {
      return newTemperatureData[index];
    } else {
      return value;
    }
  });
  tempChart.data.datasets[0].data = data;
  tempChart.options.scales.y.min =
    Math.min(...data) - 0.1 * (Math.max(...data) - Math.min(...data));
  tempChart.options.scales.y.max =
    Math.max(...data) + 0.3 * (Math.max(...data) - Math.min(...data));
  tempChart.update();
}

function updateWindChartData(windChart) {
  let customData = JSON.parse(
    windChart.data.datasets[0].cubicInterpolationMode
  );
  customData.map((value) => {
    const data = value.speed.split(" ");
    if (data[1].trim() === "m/s") {
      value.speed = `${(data[0] * 2.237).toFixed(2)} mph`;
    } else {
      value.speed = `${(data[0] / 2.237).toFixed(2)} m/s`;
    }
  });
  windChart.data.datasets[0].cubicInterpolationMode =
    JSON.stringify(customData);
  windChart.update();
}
