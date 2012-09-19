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

import org.sensapp.android.sensappdroid.contract.SensAppContract;
import org.sensapp.android.sensappdroid.database.DatabaseException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class Sensor {
	
	private String name;
	private Uri uri;
	private String description;
	private String backend;
	private String template;
	private String unit;
	private boolean uploaded;
	
	public Sensor() {
		name = new String();
		description = new String();
		backend = new String();
		template = new String();
		unit = new String();
		uploaded = false;
	}
	
	public Sensor(String name, Uri uri, String description, String backend, String template, String unit, boolean uploaded) {
		this.name = name;
		this.uri = uri;
		this.description = description;
		this.backend = backend;
		this.template = template;
		this.unit = unit;
		this.uploaded = uploaded;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getBackend() {
		return backend;
	}
	
	public void setBackend(String backend) {
		this.backend = backend;
	}
	
	public String getTemplate() {
		return template;
	}
	
	public void setTemplate(String template) {
		this.template = template;
	}
	
	public String getUnit() {
		return unit;
	}
	
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	public boolean isUploaded() {
		return uploaded;
	}
	
	public void setUploaded(boolean uploaded) {
		this.uploaded = uploaded;
	}
	
	public void toDatabase (Context context) throws DatabaseException {
		String[] projection = {SensAppContract.Sensor.NAME};
		Cursor cursor = context.getContentResolver().query(Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/" + name), projection, null, null, null);
		if (cursor == null) {
			throw new DatabaseException("Null cursor");
		}
		ContentValues cv = new ContentValues();
		cv.put(SensAppContract.Sensor.NAME, name);
		cv.put(SensAppContract.Sensor.URI, uri.toString());
		cv.put(SensAppContract.Sensor.DESCRIPTION, description);
		cv.put(SensAppContract.Sensor.BACKEND, backend);
		cv.put(SensAppContract.Sensor.TEMPLATE, template);
		cv.put(SensAppContract.Sensor.UNIT, unit);
		cv.put(SensAppContract.Sensor.UPLOADED, uploaded ? 1 : 0);
		if (cursor.getCount() == 0) {
			context.getContentResolver().insert(SensAppContract.Sensor.CONTENT_URI, cv);
		} else {
			context.getContentResolver().update(Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/" + name), cv, null, null);
		}
		cursor.close();
	}
}
