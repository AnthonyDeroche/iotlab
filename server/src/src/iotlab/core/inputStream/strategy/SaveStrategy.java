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
public interface SaveStrategy<T> {

	public T execute(int[] data, int i);
}
