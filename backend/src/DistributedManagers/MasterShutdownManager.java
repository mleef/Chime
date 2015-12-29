package DistributedManagers;

/**
 * Created by marcleef on 12/28/15.
 */
public class MasterShutdownManager implements Runnable {
    private MasterRestManager masterRestManager;

    public MasterShutdownManager(MasterRestManager masterRestManager) {
        this.masterRestManager = masterRestManager;
    }

    public void run() {
        masterRestManager.shutdown();
    }
}
