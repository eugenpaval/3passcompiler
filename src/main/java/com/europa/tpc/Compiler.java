package com.europa.tpc;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.function.*;


public class Compiler 
{
  private final Parser _parser = new Parser();
  private final Assembler _assembler = new Assembler();
  private static final Map<String, BiFunction<Integer, Integer, Integer>> _evalFuncs = new HashMap<String, BiFunction<Integer, Integer, Integer>>()
  {
    private static final long serialVersionUID = 1L;

    {
      put("+", (x,y) -> x + y);
      put("-", (x,y) -> x - y);
      put("*", (x,y) -> x * y);
      put("/", (x,y) -> x / y);
    }
  };

  public List<String> compile(String prog) 
  {
    return pass3(pass2(pass1(prog)));
  }

  /**
   * Returns an un-optimized AST
   */
  public Ast pass1(String prog) 
  {
    Deque<String> tokens = tokenize(prog);

    return _parser.Parse(tokens);
  }

  /**
   * Returns an AST with constant expressions reduced
   */
  public Ast pass2(Ast ast) 
  {
    if (ast.getClass() == UnOp.class)
      return ast;

    BinOp binop = (BinOp)ast;
    Ast a = pass2(binop.a());
    Ast b = pass2(binop.b());

    if (a.op().equals("imm") && b.op().equals("imm"))
    {
      int va = ((UnOp)a).n();
      int vb = ((UnOp)b).n();

      return new UnOp("imm", _evalFuncs.get(binop.op()).apply(va, vb));
    }

    return new BinOp(binop.op(), a, b);
  }

  /**
   * Returns assembly instructions
   */
  public List<String> pass3(Ast ast) 
  {
    return _assembler.generateInstructions(ast);
  }

  private static Deque<String> tokenize(String prog) 
  {
    Deque<String> tokens = new LinkedList<>();
    Pattern pattern = Pattern.compile("[-+*/()\\[\\]]|[a-zA-Z]+|\\d+");
    Matcher m = pattern.matcher(prog);

    while (m.find())
      tokens.add(m.group());
    
    tokens.add("$"); // end-of-stream
    return tokens;
  }
}

class Parser 
{
  private Deque<String> _tokens;
  private List<String> _parameters = new ArrayList<String>();

  public Ast Parse(Deque<String> tokens)
  {
      _tokens = tokens;
      _tokens.pop(); // '['

      String token = _tokens.pop();
      while (!token.equals("]"))
      {
        _parameters.add(token);
        token = _tokens.pop();
      }

      Ast ast = ParseExpr();
      _tokens.pop(); // $

      return ast;
  }

  private Ast ParseExpr()
  {
    Ast ast = ParseTerm();
    String token = _tokens.peek();
    
    while (token.equals("+") || token.equals("-"))
    {
      _tokens.pop();
      ast = new BinOp(token, ast, ParseTerm());
      token = _tokens.peek();
    }

    return ast;
  }

  private Ast ParseTerm()
  {
    Ast ast = ParseFactor();
    String token = _tokens.peek();

    while (token.equals("*") || token.equals("/"))
    {
      _tokens.pop();
      ast = new BinOp(token, ast, ParseFactor());
      token = _tokens.peek();
    }

    return ast;
  }

  private Ast ParseFactor()
  {
    String token = _tokens.pop();

    switch (getType(token))
    {
      case Identifier:
        int pos = _parameters.indexOf(token);
        return new UnOp("arg", pos);

      case Number:
        int value = Integer.parseInt(token);
        return new UnOp("imm", value);

      case LParen:
        Ast ast = ParseExpr();
        _tokens.pop(); // ')'
        return ast;
    }

    return null;
  }

  private TokenType getType(String token)
  {
    try
    {
      if (token.equals("("))
        return TokenType.LParen;

      Integer.parseInt(token);
      return TokenType.Number;
    } catch (Exception e) 
    {
      return TokenType.Identifier;
    }
  }
}

enum TokenType
{
  Identifier,
  Number,
  LParen
}

class Assembler
{
    private Map<String, String> _mapOpToInstr = new HashMap<String, String>();

    public Assembler()
    {
        _mapOpToInstr.put("+", "AD");
        _mapOpToInstr.put("-", "SU");
        _mapOpToInstr.put("*", "MU");
        _mapOpToInstr.put("/", "DI");
    }

    public List<String> generateInstructions(Ast ast)
    {
        if (ast.getClass() == UnOp.class)
            return generate((UnOp)ast);

        return generate((BinOp)ast);
    }

    private List<String> generate(UnOp unop)
    {
        List<String> result = new ArrayList<String>();

        if (unop.op() == "imm")
            result.add(String.format("IM %d", unop.n()));
        else
            result.add(String.format("AR %d", unop.n()));

          result.add("PU");

        return result;
    }

    private List<String> generate(BinOp binop)
    {
        List<String> result = new ArrayList<String>();

        result.addAll(generateInstructions(binop.a()));
        result.addAll(generateInstructions(binop.b()));
        result.add("PO");
        result.add("SW");
        result.add("PO");
        result.add(_mapOpToInstr.get(binop.op()));
        result.add("PU");

        return result;
    }
}