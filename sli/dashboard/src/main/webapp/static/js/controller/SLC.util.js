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
 * SLC util
 * Handles reusable or common methods required by other SLC javascript files
 */
/*global SLC $ window */

SLC.namespace('SLC.util', (function () {
		var counterInt = 1,
			tableId,
			contextRootPath = "";
		
		function counter() {
			counterInt ++;
			return counterInt;
		}
		
		function compareInt(a,b) { 
			if(!isNaN(a) && !isNaN(b)) {
				return a-b; 
			}
		}
		
		function compareIntReverse(a,b) { 
			if(!isNaN(a) && !isNaN(b)) {
				return b-a; 
			}
		}
		
		function getElementFontSize(element) {
		    var elemStyle = this.getStyleDeclaration(element);
		    return parseInt(elemStyle.fontSize, 10);
		}
		
		function getElementColor(element) {
		    var elemStyle = this.getStyleDeclaration(element);
		    return elemStyle.color;
		}
		
		function getElementWidth(element) {
		    return $(element).width();
		}
		
		function getElementHeight(element) {
		    return $(element).height();
		}
		
		function makeTabs(element) {
			if ($(element).length) {
				$(element).tabs();
				return true;
			}
			
			return false;
		}
		
		function numbersFirstComparator(a,b){
		    var aIsNull = a === null,
				bIsNull = b === null,
				aIsNumber = a.match(/^\d+$/),
				bIsNumber = b.match(/^\d+$/);
		    
		    if (aIsNull && bIsNull) {
		        return 0;
		    } else if (aIsNull) {
		        return 1;
		    } else if (bIsNull) {
		        return -1;
		    }
		    
		    //Need to do numerical comparison. With lexicographical compare,
		    //the statement '15' < '2' evaluates to true.
		    if(aIsNumber && bIsNumber) {    
				return parseInt(a, 10) - parseInt(b, 10);
		    }
		    
		    //A lexicographical comparison is sufficient for two strings
		    if(!aIsNumber && !bIsNumber) {
		        return a < b ? -1 : (a == b ? 0 : 1);
		    }
		    
		    //Since we know that one of the values is a number and one is not 
		    //and we want numbers to precede strings.
		    if(aIsNumber) {
		        return -1;
		    } else {
		        return 1;
		    }
		}
		
		function sortObject(o, compare) {
		    var sorted = {},
		    key, a = [];
		    
		    for (key in o) {
		        if (o.hasOwnProperty(key)) { 
					a.push(key);
				}
		    }
		    
		    a.sort(compare);
		    
		    for (key = 0; key < a.length; key++) {
		        sorted[a[key]] = o[a[key]];
		    }
		    
		    return sorted;
		}
		
		/*
		 * Check for ajax error response
		 */
		function checkAjaxError(XMLHttpRequest, requestUrl) {
		    if (XMLHttpRequest.status !== 200) {
		        window.location = requestUrl;
		    }
		}
		
		/*
		 * Gets the style object for the element where we're drawing the fuel gauge.
		 * @return CSSStyleDeclaration object 
		 */
		function getStyleDeclaration(element) {
		    var compStyle;
		    
		    if (window.getComputedStyle) {
		        compStyle = window.getComputedStyle(element, null);
		    } else {
				compStyle = element.currentStyle;
		    }
		    
		    return compStyle;
		}
		
		function renderLozenges(student) {
			var config = SLC.dataProxy.getWidgetConfig("lozenge"),
				item, condition, configItem,
				lozenges = '',
				i = 0;
				
			for (; i < config.items.length; i++) {
				configItem = config.items[i];
				condition = configItem.condition;
				item = student[condition.field];
				if (item) {
					for (var y in condition.value) {
						if (condition.value[y] == item) {
							lozenges += '<span class="lozenge-widget ' + configItem.style + '">' + configItem.name + '</span>';
						}
					}
				}
			}
			
			return lozenges;
		}
		
		function checkCondition(data, condition) {
			var validValues = condition.value,
				values = data[condition.field];
		    
		    if (condition === undefined) {
		        return false;
		    }
	
		    if (values === undefined || validValues === undefined) {
				return false;
		    }
		    
		    for (var j=0; j < validValues.length; j++) {
		        for (var k=0; k < values.length; k++) {
		            if (validValues[j] == values[k]) {
		                return true;
		            }
		        }
		    } 
		    
		    return false;
		}
		
		function displayErrorMessage(error) {
		    $("#dsh_dv_error").show();
		    $("#viewSelection").hide();
		    $("#dsh_dv_error").html(error);
		}
		
		function hideErrorMessage() {
		    $("#dsh_dv_error").hide();
		}
		
		function setDropDownOptions(name, defaultOptions, options, titleKey, valueKey, autoSelect, callback) {
			var select =  "",
			autoSelectOption = -1;
			
			$("#"+name).find("dropdown-menu").html(select);
			
			
			if(options === null || options === undefined || options.length === 0) {
		        this.displayErrorMessage("There is no data available for your request.  Please contact your IT administrator.");
			} else {
				if (options.length === 1 && autoSelect) {
					autoSelectOption = 0;
				}
				if (defaultOptions !== undefined && defaultOptions !== null) {

					$.each(defaultOptions, function(val, displayText) {
						select += "    <li class=\"\"><a href=\"#\" onclick=\"SLC.util.hideErrorMessage()\">" + displayText + "</a>" +
						"<input type='hidden' value='"+ val + "' id ='selectionValue' /></li>";
					});
				}
				
				for(var index = 0; index < options.length; index++) {
					var selected = index == autoSelectOption ? "selected" : "";
					select += "    <li class=\"" + selected + "\"><a href=\"#\" onclick=\"SLC.util.hideErrorMessage()\">" +$.jgrid.htmlEncode(options[index][titleKey])+"</a>" +
								"<input type='hidden' value='"+ index + "' id ='selectionValue' /></li>";
				}
				
				$("#"+name + "SelectMenu").find(".optionText").html("Choose one");
			}
			
			$("#"+name + "SelectMenu .dropdown-menu").html(select);
			$("#"+name + "SelectMenu .disabled").removeClass("disabled");
			$("#"+name + "SelectMenu .dropdown-menu li").click( function() {
				$("#"+name + "SelectMenu .selected").removeClass("selected");
				$("#"+name + "SelectMenu").find(".optionText").html($(this).find("a").html());
				$("#"+name + "Select").val($(this).find("#selectionValue").val());
				$(this).addClass("selected");
				callback($("#"+name + "Select"));
			});
		  
			$("#"+name + "SelectMenu .selected").click();
			
		}
		
		function selectDropDownOption(name, optionValue, doClick) {
			$("#" + name + "SelectMenu .dropdown-menu li").each(function() {
				if (optionValue == $(this).find("#selectionValue").val()) {
					$(this).addClass("selected");
					if (doClick) {
						$(this).click();
					} else {
						$("#" + name + "Select").val(optionValue);
						$("#" + name + "SelectMenu .optionText").html($(this).find("a").html());
					}
				}
			});
		}
		
		function placeholderFix() {
			$('[placeholder]').focus(function() {
				var input = $(this);
				if (input.val() === input.attr('placeholder')) {
					input.val('');
					input.removeClass('placeholder');
				}
			}).blur(function () {
				var input = $(this);
				if (input.val() === '' || input.val() === input.attr('placeholder')) {
					input.addClass('placeholder');
					input.val(input.attr('placeholder'));
				}
			}).blur(); 
			
			return true;
		}
		
		function getLayoutLink(name, id, queryString) {
			return contextRootPath + "/s/l/" + name + ((id) ? ("/" + id) : "") + ((queryString) ? ('?' + queryString) : '');
		}

		function goToLayout(name, id, queryString) {
			location.href = getLayoutLink(name, id, queryString);
		}
		
		function setTableId(id) {
			if (typeof id === "string") { 
				tableId = id;
				return true;
			}
			
			return false;
		}
	    
		function getTableId() {
	        return tableId;
	    }
	    
	    function setContextRootPath(path) {
			if (typeof path === "string") {
				contextRootPath = path;
				return true;
			}
			
			return false;
	    }
	    
		function getContextRootPath() {
	        return contextRootPath;
	    }
	    
		$(document).ready( function() {
		    $('#banner #dbrd_frm_search').live("submit", function(e) {
			  e.preventDefault();
			  var studentName = $('#dbrd_inp_search_studentName').val();
			  if (!studentName || studentName === "Enter student name") {
				  studentName = '';
			  }

			  goToLayout('studentSearch', null, 'name=' + studentName);
			  return false;
			});
		});
		
		return {
			counter: counter,
			compareInt: compareInt,
			compareIntReverse: compareIntReverse,
			getElementFontSize: getElementFontSize,
			getElementColor: getElementColor,
			getElementWidth: getElementWidth,
			getElementHeight: getElementHeight,
			makeTabs: makeTabs,
			numbersFirstComparator: numbersFirstComparator,
			sortObject: sortObject,
			checkAjaxError: checkAjaxError,
			getStyleDeclaration: getStyleDeclaration,
			renderLozenges: renderLozenges,
			checkCondition: checkCondition,
			displayErrorMessage: displayErrorMessage,
			hideErrorMessage: hideErrorMessage,
			setDropDownOptions: setDropDownOptions,
			selectDropDownOption: selectDropDownOption,
			placeholderFix: placeholderFix,
			goToLayout: goToLayout,
			getLayoutLink: getLayoutLink,
			setTableId: setTableId,
			getTableId: getTableId,
			setContextRootPath: setContextRootPath,
			getContextRootPath: getContextRootPath
		};
	}())
);
