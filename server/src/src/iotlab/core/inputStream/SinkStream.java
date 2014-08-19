/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.inputStream;

import iotlab.core.beans.dao.ActiveExperimentException;
import iotlab.core.beans.dao.DAOException;
import iotlab.core.beans.dao.DAOManager;
import iotlab.core.beans.entity.Error;
import iotlab.core.beans.entity.Type;
import iotlab.core.beans.entity.mote.Sink;
import iotlab.core.inputStream.strategy.MoteAddrStrategy;

/**
 * 
 * @author Thierry Duhal
 *
 */
public class SinkStream extends InputStream<Sink, Double> {

	private static final int SINK_ID_OFFSET = 2;
	private static final int DVN_OFFSET = 3;
	private Type type;

	public SinkStream(String dataStr, DAOManager dao) throws DAOException,
			InvalidDataException {
		super(dao);
		System.out.println(this.getStreamName());
		String[] split = dataStr.split("\\s");
		data = new int[split.length];
		for (int i = 1; i < split.length; i++) {
			try {
				data[i] = Integer.valueOf(split[i]);
			} catch (NumberFormatException e) {
				dao.getErrorDAO().persist(
						new Error("Sink data parsing", "Data number " + i
								+ " is not an integer"));
			}
		}

		type = dao.getTypeDAO().find(data[TYPE_OFFSET]);

	}

	protected Type getType() {
		return type;
	}

	@Override
	protected void postConvert() throws ActiveExperimentException,
			InvalidDataException {
		dataList.add(this.getSink(data[SINK_ID_OFFSET], data[DVN_OFFSET]));

	}

	/**
	 * Create a new mote or get it from persistent context if it already exists
	 * 
	 * @param node_id
	 *            The node_id coming from the raw data line
	 * @return the mote
	 */
	private Sink getSink(int node_id, int dvn) {

		// address of Mote
		String addr = new MoteAddrStrategy().execute(new int[] { node_id }, 0);
		return dao.getSinkDAO().mergeSink(addr, dvn);

	}

}
