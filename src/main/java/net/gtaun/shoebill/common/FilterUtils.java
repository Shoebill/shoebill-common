package net.gtaun.shoebill.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FilterUtils
{
	public static <T> List<T> filter(Collection<T> src, Filter<T> filter)
	{
		return filter(new ArrayList<T>(), src, filter);
	}
	
	public static <C extends Collection<T>, T> C filter(C collection, Collection<T> src, Filter<T> filter)
	{
		for (T obj : src) if (filter.isAcceptable(obj)) collection.add(obj);
		return collection;
	}
	
	private FilterUtils()
	{

	}
}
