/*
* Copyright 2012 Shared Learning Collaborative, LLC
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

// SLC Javascript SDK
// @provides	SLC.getLoginURL
//				SLC.logout
//				SLC.oauth
//				SLC.api

/*global require module */

(function () {

	var SLC = (function () {

		var request = require('request'),
			util = require('util');

		/**
		 * SLC constructor
	     *
	     * @access public
	     * @param baseURL {String}     	the base URL
	     * @param clientID {String}   	the client ID
	     * @param clientSecret {String} the client secret
	     * @param oauthURI {String}     the authorization URL
	     * @param apiVersion {String}  the api version
	     */
		function SLC(baseURL, clientID, clientSecret, oauthURI, apiVersion) {

			var API_url,
				client_ID,
				client_secret,
				oauth_URI,
				api_version;

			API_url = baseURL;
			client_ID = clientID;
			client_secret = clientSecret;
			oauth_URI = oauthURI;
			api_version = apiVersion || "v1";
		

			/**
		     * This method returns SLC login URL
		     * @access public
		     * @return SLC login URL
		     */
			function getLoginURL() {
				return API_url + '/api/oauth/authorize?response_type=code&client_id=' + client_ID + '&redirect_uri=' + oauth_URI;
			}

			/**
		     * This method logout user from SLC
		     * @params callback {Function}   the callback function to handle the response
		     * @access public
		     * @return logout success or error message
		     */
			function logout(callback) {

				var errorMessage;

				request(API_url + '/api/rest/system/session/logout', function(error, response, body) {

					if (error) {

						errorMessage = "Logout error: ";
						if (response.statusCode) {
							errorMessage += response.statusCode;
						}
						
						if (body) {
							errorMessage += " " + JSON.parse(body);
						}
						
						if (callback !== null) {
							callback(errorMessage);
							return;
						}
						else {
							return errorMessage;
						}
					}
					if (!error && response.statusCode === 200) {
						if (callback !== null) {
							callback(JSON.parse(body));
							return;
						}
						else {
							return body;
						}

					}
				});
			}

			/**
		     * OAuth
		     *
		     * @access public
		     * @param params 	
		     * @param callback {Function}   the callback function to handle the response
		     * @return api access token
		     */

			function oauth(params, callback) {
				var code,
					requestUrl,
					token;

				if (!params) {
					return "Invalid arguments";
				}

				code = params.code;

				if (code) {
					requestUrl = API_url + '/api/oauth/token?redirect_uri=' + oauth_URI
						+ '&grant_type=authorization_code&client_id=' + client_ID + '&client_secret='
						+ client_secret + '&code=' + code;

					request.get(requestUrl, function(error, response, body) {
						if (response.statusCode !== 200) {
							return "API error";
						}

						token = JSON.parse(body)['access_token'];
						util.puts(token);
						if (token === undefined) {
							return "Access token is undefined";
						}
						if (callback) {
							callback(token);
						}
						else {
							return token;
						}
					});

				}
				else {
					return "Invalid arguments";
				}
			}


			/**
			 * Make a api call to SLC server
		     *
		     * @access public
		     * @param path {String}	the url path
			 * @param method {String} the http method
			 * @param access_token {String} Access token (returned by "oauth" function)
		     * @param params {Object} the parameters for the query.
			 * @param data (Object) the body for POST and PUT requests.
		     * @param callback {Function} the callback function to handle the response
		     *
		     * @return response body / status code / error
		     *
		     * SLC.api("students", "GET", "2FRT334FEf454", {}, {}, function (data) { console.log(data) })
		     * SLC.api("/students", "GET", "2FRT334FEf454", {}, {}, function (data) { console.log(data) })
		     * SLC.api("/students/:id", "DELETE", "2FRT334FEf454", {}, {}, function (data) { console.log(data) })
		     * SLC.api(
			 * 		"/students",
			 * 		"POST",
			 * 		"2FRT334FEf454",
			 *		{},
			 * 		{
			 			"name": {
							"firstName": "Matt",
							"lastName": "Sollars"
			 			},
			 			"sex": "Male"
			 		},
			 * 		function (data) { console.log(data) }
			 * 	);
		     */
			function api(path, method, access_token, params, data, callback) {
				var args = Array.prototype.slice.apply(arguments),
					options, req, body, uri, key, value, headers;

				if (args.length !== 6) {
					return "No. of arguments passed to the function are not correct";
				}

				// remove prefix slash if one is given, as it's already in the base url
				if (path[0] === '/') {
					path = path.substr(1);
				}

				method = method.toLowerCase();

				api_version = api_version || 'v1';
				uri = API_url + '/api/rest/' + api_version + "/" + path;

				headers = { 'Authorization': 'Bearer ' + access_token, 'Content-Type': 'application/json'};

				if (method === "post" || method === "put") {
					if (data) {
						headers["Content-Length"] = data.toString().length; // Setting up content length in the request header
						body = JSON.stringify(data); // Setting up the body for POST and PUT method
					}
				} else {

					// For 'GET' request
					uri += '?';
					if (params) {
						for (key in params) {
							value = params.key;
							if (typeof value !== 'string') {
								value = JSON.stringify(value);
							}
							uri += encodeURIComponent(params.key) + '=' + encodeURIComponent(value) + '&';
						}
					}
					uri = uri.substring(0, uri.length -1);

				}

				options = {
					uri: uri,
					body: body,
					method: method,
					headers: headers
				};

				request(options, function(error, response, body) {

					if (error !== null) {
						callback(JSON.parse(body));
						return false;
					}

					if (!error) {

						if (response.statusCode === 200) {
							callback(JSON.parse(body));
						} else {
							callback(response.statusCode);
						}
					}

				});

				return true;
			}

			return {
				getLoginURL: getLoginURL,
				logout: logout,
				oauth: oauth,
				api: api
			};

		}

		return SLC;

	})();

	module.exports = SLC;
})();
