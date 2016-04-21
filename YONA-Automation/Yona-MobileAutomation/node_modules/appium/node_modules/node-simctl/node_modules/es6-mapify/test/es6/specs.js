/* global describe:true, it:true */

import 'traceur/bin/traceur-runtime';
import should from 'should';
import { mapify, demapify } from '../../lib/es5/main';

describe("mapify", () => {
  it("should return a non-object as is", () => {
    mapify(2).should.equal(2);
    mapify('hi').should.equal('hi');
    (typeof mapify(undefined)).should.eql('undefined');
    mapify([1, 2, 3]).should.eql([1, 2, 3]);
    should.ok(mapify(null) === null);
  });

  it("should convert an empty object", () => {
    let m = mapify({});
    (typeof m.get('foo')).should.equal('undefined');
  });

  it("should convert a basic object", () => {
    let m = mapify({a: 'b'});
    m.get('a').should.equal('b');
  });

  it("should convert an object with multiple non-object types", () => {
    let m = mapify({a: 'b', 'foo-bar': 3, c: [1, "x", Array]});
    m.get('a').should.equal('b');
    m.get('foo-bar').should.equal(3);
    let c = m.get('c');
    c.should.have.length(3);
    c[0].should.eql(1);
    c[1].should.eql("x");
    (typeof (new c[2]())).should.equal('object');
  });

  it("should convert an array with an embedded object", () => {
    let a = mapify([1, {a: 'b'}, 2]);
    a[0].should.equal(1);
    a[2].should.equal(2);
    let m = a[1];
    should.ok(a[1] instanceof Map);
    m.get('a').should.equal('b');
  });

  it("should convert a nested object", () => {
    let m = mapify({a: {b: [1, {c: 'd'}], e: 'f'}, g: true});
    let a = m.get('a');
    m.get('g').should.equal(true);
    a.get('b')[1].get('c').should.equal('d');
    a.get('e').should.equal('f');
  });
});


describe("demapify", () => {
  it("should return a non-object as is", () => {
    demapify(2).should.equal(2);
    demapify('hi').should.equal('hi');
    (typeof demapify(undefined)).should.eql('undefined');
    demapify([1, 2, 3]).should.eql([1, 2, 3]);
  });

  it("should convert an empty map", () => {
    let m = demapify(new Map());
    m.should.eql({});
    (typeof m.foo).should.equal('undefined');
  });

  it("should convert a basic map", () => {
    let m = new Map();
    m.set('a', 'b');
    demapify(m).should.eql({a: 'b'});
  });

  it("should convert a map multiple non-object types", () => {
    let m = new Map();
    m.set('a', 'b');
    m.set('foo-bar', 3);
    m.set('c', [1, "x", Array]);
    let d = demapify(m);
    d.a.should.equal('b');
    d['foo-bar'].should.equal(3);
    d.c.should.have.length(3);
    d.c[0].should.eql(1);
    d.c[1].should.eql("x");
    (typeof (new d.c[2]())).should.equal('object');
  });

  it("should convert an array with an embedded map", () => {
    let m = new Map();
    m.set('a', 'b');
    let a = demapify([1, m, 2]);
    a[0].should.equal(1);
    a[1].should.eql({a: 'b'});
    a[2].should.equal(2);
  });

  it("should convert a nested object", () => {
    let m = new Map();
    let m2 = new Map();
    let m3 = new Map();
    m3.set('c', 'd');
    m2.set('b', [1, m3]);
    m2.set('e', 'f');
    m.set('a', m2);
    m.set('g', true);
    let d = demapify(m);
    d.should.eql({a: {b: [1, {c: 'd'}], e: 'f'}, g: true});
  });
});
