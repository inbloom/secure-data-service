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
 * SLC attendance calendar
 * The module contains SLC attendance calendar plugin and calendar creation method
 */
/*global SLC $ jQuery*/

SLC.namespace('SLC.attendanceCalendar', (function () {

	var util = SLC.util,
		weekDayFormat = ["S", "M", "T", "W", "T", "F", "S"];

	/*
	 *	SLC attendanceCalendar plugin
	 *  @param options
	 *	Example: $("#table1").attendanceCalendar(calendarOptions)
	 */
	(function ($) {

		$.fn.attendanceCalendar = function (calendarOptions) {

			var options;

			options = {
				dayNamesMin: weekDayFormat,
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
			startDate,
			endDate,
			startEndDateFormat = 'MM dd,yy', // Example: August 08,2012
			dateFormatFromServer = 'yy-mm-dd',
			i;

		if (panelData === null || panelData === undefined || panelData.length < 1) {
			return false;
		}

		if(panelData.attendanceList === null || panelData.attendanceList === undefined || !panelData.startDate || !panelData.endDate) {
			return false;
		}

		absentData = panelData.attendanceList;
		startDate = $.datepicker.parseDate(dateFormatFromServer, panelData.startDate);
		endDate = $.datepicker.parseDate(dateFormatFromServer, panelData.endDate);

		function getMinDate() {
			return new Date($.datepicker.formatDate(startEndDateFormat, startDate));
		}

		function getMaxDate() {
			return new Date($.datepicker.formatDate(startEndDateFormat, endDate));
		}

		function absentDays(date) {
			var formattedDate = $.datepicker.formatDate(dateFormatFromServer, date);

			for (i = 0; i < absentData.length; i++) {
				if(formattedDate === absentData[i].date) {
					return [false, absentData[i].event, absentData[i].reason];
				}
			}

			// if the date is greater than end date, it should be grayed out
			if (date < startDate || date > endDate) {
				return [false, 'disableDays'];
			}

			return [false, ''];
		}

		// Check the type of day: weekend/weekday
		function formatDays(date) {
			var noWeekend = $.datepicker.noWeekends(date);
			// If it's a weekday, check for special formatting
			return noWeekend[0] ? absentDays(date) : noWeekend;
		}

		calendarOptions = {
			numberOfMonths: [3, 4],
			minDate: getMinDate(),
			maxDate: getMaxDate(),
			beforeShowDay: formatDays
		};

		if (options) {
			calendarOptions = $.extend(calendarOptions, options);
		}

		if($(".repeatHeaderTable1:last").length > 0) {
			$("#" + calendarId).appendTo(".repeatHeaderTable1:last");
		}

		return $("#" + calendarId).attendanceCalendar(calendarOptions);
	}

	return {
		create: create
	};
}())
);
