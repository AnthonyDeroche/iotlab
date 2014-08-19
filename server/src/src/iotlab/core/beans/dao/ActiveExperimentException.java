/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.dao;

/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
@SuppressWarnings("serial")
public class ActiveExperimentException extends DAOException {

	public ActiveExperimentException(String title, String message) {
		super(title, message);
	}

}
