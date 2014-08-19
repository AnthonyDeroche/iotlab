/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.dao;

import iotlab.core.beans.entity.mote.Mote;
import iotlab.core.beans.entity.mote.MoteImpl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
@Stateless
public class MoteDAO extends DAO<Mote> {

	public Mote find(int id) throws DAOException {
		Mote mote;
		mote = em.find(MoteImpl.class, id);
		if (mote == null) {
			throw new DAOException("MoteDAO", "Mote not found");
		}
		return mote;
	}

	public Mote getReference(int id) throws DAOException {
		Mote mote;
		mote = em.getReference(MoteImpl.class, id);
		if (mote == null) {
			throw new DAOException("MoteDAO", "Mote not found");
		}
		return mote;
	}

	private static final String ENTITY_NAME = "MoteImpl";

	@Override
	protected String getEntityName() {
		// TODO Auto-generated method stub
		return ENTITY_NAME;
	}

	public Mote find(String mac) throws DAOException {

		Mote mote = null;
		try {
			Query query = em.createQuery("SELECT m FROM " + ENTITY_NAME
					+ " m WHERE m.mac = :mac");
			query.setParameter("mac", mac);
			mote = (Mote) query.getSingleResult();
		} catch (NoResultException e1) {
			throw new DAOException("MoteDAO", "No result for addr=" + mac);
		} catch (NonUniqueResultException e2) {
			throw new DAOException("MoteDAO",
					"Several motes have the same address (" + mac + ")");
		}
		return mote;
	}

	public void remove(Mote mote) throws DAOException {
		em.remove(em.merge(mote));
		try {
			em.flush();
		} catch (PersistenceException e) {
			throw new DAOException(
					"Mote deletion",
					"The mote '"
							+ mote.getMac()
							+ "' cannot be deleted because some data are dependent on it");
		}
	}
}
