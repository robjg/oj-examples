package org.oddjob;
import java.io.File;
import java.util.concurrent.Exchanger;

import org.junit.Before;
import org.junit.Test;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.framework.extend.SimpleJob;
import org.oddjob.io.BufferType;
import org.oddjob.jobs.ExecJob;
import org.oddjob.jobs.WaitJob;
import org.oddjob.state.JobState;
import org.oddjob.state.State;
import org.oddjob.tools.ConsoleCapture;
import org.oddjob.tools.OurDirs;
import org.oddjob.tools.StateSteps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainShutdownTest extends OjTestCase {

	private static final Logger logger = LoggerFactory.getLogger(MainShutdownTest.class);
	
    @Before
    public void setUp() throws Exception {
		logger.debug("------------------ " + getName() + " -----------------");
	}
	
	public static class OurSimpleJob extends SimpleJob
	implements Stoppable {
		
		boolean stopped;
		Thread t;
		Exchanger<Void> exchanger = new Exchanger<Void>();
		
		@Override
		protected synchronized int execute() throws Throwable {
			t = Thread.currentThread();
			exchanger.exchange(null);
			try {
				while (true) {
					wait();
				}
			} catch (InterruptedException e) {
				logger.debug("OurSimpleJob interrupted.");
			}
			return 0;
		}
		public synchronized void onStop() {
			stopped = true;
			t.interrupt();
		}
	}
	
   @Test
	public void testShutdownHook() throws Exception {
		
		String xml = "<oddjob>" +
				" <job>" +
				"  <bean id='r' class='" + OurSimpleJob.class.getName() + "'/>" +
				" </job>" +
				"</oddjob>";
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration("XML", xml));
		
		oddjob.load();
		
		OurSimpleJob r = (OurSimpleJob) new OddjobLookup(oddjob).lookup("r");

		assertNotNull(r);
		
		Thread t = new Thread(oddjob);
		t.start();
		
		logger.info("Waiting for OurSimpleJob to be stoppable.");
		r.exchanger.exchange(null);
		
		OddjobRunner.ShutdownHook hook = new OddjobRunner(oddjob, i -> {}). new ShutdownHook();
		hook.run();
		
		assertTrue(r.stopped);
	}
	
	public static class NaughtyJob implements Runnable {
		
		@Override
		public void run() {
			
			WaitJob wait = new WaitJob() {
				@Override
				public int execute() throws Exception {
					System.out.println("Naughty Thread Started.");
					return super.execute();
				}
			};
			new Thread(wait).start();
		}
	}
	
	/** This doesn't test what we'd hoped because process destroy
	 *  appears to kill the jvm without invoking the shutdown hook.
	 *  
	 * @throws FailedToStopException
	 * @throws InterruptedException
	 */
   @Test
	public void testKillerThread() throws FailedToStopException, InterruptedException {
		
		OurDirs dirs = new OurDirs();
		
		File testClasses = dirs.relative("build/test/classes");
		if (!testClasses.exists()) {
			fail ("No test classes - build with Ant first!");
		}

		File oddjobDir = dirs.relative("../oddjob");
		
		ExecJob exec = new ExecJob();
		
		exec.setArgs(new String[] {
				"java", 
				"-D" + OddjobRunner.KILLER_TIMEOUT_PROPERTY + "=1",
				"-jar",
				new File(oddjobDir, "run-oddjob.jar").getPath(),
				"-cp",
				testClasses.getPath(),
				"-f",
				new File(oddjobDir, 
						"test/conf/test-killer.xml").getPath()
		});
		
		ConsoleCapture console = new ConsoleCapture();
		try (ConsoleCapture.Close close = console.capture(exec.consoleLog())) {

			StateSteps state = new StateSteps(exec);
			state.startCheck(JobState.READY, JobState.EXECUTING);
			
			new Thread(exec).start();

			state.checkWait();

			while (true) {

				BufferType buffer = new BufferType();
				buffer.setLines(console.getLines());
				buffer.configured();

				State jobState = exec.lastStateEvent().getState();
				if (buffer.getText().contains("Naughty Thread Started.")) {
					break;
				}
				else {
					if (JobState.EXECUTING != jobState) {
						console.dump(logger);
						fail("Something wrong.");
					}
				}

				logger.info("Waiting for console.");

				Thread.sleep(100);
			}
			
			exec.stop();
		}
		
		console.dump(logger);
		
		assertEquals(1, exec.getExitValue());
		
		assertEquals(JobState.INCOMPLETE, 
				exec.lastStateEvent().getState());
		
	}
	
}
