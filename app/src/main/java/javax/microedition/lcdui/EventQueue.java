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

package javax.microedition.lcdui;

import javax.microedition.util.LinkedEntry;
import javax.microedition.util.LinkedList;

public class EventQueue implements Runnable
{
	protected LinkedList<Event> queue;
	protected Event event;
	
	protected boolean enabled;
	protected Thread thread;
	
	protected final Object waiter;
	protected final Object interlock;
	
	protected boolean immediate;
	
	public EventQueue()
	{
		queue = new LinkedList();
		
		waiter = new Object();
		interlock = new Object();
		
		immediate = false;
	}
	
	public void setImmediate(boolean value)
	{
		immediate = value;
	}
	
	public boolean isImmediate()
	{
		return immediate;
	}
	
	public void postEvent(Event event)
	{
//		System.out.println("Post event " + event.getID());
		
		if(immediate)
		{
			event.run();
			return;
		}
		
		boolean empty;
		
		synchronized(queue)
		{
			empty = queue.isEmpty();
			
			if(empty || event.placeableAfter(queue.getLast()))
			{
				queue.addLast(event);
			}
			else
			{
				// аЁтЂ“ађТАађбѕ ађб‡аЁаІађТАађа†ађб¦ађТЛаЁаЉађа…ађТЕађТЕ, ађа…ађб» аЁтЂ“аЁаІађТЕађТБаЁб„аЁа‚аЁтЂ“аЁаіаЁаЅ ађв¦ађб»ађб‡ађб»ађТЛађа…ађб¦аЁтЂ“ађТЕађТЛаЁаЉађа…аЁтЂЙађТЕ ађб‡аЁаІађб»ађа†ађТЕаЁаІађбѕађб¦
				// queue.setLast(event).recycle();
				
				event.recycle(); // аЁтЂ“ађТАађбѕ ађа…ађТАађв¦ађТЕађТЖађа…ађТЕађТЕ
			}
			
//			queue.dump(System.out);
		}
		
		if(empty)
		{
			synchronized(waiter)
			{
				waiter.notifyAll();
			}
		}
	}
	
	public boolean removeEvents(EventFilter filter)
	{
		if(queue.isEmpty())
		{
			return false;
		}
		
		boolean removed = false;
		
		synchronized(queue)
		{
//			System.out.println("removeEvents start");
			
			LinkedEntry<Event> entry = queue.firstEntry();
			LinkedEntry<Event> last = queue.lastEntry();
			LinkedEntry<Event> next;
			
			while(true)
			{
//				queue.dump(System.out);
				
				next = entry.nextEntry();
				
				if(filter.accept(entry.getElement()))
				{
					queue.recycleEntry(entry);
					removed = true;
				}
				
				if(entry != last)
				{
					entry = next;
				}
				else
				{
					break;
				}
			}
			
//			System.out.println("removeEvents end");
		}
		
		return removed;
	}
	
	public boolean isEmpty()
	{
		return queue.isEmpty();
	}
	
	public void clear()
	{
		synchronized(queue)
		{
			queue.clear();
		}
	}
	
	public void startProcessing()
	{
		enabled = true;
		
		if(thread == null)
		{
			thread = new Thread(this);
			thread.start();
		}
	}
	
	public void stopProcessing()
	{
		enabled = false;
		
		synchronized(waiter)
		{
			waiter.notifyAll();
		}
		
		synchronized(interlock)
		{
			thread = null;
		}
	}
	
	public Event currentEvent()
	{
		return event;
	}
	
	public void run()
	{
		synchronized(interlock)
		{
			while(enabled)
			{
				synchronized(this)
				{
					event = queue.getFirst();
				}
				
				if(event != null)
				{
					event.process();
					
					synchronized(queue)
					{
						queue.removeFirst();
						event.recycle();
					}
					
//					System.out.println("Event " + event.getID() + " processed, removed from queue and recycled");
					
					synchronized(this)
					{
						event = null;
						this.notifyAll();
					}
				}
				else
				{
					synchronized(waiter)
					{
						try
						{
							waiter.wait();
						}
						catch(InterruptedException ie)
						{
						}
					}
				}
			}
		}
	}

//	public void uncaughtException(Thread thread, Throwable ex)
//	{
//		System.err.println("Uncaught exception while processing event " + (event != null ? event.getID() : "null"));
//		ex.printStackTrace();
//		
//		System.exit(1);
//	}
}