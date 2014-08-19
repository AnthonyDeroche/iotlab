/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.beans.entity;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
@Entity
@Table(name = "Error")
public class Error {
	
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private int id;
	private Date date;
	private String title;
	private String message;
	
	public Error(){
		
	}
	
	public Error(String title, String message){
		this.title=title;
		this.message=message;
		this.date = new Date(new java.util.Date().getTime());
	}
	
	public int getId() {
		return id;
	}
	public Date getDate() {
		return date;
	}
	public String getTitle() {
		return title;
	}
	public String getMessage() {
		return message;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
