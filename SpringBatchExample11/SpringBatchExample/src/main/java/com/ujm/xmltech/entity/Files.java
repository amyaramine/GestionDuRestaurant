package com.ujm.xmltech.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Files implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	
	@OneToMany(mappedBy="file",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Transaction> transaction;
	

	private String msgId;
	
	private String nameHeader;
	
	private String streetHeader;
	
	private String townHeader;
	
	private String country;
	
	private String email;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<Transaction> getTransaction() {
		return transaction;
	}

	public void setTransaction(List<Transaction> transaction) {
		this.transaction = transaction;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getNameHeader() {
		return nameHeader;
	}

	public void setNameHeader(String nameHeader) {
		this.nameHeader = nameHeader;
	}

	public String getStreetHeader() {
		return streetHeader;
	}

	public void setStreetHeader(String streetHeader) {
		this.streetHeader = streetHeader;
	}

	public String getTownHeader() {
		return townHeader;
	}

	public void setTownHeader(String townHeader) {
		this.townHeader = townHeader;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	
	
}
