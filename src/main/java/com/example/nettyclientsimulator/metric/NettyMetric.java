package com.example.nettyclientsimulator.metric;


import io.micrometer.core.instrument.Gauge;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.netty.buffer.ByteBufAllocatorMetric;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocatorMetric;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sunxu
 */
@Configuration
public class NettyMetric {

    private final PrometheusMeterRegistry registry;

    public NettyMetric(PrometheusMeterRegistry registry) {
        this.registry = registry;
    }

    private static final String METRIC_EVENT_GROUP_PENDING_TASKS = "netty.event.loop.group.pending.tasks";
    private static final String TAG_EVENT_LOOP_GROUP_NAME = "event.loop.group.name";

    public static final String METRIC_UNPOOLED_BYTEBUF_ALLOCATOR = "netty.unpooled.bytebuf.allocator";
    public static final String METRIC_POOLED_BYTEBUF_ALLOCATOR = "netty.pooled.bytebuf.allocator";

    public static final String MEM_TYPE_HEAP = "heap";

    public static final String MEM_TYPE_DIRECT = "direct";

    public static final String TAG_MEM_TYPE = "memory.type";

    @Bean
    public Gauge nettyUnpooledHeapMemoryGauge(PrometheusMeterRegistry registry) {
        return Gauge.builder(METRIC_UNPOOLED_BYTEBUF_ALLOCATOR, UnpooledByteBufAllocator.DEFAULT.metric(),
                        ByteBufAllocatorMetric::usedHeapMemory)
                .tags(TAG_MEM_TYPE, MEM_TYPE_HEAP)
                .description("netty unpooled bytebuf allocator used memory")
                .register(registry);
    }

    @Bean
    public Gauge nettyUnpooledDirectMemoryGauge(PrometheusMeterRegistry registry) {
        return Gauge.builder(METRIC_UNPOOLED_BYTEBUF_ALLOCATOR, UnpooledByteBufAllocator.DEFAULT.metric(),
                        ByteBufAllocatorMetric::usedDirectMemory)
                .tags(TAG_MEM_TYPE, MEM_TYPE_DIRECT)
                .description("netty unpooled bytebuf allocator used memory")
                .register(registry);
    }

    @Bean
    public Gauge nettyPooledHeapMemoryGauge(PrometheusMeterRegistry registry) {
        return Gauge.builder(METRIC_POOLED_BYTEBUF_ALLOCATOR, PooledByteBufAllocator.DEFAULT.metric(),
                        PooledByteBufAllocatorMetric::usedHeapMemory)
                .tags(TAG_MEM_TYPE, MEM_TYPE_HEAP)
                .description("netty pooled bytebuf allocator used memory")
                .register(registry);
    }

    @Bean
    public Gauge nettyPooledDirectMemoryGauge(PrometheusMeterRegistry registry) {
        return Gauge.builder(METRIC_POOLED_BYTEBUF_ALLOCATOR, PooledByteBufAllocator.DEFAULT.metric(),
                        PooledByteBufAllocatorMetric::usedDirectMemory)
                .tags(TAG_MEM_TYPE, MEM_TYPE_DIRECT)
                .description("netty pooled bytebuf allocator used memory")
                .register(registry);
    }

    public void addEventLoopGroupMetric(EventLoopGroup eventExecutors, String name) {
        int index = 0;
        for (EventExecutor eventExecutor : eventExecutors) {
            if (eventExecutor instanceof SingleThreadEventExecutor) {
                final SingleThreadEventExecutor singleExecutor = (SingleThreadEventExecutor) eventExecutor;
                Gauge.builder(METRIC_EVENT_GROUP_PENDING_TASKS, singleExecutor, SingleThreadEventExecutor::pendingTasks)
                        .tags(TAG_EVENT_LOOP_GROUP_NAME, name + "-" + index)
                        .description("Event Loop Group Pending Tasks")
                        .register(registry);
            }

            index += 1;
        }
    }
}
