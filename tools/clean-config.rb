
SLI_ROOT = "../sli"


def run
  cleanCanonicalConfig
  cleanDatabrowserConfig
  cleanAdminConfig
  cleanDashboardConfig
  cleanPortalConfig
  cleanPortalExtConfig

  removeFiles
end

def cleanCanonicalConfig()

  filename = "#{SLI_ROOT}/config/config.in/canonical_config.yml"
  contents = File.read(filename)

  contents = removeProfile(contents,"devapp1")
  contents = removeProfile(contents,"devjuggernauts")
  contents = removeProfile(contents,"local-pit")
  contents = removeProfile(contents,"local-maestro")
  contents = removeProfile(contents,"nxteam-pit")
  contents = removeProfile(contents,"nxteam-maestro")
  contents = removeProfile(contents,"nxteam")
  # not really profiles, but oh well
  contents = removeProfile(contents,"# Properties for systems being")
  contents = removeProfile(contents,"# These are Amazon west instances")

  contents = sanitizeConfigProperties(contents)

  File.open(filename, 'w') {|f| f.write(contents) }
end

def cleanDatabrowserConfig
  filename = "#{SLI_ROOT}/databrowser/config/config.yml"
  contents = File.read(filename)
  contents = sanitizeConfigProperties(contents)
  File.open(filename, 'w') {|f| f.write(contents) }
end

def cleanAdminConfig
  filename = "#{SLI_ROOT}/admin-tools/admin-rails/config/config.yml"
  contents = File.read(filename)
  contents = removeProfile(contents,"test") # refers to devapp1
  contents = sanitizeConfigProperties(contents)
  File.open(filename, 'w') {|f| f.write(contents) }
end


def cleanDashboardConfig
  filename = "#{SLI_ROOT}/config/config.in/dashboard_config.yml"
  contents = File.read(filename)
  contents = sanitizeConfigProperties(contents)
  File.open(filename, 'w') {|f| f.write(contents) }
end

def cleanPortalConfig
  filename = "#{SLI_ROOT}/config/config.in/portal_config.yml"
  contents = File.read(filename)
  contents = removeProfile(contents,"devlycans")
  contents = removeProfile(contents,"devlr1")
  contents = removeProfile(contents,"devlr2")
  contents = removeProfile(contents,"testlr1")
  contents = removeProfile(contents,"demo")
  contents = sanitizeConfigProperties(contents)
  File.open(filename, 'w') {|f| f.write(contents) }
end

def cleanPortalExtConfig
  filename = "#{SLI_ROOT}/config/config.in/portal_ext_config.yml"
  contents = File.read(filename)
  contents = removeProfile(contents,"devlycans")
  contents = removeProfile(contents,"devlr1")
  contents = removeProfile(contents,"devlr2")
  contents = removeProfile(contents,"testlr1")
  contents = removeProfile(contents,"demo")
  contents = sanitizeConfigProperties(contents)
  File.open(filename, 'w') {|f| f.write(contents) }
end


def removeFiles

  def deleteIfExists(file)
    if File.exists?(file)
      File.delete(file)
    end
  end

  deleteIfExists("#{SLI_ROOT}/data-access/dal/keyStore/ciKeyStore.jks")
  deleteIfExists("#{SLI_ROOT}/data-access/dal/keyStore/localKeyStore.jks")
  deleteIfExists("#{SLI_ROOT}/data-access/dal/keyStore/localRailsKey")
  deleteIfExists("#{SLI_ROOT}/databrowser/config/devmegatron_databrowser_config.yml")
  deleteIfExists("#{SLI_ROOT}/databrowser/config/megtomcat01_databrowser_config.yml")

end

def removeProfile(contents,profile)

  inProfile = false
  newContents = Array.new

  contents.split("\n").each do |line|
    if line.start_with?(profile)
      inProfile = true
    elsif line.match(/^[^\s].*/)
      inProfile = false
    end

    if !inProfile
      newContents << line
    end
  end

  newContents.join("\n")
end

def sanitizeConfigProperties(contents)
  subs = {
      # excludes # character to skip example rows, prefixed with '## '
      /(^[^#]+sli.security.sp.issuerName:).*$/ => '\1',
      /(^[^#]+sli.security.noSession.landing.url:).*$/ => '\1',
      /(^[^#]+sli.support.email:).*$/ => '\1',
      /(^[^#]+sli.api.cookieDomain:).*$/ => '\1',
      /(^[^#]+bootstrap.admin.realm.name:).*$/ => '\1',
      /(^[^#]+bootstrap.admin.realm.tenantId:).*$/ => '\1',
      /(^[^#]+bootstrap.admin.realm.idpId:).*$/ => '\1',
      /(^[^#]+bootstrap.admin.realm.redirectEndpoint:).*$/ => '\1',
      /(^[^#]+bootstrap.developer.realm.name:).*$/ => '\1',
      /(^[^#]+bootstrap.developer.realm.uniqueId:).*$/ => '\1',
      /(^[^#]+bootstrap.developer.realm.idpId:).*$/ => '\1',
      /(^[^#]+bootstrap.developer.realm.redirectEndpoint:).*$/ => '\1',
      /(^[^#]+bootstrap.sandbox.realm.idpId:).*$/ => '\1',
      /(^[^#]+bootstrap.sandbox.realm.redirectEndpoint:).*$/ => '\1',
      /(^[^#]+sli.landingZone.server:).*$/ => '\1',
      /(^[^#]+sli.api.ldap.user:).*$/ => '\1',
      /(^[^#]+sli.api.ldap.pass:).*$/ => '\1',
      /(^[^#]+sli.application.buildTag:).*$/ => '\1',
      /(^[^#]+sli.perf.mongodb.host:).*$/ => '\1',
      /(^[^#]+sli.search.username:).*$/ => '\1',
      /(^[^#]+sli.search.password:).*$/ => '\1',
      /(^[^#]+sli.ingestion.healthcheck.user:).*$/ => '\1',
      /(^[^#]+sli.ingestion.healthcheck.pass:).*$/ => '\1',
      /(^[^#]+landingzone.inbounddir:).*$/ => '\1',
      /(^[^#]+logging.path:).*$/ => '\1',
      /(^[^#]+api.server.url:).*$/ => '\1',
      /(^\s+security.server.url:).*$/ => '\1',
      /(^[^#]+portal.footer.url:).*$/ => '\1',
      /(^[^#]+portal.header.url:).*$/ => '\1',
      /(^[^#]+oauth.client.id:).*$/ => '\1',
      /(^[^#]+oauth.client.secret:).*$/ => '\1',
      /(^[^#]+oauth.redirect:).*$/ => '\1',
      /(^[^#]+dashboard.google_analytics.id:).*$/ => '\1',
      /(^[^#]+dashboard.encryption.keyStorePass:).*$/ => '\1',
      /(^[^#]+dashboard.encryption.dalKeyAlias:).*$/ => '\1',
      /(^[^#]+dashboard.encryption.dalKeyPass:).*$/ => '\1',
      /(^[^#]+dashboard.encryption.keyStore:).*$/ => '\1',
      /(^[^#]+bootstrap.app.portal.url:).*$/ => '\1',
      /(^[^#]+bootstrap.app.portal.client_id:).*$/ => '\1',
      /(^[^#]+bootstrap.app.portal.client_secret:).*$/ => '\1',
      /(^[^#]+bootstrap.app.sif.url:).*$/ => '\1',
      /(^[^#]+bootstrap.app.sif.apiUrl:).*$/ => '\1',
      /(^[^#]+bootstrap.app.sif.callbackUrl:).*$/ => '\1',
      /(^[^#]+bootstrap.app.sif.guid:).*$/ => '\1',
      /(^[^#]+bootstrap.app.sif.token:).*$/ => '\1',
      /(^[^#]+bootstrap.app.sif.client_id:).*$/ => '\1',
      /(^[^#]+bootstrap.app.sif.client_secret:).*$/ => '\1',
      /(^[^#]+sli.encryption.dalInitializationVector:).*$/ => '\1',
      /(^[^#]+sli.encryption.keyStorePass:).*$/ => '\1',
      /(^[^#]+sli.encryption.ldapKeyAlias:).*$/ => '\1',
      /(^[^#]+sli.encryption.ldapKeyPass:).*$/ => '\1',
      /(^[^#]+sli.encryption.dalKeyAlias:).*$/ => '\1',
      /(^[^#]+sli.encryption.dalKeyPass:).*$/ => '\1',
      /(^[^#]+sli.simple-idp.issuer-base:).*$/ => '\1',
      /(^[^#]+sli.simple-idp.cot:).*$/ => '\1',
      /(^[^#]+sli.simple-idp.sliAdminRealmName:).*$/ => '\1',
      /(^[^#]+sli.simple-idp.ldap.urls:).*$/ => '\1',
      /(^[^#]+sli.simple-idp.ldap.base:).*$/ => '\1',
      /(^[^#]+sli.sample.apiUrl:).*$/ => '\1',
      /(^[^#]+sli.sample.callbackUrl:).*$/ => '\1',
      /(^[^#]+sli.sample.clientId:).*$/ => '\1',
      /(^[^#]+sli.sample.clientSecret:).*$/ => '\1',
      /(^[^#]+sli.sif-agent.zoneUrl:).*$/ => '\1',
      /(^[^#]+sli.mongodb.host:).*$/ => '\1',
      /(^[^#]+sli.ingestion.staging.mongodb.host:).*$/ => '\1',
      /(^[^#]+sli.ingestion.batchjob.mongodb.host:).*$/ => '\1',
      /(^[^#]+sli.ingestion.queue.workItem.host:).*$/ => '\1',
      /(^[^#]+api.perf.log.path:).*$/ => '\1',
      /(^[^#]+sli.trust.certificates:).*$/ => '\1',
      /(^[^#]+bootstrap.app.admin.url:).*$/ => '\1',
      /(^[^#]+bootstrap.app.admin.client_id:).*$/ => '\1',
      /(^[^#]+bootstrap.app.admin.client_secret:).*$/ => '\1',
      /(^[^#]+bootstrap.app.databrowser.url:).*$/ => '\1',
      /(^[^#]+bootstrap.app.databrowser.client_id:).*$/ => '\1',
      /(^[^#]+bootstrap.app.databrowser.client_secret:).*$/ => '\1',
      /(^[^#]+bootstrap.app.dashboard.url:).*$/ => '\1',
      /(^[^#]+bootstrap.app.dashboard.client_id:).*$/ => '\1',
      /(^[^#]+bootstrap.app.dashboard.client_secret:).*$/ => '\1',
      /(^\s+log.path:).*$/ => '\1',
      /(^[^#]+portal_url:).*$/ => '\1',
      /(^[^#]+api_base:).*$/ => '\1',
      /(^[^#]+client_id:).*$/ => '\1',
      /(^[^#]+client_secret:).*$/ => '\1',
      /(^[^#]+redirect_uri:).*$/ => '\1',
      /(^[^#]+recaptcha_pub:).*$/ => '\1',
      /(^[^#]+recaptcha_priv:).*$/ => '\1',
      /(^[^#]+support_email:).*$/ => '\1',
      /(^[^#]+email_sender_address_user_reg_app:).*$/ => '\1',
      /(^[^#]+ldap_host:).*$/ => '\1',
      /(^[^#]+ldap_user:).*$/ => '\1',
      /(^[^#]+ldap_base:).*$/ => '\1',
      /(^[^#]+ldap_pass:).*$/ => '\1',
      /(^[^#]+encryption_iv:).*$/ => '\1',
      /(^[^#]+email_replace_uri:).*$/ => '\1',
      /(^.*email_sender_address:).*$/ => '\1',
      /(^\s+portal.oauth.redirect:).*$/ => '\1',
      /(^.*email_host:).*$/ => '\1',
      /(^[^#]+email_port:).*$/ => '\1',
      /(^[^#]+sli.google_analytics.id:).*$/ => '\1',
      /(^[^#]+sli.domain:).*$/ => '\1',
      /(^\s+portal.oauth.client.id:).*$/ => '\1',
      /(^\s+portal.oauth.client.secret:).*$/ => '\1',
      /(^\s+jdbc.default.username:).*$/ => '\1',
      /(^\s+jdbc.default.password:).*$/ => '\1',
      /(^\s+mail.session.mail.smtp.host:).*$/ => '\1',
      /(^\s+mail.session.mail.smtp.port:).*$/ => '\1',
      /(^\s+mail.session.mail.smtp.user:).*$/ => '\1',
      /(^\s+mail.session.mail.smtp.password:).*$/ => '\1',
      /(^\s+sli.cookie.domain:).*$/ => '\1',
      /(^[^#]+rsa_key_dir:).*$/ => '\1'
  }
  subs.each do |search,replacement|
    contents = contents.gsub(search,replacement)
  end
  contents
end


if __FILE__ == $0
  run()
end
