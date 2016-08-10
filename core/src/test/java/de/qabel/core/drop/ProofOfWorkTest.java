package de.qabel.core.drop;

import de.qabel.core.drop.ProofOfWork;
import org.junit.Test;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.util.encoders.Base64;
import org.spongycastle.util.encoders.Hex;

import static org.junit.Assert.assertEquals;

public class ProofOfWorkTest {
    @Test
    public void proofOfWorkTest() {
        SHA256Digest digest = new SHA256Digest();
        byte[] hash = new byte[ProofOfWork.Companion.getHashLength()];

        //Create a PoW with 16 leading zero bits
        ProofOfWork pow;
        pow = ProofOfWork.Companion.calculate(16, Hex.decode("257157de4b0551"), Hex.decode("abcdef"));

        digest.update(Base64.decode(pow.getIVserverB64()), 0, Base64.decode(pow.getIVserverB64()).length);
        digest.update(Base64.decode(pow.getIVclientB64()), 0, Base64.decode(pow.getIVclientB64()).length);
        digest.update(ProofOfWork.Companion.toByteArray(pow.getTime()), 0, ProofOfWork.Companion.getLongLength());
        digest.update(Base64.decode(pow.getMessageHashB64()), 0, Base64.decode(pow.getMessageHashB64()).length);
        digest.update(ProofOfWork.Companion.toByteArray(pow.getCounter()), 0, ProofOfWork.Companion.getLongLength());

        digest.doFinal(hash, 0);

        assertEquals(Hex.toHexString(Base64.decode(pow.getProofOfWorkHashB64())), Hex.toHexString(hash));
        assertEquals(Base64.decode(pow.getProofOfWorkHashB64())[0], 0);
        assertEquals(Base64.decode(pow.getProofOfWorkHashB64())[1], 0);
    }
}
