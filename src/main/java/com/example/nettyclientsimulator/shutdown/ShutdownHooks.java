package com.example.nettyclientsimulator.shutdown;

import com.example.nettyclientsimulator.util.ThreadFactoryUtil;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Ordering;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author sunxu
 */

@Component
@Slf4j
public class ShutdownHooks {

    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);

    /**
     * High priorities first
     */
    private final Multimap<Integer, ClientShutdownHook> synchronousHooks =
            MultimapBuilder.SortedSetMultimapBuilder.treeKeys(Ordering.natural().reverse())
                    .arrayListValues().build();

    public boolean isShuttingDown() {
        return shuttingDown.get();
    }

    /**
     * 添加关闭钩子
     * @param exampleShutdownHook 关闭钩子
     */
    public synchronized void add(final ClientShutdownHook exampleShutdownHook) {
        if (shuttingDown.get()) {
            return;
        }
        checkNotNull(exampleShutdownHook, "A shutdown hook must not be null");
        log.info("Adding shutdown hook {} with priority {}", exampleShutdownHook.name(), exampleShutdownHook.priority());
        synchronousHooks.put(exampleShutdownHook.priority().getValue(), exampleShutdownHook);
    }

    /**
     * Removes a {@link ClientShutdownHook} from the shutdown hook registry
     * @param exampleShutdownHook 关闭钩子
     */
    public synchronized void remove(final ClientShutdownHook exampleShutdownHook) {
        if (shuttingDown.get()) {
            return;
        }
        checkNotNull(exampleShutdownHook, "A shutdown hook must not be null");

        log.info("Removing shutdown hook {} with priority {}",
                exampleShutdownHook.name(),
                exampleShutdownHook.priority());
        synchronousHooks.values().remove(exampleShutdownHook);
    }

    /**
     * @return A registry of all Shutdown Hooks.
     */
    public Multimap<Integer, ClientShutdownHook> getShutdownHooks() {
        return synchronousHooks;
    }

    public void runShutdownHooks() {
        shuttingDown.set(true);
        log.info("Shutting down HiveMQ. Please wait, this could take a while...");
        final ScheduledExecutorService executorService =
                Executors.newSingleThreadScheduledExecutor(ThreadFactoryUtil.create("shutdown-log-executor"));
        executorService.scheduleAtFixedRate(() -> log.info(
                        "Still shutting down HiveMQ. Waiting for remaining tasks to be executed. Do not shutdown HiveMQ."),
                0,
                1,
                TimeUnit.SECONDS);

        for (final ClientShutdownHook runnable : synchronousHooks.values()) {
            log.info("Running shutdown hook {}", runnable.name());
            runnable.run();
        }

        executorService.shutdown();

        log.info("Shutdown completed.");
    }
}
