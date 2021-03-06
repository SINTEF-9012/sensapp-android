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

public class StringValueJsonModel extends ValueJsonModel {
	
	private String sv;
	
	public StringValueJsonModel() {
	}
	
	public StringValueJsonModel(String sv, long t) {
		super(t);
		this.sv = sv;
	}
	
	public String getSv() {
		return sv;
	}
	
	public void setSv(String sv) {
		this.sv = sv;
	}
}
