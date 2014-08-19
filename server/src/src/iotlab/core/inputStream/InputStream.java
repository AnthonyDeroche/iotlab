/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.inputStream;

import iotlab.core.beans.dao.ActiveExperimentException;
import iotlab.core.beans.dao.DAOException;
import iotlab.core.beans.dao.DAOManager;
import iotlab.core.beans.entity.Filter;
import iotlab.core.beans.entity.Type;
import iotlab.core.inputStream.strategy.SaveStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
public abstract class InputStream<T, V> {

	protected DAOManager dao;

	protected List<Filter> filters;
	protected List<T> dataList;
	protected int[] data;
	protected List<V> values;

	protected static final int TYPE_OFFSET = 1;

	protected InputStream(DAOManager dao) throws DAOException {
		this.dao = dao;
		values = new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public List<T> convert(Class<? extends SaveStrategy<V>> defaultStrategy)
			throws ActiveExperimentException, InvalidDataException {

		this.checkValidity(getType());
		filters = dao.getFilterDAO().find(getType());

		dataList = new ArrayList<>();
		int f = 0;
		for (int i = 0; i < data.length; i++) {

			Filter filter = null;
			if(f<filters.size())
				filter = filters.get(f);
			try {
				SaveStrategy<V> strategy;
				if (filter!=null && (i + 1) == filter.getOffset()) {
					String className = filter.getStrategy().getClassName();
					String strategyClassName = "iotlab.core.inputStream.strategy." + className;
					strategy = (SaveStrategy<V>) Class.forName(
							strategyClassName).newInstance();
					f++;
				} else {
					strategy = defaultStrategy.newInstance();
				}

				values.add(strategy.execute(data, i));

				
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException e) {
				System.err.println("InputStream : "+e.getMessage());
				e.printStackTrace();
			}
		}

		this.postConvert();

		return dataList;
	}

	protected void checkValidity(Type type) throws InvalidDataException {
		/*
		 * System.out.println(type.getMinDataNumber() + " <= " + data.length +
		 * "" + type.getStreamName() + "==" + this.getStreamName());
		 */
		if (!(type.getMinDataNumber() <= data.length && type.getStreamName()
				.equals(this.getStreamName()))) {
			throw new InvalidDataException(type.getStreamName()
					+ " : Invalid format or wrong stream name");
		}
	}

	/**
	 * Post-processing after conversion of data
	 * 
	 * @throws ActiveExperimentException
	 * @throws InvalidDataException
	 */
	protected abstract void postConvert() throws ActiveExperimentException,
			InvalidDataException;

	protected abstract Type getType();

	protected String getStreamName() {
		return this.getClass().getSimpleName();
	}

	public List<T> getDataList() {
		return dataList;
	}

	public int[] getData() {
		return data;
	}
}
