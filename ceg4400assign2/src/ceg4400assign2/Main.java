/**
 *
 */
package ceg4400assign2;

/**
 *
 */
public class Main
{

   /**
    * create a layer 4 protocol module. Students should override this method to implement their own protocol.
    *
    * @param simulator
    *           the simulator to use to send the data and set the timers
    * @return the newly created Layer4 protocol module to use in the network simulation
    */
   private static Layer4 createLayer4(final NetworkSimulator simulator)
   {
      // TODO Auto-generated method stub
      return new Layer4(simulator);
   }

   /**
    * Start the simulation. This takes no arguments.
    *
    * @param args
    */
   public static void main(final String[] args)
   {
      final NetworkSimulator simulator = new NetworkSimulator();

      final boolean bidirectional = false;
      final int packetCount = 10;
      final double lossRate = 0.1;
      final double corruptionRate = 0.4;
      final int timeBetweenMessages = 1000;
      final int variabilityBetweenMessages = 200;
      final int traceLevel = 1;

      // TODO ask the user for the conditions of the simulation

      simulator.setBehavior(bidirectional, packetCount, lossRate, corruptionRate, timeBetweenMessages,
            variabilityBetweenMessages);

      final Layer4 sideA = createLayer4(simulator);
      final Layer4 sideB = createLayer4(simulator);

      simulator.setSideA(sideA);
      simulator.setSideB(sideB);

      simulator.setTraceLevel(traceLevel);
      simulator.run();
   }

}
