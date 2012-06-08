module AppsHelper
  def sortable(title, value = nil)
    value ||= title.downcase
    direction = params[:direction] == "ascending" ? "descending" : "ascending"
    link_to title, {:sort => value, :direction => direction}, {:class => direction}
  end

  # For client_id and client_secret, return 'Pending' if app isn't yet registered
  def client_field_value(app, field)
    if app.attributes.has_key? 'registration' and app.registration.status == 'APPROVED'
      app.attributes[field]
    else
      return check["tenantId"]
    end
  end

end
