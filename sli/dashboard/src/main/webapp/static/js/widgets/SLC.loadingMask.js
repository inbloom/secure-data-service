/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

/* 
 *	Creates 'loading graphic' jquery widget for SLC
 *	Example: w_studentListLoader = $("<div></div>").loader();
 *			 w_studentListLoader.show();
 */
/*global $ SLC*/

SLC.namespace("SLC.loadingMask", (function () {
	
		$.widget( "SLC.loader", {
			
			options: {
				message: "Loading..."
			},
			
		    _create: function () {
		        var message = this.options.message;
		        this.element
		            .addClass( "loader" )
		            .html("<div class='message'>" + message + "</div>")
		            .appendTo("body");
		    },
		    
		    message: function (message) {
		        if ( message === undefined || typeof message !== "string" ) {
		            return this.options.message;
		        } else {
		            this.options.message = message;
		            this.element.find(".message").html(this.options.message);
		        }
		    }
		});
		
		return {
			create: function (opts) {
				var context = opts.context || "<div></div>";
				return $(context).loader(opts.message);
			}
		};
	}())
);
