var _isFetchHomeFeed = false;

function fetchHomeFeed (user, number) {
	if (_isFetchHomeFeed) {
		console.log('Already fetching home feed.');
		return;
	}
	
	_isFetchHomeFeed = true;
	_fetchFeed(user, number, 'homelast', _processHomeFeed);
}

function fetchThreadFeed (user, number) {
	_fetchFeed(user, number, 'threads', _processThreadFeed);
}

function _fetchFeed (user, number, feed, procFnc) {
	var statbar = $('#statbar');
	$.ajax({
	type : 'GET',
	cache : 'false',
	url : '/feeds/' + feed + '?u=' + user + '&n=' + number,
	dataType : 'xml',
	beforeSend : function () {
		statbar.text('updating...');
	},
	success : function (xml) {
		try {
			procFnc(xml);
			statbar.text('updated.');
		}
		catch (e) {
			console.log('error: processing feed: ' + e.message);
			statbar.text('error: ' + e.message);
		}
	},
	statusCode : {
		304 : function () {
			console.log('error: request blocked by browser cache (HTTP 304).');
			statbar.text('error: request blocked by browser cache.');
		}
	},
	error : function (xhr) {
		console.log('error: fetching feed: ' + e.message);
		statbar.text('error: ' + xhr.statusText);
	}
	});
}

function _processThreadFeed (xml) {
	var container = $('#threads');
	container.html("");
	$(xml).find('tweets').find('tweet').each(function () {
		var tweetXml = $(this);
		var parentXml = tweetXml.parent();
		var tweetE = _tweetElement(tweetXml);
		var parentE = parentXml[0].tagName == 'tweets' ? container : $('#t' + parentXml
				.attr('id'));
		parentE.append(tweetE);
	});
}

function _processHomeFeed (xml) {
	var container = $('#footer');
	container.html("");
	$($(xml).find('tweets').find('tweet').get().reverse()).each(function () {
		var tweetXml = $(this);
		if ($('#' + _tweetId(tweetXml)).length < 1) {
			var tweetE = _tweetElement(tweetXml);
			container.prepend(tweetE);
		}
	});
	_isFetchHomeFeed = false;
}

function _tweetElement (tweetXml) {
	var userSpan = $('<span class="user">').text(tweetXml.attr('user') + ': ');
	var msgSpan = $('<span class="msg">').text(tweetXml.children('body').text());

	var text = $('<p>');
	text.append(userSpan);
	text.append(msgSpan);

	var tweetDiv = $('<div class="tweet">');
	tweetDiv.attr('id', _tweetId(tweetXml));
	tweetDiv.append(text);
	return tweetDiv;
}

function _tweetId (tweetXml) {
	return 't' + tweetXml.attr('id');
}
