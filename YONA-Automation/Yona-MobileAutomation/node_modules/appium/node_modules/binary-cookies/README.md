node-binary-cookies
===================

Binary cookies parser for Node.

Some Apple browsers like Safari and Mobile Safari store their cookies in
a `.binarycookie` format that is hard to parse. Node to the rescue! Say you
have a file at `/my/path/to/Cookies.binarycookies`. You can parse it like so:

```js
var CookieParser = require('./cookies')
  , cookiePath = "/my/path/to/Cookies.binarycookies";

CookieParser.parse(cookiePath, function(err, cookies) {
  console.log(cookies[0]);
});
```

This will output something like this:

```js
{
    flags: 0,
    expiration: new Date("Thu Apr 02 2015 14:55:59 GMT-0700 (PDT)"),
    creation: new Date("Tue Apr 02 2013 14:56:00 GMT-0700 (PDT)"),
    url: '.apple.com',
    name: 's_pathLength',
    path: '/',
    value: 'homepage%3D1%2C'
}
```

This should all be self-explanatory except flags:

|Flag|Meaning|
|----|-------|
|0|No flags|
|1|Secure cookie|
|4|HttpOnly cookie|
|5|Secure &amp; HttpOnly|

Have fun! I wrote this for [Appium](http://github.com/appium/appium), check it
out!

Acknowledgements
--------
I learned about the spec from [this blog entry](http://www.securitylearn.net/2012/10/27/cookies-binarycookies-reader/). Thanks Internet dude!
