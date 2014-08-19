/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.authentification;

import iotlab.core.beans.dao.DAOException;
import iotlab.core.beans.dao.DAOManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author Anthony Deroche
 *
 */
@Stateless
public class AccountManager {

	public static final String USER_SESSION = "user";
	private static final String SALT = "CC2420";

	@EJB
	private DAOManager dao;

	public AccountManager() {
	}

	public void tryLogin(HttpSession session, String username, String password,
			String ip) throws AccountException {

		Member member;
		try {
			password = this.hash(password);
			member = dao.getMemberDAO().find(username, password);
			session.setAttribute(USER_SESSION, member);
			session.setAttribute("ip", ip);
		} catch (DAOException e) {
			throw new AccountException("Wrong username or password");
		}
	}
	
	public void tryRegister(String username, String password, String email, boolean admin) throws AccountException{
		try {
			dao.getMemberDAO().find(username);
			throw new AccountException("This username is unavailable");
		} catch (DAOException e) {
			password = this.hash(password);
			Member member = new Member(username,password,email,admin);
			dao.getMemberDAO().persist(member);
		}
	}

	public void logout(HttpSession session) {
		session.removeAttribute(USER_SESSION);
	}

	public boolean isLoggedIn(HttpSession session) {
		return session.getAttribute(USER_SESSION) != null;
	}

	public boolean isAdmin(HttpSession session) {
		return isLoggedIn(session)
				&& ((Member) session.getAttribute(USER_SESSION)).isAdmin();
	}

	private String hash(String password) {
		String hash = password;
		try {
			password = SALT + password;
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(password.getBytes());
			byte[] digest = md.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			System.err.println("SHA1 : NoSuchAlgorithmException");
		}
		return hash;
	}

	public void remove(String username) throws AccountException {
		try {
			dao.getMemberDAO().remove(dao.getMemberDAO().find(username));
		} catch (DAOException e) {
			throw new AccountException(e.getMessage());
		}
	}
	
	public void update(String username, String password, String email,
			boolean admin) throws AccountException {
		Member member;
		try {
			member = dao.getMemberDAO().find(username);
			if(password.length()>0)
				member.setPasswordHash(this.hash(password));
			member.setEmail(email);
			member.setAdmin(admin);
			dao.getMemberDAO().merge(member);
		} catch (DAOException e) {
			throw new AccountException(e.getMessage());
		}
		
	}
}
