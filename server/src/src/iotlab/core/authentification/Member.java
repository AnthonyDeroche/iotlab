/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.authentification;

import iotlab.core.beans.entity.JsonEncodable;

import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author Anthony Deroche
 *
 */
@Entity
@Table(name = "Member")
public class Member implements JsonEncodable {

	@Id
	private String username;
	private String password;
	private String email;
	private boolean admin;

	public Member() {

	}

	public Member(String username, String password, String email, boolean admin) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.admin = admin;
	}

	public Member(String username, String password, String email) {
		this(username, password, email, false);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return password;
	}

	public void setPasswordHash(String passwordHash) {
		this.password = passwordHash;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	@Override
	public JsonObject encode() {
		// TODO Auto-generated method stub
		return Json.createObjectBuilder().add("username", username)
				.add("email", email).add("admin", admin).build();
	}

}
