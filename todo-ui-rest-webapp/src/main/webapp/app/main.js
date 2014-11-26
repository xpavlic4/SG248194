require([
	'app/RestUtilities',
	'app/ViewUtilities',
	'dojo/when',
	'dojo/topic',
	'dojo/domReady!'
], function (restUtilities, viewUtilities, when, topic) {
	// Initialize the base interface
	var rootListId = 'root';
	var rootListName = 'Todo Lists';
	
	// Listen for operation requests
	topic.subscribe('createListView', viewUtilities.createListView);

	// Create the home page showing all todo lists
	topic.publish('createListView', rootListId, rootListName, 'lists');
});