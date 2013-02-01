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

/*global SLC module ok test deepEqual $*/
module("SLC.attendanceCalendar");

	test('Test SLC.attendanceCalendar Namespace', function () {
		ok(SLC.attendanceCalendar !== undefined, 'SLC.attendanceCalendar namespace should be defined');
		ok(typeof SLC.attendanceCalendar === 'object', 'SLC.attendanceCalendar should be an object');
	});
	
module("SLC.attendanceCalendar.create", {
	setup: function () {
		$("body").append('<div id="calendar1" class="attendanceCalendar"></div>');
		$("body").append('<div id="calendar2" class="attendanceCalendar"></div>');
		$("body").append('<div id="calendar3" class="attendanceCalendar"></div>');
	},
	teardown: function () {
		$("#calendar1").remove();
		$("#calendar2").remove();
		$("#calendar3").remove();
	}
});
	
	test('Test create method', function () {

		var calendarData = [],
			calendarWithNoData,
			calendarWithNoAbsentData,
			CalendarWithNoStartEndDate;

		ok(SLC.attendanceCalendar.create !== undefined, 'SLC.attendanceCalendar create method should be defined');
		ok(typeof SLC.attendanceCalendar.create === 'function', 'SLC.attendanceCalendar create method should be function');

		calendarWithNoData = SLC.attendanceCalendar.create("calendar1", calendarData);
		deepEqual(calendarWithNoData, false, 'Create function should return false if no data is available.');
		
		calendarData = {
			attendanceList: [
				{ date: "2011-09-07", event: "UnexcusedAbsence" },
				{ date: "2011-10-04", event: "Tardy", reason: "Missed school bus" },
				{ date: "2011-10-26", event: "ExcusedAbsence", reason: "Absent excused" }
			],
			startDate: "",
			endDate: ""
		};
		CalendarWithNoStartEndDate = SLC.attendanceCalendar.create("calendar1", calendarData);
		deepEqual(CalendarWithNoStartEndDate, false, 'Create function should return false if no start date and end date is available.');

		calendarData = {
			attendanceList: [],
			startDate: "2011-09-06",
			endDate: "2011-12-16"
		};

		calendarWithNoAbsentData = SLC.attendanceCalendar.create("calendar1", calendarData);
		deepEqual($("#calendar1").length, 1, 'First instance of calendar should be created');


		calendarData = {
			attendanceList: [
				{ date: "2011-09-07", event: "UnexcusedAbsence" },
				{ date: "2011-10-04", event: "Tardy", reason: "Missed school bus" },
				{ date: "2011-10-26", event: "ExcusedAbsence", reason: "Absent excused" }
			],
			startDate: "2011-09-06",
			endDate: "2012-05-16"
		};

		SLC.attendanceCalendar.create("calendar1", calendarData);
		deepEqual($("#calendar2").length, 1, 'Second instance of calendar should be created');

		SLC.attendanceCalendar.create("calendar2", calendarData);
		deepEqual($("#calendar3").length, 1, 'Third instance of calendar should be created');


	});
