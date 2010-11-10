/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.zamia.verilog.node;

import org.zamia.verilog.analysis.*;

@SuppressWarnings("nls")
public final class AP3EventTriggerExpr extends PEventTriggerExpr
{
    private TTPeriod _tPeriod_;
    private PIdentifier _identifier_;
    private PEventTriggerExpr _eventTriggerExpr_;

    public AP3EventTriggerExpr()
    {
        // Constructor
    }

    public AP3EventTriggerExpr(
        @SuppressWarnings("hiding") TTPeriod _tPeriod_,
        @SuppressWarnings("hiding") PIdentifier _identifier_,
        @SuppressWarnings("hiding") PEventTriggerExpr _eventTriggerExpr_)
    {
        // Constructor
        setTPeriod(_tPeriod_);

        setIdentifier(_identifier_);

        setEventTriggerExpr(_eventTriggerExpr_);

    }

    @Override
    public Object clone()
    {
        return new AP3EventTriggerExpr(
            cloneNode(this._tPeriod_),
            cloneNode(this._identifier_),
            cloneNode(this._eventTriggerExpr_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAP3EventTriggerExpr(this);
    }

    public TTPeriod getTPeriod()
    {
        return this._tPeriod_;
    }

    public void setTPeriod(TTPeriod node)
    {
        if(this._tPeriod_ != null)
        {
            this._tPeriod_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._tPeriod_ = node;
    }

    public PIdentifier getIdentifier()
    {
        return this._identifier_;
    }

    public void setIdentifier(PIdentifier node)
    {
        if(this._identifier_ != null)
        {
            this._identifier_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._identifier_ = node;
    }

    public PEventTriggerExpr getEventTriggerExpr()
    {
        return this._eventTriggerExpr_;
    }

    public void setEventTriggerExpr(PEventTriggerExpr node)
    {
        if(this._eventTriggerExpr_ != null)
        {
            this._eventTriggerExpr_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._eventTriggerExpr_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._tPeriod_)
            + toString(this._identifier_)
            + toString(this._eventTriggerExpr_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._tPeriod_ == child)
        {
            this._tPeriod_ = null;
            return;
        }

        if(this._identifier_ == child)
        {
            this._identifier_ = null;
            return;
        }

        if(this._eventTriggerExpr_ == child)
        {
            this._eventTriggerExpr_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._tPeriod_ == oldChild)
        {
            setTPeriod((TTPeriod) newChild);
            return;
        }

        if(this._identifier_ == oldChild)
        {
            setIdentifier((PIdentifier) newChild);
            return;
        }

        if(this._eventTriggerExpr_ == oldChild)
        {
            setEventTriggerExpr((PEventTriggerExpr) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}