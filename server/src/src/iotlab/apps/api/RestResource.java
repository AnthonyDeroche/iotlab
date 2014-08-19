/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.apps.api;

import iotlab.core.authentification.AccountManager;
import iotlab.core.beans.dao.DAOManager;
import iotlab.module.monitoring.LogManager;
import iotlab.utils.DataAnalyser;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
public abstract class RestResource {
	
	@EJB
	protected DAOManager dao;
	
	@Context
	protected UriInfo context;
	
	@EJB
	protected DataAnalyser dataAnalyser;
	
	@Context 
	protected HttpServletRequest request;
	
	@EJB
	protected AccountManager accountManager;
	
	@EJB
	protected LogManager logManager;

}
