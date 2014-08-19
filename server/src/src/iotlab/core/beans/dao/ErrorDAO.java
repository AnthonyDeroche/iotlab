/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.dao;

import iotlab.core.beans.entity.Error;

import javax.ejb.Stateless;

/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
@Stateless
public class ErrorDAO extends DAO<Error>{
	
	private static final String ENTITY_NAME = "Error";

	@Override
	protected String getEntityName() {
		// TODO Auto-generated method stub
		return ENTITY_NAME;
	}

	
}
