var IDevice = require('../main.js'),
    assert = require('assert'),
    path = require('path');

var device = new IDevice();

var app = path.resolve(__dirname, '../apps/TestApp.ipa'),
    appName = 'io.appium.TestApp';


device.isInstalled(appName, function (err, installed) {
    assert.equal(false, installed);

    device.installAndWait(app, appName, function (err, success) {
	assert.equal(null, err);
	assert.ok(success);
	console.log('Installed !');
	
	device.isInstalled(appName, function (err, installed) {
	    assert.equal(null, err);
	    assert.ok(installed);
	    
	    device.remove(appName, function (err) {
		assert.equal(null, err);
    		console.log('Removed !');
	    });
	});
    });
});


