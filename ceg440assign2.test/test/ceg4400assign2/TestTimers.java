/**
 *
 */
package ceg4400assign2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ceg4400assign2.NetworkSimulator.EventType;
import ceg4400assign2.NetworkSimulator.NetworkEvent;

/**
 *
 */
public class TestTimers
{
   /**
    * test that we cannot set a timer to start in the same time as the current execution time (zero duration)
    */
   @Test
   public void testRemoveTimer()
   {
      final NetworkSimulator simulator = MockUtilities.createNetworkSimulator();

      simulator.startTimer(simulator.getSideA(), 40);

      NetworkEvent event = simulator.eventQueue.peek();
      assertNotNull("Timer event should have been on the queue", event);
      assertEquals(null, event.getEventPacket());
      assertEquals(40, event.getEventTime());
      assertEquals(EventType.Timer, event.getEventType());
      assertEquals(simulator.getSideA(), event.getRecipient());

      assertTrue("stopping the timer didn't find the timer event", simulator.stopTimer(simulator.getSideA()));

      event = simulator.eventQueue.poll();
      assertNull("The timer event wasn't removed from the event queue", event);
   }

   /**
    * test that we cannot set a timer to start in the same time as the current execution time (zero duration)
    */
   @Test
   public void testSetTimer()
   {
      final NetworkSimulator simulator = MockUtilities.createNetworkSimulator();
      final Layer4 module = simulator.getSideA();

      simulator.startTimer(module, 40);

      final NetworkEvent event = simulator.eventQueue.poll();
      assertNotNull("Timer event should have been on the queue", event);
      assertEquals(null, event.getEventPacket());
      assertEquals(40, event.getEventTime());
      assertEquals(EventType.Timer, event.getEventType());
      assertEquals(module, event.getRecipient());
   }

   /**
    * test that we cannot set a timer without setting the owner
    */
   @Test(expected = IllegalArgumentException.class)
   public void testSetTimerNullOwner()
   {
      final NetworkSimulator simulator = MockUtilities.createNetworkSimulator();

      simulator.startTimer(null, 45);
   }

   /**
    * test that we cannot set a timer to start in the same time as the current execution time (zero duration)
    */
   @Test(expected = IllegalArgumentException.class)
   public void testSetTimerZeroDuration()
   {
      final NetworkSimulator simulator = MockUtilities.createNetworkSimulator();
      final Layer4 module = simulator.getSideA();

      simulator.startTimer(module, 0);
   }

   /**
    * test that we cannot set a timer to start in the same time as the current execution time (zero duration)
    */
   @Test
   public void testStartTwice()
   {
      final NetworkSimulator simulator = MockUtilities.createNetworkSimulator();
      final Layer4 module = simulator.getSideA();

      simulator.startTimer(module, 40);
      simulator.startTimer(module, 400);

      final NetworkEvent event = simulator.eventQueue.poll();
      assertNotNull("Timer event should have been on the queue", event);
      assertEquals(null, event.getEventPacket());
      assertEquals(400, event.getEventTime());
      assertEquals(EventType.Timer, event.getEventType());
      assertEquals(module, event.getRecipient());
   }

   /**
    * test that we cannot set a timer to start in the same time as the current execution time (zero duration)
    */
   @Test
   public void testStopTwice()
   {
      final NetworkSimulator simulator = MockUtilities.createNetworkSimulator();
      final Layer4 module = simulator.getSideA();

      simulator.startTimer(module, 40);

      final NetworkEvent event = simulator.eventQueue.peek();
      assertNotNull("Timer event should have been on the queue", event);
      assertEquals(null, event.getEventPacket());
      assertEquals(40, event.getEventTime());
      assertEquals(EventType.Timer, event.getEventType());
      assertEquals(module, event.getRecipient());

      assertTrue("First stop should have found a timer", simulator.stopTimer(module));
      assertFalse("Second stop should NOT have found a timer", simulator.stopTimer(module));
   }

   /**
    * test that we cannot set a timer to start in the same time as the current execution time (zero duration)
    */
   @Test
   public void testStopWithoutStart()
   {
      final NetworkSimulator simulator = MockUtilities.createNetworkSimulator();
      final Layer4 module = simulator.getSideA();

      assertFalse("First stop should NOT have found a timer", simulator.stopTimer(module));
   }

   /**
    * test that we cannot set a timer to start in the same time as the current execution time (zero duration)
    */
   @Test
   public void testTimeout()
   {
      final NetworkSimulator simulator = MockUtilities.createNetworkSimulator();
      final MockLayer4 module = (MockLayer4) simulator.getSideA();

      simulator.startTimer(module, 40);

      final NetworkEvent event = simulator.eventQueue.peek();
      assertNotNull("Timer event should have been on the queue", event);
      assertEquals(null, event.getEventPacket());
      assertEquals(40, event.getEventTime());
      assertEquals(EventType.Timer, event.getEventType());
      assertEquals(module, event.getRecipient());

      simulator.setSideA(module);
      simulator.setSideB(module);

      simulator.setMaxNumberOfMessagesToSend(1);
      simulator.run();

      assertTrue("Timeout method should have been called", module.timeoutCalled);
   }

}
