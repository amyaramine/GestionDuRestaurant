package com.ujm.xmltech.tasklet;

import java.io.File;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ujm.xmltech.utils.BankSimulationConstants;

public class MoveToArchive implements Tasklet {

	/**
	 * principal function to Move To Archive
	 * 
	 */
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
		System.out.println("move to archive");
		String input = (String) arg1.getStepContext().getJobParameters().get("inputFile");
		System.out.println("file to move " + input);
		File file = new File(BankSimulationConstants.WORK_DIRECTORY + input);
		boolean moved = file.renameTo(new File(BankSimulationConstants.ARCHIVE_DIRECTORY + input));
		System.out.println("file moved : " + moved + " in [" + file.getAbsolutePath() + "]");
		return RepeatStatus.FINISHED;
	}

}
