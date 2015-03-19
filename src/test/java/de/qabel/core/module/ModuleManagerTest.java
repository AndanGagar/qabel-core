package de.qabel.core.module;

import static org.junit.Assert.*;

import de.qabel.core.drop.DropMessage;
import org.junit.Test;

public class ModuleManagerTest {
	static class TestModule extends Module {
		public boolean isInit = false;
		private boolean isRunning = false;

		@Override
		public void init() {
			isInit = true;
		}

        @Override
        protected void onDropMessage(DropMessage<?> dm) {
            // Empty
        }

        public synchronized boolean isStarted() {
			return isRunning;
		}

		public synchronized void setRunning(boolean isRunning) {
			this.isRunning = isRunning;
		}
	}

	@Test
	public void liveCycleTest() throws Exception {
		ModuleManager mm = new ModuleManager();
		mm.startModule(TestModule.class);
		TestModule module = (TestModule) mm.getModules().iterator().next().getModule();
		assertTrue(module.isInit);
		assertFalse(module.isStarted());
		Thread.sleep(200);
		assertTrue(module.isStarted());
		mm.shutdown();
	}
}
