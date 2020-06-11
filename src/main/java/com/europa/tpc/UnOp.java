package com.europa.tpc;

import com.google.gson.Gson;

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