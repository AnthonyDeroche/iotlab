/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.monitoring;

import iotlab.core.beans.dao.DAO;

import javax.ejb.Stateless;

/**
 * 
 * @author Anthony Deroche
 *
 */
@Stateless
public class LogDAO extends DAO<Log>{
	
	private final String ENTITY_NAME = "Log";

	
	public LogDAO(){
		
	}
	
	
	
	@Override
	protected String getEntityName() {
		// TODO Auto-generated method stub
		return ENTITY_NAME;
	}

}
