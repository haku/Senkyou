var _fetchCount = 0;

function fetchFeeds (user, number) {
	if (_fetchCount > 0) {
		console.log('fetchers in progress', _fetchCount);
		return;
	}
	
	_fetchCount++;
	_fetchFeed(user, number, 'homelast', _processFeed);
}

function fetchThreadFeed (user, number) {
	_fetchCount++;
	_fetchFeed(user, number, 'threads', _processThreadFeed);
}

function _updateStatus (errMsg) {
	if (errMsg) console.log('errMsg', errMsg);
	
	var msg;
	if (_fetchCount != 0) {
		msg = _fetchCount + " running...";
	}
	else {
		msg = "updated.";
	}
	$('#statbar').text(msg);
}

function _fetchFeed (user, number, feed, procFnc) {
	$.ajax({
	type : 'GET',
	cache : 'false',
	url : '/feeds/' + feed + '?u=' + user + '&n=' + number,
	dataType : 'xml',
	beforeSend : function () {
		_updateStatus();
	},
	success : function (xml) {
		try {
			procFnc(xml);
			_updateStatus();
		}
		catch (e) {
			_updateStatus(e);
		}
	},
	statusCode : {
		304 : function () {
			_updateStatus('error: request blocked by browser cache.');
		}
	},
	error : function (xhr) {
		_updateStatus('error: fetching feed: ' + e.message);
	}
	});
}

function _processThreadFeed (xml) {
	try {
		var container = $('#threads');
		container.html("");
		$(xml).find('tweets').find('tweet').each(function () {
			var tweetXml = $(this);
			var parentXml = tweetXml.parent();
			var tweetE = _tweetElement(tweetXml);
			var parentE = parentXml[0].tagName == 'tweets' ? container : $('#t' + parentXml
					.attr('id'));
			parentE.append(tweetE);
			tweetE.show('slow');
		});
	}
	finally {
		_fetchCount--;
	}
}

function _processFeed (xml) {
	try {
		var container = $('#footer');
		container.html("");
		$($(xml).find('tweets').find('tweet').get().reverse()).each(function () {
			_insertTweet(container, $(this));
		});
	}
	finally {
		_fetchCount--;
	}
}

function _insertTweet (container, tweetXml) {
	if ($('#' + _tweetId(tweetXml)).length < 1) {
		var tweetE = _tweetElement(tweetXml);
		var parentE = $('#' + _tweetRid(tweetXml));
		if (parentE.length > 0) {
			parentE.append(tweetE);
		}
		else {
			container.prepend(tweetE);
		}
		tweetE.show('slow');
	}
}

function _tweetElement (tweetXml) {
	var userSpan = $('<span class="user">').text(tweetXml.attr('user') + ': ');
	var msgSpan = $('<span class="msg">').text(tweetXml.children('body').text());

	var text = $('<p>');
	text.append(userSpan);
	text.append(msgSpan);

	var tweetDiv = $('<div class="tweet" style="display: none">');
	tweetDiv.attr('id', _tweetId(tweetXml));
	tweetDiv.append(text);
	return tweetDiv;
}

function _tweetId (tweetXml) {
	return 't' + tweetXml.attr('id');
}

function _tweetRid (tweetXml) {
	return 't' + tweetXml.attr('rid');
}
