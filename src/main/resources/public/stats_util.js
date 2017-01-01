window.onload = function () {
  var data = read_stats_table();
  console.log(data);

  var chart = new CanvasJS.Chart("stats_graph", {
      data: [{
        type: 'pie',
        dataPoints: data
      }]
  });
  chart.render();
}

function read_stats_table() {
  var data = [];

  var table = document.getElementById('stats_table');
  var rows = table.rows;
  var headers = rows.item(0).cells; // Header row
  var columns = rows.item(3).cells; // Total value row
  for (var j=1 ; j<columns.length-1 ; j++) {
    var header = headers.item(j).textContent;
    var value = Number(columns.item(j).textContent);
    data.push({'label': header, 'y': value});
  }

  return data;
}
