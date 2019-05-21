package com.natallia.radaman.jokesTextViewResizable

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_jokes.*

class JokesActivity : AppCompatActivity(), JokesAdapter.Listener {

    private lateinit var viewModel: JokesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jokes)

        val jokesAdapter = JokesAdapter()
        jokesAdapter.setListener(this)

        jokesListView.apply {
            setHasFixedSize(true)
            val linearLayoutManager = LinearLayoutManager(this@JokesActivity)
            layoutManager = linearLayoutManager
            adapter = jokesAdapter
            addOnScrollListener(object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                    viewModel.searchJokesNextPage()
                }
            })
        }

        viewModel = ViewModelProviders.of(this).get(JokesViewModel::class.java)
        viewModel.getLoading().observe(this, Observer {
            it?.let { showLoadingView(it) }
        })
        viewModel.getError().observe(this, Observer {
            it?.let { showErrorView(it) }
        })
        viewModel.getJokes().observe(this, Observer {
            it?.let { showJokes(jokesAdapter, it) }
        })
        viewModel.searchJokes()
    }

    private fun showJokes(jokesAdapter: JokesAdapter, jokeList: List<String>) {
        jokesAdapter.update(jokeList)
    }

    private fun showErrorView(error: String) {
        Toast.makeText(this@JokesActivity, error, Toast.LENGTH_LONG).show()
    }

    private fun showLoadingView(show: Boolean) {
        if (show) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }

    //region JokesAdapter.Listener methods
    override fun onJokeClick(joke: String) {
        val intent = JokeDetailActivity.newIntent(this, joke)
        startActivity(intent)
    }
    //endregion
}
