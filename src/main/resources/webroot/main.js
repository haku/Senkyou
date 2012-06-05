var _jobCount = 0;

var pStatusBar;
var divThreads;
var divFooter;

function initMain () {
	pStatusBar = $('#statbar');
	divThreads = $('#threads');
	divFooter = $('#footer');
	_initComposeDlg();
}

function _showPromptSignin () {
	if ($('#signin').length < 1) {
		divThreads.prepend($('<div class="menu-holder">')
				.append($('<div id="signin" class="menu-box">').append($('<p>')
						.append($('<a href="/signin">').text("sign in")))));
	}
}

function fetchUsername () {
	_fetchAajx('/user', _processUser);
}

function _processUser (xml) {
	var user = $(xml).find('user').attr('screenname');
	$('#user').text(user);
	document.title = user + " in faeryland";
}

function fetchFeeds () {
	if (_jobCount > 0) {
		console.log('fetchers in progress', _jobCount);
		return;
	}

	_fetchAajx('/feeds/home', _processFeed);
	_fetchAajx('/feeds/threads', _processThreadFeed);
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
		msg = Array(_jobCount + 1).join('+');
	}
	else {
		msg = '';
	}
	pStatusBar.text(msg);
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
	statusCode : {
		401 : function () {
			_showPromptSignin();
		}
	},
	error : function (e) {
		_finishJob('error: fetching feed: ' + e.message);
	}
	});
}

function _processThreadFeed (xml) {
	$(xml).find('tweet').sort(_tweetSortDateAscending).each(function () {
		_insertTweet($(this), _addThread);
	});
}

function _tweetSortDateAscending (a, b) {
	return _tweetDate($(a)) > _tweetDate($(b)) ? 1 : -1;
};

function _processFeed (xml) {
	$($(xml).find('tweet').get().reverse()).each(function () {
		_insertTweet($(this), _addFish);
	});
}

function _insertTweet (tweetXml, addFnc) {
	var tweetE = _existingTweetElement(_tweetId(tweetXml));
	var fresh = tweetE ? false : true;
	if (fresh) tweetE = _newTweetElement(tweetXml);

	var parentId = _tweetParentId(tweetXml);
	var parentE = parentId != null ? _existingTweetElement(parentId) : null;
	if (parentE != null && parentE.length > 0 && !tweetE.is(parentE.children())) {
		parentE.append(tweetE);
		tweetE.data('replyId', parentId);
		_promoteTweet(parentId, parentE);
		_scheduleReveal(tweetE);
	}
	else if (fresh) {
		addFnc(tweetE);
		tweetE.stop();
		_scheduleReveal(tweetE);
	}
	return tweetE;
}

function _addThread (tweetE) { // Add to thread area.
	tweetE.data('inThread', true);
	divThreads.prepend(tweetE);
}

function _addFish (tweetE) { // Add to footer / sea area.
	divFooter.prepend(tweetE);
}

function _promoteTweet (tweetId, tweetE) {
	if (!(tweetE.data('replyId')) && !(tweetE.data('inThread'))) {
		tweetE.hide('slow', function () {
			_addThread(tweetE);
			tweetE.show('slow', _layoutThreads);
		});
	}
}

var _toRevealQueue = [];

function _scheduleReveal (element) {
	_toRevealQueue.push(element);
	_reveal();
}

var _revealWaiting = false;
function _reveal () {
	if (_revealWaiting) return;
	_revealWaiting = true;
	setTimeout(function () {
		_doReveal();
		_revealWaiting = false;
	}, 2);
}

function _doReveal () {
	var e;
	while (e = _toRevealQueue.pop()) {
		e.show('slow', _layoutThreads);
	}
}

var _layoutThreadsWaiting = false;
function _layoutThreads () {
	if (_layoutThreadsWaiting) return;
	_layoutThreadsWaiting = true;
	setTimeout(function () {
		_doLayoutThreads();
		_layoutThreadsWaiting = false;
	}, 2);
}

function _doLayoutThreads () {
	// Only fire masonry when we have finished other animations.
	// Lowest count gets in 1, not 0.
	if (jQuery.fx.off = false && $(".tweet:animated").length !== 1) return;

	$('#threads .tweet').each(function () {
		_sortThread($(this));
	});
	_sortThreads();

	if (!getUrlVars()['masonry']) return;
	$(function () { // TODO Is this needed every time?
		divThreads.masonry({
			itemSelector : '#threads>.tweet'
		});
	});
	divThreads.masonry('reload');
}

function _sortThreads () {
	var threads = $('#threads>.tweet');
	threads.each(function () {
		_dateThread($(this));
	});
	threads.sort(_sortThreadsAlpha).appendTo('#threads');
}

function _sortThreadsAlpha (a, b) {
	return $(a).data('sortDate') > $(b).data('sortDate') ? -1 : 1;
};

// Sort a tweet's children.
function _sortThread (tweetE) {
	tweetE.children('.tweet').sort(_sortChildrenAlpha).appendTo(tweetE);
}

function _sortChildrenAlpha (a, b) {
	return $(a).data('date') > $(b).data('date') ? 1 : -1;
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

var _allTweetElements = {};

function _existingTweetElement (tweetId) {
	return _allTweetElements[tweetId];
}

function _newTweetElement (tweetXml) {
	var tweetId = _tweetDivId(tweetXml);
	var userName = tweetXml.attr('user');
	var msgBody = tweetXml.children('body').text();

	var userSpan = $('<span class="user">').text(userName + ': ');
	var msgSpan = $('<span class="msg">').text(msgBody);

	var link = $('<a href="#">');
	link.append(userSpan);
	link.append(msgSpan);
	link.click(function (event) {
		event.preventDefault();
		_tweetClicked(tweetId, userName, msgBody);
	});

	var text = $('<p>');
	text.append(link);

	var tweetDiv = $('<div class="tweet" style="display: none">');
	tweetDiv.attr('id', tweetId);
	tweetDiv.data('date', _tweetDate(tweetXml));
	tweetDiv.append(text);

	_allTweetElements[_tweetId(tweetXml)] = tweetDiv;
	return tweetDiv;
}

function _tweetDivId (tweetXml) {
	return 't' + _tweetId(tweetXml);
}

function _tweetId (tweetXml) {
	return tweetXml.attr('id');
}

function _tweetParentDivId (tweetXml) {
	var id = _tweetParentId(tweetXml);
	if (id == null) return null;
	return 't' + id;
}

function _tweetParentId (tweetXml) {
	var id = tweetXml.attr('rid');
	if (id == '0') return null;
	return id;
}

function _tweetDate (tweetXml) {
	return parseDate(tweetXml.attr('created'));
}

var dlg_compose;

function _initComposeDlg () {
	dlg_compose = $('#dlgcompose');
	dlg_compose.dialog({
		autoOpen: false,
		width: 700,
		height: 350,
		buttons: {
			"Tweet": function () {
				var bodyTextbox = $('.tweetbody', dlg_compose);
				var tweetBody = bodyTextbox.val();
				var replyTo = dlg_compose.dlg_compose('option', 'replyId');
				$.post("tweet", {replyTo: replyTo, tweetBody: tweetBody}, function () {
					dlg_compose.dialog('close');
					bodyTextbox.val('');
				});
				// TODO error handling?
			},
			"Cancel": function () {
				$(this).dialog('close');
			}
		}
	});
}

function _tweetClicked (tweetId, userName, msgBody) {
	var tweet = $('<p>');
	tweet.text(msgBody);

	var caption = $('.caption', dlg_compose).first();
	caption.empty();
	caption.append(tweet);

	$('.tweetbody', dlg_compose).val('@' + userName + ' ');

	dlg_compose.dialog('option', 'title', 'Reply to ' + userName);
	dlg_compose.dialog('option', 'replyId', tweetId);
	dlg_compose.dialog('open');
}
