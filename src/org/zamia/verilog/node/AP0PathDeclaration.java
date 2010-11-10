/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.zamia.verilog.node;

import org.zamia.verilog.analysis.*;

@SuppressWarnings("nls")
public final class AP0PathDeclaration extends PPathDeclaration
{
    private PSimplePathDeclaration _simplePathDeclaration_;
    private TTSemicolon _tSemicolon_;

    public AP0PathDeclaration()
    {
        // Constructor
    }

    public AP0PathDeclaration(
        @SuppressWarnings("hiding") PSimplePathDeclaration _simplePathDeclaration_,
        @SuppressWarnings("hiding") TTSemicolon _tSemicolon_)
    {
        // Constructor
        setSimplePathDeclaration(_simplePathDeclaration_);

        setTSemicolon(_tSemicolon_);

    }

    @Override
    public Object clone()
    {
        return new AP0PathDeclaration(
            cloneNode(this._simplePathDeclaration_),
            cloneNode(this._tSemicolon_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAP0PathDeclaration(this);
    }

    public PSimplePathDeclaration getSimplePathDeclaration()
    {
        return this._simplePathDeclaration_;
    }

    public void setSimplePathDeclaration(PSimplePathDeclaration node)
    {
        if(this._simplePathDeclaration_ != null)
        {
            this._simplePathDeclaration_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._simplePathDeclaration_ = node;
    }

    public TTSemicolon getTSemicolon()
    {
        return this._tSemicolon_;
    }

    public void setTSemicolon(TTSemicolon node)
    {
        if(this._tSemicolon_ != null)
        {
            this._tSemicolon_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._tSemicolon_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._simplePathDeclaration_)
            + toString(this._tSemicolon_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._simplePathDeclaration_ == child)
        {
            this._simplePathDeclaration_ = null;
            return;
        }

        if(this._tSemicolon_ == child)
        {
            this._tSemicolon_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._simplePathDeclaration_ == oldChild)
        {
            setSimplePathDeclaration((PSimplePathDeclaration) newChild);
            return;
        }

        if(this._tSemicolon_ == oldChild)
        {
            setTSemicolon((TTSemicolon) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}