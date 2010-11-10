/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.zamia.verilog.node;

import org.zamia.verilog.analysis.*;

@SuppressWarnings("nls")
public final class AP6ModulePathPrimary extends PModulePathPrimary
{
    private TTLparen _tLparen_;
    private PModulePathMintypmaxExpression _modulePathMintypmaxExpression_;
    private TTRparen _tRparen_;

    public AP6ModulePathPrimary()
    {
        // Constructor
    }

    public AP6ModulePathPrimary(
        @SuppressWarnings("hiding") TTLparen _tLparen_,
        @SuppressWarnings("hiding") PModulePathMintypmaxExpression _modulePathMintypmaxExpression_,
        @SuppressWarnings("hiding") TTRparen _tRparen_)
    {
        // Constructor
        setTLparen(_tLparen_);

        setModulePathMintypmaxExpression(_modulePathMintypmaxExpression_);

        setTRparen(_tRparen_);

    }

    @Override
    public Object clone()
    {
        return new AP6ModulePathPrimary(
            cloneNode(this._tLparen_),
            cloneNode(this._modulePathMintypmaxExpression_),
            cloneNode(this._tRparen_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAP6ModulePathPrimary(this);
    }

    public TTLparen getTLparen()
    {
        return this._tLparen_;
    }

    public void setTLparen(TTLparen node)
    {
        if(this._tLparen_ != null)
        {
            this._tLparen_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._tLparen_ = node;
    }

    public PModulePathMintypmaxExpression getModulePathMintypmaxExpression()
    {
        return this._modulePathMintypmaxExpression_;
    }

    public void setModulePathMintypmaxExpression(PModulePathMintypmaxExpression node)
    {
        if(this._modulePathMintypmaxExpression_ != null)
        {
            this._modulePathMintypmaxExpression_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._modulePathMintypmaxExpression_ = node;
    }

    public TTRparen getTRparen()
    {
        return this._tRparen_;
    }

    public void setTRparen(TTRparen node)
    {
        if(this._tRparen_ != null)
        {
            this._tRparen_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._tRparen_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._tLparen_)
            + toString(this._modulePathMintypmaxExpression_)
            + toString(this._tRparen_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._tLparen_ == child)
        {
            this._tLparen_ = null;
            return;
        }

        if(this._modulePathMintypmaxExpression_ == child)
        {
            this._modulePathMintypmaxExpression_ = null;
            return;
        }

        if(this._tRparen_ == child)
        {
            this._tRparen_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._tLparen_ == oldChild)
        {
            setTLparen((TTLparen) newChild);
            return;
        }

        if(this._modulePathMintypmaxExpression_ == oldChild)
        {
            setModulePathMintypmaxExpression((PModulePathMintypmaxExpression) newChild);
            return;
        }

        if(this._tRparen_ == oldChild)
        {
            setTRparen((TTRparen) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}