ES6-Mapify
======

Convert JS Objects to ES6 Maps and vice versa.

ES6 is really nice for iteration, but it's not so nice for directly referencing properties, the way JS Objects are. This is a nice way to convert back and forth. First, simply use `npm` to include `mapify` in your project's dependencies:

```
npm install es6-mapify
```

Now you can import it and use it like so:

```js
import { mapify } from 'es6-mapify';

// converts basic objects
let myObj = {foo: 'bar'};
let myMap = mapify(myObj);
myMap.get('foo'); // 'bar'

// doesn't do anything to non-objects
mapify('foo'); // 'foo';
mapify(null);  // null

// is smart about objects nested inside arrays and other objects
let arrMap = mapify([1, {foo: 'bar'}, 3]);
arrMap[2];            // 3
arrMap[1].get('foo'); // 'bar'

let myMap = mapify({foo: {bar: 'baz'}});
myMap.get('foo').get('bar'); // 'baz';
```

Of course, you might want to go the other direction too! If you have a `Map` and want the corresponding basic JS object, just use `demapify`:

```js
import { demapify } from 'mapify';

// converts basic maps
let myMap = new Map();
myMap.set('foo', 'bar');
demapify(myMap); // {foo: 'bar'}

// doesn't do anything to non-objects
demapify(2); // 2

// is smart about nested Maps (and Maps in arrays)
let myMap = new Map()
  , myMap2 = new Map();
myMap.set('foo', 'bar');
myMap2.set('baz', 'quux');
myMap.set('inception', myMap2);
demapify(myMap); // {foo: 'bar', inception: {baz: 'quux'}}
```
