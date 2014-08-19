/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.dao;

import iotlab.core.beans.entity.Label;

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
public class LabelDAO extends DAO<Label> {

	private static final String ENTITY_NAME = "Label";

	@Override
	protected String getEntityName() {
		// TODO Auto-generated method stub
		return ENTITY_NAME;
	}

	/**
	 * Get the label from the persistent context
	 * 
	 * @param labelStr
	 *            The label (string)
	 * @return The label
	 * @throws DAOException
	 *             if there is no result
	 */
	public Label find(String labelStr) throws DAOException {
		Label label = null;
		try {
			Query query = em.createQuery("SELECT l FROM " + ENTITY_NAME
					+ " l WHERE l.label = :label");
			query.setParameter("label", labelStr);
			label = (Label) query.getSingleResult();
		} catch (NoResultException e1) {
			throw new DAOException("LabelDAO", "No result for label="
					+ labelStr);
		}
		return label;
	}

	public Label find(int label_id) throws DAOException {
		Label l = em.find(Label.class, label_id);
		if (l == null)
			throw new DAOException("Label", "This label does not exist");
		return l;
	}

	public void remove(Label label) throws DAOException {
		em.remove(em.merge(label));
		try {
			em.flush();
		} catch (PersistenceException e) {
			throw new DAOException(
					"Label deletion",
					"The label '"
							+ label.getLabel()
							+ "' cannot be deleted because some filters or data are dependent on it");
		}
	}


	public Label getReference(int id) throws DAOException {
		return super.getReference(id, Label.class);
	}

}
