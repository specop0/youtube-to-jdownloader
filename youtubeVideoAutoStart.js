var myPackagizerLink = link;

var hostId = crawledLink.getHost();

if (!String.prototype.includes) {
  String.prototype.includes = function(search, start) {
    'use strict';

    if (search instanceof RegExp) {
      throw TypeError('first argument must not be a RegExp');
    }
    if (start === undefined) { start = 0; }
    return this.indexOf(search, start) !== -1;
  };
}

if (hostId === "youtube.com") {
    var map = crawledLink.getProperties()
    var downloadType = crawledLink.getProperty("YT_COLLECTION")
    if (downloadType.includes("VIDEO"))
    {
        // 60fps variant is blacklisted via plug-in
        var downloadVariants = JSON.parse(crawledLink.getProperty("YT_VARIANTS"))

        // add timestamp prefix
        var now = new Date();
        var month = now.getMonth() + 1
        if (month < 10) {
            month = "0" + month
        }
        var day = now.getDate()
        if (day < 10) {
            day = "0" + day
        }
        var timestamp = now.getFullYear() + "-" + month + "-" + day
        crawledLink.setName(timestamp + "_" + crawledLink.getName());

        myPackagizerLink.setAutoConfirmEnabled(true)
        myPackagizerLink.setAutoStartEnabled(true);
        myPackagizerLink.setEnabled(true);
    }
}