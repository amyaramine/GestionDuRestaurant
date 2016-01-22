package com.ujm.xmltech.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Transaction implements Serializable {

	private static final long serialVersionUID = 8315057757268890401L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String IBANDeptor;

	private String BICDebtor;
	
	private String creditorName;
	
	private String debitorName;
	
	private boolean proceced = false ;
	
	private boolean transfer = false ;

	private String IBANCreditor;

	private String BICCreditor;
	
	@ManyToOne
	private Files file;
	
	private String endToEndId;
	
	private String seqTp;
	
	private long amount;
	
	private String mndtId;
	
	@Temporal(TemporalType.DATE)
	private Date dtOfSgntr;
	
	@Temporal(TemporalType.DATE)
	private Date ReqdColltnDt;
	

	public Date getDtOfSgntr() {
		return dtOfSgntr;
	}

	public void setDtOfSgntr(Date dtOfSgntr) {
		this.dtOfSgntr = dtOfSgntr;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIBANDeptor() {
		return IBANDeptor;
	}

	public void setIBANDeptor(String iBANDeptor) {
		IBANDeptor = iBANDeptor;
	}

	public String getBICDebtor() {
		return BICDebtor;
	}

	public void setBICDebtor(String bICDebtor) {
		BICDebtor = bICDebtor;
	}

	public boolean isProceced() {
		return proceced;
	}

	public void setProceced(boolean proceced) {
		this.proceced = proceced;
	}

	public boolean isTransfer() {
		return transfer;
	}

	public void setTransfer(boolean transfer) {
		this.transfer = transfer;
	}

	public String getIBANCreditor() {
		return IBANCreditor;
	}

	public void setIBANCreditor(String iBANCreditor) {
		IBANCreditor = iBANCreditor;
	}

	public String getBICCreditor() {
		return BICCreditor;
	}

	public void setBICCreditor(String bICCreditor) {
		BICCreditor = bICCreditor;
	}

	public Files getFile() {
		return file;
	}

	public void setFile(Files file) {
		this.file = file;
	}

	public String getEndToEndId() {
		return endToEndId;
	}

	public void setEndToEndId(String endToEndId) {
		this.endToEndId = endToEndId;
	}

	public String getSeqTp() {
		return seqTp;
	}

	public void setSeqTp(String seqTp) {
		this.seqTp = seqTp;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public String getMndtId() {
		return mndtId;
	}

	public void setMndtId(String mndtId) {
		this.mndtId = mndtId;
	}
	
	public String getCreditorName() {
		return creditorName;
	}

	public void setCreditorName(String creditorName) {
		this.creditorName = creditorName;
	}

	public String getDebitorName() {
		return debitorName;
	}

	public void setDebitorName(String debitorName) {
		this.debitorName = debitorName;
	}

	public Date getReqdColltnDt() {
		return ReqdColltnDt;
	}

	public void setReqdColltnDt(Date reqdColltnDt) {
		ReqdColltnDt = reqdColltnDt;
	}

}
