/*
 * Copyright 2012 Kulikov Dmitriy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javax.microedition.util;

public class LinkedList<E>
{
	protected ArrayStack<LinkedEntry<E>> pool;
	
	protected final LinkedEntry<E> head;
	protected final LinkedEntry<E> tail;
	
	public LinkedList()
	{
		pool = new ArrayStack();
		
		head = new LinkedEntry();
		tail = new LinkedEntry();
		
		head.insertBefore(tail);
	}
	
	public boolean isEmpty()
	{
		return head.nextEntry() == tail;
	}
	
	public void clear()
	{
		head.remove();
		tail.remove();
		
		head.insertBefore(tail);
	}
	
	public LinkedEntry<E> firstEntry()
	{
		return head.nextEntry();
	}
	
	public LinkedEntry<E> lastEntry()
	{
		return tail.prevEntry();
	}
	
	public E getFirst()
	{
		return head.nextEntry().getElement();
	}
	
	public E setFirst(E element)
	{
		LinkedEntry<E> entry = head.nextEntry();
		
		if(entry != tail)
		{
			E former = entry.getElement();
			entry.setElement(element);
			
			return former;
		}
		else
		{
			addFirst(element);
			return null;
		}
	}
	
	public void addFirst(E element)
	{
		getEntryInstance(element).insertAfter(head);
	}
	
	public E removeFirst()
	{
		LinkedEntry<E> entry = head.nextEntry();
		
		if(entry != tail)
		{
			return recycleEntry(entry);
		}
		else
		{
			return null;
		}
	}
	
	public E getLast()
	{
		return tail.prevEntry().getElement();
	}
	
	public E setLast(E element)
	{
		LinkedEntry<E> entry = tail.prevEntry();
		
		if(entry != head)
		{
			E former = entry.getElement();
			entry.setElement(element);
			
			return former;
		}
		else
		{
			addFirst(element);
			return null;
		}
	}
	
	public void addLast(E element)
	{
		getEntryInstance(element).insertBefore(tail);
	}
	
	public E removeLast()
	{
		LinkedEntry<E> entry = tail.prevEntry();
		
		if(entry != head)
		{
			return recycleEntry(entry);
		}
		else
		{
			return null;
		}
	}
	
	public LinkedEntry<E> getEntryInstance(E element)
	{
		LinkedEntry<E> entry = pool.pop();
		
		if(entry == null)
		{
			entry = new LinkedEntry();
		}
		
		entry.setElement(element);
		
		return entry;
	}
	
	public E recycleEntry(LinkedEntry<E> entry)
	{
		E element = entry.getElement();
		
		entry.remove();
		entry.setElement(null);
		
		pool.push(entry);
		
		return element;
	}
	
//	public void dump(PrintStream ps)
//	{
//		LinkedEntry<E> entry = head;
//		ps.println("Beginning list dump...");
//		
//		while(true)
//		{
//			ps.println(entry);
//			entry = entry.nextEntry();
//			
//			if(entry == tail)
//			{
//				ps.println(entry);
//				break;
//			}
//		}
//	}
}