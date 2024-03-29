/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.zamia.verilog.node;

import org.zamia.verilog.analysis.*;

@SuppressWarnings("nls")
public final class AP0NetType extends PNetType
{
    private TKSupply0 _kSupply0_;

    public AP0NetType()
    {
        // Constructor
    }

    public AP0NetType(
        @SuppressWarnings("hiding") TKSupply0 _kSupply0_)
    {
        // Constructor
        setKSupply0(_kSupply0_);

    }

    @Override
    public Object clone()
    {
        return new AP0NetType(
            cloneNode(this._kSupply0_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAP0NetType(this);
    }

    public TKSupply0 getKSupply0()
    {
        return this._kSupply0_;
    }

    public void setKSupply0(TKSupply0 node)
    {
        if(this._kSupply0_ != null)
        {
            this._kSupply0_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._kSupply0_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._kSupply0_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._kSupply0_ == child)
        {
            this._kSupply0_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._kSupply0_ == oldChild)
        {
            setKSupply0((TKSupply0) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
