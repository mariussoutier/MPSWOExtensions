package de.soutier.d2w.components;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.directtoweb.D2W;
import com.webobjects.directtoweb.EditPageInterface;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;

import er.directtoweb.components.ERDCustomEditComponent;

/**
 * Component similar to ERDLinkToEditObject except it displays an HTML list of links to the items
 * in a given relationship.
 * 
 * D2W example:
 * "100 : (pageConfiguration = 'ListMovie' and propertyKey = 'roles') => componentName = 
 * er.modern.movies.demo.components.LinkToEditRelationshipObjectComponent [com.webobjects.directtoweb.Assignment]",
 * 
 * 
 * @binding list Pass a list of EOEnterpriseObjects. Omit this to use the list of the given relationship.
 * @binding sortOrdering EOSortOrdering to sort the list.
 * @binding sortKey Simpler alternative to a sortOrdering, just pass a key to by which to sort the list (ascending).
 * @binding valueWhenEmpty Value to display when the list is empty.
 * @binding displayPropertyKey Display key for each list item, default is userPresentableDescription.
 * 
 * @author Marius Soutier, m.soutier@starhealthcare.info
 */
public class LinkToEditRelationshipObjectComponent extends ERDCustomEditComponent {
	private static final Logger LOG = Logger.getLogger(LinkToEditRelationshipObjectComponent.class);

	private NSArray<EOEnterpriseObject> _list = null;
	public EOEnterpriseObject listItem = null;
	private EOSortOrdering _sortOrdering  = null;
	private EOSortOrdering _sortKey  = null;

	public LinkToEditRelationshipObjectComponent(WOContext context) {
		super(context);
	}

	@Override public boolean isStateless() {return true;};
	@Override public boolean synchronizesVariablesWithBindings() {return false;};

	@Override
	public void reset() {
		super.reset();
		_list = null;
		_sortOrdering = null;
		_sortKey = null;
	}

	public NSArray<EOEnterpriseObject> eoList() {
		if (_list == null) {
			if (hasBinding("list"))
				_list = (NSArray<EOEnterpriseObject>) valueForBinding("list");
			else
				_list = (NSArray<EOEnterpriseObject>) objectPropertyValue();

			if (_list != null) {
				if (sortOrdering() != null)
					_list = EOSortOrdering.sortedArrayUsingKeyOrderArray(_list, new NSArray<EOSortOrdering>(sortOrdering()));
				else if (sortKey() != null)
					_list = EOSortOrdering.sortedArrayUsingKeyOrderArray(_list, new NSArray<EOSortOrdering>(sortKey()));
			}
		}
		return _list;
	}

	public EOSortOrdering sortOrdering() {		
		if (_sortOrdering == null)
			_sortOrdering = (EOSortOrdering) valueForBinding("sortOrdering");
		return _sortOrdering;
	}

	public EOSortOrdering sortKey() {
		if (_sortKey == null) {
			String sortKeyBinding = stringValueForBinding("sortKey");
			if (sortKeyBinding != null && sortKeyBinding.length() > 0)
				_sortKey = new EOSortOrdering(sortKeyBinding, EOSortOrdering.CompareAscending);
		}
		return _sortKey;
	}

	public WOActionResults editAction() {
		final EditPageInterface ipi = D2W.factory().editPageForEntityNamed(listItem.entityName(), session());
		ipi.setObject(listItem);
		ipi.setNextPage(context().page());
		if (LOG.isDebugEnabled())
			LOG.debug("Editing object " + listItem);
		return (WOActionResults) ipi;
	}

	public String valueWhenEmpty() {
		return stringValueForBinding("valueWhenEmpty", "--");
	}

	public String displayPropertyKey() {
		return stringValueForBinding("displayPropertyKey", "userPresentableDescription");
	}

	public String itemDisplayValue() {
		return (String) listItem.valueForKey(displayPropertyKey());
	}
}