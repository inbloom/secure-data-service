# Adds thread local variables that we can use for the log messages
class LoggingMiddleware
   def initialize(app)
    @app = app
  end
  
 
   
    def call(env)
   # Thread.current[:remote] =  env["REMOTE_ADDR"]
    Thread.current[:env] = env
    Thread.current[:awesome] = 'Sean'
      Thread.current[:remote] =  env["REMOTE_ADDR"]
    puts "remote"
  puts  Thread.current[:remote] 
  puts "end remote"
  puts   env["ENV_SESSION_KEY"]
    puts "Session option #{env['ENV_SESSION_KEY']}#"
    sessionId = env[:_session_id];
    puts "Session id: #{sessionId}"
    puts "the prowler lives #{env}\n"

   
    @app.call(env)
    
  end

  
  
  def threads( env )

    Thread.current[:session_id] = session_id
    Thread.current[:remote_ip] =  env["action_dispatch.remote_ip"]
    Thread.current[:env] = env
    puts "the prowler lives #{env}\n"
  end

end
