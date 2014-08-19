/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.dao;

import iotlab.core.beans.entity.Experiment;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
@Stateless
public class ExperimentDAO extends DAO<Experiment>{


	private static final String ENTITY_NAME = "Experiment";

	@Override
	protected String getEntityName() {
		// TODO Auto-generated method stub
		return ENTITY_NAME;
	}
	
	public Experiment getActiveExperiment() throws ActiveExperimentException{
		Experiment experiment = null;
		try {
			Query query = em.createQuery("SELECT e FROM " + ENTITY_NAME
					+ " e WHERE e.active = 1");
			experiment = (Experiment) query.getSingleResult();
		} catch (NoResultException e1) {
			throw new ActiveExperimentException("ExperimentDAO","No active experiment");
		} catch (NonUniqueResultException e2) {
			throw new ActiveExperimentException("ExperimentDAO","Several active experiment");
		}
		return experiment;
	}

}
