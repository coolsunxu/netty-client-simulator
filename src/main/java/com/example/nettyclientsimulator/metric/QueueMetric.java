package com.example.nettyclientsimulator.metric;

import com.example.nettyclientsimulator.disruptor.impl.DisruptorReadQueueImpl;
import com.example.nettyclientsimulator.disruptor.impl.DisruptorWriteQueueImpl;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class QueueMetric {

    private final PrometheusMeterRegistry registry;

    private final DisruptorWriteQueueImpl disruptorWriteQueue;

    private final DisruptorReadQueueImpl disruptorReadQueue;


    public QueueMetric(PrometheusMeterRegistry registry, DisruptorWriteQueueImpl disruptorWriteQueue,
                       DisruptorReadQueueImpl disruptorReadQueue) {
        this.registry = registry;
        this.disruptorWriteQueue = disruptorWriteQueue;
        this.disruptorReadQueue = disruptorReadQueue;
    }

    private static final String SERVICE_QUEUE_REMAINING_CAPACITY = "service.queue.remaining.capacity";

    private static final String QUEUE_NAME = "queue.name";
    private static final String INDEX_NAME = "index.name";

    @PostConstruct
    public void init() {

        for (int i = 0; i < this.disruptorReadQueue.getQueueSize(); i++) {
            int finalI = i;
            Gauge.builder(SERVICE_QUEUE_REMAINING_CAPACITY, this.disruptorReadQueue, e->e.getRemainingCapacity(finalI))
                    .tags(QUEUE_NAME, "readQueue")
                    .tags(INDEX_NAME, String.valueOf(finalI))
                    .register(registry);
        }

        for (int i = 0; i < this.disruptorWriteQueue.getQueueSize(); i++) {
            int finalI = i;
            Gauge.builder(SERVICE_QUEUE_REMAINING_CAPACITY, this.disruptorReadQueue, e->e.getRemainingCapacity(finalI))
                    .tags(QUEUE_NAME, "writeQueue")
                    .tags(INDEX_NAME, String.valueOf(finalI))
                    .register(registry);
        }
    }
}
