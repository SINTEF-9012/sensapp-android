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
package org.sensapp.android.sensappdroid.clientsamples.batterylogger;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public final class AlarmHelper {

	protected static final int REFRESH_RATE = 60; // Polling interval in seconds to start the service.
	
	private static final int ACTIVE_NOTIFICATION_ID = 79290;
	
	private AlarmHelper() {}
	
	protected static void setAlarm(Context context) {
		Intent startService = new Intent(context, BatteryLoggerService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, startService, PendingIntent.FLAG_CANCEL_CURRENT);
		((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), REFRESH_RATE * 1000, pendingIntent);
		
		@SuppressWarnings("deprecation") // We don't want target only API 16...
		Notification notification = new Notification.Builder(context)
			.setContentTitle("BatLog active")
			.setContentText("BatLog active")
		    .setSmallIcon(R.drawable.ic_launcher)
		    .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0))
		    .getNotification();
		notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
		((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(ACTIVE_NOTIFICATION_ID, notification);
	}
	
	protected static void cancelAlarm(Context context) {
		Intent startService = new Intent(context, BatteryLoggerService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, startService, PendingIntent.FLAG_CANCEL_CURRENT);
		((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(pendingIntent);
		
		((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(ACTIVE_NOTIFICATION_ID);
	}
}
