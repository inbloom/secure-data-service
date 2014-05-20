require "active_resource/base"
# This is the main controller of the Databrowser.
# We try to "Wrap" all api requests in this one single point
# and do some clever work with filters and routing to make this work.
# The basic flow goes like this:
# * The Api request is routed as parameters to this controller
# * The set_url field deals with that parameter as well as search parameters
# * The show action creates the new model with the url, searches, and pages.
# We make heavy use of params which is everything that comes into
# this controller after /entities/
class EntitiesController < ApplicationController

  ID_REGEX = /^\h{8}-\h{4}-\h{4}-\h{4}-\h{12}|_id$/

  before_filter :set_url

  # What we see mostly here is that we are looking for searh parameters.
  # Now, we also try to simply set up the search field and then remove it
  # from the parameters so that we don't confuse the API by passing it
  # through later.
  #
  # Here we tell the Entity model that it's url is the thing that was passed
  # through in params. Which is how we are able to wrap the entire
  # api through one place.
  def set_url
    @search_field = nil
    case params[:search_type]
    when /studentByName/
      @search_field = "name.lastSurname"
    when /staffByName/
      @search_field = "name.lastSurname"
    when /edOrgByName/
      @search_field = "nameOfInstitution"
    when /students/
      @search_field = "studentUniqueStateId"
    when /staff/
      @search_field = "staffUniqueStateId"
    when /teacherByName/
      @search_field = "name.lastSurname"
    when /teacher/
      @search_field = "staffUniqueStateId"
    when /parents/
      @search_field = "parentUniqueStateId"
    when /educationOrganizations/
      @search_field = "stateOrganizationId"
    end
      params[:other] = params[:search_type] if @search_field
    if params[:search_type] == "studentByName"
      Entity.url_type = "studentsgit status"
    elsif params[:search_type] == "staffByName"
      Entity.url_type = "staff"
    elsif params[:search_type] == "teacherByName"
      Entity.url_type = "teachers"
    elsif params[:search_type] == "edOrgByName"
      Entity.url_type = "educationOrganizations"
    else
      Entity.url_type = params[:other]
    end
      params.delete(:search_type)
      Entity.format = ActiveResource::Formats::JsonLinkFormat
  end

  # Ignoring some of the complicated parts, is we use the configured
  # model from set_url to make the Api call to get the data from the Api.
  #
  # Because we are trying to be generic with the data we get back, we handle
  # two special cases. The first is if params is 'home' which is a
  # special home page in the Api. So if we call that we, render the index
  # page instead of the normal 'show'.
  #
  # Second, if we only got one entity back, like the data for a single student
  # we go ahead and wrap that up into an array with that as the only element so
  # that our view logic can be simpler.
  #
  # As for the complicated parts, we do a few things, first is we detect if we
  # were passed any search parameters, and augment the Api call to deal with that
  # instead.
  #
  # Second, if we see any offset in params then we make the call to
  # grab the next page of data from the Api.
  def show

    # This section was put here for pagination. It sets the offset to 0 if not
    # set and then has a session and params variable for the limit. If the params
    # limit is set, it takes precedence. If there is no limit, the default is set
    # to the first item in the paginate_ipp array in views.yml
    params[:offset] = 0 unless params[:offset]

    if params[:limit].nil? and session[:limit].nil?
      params[:limit] = VIEW_CONFIG['paginate_ipp'].first.to_i
      session[:limit] = params[:limit]
    elsif !params[:limit].nil?
      session[:limit] = params[:limit]
    elsif params[:limit].nil? and !session[:limit].nil?
      params[:limit] = session[:limit]
    end

    @page = Page.new
    if params[:search_id] && @search_field
      @entities = []
      @entities = Entity.get("", @search_field => params[:search_id].strip) if params[:search_id] && !params[:search_id].strip.empty?
      @entities = clean_up_results(@entities)
      flash.now[:notice] = 'There were no entries matching your search' if @entities.blank?
    else
      query = params.reject {|k, v| k == 'controller' || k == 'action' || k == 'other' || k == 'search_id'}
      logger.debug {"Keeping query parameters #{query.inspect}"}
      @entities = Entity.get("", query)

      @headers = @entities.http_response
      @page = Page.new(@headers)
      @entities= clean_up_results(@entities)
    end

    set_crumbs

    if params[:other] == 'home'
      user_id, collection, links = get_user_entity_id_collection_and_links
      logger.debug{"user_id: #{user_id}"}
      logger.debug{"collection: #{collection}"}
      logger.debug{"links: #{links}"}
      get_user_ed_orgs(links, collection)
      @is_a_student if collection == 'students'
      render :index
      return
    end

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json:  {
         entities: @entities,
         headers: @headers
         }
      }
      format.js #show.js.erb
    end
  end

  private

  def set_crumbs
    if crumbable?
      if request.path != '/entities/home'
        if request.query_parameters.include?('search_id')
          add_breadcrumb('search', request.url)
        else
          parts = request.path.split('/').reject {|part| part.blank?}
          parts.each_with_index do |part,index|
            if part != 'entities'
              add_breadcrumb crumb_name(part, index == parts.length-1), "/#{parts[0..index].join('/')}"
            end
          end
        end
      end
    end
  end

  def crumb_name(path_part, terminus=false)
    if @entities.nil? || @entities.length != 1 || path_part !~ ID_REGEX || !terminus
      path_part
    else
      entity_display_name(@entities.first)
    end
  end

  def entity_display_name(entity_hash)
    case entity_hash['entityType']
      when 'educationOrganization'; entity_hash['nameOfInstitution']
      when 'staff','teacher','student'; "#{entity_hash['name']['firstName']} #{entity_hash['name']['lastSurname']}"
      else
        entity_hash['id']
    end
  end

  def crumbable?
    request.format != 'application/json' && !request.path.end_with?('/callback')
  end

  def clean_up_results(entities)
    tmp = entities
    if entities.is_a?(Hash)
      tmp = Array.new()
      tmp.push(entities)
    end
    tmp
  end

  def get_user_entity_id_collection_and_links
    body = JSON.parse(@entities.http_response.body)
    # get the link to self
    logger.debug{"Body{'links']: #{body['links']}"}
    logger.debug{"get_user_entity_id_collection_and_links"}
    self_url = get_url_from_hateoas_array(body['links'],'self')
    *garbage, user_id, collection = self_url.split('/')
    # check for students, as student self link is arranged in reverse order
    if user_id == 'students'
      temp = collection
      collection = user_id
      user_id = temp
    end
    return user_id, collection, body['links']
  end

  def get_url_from_hateoas_array(hateoas_array,target)
    logger.debug{"Target URL Key #{target}"}
    target_url=""
    #find the correct url
    logger.debug{"HATEOAS array: #{hateoas_array}"}
    hateoas_array.each do |e|
      if e['rel'] == target
        logger.debug {"found #{target}"}
	      target_url = e['href']
      end
    end
    logger.debug{"URL from hateoas array: #{target_url}"}
    #strip hostname/api/rest/v1
    target_url.partition("v1.5/").last
  end

  #This function is used to set the values for EdOrg table for Id, name, parent and type fields in the homepage
  def get_user_ed_orgs(links, collection)
    @edOrgArr = []
    userEdOrgs  = get_edorgs(links,collection)
    #Looping through EdOrgs and sending an API call to get the parent EdOrg attributes associated EdOrg Id
    userEdOrgs.each do |edOrg|
      @edOrgArr.push({"EdOrgs Id"=>edOrg["id"],"EdOrgs Name"=>edOrg["nameOfInstitution"], "EdOrgs Type"=>edOrg["entityType"],"EdOrgs Parent"=>get_parent_edorg_name(edOrg), "EdOrgs URL"=>get_url_from_hateoas_array(edOrg['links'],'self')})
    end
  end

  #This function is used to run an API call to fetch the values of attributes associated with Entity Id
  def get_edorgs(links,collection)
    #The URL to get edOrgs
    logger.debug{"get_edorgs"}
    if collection =='students'
      Entity.url_type = get_url_from_hateoas_array(links,'getSchools')
    else
      Entity.url_type = get_url_from_hateoas_array(links,'getEducationOrganizations')
    end
    user_edorgs = Entity.get("")
    user_edorgs = clean_up_results(user_edorgs)
  end

  def get_parent_edorg_name(edorg)
    edorg_name = 'N/A'
    if !edorg['parentEducationAgencyReference'].nil?
      parent_edorg_id = edorg['parentEducationAgencyReference'][0]
      logger.debug{"Parent ID: #{parent_edorg_id}"}
      Entity.url_type = "educationOrganizations/#{parent_edorg_id}"
      edorg_name = Entity.get("")['nameOfInstitution']
    end
    edorg_name
  end

  def strip_wrapping_array(item)
    tmp = item
    tmp = item[0] if item.is_a?(Array)
    tmp
  end
end
