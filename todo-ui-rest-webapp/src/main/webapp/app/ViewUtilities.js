define([
	'dojo/_base/declare',
	'dojo/dom-construct',
	'app/ListView',
	'exports'
], function (declare, domConstruct, ListView, exports) {
	var rootViewContainerId = 'view-container';

	exports.createListView = function (id, label, resourceUrl, parentView) {
		var viewNode = domConstruct.create('div', {
			id: id // ID to uniquely identify the view for linking
		});
		domConstruct.place(viewNode, rootViewContainerId, 'last');
		
		var todoListView = new ListView(null, viewNode);

		if (parentView) {
			todoListView.addHeading(label, parentView);
		} else {
			todoListView.addHeading(label);
		}
		
		todoListView.setResourceUrl(resourceUrl);
		todoListView.startup();
		
		return todoListView;
	};

	return exports;
});