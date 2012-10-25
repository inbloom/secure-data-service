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

/*global require describe test module ok SLC*/


var assert = require("assert"),
	sinon = require("sinon"),
	SLC = require("../SLC");
	config = require('../../sample/config');

var SLC_app = new SLC(config.api.base_url, 
                  config.api.client_id, 
                  config.api.client_secret, 
                  config.api.oauthUri,
                  config.api.api_version);

describe('SLC', function(){
	describe('getLoginURL', function(){
		it('should be a function', function(){
			assert.ok(SLC_app.getLoginURL !== undefined);
			assert.ok(typeof SLC_app.getLoginURL === "function");
		});

		it('should return URL string', function(){
			assert.equal(typeof SLC_app.getLoginURL(), "string");
		});
	});

	describe('logout', function(){
		it('should be a function', function(){
			assert.ok(SLC_app.logout !== undefined);
			assert.ok(typeof SLC_app.logout === "function");
		});

		it('should return logout response in object format', function(done){
			SLC_app.logout(function (data){
				assert.equal(typeof data, "object");
				assert.ok(typeof data.msg !== undefined);
				done();
			});
		});
	});

	describe('oauth', function(){
		it('should be a function', function(){
			assert.ok(SLC_app.oauth !== undefined);
			assert.ok(typeof SLC_app.oauth === "function");
		});

		it("should return error if code is not passed to the function", function () {
			assert.equal(SLC_app.oauth(), "Invalid arguments");
		});

		it('should make oauth request call', function(){
			sinon.stub(SLC_app, "oauth");

			SLC_app.oauth({code: 'c-ddc302ad-8190-4743-b6b8-62b34c641f8d'}, sinon.spy());

			sinon.assert.calledOnce(SLC_app.oauth);
			sinon.assert.calledWithMatch(SLC_app.oauth, { code: 'c-ddc302ad-8190-4743-b6b8-62b34c641f8d' });
		});
	});

	describe('api', function(){
		it('should be a function', function(){
			assert.ok(SLC_app.api !== undefined);
			assert.ok(typeof SLC_app.api === "function");
		});

		it("should return error if 6 arguments are not passed to the functions", function () {
			assert.equal(typeof SLC_app.api('/students', "GET", "12344567788", {}, {}), "string");
			assert.equal(typeof SLC_app.api('/students', "GET", "12344567788", {}, function () {}), "string");
			assert.equal(typeof SLC_app.api('/students', "GET", {}, {}, function () {}), "string");
			assert.equal(SLC_app.api('/students', "GET", "12344567788", {}, {}, function () {}), true);
		});

		it('should make api request call', function(){

			sinon.stub(SLC_app, "api");

			SLC_app.api('/students', "GET", "12344567788", {}, {}, sinon.spy());

			sinon.assert.calledOnce(SLC_app.api);
			sinon.assert.calledWithMatch(SLC_app.api, '/students');
		});
	});
});

