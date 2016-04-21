"use strict";

var exec = require('child_process').exec,
    path = require('path'),
    fs = require('fs');

var IDevice = function (udid, opts) {
  this.udid = udid || false;
  this.cmd = "ideviceinstaller";

  if (!this._check_cmd()) {
    throw new Error("Could not find " + this.cmd + ". Please install ideviceinstaller using " +
    "Homebrew, e.g. `brew install -g ideviceinstaller`, and ensure it is available " +
    "from PATH.");
  }
};

var wrapForExec = function (s) {
  // not a string
  if (typeof s !== 'string') return s;
  // already wrapped
  if (s.match(/^['"].*['"]$/)) return s;
  // wrap if necessary;
  if (s.match(/[\s"]/)) {
    // escape quote
    s = s.replace(/"/g, '\\"');
    return '"' + s + '"';
  }
  return s;
};

var _prependDirSeparator = function (str) {

  if (str[0] !== "/" && str[0] !== "\\") {
    str = "/" + str;
  }

  return str;
};

IDevice.prototype._check_cmd = function () {
  var found = fs.existsSync(this.cmd);

  if (!found && this.cmd[0] !== '.') {
    var bins = process.env.PATH.split(':');
    var path = _prependDirSeparator(this.cmd);
    for (var i = 0; !found && i < bins.length; i++) {
      found = fs.existsSync(bins[i] + path);
    }
  }

  return found;
};

IDevice.prototype._build_cmd = function (options) {
    var cmd = '';

    cmd += this.cmd;

    if (this.udid) {
	cmd += " -u " + this.udid;
    }

    if (typeof options == 'object' && options.indexOf) {
	for (var i = 0; i < options.length; i++) {
	    cmd += " " + options[i];
	}
    } else {
	cmd += " " + options;
    }

    return cmd;
};

IDevice.prototype.list = function (option, cb) {
    var foption = "-l ";

    if (option) {
	foption += option;
    }

    exec(this._build_cmd(foption), function (err, stdout, stderr) {
	if(err) {
	    cb(err, stdout);
	} else {
	    var apps = stdout.split('\n'),
		res = [];
	    for (var i = 0; i < apps.length; i++) {
               // handle old-style output
		var info = apps[i].split(' - ');
		if (info.length === 2) {
		    res.push({name: info[1], fullname: info[0]});
		}

               // handle new-style output
               info = apps[i].replace(/"/g, "").split(",");
               if (info.length === 3) {
                   var name = info[2].trim() + " " + info[1].trim();
                   res.push({name: name, fullname: info[0]});
               }
	    }
	    cb(null, res);
	}
    });
};

IDevice.prototype.isInstalled = function (appName, cb) {
    var self = this;

    self.listInstalled(function (err, apps) {
	if (err) {
	    cb(err);
	} else {
	  for (var i = 0; i < apps.length; i++) {
		if (apps[i]['name'].indexOf(appName) != -1 ||
		    apps[i]['fullname'].indexOf(appName) != -1) {
		    return cb(null, true);
		}
	    }
	    cb(null, false);
	}
    });
};

IDevice.prototype.listInstalled = function (cb) {
    this.list(null, cb);
};

IDevice.prototype.listSystem = function (cb) {
    this.list("-o list_system", cb);
};

IDevice.prototype.listAll = function (cb) {
    this.list("-o list_all", cb);
};

IDevice.prototype.remove = function (app, cb) {
    exec(this._build_cmd(['-u', app]), function (err, stdout, stderr) {
	if (err) {
	    cb(err, stdout);
	} else {
	    if (stdout.indexOf('Complete') != -1) {
		cb(null);
	    } else {
		cb(new Error('Removing ' + app + ' failed'));
	    }
	}
    });
};

IDevice.prototype.install = function (app, cb) {
    exec(this._build_cmd(['-i', wrapForExec(app)]), function (err, stdout, stderr) {
	if (err) {
	    cb(err, stdout);
	} else {
	    if (stdout.indexOf('Complete') != -1) {
		cb(null);
	    } else {
		cb(new Error('Installing ' + app + ' failed'));
	    }
	}
    });
};

IDevice.prototype.installAndWait = function (ipa, app, cb) {
    var self = this;

    var check = function () {
	self.isInstalled(app, function (err, installed) {
	    if (installed) {
		cb(null, true);
	    } else {
		setTimeout(check, 500);
	    }
	});
    }

    this.install(ipa, function (err) {
	if (err) {
	    cb(null);
	} else {
	    var inter = setTimeout(check, 500)
	}
    })
}


module.exports = function (udid, opts) {
  return new IDevice(udid, opts);
};
