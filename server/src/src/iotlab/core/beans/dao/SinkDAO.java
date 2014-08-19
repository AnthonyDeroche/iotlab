/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.dao;

import iotlab.core.beans.entity.mote.Sink;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

/**
 * 
 * @author Thierry Duhal
 *
 */
@Stateless
public class SinkDAO extends DAO<Sink> {

	private static final String ENTITY_NAME = "Sink";

	public String getEntityName() {
		return ENTITY_NAME;
	}

	public SinkDAO() {
		super();
	}

	public Sink find(int id) throws DAOException {
		Sink sink = em.find(Sink.class, id);
		if (sink == null) {
			throw new DAOException("SinkDAO", "Sink not found");
		}
		return sink;
	}

	public Sink find(String mac) throws DAOException {

		Sink mote = null;
		try {
			Query query = em.createQuery("SELECT m FROM " + ENTITY_NAME
					+ " m WHERE m.mote.mac = :mac");
			query.setParameter("mac", mac);
			mote = (Sink) query.getSingleResult();
		} catch (NoResultException e1) {
			throw new DAOException("SinkDAO", "No result for addr=" + mac);
		} catch (NonUniqueResultException e2) {
			throw new DAOException("SinkDAO",
					"Several motes have the same address (" + mac + ")");
		}
		return mote;
	}

	public Sink mergeSink(String addr, int dvn) {
		Sink sink;
		try {
			sink = this.find(addr);
			sink.setDodagVersionNumber(dvn);
			this.merge(sink);

			// Mote already in persistent context
		} catch (DAOException e) {
			// Adding new Mote in persistent context
			sink = new Sink(addr, addr, dvn);
			this.persist(sink);
			System.out.println("Adding new sink " + addr
					+ " to persistent context");

		}
		return sink;

	}

}
