/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.geolocation.calibration;

import iotlab.core.beans.dao.DAO;

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
public class CalibrationDataDAO extends DAO<CalibrationData> {

	private static final String ENTITY_NAME = "CalibrationData";

	@Override
	protected String getEntityName() {
		// TODO Auto-generated method stub
		return ENTITY_NAME;
	}

	public CalibrationDataDAO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CalibrationData find(int id) {
		return em.find(CalibrationData.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<CalibrationData> getLastDataForEachMote(int count) {
		// TODO Auto-generated method stub
		Query subquery = em.createNativeQuery("SELECT "+
			" CAST(SUBSTRING_INDEX( "+
			      " SUBSTRING_INDEX( "+
			        " GROUP_CONCAT(id ORDER BY id DESC), "+
			        " ',', value), "+
			      " ',', -1) AS SIGNED) AS id "+
			" FROM CalibrationData, tinyint_asc WHERE tinyint_asc.value >= 1 AND tinyint_asc.value <= "+count+" GROUP BY measure_id,value");

		List<Long> subResult = subquery.getResultList();
		List<CalibrationData> results = new ArrayList<CalibrationData>();
		if (subResult.size() > 0) {
			for (Long id : subResult)
				results.add(this.find(id.intValue()));
		}
		return results;
	}
}
