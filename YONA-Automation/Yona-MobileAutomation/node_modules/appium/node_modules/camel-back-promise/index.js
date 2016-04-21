module.exports = function (deferred, numberOfStraws) {
  if (numberOfStraws <= 0) {
      deferred.resolve(true);
  }
  
  return (function () {
    var straws = 0;
    var maxStraws = numberOfStraws;
    return function (err) {
      if (err) {
        deferred.reject(err);
      }
      straws++;
      if (straws >= maxStraws) {
        deferred.resolve(true);
      }
    };
  })();
}
