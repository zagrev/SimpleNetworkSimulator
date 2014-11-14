/**
 * 
 */
package ceg4400assign2;

/**
 * a dummy layer4 implementation that includes timeout checking
 */
class MockLayer4 extends Layer4
{
   /** flag to determine if the timeout method was called */
   public boolean timeoutCalled = false;

   /**
    * @param network
    */
   public MockLayer4(final NetworkSimulator network)
   {
      super(network);
   }

   /*
    * (non-Javadoc)
    * @see ceg4400assign2.Layer4#timeout()
    */
   @Override
   public void timeout()
   {
      timeoutCalled = true;
   }

}