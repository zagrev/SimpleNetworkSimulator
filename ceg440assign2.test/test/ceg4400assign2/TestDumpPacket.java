/**
 *
 */
package ceg4400assign2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 *
 */
public class TestDumpPacket
{
   /**
    * test that it throws an exception when passed a null array
    */
   @Test(expected = IllegalArgumentException.class)
   public void testHeaderPacket()
   {
      final NetworkSimulator simulator = MockUtilities.createNetworkSimulator();
      final Packet packet = new Packet();
      simulator.dumpBytes(packet.getPayload());
   }

   /**
    * test that it throws an exception when passed a null array
    */
   @Test
   public void testMixedDataTypes()
   {
      final NetworkSimulator simulator = MockUtilities.createNetworkSimulator();

      final String source = "abc-=!~abc";
      final String result = simulator.toString(source.getBytes(), 50);

      assertNotNull("should always return result", result);
      assertTrue(result.contains(" 0x"));
   }

   /**
    * test that it throws an exception when passed a null array
    */
   @Test(expected = IllegalArgumentException.class)
   public void testNullPacket()
   {
      final NetworkSimulator simulator = MockUtilities.createNetworkSimulator();
      simulator.toString(null, 0);
   }

   /**
    * test that it truncates the output properly
    */
   @Test
   public void testPacketLongerThanLimit()
   {
      final NetworkSimulator simulator = MockUtilities.createNetworkSimulator();

      final byte[] data = new byte[simulator.getDumpLimit() + 1];
      final String result = simulator.toString(data, simulator.getDumpLimit());

      assertNotNull("toString should never return null", result);
      assertTrue("truncated results should end with ellipsis", result.endsWith("..."));
   }

   /**
    * test that it throws an exception when passed a null array
    */
   @Test
   public void testPacketShorterThanLimit()
   {
      final NetworkSimulator simulator = MockUtilities.createNetworkSimulator();

      final byte[] data = new byte[1];
      final String result = simulator.toString(data, 25);

      assertNotNull("toString should never return null", result);
      assertFalse("non-truncated results should NOT end with ellipsis", result.endsWith("..."));
   }
}
