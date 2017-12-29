package l2f.gameserver.utils;

import java.util.LinkedList;

public class MaxElementsList<E> extends LinkedList<E>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3464131451892328698L;
	private final int MAX;
	public MaxElementsList(int maxElements)
	{
		MAX = maxElements;
	}
	/**
	 * If size will reach MAX, removing first Element and adding new as Last
	 */
	@Override
	public boolean add(E e)
	{
		if (size() + 1 > MAX)
			removeFirst();
		super.addLast(e);
		return true;
	}
}
