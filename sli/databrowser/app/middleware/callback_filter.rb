class CallbackFilter
  def initialize(app)
    @app = app
  end

  def call(env)
    puts "callback"
  
  #  Thread.current[:awesome] =    env['REMOTE_ADDR']
    #Thread.current[:remote] = env["REMOTE_ADDR"]
    #request = ActionDispatch::Request.new(env)

    #puts env["rack.session"]
    puts "cb done"
#puts   env['REMOTE_ADDR']
   
    @app.call(env)
  end 
end
