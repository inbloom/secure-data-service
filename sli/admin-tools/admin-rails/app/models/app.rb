class App < SessionResource
  self.format = ActiveResource::Formats::JsonFormat
  validates_presence_of [:description, :name, :vendor], message: 'must not be blank'
  validates_format_of :version, with: /^[A-Za-z0-9\.]{1,25}$/, message: 'must contain only alphanumeric characters and periods and be less than 25 characters long'
  validates_format_of [:application_url, :redirect_uri, :administration_url, :image_url], with: URI.regexp(['http', 'https']), allow_nil: true, message: 'must be a valid url (starting with http:// or https://)'
  validates_presence_of [:application_url, :redirect_uri], message: 'must not be blank', if: '!installed?'

  def pending?
    registration.status == 'PENDING'
  end

  def in_progress?
    allowed_for_all_edorgs ? false : authorized_ed_orgs.blank?
  end

  schema do
    string 'client_secret', 'redirect_uri', 'description', 'image_url'
    string 'name', 'client_id', 'application_url', 'administration_url'
    string 'version', 'behavior'
    boolean 'is_admin', 'license_acceptance', 'installed', 'allowed_for_all_edorgs', 'isBulkExtract'
    time 'created', 'updated'
    string 'authorized_ed_orgs', 'vendor'
    string 'author_first_name', 'author_last_name'
  end

  class Registration < SessionResource
    schema do
      string 'status'
    end
  end
end


