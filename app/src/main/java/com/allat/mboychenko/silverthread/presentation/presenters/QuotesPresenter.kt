package com.allat.mboychenko.silverthread.presentation.presenters

import android.content.Context
import com.allat.mboychenko.silverthread.R
import com.allat.mboychenko.silverthread.domain.interactor.QuotesDetailsStorage
import com.allat.mboychenko.silverthread.presentation.helpers.runTaskOnComputationWithResult
import com.allat.mboychenko.silverthread.presentation.views.fragments.IQuotesFragmentView
import com.allat.mboychenko.silverthread.presentation.views.listitems.QuoteItem
import java.util.*

class QuotesPresenter(
    private val context: Context,
    private val storage: QuotesDetailsStorage
) : BasePresenter<IQuotesFragmentView>() {

    private var quotesState: QuotesState = QuotesState.NORMAL
    private val quotes: Array<String> by lazy { context.resources.getStringArray(R.array.quotes) }

    override fun attachView(view: IQuotesFragmentView) {
        super.attachView(view)
        getQuotes()
    }

    fun getQuotesState() = quotesState

    fun changeQuotesState(state: QuotesState? = null) {
        quotesState = state ?: if (quotesState == QuotesState.NORMAL) QuotesState.FAVORITE else QuotesState.NORMAL
        view?.let { getQuotes() }
    }

    fun getRandomQuote(): String =
        quotes[Random().nextInt(quotes.size)]

    private fun getQuotes() {
        subscriptions.add(
            runTaskOnComputationWithResult(
                {
                    val quotesItems = mutableListOf<QuoteItem>()
                    val favorites = storage.getFavoriteQuotesPositions()

                    if (quotesState == QuotesState.FAVORITE) {
                        quotes.forEachIndexed { index, s ->
                            if (favorites.contains(index))
                                quotesItems.add(QuoteItem(s, quotesActionListener, index, true))
                        }
                    } else {
                        quotesItems.addAll(quotes.mapIndexed { index, s ->
                            QuoteItem(s, quotesActionListener, index, favorites.contains(index))
                        })
                    }
                    quotesItems
                },
                { view?.quotesReady(it) })
        )
    }

    private val quotesActionListener = object : QuoteItem.QuotesActionListener {
        override fun onShare(quote: String) {
            view?.shareText(context, quote, context.getString(R.string.share_quote))
        }

        override fun onCopy(quote: String) {
            view?.copyToClipboard(context, quote)
        }

        override fun onFavoriteClick(quoteItem: QuoteItem) {
            if (quoteItem.favorite) {
                storage.removeFavoriteQuotePosition(quoteItem.arrayPosition)
                if (quotesState == QuotesState.FAVORITE) {
                    view?.removeItem(quoteItem)
                }
            } else {
                storage.putFavoriteQuotePosition(quoteItem.arrayPosition)
            }
        }
    }

    enum class QuotesState {
        NORMAL,
        FAVORITE
    }
}