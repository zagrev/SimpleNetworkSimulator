SimpleNetworkSimulator
======================

This is a simple framework to provide students with a network simulation so that they can implement Layer4 protocol modules.

Students create a new subclass of Layer4 and implement send() and receive() for the protocol they wish to create. The network simulator will send messages in a single direction from Side A to Side B and verify that the protocol works correctly. Currently, the simulator just sends the messages and it's up to the human reading the output to determine if the protocol worked correctly, but the plan is to add a third project to run known simulations and verify the behavior of the student written Layer4 modules.

This is based on the work in C: ALTERNATING BIT AND GO-BACK-N NETWORK EMULATOR: VERSION 1.1  by J.F.Kurose
