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
package org.sensapp.android.sensappdroid.restrequests;

public class RequestErrorException extends Exception {
	
	public static final int CODE_CONFLICT = 409;
	
	private static final long serialVersionUID = 1L;

	private int code;
	
	public RequestErrorException(String msg) {
		super(msg);
	}
	
	public RequestErrorException(String msg, int code) {
		super(msg);
		this.code = code;
	}
	
	public RequestErrorException(String msg, Exception e) {
		super(msg, e);
	}
	
	public RequestErrorException(String msg, Exception e, int code) {
		super(msg, e);
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
