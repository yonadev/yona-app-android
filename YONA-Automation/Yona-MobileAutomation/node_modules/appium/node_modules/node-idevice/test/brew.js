var IDevice = require('../main.js'),
    assert = require('assert'),
    path = require('path');

var device = new IDevice(false, {cmd: 'ideviceinstaller'});

device.listInstalled(function (err, data) {
    assert.equal(err, null, 'Error should be null. Error was '+err);
    assert.equal(data.length, 2, 'Only two apps should be installed');

    device.remove('io.appium.TestApp', function (err) {
	assert.equal(null, err);
	console.log('Removed !');

	var app = path.resolve(__dirname, '../apps/TestApp.ipa');
	device.install(app, function (err) {
	    assert.equal(null, err);
	    console.log('Installed !');

	    device.isInstalled('TestApp', function (err, installed) {
		assert.equal(null, err);
		assert.ok(installed);
		console.log('On device !');
	    })
	});

    });
});
