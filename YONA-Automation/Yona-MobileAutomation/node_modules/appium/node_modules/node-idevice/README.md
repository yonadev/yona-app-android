# node-idevice

Install apps to your ios device with node.

This project depends on ideviceinstaller from the [libimobiledevice](http://www.libimobiledevice.org/) project. The currently preferred way of obtaining the binary is to use brew.
```
brew install ideviceinstaller
```
You can then tell node-idevice to use the command:
```javascript
// Use executable found in your $PATH
var device = new IDevice();

// Or you can manually set the executable
var device = new IDevice(false, {cmd: './path/to/ideviceinstaller'});
```

If you want to build the binary yourself you can try:
```
./utils/steps
```
This should pull and build all the dependencies. Be warned this is pretty long.

We currently support installing, removing and listing apps on a device.
### Installing
```javascript
var ipa = path.resolve(__dirname, '../path/to/your/App.ipa');
device.install(ipa, function (err) {
	// Do stuff when app is installed
});

// If you want to be sure the callback executes with the app on device you can use
device.installAndWait(ipa, 'domain.organisation.App', function (err, success) {
    // Do stuff when app is on device and ready
})
```
### Note
ideviceinstaller consumes IPA packages, please see the [docs](https://github.com/OniOni/node-idevice/blob/master/docs/building_ipa.md) on how to get an IPA from your App.

### Removing
```javascript
device.remove('domain.organisation.App', function (err) {
	// Do stuff when app is installed
});
```
### Checking if an app is Installed
```javascript
device.isInstalled(appName, function (err, installed) {
	// Installed is true when app is found on device
});
```
### Listing Installed apps
```javascript
device.listInstalled(function (err, data) {
	// data is a list of objects, one per app
	// The object contains info about the app, currently 'name' and 'fullname'
});
```
