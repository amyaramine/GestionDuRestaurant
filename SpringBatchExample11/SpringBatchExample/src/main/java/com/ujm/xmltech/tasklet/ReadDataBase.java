package com.ujm.xmltech.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ujm.xmltech.entity.Files;
import com.ujm.xmltech.entity.Transaction;
import com.ujm.xmltech.utils.Banks;

import iso.std.iso._20022.tech.xsd.pain_008_001.AccountIdentification4Choice;
import iso.std.iso._20022.tech.xsd.pain_008_001.ActiveOrHistoricCurrencyAndAmount;
import iso.std.iso._20022.tech.xsd.pain_008_001.BranchAndFinancialInstitutionIdentification4;
import iso.std.iso._20022.tech.xsd.pain_008_001.CashAccount16;
import iso.std.iso._20022.tech.xsd.pain_008_001.ContactDetails2;
import iso.std.iso._20022.tech.xsd.pain_008_001.CustomerDirectDebitInitiationV02;
import iso.std.iso._20022.tech.xsd.pain_008_001.DirectDebitTransaction6;
import iso.std.iso._20022.tech.xsd.pain_008_001.DirectDebitTransactionInformation9;
import iso.std.iso._20022.tech.xsd.pain_008_001.FinancialInstitutionIdentification7;
import iso.std.iso._20022.tech.xsd.pain_008_001.GroupHeader39;
import iso.std.iso._20022.tech.xsd.pain_008_001.MandateRelatedInformation6;
import iso.std.iso._20022.tech.xsd.pain_008_001.PartyIdentification32;
import iso.std.iso._20022.tech.xsd.pain_008_001.PaymentIdentification1;
import iso.std.iso._20022.tech.xsd.pain_008_001.PaymentInstructionInformation4;
import iso.std.iso._20022.tech.xsd.pain_008_001.PaymentMethod2Code;
import iso.std.iso._20022.tech.xsd.pain_008_001.PaymentTypeInformation20;
import iso.std.iso._20022.tech.xsd.pain_008_001.PostalAddress6;
import iso.std.iso._20022.tech.xsd.pain_008_001.SequenceType1Code;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;

import com.ujm.xmltech.services.TransactionService;

public class ReadDataBase implements Tasklet {


	public static HashMap<String, ArrayList<CustomerDirectDebitInitiationV02>> cstmrDrctDbtInitn;

	@Autowired
	private TransactionService service;

	
	 
	/**
	 * principal function to create cstmrDrctDbtInitn object 
	 */
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		//check all transaction whit transaction's value equal to true
		List<Transaction> transaction = service.findTransactionByProces();

		//This update don't work 
		for(Transaction transactionLoop : transaction){
			service.updatePrececed(transactionLoop);
		}

		//create a cstmrDrctDbtInitn Object
		if(transaction != null && !transaction.isEmpty()){
			cstmrDrctDbtInitn = cstmrDrctDbtInitnForEachBank(transaction);
		}
		else
		{
			System.out.println("New Transaction not found ");
		}


		System.out.println("################ Read Data base succes #################");
		return RepeatStatus.FINISHED;
	}


	/**
	 * This function create header of payment information of pain008
	 * 
	 * @param Transaction
	 * @return PaymentInstructionInformation4
	 */
	public PaymentInstructionInformation4 creatPmtInfHeader(Transaction t){
		PaymentInstructionInformation4 PmtInf = new PaymentInstructionInformation4();

		//PmtInfId
		PmtInf.setPmtInfId("1");

		//set PmtMtd
		PmtInf.setPmtMtd(PaymentMethod2Code.DD);


		//set ReqdColltnDt
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			String date = sdf.format(t.getReqdColltnDt());
			XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(date);
			PmtInf.setReqdColltnDt(xmlCal);
		}catch(DatatypeConfigurationException ex) {
			Logger.getLogger(ReadDataBase.class.getName()).log(Level.SEVERE, null, ex);
		}


		//set name creditor 
		PartyIdentification32 Cdtr = new PartyIdentification32();
		Cdtr.setNm(t.getCreditorName());
		PmtInf.setCdtr(Cdtr);


		//creditor IBAN
		CashAccount16 CdtrAcct = new CashAccount16();
		AccountIdentification4Choice Id = new AccountIdentification4Choice();
		Id.setIBAN(t.getIBANCreditor());
		CdtrAcct.setId(Id);
		PmtInf.setCdtrAcct(CdtrAcct);

		//creditor BIC
		BranchAndFinancialInstitutionIdentification4 CdtrAgt = new BranchAndFinancialInstitutionIdentification4();
		FinancialInstitutionIdentification7 FinInstnId = new FinancialInstitutionIdentification7();
		FinInstnId.setBIC(t.getBICCreditor());
		CdtrAgt.setFinInstnId(FinInstnId);
		PmtInf.setCdtrAgt(CdtrAgt);

		return PmtInf;
	}


	/**
	 * This function create Hash Map of String and Customer Direct Debit Initiation 
	 * example : <ABHM,  CustomerDirectDebitInitiationV02 of ABHM>
	 * 
	 * @param List of Transactions
	 * @return HashMap<String, ArrayList<CustomerDirectDebitInitiationV02>>
	 */

	public HashMap<String, ArrayList<CustomerDirectDebitInitiationV02>> cstmrDrctDbtInitnForEachBank(List<Transaction> transactions){

		HashMap<String, ArrayList<DirectDebitTransactionInformation9>> myHashmapDrctDbtTxInf = null;
		HashMap<String, ArrayList<CustomerDirectDebitInitiationV02>> myHashmapCstmrDrctDbtInitn = null;

		//Initialization of all myHashmap
		myHashmapDrctDbtTxInf = new HashMap<String, ArrayList<DirectDebitTransactionInformation9>>();
		myHashmapCstmrDrctDbtInitn = new HashMap<String, ArrayList<CustomerDirectDebitInitiationV02>>();

		for (Banks bank : Banks.values()) {
			myHashmapDrctDbtTxInf.put(bank.toString(), new ArrayList<DirectDebitTransactionInformation9>());
			myHashmapCstmrDrctDbtInitn.put(bank.toString(), new ArrayList<CustomerDirectDebitInitiationV02>());
		}

		/*---------------------DrctDbtTxInf----------------------*/
		//add DrctDbtTxInf to myHashmapDrctDbtTxInf
		for(Transaction transaction : transactions){
			DirectDebitTransactionInformation9 DrctDbtTxInf = creatDrctDbtTxInf(transaction);
			myHashmapDrctDbtTxInf.get(transaction.getBICDebtor().substring(0, 4)).add(DrctDbtTxInf);
		}

		//get list of DrctDbtTxInf
		ArrayList<DirectDebitTransactionInformation9> DrctDbtTxInfList;
		for (Banks bank : Banks.values()) {
			DrctDbtTxInfList = myHashmapDrctDbtTxInf.get(bank.toString());

			int numberTransaction = 0;
			long chuckSum = 0;

			Transaction tran = transactions.get(1);

			PaymentInstructionInformation4 PmtInf = new PaymentInstructionInformation4();
			PmtInf = creatPmtInfHeader(tran);

			System.out.println("Bank : "+bank.toString()+" : --------------------");
			for(DirectDebitTransactionInformation9 DrctDbtTxInf : DrctDbtTxInfList){
				PmtInf.getDrctDbtTxInf().add(DrctDbtTxInf);
				numberTransaction++;
				chuckSum = chuckSum + DrctDbtTxInf.getInstdAmt().getValue().longValue();
			}

			if(!DrctDbtTxInfList.isEmpty()){

				String NbOfTxs = Integer.toString(numberTransaction);
				BigDecimal CtrlSum = BigDecimal.valueOf(chuckSum);
				CustomerDirectDebitInitiationV02 CstmrDrctDbtInitn = new CustomerDirectDebitInitiationV02();

				CstmrDrctDbtInitn.setGrpHdr(creatGrpHdr(tran.getFile(),NbOfTxs,CtrlSum));

				CstmrDrctDbtInitn.getPmtInf().add(PmtInf);	
				System.out.println("CstmrDrctDbtInitn msgId : "+CstmrDrctDbtInitn.getGrpHdr().getMsgId());

				myHashmapCstmrDrctDbtInitn.get(bank.toString()).add(CstmrDrctDbtInitn);
			}
		}

		return myHashmapCstmrDrctDbtInitn;
	}


	/**
	 * This function create a Direct Debit Transaction Information for each transaction
	 * 
	 * @param Transaction
	 * @return DirectDebitTransactionInformation9
	 */
	public DirectDebitTransactionInformation9 creatDrctDbtTxInf(Transaction t){

		DirectDebitTransactionInformation9 tr = new DirectDebitTransactionInformation9();

		//EndToEndId
		PaymentIdentification1 endToEndId = new PaymentIdentification1();
		endToEndId.setEndToEndId(t.getEndToEndId());
		tr.setPmtId(endToEndId);

		//SeqTp
		PaymentTypeInformation20 pmtTpInf = new PaymentTypeInformation20();
		if(t.getSeqTp().equals("FRST")){
			pmtTpInf.setSeqTp(SequenceType1Code.FRST);
			tr.setPmtTpInf(pmtTpInf);
		}
		else if(t.getSeqTp().equals("RCUR"))
		{
			pmtTpInf.setSeqTp(SequenceType1Code.RCUR);
			tr.setPmtTpInf(pmtTpInf);
		}
		else
		{
			System.out.println("SeqTp diff�rent de FRST ou RCUR :(");
		}

		//Ccy="EUR" & Amount
		ActiveOrHistoricCurrencyAndAmount InstdAmt = new ActiveOrHistoricCurrencyAndAmount();
		InstdAmt.setCcy("EUR");
		InstdAmt.setValue(BigDecimal.valueOf(t.getAmount()));
		tr.setInstdAmt(InstdAmt);

		//MndtId & Date Of Signature 
		DirectDebitTransaction6 DrctDbtTx = new DirectDebitTransaction6();
		MandateRelatedInformation6 MndtRltdInf = new MandateRelatedInformation6();
		MndtRltdInf.setMndtId(t.getMndtId());

		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			String date = sdf.format(t.getDtOfSgntr());
			XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(date);
			MndtRltdInf.setDtOfSgntr(xmlCal);

		}catch(DatatypeConfigurationException ex) {
			Logger.getLogger(ReadDataBase.class.getName()).log(Level.SEVERE, null, ex);
		}
		DrctDbtTx.setMndtRltdInf(MndtRltdInf);
		tr.setDrctDbtTx(DrctDbtTx);

		//BIC Debitor
		BranchAndFinancialInstitutionIdentification4 DbtrAgt = new BranchAndFinancialInstitutionIdentification4();
		FinancialInstitutionIdentification7 FinInstnId = new FinancialInstitutionIdentification7();
		FinInstnId.setBIC(t.getBICDebtor());
		DbtrAgt.setFinInstnId(FinInstnId);
		tr.setDbtrAgt(DbtrAgt);


		//name Debitor
		PartyIdentification32 Dbtr = new PartyIdentification32();
		Dbtr.setNm(t.getDebitorName());
		tr.setDbtr(Dbtr);

		//IBAN Debitor
		CashAccount16 DbtrAcct = new CashAccount16();
		AccountIdentification4Choice Id = new AccountIdentification4Choice();
		Id.setIBAN(t.getIBANDeptor());
		DbtrAcct.setId(Id);
		tr.setDbtrAcct(DbtrAcct);


		return tr;
	}


	/**
	 * This function create a Group Header for each file pain 008
	 * NbOfTxs is 
	 * 
	 * @param Files
	 * @param String 
	 * @param BigDecimal
	 * @return GroupHeader39
	 */
	private GroupHeader39 creatGrpHdr (Files file, String NbOfTxs, BigDecimal CtrlSum) {

		String myMsgId = new String();
		myMsgId = "myGeneratedFile"+Math.random();
		XMLGregorianCalendar xmlGregorianCalendar = null;
		GroupHeader39 grpHdr = null;
		PartyIdentification32 initgPty = null;
		PostalAddress6 pstlAdr = null;
		ContactDetails2 ctctDtls = null;

		try {
			GregorianCalendar calendar = new GregorianCalendar();
			xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
		} catch (DatatypeConfigurationException ex) {
			Logger.getLogger(ReadDataBase.class.getName()).log(Level.SEVERE, null, ex);
		}

		//creation of postal address of writer of pain 008
		pstlAdr = new PostalAddress6();
		pstlAdr.setStrtNm(file.getStreetHeader());
		pstlAdr.setTwnNm(file.getTownHeader());
		pstlAdr.setCtry(file.getCountry());

		//create name and email of writer of pain 008
		ctctDtls = new ContactDetails2();
		ctctDtls.setNm(file.getNameHeader());
		ctctDtls.setEmailAdr(file.getEmail());

		//creation of global information of writer of pain 008
		initgPty = new PartyIdentification32();
		initgPty.setNm(file.getNameHeader());
		initgPty.setPstlAdr(pstlAdr);
		initgPty.setCtctDtls(ctctDtls);

		//creation of group header and set random id of message
		grpHdr = new GroupHeader39(); 
		grpHdr.setMsgId(myMsgId);

		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			String date = sdf.format(new Date());
			XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(date);
			grpHdr.setCreDtTm(xmlCal);
		}catch(DatatypeConfigurationException ex) {
			Logger.getLogger(ReadDataBase.class.getName()).log(Level.SEVERE, null, ex);
		}
		grpHdr.setNbOfTxs(NbOfTxs);
		grpHdr.setCtrlSum(CtrlSum);
		grpHdr.setInitgPty(initgPty);


		return grpHdr;
	}

}
