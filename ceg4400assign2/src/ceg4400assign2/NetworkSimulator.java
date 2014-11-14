/**
 *
 */
package ceg4400assign2;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * this is the network simulator for programming assignment 2. This will initialize the two communicating entities A and
 * B and then send data to them and expect them to send it reliably to the other entity.
 */
public class NetworkSimulator
{
   /**
    * The type of network events possible in the simulation
    */
   enum EventType
   {
      /** the event is from the application (sending more data) */
      FromApplication,
      /** the event if from the network (receiving data) */
      FromNetwork,
      /** the event is a timer */
      Timer
   }

   /**
    * An event in the simulation. It has a time, a type, and the actual
    */
   final class NetworkEvent
   {
      /** the time the event occurs in the simulation */
      private long eventTime;
      /** the type of event */
      private EventType eventType;
      /** the packet of the event, if any */
      private Packet eventPacket;
      /** the side that should receive the event */
      private Layer4 recipient;

      /**
       * @return the eventPacket
       */
      public Packet getEventPacket()
      {
         return eventPacket;
      }

      /**
       * get the time of this event in time units. This is not based on any real time value.
       *
       * @return the time of the event, in time units
       */
      public long getEventTime()
      {
         return eventTime;
      }

      /**
       * get the type of the event for this packet
       *
       * @return the type of this event
       */
      public EventType getEventType()
      {
         return eventType;
      }

      /**
       * @return the recipient
       */
      public Layer4 getRecipient()
      {
         return recipient;
      }

      /**
       * @param eventPacket
       *           the eventPacket to set
       */
      public void setEventPacket(final Packet eventPacket)
      {
         this.eventPacket = eventPacket;
      }

      /**
       * set the time of this event. This event will occur at this time in the simulation. The time units are simulation
       * time, not any sort of real time.
       *
       * @param eventTime
       *           the time for the event to occur
       */
      public void setEventTime(final long eventTime)
      {
         this.eventTime = eventTime;
      }

      /**
       * set the event type
       *
       * @param eventtype
       *           the event type to set
       */
      public void setEventType(final EventType eventtype)
      {
         this.eventType = eventtype;
      }

      /**
       * @param recipient
       *           the recipient to set
       */
      public void setRecipient(final Layer4 recipient)
      {
         this.recipient = recipient;
      }
   }

   /** the maximum number of payload bytes to dump */
   private int dumpLimit = 20;

   /** the level of detail in the output of the simulation engine */
   private int traceLevel = 0;

   /** whether sending data in both directions */
   private boolean bidirectional = false;

   /** the random number generator to use */
   SecureRandom random = new SecureRandom();

   /** the number of messages to send */
   private long maxNumberOfMessagesToSend = 1000;

   /** the A side of the network simulation */
   private Layer4 sideA;

   /** the B side of the network simulation */
   private Layer4 sideB;

   /** the event queue */
   final PriorityQueue<NetworkEvent> eventQueue = new PriorityQueue<>(10, new Comparator<NetworkEvent>()
   {
      @Override
      public int compare(final NetworkEvent event1, final NetworkEvent event2)
      {
         return (int) (event1.getEventTime() - event2.getEventTime());
      }
   });

   /** The size of the packet payload */
   private int payloadSize = 20;

   /** the simulation time */
   long currentTime = 0;

   /** arrival rate of messages from layer 5 (the application) */
   private int timeBetweenMessages = 1000;

   /** variability in the arrival rate of messages from layer 5 (the application) */
   private int variabilityBetweenMessages = 200;

   /** the transmission time */
   private int transmissionTime = 5;

   /** the probability that a packet is lost. */
   private double probabilityOfLoss = 0.5;

   /** the chance that a packet is corrupted in transit */
   private double probabilityOfCorruption = 0.05;

   /**
    * Corrupt a random bit in the given packet. This bit can be anywhere in the packet including the headers.
    *
    * @param packet
    *           the packet to corrupt
    * @param bitOffset
    *           the bit offset in the packet to corrupt
    */
   void corruptPacket(final Packet packet, final int bitOffset)
   {
      if (packet == null)
      {
         throw new IllegalArgumentException();
      }
      final int maxOffset = 8 * (6 + (packet.getPayload() == null ? 0 : packet.getPayload().length));
      if (bitOffset < 0 || bitOffset > maxOffset)
      {
         throw new IllegalArgumentException();
      }

      // if the offset is in the ack field
      if (bitOffset < 16)
      {
         packet.setAck((short) (packet.getAck() ^ 1 << bitOffset));
      }

      // else if the offset is in the checksum field
      else if (bitOffset < 32)
      {
         packet.setChecksum((short) (packet.getChecksum() ^ 1 << bitOffset - 16));
      }

      // else if the offset is in the checksum field
      else if (bitOffset < 48)
      {
         packet.setSequence((short) (packet.getSequence() ^ 1 << bitOffset - 32));
      }

      // the offset must be in the payload
      else
      {
         final int byteOffset = (bitOffset - 48) / 8;
         final int offset = bitOffset % 8;

         final byte[] bytes = packet.getPayload();
         bytes[byteOffset] ^= 1 << offset;

         packet.setPayload(bytes);
      }
   }

   /**
    * generate the time of arrival for the transmitted packet
    *
    * @return the arrival time for the packet
    */
   private long createTravelTime()
   {
      return currentTime + random.nextInt(getTimeBetweenMessages()) + 1;
   }

   /**
    * dump the given byte array to standard out. This will only print out the first {@link #dumpLimit} bytes.
    *
    * @param data
    *           the data to display
    */
   void dumpBytes(final byte[] data)
   {
      if (data == null)
      {
         throw new IllegalArgumentException();
      }

      final int maxLength = Math.min(dumpLimit, data.length);
      final String string = toString(data, maxLength);

      System.out.println(string);
   }

   /**
    * generate a new message to send and add it to the event queue
    */
   void generateSendDataEvent()
   {
      final NetworkEvent event = new NetworkEvent();

      final long eventTime = createTravelTime();
      event.setEventTime(eventTime);
      event.setEventType(EventType.FromApplication);

      // if sending data in both directions, randomly select the side.
      if (isBidirectional() && random.nextDouble() < 0.50)
      {
         event.setRecipient(getSideB());
      }
      else
      {
         event.setRecipient(getSideA());
      }

      eventQueue.add(event);
   }

   /**
    * @return the currentTime
    */
   public long getCurrentTime()
   {
      return currentTime;
   }

   /**
    * @return the dumpLimit
    */
   public int getDumpLimit()
   {
      return dumpLimit;
   }

   /**
    * get the arrival time for last packet to be received by the given Layer4 module
    *
    * @param receiver
    *           the module to receiving the data
    * @return the time of the last packet to arrive at the given receiver, or the current time if no packets are in
    *         transit.
    */
   private long getLastArrivalTime(final Layer4 receiver)
   {
      long time = currentTime;

      for (final NetworkEvent event : eventQueue)
      {
         if (event.getRecipient() == receiver)
         {
            time = event.getEventTime();
         }
      }
      return time;
   }

   /**
    * @return the maxNumberOfMessagesToSend
    */
   public long getMaxNumberOfMessagesToSend()
   {
      return maxNumberOfMessagesToSend;
   }

   /**
    * @return the payloadSize
    */
   public int getPayloadSize()
   {
      return payloadSize;
   }

   /**
    * @return the probabilityOfCorruption
    */
   public double getProbabilityOfCorruption()
   {
      return probabilityOfCorruption;
   }

   /**
    * @return the probabilityOfLoss
    */
   public double getProbabilityOfLoss()
   {
      return probabilityOfLoss;
   }

   /**
    * @return the sideA
    */
   public Layer4 getSideA()
   {
      return sideA;
   }

   /**
    * @return the sideB
    */
   public Layer4 getSideB()
   {
      return sideB;
   }

   /**
    * @return the timeBetweenMessages
    */
   public int getTimeBetweenMessages()
   {
      return timeBetweenMessages;
   }

   /**
    * @return the traceLevel
    */
   public int getTraceLevel()
   {
      return traceLevel;
   }

   /**
    * @return the transmissionTime
    */
   public int getTransmissionTime()
   {
      return transmissionTime;
   }

   /**
    * @return the variabilityBetweenMessages
    */
   public int getVariabilityBetweenMessages()
   {
      return variabilityBetweenMessages;
   }

   /**
    * initialize the simulation. Dump all the simulation parameters, set the logging level for the protocol modules, and
    * call their initialization methods.
    *
    * @return <code>true</code> if the initialization succeeds, or <code>false</code> if there is a configuration error
    */
   private boolean init()
   {
      System.out.println("Simulation starting...");
      System.out.println(String.format("\t%-35s %s", "Traffic is:", isBidirectional() ? "Both ways" : "One way"));
      System.out.println(String.format("\t%-35s %d", "The number of messages to send:",
            Long.valueOf(getMaxNumberOfMessagesToSend())));
      System.out.println(String.format("\t%-35s %2.0f%%", "Chance of losing a packet:",
            Double.valueOf(getProbabilityOfLoss() * 100)));
      System.out.println(String.format("\t%-35s %2.0f%%", "Chance of packet corruption:",
            Double.valueOf(getProbabilityOfCorruption() * 100)));
      System.out.println(String.format("\t%-35s %d", "The time between messages is about:",
            Long.valueOf(getTimeBetweenMessages())));

      // set the debugging level in the protocol modules
      if (getSideA() == null || getSideB() == null)
      {
         System.err.println("Don't have both a source and a destination in the simulation");
         return false;
      }
      getSideA().setTraceLevel(getTraceLevel());
      getSideB().setTraceLevel(getTraceLevel());

      // initialize the protocol modules
      try
      {
         sideA.init();
         sideB.init();
      }
      catch (final Throwable t)
      {
         System.err.println("Cannot initialize protocol modules");
         return false;
      }

      return true;
   }

   /**
    * @return the bidirectional
    */
   public boolean isBidirectional()
   {
      return bidirectional;
   }

   /**
    * Run the network simulation. This will return when all the simulated events have been processed.
    */
   public void run()
   {
      long messagesCreated = 0;

      if (!init())
      {
         System.err.println("Simulation ending");
         return;
      }

      // start with the first event - this assumes at least 1 packet is intended to be sent
      generateSendDataEvent();

      // run until no future events
      while (!eventQueue.isEmpty())
      {
         final NetworkEvent event = eventQueue.poll();
         currentTime = event.getEventTime();

         if (getTraceLevel() > 0)
         {
            System.out.println(String.format("\n\tEvent: %s, time: %6d, %s", event.getRecipient() == getSideA() ? "A"
                  : "B", Long.valueOf(currentTime), event.getEventType()));
         }

         switch (event.getEventType())
         {
         case FromApplication:
            // if there are more messages to send in the simulation, generate the time of the next message
            if (messagesCreated < getMaxNumberOfMessagesToSend())
            {
               generateSendDataEvent();
               messagesCreated++;
            }

            // create the message data
            final byte[] message = new byte[payloadSize];
            Arrays.fill(message, (byte) ('a' + (messagesCreated - 1) % 26));

            // log the message
            if (getTraceLevel() > 1)
            {
               System.out.print("\tMessage from application arrived:  ");
               dumpBytes(message);
            }

            // now let the protocol module have the message
            try
            {
               event.getRecipient().send(message);
            }
            catch (final Throwable t)
            {
               System.err.println("Cannot send message from application");
               t.printStackTrace(System.err);
            }
            break;

         case FromNetwork:
            if (getTraceLevel() > 1)
            {
               System.out.println("\tMessage from the network for " + (event.getRecipient() == getSideA() ? "A" : "B"));
            }
            try
            {
               event.getRecipient().receive(event.getEventPacket());
            }
            catch (final Throwable t)
            {
               System.err.println("Failed to receive a packet");
               t.printStackTrace(System.err);
            }
            break;

         case Timer:
            if (getTraceLevel() > 1)
            {
               System.out.println("\ttimer interrupt for " + (event.getRecipient() == getSideA() ? "A" : "B"));
            }
            try
            {
               event.getRecipient().timeout();
            }
            catch (final Throwable t)
            {
               System.err.println("Timeout for side " + (event.getRecipient() == getSideA() ? "A" : "B") + " Failed");
               t.printStackTrace(System.err);
            }
            break;

         default:
            System.err.println("Invalid event type: terminating");
            return;
         }
      }
   }

   /**
    * set the behavior of the simulation. Set the direction of transmission, the number of packets to send, the rate at
    * which packets are lost, the rate at which packets are corrupted, and the rate at which messages are send by the
    * application layer.
    *
    * @param sendBothWays
    *           true if the messages should be send in both directions (extra-credit)
    * @param packetCount
    *           the number of packets to send in the simulation
    * @param lossRate
    *           the rate at which packets are lost. This is a value between 0.0 and 1.0.
    * @param corruptionRate
    *           the rate at which packets are corrupted. This is value between 0.0 and 1.0
    * @param timeBetweenMessages
    *           the time between the messages sent from the application layer
    * @param variabilityBetweenMessages
    *           the variability of the message timings
    */
   public void setBehavior(final boolean sendBothWays, final int packetCount, final double lossRate,
         final double corruptionRate, final int timeBetweenMessages, final int variabilityBetweenMessages)
   {
      setBidirectional(sendBothWays);
      setMaxNumberOfMessagesToSend(packetCount);
      setProbabilityOfLoss(lossRate);
      setProbabilityOfCorruption(corruptionRate);
      setTimeBetweenMessages(timeBetweenMessages);
      setVariabilityBetweenMessages(variabilityBetweenMessages);
   }

   /**
    * @param bidirectional
    *           the bidirectional to set
    */
   public void setBidirectional(final boolean bidirectional)
   {
      this.bidirectional = bidirectional;
   }

   /**
    * @param dumpLimit
    *           the dumpLimit to set
    */
   public void setDumpLimit(final int dumpLimit)
   {
      this.dumpLimit = dumpLimit;
   }

   /**
    * @param maxNumberOfMessagesToSend
    *           the maxNumberOfMessagesToSend to set
    */
   public void setMaxNumberOfMessagesToSend(final long maxNumberOfMessagesToSend)
   {
      this.maxNumberOfMessagesToSend = maxNumberOfMessagesToSend;
   }

   /**
    * @param payloadSize
    *           the payloadSize to set
    */
   public void setPayloadSize(final int payloadSize)
   {
      this.payloadSize = payloadSize;
   }

   /**
    * @param probabilityOfCorruption
    *           the probabilityOfCorruption to set
    */
   public void setProbabilityOfCorruption(final double probabilityOfCorruption)
   {
      this.probabilityOfCorruption = probabilityOfCorruption;
   }

   /**
    * @param probabilityOfLoss
    *           the probabilityOfLoss to set
    */
   public void setProbabilityOfLoss(final double probabilityOfLoss)
   {
      this.probabilityOfLoss = probabilityOfLoss;
   }

   /**
    * @param sideA
    *           the sideA to set
    */
   public void setSideA(final Layer4 sideA)
   {
      this.sideA = sideA;
   }

   /**
    * @param sideB
    *           the sideB to set
    */
   public void setSideB(final Layer4 sideB)
   {
      this.sideB = sideB;
   }

   /**
    * @param timeBetweenMessages
    *           the timeBetweenMessages to set
    */
   public void setTimeBetweenMessages(final int timeBetweenMessages)
   {
      this.timeBetweenMessages = timeBetweenMessages;
   }

   /**
    * @param traceLevel
    *           the traceLevel to set
    */
   public void setTraceLevel(final int traceLevel)
   {
      this.traceLevel = traceLevel;
   }

   /**
    * @param transmissionTime
    *           the transmissionTime to set
    */
   public void setTransmissionTime(final int transmissionTime)
   {
      this.transmissionTime = transmissionTime;
   }

   /**
    * @param variabilityBetweenMessages
    *           the variabilityBetweenMessages to set
    */
   public void setVariabilityBetweenMessages(final int variabilityBetweenMessages)
   {
      this.variabilityBetweenMessages = variabilityBetweenMessages;
   }

   /**
    * start a timer with the given duration. When the timer expires, the owner.timeout() method will be called.
    *
    * @param owner
    *           the protocol module that started the timer
    * @param duration
    *           the length of time to wait
    */
   public void startTimer(final Layer4 owner, final int duration)
   {
      if (owner == null || duration == 0)
      {
         throw new IllegalArgumentException();
      }

      // check that the timer is not already set for this owner
      if (stopTimer(owner))
      {
         System.err.println("\tWarning: starting a timer that is already started. Restarting.");
      }

      // ok, now create a time event for the proper simulation time
      final NetworkEvent event = new NetworkEvent();
      event.setEventTime(currentTime + duration);
      event.setEventType(EventType.Timer);
      event.setRecipient(owner);

      eventQueue.add(event);
   }

   /**
    * Stop the timer for the given protocol module.
    *
    * @param owner
    *           the owner of the timer
    * @return <code>true</code> if the timer was found and stopped, or <code>false</code> if no timer found
    */
   public boolean stopTimer(final Layer4 owner)
   {
      for (final NetworkEvent event : eventQueue)
      {
         if (event.getEventType() == EventType.Timer || event.getRecipient() == owner)
         {
            eventQueue.remove(event);
            return true;
         }
      }
      if (getTraceLevel() > 0)
      {
         System.out.println("\tNo timer found for " + (owner == getSideA() ? "A" : "B"));
      }
      return false;
   }

   /**
    * @param layer4
    * @param payload
    */
   public void toApplication(final Layer4 layer4, final byte[] payload)
   {
      if (getTraceLevel() > 0)
      {
         System.out.print("\tApplication " + (layer4 == getSideA() ? "A" : "B") + " received " + payload.length
               + " bytes of data:  ");
         dumpBytes(payload);
      }
   }

   /**
    * format the given byte array as a character string, IFF the bytes are printable. If the bytes are not printable,
    * then they are converted to hex.
    *
    * @param data
    *           the byte array to format
    * @param maxLength
    *           the maximum number of byte to translate
    * @return the
    */
   String toString(final byte[] data, final int maxLength)
   {
      if (data == null || maxLength < 0)
      {
         throw new IllegalArgumentException();
      }

      final StringBuilder builder = new StringBuilder();
      String chunk;
      for (int i = 0; i < Math.min(maxLength, data.length); i++)
      {
         final byte current = data[i];
         if (Character.isAlphabetic(current))
         {
            final Integer value = Integer.valueOf(current);
            chunk = String.format("%c", value, i % 2 == 0 ? "" : " ");
         }
         else
         {
            chunk = String.format(" 0x%02x ", Byte.valueOf(current));
         }
         builder.append(chunk);
      }
      if (maxLength < data.length)
      {
         builder.append("...");
      }
      return builder.toString();
   }

   /**
    * transmit the given packet to the destination. If toB is true, this sends it to side B.
    *
    * @param sender
    *           the protocol module that sent this packet
    * @param packet
    *           the packet to transmit over the network
    */
   public void transmit(final Layer4 sender, final Packet packet)
   {
      // simulate loss of the packet
      if (random.nextDouble() < getProbabilityOfLoss())
      {
         if (getTraceLevel() > 0)
         {
            System.out.println("\ttransmit: packet lost");
            return;
         }
      }

      // copy the packet, so the user can change it if they want
      final Packet copy = new Packet();
      copy.setAck(packet.getAck());
      copy.setChecksum(packet.getChecksum());
      copy.setSequence(packet.getSequence());

      if (packet.getPayload() != null)
      {
         final byte[] payload = packet.getPayload();
         copy.setPayload(Arrays.copyOf(payload, payload.length));
      }

      // generate a new event to represent the arrival of the packet at the destination
      final NetworkEvent event = new NetworkEvent();
      event.setEventPacket(packet);
      event.setEventType(EventType.FromNetwork);
      event.setRecipient(sender == getSideA() ? getSideB() : getSideA());

      // finally, compute the arrival time of packet at the other end. The medium can not reorder packets, so make sure
      // packet arrives between 1 and N time units after the latest arrival time of packets currently in the medium on
      // their way to the destination
      final long lastTime = getLastArrivalTime(event.getRecipient());
      event.setEventTime(lastTime + random.nextInt(getTransmissionTime()));

      // simulation corruption by changing the checksum
      if (random.nextDouble() < probabilityOfCorruption)
      {
         if (getTraceLevel() > 0)
         {
            System.out.println("\ttransmit: corrupting packet");
         }

         // what is the length of the packet? if the packet has a payload, add the payload length to the header
         final int totalBits = packet.getPayload() != null ? (packet.getPayload().length + 6) * 8 : 48;
         final int bitOffset = random.nextInt(totalBits);

         corruptPacket(copy, bitOffset);
      }

      eventQueue.add(event);
   }

}
