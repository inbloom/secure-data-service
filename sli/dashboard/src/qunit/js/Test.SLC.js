module("SLC namespace");
		
test('Test SLC namespace', function () {

	ok(SLC !== undefined, 'SLC namespace should be defined');
	ok(typeof SLC === 'object', 'SLC method should be an object');
});

test('Test SLC.namespace method', function () {

	ok(SLC.namespace !== undefined, 'SLC.namespace method should be defined');
	ok(typeof SLC.namespace === 'function', 'SLC.namespace method should be function');
	
	SLC.namespace("SLC.module1");
	SLC.namespace("SLC.module2", function () {});
	
	ok(SLC.module1 !== undefined, 'SLC.module1 method should be defined');
	ok(typeof SLC.module1 === 'object', 'SLC.module1 method should be an object');
	
	ok(SLC.module2 !== undefined, 'SLC.module2 method should be defined');
	ok(typeof SLC.module2 === 'function', 'SLC.module2 method should be function');
});