function fetchThreadFeed (user) {
	var statbar = $('#statbar');
	$.ajax({
	type : 'GET',
	cache : 'false',
	url : '/feeds/threads?u=' + user + '&n=5',
	dataType : 'xml',
	beforeSend : function () {
		statbar.text('updating...');
	},
	success : function (xml) {
		try {
			_processThreadFeed(xml);
			statbar.text('updated ' + new Date());
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

	var xmlDoc = $(xml);
	xmlDoc.find('tweets').children('tweet').each(function () { // For each head tweet.
		var head = $(this);

		var tweet = $('<div>');
		var text = $('<p>').text(head.attr('user') + ': ' + head.children('body').text());
		tweet.append(text);
		threadContainer.append(tweet);
	});
}