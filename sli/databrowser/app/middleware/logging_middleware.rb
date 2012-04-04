# Adds thread local variables that we can use for the log messages
class LoggingMiddleware
  def initialize(app)
    @app = app
  end

  def call(env)
    Thread.current[:remote] =  env["REMOTE_ADDR"]

    # Conceptually, we should be able to fetch more variables from env and stash them in the Thread local space for later access
    @app.call(env)

  end

end
