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

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.game.Sprite;
import javax.microedition.util.ContextHolder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Image
{
	private Bitmap bitmap;
	private Canvas canvas;
	
	public Image(Bitmap bitmap)
	{
		if(bitmap == null)
		{
			throw new NullPointerException();
		}
		
		this.bitmap = bitmap;
	}
	
	public Bitmap getBitmap()
	{
		return bitmap;
	}
	
	public Canvas getCanvas()
	{
		if(canvas == null)
		{
			canvas = new Canvas(bitmap);
		}
		
		return canvas;
	}
	
	public static Image createImage(int width, int height)
	{
		return new Image(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888));
	}
	
	public static Image createImage(int id)
	{
		return new Image(BitmapFactory.decodeResource(ContextHolder.getContext().getResources(), id));
	}
	
	public static Image createImage(String resname) throws IOException
	{
		if(resname.startsWith("/"))
		{
			resname = resname.substring(1);
		}
		
		InputStream is = ContextHolder.getContext().getAssets().open(resname);
		Bitmap bitmap = BitmapFactory.decodeStream(is);
		is.close();
		
		return new Image(bitmap);
	}
	
	public static Image createImage(InputStream stream)
	{
		return new Image(BitmapFactory.decodeStream(stream));
	}
	
	public static Image createImage(byte[] imageData, int imageOffset, int imageLength)
	{
		return new Image(BitmapFactory.decodeByteArray(imageData, imageOffset, imageLength));
	}
	
	public static Image createImage(Image image, int x, int y, int width, int height, int transform)
	{
		return new Image(Bitmap.createBitmap(image.bitmap, x, y, width, height, Sprite.transformMatrix(null, transform, width / 2f, height / 2f), true));
	}
	
	public static Image createRGBImage(int[] rgb, int width, int height, boolean processAlpha)
	{
		return new Image(Bitmap.createBitmap(rgb, width, height, Bitmap.Config.ARGB_8888));
	}
	
	/**
	 * Р¤СѓРЅРєС†РёСЏ РјР°СЃС€С‚Р°Р±РёСЂРѕРІР°РЅРёСЏ РёР·РѕР±СЂР°Р¶РµРЅРёР№.
	 *
	 * Р•СЃР»Рё РѕРґРёРЅ РёР· РєРѕРЅРµС‡РЅС‹С… СЂР°Р·РјРµСЂРѕРІ РјРµРЅСЊС€Рµ 0,
	 * РѕРЅ РІС‹С‡РёСЃР»СЏРµС‚СЃСЏ РЅР° РѕСЃРЅРѕРІРµ РґСЂСѓРіРѕРіРѕ СЃ СЃРѕС…СЂР°РЅРµРЅРёРµРј РїСЂРѕРїРѕСЂС†РёР№.
	 *
	 * Р•СЃР»Рё РѕР±Р° РєРѕРЅРµС‡РЅС‹С… СЂР°Р·РјРµСЂР° РјРµРЅСЊС€Рµ 0,
	 * РёР»Рё РѕРЅРё СЂР°РІРЅС‹ СЂР°Р·РјРµСЂСѓ РёСЃС…РѕРґРЅРѕР№ РєР°СЂС‚РёРЅРєРё,
	 * РІРѕР·РІСЂР°С‰Р°РµС‚СЃСЏ РёСЃС…РѕРґРЅР°СЏ РєР°СЂС‚РёРЅРєР°.
	 *
	 * @param destw РєРѕРЅРµС‡РЅР°СЏ С€РёСЂРёРЅР°
	 * @param desth РєРѕРЅРµС‡РЅР°СЏ РІС‹СЃРѕС‚Р°
	 * @param filter true, РµСЃР»Рё РЅСѓР¶РЅРѕ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ РёРЅС‚РµСЂРїРѕР»СЏС†РёСЋ
	 * @return РјР°СЃС€С‚Р°Р±РёСЂРѕРІР°РЅРЅР°СЏ РєР°СЂС‚РёРЅРєР°
	 */
	public Image scale(int destw, int desth, boolean filter)
	{
		int srcw = getWidth();
		int srch = getHeight();

		if(srcw == destw && srch == desth)
		{
			return this;
		}

		if(destw < 0)
		{
			if(desth < 0)
			{
				return this;
			}
			else
			{
				destw = srcw * desth / srch;
			}
		}
		else if(desth < 0)
		{
			desth = srch * destw / srcw;
		}
		
		return new Image(Bitmap.createScaledBitmap(bitmap, destw, desth, filter));
	}
	
	public Graphics getGraphics()
	{
		return new Graphics(getCanvas());
	}
	
	public boolean isMutable()
	{
		return bitmap.isMutable();
	}
	
	public int getWidth()
	{
		return bitmap.getWidth();
	}
	
	public int getHeight()
	{
		return bitmap.getHeight();
	}
	
	public void getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height)
	{
		bitmap.getPixels(rgbData, offset, scanlength, x, y, width, height);
	}
}