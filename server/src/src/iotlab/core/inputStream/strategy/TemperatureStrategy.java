/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.inputStream.strategy;

/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
public class TemperatureStrategy implements SaveStrategy<Double> {

	public TemperatureStrategy() {
		super();
	}

	@Override
	public Double execute(int[] data, int i) {
		return new Double(-39.6 + 0.01 * data[i]);
	}

}
