package com.europa.tpc;

public class UnOp implements Ast
{
    private int _value;
    private String _operator;

    public UnOp(String operator, int value)
    {
        _operator = operator;
        _value = value;
    }

    public String op()
    {
        return _operator;
    }

    public int n()
    {
        return _value;
    }
}