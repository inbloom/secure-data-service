class Page
  
  attr_accessor :next, :prev
  
  def initialize(headers = {})
    @prev = nil
    @next = nil
    if(!headers.nil? && headers['Link'])
      @headers = headers['Link'];
      values = @headers.split(';')
      logger.debug {values.inspect}
      values[0].scan(/offset=(\d+)/) do |d|
        @next = d.first if values[1].include? "next"
        @prev = d.first if values[1].include? "prev"
      end
    end
  end
  def next?
    !@next.nil?
  end
  
  def prev?
    !@prev.nil?
  end
  
  
end