/**
 *
 */
package ceg4400assign2;

/**
 * This is the protocol module to implement for the ABP or GBN
 */
public class Layer4
{
   /** the network simulator to use when sending messages */
   private final NetworkSimulator network;

   /** the level of output to generate */
   private int traceLevel = 2;

   /** the maximum number of bytes in a packet to dump */
   private int dumpLimit = 20;

   /** how to dump packet data. As character strings or as hex bytes */
   private final boolean dumpAsCharacters = true;

   /**
    * Create a new Layer4 protocol modules
    *
    * @param network
    *           the network simulator to be used to send data.
    */
   public Layer4(final NetworkSimulator network)
   {
      this.network = network;
   }

   /**
    * @param payload
    */
   protected void dumpBytes(final byte[] payload)
   {
      if (payload == null)
      {
         throw new IllegalArgumentException();
      }

      System.out.print("length = " + payload.length + ": ");
      final int maxLength = Math.min(dumpLimit, payload.length);
      for (int i = 0; i < maxLength; i++)
      {
         final Integer value = Integer.valueOf(payload[i]);
         String chunk;
         if (dumpAsCharacters)
         {
            chunk = String.format("%c", value, i % 2 == 0 ? "" : " ");
         }
         else
         {
            chunk = String.format("%2x%s", value, i % 2 == 0 ? "" : " ");
         }
         System.out.print(chunk);
      }
      System.out.println("");
   }

   /**
    * @return the dumpLimit
    */
   public int getDumpLimit()
   {
      return dumpLimit;
   }

   /**
    * @return the traceLevel
    */
   public int getTraceLevel()
   {
      return traceLevel;
   }

   /**
    * Add all the initialization code needed to implement the layer 4 protocol here. This will be called once before any
    * other methods of this class.
    */
   public void init()
   {
      // TODO Student to implement this method
   }

   /**
    * @param packet
    */
   public void receive(final Packet packet)
   {
      // TODO Student to override and implement this method

      if (getTraceLevel() > 0)
      {
         System.out.print("Received a packet from the network: ");
         dumpBytes(packet.getPayload());
      }

      // TODO validate the packet checksum
      // TODO validate the sequence and ack numbers
      // TODO if ok, send on the to the application
      network.toApplication(this, packet.getPayload());
   }

   /**
    * This is a message from Layer 5 (the application) and this data needs to be sent over the network.
    *
    * @param payload
    *           the message to send over the network
    */
   public void send(final byte[] payload)
   {
      // TODO student to implement this method
      if (getTraceLevel() > 0)
      {
         System.out.print("Send a message: ");
         dumpBytes(payload);
      }

      final Packet packet = new Packet();

      packet.setAck((short) 0);
      packet.setChecksum((short) 0);
      packet.setSequence((short) 0);
      packet.setPayload(payload);

      network.transmit(this, packet);
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
    * @param traceLevel
    *           the traceLevel to set
    */
   public void setTraceLevel(final int traceLevel)
   {
      this.traceLevel = traceLevel;
   }

   /**
    * Called when the timer expires
    */
   public void timeout()
   {
      // TODO Student should override this method to handle the interrupt
      System.out.println("Interrupted");
   }
}
