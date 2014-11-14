/**
 * 
 */
package ceg4400assign2;

/**
 * The network packet for the simulation. The protocol module is expected to fill out all the information for the
 * message.
 */
final public class Packet
{
   /** the sequence number of the packet */
   private short sequence;
   /** the acknowledgement number of the packet */
   private short ack;
   /** the checksum of the packet */
   private short checksum;
   /** the payload of the packet */
   private byte[] payload;

   /**
    * get the acknowledgement number of the packet
    *
    * @return the acknowledgement number
    */
   public short getAck()
   {
      return ack;
   }

   /**
    * get the checksum for the packet
    *
    * @return the checksum the checksum of the packet
    */
   public short getChecksum()
   {
      return checksum;
   }

   /**
    * get the payload data for the packet
    *
    * @return the payload data for the packet
    */
   public byte[] getPayload()
   {
      return payload;
   }

   /**
    * get the sequence number for the packet
    *
    * @return the sequence number for the packet
    */
   public short getSequence()
   {
      return sequence;
   }

   /**
    * set the acknowledgement number for this packet
    *
    * @param ack
    *           the the acknowledgement number to set in the packet
    */
   public void setAck(final short ack)
   {
      this.ack = ack;
   }

   /**
    * set the checksum for this packet
    *
    * @param checksum
    *           the checksum to set in this packet
    */
   public void setChecksum(final short checksum)
   {
      this.checksum = checksum;
   }

   /**
    * set the payload of this packet
    *
    * @param payload
    *           the payload data to set
    */
   public void setPayload(final byte[] payload)
   {
      this.payload = payload;
   }

   /**
    * set the sequence number of this packet
    *
    * @param sequence
    *           the sequence number to set
    */
   public void setSequence(final short sequence)
   {
      this.sequence = sequence;
   }
}