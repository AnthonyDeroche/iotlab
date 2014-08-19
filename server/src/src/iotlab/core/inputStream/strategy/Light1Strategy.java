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
public class Light1Strategy implements SaveStrategy<Double> {

	public Light1Strategy() {
		super();
	}

	@Override
	public Double execute(int[] data, int i) {
		return new Double(10.0 * data[i] / 7.0);
	}

}
