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
public class BestNeighborETXStrategy implements SaveStrategy<Double> {

	public BestNeighborETXStrategy() {
		super();
	}

	@Override
	public Double execute(int[] data, int i) {
		return new Double(data[i] / 8.0);
	}

}
