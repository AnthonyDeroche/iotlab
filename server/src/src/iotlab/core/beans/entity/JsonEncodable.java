/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.entity;

import javax.json.JsonObject;
/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
public interface JsonEncodable {
	public JsonObject encode();
}
