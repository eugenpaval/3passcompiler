package com.europa.tpc;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

class Simulator 
{
  public static int simulate(List<String> asm, int... argv) 
  {
    int r0 = 0;
    int r1 = 0;
    Deque<Integer> stack = new LinkedList<>();

    for (String ins : asm) 
    {
      String code = ins.replaceAll("\\s+[0-9]+", "");
      switch (code) 
      {
        case "IM": r0 = Integer.parseInt(ins.substring(2).trim()); break;
        case "AR": r0 = argv[Integer.parseInt(ins.substring(2).trim())]; break;
        case "SW": int tmp = r0; r0 = r1; r1 = tmp; break;
        case "PU": stack.addLast(r0); break;
        case "PO": r0 = stack.removeLast(); break;
        case "AD": r0 += r1; break;
        case "SU": r0 -= r1; break;
        case "MU": r0 *= r1; break;
        case "DI": r0 /= r1; break;
      }
    }

    return r0;
  }
}