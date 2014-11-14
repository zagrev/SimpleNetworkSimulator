/**
 *
 */
package ceg4400assign2;

/**
 *
 */
public class MockUtilities
{
   /**
    * create a network simulator with the appropriate configuration for testing
    *
    * @return a newly created network simulator
    */
   public static NetworkSimulator createNetworkSimulator()
   {
      final NetworkSimulator simulator = new NetworkSimulator();
      simulator.setTraceLevel(1000);

      simulator.setSideA(new MockLayer4(simulator));
      simulator.setSideB(new MockLayer4(simulator));

      return simulator;
   }
}
