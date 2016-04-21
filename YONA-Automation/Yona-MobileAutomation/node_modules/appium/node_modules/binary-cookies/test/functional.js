/*global describe:true, it:true, before:true */
"use strict";

var binaryCookies = require('../cookies')
  , should = require('should')
  , _ = require('underscore')
  , path = require('path')
  , APPLE_COOKIES = path.resolve(__dirname, 'AppleStore.binarycookies');

describe('parser end-to-end tests', function() {
  var cookies = null;

  before(function() {
    cookies = binaryCookies();
  });

  it('should be able to return all cookies from binary file', function(done) {
    cookies.parse(APPLE_COOKIES, function(err, cookieObjs) {
      should.not.exist(err);
      _.each(testData, function(testObj, i) {
        cookieObjs[i].should.eql(testObj);
      });
      done();
    });
  });

});

var testData = [ { unknown: 113,
    flags: 0,
    unknown2: 0,
    expiration: new Date("Wed Apr 02 2014 14:56:02 GMT-0700 (PDT)"),
    creation: new Date("Tue Apr 02 2013 14:56:02 GMT-0700 (PDT)"),
    url: '.apple.com',
    name: 'dssid2',
    path: '/',
    value: 'b267acef-b91e-4a5e-8f15-54be2c037b1c' },
  { unknown: 76,
    flags: 0,
    unknown2: 0,
    expiration: new Date("Thu Apr 02 2015 14:56:02 GMT-0700 (PDT)"),
    creation: new Date("Tue Apr 02 2013 14:56:02 GMT-0700 (PDT)"),
    url: '.apple.com',
    name: 'pxro',
    path: '/',
    value: '1' },
  { unknown: 87,
    flags: 0,
    unknown2: 0,
    expiration: new Date("Thu Apr 02 2015 14:55:59 GMT-0700 (PDT)"),
    creation: new Date("Tue Apr 02 2013 14:56:00 GMT-0700 (PDT)"),
    url: '.apple.com',
    name: 's_invisit_n2_us',
    path: '/',
    value: '3' },
  { unknown: 98,
    flags: 0,
    unknown2: 0,
    expiration: new Date("Thu Apr 02 2015 14:55:59 GMT-0700 (PDT)"),
    creation: new Date("Tue Apr 02 2013 14:56:00 GMT-0700 (PDT)"),
    url: '.apple.com',
    name: 's_pathLength',
    path: '/',
    value: 'homepage%3D1%2C' },
  { unknown: 109,
    flags: 0,
    unknown2: 0,
    expiration: new Date("Thu Apr 02 2015 14:56:01 GMT-0700 (PDT)"),
    creation: new Date("Tue Apr 02 2013 14:56:02 GMT-0700 (PDT)"),
    url: '.apple.com',
    name: 's_pv',
    path: '/',
    value: 'apple%20-%20index%2Ftab%20%28us%29' },
  { unknown: 119,
    flags: 0,
    unknown2: 0,
    expiration: new Date("Thu Apr 02 2015 14:57:24 GMT-0700 (PDT)"),
    creation: new Date("Tue Apr 02 2013 14:57:24 GMT-0700 (PDT)"),
    url: '.apple.com',
    name: 's_vi',
    path: '/',
    value: '[CS]v1|28ADA9F785011356-60001602602D90C1[CE]' },
  { unknown: 88,
    flags: 0,
    unknown2: 0,
    expiration: new Date("Thu Apr 02 2015 14:55:59 GMT-0700 (PDT)"),
    creation: new Date("Tue Apr 02 2013 14:56:00 GMT-0700 (PDT)"),
    url: '.apple.com',
    name: 's_vnum_n2_us',
    path: '/',
    value: '3%7C1' },
  { unknown: 197,
    flags: 0,
    unknown2: 0,
    expiration: new Date("Tue Apr 02 2013 15:27:24 GMT-0700 (PDT)"),
    creation: new Date("Tue Apr 02 2013 14:57:24 GMT-0700 (PDT)"),
    url: '.store.apple.com',
    name: 'asmetrics',
    path: '/',
    value: '%257B%2522store%2522%253A%257B%2522sid%2522%253A%2522wHF2F2PHCCCX72KDY%2522%252C%2522vh%2522%253Atrue%257D%257D' },
  { unknown: 82,
    flags: 0,
    unknown2: 0,
    expiration: new Date("Tue Apr 02 2013 15:27:18 GMT-0700 (PDT)"),
    creation: new Date("Tue Apr 02 2013 14:57:18 GMT-0700 (PDT)"),
    url: '.store.apple.com',
    name: 'dc',
    path: '/',
    value: 'nwk' } ];

