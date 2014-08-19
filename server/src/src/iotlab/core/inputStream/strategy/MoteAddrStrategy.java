/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.inputStream.strategy;

/**
 * 
 * This class should not be removed because it's instantiated
 * for the conversion of a node_id to a mote address
 * 
 */
/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
public class MoteAddrStrategy implements SaveStrategy<String>{

	@Override
	public String execute(int[] data, int i) {
		String addr = (data[i] & 0xff) + "." + ((data[i] >> 8) & 0xff);
		return addr;
	}
}
