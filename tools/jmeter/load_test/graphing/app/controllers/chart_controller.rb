class ChartController < ApplicationController
  def index
    @test = LazyHighCharts::HighChart.new('graph') do |f|
      f.chart(:renderTo => 'container', :type => 'line', :marginRight => 130, :marginBottom => 50)
      f.title(:text => 'JMeter API Stress Test (Get student by ID)', :x => -20)
      f.subtitle(:text => 'single node', :x => -20)
      f.xAxis(:categories => [5, 10, 15, 20, 25, 30, 35, 40, 45, 50], :title => { :text => "Number of Threads" })
      f.yAxis(:title => { :text => 'Response Time in millisecond' },
              :plotLines => [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }])
      f.legend(:layout => 'vertical',
                :align => 'right',
                :verticalAlign => 'top',
                :x => -10,
                :y => 100,
                :borderWidth => 0
            )
      f.series(:name => 'min', :data => [58, 95, 72, 54, 174, 88, 252, 197, 321, 311])
      f.series(:name => 'med', :data => [97, 189, 288, 392, 518, 679, 853, 976, 976, 1115])
      f.series(:name => 'max', :data => [173, 324, 538, 695, 917, 1137, 1565, 2151, 1542, 1612])
    end
  end
end
