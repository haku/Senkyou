function getUrlVars () {
	var vars = [], hash;
	var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
	for ( var i = 0; i < hashes.length; i++) {
		hash = hashes[i].split('=');
		vars.push(hash[0]);
		vars[hash[0]] = hash[1];
	}
	return vars;
}

function parseArgToInt (arg, deft, lower, upper) {
	var n = parseInt(getUrlVars()[arg], 10);
	if (!(n)) {
		n = deft;
	}
	else if (n < lower) {
		n = lower;
	}
	else if (n > upper) {
		n = upper;
	}
	return n;
}

function parseDate (dateString) {
	// 2012-01-27T21:47:00Z
	var parts = dateString.match(/\d+/g);
	return new Date(Date.UTC(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], 0));
}
