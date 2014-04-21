package utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import statics.Configurations;

public class DatetimeHelper {
	public static final String formatDatetimeString(String datetimeString) {
		SimpleDateFormat sdf = new SimpleDateFormat(Configurations.DATETIME_FORMAT);
		sdf.setLenient(false);
		try {
			Date datetime = sdf.parse(datetimeString);
			return sdf.format(datetime);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static final String getNowDatetimeString() {
		SimpleDateFormat sdf = new SimpleDateFormat(Configurations.DATETIME_FORMAT);
		Date now = new Date();
		return sdf.format(now);
	}
}
