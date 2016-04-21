function mapify (obj) {
  let m = new Map();
  if (typeof obj !== 'object' || obj === null) {
    return obj;
  }
  if (obj instanceof Array) {
    let newArr = [];
    for (let x of obj) {
      newArr.push(mapify(x));
    }
    return newArr;
  }
  for (let k in obj) {
    if (obj.hasOwnProperty(k)) {
      m.set(k, mapify(obj[k]));
    }
  };
  return m;
}

function demapify (map) {
  if (map instanceof Array) {
    let newArr = [];
    for (let x of map) {
      newArr.push(demapify(x));
    }
    return newArr;
  } else if (!(map instanceof Map)) {
    return map;
  }
  let obj = {};
  for (let [k, v] of map) {
    obj[k] = demapify(v);
  }
  return obj;
};

let objify = demapify;

export { mapify, demapify, objify };

