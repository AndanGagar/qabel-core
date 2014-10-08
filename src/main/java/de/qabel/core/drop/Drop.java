package de.qabel.core.drop;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.qabel.core.config.Contact;
import de.qabel.core.config.Contacts;
import de.qabel.core.config.Identity;
import de.qabel.core.crypto.*;
import de.qabel.core.http.DropHTTP;

import java.net.URL;

public class Drop <T extends ModelObject> {
    GsonBuilder gb = null;
    Gson gson = null;


    public Drop() {
        gb = new GsonBuilder();
        gb.registerTypeAdapter(DropMessage.class, new DropSerializer<T>());
        gb.registerTypeAdapter(DropMessage.class, new DropDeserializer());
        gson = gb.create();
    }

    /**
     * Sends the message and waits for acknowledgement.
     * Uses sendAndForget() for now.
     *
     * TODO: implement
     *
     */
    public void send(DropMessage<ModelObject> message, Contacts contacts, Identity identity) {
        sendAndForget(message, contacts, identity);
    }

    /**
     * Sends the message and does not wait for acknowledgement
     *
     * @param message
     *                Message to send
     * @param contacts
     *                Contacts to send message to
     *
     * @param identity
     *                Identity to sign message with
     *
     * @return HTTP status code from the drop-server.
     *
     */
    public int sendAndForget(DropMessage<ModelObject> message, Contacts contacts, Identity identity) {
        DropHTTP http = new DropHTTP();
        String m = serialize(message);
        int res = 0;
        for (Contact c : contacts.getContacts()) {
            byte[] cryptedMessage = encryptDrop(
                                    m,
                                    c.getEncryptionPublicKey(),
                                    c.getContactOwner().getPrimaryKeyPair().getSignKeyPairs()
            );
            for (URL u : c.getDropUrls()) {
                res = http.send(u, cryptedMessage);
            }
        }
        return res;
    }

    /**
     * Retrieves a drop message from given URL
     *
     * @param url
     *            URL where to retrieve the drop from
     *
     * @param contacts
     *            Contacts to check the signature with
     *
     * @return Retrieved, encrypted Dropmessage.
     */
    public DropMessage retrieve(URL url, Contacts contacts){
        DropHTTP http = new DropHTTP();

        byte[] cipherMessage = http.receiveMessages(url).getBytes();
        String plainJson = null;
        for (Contact c : contacts.getContacts()) {
            if(plainJson == null) {
                plainJson = decryptDrop(cipherMessage,
                        c.getContactOwner().getPrimaryKeyPair(),
                        c.getSignaturePublicKey()
                );
            } else {
                break;
            }
        }
        if(plainJson != null) {
            return deserialize(plainJson);
        } else {
            return null;
        }
    }

    /**
     * Serializes the message
     *
     * @param message
     *              DropMessage to serialize
     *
     * @return String with message as json
     *
     */
    private String serialize(DropMessage<ModelObject> message) {
        return gson.toJson(message);
    }

    /**
     * Deserializes the message
     *
     * @param plainJson
     *            plain Json String
     *
     * @return deserialized Dropmessage
     */
    private DropMessage<ModelObject> deserialize(String plainJson) {
        return gson.fromJson(plainJson, DropMessage.class);
    }

    /**
     * Deserializes the message
     *
     * @param jsonMessage
     *            plain Json String to encrypt
     * @param publickey
     *            Publickey to encrypt the jsonMessage with
     * @param skp
     *            Sign key pair to sign the message
     *
     * @return the cyphertext as byte[]
     */
    private byte[] encryptDrop(String jsonMessage, QblEncPublicKey publickey, QblSignKeyPair skp) {
        CryptoUtils cu = CryptoUtils.getInstance();
        return cu.encryptHybridAndSign(jsonMessage, publickey, skp);
    }


    /**
     *
     * @param cipher
     *            Ciphertext to decrypt
     * @param keypair
     *            Keypair to decrypt the ciphertext with
     * @param signkey
     *            Public sign key to validate the signature
     *
     * @return The encrypted message as string
     */
    private String decryptDrop(byte[] cipher, QblPrimaryKeyPair keypair, QblSignPublicKey signkey){
        CryptoUtils cu = CryptoUtils.getInstance();
        return cu.decryptHybridAndValidateSignature(cipher, keypair, signkey);

    }

}