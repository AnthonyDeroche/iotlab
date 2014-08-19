/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.dao;

import iotlab.core.beans.entity.Strategy;

import javax.ejb.Stateless;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
@Stateless
public class StrategyDAO extends DAO<Strategy> {

	private static final String ENTITY_NAME = "Strategy";

	@Override
	protected String getEntityName() {
		// TODO Auto-generated method stub
		return ENTITY_NAME;
	}

	public Strategy find(int id) throws DAOException {
		Strategy s = em.find(Strategy.class, id);
		if (s == null)
			throw new DAOException("Strategy", "This strategy does not exist");
		return s;
	}

	public void remove(Strategy strategy) throws DAOException {
		em.remove(em.merge(strategy));
		try {
			em.flush();
		} catch (PersistenceException e) {
			throw new DAOException("Strategy deletion",
					"Strategy associated with '" + strategy.getClassName()
							+ "' cannot be deleted due to dependances");
		}
	}

	public Strategy find(String className) throws DAOException {
		Strategy strategy = null;
		try {
			Query query = em.createQuery("SELECT s FROM " + ENTITY_NAME
					+ " s WHERE s.className = :className");
			query.setParameter("className", className);
			strategy = (Strategy) query.getSingleResult();
		} catch (NoResultException e1) {
			throw new DAOException("LabelDAO", "No result for strategy="
					+ className);
		}
		return strategy;
	}

	public Strategy getReference(int strategy_id) throws DAOException {
		Strategy s = null;
		try {
			s = em.getReference(Strategy.class, strategy_id);

		} catch (EntityNotFoundException e) {
			throw new DAOException("Label", "Strategy with id=" + strategy_id
					+ " not found");
		}
		return s;
	}

}
