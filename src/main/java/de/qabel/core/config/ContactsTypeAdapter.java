package de.qabel.core.config;

import java.io.IOException;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class ContactsTypeAdapter extends TypeAdapter<Contacts> {

	@Override
	public void write(JsonWriter out, Contacts value) throws IOException {
		out.beginArray();
		Set<Contact> set = value.getContacts();
		TypeAdapter<Contact> adapter = new ContactTypeAdapter();
		for(Contact contact : set) {
			adapter.write(out, contact);
		}
		out.endArray();
		return;
	}

	@Override
	public Contacts read(JsonReader in) throws IOException {
		if(in.peek() == JsonToken.NULL) {
			in.nextNull();
			return null;
		}
		
		Contacts contacts = new Contacts();
		TypeAdapter<Contact> adapter = new ContactTypeAdapter();
		Contact contact = null; 
		
		in.beginArray();
		while(in.hasNext()) {
			contact = adapter.read(in);
			contacts.add(contact);
		}
		in.endArray();
		
		return contacts;
	}
}