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
package org.sensapp.android.sensappdroid.api;

public enum SensAppUnit {
	
	METER("m"),
	KILOGRAM("kg"),
	SECOND("s"),
	AMPERE("A"),
	KELVIN("K"),
	PERCENT("%"),
	CANDELA("cd"),
	MOL("mole"),
	HERTZ("Hz"),
	RADIAN("rad"),
	STERADIANT("sr"),
	NEWTON("N"),
	PASCAL("Pa"),
	JOULE("J"),
	WATT("W"),
	COULOMB("C"),
	VOLT("V"), 
	FARAD("F"),
	OHM("Ohm"),
	SIEMENS("S"),
	WEBER("Wb"),
	TESLA("T"),
	HENRY("H"),
	DEGREES_CELSIUS("degC"),
	LUMEN("lm"),
	LUX("lx"),
	BECQUEREL("Bq"),
	GRAY("Gy"),
	SIEVERT("Sv"),
	KATAL("kat"),
	PH_ACIDITY("pH"),
	COUNTER_VALUE("count"),
	RELATIVE_HUMIDITY("%RH"),
	AREA("m2"),
	VOLUME_LITER("l"),
	VELOCITY("m/s"),
	ACCELERATION("m/s2"),
	FLOW_RATE("l/s"),
	IRRADIANCE("W/m2"),
	LUMINANCE("cd/m2"),
	BEL_SOUND_PRESSURE_LEVEL("Bspl"),
	BITS_SECOND("bit/s"),
	LATITUDE("lat"),
	LONGITUDE("lon"),
	BATTERY_LEVEL("%EL");
	
	private String iANAUnit;
	
	private SensAppUnit(String iANAUnit) {
		this.iANAUnit = iANAUnit;
	}

	public String getIANAUnit() {
		return iANAUnit;
	}
	
}
