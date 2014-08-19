/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.geolocation;

import iotlab.core.beans.dao.DAO;
import iotlab.core.beans.dao.DAOException;
import iotlab.core.beans.entity.mote.Sender;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 * 
 * @author Anthony Deroche
 *
 */
@Stateless
public class GeolocationDataDAO extends DAO<GeolocationData> {

	private static final String ENTITY_NAME = "GeolocationData";

	@Override
	protected String getEntityName() {
		// TODO Auto-generated method stub
		return ENTITY_NAME;
	}

	public GeolocationData find(int id) {
		return em.find(GeolocationData.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<GeolocationData> getLastDataForEachMote(String src,String dest) {
		this.buildCondition(src,dest);
		Query subquery = em.createQuery("SELECT MAX(o.id) FROM "
				+ getEntityName() + " o WHERE " + condition
				+ " GROUP BY o.src.mote.id,o.dest.mote.id");
		if (parameters != null)
			for (int i = 1; i <= parameters.size(); i++) {
				subquery.setParameter(i, parameters.get(i - 1));
			}
		List<Integer> subResult = subquery.getResultList();

		List<GeolocationData> results = new ArrayList<GeolocationData>();
		if (subResult.size() > 0) {
			for (int id : subResult)
				results.add(this.find(id));
		}
		return results;
	}

	private String condition;
	private List<Object> parameters;

	private void buildCondition(String src,String dest) {
		condition = "1=1";
		parameters = new ArrayList<Object>();
		int k = 1;

		if (src.length() > 0) {
			String motes[] = src.split("-");
			condition += " AND (o.src.mote.mac=?" + (k++);
			parameters.add(motes[0]);
			for (int i = 1; i < motes.length; i++) {
				condition += " OR o.src.mote.mac=?" + (k++);
				parameters.add(motes[i]);
			}
			condition += ")";
		}
		
		if (dest.length() > 0) {
			String motes[] = dest.split("-");
			condition += " AND (o.dest.mote.mac=?" + (k++);
			parameters.add(motes[0]);
			for (int i = 1; i < motes.length; i++) {
				condition += " OR o.dest.mote.mac=?" + (k++);
				parameters.add(motes[i]);
			}
			condition += ")";
		}
	}

	@SuppressWarnings("unchecked")
	public List<GeolocationData> getMeans(boolean asc, int nb, String src,String dest,
			long from, long to) throws DAOException {

		condition = "1=1";
		parameters = new ArrayList<Object>();
		int k = 1;

		String motes[] = new String[0];
		if (src.length() > 0) {
			motes = src.split("-");
			condition += " AND (m1.mac=?" + (k++);
			parameters.add(motes[0]);
			for (int i = 1; i < motes.length; i++) {
				condition += " OR m1.mac=?" + (k++);
				parameters.add(motes[i]);
			}
			condition += ")";
		}
		
		if (dest.length() > 0) {
			motes = dest.split("-");
			condition += " AND (m2.mac=?" + (k++);
			parameters.add(motes[0]);
			for (int i = 1; i < motes.length; i++) {
				condition += " OR m2.mac=?" + (k++);
				parameters.add(motes[i]);
			}
			condition += ")";
		}

		if (from != 0 && to != 0) {
			condition += " AND o.timestamp>=FROM_UNIXTIME(?" + (k++) + ") ";
			condition += " AND o.timestamp<=FROM_UNIXTIME(?" + (k++) + ") ";
			parameters.add(from);
			parameters.add(to);
		}

		String ascStr = "ASC";
		if (!asc)
			ascStr = "DESC";

		String step = "1";
		if (nb > 0)
			step = "CEIL((SELECT COUNT(o.id) FROM " + ENTITY_NAME
					+ " o,Mote m1, Mote m2 WHERE " + condition + " AND o.src=m1.id AND o.dest=m2.id)/" + nb
					+ ")";

		Query query = em
				.createNativeQuery(" SELECT grp,AVG(`rssi`) AS value,(MAX(unix_timestamp(`timestamp`))+MIN(unix_timestamp(`timestamp`)))/2 AS timestamp,`src`,`dest` "
						+ " FROM (SELECT @i := -1) i , (SELECT @step := "
						+ step
						+ ") AS step , "
						+ " ("
						+ " SELECT "
						+ "@i:=@i+1 as rownum, "
						+ "FLOOR(@i/@step) AS `grp`,`rssi`,`timestamp`,`src`,`dest`  FROM "
						+ "(SELECT `rssi`,`timestamp`,`src`,`dest`"
						+ " FROM "+ENTITY_NAME+" o, Mote m1, Mote m2 WHERE "+condition+" AND o.src=m1.id AND o.dest=m2.id "
						+ " ORDER BY o.`id` "+ ascStr + " )T1)T2 " + " GROUP BY `src`,`dest`,`grp`; ");

		if (parameters != null)
			for (int i = 1; i <= parameters.size(); i++) {
				query.setParameter(i, parameters.get(i - 1));
			}
		List<GeolocationData> objects = new ArrayList<>();
		List<Object[]> objs = query.getResultList();

		for (Object[] o : objs) {
			
			Sender srcMote = dao.getSenderDAO().find((int) o[3]);
			Sender destMote = dao.getSenderDAO().find((int) o[4]);

			objects.add(new GeolocationData(srcMote, destMote, ((BigDecimal) o[1])
					.intValue(), new Timestamp(((BigDecimal) o[2]).longValue())));
		}

		return objects;

	}

	public List<GeolocationData> getLastDataForEachMote() {
		// TODO Auto-generated method stub
		return getLastDataForEachMote("","");
	}

}
