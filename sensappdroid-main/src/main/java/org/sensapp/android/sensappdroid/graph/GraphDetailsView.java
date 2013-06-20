/**
 * Copyright (C) 2012 SINTEF <fabien@fleurey.com>
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June 2007;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sensapp.android.sensappdroid.graph;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import org.sensapp.android.sensappdroid.R;

public class GraphDetailsView extends GraphBaseView {
	
	private Paint paint = new Paint();
	
	public GraphDetailsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray customAttrs = context.obtainStyledAttributes(attrs, R.styleable.graph_details);
		checkAttributes(customAttrs);
	}

	private void checkAttributes(TypedArray customAttrs) {
		getWrapper().setName(customAttrs.getString(R.styleable.graph_details_name));
		getWrapper().setLineNumber(customAttrs.getInteger(R.styleable.graph_details_lines, 0));
	}
	
	@Override
	protected void onMeasure(int h, int w) {
		super.onMeasure(h, w);
		setMeasuredDimension(h, w);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawGrid(canvas, getWrapper().getLineNumber());
		if (getWrapper().printName()) {
			drawName(canvas);
		}
		if (getWrapper().printValue()) {
			drawValue(canvas);
		}
		if (getWrapper().printScale()) {
			drawScale(canvas);
		}
	}
	
	private void drawValue(Canvas canvas) {
		paint.setColor(Color.WHITE);
		paint.setTextSize(25);
		paint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(String.valueOf(getLastValue()), getWidth() / (float) 2, getHeight() / (float) 2, paint);
	}

	private void drawName(Canvas canvas) {
		if (getWrapper().getName() != null) {
			paint.setColor(Color.WHITE);
			paint.setTextSize(25);
			paint.setTextAlign(Paint.Align.CENTER);
			canvas.drawText(getWrapper().getName(), getWidth() / (float) 2, 30, paint);
		}
	}

	private void drawScale(Canvas canvas) {
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.FILL);
		paint.setTextSize(20);
		paint.setTextAlign(Paint.Align.LEFT);
		canvas.drawText(String.valueOf(getWrapper().getHighestVisible()), 0, 20, paint);
		canvas.drawText(String.valueOf(getWrapper().getLowestVisible()), 0, getHeight(), paint);
		paint.setTextAlign(Paint.Align.RIGHT);
		if (!getWrapper().getBuffer().isEmpty()) {
			canvas.drawText(String.valueOf(findHighestValue()), getWidth(), 20, paint);
			canvas.drawText(String.valueOf(findLowestValue()), getWidth(), getHeight(), paint);
		}
	}

	private void drawGrid(Canvas canvas, int lines) {
		if (lines != 0) {
			int linePosition = getHeight() / lines;
			paint.setStrokeWidth(1);
			paint.setColor(Color.WHITE);
			for (int k = 0 ; k < lines - 1 ; k ++) {
				canvas.drawLine(0, linePosition, canvas.getWidth(), linePosition, paint);
				linePosition = linePosition + (getHeight() / lines);
			}
		}
	}
}
