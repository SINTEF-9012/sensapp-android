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
package org.sensapp.android.sensappdroid.preferences;

import org.sensapp.android.sensappdroid.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.util.Patterns;
import android.webkit.URLUtil;

public class EditTextServerPreference extends EditTextPreference {

	private String lastValid;
	
	public EditTextServerPreference(Context context) {
		super(context);
		lastValid = context.getString(R.string.pref_server_default_value);
	}

	public EditTextServerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		lastValid = context.getString(R.string.pref_server_default_value);
	}

	public EditTextServerPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		lastValid = context.getString(R.string.pref_server_default_value);
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult) {
			String address = this.getText();
			boolean valid = true;
			String errorMsg = "";
			if (!Patterns.WEB_URL.matcher(address).matches()) {
				valid = false;
				errorMsg += "The server url is not a valid web address. Check for unexpected spaces or characters.";
			} else if (!URLUtil.isValidUrl(address)) {
				address = "http://".concat(address);
			} else if (!URLUtil.isHttpUrl(address)) {
				valid = false;
				errorMsg += "The server url is not a HTTP one. At this time only this protocol is supported.";
			}
			if (!valid) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
				builder.setMessage(errorMsg)
				.setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						showDialog(null);
					}
				}).create().show();
			} else {
				this.setText(address);
				lastValid = address;
			}
		} else {
			this.setText(lastValid);
		}
	}
}
