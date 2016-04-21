var os = require('os');

module.exports = {
  isWindows: function() {
    return os.type() == 'Windows_NT'
  },

  isLinux: function() {
    return os.type() == 'Linux'
  },

  isMac: function() {
    return os.type() == 'Darwin'
  },

  is64bit: function() {
    return os.arch() == 'x64' || os.arch == 'ia64'
  },

  is32bit: function() {
    return os.arch() == 'x32' || os.arch == 'ia32'
  }
}
