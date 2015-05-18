package org.violin.asynchronous;

/**
 * Request to send is handled here. The destination for a message is assumed to
 * be all friends which are not online.
 * 
 * @author Hampus Balldin
 */
public class Sender implements Runnable {
	private boolean running;

	@Override
	public void run() {
		running = true;
		while (running) {

		}
	}
}
