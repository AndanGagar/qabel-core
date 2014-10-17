package de.qabel.core.drop;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.qabel.core.config.DropServer;
import de.qabel.core.config.DropServers;
import de.qabel.core.http.DropHTTP;

public class DropController {

	Map<Class<? extends ModelObject>, Set<DropCallback<? extends ModelObject>>> mCallbacks;

	private DropServers mDropServers;
	
	public DropController() {
		mCallbacks = new HashMap<Class<? extends ModelObject>, Set<DropCallback<? extends ModelObject>>>();
	}

	/**
	 * Register for DropMessages with a modelObject
	 * 
	 * @param modelObject
	 *            ModelObject to receive DropMessages for
	 * @return a queue in which the DropController will put received
	 *         DropMessages with the selected ModelObject
	 */
	public <T extends ModelObject> void register(Class<T> type,
			DropCallback<T> callback) {
		Set<DropCallback<? extends ModelObject>> typeCallbacks = mCallbacks
				.get(type);
		if (typeCallbacks == null) {
			typeCallbacks = new HashSet<DropCallback<? extends ModelObject>>();
			mCallbacks.put(type, typeCallbacks);
		}
		typeCallbacks.add(callback);
	}

	/**
	 * Handles a received DropMessage. Puts this DropMessage into the registered
	 * Queues.
	 * 
	 * @param dm
	 *            DropMessage which should be handled
	 */
	public void handleDrop(DropMessage<? extends ModelObject> dm) {
		Class<? extends ModelObject> cls = dm.getModelObject();
		Set<DropCallback<? extends ModelObject>> typeCallbacks = mCallbacks
				.get(cls);

		for (DropCallback<? extends ModelObject> callback : typeCallbacks) {
			Method m;
			try {
				m = callback.getClass().getMethod("onDropMessage",
						ModelObject.class);
				m.invoke(callback, dm.getData());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void receive() {
		HashSet<DropServer> servers = new HashSet<DropServer>(getDropServers().getDropServer());
		for(DropServer server : servers) {
			DropHTTP http = new DropHTTP();
			Collection<String> message = http.receiveMessages(server.getUrl());
			
			// TODO deserialize message to object and send it to handleDrop
		}
	}

	public DropServers getDropServers() {
		return mDropServers;
	}

	public void setDropServers(DropServers mDropServers) {
		this.mDropServers = mDropServers;
	}
}
