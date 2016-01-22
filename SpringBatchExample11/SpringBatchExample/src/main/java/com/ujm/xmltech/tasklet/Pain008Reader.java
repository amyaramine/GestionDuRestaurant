package com.ujm.xmltech.tasklet;

import iso.std.iso._20022.tech.xsd.pain_002_001.AccountIdentification4Choice;
import iso.std.iso._20022.tech.xsd.pain_002_001.ActiveOrHistoricCurrencyAndAmount;
import iso.std.iso._20022.tech.xsd.pain_002_001.AmountType3Choice;
import iso.std.iso._20022.tech.xsd.pain_002_001.BranchAndFinancialInstitutionIdentification4;
import iso.std.iso._20022.tech.xsd.pain_002_001.CashAccount16;
import iso.std.iso._20022.tech.xsd.pain_002_001.CustomerPaymentStatusReportV03;
import iso.std.iso._20022.tech.xsd.pain_002_001.FinancialInstitutionIdentification7;
import iso.std.iso._20022.tech.xsd.pain_002_001.GroupHeader36;
import iso.std.iso._20022.tech.xsd.pain_002_001.OriginalGroupInformation20;
import iso.std.iso._20022.tech.xsd.pain_002_001.OriginalPaymentInformation1;
import iso.std.iso._20022.tech.xsd.pain_002_001.OriginalTransactionReference13;
import iso.std.iso._20022.tech.xsd.pain_002_001.PartyIdentification32;
import iso.std.iso._20022.tech.xsd.pain_002_001.PaymentMethod4Code;
import iso.std.iso._20022.tech.xsd.pain_002_001.PaymentTransactionInformation25;
import iso.std.iso._20022.tech.xsd.pain_002_001.StatusReason6Choice;
import iso.std.iso._20022.tech.xsd.pain_002_001.StatusReasonInformation8;
import iso.std.iso._20022.tech.xsd.pain_002_001.TransactionGroupStatus3Code;
import iso.std.iso._20022.tech.xsd.pain_002_001.TransactionIndividualStatus3Code;
import iso.std.iso._20022.tech.xsd.pain_008_001.DirectDebitTransactionInformation9;
import iso.std.iso._20022.tech.xsd.pain_008_001.Document;
import iso.std.iso._20022.tech.xsd.pain_008_001.GroupHeader39;
import iso.std.iso._20022.tech.xsd.pain_008_001.ObjectFactory;
import iso.std.iso._20022.tech.xsd.pain_008_001.PaymentInstructionInformation4;

import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.ujm.xmltech.entity.Files;
import com.ujm.xmltech.entity.Transaction;
import com.ujm.xmltech.services.TransactionService;
import com.ujm.xmltech.utils.BankSimulationConstants;
import com.ujm.xmltech.utils.Banks;

public class Pain008Reader implements Tasklet {

	public static CustomerPaymentStatusReportV03 CstmrPmtStsRpt;

	@Autowired
	private TransactionService service;

	/**
	 * Principal function 
	 */
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
		if(Pain008Checker.DEBUGMODE){
			read((String) arg1.getStepContext().getJobParameters().get("inputFile"));
		}else{
			System.out.println("Fichier non conforme :(");
		}
		return RepeatStatus.FINISHED;
	}

	
	@SuppressWarnings("rawtypes")
	public Object read(String fileName) throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller u = jc.createUnmarshaller();
			File f = new File(BankSimulationConstants.WORK_DIRECTORY + fileName);
			FileReader fileReader = new FileReader(f);
			JAXBElement element = (JAXBElement) u.unmarshal(fileReader);
			Document document = (Document) element.getValue();

			//get value of number of transaction "chuckSum"
			int chuckSum = Integer.valueOf(document.getCstmrDrctDbtInitn().getGrpHdr().getNbOfTxs());

			//get value of total chuckSum
			int chuckSumTotalOfFile = Integer.valueOf(document.getCstmrDrctDbtInitn().getGrpHdr().getCtrlSum().toString());
			System.out.println("chuckSum = "+chuckSum);

			//get header of file pain.008
			GroupHeader39 header = document.getCstmrDrctDbtInitn().getGrpHdr();


			System.out.println("-----"+header.getMsgId());
			Iterator<PaymentInstructionInformation4> it = document.getCstmrDrctDbtInitn().getPmtInf().iterator();

			int i = 0;
			int chuckSumTotal = 0;
			boolean persist = true;
			boolean persist1 = true;
			boolean existe = false;


			String sourceError;

			/*------------------------------verification of msgId------------------------------*/ 
			Transaction foundMsgId = service.findTransactionByMsgId(header.getMsgId().toString());
			if(foundMsgId != null){
				System.out.println("The file already exists in the database");
				existe = true;
			}




			if(!existe){
				/*---------------------------verification of chuckSum--------------------------*/
				while (it.hasNext()) {
					//collection
					PaymentInstructionInformation4 transaction = it.next();
					for(DirectDebitTransactionInformation9 tr :transaction.getDrctDbtTxInf()){
						i++;
						chuckSumTotal = chuckSumTotal + tr.getInstdAmt().getValue().intValue();
					}
				}

				/*---------change values of persiste1 if chuckSum are not ---------*/
				if(chuckSum != i || chuckSumTotalOfFile != chuckSumTotal ){
					System.out.println("checkSum different from the number of transactions " + i);
					persist1 = false;
				}

				if(persist1){
					/*------------------------read file and persist*-----------------------------*/
					it = document.getCstmrDrctDbtInitn().getPmtInf().iterator();

					//list of transaction 
					List<Transaction> transactions = new ArrayList<Transaction>(); 
					Files file = new Files();

					file.setMsgId(header.getMsgId());
					file.setNameHeader(header.getInitgPty().getNm());
					file.setStreetHeader(header.getInitgPty().getPstlAdr().getStrtNm());
					file.setTownHeader(header.getInitgPty().getPstlAdr().getTwnNm());
					file.setCountry(header.getInitgPty().getPstlAdr().getCtry());
					file.setEmail(header.getInitgPty().getCtctDtls().getEmailAdr());


					/*-------------------------------------------------------------------pain002------------------------------------------------------------*/
					CstmrPmtStsRpt = new CustomerPaymentStatusReportV03();
					GroupHeader36 GrpHdr = new GroupHeader36();
					OriginalGroupInformation20 OrgnlGrpInfAndSts = new OriginalGroupInformation20();
					OriginalPaymentInformation1 OrgnlPmtInfAndSts = new OriginalPaymentInformation1();
					PaymentTransactionInformation25 TxInfAndSts = new PaymentTransactionInformation25() ;
					StatusReasonInformation8 StsRsnInf = new StatusReasonInformation8();

					//set MsgId of pain002
					GrpHdr.setMsgId("eraseddrgsrg"+Math.random());
					try{
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
						sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
						String date = sdf.format(new Date());
						XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(date);
						GrpHdr.setCreDtTm(xmlCal);
					}
					catch(DatatypeConfigurationException ex) {
						Logger.getLogger(ReadDataBase.class.getName()).log(Level.SEVERE, null, ex);
					}

					CstmrPmtStsRpt.setGrpHdr(GrpHdr);


					//set original msgId and name MsgID
					OrgnlGrpInfAndSts.setOrgnlMsgId(file.getMsgId());
					OrgnlGrpInfAndSts.setOrgnlMsgNmId("PAIN.008.001.02");
					CstmrPmtStsRpt.setOrgnlGrpInfAndSts(OrgnlGrpInfAndSts);

					/*-------------------------------------------------------------------------------------------------------------------------------*/

					while (it.hasNext()) {
						
						//collection
						PaymentInstructionInformation4 transaction = it.next();

						//set PmtInfId in pain002
						OrgnlPmtInfAndSts.setOrgnlPmtInfId(transaction.getPmtInfId());

						OrgnlPmtInfAndSts.setOrgnlNbOfTxs(header.getNbOfTxs());

						OrgnlPmtInfAndSts.setOrgnlCtrlSum(header.getCtrlSum());

						OrgnlPmtInfAndSts.setPmtInfSts(TransactionGroupStatus3Code.PART);

						String error = new String();
						String errorReason = new String();

						for(DirectDebitTransactionInformation9 tr :transaction.getDrctDbtTxInf()){
							
							Transaction t = new Transaction();

							/*--------get values from file---------*/
							t.setEndToEndId(tr.getPmtId().getEndToEndId());
							t.setAmount(tr.getInstdAmt().getValue().longValue());
							t.setMndtId(tr.getDrctDbtTx().getMndtRltdInf().getMndtId());
							t.setDtOfSgntr(tr.getDrctDbtTx().getMndtRltdInf().getDtOfSgntr().toGregorianCalendar().getTime());
							t.setIBANDeptor(tr.getDbtrAcct().getId().getIBAN());
							t.setBICDebtor(tr.getDbtrAgt().getFinInstnId().getBIC());
							t.setSeqTp(tr.getPmtTpInf().getSeqTp().toString());
							t.setIBANCreditor(transaction.getCdtrAcct().getId().getIBAN());
							t.setBICCreditor(transaction.getCdtrAgt().getFinInstnId().getBIC());
							t.setCreditorName(transaction.getCdtr().getNm());
							t.setReqdColltnDt(transaction.getReqdColltnDt().toGregorianCalendar().getTime());
							t.setDebitorName(tr.getDbtr().getNm());
							t.setFile(file);

							/*-----------------------RJC000-------------------------*/	
							String BIC = tr.getDbtrAgt().getFinInstnId().getBIC();
							String bank = BIC.substring(0, 4);

							for ( Banks bankExistes : Banks.values() ) {
								persist=false; 
								System.out.println("bankExistes : " + bankExistes.toString() + " / " +  bank);
								if ( bank.equals(bankExistes.toString()) ) {
									System.out.println("The account debtor belongs to a simulated bank by one of the groups");
									persist = true;
									break;
								}
							}
							if(persist == false){
								error = "RJC000";
								errorReason ="The account debtor doesn't belong to a simulated bank by one of the groups";
							}

							/*-----------------------RJC001---------------------------*/
							Long amount=tr.getInstdAmt().getValue().longValue();
							if(amount<1.0){
								System.out.println("The amount is less then 1.0 Euro");
								persist=false;  
								error = "RJC001";
								errorReason ="Amount less than 1 Euro";
							}

							/*-----------------------RJC002-------------------------*/	
							Long amount2=tr.getInstdAmt().getValue().longValue();
							if(amount2>10000.0){
								System.out.println("The amount 10000 Euro");
								persist=false;  
								error = "RJC002";
								errorReason ="Amount exceed 10000 Euro";
							}	

							/*-----------------------RJC003----------------------*/				  
							String currency = tr.getInstdAmt().getCcy().toString();
							if(!(currency.equals("EUR"))){
								System.out.println("Amount in currency other than the Euro");
								persist=false;  
								error = "RJC003";
								errorReason ="Amount in currency other than the Euro";
							}
							/*-----------------------RJC005-------------------------*/	
							GregorianCalendar dateOfSignature = new GregorianCalendar();
							GregorianCalendar grgrnCldr = new GregorianCalendar();

							dateOfSignature = tr.getDrctDbtTx().getMndtRltdInf().getDtOfSgntr().toGregorianCalendar();
							Date today = grgrnCldr.getTime();
							int diffYears = today.getYear() - dateOfSignature.getTime().getYear();
							if(diffYears > 0) {
								int diffMonth = (diffYears*12-dateOfSignature.getTime().getMonth()) + today.getMonth();
								if(diffMonth > 13) {
									System.out.println("rejected date of transaction outdates 13 months");
									persist=false;
									error = "RJC005";
									errorReason ="SUP MONTHS 13";
								}

								else if(diffMonth == 13) {
									//System.out.println("day date of signature : " + dateOfSignature.get(GregorianCalendar.DAY_OF_MONTH) + " today " + grgrnCldr.get(GregorianCalendar.DAY_OF_MONTH));
									int diffDays = dateOfSignature.get(GregorianCalendar.DAY_OF_MONTH) - grgrnCldr.get(GregorianCalendar.DAY_OF_MONTH);

									if(diffDays < 0) {
										System.out.println("rejected date of transaction outdates 13 months");
										persist=false;
										error = "RJC005";
										errorReason ="SUP MONTHS 13";
									}
								}
							}

							/*-----------------------RJC006--------------------------*/
							Date datePrelevement = transaction.getReqdColltnDt().toGregorianCalendar().getTime();
							Date current = new Date();

							long diffD= ((datePrelevement.getTime() - current.getTime())/(24 * 60 * 60 * 1000));
							System.out.println("Date of prelevement - current day : "+diffD+" days");
							String seqTpType=tr.getPmtTpInf().getSeqTp().toString();

							if( seqTpType.equals("RCUR") && diffD < 2){
								//System.out.println("current : "+current+" datePrelevement : "+datePrelevement+" seqTpType = "+seqTpType);
								System.out.println("seqTpType = RCUR && Date of prelevement  - current day < 2");
								persist=false;
								error = "RJC006";
								errorReason ="PROBLEM DATE";
							}
							/*-----------------------RJC007------------------------*/				  
							if( seqTpType.equals("FRST") && diffD < 5){
								System.out.println("seqTpType = FRST && date pr�levement - current day < 5");
								persist=false;
								error = "RJC007";
								errorReason ="INF 5 DAYS";
							}

							/*-----------------------RJC008-------------------------*/
							if( seqTpType.equals("RCUR")){
								String mondat = tr.getDrctDbtTx().getMndtRltdInf().getMndtId().toString();

								Transaction found = service.findTransactionByMndtId(mondat);
								if(found == null){
									System.out.println("RJC008");
									persist=false;
									error = "RJC008";
									errorReason ="MNDT NOT EXIST";
								}
							}

							/*----------verify if  we have a problem or no--------*/
							if(persist && persist1){
								System.out.println("yes adding this element into the list succes ");
								System.out.println();

								//add transaction to list transactions
								transactions.add(t);
							}
							else{

								TxInfAndSts.setOrgnlInstrId("A"+Math.random());
								TxInfAndSts.setOrgnlEndToEndId(t.getEndToEndId());
								TxInfAndSts.setTxSts(TransactionIndividualStatus3Code.RJCT);

								StatusReason6Choice Rsn = new StatusReason6Choice();
								OriginalTransactionReference13 OrgnlTxRef = new OriginalTransactionReference13();
								AmountType3Choice Amt = new AmountType3Choice();
								ActiveOrHistoricCurrencyAndAmount InstdAmt = new ActiveOrHistoricCurrencyAndAmount();
								PartyIdentification32 Dbtr = new PartyIdentification32();
								CashAccount16 DbtrAcct = new CashAccount16();
								AccountIdentification4Choice Id = new AccountIdentification4Choice();
								BranchAndFinancialInstitutionIdentification4 DbtrAgt = new BranchAndFinancialInstitutionIdentification4();
								FinancialInstitutionIdentification7 FinInstnId = new FinancialInstitutionIdentification7();

								Rsn.setCd(error);
								StsRsnInf.setRsn(Rsn);

								try{
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
									sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
									String date = sdf.format(new Date());
									XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(date);
									TxInfAndSts.setAccptncDtTm(xmlCal);
								}catch(DatatypeConfigurationException ex) {
									Logger.getLogger(ReadDataBase.class.getName()).log(Level.SEVERE, null, ex);
								}

								InstdAmt.setCcy("EUR");
								InstdAmt.setValue(BigDecimal.valueOf(t.getAmount()));
								Amt.setInstdAmt(InstdAmt);
								OrgnlTxRef.setAmt(Amt);

								try{
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
									sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
									String date = sdf.format(t.getReqdColltnDt());
									XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(date);
									OrgnlTxRef.setReqdColltnDt(xmlCal);
								}catch(DatatypeConfigurationException ex) {
									Logger.getLogger(ReadDataBase.class.getName()).log(Level.SEVERE, null, ex);
								}

								OrgnlTxRef.setPmtMtd(PaymentMethod4Code.DD);

								Dbtr.setNm(t.getDebitorName());

								Id.setIBAN(t.getIBANDeptor());
								DbtrAcct.setId(Id);

								FinInstnId.setBIC(t.getBICDebtor());
								DbtrAgt.setFinInstnId(FinInstnId);

								OrgnlTxRef.setDbtrAgt(DbtrAgt);
								OrgnlTxRef.setDbtrAcct(DbtrAcct);
								OrgnlTxRef.setDbtr(Dbtr);

								TxInfAndSts.setOrgnlTxRef(OrgnlTxRef);

								OrgnlPmtInfAndSts.getTxInfAndSts().add(TxInfAndSts);

								System.out.println("Sorry ! cannot add this element into the list ");
								System.out.println();
							}
						}
						StsRsnInf.getAddtlInf().add(errorReason);
						TxInfAndSts.getStsRsnInf().add(StsRsnInf);
						CstmrPmtStsRpt.getOrgnlPmtInfAndSts().add(OrgnlPmtInfAndSts);
					}
					file.setTransaction(transactions);
					service.createTransaction(file);
					return document.getCstmrDrctDbtInitn();
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return RepeatStatus.FINISHED;
	}

}

