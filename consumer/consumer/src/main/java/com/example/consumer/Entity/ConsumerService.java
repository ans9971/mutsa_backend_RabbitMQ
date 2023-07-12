package com.example.consumer.Entity;

import com.example.consumer.dto.JobPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
// 이 클래스는 RabbitMQ의 Queue에 적재되는 메시지를 받아서 처리하기 위한 Queue이다.
//config에서 선언된 queue를 가져와서 사용
@RabbitListener(queues = "boot.amqp.worker-queue") //큐에 메시지 적재되면
@RequiredArgsConstructor
public class ConsumerService {


    private final JobRepository jobRepository;
    private final Gson gson;


    @RabbitHandler
    // message에 Queue에 담겼던 문자열이 전달된다.
    public void receive(String message) throws InterruptedException {
        // Message Broker로 부터 온 데이터 역직렬화
        JobPayload newJob
                = gson.fromJson(message, JobPayload.class);
        String jobId = newJob.getJobId();
        log.info("Received Job: {}", jobId);
        // Entity 검색
        Optional<JobEntity> optionalJob = jobRepository.findByJobId(jobId);
        // TODO 예외처리를 해줘야 마땅하나 잠시 생략

        // 요청을 처리상태로 업데이트
        JobEntity jobEntity = optionalJob.get();
        jobEntity.setStatus("PROCESSING");
        jobEntity = jobRepository.save(jobEntity);

        log.info("Start Processing Job: {}", jobId);
        // 처리하는데 시간이 5초 걸린다고 치자
        TimeUnit.SECONDS.sleep(5);

        // 요청을 완료 상태로 업데이트
        jobEntity.setStatus("DONE");
        jobEntity.setResultPath(
                String.format("/media/user-uploaded/processed/%s", newJob.getFilename())
        );
        jobRepository.save(jobEntity);
        // 기록
        log.info("Finished Job: {}", jobId);
    }
}
