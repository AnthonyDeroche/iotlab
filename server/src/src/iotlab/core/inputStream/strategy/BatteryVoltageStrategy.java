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
public class BatteryVoltageStrategy implements SaveStrategy<Double> {

	public BatteryVoltageStrategy() {
		super();
	}

	@Override
	public Double execute(int[] data, int i) {
		return new Double(data[i] * 2 * 2.5 / 4096.0);
	}

}
