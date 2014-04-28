class User < SessionResource
  self.collection_name = "users"
  include ActiveModel::Validations

  validates_presence_of :email
  validates :fullName, presence: true, length: { maximum: 128 }
  validates :tenant, length: { maximum: 160 }, allow_blank: true, format: { with: /^[-_a-zA-Z0-9]+[-_.@a-zA-Z0-9]*$/, message: 'should not contains spaces or special characters' }
  validates :edorg, length: { maximum: 255 }, allow_blank: true

  schema do
    string  'uid'
    string  'email'
    string  'tenant'
    string  'edorg'
    time 'createTime', 'modifyTime'
    string  'groups'
    string  'fullName'
    string  'sn'
    string  'givenName'
    string  'primary_role'
    string  'optional_role_1'
    string  'optional_role_2'
    string   'homeDir'
  end

  def get_groups
    self.groups.sort!.join(", ")
  end

  def get_create_time
    Time.parse(self.createTime + "UTC")
  end

end
