package org.sensapp.android.sensappdroid.api;

public enum SensAppUnit {
	
	meter("m"),
	kilogram("kg"),
	second("s"),
	ampere("A"),
	kelvin("K"),
	percent("%");
	
//	|     cd | candela                                      | RFC-AAAA  |
//	   |    mol | mole                                         | RFC-AAAA  |
//	   |     Hz | hertz                                        | RFC-AAAA  |
//	   |    rad | radian                                       | RFC-AAAA  |
//	   |     sr | steradian                                    | RFC-AAAA  |
//	   |      N | newton                                       | RFC-AAAA  |
//	   |     Pa | pascal                                       | RFC-AAAA  |
//	   |      J | joule                                        | RFC-AAAA  |
//	   |      W | watt                                         | RFC-AAAA  |
//	   |      C | coulomb                                      | RFC-AAAA  |
//	   |      V | volt                                         | RFC-AAAA  |
//	   |      F | farad                                        | RFC-AAAA  |
//	   |    Ohm | ohm                                          | RFC-AAAA  |
//	   |      S | siemens                                      | RFC-AAAA  |
//	   |     Wb | weber                                        | RFC-AAAA  |
//	   |      T | tesla                                        | RFC-AAAA  |
//	   |      H | henry                                        | RFC-AAAA  |
//	   |   degC | degrees Celsius                              | RFC-AAAA  |
//	   |     lm | lumen                                        | RFC-AAAA  |
//	   |     lx | lux                                          | RFC-AAAA  |
//	   |     Bq | becquerel                                    | RFC-AAAA  |
//	   |     Gy | gray                                         | RFC-AAAA  |
//	   |     Sv | sievert                                      | RFC-AAAA  |
//	   |    kat | katal                                        | RFC-AAAA  |
//	   |     pH | pH acidity                                   | RFC-AAAA  |
//	   |  count | counter value                                | RFC-AAAA  |
//	 |    %RH | Relative Humidity                            | RFC-AAAA  |
//	   |     m2 | area                                         | RFC-AAAA  |
//	   |      l | volume in liters                             | RFC-AAAA  |
//	   |    m/s | velocity                                     | RFC-AAAA  |
//	   |   m/s2 | acceleration                                 | RFC-AAAA  |
//	   |    l/s | flow rate in liters per second               | RFC-AAAA  |
//	   |   W/m2 | irradiance                                   | RFC-AAAA  |
//	   |  cd/m2 | luminance                                    | RFC-AAAA  |
//	   |   Bspl | bel sound pressure level                     | RFC-AAAA  |
//	   |  bit/s | bits per second                              | RFC-AAAA  |
//	   |    lat | degrees latitude. Assumed to be in WGS84     | RFC-AAAA  |
//	   |        | unless another reference frame is known for  |           |
//	   |        | the sensor.                                  |           |
//	   |    lon | degrees longitude. Assumed to be in WGS84    | RFC-AAAA  |
//	   |        | unless another reference frame is known for  |           |
//	   |        | the sensor.                                  |           |
//	   |    %EL | remaining battery energy level in percents   | RFC-AAAA  |
	
	private String iANAUnit;
	
	private SensAppUnit(String iANAUnit) {
		this.iANAUnit = iANAUnit;
	}

	public String getIANAUnit() {
		return iANAUnit;
	}
	
}
