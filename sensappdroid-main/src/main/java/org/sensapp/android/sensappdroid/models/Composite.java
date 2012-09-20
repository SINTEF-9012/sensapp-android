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
package org.sensapp.android.sensappdroid.models;

import java.util.ArrayList;

import android.net.Uri;

public class Composite {
	
	private String name;
	private String description;
	private Uri uri; 
	private ArrayList<Uri> sensors;
	
	public Composite() {
		sensors = new ArrayList<Uri>();
	}
	
	public Composite(String name, String description, Uri uri) {
		this.name = name;
		this.description = description;
		this.uri = uri;
	}
	
	public Composite(String name, String description, Uri uri, ArrayList<Uri> sensors) {
		this.name = name;
		this.description = description;
		this.uri = uri;
		this.sensors = sensors;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Uri getUri() {
		return uri;
	}
	
	public void setUri(Uri uri) {
		this.uri = uri;
	}
	
	public ArrayList<Uri> getSensors() {
		return sensors;
	}
	
	public void setSensors(ArrayList<Uri> sensors) {
		this.sensors = sensors;
	}
}
