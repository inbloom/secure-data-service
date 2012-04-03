
  class Snoop
    def initialize(app)
      @app = app
    end

    def call(env)
      
      status, headers, body = @app.call(env)

      case status
      when 200
        # Who needs weekly status reports?
       
      when 500
        # A bit of extra motivation to fix these errors
       
      end
puts "header: #{headers}"
puts env
      [status, headers, body]
    end
  end

