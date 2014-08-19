/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.dao;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
@Stateless
public abstract class DAO<T> {

	@PersistenceContext(unitName = "iotlabUnit")
	protected EntityManager em;

	@EJB
	protected DAOManager dao;

	// public abstract T find(int id);

	public void persist(T object) {

		em.persist(object);
	}


	public void persist(List<T> values) {
		for (T v : values){
			this.persist(v);
		}
	}
	
	public void merge(List<T> values) {
		for (T v : values){
			this.merge(v);
		}
	}

	public T merge(T unit) {
		return em.merge(unit);
	}

	public List<T> getAll(String order, boolean asc, int limit) {
		return this.getAll(order, asc, limit, null, null);
	}

	public List<T> getAll(String order, boolean asc) {
		return this.getAll(order, asc, -1);
	}

	public List<T> getAll(String order, boolean asc, int limit, String where) {
		return this.getAll(order, asc, limit, where, new ArrayList<Object>());
	}

	public List<T> getAll() {
		return this.getAll(null, false, -1);
	}

	@SuppressWarnings("unchecked")
	public List<T> getAll(String order, boolean asc, int limit, String where,
			List<Object> parameters) {
		String ascStr = "ASC";
		if (!asc)
			ascStr = "DESC";

		String whereStr = "";
		if (where != null) {
			whereStr = "WHERE " + where;
		}

		String orderStr = "";
		if (order != null)
			orderStr = "ORDER BY o." + order + " " + ascStr;

		Query query = em.createQuery("SELECT o FROM " + this.getEntityName()
				+ " o " + whereStr + " " + orderStr);
		if (limit > -1)
			query.setMaxResults(limit);
		if (parameters != null)
			for (int i = 1; i <= parameters.size(); i++) {
				query.setParameter(i, parameters.get(i - 1));
			}
		List<T> objects = new ArrayList<T>();
		objects = query.getResultList();
		return objects;
	}

	protected T getReference(int id, Class<T> cast) throws DAOException {
		T t = null;
		try {
			t = (T) em.getReference(cast, id);

		} catch (EntityNotFoundException e) {
			throw new DAOException(getEntityName(), getEntityName()
					+ " with id=" + id + " not found");
		}
		return t;
	}

	abstract protected String getEntityName();
}
