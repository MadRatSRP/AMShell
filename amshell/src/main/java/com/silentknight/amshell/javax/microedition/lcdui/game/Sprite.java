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

package com.silentknight.amshell.javax.microedition.lcdui.game;

import android.graphics.Matrix;

public class Sprite
{
	public static final int TRANS_NONE = 0;
	public static final int TRANS_MIRROR_ROT180 = 1;
	public static final int TRANS_MIRROR = 2;
	public static final int TRANS_ROT180 = 3;
	public static final int TRANS_MIRROR_ROT270 = 4;
	public static final int TRANS_ROT90 = 5;
	public static final int TRANS_ROT270 = 6;
	public static final int TRANS_MIRROR_ROT90 = 7;
	
	/**
	 * Получить матрицу для указанного преобразования.
	 *
	 * @param matrix матрица, в которую нужно добавить преобразование, или null для создания новой матрицы
	 * @param transform преобразование
	 * @param px координаты центра преобразования
	 * @param py координаты центра преобразования
	 * @return матрица с добавленным преобразованием
	 */
	public static Matrix transformMatrix(Matrix matrix, int transform, float px, float py)
	{
		if(matrix == null)
		{
			matrix = new Matrix();
		}
		
		switch(transform)
		{
			case Sprite.TRANS_ROT90:
				matrix.preRotate(90, px, py);
				break;
				
			case Sprite.TRANS_ROT180:
				matrix.preRotate(180, px, py);
				break;
				
			case Sprite.TRANS_ROT270:
				matrix.preRotate(270, px, py);
				break;
				
			case Sprite.TRANS_MIRROR:
				matrix.preScale(-1, 1, px, py);
				break;
				
			case Sprite.TRANS_MIRROR_ROT90:
				matrix.preScale(-1, 1, px, py);
				matrix.preRotate(90, px, py);
				break;
				
			case Sprite.TRANS_MIRROR_ROT180:
				matrix.preScale(-1, 1, px, py);
				matrix.preRotate(180, px, py);
				break;
				
			case Sprite.TRANS_MIRROR_ROT270:
				matrix.preScale(-1, 1, px, py);
				matrix.preRotate(270, px, py);
				break;
		}
		
		return matrix;
	}
}