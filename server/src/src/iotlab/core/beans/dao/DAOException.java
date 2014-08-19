/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.dao;

import iotlab.core.beans.entity.JsonEncodable;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
public class DAOException extends Exception implements JsonEncodable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title;

	public DAOException(String title, String message) {
		super(message);
		this.title = title;

	}

	public DAOException() {
		super();
	}

	public DAOException(Exception e, String title) {
		super(e);
	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return title;
	}

	@Override
	public JsonObject encode() {
		// TODO Auto-generated method stub
		return Json.createObjectBuilder().add("Error", this.getMessage()).build();
	}
}
