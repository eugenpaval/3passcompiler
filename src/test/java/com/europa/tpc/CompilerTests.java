package com.europa.tpc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

public class CompilerTests 
{
  @Test
  public void testSimpleProg()
  {
    String prog = "[ x y z ] ( 2*3*x + 5*y - 3*z ) / (1 + 3 + 2*2)";
    Compiler compiler = new Compiler();

    // {'op':'/','a':{'op':'-','a':{'op':'+','a':{'op':'*','a':{'op':'*','a':{'op':'imm','n':2},'b':{'op':'imm','n':3}},'b':{'op':'arg','n':0}},'b':{'op':'*','a':{'op':'imm','n':5},'b':{'op':'arg','n':1}}},'b':{'op':'*','a':{'op':'imm','n':3},'b':{'op':'arg','n':2}}},'b':{'op':'+','a':{'op':'+','a':{'op':'imm','n':1},'b':{'op':'imm','n':3}},'b':{'op':'*','a':{'op':'imm','n':2},'b':{'op':'imm','n':2}}}}
    Ast t1 = new BinOp("/", new BinOp("-", new BinOp("+", new BinOp("*", new BinOp("*", new UnOp("imm", 2), new UnOp("imm", 3)), new UnOp("arg", 0)), new BinOp("*", new UnOp("imm", 5), new UnOp("arg", 1))), new BinOp("*", new UnOp("imm", 3), new UnOp("arg", 2))), new BinOp("+", new BinOp("+", new UnOp("imm", 1), new UnOp("imm", 3)), new BinOp("*", new UnOp("imm", 2), new UnOp("imm", 2))));
    Ast p1 = compiler.pass1(prog);
    assertEquals(t1, p1, "Pass 1");

    // {'op':'/','a':{'op':'-','a':{'op':'+','a':{'op':'*','a':{'op':'imm','n':6},'b':{'op':'arg','n':0}},'b':{'op':'*','a':{'op':'imm','n':5},'b':{'op':'arg','n':1}}},'b':{'op':'*','a':{'op':'imm','n':3},'b':{'op':'arg','n':2}}},'b':{'op':'imm','n':8}}
    Ast t2 = new BinOp("/", new BinOp("-", new BinOp("+", new BinOp("*", new UnOp("imm", 6), new UnOp("arg", 0)), new BinOp("*", new UnOp("imm", 5), new UnOp("arg", 1))), new BinOp("*", new UnOp("imm", 3), new UnOp("arg", 2))), new UnOp("imm", 8));
    Ast p2 = compiler.pass2(p1);
    assertEquals(t2, p2, "Pass 2");

    List<String> p3 = compiler.pass3(p2);
    assertEquals(3, Simulator.simulate(p3, 4, 0, 0), "prog(4,0,0) == 3");
    assertEquals(8, Simulator.simulate(p3, 4, 8, 0), "prog(4,8,0) == 8");
    assertEquals(2, Simulator.simulate(p3, 4, 8, 16), "prog(4,8,16) == 2");
  }
}
