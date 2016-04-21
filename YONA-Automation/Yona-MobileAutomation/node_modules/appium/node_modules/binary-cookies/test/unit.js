/*global describe:true, it:true, before:true */
"use strict";

var binaryCookies = require('../cookies')
  , should = require('should')
  , _ = require('underscore')
  , path = require('path')
  , BAD_COOKIES = path.resolve(__dirname, 'NotCookies.binarycookies')
  , APPLE_COOKIES = path.resolve(__dirname, 'AppleStore.binarycookies');

describe('parser unit tests', function() {
  var cookies = null;

  before(function() {
    cookies = binaryCookies();
  });

  it('should not be able to open non-existent file', function(done) {
    cookies.cookiePath = "/does/not/exist.binarycookies";
    cookies._open(function(err) {
      should.exist(err);
      done();
    });
  });

  it('should not be able to open non-binary-cookie file', function(done) {
    cookies._init();
    cookies.cookiePath = BAD_COOKIES;
    cookies._open(function(err) {
      should.exist(err);
      done();
    });
  });


  it('should be able to get header from binary-cookie file', function(done) {
    cookies._init();
    cookies.cookiePath = APPLE_COOKIES;
    cookies._open(function(err) {
      should.not.exist(err);
      done();
    });
  });

  it('should get number of pages', function(done) {
    var numPages = cookies._getNumPages();
    numPages.should.equal(2);
    done();
  });

  it('should get page sizes', function(done) {
    var sizes = cookies._getPageSizes();
    sizes[0].should.equal(730);
    sizes[1].should.equal(299);
    done();
  });

  it('should get page data', function(done) {
    var rawPages = cookies._getPages();
    rawPages[0].buf.length.should.equal(730);
    rawPages[1].buf.length.should.equal(299);
    done();
  });

  it('should get number of cookies in a page', function(done) {
    var numCookies = cookies._getNumCookies(0);
    numCookies.should.equal(7);
    numCookies = cookies._getNumCookies(1);
    numCookies.should.equal(2);
    done();
  });

  it('should get cookie sizes', function(done) {
    var sizes = cookies._getCookieOffsets(0);
    sizes.should.eql([40, 153, 229, 316, 414, 523, 642]);
    sizes = cookies._getCookieOffsets(1);
    sizes.should.eql([20, 217]);
    done();
  });

  it('should get cookie data chunk out of pages', function(done) {
    var chunks = cookies._getCookieData(0);
    var cookieLens = _.pluck(_.pluck(chunks, 'buf'), 'length');
    cookieLens.should.eql([113, 76, 87, 98, 109, 119, 88]);
    done();
  });

  it('should parse cookie data from individual cookies', function(done) {
    var data = cookies._parseCookieData(0, 0);
    var testObj = {
      unknown: 113,
      flags: 0,
      unknown2: 0,
      expiration: new Date("Wed Apr 02 2014 14:56:02 GMT-0700 (PDT)"),
      creation: new Date("Tue Apr 02 2013 14:56:02 GMT-0700 (PDT)"),
      url: '.apple.com',
      name: 'dssid2',
      path: '/',
      value: 'b267acef-b91e-4a5e-8f15-54be2c037b1c'
    };
    data.should.eql(testObj);
    cookies._getNumCookies(1);
    cookies._getCookieOffsets(1);
    cookies._getCookieData(1);
    data = cookies._parseCookieData(1, 1);
    testObj = {
      unknown: 82,
      flags: 0,
      unknown2: 0,
      expiration: new Date("Tue Apr 02 2013 15:27:18 GMT-0700 (PDT)"),
      creation: new Date("Tue Apr 02 2013 14:57:18 GMT-0700 (PDT)"),
      url: '.store.apple.com',
      name: 'dc',
      path: '/',
      value: 'nwk'
    };
    data.should.eql(testObj);
    done();
  });

});
