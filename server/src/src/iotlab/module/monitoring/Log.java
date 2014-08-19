/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.monitoring;

import iotlab.core.authentification.Member;
import iotlab.core.beans.entity.JsonEncodable;

import java.sql.Timestamp;

import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 
 * @author Anthony Deroche
 *
 */
@Entity
@Table(name = "Log")
public class Log implements JsonEncodable {

	public static final int LEVEL_INFO = 0;
	public static final int LEVEL_WARNING = 1;
	public static final int LEVEL_SEVERE = 2;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "member_username", referencedColumnName = "username")
	private Member member;

	private String module;

	private String message;

	private int level;

	private Timestamp datetime;

	private String ip;

	public Log() {

	}

	public Log(Member member, String module, String message, int level,
			String ip) {
		this.member = member;
		this.module = module;
		this.message = message;
		this.level = level;
		this.datetime = new Timestamp(System.currentTimeMillis());
		this.ip = ip!=null ? ip : "";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Timestamp getDatetime() {
		return datetime;
	}

	public void setDatetime(Timestamp datetime) {
		this.datetime = datetime;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Override
	public JsonObject encode() {
		// TODO Auto-generated method stub
		String user = member != null ? member.getUsername() : "guest";
		return Json.createObjectBuilder().add("member", user)
				.add("module", module).add("message", message)
				.add("level", level).add("datetime", datetime.toString())
				.add("ip", ip).build();
	}

	public String toString() {
		String lvl = "";
		if (level == Log.LEVEL_INFO)
			lvl = "[INFO]";
		else if (level == Log.LEVEL_WARNING)
			lvl = "[WARNING]";
		else if (level == Log.LEVEL_SEVERE)
			lvl = "[SEVERE]";

		String user = member != null ? "(" + member.getUsername() + ")" : "guest";

		return lvl + " {" + module +" "+ user + "}  " + message;
	}

}
