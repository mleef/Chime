package DistributedManagers;

/**
 * Created by marcleef on 12/28/15.
 * To add shutdown hooks to workers.
 */
public class WorkerShutdownManager implements Runnable {
    private WorkerRestManager workerRestManager;

    public WorkerShutdownManager(WorkerRestManager workerRestManager) {
        this.workerRestManager = workerRestManager;
    }

    public void run() {
        workerRestManager.shutdown();
    }
}
