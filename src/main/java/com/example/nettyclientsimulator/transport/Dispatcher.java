package com.example.nettyclientsimulator.transport;

import io.netty.channel.EventLoop;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author sunxu
 */

@Component
public class Dispatcher {
    private static final ConcurrentMap<EventLoop, Flusher> FLUSHER_LOOKUP = new ConcurrentHashMap<>();

    public void flush(Flusher.FlushItem<?> item) {
        EventLoop loop = item.channel.eventLoop();
        Flusher flusher = FLUSHER_LOOKUP.get(loop);
        if (flusher == null) {
            Flusher created = new Flusher(loop);
            Flusher alt = FLUSHER_LOOKUP.putIfAbsent(loop, flusher = created);
            if (alt != null) {
                flusher = alt;
            }
        }
        flusher.enqueue(item);
        flusher.start();
    }
}
