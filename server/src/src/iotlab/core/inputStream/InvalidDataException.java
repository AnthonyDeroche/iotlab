/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.inputStream;

/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
public class InvalidDataException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidDataException(String message){
		super(message);
	}
	
	public InvalidDataException(){
		super("Data are not valid");
	}

}
