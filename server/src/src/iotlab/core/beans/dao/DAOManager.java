/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.dao;

import iotlab.core.authentification.MemberDAO;
import iotlab.module.data.DataDAO;
import iotlab.module.geolocation.AnchorDAO;
import iotlab.module.geolocation.GeolocationDataDAO;
import iotlab.module.geolocation.calibration.CalibrationDataDAO;
import iotlab.module.geolocation.calibration.CalibrationMeasureDAO;
import iotlab.module.monitoring.AlertDAO;
import iotlab.module.monitoring.LogDAO;
import iotlab.module.monitoring.MonitoringRuleDAO;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
@Stateless
public class DAOManager {

	@EJB
	private MoteDAO moteDAO;
	@EJB
	private SenderDAO senderDAO;
	@EJB
	private SinkDAO sinkDAO;
	@EJB
	private FilterDAO filterDAO;
	@EJB
	private ErrorDAO errorDAO;
	@EJB
	private LabelDAO labelDAO;
	@EJB
	private DataDAO dataDAO;
	@EJB
	private SensorDAO sensorDAO;
	@EJB
	private StrategyDAO strategyDAO;
	@EJB
	private ExperimentDAO experimentDAO;
	@EJB
	private MonitoringRuleDAO monitoringRuleDAO;
	@EJB
	private AlertDAO alertDAO;
	@EJB
	private TypeDAO typeDAO;
	@EJB
	private GeolocationDataDAO geolocationDataDAO;
	@EJB
	private AnchorDAO anchorDAO;
	@EJB
	private CalibrationMeasureDAO calibrationMeasureDAO;
	@EJB
	private CalibrationDataDAO calibrationDataDAO;
	@EJB
	private MemberDAO memberDAO;
	@EJB
	private LogDAO logDAO;
	
	

	public LogDAO getLogDAO() {
		return logDAO;
	}

	public MemberDAO getMemberDAO() {
		return memberDAO;
	}

	public CalibrationMeasureDAO getCalibrationMeasureDAO() {
		return calibrationMeasureDAO;
	}

	public GeolocationDataDAO getGeolocationDataDAO() {
		return geolocationDataDAO;
	}

	public AlertDAO getAlertDAO() {
		return alertDAO;
	}

	public ExperimentDAO getExperimentDAO() {
		return experimentDAO;
	}

	public LabelDAO getLabelDAO() {
		return labelDAO;
	}

	public DataDAO getDataDAO() {
		return dataDAO;
	}

	public SensorDAO getSensorDAO() {
		return sensorDAO;
	}

	public StrategyDAO getStrategyDAO() {
		return strategyDAO;
	}

	public MoteDAO getMoteDAO() {
		return moteDAO;
	}

	public SenderDAO getSenderDAO() {
		return senderDAO;
	}

	public SinkDAO getSinkDAO() {
		return sinkDAO;
	}

	public FilterDAO getFilterDAO() {
		return filterDAO;
	}

	public ErrorDAO getErrorDAO() {
		return errorDAO;
	}

	public MonitoringRuleDAO getMonitoringRuleDAO() {
		return monitoringRuleDAO;
	}

	public TypeDAO getTypeDAO() {
		return typeDAO;
	}

	public AnchorDAO getAnchorDAO() {
		return anchorDAO;
	}

	public CalibrationDataDAO getCalibrationDataDAO() {
		return calibrationDataDAO;
	}

}
