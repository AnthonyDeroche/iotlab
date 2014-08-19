/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.data;

import iotlab.core.beans.dao.DAO;
import iotlab.core.beans.entity.Experiment;
import iotlab.core.beans.entity.Label;
import iotlab.core.beans.entity.mote.Sender;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
@Stateless
public class DataDAO extends DAO<Data> {
	private static final String ENTITY_NAME = "Data";

	@Override
	protected String getEntityName() {
		// TODO Auto-generated method stub
		return ENTITY_NAME;
	}

	private String condition;
	private List<Object> parameters;

	@SuppressWarnings({ "unchecked" })
	public Data find(int experiment, int mote_id, int label_id, double value) {
		Query subquery = em
				.createQuery("SELECT o FROM "
						+ getEntityName()
						+ " o WHERE o.experiment.id=?1 AND o.mote.mote.id=?2 AND o.label.label_id=?3 "
						+ "AND o.value=?4");
		List<Object> param = new ArrayList<>();
		param.add(experiment);
		param.add(mote_id);
		param.add(label_id);
		param.add(value);
		for (int i = 1; i <= param.size(); i++) {
			subquery.setParameter(i, param.get(i - 1));
		}
		List<Data> results = subquery.getResultList();
		return results.get(0);
	}

	private void buildCondition(int experiment, String label, String mote) {
		condition = "1=1";
		parameters = new ArrayList<Object>();
		int k = 1;
		condition += " AND o.experiment.id=?" + (k++);
		parameters.add(experiment);

		if (label.length() > 0) {
			String labels[] = label.split("-");
			condition += " AND (o.label.label=?" + (k++);
			parameters.add(labels[0]);
			for (int i = 1; i < labels.length; i++) {
				condition += " OR o.label.label=?" + (k++);
				parameters.add(labels[i]);
			}
			condition += ")";
		}

		if (mote.length() > 0) {
			String motes[] = mote.split("-");
			condition += " AND (o.mote.mote.mac=?" + (k++);
			parameters.add(motes[0]);
			for (int i = 1; i < motes.length; i++) {
				condition += " OR o.mote.mote.mac=?" + (k++);
				parameters.add(motes[i]);
			}
			condition += ")";
		}
	}

	public Data find(int id) {
		return em.find(Data.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<Data> getLastDataForEachMote(int experiment, String label,
			String mote) {
		this.buildCondition(experiment, label, mote);
		Query subquery = em.createQuery("SELECT MAX(o.id) FROM "
				+ getEntityName() + " o WHERE " + condition
				+ " GROUP BY o.label.label_id,o.mote.mote.id");
		if (parameters != null)
			for (int i = 1; i <= parameters.size(); i++) {
				subquery.setParameter(i, parameters.get(i - 1));
			}
		List<Integer> subResult = subquery.getResultList();

		List<Data> results = new ArrayList<Data>();
		if (subResult.size() > 0) {
			for (int id : subResult)
				results.add(this.find(id));
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<Data> getFirstDataForEachMote(int experiment, String label,
			String mote) {
		this.buildCondition(experiment, label, mote);
		Query subquery = em.createQuery("SELECT MIN(o.id) FROM "
				+ getEntityName() + " o WHERE " + condition
				+ " GROUP BY o.label.label_id,o.mote.mote.id");
		if (parameters != null)
			for (int i = 1; i <= parameters.size(); i++) {
				subquery.setParameter(i, parameters.get(i - 1));
			}
		List<Integer> subResult = subquery.getResultList();

		List<Data> results = new ArrayList<Data>();
		if (subResult.size() > 0) {
			for (int id : subResult)
				results.add(this.find(id));
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<Data> getMeans(boolean asc, int nb, int experiment,
			String label, String mote, long from, long to) {

		condition = "1=1";
		parameters = new ArrayList<Object>();
		int k = 1;
		condition += " AND o.experiment=?" + (k++);
		parameters.add(experiment);

		String labels[] = new String[0];
		if (label.length() > 0) {
			labels = label.split("-");
			condition += " AND (l.label=?" + (k++);
			parameters.add(labels[0]);
			for (int i = 1; i < labels.length; i++) {
				condition += " OR l.label=?" + (k++);
				parameters.add(labels[i]);
			}
			condition += ")";
		}

		String motes[] = new String[0];
		if (mote.length() > 0) {
			motes = mote.split("-");
			condition += " AND (m.mac=?" + (k++);
			parameters.add(motes[0]);
			for (int i = 1; i < motes.length; i++) {
				condition += " OR m.mac=?" + (k++);
				parameters.add(motes[i]);
			}
			condition += ")";
		}

		if (from != 0 && to != 0) {
			condition += " AND o.timestamp>=FROM_UNIXTIME(?" + (k++) + ") ";
			condition += " AND o.timestamp<=FROM_UNIXTIME(?" + (k++) + ") ";
			parameters.add(from);
			parameters.add(to);
		}

		String ascStr = "ASC";
		if (!asc)
			ascStr = "DESC";

		String step = "1";
		if (nb > 0)
			step = "CEIL((SELECT COUNT(o.id) FROM Data o,Label l,Mote m WHERE "
					+ condition
					+ " AND o.label_id=l.label_id AND o.mote_id=m.id)/" + nb
					+ ")";

		Query query = em
				.createNativeQuery(" SELECT grp,AVG(`value`) AS value,`label_id`,`label`,`experiment`,(MAX(unix_timestamp(`timestamp`))+MIN(unix_timestamp(`timestamp`)))/2 AS timestamp,`mote_id`,`mac` "
						+ " FROM (SELECT @i := -1) i , (SELECT @step := "
						+ step
						+ ") AS step , "
						+ " ("
						+ " SELECT "
						+ "@i:=@i+1 as rownum, "
						+

						"FLOOR(@i/@step) AS `grp`,value,label_id,label,experiment,timestamp,mote_id,mac  FROM "
						+ "(SELECT `value`,o.`label_id`,l.`label`,`experiment`,`timestamp`,m.`id` AS mote_id,m.`mac` "
						+ " FROM Data o, Label l, Mote m "
						+ "WHERE "
						+ condition
						+ " AND o.label_id=l.label_id AND o.mote_id=m.id "
						+ " ORDER BY o.`id` "
						+ ascStr
						+ " )T1)T2 "
						+ " GROUP BY mote_id,label_id,`grp`; ");

		if (parameters != null)
			for (int i = 1; i <= parameters.size(); i++) {
				query.setParameter(i, parameters.get(i - 1));
			}
		List<Data> objects = new ArrayList<Data>();
		List<Object[]> objs = query.getResultList();
		// System.out.println(query);

		for (Object[] o : objs) {
			Label nlabel = new Label();
			nlabel.setLabel_id((int) o[2]);
			nlabel.setLabel((String) o[3]);

			Sender nmote = new Sender();

			nmote.setId((int) o[6]);
			nmote.setMac((String) o[7]);
			nmote.setIpv6((String) o[7]);

			Experiment nexperiment = new Experiment();
			nexperiment.setId((int) o[4]);

			objects.add(new Data((double) o[1], nlabel, new Timestamp(
					((BigDecimal) o[5]).longValue()), nmote, nexperiment));
		}

		return objects;

	}

	public HashMap<String, Double> getLossRatio(int experiment, String mote,
			long from, long to) {

		this.buildCondition(experiment, "seq_no", mote);
		int k = parameters.size();
		if (from != 0 && to != 0) {
			condition += " AND o.timestamp>=FROM_UNIXTIME(?" + (k++) + ") ";
			condition += " AND o.timestamp<=FROM_UNIXTIME(?" + (k++) + ") ";
			parameters.add(from);
			parameters.add(to);
		}

		List<Data> data = this.getAll("id", true, -1, condition, parameters);
		HashMap<String, Double> lossRatios = new HashMap<>();

		List<Integer> receivedNb = new ArrayList<>();
		List<Integer> theoryNb = new ArrayList<>();
		List<Integer> lastSeqNo = new ArrayList<>();

		HashMap<String, Integer> indexOfCnt = new HashMap<>();
		int c = 0;
		for (int i = 0; i < data.size(); i++) {

			if (indexOfCnt.get(data.get(i).getMote().getMac()) == null) {
				indexOfCnt.put(data.get(i).getMote().getMac(), c);
				receivedNb.add(0);
				theoryNb.add(0);
				lastSeqNo.add(-1);
				c++;
			}
			int index = indexOfCnt.get(data.get(i).getMote().getMac());

			int seq_no = (int) data.get(i).getValue() % 128;
			receivedNb.set(index, receivedNb.get(index) + 1);

			if (lastSeqNo.get(index) >= 0) {
				int diff = Math.max(1,
						((seq_no - lastSeqNo.get(index) + 128) % 128));
				// System.out.println(data.get(i).getMote().getMac()+" : "+seq_no+" - "+lastSeqNo.get(index)+" = "+diff);
				theoryNb.set(index, theoryNb.get(index) + diff);
			}

			lastSeqNo.set(index, seq_no);
		}

		for (Entry<String, Integer> e : indexOfCnt.entrySet()) {
			String mac = e.getKey();
			int index = e.getValue();
			// System.out.println(mac+
			// " -> "+(double)receivedNb.get(index)+" / "+
			// (double)theoryNb.get(index));
			double lossRatio = 1 - ((double) receivedNb.get(index) / ((double) theoryNb
					.get(index) + 1));
			lossRatios.put(mac, lossRatio);
		}

		int theorySum = 0, receivedSum = 0;
		for (int i = 0; i < theoryNb.size(); i++) {
			theorySum += theoryNb.get(i) + 1;
			receivedSum += receivedNb.get(i);
		}
		lossRatios.put("all", 1 - (double) receivedSum / (double) theorySum);

		return lossRatios;
	}
}
