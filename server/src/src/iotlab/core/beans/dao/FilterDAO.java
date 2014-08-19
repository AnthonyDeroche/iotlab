/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.dao;

import iotlab.core.beans.entity.Filter;
import iotlab.core.beans.entity.Type;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
@Stateless
public class FilterDAO extends DAO<Filter> {

	private static final String ENTITY_NAME = "Filter";

	@Override
	protected String getEntityName() {
		// TODO Auto-generated method stub
		return ENTITY_NAME;
	}

	public Filter find(int id) throws DAOException {
		Filter f = em.find(Filter.class, id);
		if (f == null)
			throw new DAOException("Filter", "This filter does not exist");
		return f;
	}
	
	public List<Filter> find(Type type){
		List<Object> parameters = new ArrayList<>();
		parameters.add(type.getId());
		return this.getAll("offset", true, 0, "o.type.id=?1",parameters);
	}

	public Filter find(int offset, Type type) throws DAOException {
		Filter filter = null;
		try {
			Query query = em.createQuery("SELECT f FROM " + ENTITY_NAME
					+ " f WHERE f.offset = :offset AND f.type = :type");
			query.setParameter("offset", offset);
			query.setParameter("type", type);
			filter = (Filter) query.getSingleResult();
		} catch (NoResultException e1) {
			throw new DAOException("FilterDAO", "No result for offset="
					+ offset + "and type=" + type.getId());
		}
		return filter;
	}

	public void remove(Filter filter) throws DAOException {
		em.remove(em.merge(filter));
		try {
			em.flush();
		} catch (PersistenceException e) {
			throw new DAOException("Filter deletion", "Filter with offset '"
					+ filter.getOffset() + "' cannot be deleted");
		}
	}

}
