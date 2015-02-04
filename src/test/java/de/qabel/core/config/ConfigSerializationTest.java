package de.qabel.core.config;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.junit.Test;

import static org.junit.Assert.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.qabel.core.crypto.QblEncPublicKey;
import de.qabel.core.crypto.QblKeyFactory;
import de.qabel.core.crypto.QblPrimaryKeyPair;
import de.qabel.core.crypto.QblSignPublicKey;
import de.qabel.core.drop.DropURL;
import de.qabel.core.exceptions.QblDropInvalidURL;

public class ConfigSerializationTest {	
	
	@Test
	public void syncedSettingsTest() throws QblDropInvalidURL, IOException {
		SyncedSettings syncedSettings = new SyncedSettings();
		
		//generate and add an "accounts" entry
		Account account = new Account("provider", "user", "auth");
		
		syncedSettings.getAccounts().add(account);
		
		//generate and add an "drop_servers" entry
		DropServer dropServer = new DropServer(new URL("https://drop.qabel.de/0123456789012345678901234567890123456789123"),"auth", true);
		syncedSettings.getDropServers().add(dropServer);
		
		//generate "identities" array
		syncedSettings.setIdentities(new Identities());
		//generate and add an "identities" entry
		QblPrimaryKeyPair key;
		Collection<DropURL> drops; 
		Identity identity;
			
		key = QblKeyFactory.getInstance().generateQblPrimaryKeyPair();
		key.generateEncKeyPair();
		key.generateSignKeyPair();
		drops = new ArrayList<DropURL>();
		drops.add(new DropURL("https://inbox.qabel.de/123456789012345678901234567890123456789012c"));
		identity = new Identity("alias", drops, key);
		syncedSettings.getIdentities().add(identity);
		
		//generate and add a "storage_servers" entry
		StorageServer storageServer = new StorageServer(new URL("https://storage.qabel.de"), "auth");
		syncedSettings.getStorageServers().add(storageServer);
		
		//generate and add a "storage_volumes" entry
		syncedSettings.getStorageVolumes().add(new StorageVolume(storageServer, "publicIdentifier", "token", "revokeToken"));
		syncedSettings.getSyncedModuleSettings().add(new SyncedModuleSettings());

		SyncedSettings deserializedSyncedSettings = SyncedSettings.fromJson(syncedSettings.toJson());
		assertEquals(0, 
				syncedSettings.toJson().compareTo(deserializedSyncedSettings.toJson()));
		
		assertEquals(deserializedSyncedSettings, syncedSettings);
	}
	
	@Test
	public void localSettingsTest() throws IOException {
		LocalSettings localSettings = new LocalSettings(10, new Date(System.currentTimeMillis()));		
		
		LocalSettings deserializedLocalSettings = LocalSettings.fromJson(localSettings.toJson());
		
		assertEquals(deserializedLocalSettings, localSettings);
	}
	
	@Test
	public void contactTest() {
		Contact contact;
		Contact deserializedContact;
		QblKeyFactory kf = QblKeyFactory.getInstance();
		try {
			
			Identity i = new Identity("alias", new ArrayList<DropURL>(), kf.generateQblPrimaryKeyPair());
			i.addDrop(new DropURL("https://inbox.qabel.de/123456789012345678901234567890123456789012c"));
			QblPrimaryKeyPair qpkp = kf.generateQblPrimaryKeyPair();
			qpkp.generateEncKeyPair();
			qpkp.generateSignKeyPair();
			contact = new Contact(i, null, qpkp.getQblPrimaryPublicKey());
			for(QblEncPublicKey key : qpkp.getQblEncPublicKeys()) {
				contact.addEncryptionPublicKey(key);
			}
			for(QblSignPublicKey key : qpkp.getQblSignPublicKeys()) {
				contact.addSignaturePublicKey(key);
			}
			contact.addDrop(new DropURL("https://inbox.qabel.de/123456789012345678901234567890123456789012d"));
			
			GsonBuilder builder = new GsonBuilder();
			builder.registerTypeAdapter(Contact.class, new ContactTypeAdapter());
			Gson gson = builder.create();
			deserializedContact = gson.fromJson(gson.toJson(contact), Contact.class);
			
			//this has to be set by the caller for deserialization:
			deserializedContact.setContactOwner(i);
			
			assertEquals(contact, deserializedContact);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QblDropInvalidURL e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
