/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.authentification;

import iotlab.core.beans.dao.DAO;
import iotlab.core.beans.dao.DAOException;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 * 
 * @author Anthony Deroche
 *
 */
@Stateless
public class MemberDAO extends DAO<Member> {

	private static final String ENTITY_NAME = "Member";

	@Override
	protected String getEntityName() {
		// TODO Auto-generated method stub
		return ENTITY_NAME;
	}

	public Member find(String username) throws DAOException {
		Member m = em.find(Member.class, username);
		if(m==null)
			throw new DAOException("MemberDAO","Member "+username+" not found");
		return m;
	}

	public Member find(String username, String password) throws DAOException {
		Member member = null;
		try {
			Query query = em
					.createQuery("SELECT m FROM "
							+ ENTITY_NAME
							+ " m WHERE m.username = :username AND m.password = :password");
			query.setParameter("username", username);
			query.setParameter("password", password);
			member = (Member) query.getSingleResult();
		} catch (NoResultException e1) {
			throw new DAOException("MemberDAO", "No result for username="
					+ username + "and password provided");
		}
		return member;
	}
	
	public Member getReference(String username) throws DAOException{
		Member m = em.getReference(Member.class, username);
		if(m==null)
			throw new DAOException("MemberDAO","Member "+username+" not found");
		return m;
	}
	
	public void remove(Member member){
		em.remove(em.merge(member));
	}
}
