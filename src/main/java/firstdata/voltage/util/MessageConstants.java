package firstdata.voltage.util;

public interface MessageConstants {
    
    String INVALID_NUMERIC_PARAMETERS = "Unexpected numeric passed. Check the parameters sent!";
    String UNKNOWN_TASK = "Unknown or unrecogonized task. Check the implementing interface or ensure that you have sent non-null task item.";
    String ENQUEUE_INTERRUPT = "Interuption when enqueueing a task";
    String DEQUEUE_INTERRUPT = "Interuption when dequeueing the task queue.";
    String THREADS_INTERRUPTED = "Stop Threads task was interrupted";
    String INVALID_CREDENTIALS = "Unexpected numeric passed. Check the parameters sent!";
    String ERR_ENCRYPT_FAILURE = "Failed to Encrypt Password";
    String ERR_DECRYPT_FAILURE = "Failed to Decrypt Password";
    
	//modified for FOD Vulnerability
    String TEST_MODE_PPHRASE = "005056BB036C0";

}
