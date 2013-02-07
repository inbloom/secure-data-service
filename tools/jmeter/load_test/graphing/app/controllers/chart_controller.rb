class ChartController < ApplicationController
  def index
    #@test = LazyHighCharts::HighChart.new('graph') do |f|
    #  f.chart(:renderTo => 'container', :type => 'line', :marginRight => 130, :marginBottom => 50)
    #  f.title(:text => 'JMeter API Stress Test (Get student by ID)', :x => -20)
    #  f.subtitle(:text => 'single node', :x => -20)
    #  f.xAxis(:categories => [5, 10, 15, 20, 25, 30, 35, 40, 45, 50], :title => { :text => "Number of Threads" })
    #  f.yAxis(:title => { :text => 'Response Time in millisecond' },
    #          :plotLines => [{
    #                value: 0,
    #                width: 1,
    #                color: '#808080'
    #            }])
    #  f.legend(:layout => 'vertical',
    #            :align => 'right',
    #            :verticalAlign => 'top',
    #            :x => -10,
    #            :y => 100,
    #            :borderWidth => 0
    #        )
    #  f.series(:name => 'min', :data => [58, 95, 72, 54, 174, 88, 252, 197, 321, 311])
    #  f.series(:name => 'med', :data => [97, 189, 288, 392, 518, 679, 853, 976, 976, 1115])
    #  f.series(:name => 'max', :data => [173, 324, 538, 695, 917, 1137, 1565, 2151, 1542, 1612])
    #end

    file = File.read("#{Rails.root}/../load_test_result/single_node/result.json")
    json = JSON.parse(file)
    @charts = []
    @table = {}
    json.each do |scenario, result|
      p result
      categories = result.keys.sort! {|x,y| x.to_i <=> y.to_i}
      aggregate = {}
      @table[scenario] = {:header => [], :data => []}
      categories.each do |category|
        row = [category]
        result[category].each do |request, average|
          aggregate[request] = [] if aggregate[request].nil?
          aggregate[request] << average
          @table[scenario][:header] << request unless @table[scenario][:header].include? request
          row << average
        end
        @table[scenario][:data] << row
      end
      @charts << LazyHighCharts::HighChart.new('graph') do |f|
        f.chart(:renderTo => 'container', :type => 'line', :marginRight => 130, :marginBottom => 50)
        f.title(:text => scenario, :x => -20, :style => {"fontSize" =>"20px" })
        f.subtitle(:text => 'subtitle', :x => -20)
        f.xAxis(:categories => categories, :title => { :text => "Number of Threads" ,:style => {"fontSize" => "16px"}}, :labels => {:style => {"fontSize" => "14px"}, :y => 20},:min => 1)
        f.yAxis(:title => { :text => 'Response Time in millisecond', :style => {"fontSize" => "16px"} },
                :plotLines => [{
                                   value: 0,
                                   width: 1,
                                   color: '#808080'
                               }],
	       :min => 0, :labels => {:style => {"fontSize" => "14px","margin-top" => "8px"}})
        f.legend(:layout => 'vertical',
                 :align => 'right',
                 :verticalAlign => 'top',
                 :x => -5,
                 :y => 80,
                 :borderWidth => 0,
	         :itemStyle => {"fontSize" => "14px"}
        )
        aggregate.each do |request, averages|
          f.series(:name => request, :data => averages)
        end
      end
    end

    @transposed_tables = {}
    @table.each do |scenario, row|
      @transposed_tables[scenario] = [["Threads"] + row[:header]]
      row[:data].each do |data|
        @transposed_tables[scenario] << data
      end
      size = @transposed_tables[scenario].max { |r1, r2| r1.size <=> r2.size }.size
      @transposed_tables[scenario].each { |r| r[size - 1] ||= nil }
      @transposed_tables[scenario] = @transposed_tables[scenario].transpose
    end
  end
end
