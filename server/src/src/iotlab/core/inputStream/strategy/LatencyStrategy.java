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
public class LatencyStrategy implements SaveStrategy<Double> {

	public LatencyStrategy() {
		super();
	}

	@Override
	public Double execute(int[] data, int i) {
		return new Double(data[i] / 32678.0);
	}
}
