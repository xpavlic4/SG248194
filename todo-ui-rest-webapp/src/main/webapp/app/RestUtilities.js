define([
	'dojo/request',
	'exports'
], function (request, exports) {
	var apiUri = 'api/';

	exports.requestResources = function (requestUri) {
		return request.get(apiUri + requestUri, {
			handleAs: 'json'
		});
	};

	return exports;
});