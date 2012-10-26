var config = {};

config.app = {};

config.app.port = 8080;
config.api = {};

config.api.base_url = 'https://api.sandbox.slcedu.org';
config.api.client_id = '$YOUR_CLIENT_ID_HERE'
config.api.client_secret = '$YOUR_ENCRYPTED_CLIENT_SECRET_HERE';
config.api.oauth_uri = 'http://$YOUR_IP_ADDRESS:$YOUR_PORT/callback';
config.api.api_version = "$API_VERSION_HERE";

module.exports = config;