package sr.ice.server;
// **********************************************************************
//
// Copyright (c) 2003-2019 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

import Demo.PowSeq;
import Demo.PowSeqResults;
import com.zeroc.Ice.Current;

import Demo.A;
import Demo.Calc;

public class CalcI implements Calc
{
	private static final long serialVersionUID = -2448962912780867770L;
	long counter = 0;

	@Override
	public long add(int a, int b, Current __current) 
	{
		System.out.println("ADD: a = " + a + ", b = " + b + ", result = " + (a+b));
		
		if(a > 1000 || b > 1000) {
			try { Thread.sleep(6000); } catch(java.lang.InterruptedException ex) { } 
		}
		
		if(__current.ctx.values().size() > 0) System.out.println("There are some properties in the context");
		
		return a + b;
	}
	
	@Override
	public long subtract(int a, int b, Current __current) {
		return 0;
	}


	@Override
	public /*synchronized*/ void op(A a1, short b1, Current current) {
		System.out.println("OP" + (++counter));
		try { Thread.sleep(500); } catch(java.lang.InterruptedException ex) { }		
	}

	@Override
	public PowSeqResults pow(PowSeq inputSeq, Current current) {
		System.out.println("POW");
		PowSeq[] resultSeq = new PowSeq[inputSeq.exponent];
		for (int i = 0; i < inputSeq.exponent; i++) {
			resultSeq[i] = new PowSeq(i + 1, new int[inputSeq.seq.length]);
		}
		for (int i = 0; i < inputSeq.seq.length; i++) {
			for (int j = 0; j < resultSeq.length; j++) {
				resultSeq[j].seq[i] = (int) Math.pow(inputSeq.seq[i], j + 1);
			}
		}
		return new PowSeqResults(resultSeq);
	}

	@Override
	public double avg(PowSeqResults input, Current current) {
		System.out.println("AVG");
		double sum = 0, count = 0;

		for (PowSeq powSeq : input.sequences) {
			for (int i : powSeq.seq) {
				sum += i;
				count++;
			}
		}

		return count > 0 ? sum / count : 0;
	}

}
