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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * РљР»Р°СЃСЃ РґР»СЏ СЃСЂР°РІРЅРµРЅРёСЏ СЃС‚СЂРѕРє РЅР° СЃРѕРѕС‚РІРµС‚СЃС‚РІРёРµ С€Р°Р±Р»РѕРЅСѓ.
 *
 * Р’ С€Р°Р±Р»РѕРЅР°С… РІРѕР·РјРѕР¶РЅРѕ РёСЃРїРѕР»СЊР·РѕРІР°РЅРёРµ СЃРїРµС†СЃРёРјРІРѕР»РѕРІ:
 * '*' - Р·Р°РјРµРЅСЏРµС‚ РЅРѕР»СЊ РёР»Рё Р±РѕР»РµРµ Р»СЋР±С‹С… СЃРёРјРІРѕР»РѕРІ,
 * ';' - СЃР»СѓР¶РёС‚ РґР»СЏ СЂР°Р·РґРµР»РµРЅРёСЏ РЅРµСЃРєРѕР»СЊРєРёС… С€Р°Р±Р»РѕРЅРѕРІ.
 *
 * @author SilentKnight
 * @version 2.0
 */
public class StringPattern
{
	public static final char WILDCARD = '*';
	public static final char SEPARATOR = ';';
	
	protected String[] prefix;
	protected String[] suffix;
	protected ArrayList<String>[] middle;

	protected boolean ignorecase;
	protected int count;

	protected int hashcode;
	
	/**
	 * РЎРѕР·РґР°С‚СЊ СЃС‚СЂРѕРєРѕРІС‹Р№ С€Р°Р±Р»РѕРЅ.
	 *
	 * @param pattern СЃРѕР±СЃС‚РІРµРЅРЅРѕ СЃРїРµС†РёС„РёРєР°С‚РѕСЂ С€Р°Р±Р»РѕРЅР°
	 * @param ignorecase true, РµСЃР»Рё РЅРµ РЅСѓР¶РЅРѕ СѓС‡РёС‚С‹РІР°С‚СЊ СЂРµРіРёСЃС‚СЂ РїСЂРё СЃСЂР°РІРЅРµРЅРёРё
	 */
	public StringPattern(String pattern, boolean ignorecase)
	{
		this.ignorecase = ignorecase;
		
		if(ignorecase)
		{
			pattern = pattern.toLowerCase();
		}

		hashcode = pattern.hashCode();
		
		ArrayList<String> patterns = tokenizeString(pattern, SEPARATOR);
		count = patterns.size();
		
		prefix = new String[count];
		suffix = new String[count];
		middle = new ArrayList[count];
		
		for(int i = 0; i < count; i++)
		{
			pattern = patterns.get(i);
			
			int index = pattern.indexOf(WILDCARD);
			
			if(index < 0)
			{
				prefix[i] = pattern;
				pattern = "";
			}
			else
			{
				prefix[i] = pattern.substring(0, index);
				pattern = pattern.substring(index + 1);
			}
			
			index = pattern.lastIndexOf(WILDCARD);
			
			if(index < 0)
			{
				suffix[i] = pattern;
				pattern = "";
			}
			else
			{
				suffix[i] = pattern.substring(index + 1, pattern.length());
				pattern = pattern.substring(0, index);
			}
			
			middle[i] = tokenizeString(pattern, WILDCARD);
		}
	}
	
	/**
	 * РџСЂРѕРІРµСЂРёС‚СЊ СЃС‚СЂРѕРєСѓ РЅР° СЃРѕРѕС‚РІРµСЃС‚РІРёРµ С€Р°Р±Р»РѕРЅСѓ.
	 *
	 * Р•СЃР»Рё С€Р°Р±Р»РѕРЅ СЃРѕСЃС‚РѕРёС‚ РёР· РЅРµСЃРєРѕР»СЊРєРёС… С‡Р°СЃС‚РµР№ (СЂР°Р·РґРµР»РµРЅРЅС‹С… ';'), С‚Рѕ СЃС‚СЂРѕРєР°
	 * РїСЂРѕС…РѕРґРёС‚ РїСЂРѕРІРµСЂРєСѓ, РµСЃР»Рё РѕРЅР° СЃРѕРѕС‚РІРµС‚СЃС‚РІСѓРµС‚ С…РѕС‚СЏ Р±С‹ РѕРґРЅРѕР№ С‡Р°СЃС‚Рё.
	 *
	 * @param str РїСЂРѕРІРµСЂСЏРµРјР°СЏ СЃС‚СЂРѕРєР°
	 * @return true, РµСЃС‚Рё СЃС‚СЂРѕРєР° СЃРѕРѕС‚РІРµС‚СЃС‚РІСѓРµС‚ С€Р°Р±Р»РѕРЅСѓ РёР»Рё РµРіРѕ С‡Р°СЃС‚Рё
	 */
	public boolean matchesWith(String str)
	{
		if(ignorecase)
		{
			str = str.toLowerCase();
		}
		
		for(int i = 0; i < count; i++)
		{
			boolean flag = str.startsWith(prefix[i]);
			
			if(flag)
			{
				str = str.substring(prefix[i].length());
				
				if(str.length() > 0)
				{
					flag = str.endsWith(suffix[i]);
					
					if(flag)
					{
						str = str.substring(0, str.length() - suffix[i].length());
						
						Iterator<String> mid = middle[i].iterator();
						String cmp;
						int index;
						
						while(str.length() > 0 && mid.hasNext())
						{
							cmp = mid.next();
							index = str.indexOf(cmp);
							
							if(index >= 0)
							{
								str = str.substring(index + cmp.length());
							}
							else
							{
								flag = false;
								break;
							}
						}
					}
				}
			}
			
			if(flag)
			{
				return true;
			}
		}
		
		return false;
	}

	public boolean equals(Object obj)
	{
		if(obj == null)
		{
			return false;
		}

		if(getClass() != obj.getClass())
		{
			return false;
		}

		final StringPattern other = (StringPattern)obj;

		if(this.hashcode != other.hashcode)
		{
			return false;
		}
		
		return true;
	}

	public int hashCode()
	{
		return hashcode;
	}
	
	/**
	 * Р Р°Р·Р±РёС‚СЊ СЃС‚СЂРѕРєСѓ РЅР° РїРѕРґСЃС‚СЂРѕРєРё.
	 *
	 * РќРµСЃРєРѕР»СЊРєРѕ РїРѕСЃР»РµРґРѕРІР°С‚РµР»СЊРЅС‹С… СЂР°Р·РґРµР»РёС‚РµР»РµР№ СЃС‡РёС‚Р°СЋС‚СЃСЏ Р·Р° РѕРґРёРЅ.
	 * Р’ СЂРµР·СѓР»СЊС‚Р°С‚ СЂР°Р·Р±РёРµРЅРёСЏ СЂР°Р·РґРµР»РёС‚РµР»Рё РЅРµ РІРєР»СЋС‡Р°СЋСЃСЏ.
	 *
	 * @param str СЂР°Р·РґРµР»СЏРµРјР°СЏ СЃС‚СЂРѕРєР°
	 * @param sep СЃРёРјРІРѕР»-СЂР°Р·РґРµР»РёС‚РµР»СЊ
	 * @return РІРµРєС‚РѕСЂ СЃ СЂРµР·СѓР»СЊС‚Р°С‚Р°РјРё СЂР°Р·Р±РёРµРЅРёСЏ
	 */
	public static ArrayList<String> tokenizeString(String str, char sep)
	{
		ArrayList<String> res = new ArrayList();
		int index;
		
		while(str.length() > 0)
		{
			index = str.indexOf(sep);
			
			if(index > 0)
			{
				res.add(str.substring(0, index));
				str = str.substring(index + 1);
			}
			else if(index == 0)
			{
				str = str.substring(1);
			}
			else
			{
				res.add(str);
				break;
			}
		}
		
		return res;
	}
}
