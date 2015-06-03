package de.qabel.core.module;

import de.qabel.ackack.MessageInfo;
import de.qabel.ackack.event.EventActor;
import de.qabel.ackack.event.EventEmitter;
import de.qabel.ackack.event.EventListener;

import java.io.Serializable;

/**
 * Abstract class to create modules. Restricts direct access to the
 * utilized EventEmitter. Inheritors have to implement onEvent from EventListener
 * to receive registered events.
 */
public abstract class Module extends EventActor implements EventListener {
	private final ModuleManager moduleManager;
	private final EventEmitter emitter;

	protected Module(ModuleManager moduleManager) {
		super(moduleManager.getEventEmitter());
		this.emitter = moduleManager.getEventEmitter();
		this.moduleManager = moduleManager;
	}

	/**
	 * gets the ModuleManager which manages this Module.
	 *
	 * @return ModuleManager
	 */
	ModuleManager getModuleManager() {
		return this.moduleManager;
	}

	/**
	 * Called by the ModuleManager to set up this Module and registers Listener
	 */
	abstract public void init();

	/**
	 * stops the background thread. Overwrite this if you want to do cleanup work.
	 * Don't forget to call super.
	 * This should NOT be called from the background thread itself!
	 */
	public synchronized void stopModule() {
		this.stop();
		moduleManager.removeModule(this);
	}

	/**
	 * Override on as final to disallow inheritors of Module to
	 * directly register to Events by calling on method on
	 * ModuleManager.
	 * @param event Event id
	 * @param listener Event listener
	 */
	@Override
	public final void on(String event, EventListener listener) {
		moduleManager.on(event, this);
	}

	/**
	 * Callback method for ModuleManager to register EventListener
	 * @param event Event to register for
	 * @param listener EventListener to register
	 */
	final void doOn(String event, EventListener listener) {
		super.on(event, listener);
	}

	/**
	 * Wrapper to emit event on Modules EventEmitter
	 * @param event Event id
	 * @param data Data to emit
	 * @return Number of actor which want the data
	 */
	public final int emit(String event, Serializable... data) {
		return emitter.emit(event, data);
	}

	/**
	 * Wrapper to emit event on Modules EventEmitter
	 * @param event Event id
	 * @param info Information of the message
	 * @param data Data to emit
	 * @return Number of actor which want the data
	 */
	public final int emit(String event, MessageInfo info, Serializable... data) {
		return emitter.emit(event, info, data);
	}
}
