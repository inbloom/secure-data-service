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

		$.fn.attendanceCalendar = function (data, startDate, endDate, options) {

			var minDate = "",
				maxDate = "",
				i;

			function absenceDays(date) {
				var m = pad2(parseInt(date.getMonth(), 10)+1),
					d = pad2(parseInt(date.getDate(), 10)),
					y = date.getFullYear(),
					formattedDate = y + '-' + (m) + '-' + d;

				for (i = 0; i < data.length; i++) {
					if(formattedDate === data[i].date) {
						if (data[i].event === "Tardy") {
							return [false, "tardy", data[i].reason];
						}
						else if (data[i].event === "Excused Absence") {
							return [false, "excusedAbsence", data[i].reason];
						}
						else if (data[i].event === "Unexcused Absence") {
							return [false, "unexcusedAbsence", data[i].reason];
						}
					}
				}

				// if the date is greater than end date, it should be grayed out
				if (formattedDate > endDate) {
					return [false, 'disableDays'];
				}

				return [false, ''];
			}
			function noWeekendsOrHolidays(date) {
				var noWeekend = jQuery.datepicker.noWeekends(date);
				return noWeekend[0] ? absenceDays(date) : noWeekend;
			}
			function pad2(number) {
				return (number < 10 ? '0' : '') + number;
			}

			function minmaxDate() {
				var newDate = startDate.split("-");
				minDate = newDate[0] + "," + newDate[1] + "," + newDate[2];
				maxDate = newDate[0] + ",12," + newDate[2];
			}

			minmaxDate();

			$(this).datepicker({
				numberOfMonths: [3, 4],
				dayNamesMin: ["S", "M", "T", "W", "T", "F", "S"],
				hideIfNoPrevNext: true,
				minDate: new Date(minDate),
				maxDate: new Date(maxDate),
				beforeShowDay: noWeekendsOrHolidays
			});
		};
	})(jQuery);

	/*
	 * Creates SLC attendance calendar
	 * @param containerId - The container id for calendar
	 * @param panelData
	 * @param options
	 */
	function create(calendarId, data, options) {

		var calendarOptions = {},
			panelData,
			absentData = [],
			startDate,
			endDate,
			i;

		panelData = [{reason: "Absent excused", event:"Excused Absence", date:"2011-09-01"}, {event:"In Attendance", date:"2011-09-02"}, {event:"In Attendance", date:"2011-09-05"}, {reason: "Absent unexcused", event:"Unexcused Absence", date:"2011-09-06"}, {event:"In Attendance", date:"2011-09-07"}, {event:"In Attendance", date:"2011-09-08"}, {reason:"traffic", event:"Tardy", date:"2011-09-09"}, {reason:"Missed school bus", event:"Tardy", date:"2011-09-12"}, {event:"In Attendance", date:"2011-09-13"}, {event:"In Attendance", date:"2011-09-14"}, {event:"In Attendance", date:"2012-05-14"}];

			if (options) {
				calendarOptions = $.extend(calendarOptions, options);
			}

		for (i = 0; i < panelData.length; i++) {
			if(panelData[i].event === "Tardy" || panelData[i].event === "Excused Absence" || panelData[i].event === "Unexcused Absence"){
				absentData.push(panelData[i]);
			}
		}

		startDate = panelData[0].date;
		endDate = panelData[panelData.length - 1].date;

		return $("#" + calendarId).attendanceCalendar(absentData, startDate, endDate, calendarOptions);
	}

	return {
		create: create
	};
}())
);