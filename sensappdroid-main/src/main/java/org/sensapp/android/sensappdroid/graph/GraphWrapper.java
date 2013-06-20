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

import android.graphics.Color;

public class GraphWrapper {
	
	private GraphBuffer graphBuffer;
	private int color = Color.RED;
	private long sleepTime = 1000;
	private int drawGraphType = GraphBaseView.LINECHART;
	private float lowestVisible = 0;
	private float highestVisible = 1023;
	private String name;
    private long ID;
	private boolean printName = false;
	private boolean printValue = false;
	private boolean printScale = false;
	private int lineNumber = 0;
	
	public GraphWrapper() {
		this.graphBuffer = new GraphBuffer();
	}
	
	public GraphWrapper(long ID, GraphBuffer graphBuffer) {
		this.ID = ID;
        this.graphBuffer = graphBuffer;
        this.lowestVisible = graphBuffer.getMinValue();
        this.highestVisible = graphBuffer.getMaxValue();
	}
	
	public GraphBuffer getBuffer() {
		return graphBuffer;
	}
	
	public void setBuffer(GraphBuffer gb) {
		this.graphBuffer = gb;
	}

	public void setGraphOptions(int color, long sleepTime, int drawGraphType, String name) {
		this.color = color;
		this.sleepTime = sleepTime;
		this.drawGraphType = drawGraphType;
		this.name = name;
	}

	public void setPrinterParameters(boolean printName, boolean printValue, boolean printScale) {
		this.printName = printName;
		this.printValue = printValue;
		this.printScale = printScale;
	}
	
	public boolean printName() {
		return printName;
	}
	
	public boolean printScale() {
		return printScale;
	}
	
	public boolean printValue() {
		return printValue;
	}
	
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public int getLineNumber() {
		return lineNumber;
	}
	
	public void setColor(int color) {
		this.color = color;
	}


	public int getColor() {
		return color;
	}


	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}


	public long getSleepTime() {
		return sleepTime;
	}


	public void setDrawGraphType(int drawGraphType) {
		this.drawGraphType = drawGraphType;
	}


	public int getDrawGraphType() {
		return drawGraphType;
	}


	public void setLowestVisible(int lowestVisible) {
		this.lowestVisible = lowestVisible;
	}


	public float getLowestVisible() {
		return lowestVisible;
	}


	public void setHighestVisible(int highestVisible) {
		this.highestVisible = highestVisible;
	}


	public float getHighestVisible() {
		return highestVisible;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}

    public long getID(){
        return ID;
    }
}
