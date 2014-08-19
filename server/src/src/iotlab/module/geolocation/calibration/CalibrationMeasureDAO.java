/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.geolocation.calibration;

import iotlab.core.beans.dao.DAO;
import iotlab.core.beans.dao.DAOException;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 * 
 * @author Anthony Deroche
 *
 */
@Stateless
public class CalibrationMeasureDAO extends DAO<CalibrationMeasure> {

	private static final String ENTITY_NAME = "CalibrationMeasure";

	// private int countMeasures=-1;

	@Override
	protected String getEntityName() {
		// TODO Auto-generated method stub
		return ENTITY_NAME;
	}

	/*
	 * public Map<Integer,Map<Integer,CalibrationMeasure>> getAllInMap(){
	 * Map<Integer,Map<Integer,CalibrationMeasure>> map = new HashMap<>();
	 * List<CalibrationMeasure> all = super.getAll(); countMeasures =
	 * all.size(); for(CalibrationMeasure m : all){
	 * if(map.get(m.getFirst().getId())==null) map.put(m.getFirst().getId(), new
	 * HashMap<Integer,CalibrationMeasure>());
	 * map.get(m.getFirst().getId()).put(m.getSecond().getId(), m); } return
	 * map; }
	 * 
	 * public int countMeasures(){ return countMeasures; }
	 */

	public CalibrationMeasure find(int first_mote_id, int second_mote_id)
			throws DAOException {

		CalibrationMeasure measure = null;
		try {
			Query query = em.createQuery("SELECT m FROM " + ENTITY_NAME
					+ " m WHERE m.first.mote.id = :first AND m.second.mote.id = :second");
			query.setParameter("first", first_mote_id);
			query.setParameter("second", second_mote_id);
			measure = (CalibrationMeasure) query.getSingleResult();
		} catch (NoResultException e1) {
			throw new DAOException("CalibrationMeasureDAO",
					"No result for first_id=" + first_mote_id
							+ " and second_id=" + second_mote_id);
		}
		return measure;
	}
}
