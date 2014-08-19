/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.dao;

import iotlab.core.beans.entity.mote.Sender;

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
public class SenderDAO extends DAO<Sender> {

	private static final String ENTITY_NAME = "Sender";

	public String getEntityName() {
		return ENTITY_NAME;
	}

	public SenderDAO() {
		super();
	}

	public Sender find(String mac) throws DAOException {

		Sender mote = null;
		try {
			Query query = em.createQuery("SELECT m FROM " + ENTITY_NAME
					+ " m WHERE m.mote.mac = :mac");
			query.setParameter("mac", mac);
			mote = (Sender) query.getSingleResult();
		} catch (NoResultException e1) {
			throw new DAOException("SenderDAO", "No result for addr=" + mac);
		} catch (NonUniqueResultException e2) {
			throw new DAOException("SenderDAO",
					"Several motes have the same address (" + mac + ")");
		}
		return mote;
	}

	public Sender find(int id) throws DAOException {
		Sender mote;
		mote = em.find(Sender.class, id);
		if (mote == null) {
			throw new DAOException("SenderDAO", "Mote not found");
		}
		return mote;
	}

	public Sender mergeSender(String addr) {
		Sender sender;
		try {
			sender = this.find(addr);
			this.merge(sender);

			// Mote already in persistent context
		} catch (DAOException e) {
			// Adding new Mote in persistent context
			sender = new Sender(addr, addr);
			this.persist(sender);
			System.out.println("Adding new sender " + addr
					+ " to persistent context");

		}
		return sender;

	}
}
