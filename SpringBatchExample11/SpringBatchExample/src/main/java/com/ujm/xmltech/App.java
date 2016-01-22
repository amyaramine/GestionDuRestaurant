package com.ujm.xmltech;

import java.io.File;
import java.util.Calendar;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ujm.xmltech.utils.BankSimulationConstants;

public class App {

    
    public void launch() {
        File input = retrieveFileToProcess();
        if (input != null) {
            String[] springConfig = { "spring/batch/jobs/jobs.xml" };
            ApplicationContext context = new ClassPathXmlApplicationContext(springConfig);
            JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
            Job job = (Job) context.getBean("integratePain008File");
            try {
                JobExecution execution = jobLauncher.run(job, new JobParametersBuilder().addString("inputFile", input.getName()).toJobParameters());
                System.out.println("Exit Status : " + execution.getStatus());
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (context != null)
                    ((AbstractApplicationContext) context).close();       
        }
        } else {
            System.out.println("[" + Calendar.getInstance().getTime().toString() + "] No file to process !!!");
        }
    }

    private File retrieveFileToProcess() {
        File toReturn = null;
        File folder = new File(BankSimulationConstants.IN_DIRECTORY);
        for (File file : folder.listFiles()) {
            System.out.println("File found name : " + file.getName());
            toReturn = file;
        }
        return toReturn;
    }
    
    
    public void launchReturnFile() {
    	
            String[] springConfig = { "spring/batch/jobs/jobs.xml" };
            ApplicationContext context = new ClassPathXmlApplicationContext(springConfig);
            JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
            Job job = (Job) context.getBean("writePain008File");
            try {
                JobExecution execution = jobLauncher.run(job, new JobParametersBuilder().toJobParameters());
                System.out.println("Exit Status : " + execution.getStatus());
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (context != null)
                    ((AbstractApplicationContext) context).close();       
        }
    }

   
}
