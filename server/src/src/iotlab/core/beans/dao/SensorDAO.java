/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.dao;

import iotlab.core.beans.entity.Sensor;

import javax.ejb.Stateless;
/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
@Stateless
public class SensorDAO extends DAO<Sensor> {
	private static final String ENTITY_NAME = "Sensor";

	@Override
	protected String getEntityName() {
		// TODO Auto-generated method stub
		return ENTITY_NAME;
	}

}
