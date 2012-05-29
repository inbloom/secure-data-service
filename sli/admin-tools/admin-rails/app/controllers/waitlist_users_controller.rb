class WaitlistUsersController < ActionController::Base
  # GET /waitlist_users
  # GET /waitlist_users.json
  def index
    @waitlist_users = WaitlistUser.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @waitlist_users }
    end
  end

  # GET /waitlist_users/new
  # GET /waitlist_users/new.json
  def new
    @waitlist_user = WaitlistUser.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @waitlist_user }
    end
  end

  # POST /waitlist_users
  # POST /waitlist_users.json
  def create
    @waitlist_user = WaitlistUser.new(params[:waitlist_user])

    respond_to do |format|
      if @waitlist_user.save
        format.html { redirect_to :action => "success" }
        format.json { render json: @waitlist_user, status: :created, location: @waitlist_user }
      else
        format.html { redirect_to :action => "failed" }
        format.json { render json: @waitlist_user.errors, status: :unprocessable_entity }
      end
    end
  end

end
