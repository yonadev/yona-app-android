# Getting started with IPAS 

## Step 1

### Make awesome App

## Step 2

### Getting a provisioning profile
You can find information about provisioning profiles and the general iOS dev work-flow at the [Apple Dev website][1] or more specifically information about [distribution][3] or [organization][2] 

### Build app
```
xcodebuild -sdk iphoneos -configuration Debug CODE_SIGN_IDENTITY="iPhone Developer" PROVISIONING_PROFILE="XXXXXXXX-XXXXX-XXXX-XXXXX-XXXXXXXXXXX"
```

### Package IPA
```
xcrun -sdk iphoneos PackageApplication relative/path/to/your/App.app -o /Absolute/Path/for/your/packaged/App.ipa
```

## Step 3
...

## Step 4
Profit 
```
drink IPA
```

[1]: https://developer.apple.com/ "Apple Dev hub"
[2]: https://developer.apple.com/library/ios/#recipes/xcode_help-devices_organizer/_index.html "Device Organizer Help"
[3]: https://developer.apple.com/library/ios/#documentation/IDEs/Conceptual/AppDistributionGuide/Introduction/Introduction.html "About App Distribution"

