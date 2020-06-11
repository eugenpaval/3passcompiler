package com.europa.tpc;

import com.google.gson.Gson;

public class BinOp implements Ast
{
    private String _operator;
    private Ast _a;
    private Ast _b;

    public BinOp(String operator, Ast a, Ast b)
    {
        _operator = operator;
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

    @Override
    public String toString()
    {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == null || other.getClass() != getClass())
            return false;
        if (other == this)
            return true;

        return toString().equals(other.toString());
    }
}