/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.geolocation;

import iotlab.core.beans.dao.DAO;
import iotlab.core.beans.dao.DAOException;

import javax.ejb.Stateless;

/**
 * 
 * @author Anthony Deroche
 *
 */
@Stateless
public class AnchorDAO extends DAO<Anchor>{
	
	private static final String ENTITY_NAME = "Anchor";

	public String getEntityName(){
		return ENTITY_NAME;
	}

	public AnchorDAO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Anchor find(int id) throws DAOException{
		Anchor anchor = em.find(Anchor.class,id);
		if(anchor==null){
			throw new DAOException("AnchorDAO","Anchor not found");
		}
		return anchor;
	}
	
	public Anchor getReference(int id) throws DAOException{
		Anchor anchor = em.getReference(Anchor.class, id);
		if(anchor==null){
			throw new DAOException("AnchorDAO","Anchor not found");
		}
		return anchor;
	}
	
	public void remove(Anchor anchor){
		em.remove(em.merge(anchor));
	}
}
