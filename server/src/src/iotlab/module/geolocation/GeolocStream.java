/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.geolocation;

import iotlab.core.beans.dao.ActiveExperimentException;
import iotlab.core.beans.dao.DAOException;
import iotlab.core.beans.dao.DAOManager;
import iotlab.core.beans.entity.Error;
import iotlab.core.beans.entity.Type;
import iotlab.core.beans.entity.mote.Sender;
import iotlab.core.inputStream.InputStream;
import iotlab.core.inputStream.InvalidDataException;
import iotlab.core.inputStream.strategy.MoteAddrStrategy;

import java.sql.Timestamp;

/**
 * 
 * @author Anthony Deroche
 *
 */
public class GeolocStream extends InputStream<GeolocationData, Double> {

	private Timestamp timestamp;
	private static boolean valid = true;
	private static final int NODE_ID_OFFSET = 2;
	private static final int DATA_NB_OFFSET = 3;
	private static final int FIRST_NEIGHBOR_ID = 4;
	private Type type;

	public GeolocStream(String dataStr, DAOManager dao)
			throws InvalidDataException, DAOException {
		super(dao);

		String[] split = dataStr.split("\\s");
		this.timestamp = new Timestamp(Long.valueOf(split[0]));
		data = new int[split.length];
		for (int i = 1; i < split.length; i++) {
			try {
				data[i] = Integer.valueOf(split[i]);
			} catch (NumberFormatException e) {
				valid = false;
				// System.err.println("Data number " + i +
				// " is not an integer");
				dao.getErrorDAO().persist(
						new Error("Geolocation data parsing", "Data number "
								+ i + " is not an integer"));

			}
		}

		type = dao.getTypeDAO().find(data[TYPE_OFFSET]);

	}

	protected Type getType() {
		return type;
	}

	public int[] getData() {
		return this.data;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public boolean isValid() {
		return valid;
	}

	@Override
	protected void postConvert() throws ActiveExperimentException,
			InvalidDataException {
		Sender src = this.getMote(this.data[NODE_ID_OFFSET]);
		Sender dest;
		// Experiment experiment = dao.getExperimentDAO().getActiveExperiment();

		GeolocationData data;
		int end = Math.min(this.data[DATA_NB_OFFSET] * 2, this.data.length
				- DATA_NB_OFFSET);
		for (int i = FIRST_NEIGHBOR_ID; i < end + FIRST_NEIGHBOR_ID; i += 2) {
			dest = this.getMote(this.data[i]);
			data = new GeolocationData(src, dest, this.data[i + 1], timestamp);
			dataList.add(data);
		}
	}

	/**
	 * Create a new mote or get it from persistent context if it already exists
	 * 
	 * @param node_id
	 *            The node_id coming from the raw data line
	 * @return the mote
	 */
	private Sender getMote(int node_id) {

		// address of Mote
		String addr = new MoteAddrStrategy().execute(new int[] { node_id }, 0);
		return dao.getSenderDAO().mergeSender(addr);
	}

}
