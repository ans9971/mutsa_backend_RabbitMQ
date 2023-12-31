package com.example.producer.dto;

import com.example.producer.jpa.JobEntity;
import lombok.Data;

@Data
public class JobStatus {
    private String jobId;
    private String status;
    private String resultPath;

    public static JobStatus fromEntity(JobEntity jobEntity) {
        JobStatus jobStatus = new JobStatus();
        jobStatus.setJobId(jobEntity.getJobId());
        jobStatus.setStatus(jobEntity.getStatus());
        jobStatus.setResultPath(jobEntity.getResultPath());
        return jobStatus;
    }

}
