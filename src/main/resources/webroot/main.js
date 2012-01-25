function fetchThreadFeed (user, number) {
	var statbar = $('#statbar');
	$.ajax({
	type : 'GET',
	cache : 'false',
	url : '/feeds/threads?u=' + user + '&n=' + number,
	dataType : 'xml',
	beforeSend : function () {
		statbar.text('updating...');
	},
	success : function (xml) {
		try {
			_processThreadFeed(xml);
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
	var threadContainer = $('#threads');
	threadContainer.html("");
	$(xml).find('tweets').find('tweet').each(function () {
		var tweetXml = $(this);
		var parentXml = tweetXml.parent();
		var tweetE = tweetElement(tweetXml);
		var parentE = parentXml[0].tagName == 'tweets' ? threadContainer : $('#t' + parentXml.attr('id'));
		parentE.append(tweetE);
	})
}

function tweetElement (tweetXml) {
	var userSpan = $('<span class="user">').text(tweetXml.attr('user') + ': ');
	var msgSpan = $('<span class="msg">').text(tweetXml.children('body').text());
	
	var text = $('<p>');
	text.append(userSpan);
	text.append(msgSpan);
	
	var tweetDiv = $('<div class="tweet">');
	tweetDiv.attr('id', 't' + tweetXml.attr('id'));
	tweetDiv.append(text);
	return tweetDiv;
}
