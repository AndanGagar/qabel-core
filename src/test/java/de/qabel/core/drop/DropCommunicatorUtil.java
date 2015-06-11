package de.qabel.core.drop;

import de.qabel.ackack.MessageInfo;
import de.qabel.ackack.event.EventActor;
import de.qabel.ackack.event.EventEmitter;
import de.qabel.ackack.event.EventListener;
import de.qabel.core.config.*;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DropCommunicatorUtil <T extends ModelObject> {
	private final EventEmitter emitter;
	LinkedBlockingQueue<DropMessage<T>> inputqueue = new LinkedBlockingQueue<>();
	private ContactsActor contactsActor;
	private ConfigActor configActor;
	private EventActor actor;
	private DropActor dropActor;
	private Thread actorThread;
	private Thread dropActorThread;
	private Identities identities;
	private DropServers dropServers;
	Class<?> cls;
	public DropCommunicatorUtil(EventEmitter emitter) {
		this.emitter = emitter;
		this.contactsActor = ContactsActor.getDefault();
		this.configActor = ConfigActor.getDefault();
	}

	public void setCls(Class<?> cls) {
		this.cls = cls;
	}

	public void start(Contacts contacts, Identities identities, DropServers dropServers) throws InterruptedException {
		this.identities = identities;
		this.dropServers = dropServers;
		this.actor = new EventActor(emitter);
		this.actor.on(DropActor.EVENT_DROP_MESSAGE_RECEIVED, new EventListener() {
			@Override
			public void onEvent(String event, MessageInfo info, Object... data) {
				try {
					DropMessage<T> dropMessage = (DropMessage<T>) data[0];
					if(cls == null || dropMessage.getData().getClass().isAssignableFrom(cls))
						inputqueue.put(dropMessage);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		this.dropActor = new DropActor(emitter);
		this.contactsActor.writeContacts(contacts.getContacts().toArray(new Contact[0]));
		this.configActor.writeIdentities(identities.getIdentities().toArray(new Identity[0]));
		this.configActor.writeDropServers(dropServers.getDropServers().toArray(new DropServer[0]));
		this.actorThread = new Thread(actor, "actor");
		this.dropActorThread = new Thread(dropActor, "dropActor");
		this.dropActor.setInterval(500);
		actorThread.start();
		dropActorThread.start();

		Thread.sleep(1000);
	}

	public void stop() throws InterruptedException {
		this.actor.stop();
		this.dropActor.stop();
		this.dropActor.unregister();
		this.configActor.removeIdentities(identities.getIdentities().toArray(new Identity[0]));
		this.configActor.removeDropServers(dropServers.getDropServers().toArray(new DropServer[0]));
		this.actorThread.join();
		this.dropActorThread.join();
	}

	public DropMessage<T> retrieve() throws InterruptedException {
		return inputqueue.take();
	}
}
