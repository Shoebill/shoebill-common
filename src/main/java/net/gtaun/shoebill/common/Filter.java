package net.gtaun.shoebill.common;

public interface Filter<T>
{
	boolean isAcceptable(T t);
}
