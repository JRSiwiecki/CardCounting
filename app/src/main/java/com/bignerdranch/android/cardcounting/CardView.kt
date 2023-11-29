package com.bignerdranch.android.cardcounting

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class CardView : ConstraintLayout {

    private lateinit var rankSymbolTop: TextView
    private lateinit var suitSymbolTop: TextView
    private lateinit var rankSymbolBottom: TextView
    private lateinit var suitSymbolBottom: TextView

    constructor(context: Context) : super(context) {
        initializeViews(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initializeViews(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initializeViews(context)
    }

    private fun initializeViews(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.card_view, this)
        rankSymbolTop = findViewById(R.id.rankSymbolTop)
        suitSymbolTop = findViewById(R.id.suitSymbolTop)
        rankSymbolBottom = findViewById(R.id.rankSymbolBottom)
        suitSymbolBottom = findViewById(R.id.suitSymbolBottom)
    }

    fun setSymbols(rankTop: String, suitTop: String, rankBottom: String, suitBottom: String) {
        rankSymbolTop.text = rankTop
        suitSymbolTop.text = suitTop
        rankSymbolBottom.text = rankBottom
        suitSymbolBottom.text = suitBottom
    }
}

