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

/*
 * SLC attendance calendar
 * The module contains SLC attendance calendar plugin and calendar creation method
 */
/*global SLC $ jQuery*/

SLC.namespace('SLC.attendanceCalendar', (function () {

	var util = SLC.util;

	/*
	 *	SLC attendanceCalendar plugin
	 *  @param options
	 *	Example: $("#table1").attendanceCalendar(calendarOptions)
	 */
	(function ($) {

		$.fn.attendanceCalendar = function (calendarOptions) {

			var options;

			options = {
				dayNamesMin: ["S", "M", "T", "W", "T", "F", "S"],
				hideIfNoPrevNext: true
			};

			options = $.extend(options, calendarOptions);
			$(this).datepicker(options);
		};
	})(jQuery);

	/*
	 * Creates SLC attendance calendar
	 * @param containerId - The container id for calendar
	 * @param panelData
	 * @param options
	 */
	function create(calendarId, panelData, options) {

		var calendarOptions,
			absentData = [],
			minDate,
			maxDate,
			startDate,
			endDate,
			i,
			util = SLC.util;

		absentData = panelData.attendanceList;
		startDate = panelData.startDate;
		endDate = panelData.endDate;

		function setMinMaxDates() {
			var newDate = startDate.split("-");
			minDate = newDate[0] + "," + newDate[1] + "," + newDate[2];
			maxDate = newDate[0] + ",12," + newDate[2];
		}

		function absentDays(date) {
			var m = util.pad2(parseInt(date.getMonth(), 10)+1),
				d = util.pad2(parseInt(date.getDate(), 10)),
				y = date.getFullYear(),
				formattedDate = y + '-' + (m) + '-' + d;

			for (i = 0; i < absentData.length; i++) {
				if(formattedDate === absentData[i].date) {
					return [false, absentData[i].event, absentData[i].reason];
				}
			}

			// if the date is greater than end date, it should be grayed out
			if (formattedDate > endDate) {
				return [false, 'disableDays'];
			}

			return [false, ''];
		}

		// Check the type of day: weekend/weekday
		function formatDays(date) {
			var noWeekend = jQuery.datepicker.noWeekends(date);
			// If it's a weekday, check for special formatting
			return noWeekend[0] ? absentDays(date) : noWeekend;
		}


		setMinMaxDates();

		calendarOptions = {
			numberOfMonths: [3, 4],
			minDate: new Date(minDate),
			maxDate: new Date(maxDate),
			beforeShowDay: formatDays
		};

		if (options) {
			calendarOptions = $.extend(calendarOptions, options);
		}

		return $("#" + calendarId).attendanceCalendar(calendarOptions);
	}

	return {
		create: create
	};
}())
);