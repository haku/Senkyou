var _fetchCount = 0;

function fetchFeeds (user, number) {
	if (_fetchCount > 0) {
		console.log('fetchers in progress', _fetchCount);
		return;
	}
	
	_fetchCount++;
	_fetchFeed(user, number, 'home', _processFeed);
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
		msg = "idle.";
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
		$(xml).find('tweet').each(function () {
			_insertTweet(container, $(this));
		});
		_sortThreads();
	}
	finally {
		_fetchCount--;
	}
}

function _processFeed (xml) {
	try {
		var container = $('#footer');
		$($(xml).find('tweet').get().reverse()).each(function () {
			_insertTweet(container, $(this));
		});
		_sortThreads();
	}
	finally {
		_fetchCount--;
	}
}

function _insertTweet (container, tweetXml) {
	var tweetE = $('#' + _tweetId(tweetXml));
	var fresh = false;
	if (tweetE.length < 1) {
		fresh = true;
		tweetE = _tweetElement(tweetXml);
	}
	
	var parentId = _tweetParentId(tweetXml);
	var parentE = $('#' + parentId);
	if (parentE.length > 0 && !tweetE.is(parentE.children())) {
		parentE.append(tweetE);
		tweetE.data('replyId', parentId);
		_promoteTweet(parentId, parentE);
		tweetE.show('slow');
	}
	else if (fresh) {
		container.prepend(tweetE);
		tweetE.show('slow');
	}
}

function _promoteTweet (tweetId, tweetE) {
	if (!(tweetE.data('replyId')) && $('#threads #' + tweetId).length < 1) {
		tweetE.hide('slow', function () {
			$('#threads').prepend(tweetE);
			tweetE.show('slow');
		});
	}
}

function _sortThreads () {
	var threads = $('#threads>.tweet');
	threads.each(function () {
		_dateThread($(this));
	});
	threads.sort(_sortThreadAlpha).appendTo('#threads');
}

function _sortThreadAlpha(a, b){
	return $(a).data('sortDate') > $(b).data('sortDate') ? -1 : 1;
};

function _dateThread(headTweet) {
	var retDate;
	$('.tweet', headTweet).each(function () {
		var date = $(this).data('date');
		if (!(retDate) || date.getTime() > retDate.getTime()) {
			retDate = date;
		}
	});
	headTweet.data('sortDate', retDate.getTime());
	return retDate;
}

function _tweetElement (tweetXml) {
	var userSpan = $('<span class="user">').text(tweetXml.attr('user') + ': ');
	var msgSpan = $('<span class="msg">').text(tweetXml.children('body').text());

	var text = $('<p>');
	text.append(userSpan);
	text.append(msgSpan);

	var tweetDiv = $('<div class="tweet" style="display: none">');
	tweetDiv.attr('id', _tweetId(tweetXml));
	tweetDiv.data('date', _tweetDate(tweetXml));
	tweetDiv.append(text);
	return tweetDiv;
}

function _tweetId (tweetXml) {
	return 't' + tweetXml.attr('id');
}

function _tweetParentId (tweetXml) {
	return 't' + tweetXml.attr('rid');
}

function _tweetDate (tweetXml) {
	return parseDate(tweetXml.attr('created'));
}
