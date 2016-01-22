package com.ujm.xmltech.tasklet;

import iso.std.iso._20022.tech.xsd.pain_008_001.CustomerDirectDebitInitiationV02;
import iso.std.iso._20022.tech.xsd.pain_008_001.DirectDebitTransactionInformation9;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ujm.xmltech.entity.Transaction;
import com.ujm.xmltech.utils.BankSimulationConstants;
import com.ujm.xmltech.utils.Banks;

public class Pain008Writer implements Tasklet {

	/**
	 * principal function to create a pain 008 for each bank
	 */
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
		
		//verification of global variable 
		if(ReadDataBase.cstmrDrctDbtInitn != null){
			for (Banks bank : Banks.values()) {
				for(CustomerDirectDebitInitiationV02 cstmrDrctDbtInitn :ReadDataBase.cstmrDrctDbtInitn.get(bank.toString())){ 
					if(cstmrDrctDbtInitn != null){
						write(cstmrDrctDbtInitn,bank.toString());
						System.out.println("File Pain008.xml Created For : "+bank.toString());
					}
				}
			}
		}
		else{
			System.out.println("No File to create ");
		}
		return RepeatStatus.FINISHED;
	}

	/**
	 * this function has object like parameter 
	 * create xml file frome object
	 * 
	 * @param item
	 * @param folder
	 */
	public void write(Object item, String folder) {
		//Added a random in order to have a different file each time
		File file = new File(BankSimulationConstants.OUT_DIRECTORY +folder+"/"+ "Pain008__ByABHM__to"+folder+"__"+ Math.random() + ".xml");
		OutputStream out;
		try {
			out = new FileOutputStream(file);
			OutputStreamWriter writer = new OutputStreamWriter(out);
			JAXBContext ctx = JAXBContext.newInstance(item.getClass());
			Marshaller marshaller = ctx.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty("jaxb.fragment", Boolean.TRUE);
			//writer file header
			String documentBase = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.008.001.02\">\n";
			writer.write(documentBase);
			//write header item
			writer.write(getXMLFragment(item, "CstmrDrctDbtInitn", marshaller) + "\n");
			//write footer
			String documentEnd = "</Document>";
			writer.write(documentEnd);
			writer.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Transform an object into xml string
	 * 
	 * @param object
	 * @param name
	 * @param marshaller
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String getXMLFragment(Object object, String name, Marshaller marshaller) {
		StringWriter writer = new StringWriter();
		try {
			marshaller.marshal(new JAXBElement(new QName("", name, ""), object.getClass(), object), writer);
		} catch (JAXBException e) {
			return null;
		}
		String originFragment = writer.toString();
		String fragment = originFragment.replaceAll("<" + name + ".*>", "<" + name + ">").replaceAll("<ns2:", "<").replaceAll("</ns2:", "</");
		fragment = fragment.replaceAll("&quot;", "\"").replaceAll("&apos;", "\'");

		return fragment;
	}

}
