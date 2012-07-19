module("SLC.loadingMask");
		
test('Test SLC.loadingMask namespace', function () {

	ok(SLC.loadingMask !== undefined, 'SLC.loadingMask namespace should be defined');
	ok(typeof SLC.loadingMask === 'object', 'SLC.loadingMask method should be function');
});

test('Test SLC.loadingMask.create method', function () {

	ok(SLC.loadingMask.create !== undefined, 'SLC.loadingMask create method should be defined');
	ok(typeof SLC.loadingMask.create === 'function', 'SLC.loadingMask create method should be function');
});

module("Loading Mask widget", {
	setup: function () {
		this.testLoader = SLC.loadingMask.create({context: "<div></div>", message: "testing..."});
		this.testLoader1 = SLC.loadingMask.create({context: "<div id='testing1'></div>"});
	},
	teardown: function () {
		this.testLoader.remove();
		this.testLoader1.remove();
	}
});

test("Test first loader", function () {
	equal(this.testLoader.length, 1, "First Loader instance is created.");
	
	this.testLoader.show();
	equal(this.testLoader.css("display"), "block");
	
	this.testLoader.hide();
	equal(this.testLoader.css("display"), "none");
});

test("Test second loader", function () {
	equal(this.testLoader1.length, 1, "Second Loader instance is created.");
	
	this.testLoader1.show();
	equal(this.testLoader1.css("display"), "block");
	
	this.testLoader1.hide();
	equal(this.testLoader1.css("display"), "none");

});
