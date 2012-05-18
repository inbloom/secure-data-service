module AppsHelper
  def sortable(title, value = nil)
    value ||= title.downcase
    direction = params[:direction] == "ascending" ? "descending" : "ascending"
    link_to title, {:sort => value, :direction => direction}, {:class => direction}
  end

  def get_district_hierarchy
    state_ed_orgs = EducationOrganization.all

    result = {}

    state_ed_orgs.each do |ed_org|
      next if ed_org.organizationCategories == nil or ed_org.organizationCategories.index("State Education Agency") == nil
      current_parent = {"id" => ed_org.id, "name" => ed_org.nameOfInstitution, "stateOrganizationId" => ed_org.stateOrganizationId}
      child_ed_orgs = EducationOrganization.find(:all, :params => {"parentEducationAgencyReference" => ed_org.id})
      child_ed_orgs.each do |child_ed_org|
        current_child = {"id" => child_ed_org.id, "name" => child_ed_org.nameOfInstitution, "stateOrganizationId" => child_ed_org.stateOrganizationId}
        if result.keys.include?(current_parent)
          result[current_parent].push(current_child)
        else
          result[current_parent] = [current_child]
        end
      end
    end
    result
  end

  # For client_id and client_secret, return 'Pending' if app isn't yet registered
  def client_field_value(app, field)
    if app.attributes.has_key? 'registration' and app.registration.status == 'APPROVED'
      app.attributes[field]
    else
      "Pending"
    end
  end
end
