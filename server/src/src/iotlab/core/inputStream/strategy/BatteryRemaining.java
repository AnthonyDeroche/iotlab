/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.inputStream.strategy;

/**
 * 
 * @author Arthur Garnier
 *
 */
public class BatteryRemaining implements SaveStrategy<Double> {

	public BatteryRemaining() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Double execute(int[] data, int i) {
		String st = data[i]+"."+data[i+1];
		return Double.parseDouble(st);
	}

}
