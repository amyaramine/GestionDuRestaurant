package com.ujm.xmltech.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class HelloTasklet implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution step, ChunkContext context) throws Exception {

    System.out.println("The file is in files/common/out/ (from the project root)");
    return RepeatStatus.FINISHED;
  }

}
