/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.admin;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * 
 * @author Anthony Deroche
 *
 */
@Stateless
public class Config extends HashMap<String, Parameter> {

	private static final long serialVersionUID = 1L;

	@PersistenceContext(unitName = "iotlabUnit")
	protected EntityManager em;

	public Config() {
		
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void load() {
		Query query = em.createQuery("SELECT p FROM Parameter p");
		for (Parameter p : (List<Parameter>) query.getResultList()) {
			this.put(p.getKey(), p);
		}
	}
}