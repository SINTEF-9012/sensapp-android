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
package org.sensapp.android.sensappdroid.json;

public class NumericalValueJsonModel extends ValueJsonModel {
	
	private float v;
	
	public NumericalValueJsonModel() {
		super();
	}
	
	public NumericalValueJsonModel(float v, long t) {
		super(t);
		this.v = v;
	}
	
	public float getV() {
		return v;
	}
	
	public void setV(int v) {
		this.v = v;
	}
}
