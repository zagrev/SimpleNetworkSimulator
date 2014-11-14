/**
 *
 */
package ceg4400assign2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.SecureRandom;

import org.junit.Test;

import ceg4400assign2.NetworkSimulator.EventType;
import ceg4400assign2.NetworkSimulator.NetworkEvent;

/**
 *
 */
@SuppressWarnings("boxing")
public class TestGenerateSendDataEvent
{
   /**
    * test that the events are sent to equally to both sides
    */
   @Test
   public void testSideAEvent()
   {
      final NetworkSimulator simulator = MockUtilities.createNetworkSimulator();

      final SecureRandom mockRandom = mock(SecureRandom.class);
      when(mockRandom.nextDouble()).thenReturn(Double.valueOf(0.49));
      simulator.random = mockRandom;

      simulator.generateSendDataEvent();

      final NetworkEvent event = simulator.eventQueue.poll();

      assertEquals("Event should have been send to Side A", simulator.getSideA(), event.getRecipient());
      assertTrue("Event should be in the future", simulator.getCurrentTime() < event.getEventTime());
      assertTrue("Event should not be too far in the future",
            simulator.getCurrentTime() + simulator.getTimeBetweenMessages() + 1 > event.getEventTime());
      assertEquals("Event type should be FromApplication", EventType.FromApplication, event.getEventType());
      assertNull("Event should have data packet", event.getEventPacket());
   }

   /**
    * test that the events are sent to equally to both sides
    */
   @Test
   public void testSideBEvent()
   {
      final NetworkSimulator simulator = MockUtilities.createNetworkSimulator();

      final SecureRandom mockRandom = mock(SecureRandom.class);
      when(mockRandom.nextDouble()).thenReturn(Double.valueOf(0.50));
      simulator.random = mockRandom;

      simulator.generateSendDataEvent();

      final NetworkEvent event = simulator.eventQueue.poll();

      assertEquals("Event should have been send to Side B", simulator.getSideA(), event.getRecipient());
      assertTrue("Event should be in the future", simulator.getCurrentTime() < event.getEventTime());
      assertTrue("Event should not be too far in the future",
            simulator.getCurrentTime() + simulator.getTimeBetweenMessages() + 1 > event.getEventTime());
      assertEquals("Event type should be FromApplication", EventType.FromApplication, event.getEventType());
      assertNull("Event should have data packet", event.getEventPacket());
   }
}
