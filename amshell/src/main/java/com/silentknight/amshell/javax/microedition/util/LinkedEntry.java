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

package com.silentknight.amshell.javax.microedition.util;

public class LinkedEntry<E>
{
	private LinkedEntry<E> prev;
	private LinkedEntry<E> next;
	
	private E element;
	
//	private static int counter = 0;
//	private int id;
//	
//	public LinkedEntry()
//	{
//		id = ++counter;
//	}
	
	public void setElement(E element)
	{
		this.element = element;
	}
	
	public E getElement()
	{
		return element;
	}
	
	public LinkedEntry<E> prevEntry()
	{
		return prev;
	}
	
	public LinkedEntry<E> nextEntry()
	{
		return next;
	}
	
	public void remove()
	{
		if(prev != null)
		{
			prev.next = next;
		}
		
		if(next != null)
		{
			next.prev = prev;
		}
		
		prev = null;
		next = null;
	}
	
	private void updateLinks()
	{
		if(prev != null)
		{
			prev.next = this;
		}
		
		if(next != null)
		{
			next.prev = this;
		}
	}
	
	public void insertBefore(LinkedEntry<E> entry)
	{
		remove();
		
		prev = entry.prev;
		next = entry;
		
		updateLinks();
	}
	
	public void insertAfter(LinkedEntry<E> entry)
	{
		remove();
		
		prev = entry;
		next = entry.next;
		
		updateLinks();
	}
	
//	public String toString()
//	{
//		StringBuilder buf = new StringBuilder();
//		
//		buf.append("LinkedEntry(");
//		
//		if(prev != null)
//		{
//			buf.append(prev.id);
//		}
//		else
//		{
//			buf.append(-1);
//		}
//		
//		buf.append(", ");
//		
//		buf.append(id);
//		buf.append(", ");
//		
//		if(next != null)
//		{
//			buf.append(next.id);
//		}
//		else
//		{
//			buf.append(-1);
//		}
//		
//		buf.append(", ");
//		buf.append(element);
//		
//		buf.append(")");
//		
//		return buf.toString();
//	}
}