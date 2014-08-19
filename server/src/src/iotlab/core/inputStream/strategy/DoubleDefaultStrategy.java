/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.inputStream.strategy;

/**
 * 
 * This class should not be removed because it's instantiated
 * during the conversion process
 * 
 */
/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
public class DoubleDefaultStrategy implements SaveStrategy<Double> {

	public DoubleDefaultStrategy() {
		super();
	}

	@Override
	public Double execute(int[] data, int i) {
		return new Double(data[i]);
	}

}
