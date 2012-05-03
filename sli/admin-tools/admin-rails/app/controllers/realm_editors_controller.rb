class RealmEditorsController < ApplicationController
  # GET /realm_editors
  # GET /realm_editors.json
  def index
    @realms = Realm.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @realms }
    end
  end

  ## GET /realm_editors/1
  ## GET /realm_editors/1.json
  #def show
  #  @realm = Realm.find(params[:id])
  #
  #  respond_to do |format|
  #    format.html # show.html.erb
  #    format.json { render json: @realm }
  #  end
  #end

  # GET /realm_editors/new
  # GET /realm_editors/new.json
  def new
    @realm = Realm.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @realm }
    end
  end

  # GET /realm_editors/1/edit
  def edit
    @realm = Realm.find(params[:id])
  end

  # POST /realm_editors
  # POST /realm_editors.json
  #def create
  #  @realm = Realm.new(params[:realm_editor])
  #
  #  respond_to do |format|
  #    if @realm.save
  #      format.html { redirect_to @realm, notice: 'Realm editor was successfully created.' }
  #      format.json { render json: @realm, status: :created, location: @realm }
  #    else
  #      format.html { render action: "new" }
  #      format.json { render json: @realm.errors, status: :unprocessable_entity }
  #    end
  #  end
  #end

  # PUT /realm_editors/1
  # PUT /realm_editors/1.json
  #def update
  #  @realm = Realm.find(params[:id])
  #
  #  respond_to do |format|
  #    if @realm.update_attributes(params[:realm_editor])
  #      format.html { redirect_to @realm, notice: 'Realm editor was successfully updated.' }
  #      format.json { head :ok }
  #    else
  #      format.html { render action: "edit" }
  #      format.json { render json: @realm.errors, status: :unprocessable_entity }
  #    end
  #  end
  #end
  #
  ## DELETE /realm_editors/1
  ## DELETE /realm_editors/1.json
  #def destroy
  #  @realm = Realm.find(params[:id])
  #  @realm.destroy
  #
  #  respond_to do |format|
  #    format.html { redirect_to realm_editors_url }
  #    format.json { head :ok }
  #  end
  #end
end
