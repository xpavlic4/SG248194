define([
	'dojo/when',
	'dojo/topic',
	'dojo/dom-construct',
	'dojo/_base/declare',
	'dojo/_base/array',
	'dojo/date/locale',
	'dijit/registry',
	'app/RestUtilities',
	'dojox/mobile/View',
	'dojox/mobile/Heading',
	'dojox/mobile/RoundRectList',
	'dojox/mobile/ListItem'
], function (when, topic, domConstruct, declare, arrayUtil, locale, registry, restUtilities, View, Heading, RoundRectList, ListItem) {
	return declare(View, {
		listIdPrefix: 'list',
		
		postCreate: function () {
			this._addListContainer();
		},
		
		// Add a list container for holding items
		_addListContainer: function () {
			// Create a list container for the items
			var listContainer = new RoundRectList();
			this.addChild(listContainer);
			this.listContainer = listContainer;
		},

		// Helper function to add and link up a nested todo list
		_addTodoList: function (listContainer, item) {
			var t = this;
			var itemResourceUrl = 'lists/' + item.id + '/items';
			var viewId = t.listIdPrefix + item.id;
			
			var itemLabel = item.name;
			if (item.description) {
				itemLabel += " - " + item.description;
			}
			
			topic.publish('createListView', viewId, item.name, itemResourceUrl, t);
			
			// Add and link the view to the home page
			listContainer.addChild(new ListItem({
				label: itemLabel,
				moveTo: viewId
			}));
		},

		// Helper function to add and link up a nested todo list item
		_addTodoListItem: function (listContainer, item) {
			var icon;
			if (item.isDone) {
				icon = 'mblDomButtonCheckboxOn';
			} else {
				icon = 'mblDomButtonCheckboxOff';
			}

			if (item.dueDate) {
				// Format the date from Unix time (ms) to a readable value
				var convertedDueDate = new Date(item.dueDate);
				var dueDate = locale.format(convertedDueDate, {
					selector: 'date',
					datePattern: 'yyyy-MM-dd'
				});
				
				listContainer.addChild(new ListItem({
					label: item.description,
					rightText: 'Due date: ' + dueDate,
					icon: icon
				}));
			} else {
				listContainer.addChild(new ListItem({
					label: item.description,
					icon: icon
				}));
			}
		},
		
		// Delete all items from this view
		_removeItems: function () {
			if (this.listContainer) {
				var items = this.listContainer.getChildren();
				
				arrayUtil.forEach(items, function (item) {
					// Remove the linked view as well
					if (item.moveTo) {
						registry.byId(item.moveTo).destroyRecursive();
					}
					
					item.destroyRecursive();
				});
			}
		},
		
		// Download the latest list of items from the server and add them to the view		
		_updateItems: function () {
			var t = this;
			
			when(restUtilities.requestResources(t.resourceUrl), function(items) {
				t._removeItems();
				
				// Add each item
				if (items) {
					arrayUtil.forEach(items, function (item) {
						if (item.listId) {
							// Only todo list items will contain a reference to their parent list
							t._addTodoListItem(t.listContainer, item);
						} else {
							t._addTodoList(t.listContainer, item);
						}
					});
				}
			});
		},
		
		onAfterTransitionIn: function () {
			// Update the list of items 
			this._updateItems();
		},
		
		/*
		 * Add a heading to the view
		 */
		addHeading: function (labelText, parentView) {
			var heading = new Heading({
				label: labelText
			});
			
			// If this view is navigated to from another, provide a link button back
			if (parentView) {
				heading.set('moveTo', parentView.get('id'));
				heading.set('back', parentView.heading.get('label'));
			}

			// Create a reference from the view to heading for linking
			this.heading = heading; 
			
			// Add the heading to the view
			domConstruct.place(heading.domNode, this.domNode, 'first');
		}, 
		
		/*
		 * Set the resource URL for updating items
		 */
		setResourceUrl: function (resourceUrl) {
			this.resourceUrl = resourceUrl;
			this._updateItems();
		}
	});
});