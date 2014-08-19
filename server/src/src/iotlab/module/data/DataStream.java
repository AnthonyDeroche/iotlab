/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.data;

import iotlab.core.beans.dao.ActiveExperimentException;
import iotlab.core.beans.dao.DAOException;
import iotlab.core.beans.dao.DAOManager;
import iotlab.core.beans.entity.Error;
import iotlab.core.beans.entity.Experiment;
import iotlab.core.beans.entity.Type;
import iotlab.core.beans.entity.mote.Sender;
import iotlab.core.inputStream.InputStream;
import iotlab.core.inputStream.InvalidDataException;
import iotlab.core.inputStream.strategy.MoteAddrStrategy;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;


/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
public class DataStream extends InputStream<Data, Double> {

	private Timestamp timestamp;
	private static final int NODE_ID_OFFSET = 6; // Offsets begin at 0 here, but
													// begin at 1 in the
													// database

	private static boolean valid = true;
	private Type type;

	public DataStream(String dataStr, DAOManager dao)
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
						new Error("Raw data parsing", "Data number " + i
								+ " is not an integer"));

			}
		}

		type = dao.getTypeDAO().find(data[TYPE_OFFSET]);
	}

	protected Type getType() {
		return type;
	}

	public DataStream(List<Data> list, DAOManager dao) throws DAOException {
		super(dao);
		this.dataList = list;
	}

	public void postConvert() throws ActiveExperimentException,
			InvalidDataException {
		// if (!valid)
		// throw new InvalidDataException();

		Sender mote = this.getMote(this.data[NODE_ID_OFFSET]);
		Experiment experiment = dao.getExperimentDAO().getActiveExperiment();

		Data data;
		for (int i = 0; i < values.size(); i++) {

			data = new Data(values.get(i), filters.get(i).getLabel(),
					timestamp, mote, experiment);

			// System.out.println(data.getExperiment().getId() + " - " +
			// data.getLabel().getLabel() + " = " + data.getValue());

			dataList.add(data);
		}
	}

	/**
	 * Methode qui convertit les Data de la BDD en JsonObject
	 * 
	 * @return Arraylist contenant les JsonObjects
	 */
	public static ArrayList<JsonObject> fromBDD(List<Data> list) {
		ArrayList<JsonObject> ret = new ArrayList<JsonObject>();
		for (Data d : list) {
			JsonObjectBuilder dataBuilder = Json.createObjectBuilder();
			dataBuilder.add("mote", d.getMote().getIpv6());
			dataBuilder.add("timestamp", d.getTimestamp());
			dataBuilder.add(d.getLabel().getLabel(), d.getValueToStr());
			dataBuilder.add("experiment_id", d.getExperiment().getId());
			JsonObject jsonObject = dataBuilder.build();
			ret.add(jsonObject);
		}
		return ret;
	}

	public JsonObject toJSON() {
		if (dataList == null) {
			throw new IllegalStateException(
					"Data must be converted before getting JSON");
		}
		int node_id = this.data[NODE_ID_OFFSET];
		String addr = (node_id & 0xff) + "." + ((node_id >> 8) & 0xff);
		JsonObjectBuilder dataBuilder = Json.createObjectBuilder();

		dataBuilder.add("mote", addr);
		for (Data data : dataList) {
			dataBuilder.add(data.getLabel().getLabel(), data.getValueToStr());
		}
		dataBuilder.add("timestamp", timestamp.getTime());
		JsonObject jsonObject = dataBuilder.build();
		return jsonObject;
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

	public int[] getData() {
		return this.data;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public boolean isValid() {
		return valid;
	}

}
