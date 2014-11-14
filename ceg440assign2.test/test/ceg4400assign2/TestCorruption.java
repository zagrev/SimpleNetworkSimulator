/**
 *
 */
package ceg4400assign2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.security.SecureRandom;

import org.junit.Test;

/**
 *
 */
public class TestCorruption
{
   /** the RNG for bit setting */
   private final SecureRandom random = new SecureRandom();

   /**
    * test that the corruption routine handles the empty payload and that it actually corrupts a bit somewhere
    */
   @Test
   public void testAckCorruption()
   {
      final Packet original = new Packet();
      final short zero = (short) 0;

      final NetworkSimulator simulator = new NetworkSimulator();

      // test this 6 times to try to get total coverage on which field gets corrupted
      original.setAck(zero);
      original.setChecksum(zero);
      original.setSequence(zero);

      final int bitOffset = random.nextInt(16);
      simulator.corruptPacket(original, bitOffset);

      assertEquals("checksum should not have changed", 0, original.getChecksum());
      assertEquals("sequence should not have changed", 0, original.getSequence());
      assertTrue("ack should have been corrupted", original.getAck() != 0);
   }

   /**
    * test that the corruption routine handles the empty payload and that it actually corrupts a bit somewhere
    */
   @Test
   public void testChecksumCorruption()
   {
      final Packet original = new Packet();
      final short zero = (short) 0;

      final NetworkSimulator simulator = new NetworkSimulator();

      // test this 6 times to try to get total coverage on which field gets corrupted
      original.setAck(zero);
      original.setChecksum(zero);
      original.setSequence(zero);

      final int bitOffset = random.nextInt(16);
      simulator.corruptPacket(original, 16 + bitOffset);

      assertEquals("ack should not have changed", 0, original.getAck());
      assertEquals("sequence should not have changed", 0, original.getSequence());
      assertTrue(original.getChecksum() != 0);
   }

   /**
    * test that the corruption routine handles the empty payload and that it actually corrupts a bit somewhere
    */
   @Test(expected = IllegalArgumentException.class)
   public void testCorruptionBadOffset()
   {
      final NetworkSimulator simulator = new NetworkSimulator();
      simulator.corruptPacket(new Packet(), 49);
   }

   /**
    * test that the corruption routine handles the empty payload and that it actually corrupts a bit somewhere
    */
   @Test(expected = IllegalArgumentException.class)
   public void testCorruptionBadPacket()
   {
      final NetworkSimulator simulator = new NetworkSimulator();
      simulator.corruptPacket(null, 0);
   }

   /**
    * test that the corruption routine handles the empty payload and that it actually corrupts a bit somewhere
    */
   @Test(expected = IllegalArgumentException.class)
   public void testCorruptionNegativeOffset()
   {
      final NetworkSimulator simulator = new NetworkSimulator();
      simulator.corruptPacket(new Packet(), -40);
   }

   /**
    * test that the corruption routine handles the empty payload and that it actually corrupts a bit somewhere
    */
   @Test
   public void testPayloadCorruption()
   {
      final Packet original = new Packet();
      final short zero = (short) 0;

      final NetworkSimulator simulator = new NetworkSimulator();

      // test this 6 times to try to get total coverage on which field gets corrupted
      original.setAck(zero);
      original.setChecksum(zero);
      original.setSequence(zero);

      final byte[] payload = new byte[20];

      original.setPayload(payload);

      final int bitOffset = random.nextInt(16);
      simulator.corruptPacket(original, 48 + bitOffset);

      // check the payload for corruption
      int bitSet = 0;
      for (int i = 0; i < 20; i++)
      {
         bitSet |= payload[i];
      }

      assertEquals("checksum should not have changed", 0, original.getChecksum());
      assertEquals("sequence should not have changed", 0, original.getSequence());
      assertEquals("ack shoult not have changed", 0, original.getAck());
      assertTrue("payload shoud have changed", bitSet != 0);
   }

   /**
    * test that the corruption routine handles the empty payload and that it actually corrupts a bit somewhere
    */
   @Test
   public void testSequenceCorruption()
   {
      final Packet original = new Packet();
      final short zero = (short) 0;

      final NetworkSimulator simulator = new NetworkSimulator();

      // test this 6 times to try to get total coverage on which field gets corrupted
      original.setAck(zero);
      original.setChecksum(zero);
      original.setSequence(zero);

      final int bitOffset = random.nextInt(16);
      simulator.corruptPacket(original, 32 + bitOffset);

      assertEquals("checksum should not have changed", 0, original.getChecksum());
      assertEquals("ack should not have changed", 0, original.getAck());
      assertTrue("sequence have changed", original.getSequence() != 0);
   }
}
