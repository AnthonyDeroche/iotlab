/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.admin;

import iotlab.core.beans.entity.JsonEncodable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Locale;

import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * 
 * @author Anthony Deroche
 *
 */
@Stateless
public class Stats extends ArrayList<Stats.Stat> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@PersistenceContext(unitName = "iotlabUnit")
	protected EntityManager em;
	private static final String db = "iotlab"; //db name, todo : get it dynamic

	public void calculate() {
		this.clear();
		Stat stat;
		Query query;
		query = em.createNativeQuery("SELECT sum( data_length + index_length) / 1024 'Size' FROM information_schema. TABLES WHERE table_schema='"+db+"'");
		stat = new Stat("Database size",
				((BigDecimal) query.getSingleResult()).doubleValue(), "KB");
		this.add(stat);

		query = em
				.createNativeQuery("SELECT table_name AS 'Tables', round(((data_length + index_length) / 1024), 2) 'Size', table_rows 'count' FROM information_schema. TABLES WHERE table_schema = '"+db+"' ORDER BY table_schema ASC");
		Object[] result;

		for (Object o : query.getResultList()) {
			result = (Object[]) o;
			this.add(new Stat(result[0] + " number", ((BigInteger) result[2])
					.doubleValue(), "rows"));
			stat = new Stat(result[0] + " size",
					((BigDecimal) result[1]).doubleValue(), "KB");
			this.add(stat);
		}
	}

	public static class Stat implements JsonEncodable {

		private String parameter;
		private double value;
		private String unit;

		public Stat(String parameter, double value, String unit) {
			super();
			this.parameter = parameter;
			this.value = value;
			this.unit = unit;

			if (value >= 1024 && unit.equals("KB")) {
				this.setValue((double) Math.round(value * 10 / 1024) / 10);
				this.setUnit("MB");
			}
		}

		public String getParameter() {
			return parameter;
		}

		public void setParameter(String parameter) {
			this.parameter = parameter;
		}

		public double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		@Override
		public JsonObject encode() {
			return Json
					.createObjectBuilder()
					.add("parameter", parameter)
					.add("value",
							Double.valueOf(String.format(Locale.ENGLISH,
									"%.2f", value))).add("unit", unit).build();
		}
	}

}
