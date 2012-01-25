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
