package com.example.nettyclientsimulator.transport;

import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author sunxu
 */
@Slf4j
public class Flusher implements Runnable{


    protected final EventLoop eventLoop;
    private final ConcurrentLinkedQueue<FlushItem<?>> queued = new ConcurrentLinkedQueue<>();
    protected final AtomicBoolean scheduled = new AtomicBoolean(false);
    protected final List<FlushItem<?>> processed = new ArrayList<>();
    private final HashSet<Channel> channels = new HashSet<>();

    int runsSinceFlush = 0;
    int runsWithNoWork = 0;

    void start() {
        if (!scheduled.get() && scheduled.compareAndSet(false, true)) {
            this.eventLoop.execute(this);
        }
    }

    Flusher(EventLoop eventLoop) {
        this.eventLoop = eventLoop;
    }

    void enqueue(FlushItem<?> item) {
        queued.add(item);
    }

    FlushItem<?> poll() {
        return queued.poll();
    }

    boolean isEmpty() {
        return queued.isEmpty();
    }

    private void processResponse(FlushItem  flush) {
        flush.channel.write(flush.response);
        channels.add(flush.channel);
    }

    protected boolean processQueue() {
        boolean doneWork = false;
        FlushItem<?> flush;
        while ((flush = poll()) != null) {
            processResponse(flush);
            processed.add(flush);
            doneWork = true;
        }
        return doneWork;
    }

    protected void flushWrittenChannels() {

        if (!channels.isEmpty()) {
            for (Channel channel : channels) {
                //log.info("begin flush channel {}",channel);
                channel.flush();
            }
        }

        for (int i = 0; i < processed.size(); i++) {
            FlushItem<?> item = processed.get(i);
            //item.release();
        }

        channels.clear();
        processed.clear();
    }

    @Override
    public void run() {

        //log.info("begin run");
        boolean doneWork = processQueue();
        runsSinceFlush++;

        //log.info("doneWork {}",doneWork);

        if (!doneWork || runsSinceFlush > 2 || processed.size() > 50) {
            //log.info("begin flushWrittenChannels");
            flushWrittenChannels();
            runsSinceFlush = 0;
        }

        if (doneWork) {
            //log.info("doneWork done");
            runsWithNoWork = 0;
        } else {
            // either reschedule or cancel
            //log.info("doneWork not done,runsWithNoWork {}",runsWithNoWork);
            if (++runsWithNoWork > 5) {
                scheduled.set(false);
                if (isEmpty() || !scheduled.compareAndSet(false, true)) {
                    return;
                }
            }
        }
        eventLoop.schedule(this, 10000, TimeUnit.NANOSECONDS);
    }

    public static class FlushItem<T> {

        final Channel channel;
        final T response;

        public FlushItem(Channel channel, T response) {
            this.channel = channel;
            this.response = response;
        }

        void release() {
            ReferenceCountUtil.release(response);
        }
    }

}

