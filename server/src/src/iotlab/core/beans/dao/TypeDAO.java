/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.dao;

import iotlab.core.beans.entity.Type;

import javax.ejb.Stateless;
import javax.persistence.PersistenceException;

/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
@Stateless
public class TypeDAO extends DAO<Type> {

	private static final String ENTITY_NAME = "Type";

	@Override
	protected String getEntityName() {
		// TODO Auto-generated method stub
		return ENTITY_NAME;
	}

	public Type find(int id) throws DAOException {
		Type t = em.find(Type.class, id);
		if (t == null)
			throw new DAOException("Type", "This type does not exist");
		return t;
	}

	public Type getReference(int id) throws DAOException {
		return super.getReference(id, Type.class);
	}

	public void remove(Type type) throws DAOException {
		em.remove(em.merge(type));
		try {
			em.flush();
		} catch (PersistenceException e) {
			throw new DAOException(
					"Type deletion",
					"The type '"
							+ type.getDescription()
							+ "' cannot be deleted because some filters are dependent on it");
		}
	}
}
