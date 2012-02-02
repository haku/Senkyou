var _jobCount = 0;

function fetchUsername () {
	_fetchAajx('/user', _processUser);
}

function _processUser (xml) {
	var user = $(xml).find('screenname').text();
	$('#user').text(user);
}

function fetchThreadFeed (number) {
	_fetchFeed(number, 'threads', _processThreadFeed);
}

function fetchFeeds () {
	if (_jobCount > 0) {
		console.log('fetchers in progress', _jobCount);
		return;
	}

	_fetchFeed(40, 'home', _processFeed);
	_fetchFeed(10, 'mentions', _processThreadFeed);
}

function fetchTweet (tweetId, childDivId) {
	_fetchFeed(tweetId, 'tweet', _processTweet, childDivId);
}

function _startJob () {
	_jobCount++;
	_updateStatus();
}

function _finishJob (errMsg) {
	_jobCount--;
	_updateStatus(errMsg);
}

function _updateStatus (errMsg) {
	if (errMsg) console.log('errMsg', errMsg);

	var msg;
	if (_jobCount != 0) {
		msg = _jobCount + " running...";
	}
	else {
		msg = "idle.";
	}
	$('#statbar').text(msg);
}

function _fetchFeed (number, feed, procFnc, arg) {
	_fetchAajx('/feeds/' + feed + '?n=' + number, procFnc, arg);
}

function _fetchAajx (url, procFnc, arg) {
	$.ajax({
		type : 'GET',
		cache : 'false',
		url : url,
		dataType : 'xml',
		beforeSend : function () {
			_startJob();
		},
		success : function (xml) {
			try {
				if (arg) {
					procFnc(xml, arg);
				}
				else {
					procFnc(xml);
				}
			}
			catch (e) {
				_updateStatus(e);
			}
			finally {
				_finishJob();
			}
		},
		error : function (e) {
			_finishJob('error: fetching feed: ' + e.message);
		}
	});
}

function _processThreadFeed (xml) {
	var container = $('#threads');
	$(xml).find('tweet').each(function () {
		_insertTweet(container, $(this));
	});
	_sortThreads();
}

function _processFeed (xml) {
	var container = $('#footer');
	$($(xml).find('tweet').get().reverse()).each(function () {
		_insertTweet(container, $(this));
	});
	_sortThreads();
}

function _processTweet (xml, childDivId) {
	var container = $('#threads');
	$(xml).find('tweet').each(function () {
		var tweetDiv = _insertTweet(container, $(this));
		var childDiv = $('#' + childDivId);
		tweetDiv.append(childDiv);
	});
	_sortThreads();
}

function _insertTweet (container, tweetXml) {
	var tweetDivId = _tweetDivId(tweetXml);
	var tweetE = $('#' + tweetDivId);
	var fresh = false;
	if (tweetE.length < 1) {
		fresh = true;
		tweetE = _tweetElement(tweetXml);
	}
	
	var parentDivId = _tweetParentDivId(tweetXml);
	var parentE = parentDivId != null ? $('#' + parentDivId) : null;
	if (parentE != null && parentE.length > 0 && !tweetE.is(parentE.children())) {
		parentE.append(tweetE);
		tweetE.data('replyId', parentDivId);
		_promoteTweet(parentDivId, parentE);
		tweetE.show('slow');
	}
	else if (fresh) {
		container.prepend(tweetE);
		tweetE.show('slow');
		
		if (parentDivId != null) {
			fetchTweet(_tweetParentId(tweetXml), tweetDivId);
		}
	}
	return tweetE;
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

function _sortThreadAlpha (a, b) {
	return $(a).data('sortDate') > $(b).data('sortDate') ? -1 : 1;
};

function _dateThread (headTweet) {
	var retDate = headTweet.data('date');
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
	tweetDiv.attr('id', _tweetDivId(tweetXml));
	tweetDiv.data('date', _tweetDate(tweetXml));
	tweetDiv.append(text);
	return tweetDiv;
}

function _tweetDivId (tweetXml) {
	return 't' + tweetXml.attr('id');
}

function _tweetParentDivId (tweetXml) {
	var id = _tweetParentId(tweetXml);
	if (id == '0') return null;
	return 't' + id;
}

function _tweetParentId (tweetXml) {
	return tweetXml.attr('rid');
}

function _tweetDate (tweetXml) {
	return parseDate(tweetXml.attr('created'));
}
