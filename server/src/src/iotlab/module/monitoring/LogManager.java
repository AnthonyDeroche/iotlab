/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.monitoring;

import iotlab.core.authentification.AccountManager;
import iotlab.core.authentification.Member;
import iotlab.core.beans.dao.DAOManager;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author Anthony Deroche
 *
 */
@Stateless
public class LogManager {

	@EJB
	private DAOManager dao;

	private static final int OUT_LEVEL = Log.LEVEL_WARNING;

	public void info(HttpSession session, String module, String message,
			String ip) {

		Log log = new Log(
				(Member) session.getAttribute(AccountManager.USER_SESSION),
				module, message, Log.LEVEL_INFO, ip);
		dao.getLogDAO().persist(log);
		out(log, Log.LEVEL_INFO);
	}

	public void warning(HttpSession session, String module, String message,
			String ip) {
		Log log = new Log(
				(Member) session.getAttribute(AccountManager.USER_SESSION),
				module, message, Log.LEVEL_WARNING,
				(String) session.getAttribute("ip"));
		dao.getLogDAO().persist(log);
		out(log, Log.LEVEL_WARNING);
	}

	public void severe(HttpSession session, String module, String message,
			String ip) {
		Log log = new Log(
				(Member) session.getAttribute(AccountManager.USER_SESSION),
				module, message, Log.LEVEL_SEVERE,
				(String) session.getAttribute("ip"));
		dao.getLogDAO().persist(log);
		out(log, Log.LEVEL_SEVERE);
	}

	private void out(Log log, int lvl) {
		if (lvl >= OUT_LEVEL)
			System.out.println(log);
	}

	public static String getIP(HttpServletRequest request) {
		return request.getHeader("X-Forwarded-For") != null ? request
				.getHeader("X-Forwarded-For") : request.getRemoteHost();
	}

}
