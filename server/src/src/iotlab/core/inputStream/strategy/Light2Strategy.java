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
public class Light2Strategy implements SaveStrategy<Double> {

	public Light2Strategy() {
		super();
	}

	@Override
	public Double execute(int[] data, int i) {
		return new Double(46.0 * data[i] / 10.0);
	}

}
