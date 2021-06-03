using Demo;
using Ice;
using System;
using System.Collections.Generic;

namespace Client_csharp
{
    class Program
    {
        static void Main(string[] args)
        {
            Communicator communicator = Util.initialize();
            ObjectPrx proxy = communicator.stringToProxy("calc/calc11:tcp -h 127.0.0.2 -p 10000 -z : udp -h 127.0.0.2 -p 10000 -z");
            if (proxy == null)
            {
                throw new System.Exception("some exception line 13");
            }

            OutputStream outputStream;
            InputStream inputStream;
            byte[] inParams;
            byte[] outParams;
            int addResult;
            double avgResult;
            PowSeqResults powSeqResults = null;

            // add two ints
            int x = 10, y = 4;
            outputStream = new OutputStream(communicator);
            outputStream.startEncapsulation();
            outputStream.writeInt(x);
            outputStream.writeInt(y);
            outputStream.endEncapsulation();
            inParams = outputStream.finished();
            if (proxy.ice_invoke("add", OperationMode.Normal, inParams, out outParams))
            {
                inputStream = new InputStream(communicator, outParams);
                inputStream.startEncapsulation();
                addResult = inputStream.readInt();
                inputStream.endEncapsulation();
                Console.WriteLine(x + " + " + y + " = " + addResult);
            }

            // pow
            //int[] seq = new int[] { 1, 2, 3, 4, 5 };
            int[] seq = new int[] { -1, -2, -3, -4, -5 };
            PowSeq inputSeq = new PowSeq(5, seq);
            outputStream = new OutputStream(communicator);
            outputStream.startEncapsulation();
            PowSeq.ice_write(outputStream, inputSeq);
            outputStream.endEncapsulation();
            inParams = outputStream.finished();
            if (proxy.ice_invoke("pow", OperationMode.Normal, inParams, out outParams))
            {
                inputStream = new InputStream(communicator, outParams);
                inputStream.startEncapsulation();
                powSeqResults = PowSeqResults.ice_read(inputStream);
                inputStream.endEncapsulation();
                foreach (PowSeq powSeq in powSeqResults.sequences)
                {
                    Console.Write(powSeq.exponent + ": ");
                    foreach (int i in powSeq.seq)
                    {
                        Console.Write(i + " ");
                    }
                    Console.WriteLine();
                }
            }

            // avg
            outputStream = new OutputStream(communicator);
            outputStream.startEncapsulation();
            PowSeqResults.ice_write(outputStream, powSeqResults);  //might be null
            outputStream.endEncapsulation();
            inParams = outputStream.finished();
            if (proxy.ice_invoke("avg", OperationMode.Normal, inParams, out outParams))
            {
                inputStream = new InputStream(communicator, outParams);
                inputStream.startEncapsulation();
                avgResult = inputStream.readDouble();
                inputStream.endEncapsulation();
                Console.WriteLine("avg: " + avgResult);
            }
        }
    }
}
