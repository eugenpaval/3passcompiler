package com.europa.tpc;

public class BinOp implements Ast
{
    private String _operator;
    private Ast _a;
    private Ast _b;

    public BinOp(String operator, Ast a, Ast b)
    {
        _a = a;
        _b = b;
    }

    public String op()
    {
        return _operator;
    }

    public Ast a()
    {
        return _a;
    }

    public Ast b()
    {
        return _b;
    }
}