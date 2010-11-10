/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.zamia.verilog.node;

import org.zamia.verilog.analysis.*;

@SuppressWarnings("nls")
public final class AIfThenElseStatement extends PIfThenElseStatement
{
    private TKIf _kIf_;
    private TTLparen _tLparen_;
    private PExpression _expression_;
    private TTRparen _tRparen_;
    private PStatementNoShortIf _statementNoShortIf_;
    private TKElse _kElse_;
    private PStatement _statement_;

    public AIfThenElseStatement()
    {
        // Constructor
    }

    public AIfThenElseStatement(
        @SuppressWarnings("hiding") TKIf _kIf_,
        @SuppressWarnings("hiding") TTLparen _tLparen_,
        @SuppressWarnings("hiding") PExpression _expression_,
        @SuppressWarnings("hiding") TTRparen _tRparen_,
        @SuppressWarnings("hiding") PStatementNoShortIf _statementNoShortIf_,
        @SuppressWarnings("hiding") TKElse _kElse_,
        @SuppressWarnings("hiding") PStatement _statement_)
    {
        // Constructor
        setKIf(_kIf_);

        setTLparen(_tLparen_);

        setExpression(_expression_);

        setTRparen(_tRparen_);

        setStatementNoShortIf(_statementNoShortIf_);

        setKElse(_kElse_);

        setStatement(_statement_);

    }

    @Override
    public Object clone()
    {
        return new AIfThenElseStatement(
            cloneNode(this._kIf_),
            cloneNode(this._tLparen_),
            cloneNode(this._expression_),
            cloneNode(this._tRparen_),
            cloneNode(this._statementNoShortIf_),
            cloneNode(this._kElse_),
            cloneNode(this._statement_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAIfThenElseStatement(this);
    }

    public TKIf getKIf()
    {
        return this._kIf_;
    }

    public void setKIf(TKIf node)
    {
        if(this._kIf_ != null)
        {
            this._kIf_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._kIf_ = node;
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

    public PStatementNoShortIf getStatementNoShortIf()
    {
        return this._statementNoShortIf_;
    }

    public void setStatementNoShortIf(PStatementNoShortIf node)
    {
        if(this._statementNoShortIf_ != null)
        {
            this._statementNoShortIf_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._statementNoShortIf_ = node;
    }

    public TKElse getKElse()
    {
        return this._kElse_;
    }

    public void setKElse(TKElse node)
    {
        if(this._kElse_ != null)
        {
            this._kElse_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._kElse_ = node;
    }

    public PStatement getStatement()
    {
        return this._statement_;
    }

    public void setStatement(PStatement node)
    {
        if(this._statement_ != null)
        {
            this._statement_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._statement_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._kIf_)
            + toString(this._tLparen_)
            + toString(this._expression_)
            + toString(this._tRparen_)
            + toString(this._statementNoShortIf_)
            + toString(this._kElse_)
            + toString(this._statement_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._kIf_ == child)
        {
            this._kIf_ = null;
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

        if(this._statementNoShortIf_ == child)
        {
            this._statementNoShortIf_ = null;
            return;
        }

        if(this._kElse_ == child)
        {
            this._kElse_ = null;
            return;
        }

        if(this._statement_ == child)
        {
            this._statement_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._kIf_ == oldChild)
        {
            setKIf((TKIf) newChild);
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

        if(this._statementNoShortIf_ == oldChild)
        {
            setStatementNoShortIf((PStatementNoShortIf) newChild);
            return;
        }

        if(this._kElse_ == oldChild)
        {
            setKElse((TKElse) newChild);
            return;
        }

        if(this._statement_ == oldChild)
        {
            setStatement((PStatement) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}