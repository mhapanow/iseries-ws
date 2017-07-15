package com.inodes.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.Transformer;

public class CollectionUtils {

	public static <T> List<T> reverse (List<T> original) {
		List<T> reversedList = new ArrayList<T>(original);
		Collections.reverse(reversedList);
		return reversedList;
	}

	@SuppressWarnings({"rawtypes","unchecked"})
	public static Collection<?> transform (Collection<?> original, Transformer transformer) {
		Collection<?> transformedCollection = new ArrayList(original);
		org.apache.commons.collections.CollectionUtils.transform(transformedCollection, transformer);
		return transformedCollection;
	}

	public static <T> List<T> order (Collection<T> original, Comparator<T> comparator) {
		List<T> orderedCollection = CollectionFactory.createList();
		orderedCollection.addAll(original);
		Collections.sort(orderedCollection, comparator);
		return orderedCollection;
	}
	
	public static boolean containsAny(Collection<?> origin, Collection<?> find) {
		if( find.size() == 0 ) return true;
		Iterator<?> i = find.iterator();
		while(i.hasNext()) {
			Object o = i.next();
			if( origin.contains(o)) return true;
		}
		return false;
	}
}
