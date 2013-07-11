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
import android.view.View;
import org.sensapp.android.sensappdroid.R;

public class GraphBaseView extends View {

	public interface GraphListenner {
		public void lastValueChanged(float value);
	}
	
	public static final int BARCHART = 0;
	public static final int LINECHART = 1;

	private GraphListenner listenner;
	private GraphWrapper wrapper = new GraphWrapper();
	private float lastValue;
	private int bottomOffset = 0;
	private int topOffset = 0;

	private Paint paint = new Paint();
	private PaintManager paintManager = new PaintManager();
	private float[] graphValues;
	
	public GraphBaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray customAttrs = context.obtainStyledAttributes(attrs, R.styleable.graph_base);
		checkAttributes(customAttrs);
		paintManager.start();
	}

	private void checkAttributes(TypedArray customAttrs) {
		wrapper.setColor(customAttrs.getInteger(R.styleable.graph_base_color, Color.RED));
		wrapper.setLowestVisible(customAttrs.getInteger(R.styleable.graph_base_minimum, 0));
		wrapper.setHighestVisible(customAttrs.getInteger(R.styleable.graph_base_maximum, 500));
		wrapper.setSleepTime(customAttrs.getInteger(R.styleable.graph_base_refresh, 1000));
		wrapper.setDrawGraphType(customAttrs.getInteger(R.styleable.graph_base_style, LINECHART));
	}

	public void registerListenner(GraphListenner listenner) {
		this.listenner = listenner;
	}
	
	public void unregisterListenner(GraphListenner listenner) {
		if (this.listenner == listenner) {
			this.listenner = null;
		}
	}
	
	public void registerWrapper(GraphWrapper wrapper) {
		this.wrapper = wrapper;
	}
	
	public GraphWrapper getWrapper() {
		return wrapper;
	}
	
	public float getLastValue() {
		return lastValue;
	}
	
	@Override
	protected void onMeasure(int h, int w){
		super.onMeasure(w, h);
		setMeasuredDimension(h, w);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (graphValues != null) {
			paint.setColor(wrapper.getColor());
			switch (wrapper.getDrawGraphType()) {
			case BARCHART:
				drawBarGraph(canvas);
				break;
			case LINECHART:
				drawLineGraph(canvas);
				break;
			default:
				break;
			}
		}
	}
	
	private void drawBarGraph(Canvas c) {
		paint.setStrokeWidth((float) computeX(1) + 1);
		for (int i = 0 ; i < graphValues.length ; i++) {
			if (graphValues[i] == wrapper.getBuffer().getInvalidNumber()) {
				break;
			} else {
				c.drawLine(computeX(i), computeY(0), computeX(i), computeY(graphValues[i]), paint);
			}
		}
	}
	
	private void drawLineGraph(Canvas c) {
		float lastY = 0;
		paint.setStrokeWidth(2);
		for (int i = 0 ; i < graphValues.length ; i ++) {
			if (graphValues[i] == wrapper.getBuffer().getInvalidNumber()) {
				break;
			} else {
				float y = computeY(graphValues[i]);
				if (i == 0) {
					c.drawLine(computeX(i), y, computeX(i), y, paint);
				} else {
					c.drawLine(computeX(i-1), lastY, computeX(i), y, paint);
				}
				lastY =  y;
			}
		}
	}
	
	protected float computeX(float value) {
        return value * getWidth() / graphValues.length;
    }

    protected float computeY(float value) {
       return getHeight() - bottomOffset - map(value, wrapper.getLowestVisible(), wrapper.getHighestVisible(), bottomOffset, getHeight() - topOffset);
    }

	protected float findHighestValue() {
		if (graphValues == null) {
			return 0;
		}
		float max = Integer.MIN_VALUE;
		for (int i = 0 ; i < graphValues.length ; i++){
			if(graphValues[i] > max && graphValues[i] != wrapper.getBuffer().getInvalidNumber()){
				max = graphValues[i];
			}
		}
		if (max == Integer.MIN_VALUE) {
			return 0;
		}
		return max;
	}

	protected float findLowestValue() {
		if (graphValues == null) {
			return 0;
		}
		float min = Integer.MAX_VALUE;
		for (int i = 0; i < graphValues.length; i++){
			if(graphValues[i] < min && graphValues[i] != wrapper.getBuffer().getInvalidNumber()){
				min = graphValues[i];
			}
		}
		if (min == Integer.MAX_VALUE) {
			return 0;
		}
		return min;
	}

	private float map(float x, float in_min, float in_max, float out_min, float out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
	
	private class PaintManager extends Thread {
		public void run() {
			while (true) {
				if (wrapper.getBuffer() != null) {
					graphValues = wrapper.getBuffer().getGraphData();
					lastValue = wrapper.getBuffer().getLastValue();
					if (listenner != null) {
						listenner.lastValueChanged(lastValue);
					}
					postInvalidate();
				}
				try {
					sleep(wrapper.getSleepTime());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}