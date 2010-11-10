/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.zamia.verilog.node;

import org.zamia.verilog.analysis.*;

@SuppressWarnings("nls")
public final class AP2DelayOrEventControl extends PDelayOrEventControl
{
    private TKRepeat _kRepeat_;
    private TTLparen _tLparen_;
    private PExpression _expression_;
    private TTRparen _tRparen_;
    private PEventControl _eventControl_;

    public AP2DelayOrEventControl()
    {
        // Constructor
    }

    public AP2DelayOrEventControl(
        @SuppressWarnings("hiding") TKRepeat _kRepeat_,
        @SuppressWarnings("hiding") TTLparen _tLparen_,
        @SuppressWarnings("hiding") PExpression _expression_,
        @SuppressWarnings("hiding") TTRparen _tRparen_,
        @SuppressWarnings("hiding") PEventControl _eventControl_)
    {
        // Constructor
        setKRepeat(_kRepeat_);

        setTLparen(_tLparen_);

        setExpression(_expression_);

        setTRparen(_tRparen_);

        setEventControl(_eventControl_);

    }

    @Override
    public Object clone()
    {
        return new AP2DelayOrEventControl(
            cloneNode(this._kRepeat_),
            cloneNode(this._tLparen_),
            cloneNode(this._expression_),
            cloneNode(this._tRparen_),
            cloneNode(this._eventControl_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAP2DelayOrEventControl(this);
    }

    public TKRepeat getKRepeat()
    {
        return this._kRepeat_;
    }

    public void setKRepeat(TKRepeat node)
    {
        if(this._kRepeat_ != null)
        {
            this._kRepeat_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._kRepeat_ = node;
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

    public PExpression getExpression()
    {
        return this._expression_;
    }

    public void setExpression(PExpression node)
    {
        if(this._expression_ != null)
        {
            this._expression_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._expression_ = node;
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

    public PEventControl getEventControl()
    {
        return this._eventControl_;
    }

    public void setEventControl(PEventControl node)
    {
        if(this._eventControl_ != null)
        {
            this._eventControl_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._eventControl_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._kRepeat_)
            + toString(this._tLparen_)
            + toString(this._expression_)
            + toString(this._tRparen_)
            + toString(this._eventControl_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._kRepeat_ == child)
        {
            this._kRepeat_ = null;
            return;
        }

        if(this._tLparen_ == child)
        {
            this._tLparen_ = null;
            return;
        }

        if(this._expression_ == child)
        {
            this._expression_ = null;
            return;
        }

        if(this._tRparen_ == child)
        {
            this._tRparen_ = null;
            return;
        }

        if(this._eventControl_ == child)
        {
            this._eventControl_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._kRepeat_ == oldChild)
        {
            setKRepeat((TKRepeat) newChild);
            return;
        }

        if(this._tLparen_ == oldChild)
        {
            setTLparen((TTLparen) newChild);
            return;
        }

        if(this._expression_ == oldChild)
        {
            setExpression((PExpression) newChild);
            return;
        }

        if(this._tRparen_ == oldChild)
        {
            setTRparen((TTRparen) newChild);
            return;
        }

        if(this._eventControl_ == oldChild)
        {
            setEventControl((PEventControl) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}