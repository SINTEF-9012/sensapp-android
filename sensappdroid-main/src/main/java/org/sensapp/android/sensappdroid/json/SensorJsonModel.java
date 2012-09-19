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

public class SensorJsonModel {
	
	public static class Schema {
		
		private String backend;
		private String template;
		
		public Schema(String backend, String template) {
			this.backend = backend;
			this.template = template;
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
	}
	
	private String id;
	private String descr;
	private Schema schema;
	
	public SensorJsonModel(String id, String descr, Schema schema) {
		this.id = id;
		this.descr = descr;
		this.schema = schema;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public Schema getSchema() {
		return schema;
	}
	public void setSchema(Schema schema) {
		this.schema = schema;
	}
}
