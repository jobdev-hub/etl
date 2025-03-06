package com.jobdev.dataharvest.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobdev.dataharvest.enums.JobName;
import com.jobdev.dataharvest.service.JobSyncWorkService;
import com.jobdev.dataharvest.util.ThreadUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
public class JobController {
    private final JobSyncWorkService jobSyncWorkService;
    private final JobExplorer jobExplorer;

    @PostMapping("/sync-works")
    public ResponseEntity<String> syncWorks(
            @RequestParam(defaultValue = "programming") String subject,
            @RequestParam(defaultValue = "1000") int batchSize) {

        ThreadUtil.runAsync(() -> jobSyncWorkService.syncWorks(subject, batchSize));
        return ResponseEntity.ok("Sincronização iniciada para " + subject + " (lotes de " + batchSize + ")");
    }

    @GetMapping("/status")
    public ResponseEntity<List<Map<String, Object>>> getJobStatus() {
        Set<JobExecution> executions = jobExplorer.findRunningJobExecutions(JobName.SYNC_WORK.getName());
        List<Map<String, Object>> result = executions.stream()
                .map(execution -> {
                    Map<String, Object> jobInfo = new HashMap<>();
                    jobInfo.put("jobId", execution.getJobId());
                    jobInfo.put("startTime", execution.getStartTime());
                    jobInfo.put("status", execution.getStatus());
                    jobInfo.put("exitStatus", execution.getExitStatus().getExitCode());

                    // Obtenha métricas das etapas
                    List<StepExecution> steps = new ArrayList<>(execution.getStepExecutions());
                    List<Map<String, Object>> stepMetrics = steps.stream()
                            .map(step -> {
                                Map<String, Object> metrics = new HashMap<>();
                                metrics.put("stepName", step.getStepName());
                                metrics.put("readCount", step.getReadCount());
                                metrics.put("writeCount", step.getWriteCount());
                                metrics.put("skipCount", step.getSkipCount());
                                return metrics;
                            })
                            .collect(Collectors.toList());

                    jobInfo.put("steps", stepMetrics);
                    return jobInfo;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
