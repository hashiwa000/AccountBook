window.onload = function () {
  var data = read_stats_table();
  var data2 = read_stats_table2();
  console.log(data);
  console.log(data2);

  var chart = new CanvasJS.Chart("stats_graph", {
      data: [{
        type: 'pie',
        dataPoints: data
      }]
  });
  chart.render();

  var chart2 = new CanvasJS.Chart("stats_graph2", {
      data: [{
        type: 'column',
        dataPoints: data2
      }]
  });
  chart2.render();
}

function read_stats_table() {
  var data = [];

  var table = document.getElementById('stats_table');
  var rows = table.rows;
  var headers = rows.item(0).cells; // Header row
  var columns = rows.item(3).cells; // Total value row
  for (var j=1 ; j<columns.length-1 ; j++) {
    var header = headers.item(j).textContent;
    var valueStr = columns.item(j).textContent;
    var value = Number(valueStr.replace(/,/g, ''));
    data.push({'label': header, 'y': value});
  }
  return data;
}

function read_stats_table2() {
  var data = [];

  var table = document.getElementById('stats_table');
  var rows = table.rows;
  var headers = rows.item(0).cells; // Header row
  var columns = rows.item(3).cells; // Total value row
  var columns2 = rows.item(4).cells; // Planned value row

  for (var j=1 ; j<columns.length-1 ; j++) {
    var header = headers.item(j).textContent;
    var valueStr = columns.item(j).textContent;
    var valueStr2 = columns2.item(j).textContent;
    var value = Number(valueStr.replace(/,/g, ''));
    var value2 = Number(valueStr2.replace(/,/g, ''));
    data.push({'label': "Planned " + header, 'y': value2});
    data.push({'label': "Actual " + header, 'y': value});
  }

  return data;
}
