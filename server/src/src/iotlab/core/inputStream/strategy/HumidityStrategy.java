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
public class HumidityStrategy implements SaveStrategy<Double> {

	public HumidityStrategy() {
		super();
	}

	@Override
	public Double execute(int[] data, int i) {
		double v = -4.0 + 405.0 * data[i] / 10000.0;
		if (v > 100) {
			v=100;
		}
		return new Double(v);
	}

}
