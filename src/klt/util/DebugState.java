package klt.util;

/**
 * Created by Tobi on 04.11.2014.
 */
public enum DebugState {
    NO_DEBUG				(false, false, 	false),
    AGENT_DEBUG				(true, 	false, 	false),
    ENVIRONMENT_DEBUG		(false, false, 	true),
    KI_DEBUG				(false, true, 	false),
    AGENT_ENVIRONMENT_DEBUG	(true, 	false, 	true),
    ALL_DEBUG				(true, 	true, 	true);

    private boolean debugAgent;
    private boolean debugKI;
    private boolean debugEnvironment;
    private boolean qLoggingEnabled = false;

    DebugState(boolean debugA, boolean debugKI, boolean debugE){
        this.debugAgent = debugA;
        this.debugEnvironment = debugE;
        this.debugKI = debugKI;
    }

    DebugState(boolean debugA, boolean debugKI, boolean debugE, boolean qLogging){
        this(debugA, debugKI, debugE);
        qLoggingEnabled = qLogging;
    }

    public boolean isqLoggingEnabled() {
        return qLoggingEnabled;
    }

    public void setqLoggingEnabled(boolean qLoggingEnabled) {
        this.qLoggingEnabled = qLoggingEnabled;
    }

    public boolean getEnvironmentDebugState() {
        return debugEnvironment;
    }

    public boolean getAgentDebugState() {
        return debugAgent;
    }

    public boolean getKIDebugState() {
        return debugKI;
    }
}
