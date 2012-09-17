package org.sensapp.android.sensappdroid.test;

import org.sensapp.android.sensappdroid.contract.SensAppContract;

import android.database.Cursor;
import android.test.AndroidTestCase;

public class CompositesTest extends AndroidTestCase {

	private int countTestComposite() {
		Cursor cursor = getContext().getContentResolver().query(SensAppContract.Composite.CONTENT_URI, null, null, null, null);
		assertNotNull(cursor);
		int count  = 0;
		while (cursor.moveToNext()) {
			if (cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Composite.NAME)).startsWith("test")) {
				count ++;
			}
		}
		cursor.close();
		return count;
	}
	
	private int countTestCompose() {
		Cursor cursor = getContext().getContentResolver().query(SensAppContract.Compose.CONTENT_URI, null, null, null, null);
		assertNotNull(cursor);
		int count  = 0;
		while (cursor.moveToNext()) {
			if (cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Compose.COMPOSITE)).startsWith("test")) {
				count ++;
			}
		}
		cursor.close();
		return count;
	}
	
	public void testInsertComposite() {
		int nbComposite = 10;
		assertEquals(0, countTestComposite());
		DataManager.insertComposite(getContext().getContentResolver(), nbComposite);
		assertEquals(nbComposite, countTestComposite());
	}
	
	public void testInsertCompose() {
		int nbCompose = 25;
		assertEquals(0, countTestCompose());
		DataManager.insertCompose(getContext().getContentResolver(), nbCompose, 5, 10);
		assertEquals(nbCompose, countTestCompose());
	}
	
	@Override
	public void tearDown() throws Exception {
		DataManager.cleanAll(getContext());
	}
	
}
