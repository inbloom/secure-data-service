/*global module test ok define equal deepEqual notEqual SLC $*/

module("SLC.grid.sorters");

	test('Test SLC.grid.sorters Namespace', function () {
		ok(SLC.grid.sorters !== undefined, 'SLC.sorters namespace should be defined');
		ok(typeof SLC.grid.sorters === 'object', 'SLC.sorters should be an object');
	});
	
	test('Test Enum method', function () {
		ok(SLC.grid.sorters.Enum !== undefined, 'SLC.grid.sorters Enum method should be defined');
		ok(typeof SLC.grid.sorters.Enum === 'function', 'SLC.grid.sorters Enum method should be function');
	});
	
	test('Test ProxyInt method', function () {
		ok(SLC.grid.sorters.ProxyInt !== undefined, 'SLC.grid.sorters ProxyInt method should be defined');
		ok(typeof SLC.grid.sorters.ProxyInt === 'function', 'SLC.grid.sorters ProxyInt method should be function');
	});
	
	test('Test LetterGrade method', function () {
		ok(SLC.grid.sorters.LetterGrade !== undefined, 'SLC.grid.sorters LetterGrade method should be defined');
		ok(typeof SLC.grid.sorters.LetterGrade === 'function', 'SLC.grid.sorters LetterGrade method should be function');
	});
	
	test('Test LettersAndNumbers method', function () {
		ok(SLC.grid.sorters.LettersAndNumbers !== undefined, 'SLC.grid.sorters LettersAndNumbers method should be defined');
		ok(typeof SLC.grid.sorters.LettersAndNumbers === 'function', 'SLC.grid.sorters LettersAndNumbers method should be function');
	});