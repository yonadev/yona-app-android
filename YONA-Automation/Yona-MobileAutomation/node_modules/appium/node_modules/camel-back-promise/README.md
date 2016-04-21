## CamelBack Promise

*The straw that breaks the camel's back*

Instantiate by passing in a deferred promise and a number n. After the CamelBackPromise is called n times, the deferred promise is resolved.

`npm install camel-back-promise`

```
var CamelBackPromise = require('camel-back-promise');

var deferred = Q.defer();
deferred.promise.then(function(){
  console.log("broke the camel's back")
});

var camelback = CamelBackPromise(deferred, 3);

camelback()
camelback()
camelback() // "broke the camel's back"
```
