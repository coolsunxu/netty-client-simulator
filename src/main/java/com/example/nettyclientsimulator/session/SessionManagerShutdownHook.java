package com.example.nettyclientsimulator.session;

import com.example.nettyclientsimulator.session.impl.SessionManagerImpl;
import com.example.nettyclientsimulator.shutdown.ClientShutdownHook;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sunxu
 */
@Slf4j
public class SessionManagerShutdownHook implements ClientShutdownHook {
    private final SessionManagerImpl sessionManager;

    public SessionManagerShutdownHook(SessionManagerImpl sessionManager) {
        this.sessionManager = sessionManager;
    }


    @Override
    public String name() {
        return "SessionManagerShutdownHook";
    }

    @Override
    public ClientShutdownHook.Priority priority() {
        return Priority.HIGH;
    }

    @Override
    public void run() {
        log.info("Shutting down SessionManager");
        sessionManager.shutdown();
    }
}
