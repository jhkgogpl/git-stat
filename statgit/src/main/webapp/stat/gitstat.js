$(function() {
    init();
});

function init() {
    commonInit();
    eventInit();
    viewInit();
}

function commonInit() {
    if (!!localStorage.gitStat_gitPath && localStorage.gitStat_gitPath != '') {
        $('#gitRoot').val(localStorage.gitStat_gitPath);
    }
    window.hidTable = false;
    window.hidChart = false;
}

function eventInit() {
    $('#btnStat').on('click', function() {
        var method = $('#method').val();
        var startDate = $('#startDate').val();
        var endDate = $('#endDate').val();
        var name = $('#name').val();
        var gitRoot = $('#gitRoot').val();
        //console.log(method, startDate, endDate, name, gitRoot);

        if (method == '0' && (startDate == '' || endDate == '')) {
            alert('Start Date and End Date must not null.');
            return;
        }

        showMask(true);
        $.ajax({
            type: 'get',
            url: '../git/stat',
            data: {
                method: method,
                startDate: startDate,
                endDate: endDate,
                name: name,
                gitRoot: gitRoot
            },
            success: function(data) {
                //console.log(JSON.parse(data));
                window.statData = JSON.parse(data).data;
                tableInit();
                chartInit();
                showMask(false);
            }
        });
    });

    $('#method').on('change', function() {
        var method = $('#method').val();
        if (method <= 0) {
            $('#startDate').removeAttr('disabled');
            $('#endDate').removeAttr('disabled');
        } else {

            $('#startDate').attr('disabled', 'true');
            $('#endDate').attr('disabled', 'true');
        }
    });

    $('#isTable').on('click', function() {
        showTable(true);
    });

    $('#isChart').on('click', function() {
        showChart();
    });

    $('#detailMask').on('click', function() {
        $('#dailyPanel').css('display', 'none');
    });

    $('#gitRoot').on('change', function() {
        localStorage.gitStat_gitPath = $(this).val();
    });
}

function showMask(status) {
    if (status) {
        $('#mask').css('display', 'block');
    } else {
        $('#mask').css('display', 'none');
    }
}

function showTable(show) {
    window.hidTable = !show;
    window.hidChart = show;
    $('#tabStat').css('display', show ? 'block' : 'none');
    $('#chartStat').css('display', show ? 'none' : 'block');
    $('#isTable').css('background-color', show ? '#ccc' : '#fff');
    $('#isChart').css('background-color', show ? '#fff' : '#ccc');
}

function showChart() {
    showTable(false);
    chartInit();
}

function viewInit() {
}

function tableInit() {
    var table = $('#tableStat tbody');
    var rows = window.statData;
    table.empty();
    var num = 0;
    for (var ind in rows) {
        var row = rows[ind];
        num += row.add;
    }
    var i = 0;
    for (var ind in rows) {
        var row = rows[ind];
        table.append('<tr id="' + i + '"><td>' + (++i) + '</td><td>    ' + row.name + '</td><td>'
                        + Math.round(row.add/num * 10000)/100 + '</td><td>'
                        + row.add + '</td><td>' + row.del + '</td><td>'
                        + row.file + '</td><td>' + row.first + '</td><td>' + row.last + '</td></tr>');
    }

    $('tr').on('click', function() {
        $('#dailyPanel').css('display', 'block');
        var id = $(this).attr('id');
        detailLineChart(id);
    });
}

/**
 * pie chart
 */
function chartInit() {
    if (window.hidChart) return;
    var rows = window.statData;

    var legData = [];
    var serData = [];
    for (var ind in rows) {
        var row = rows[ind];
        var name = row.name;
        var add = row.add;
        legData.push(name);
        serData.push({
            name: name,
            value: add
        });
    }

    // 路径配置
    require.config({
        paths:{
            'echarts' : '../lib/echarts/echarts',
            'echarts/chart/pie' : '../lib/echarts/echarts'
        }
    });

    // 使用
    require(
        [
            'echarts',
            'echarts/chart/pie' // 使用柱状图就加载bar模块，按需加载
        ],
        function (ec) {
            // 基于准备好的dom，初始化echarts图表
            var myChart = ec.init(document.getElementById('chartStat'));

            var option = {
                title : {
                    text: 'Git 代码量统计',
                    subtext: 'Git Log',
                    x:'center'
                },
                tooltip : {
                    trigger: 'item',
                    formatter: "{a} <br/>{b} : {c} ({d}%)"
                },
                legend: {
                    orient : 'vertical',
                    x : 'left',
                    data:legData
                },
                toolbox: {
                    show : true,
                    feature : {
                        mark : {show: true},
                        dataView : {show: true, readOnly: false},
                        restore : {show: true},
                        saveAsImage : {show: true}
                    }
                },
                calculable : true,
                series : [
                    {
                        name:'提交人',
                        type:'pie',
                        radius : '55%',
                        center: ['50%', '60%'],
                        data:serData
                    }
                ]
            };

            // 为echarts对象加载数据
            myChart.setOption(option);
        }
    );
}

/**
 * line chart
 *
 * @param id
 */
function detailLineChart(id) {

    var rows = window.statData;
    var row = rows[id];
    var adds = row.adds;
    var dels = row.dels;
    var xdata = [];
    for (var i = 0; i < adds.length; i++) {
        xdata.push(i+1);
    }

    var method = $('#method').val();
    var name = row.name;
    var subTitle;
    switch(method) {
        case '1':
            subTitle = '1天内提交量';
            break;
        case '2':
            subTitle = '1周内提交明细';
            break;
        case '3':
            subTitle = '1月内提交明细';
            break;
        case '0':
            var startDate = $('#startDate').val();
            var endDate = $('#endDate').val();
            subTitle = startDate + ' ~ ' + endDate + ' 时间内提交明细';
            break;
        default :
            break;
    }
    subTitle = name + ' ' + subTitle;

    require(
        [
            'echarts',
            'echarts/chart/line'
        ],
        function (ec) {
            var myChart = ec.init(document.getElementById('detailStat'));

            var option = {
                title : {
                    text: '个人提交明细',
                    subtext: subTitle,
                    x:'left'
                },
                tooltip : {
                    trigger: 'axis'
                },
                legend: {
                    data:['增加行数','删除行数']
                },
                toolbox: {
                    show : true,
                    feature : {
                        mark : {show: true},
                        dataView : {show: true, readOnly: false},
                        magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
                        restore : {show: true},
                        saveAsImage : {show: true}
                    }
                },
                calculable : true,
                xAxis : [
                    {
                        type : 'category',
                        boundaryGap : false,
                        data : xdata
                    }
                ],
                yAxis : [
                    {
                        type : 'value'
                    }
                ],
                series : [
                    {
                        name:'增加行数',
                        type:'line',
                        stack: '总量',
                        data:adds
                    },
                    {
                        name:'删除行数',
                        type:'line',
                        stack: '总量',
                        data:dels
                    }
                ]
            };

            myChart.setOption(option);
        }
    );
}