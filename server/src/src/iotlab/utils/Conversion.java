/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.utils;

import iotlab.core.beans.entity.JsonEncodable;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

/**
 * 
 * @author Anthony Deroche - Thierry Duhal - Arthur Garnier
 *
 */
public class Conversion {

	public static Timestamp dateToTimestamp(String date) throws ParseException, NullPointerException {
		DateFormat df;
		Date d;
		df = new SimpleDateFormat("MM/dd/yyyyHH:mm");
		try {
			d = (Date) df.parse(date);
		} catch (ParseException pe) {
			d = new Date();
		}
		Timestamp timestamp = new Timestamp(d.getTime());
		return timestamp;
	}

	public static long timeToTimestampLong(String s){
		String[] st = s.split(":");
		long l = 0L;
		l+=Integer.parseInt(st[0])*60*60*1000L;
		l+=Integer.parseInt(st[1])*60*1000L;
		l+=Integer.parseInt(st[2])*1000L;
		//l-=3600*1000;
		return l;
	}
	
	
	public static <T extends JsonEncodable> JsonArray listToJson(List<T> list){
		return Conversion.listToJson(list,null);
	}
	
	public static <T extends JsonEncodable> JsonArray listToJson(List<T> list,String valueField){
		JsonArrayBuilder builder = Json.createArrayBuilder();
		if(valueField!=null){
			for(JsonEncodable obj : list){
				builder.add(obj.encode().get(valueField));
			}
		}else{
			for(JsonEncodable obj : list){
				builder.add(obj.encode());
			}
		}
		return builder.build();
	}
}
