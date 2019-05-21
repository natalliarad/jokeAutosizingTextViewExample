package com.natallia.radaman.jokesTextViewResizable

import android.arch.lifecycle.*

class JokesViewModel : ViewModel() {
    private var loading: MutableLiveData<Boolean> = MutableLiveData()
    private var jokes: MutableLiveData<List<String>> = MutableLiveData()
    private var error: MutableLiveData<String> = MutableLiveData()
    private lateinit var searchResult: JokeSearchResult

    init {
        loading.value = false
    }

    fun getLoading(): LiveData<Boolean> = loading

    fun getJokes(): LiveData<List<String>> = jokes

    fun getError(): LiveData<String> = error

    fun searchJokes(page: Int = 1) {
        jokes.value = listOf()
        loading.value = true
        JokesRepository.searchJokes(object : RepositoryCallback<JokeSearchResult, String> {
            override fun onSuccess(entity: JokeSearchResult) {
                loading.value = false
                searchResult = entity
                jokes.value = searchResult.results.map { it.joke }
            }

            override fun onError(entity: String) {
                loading.value = false
                error.value = entity
            }
        }, page)
    }

    fun searchJokesNextPage() {
        if (searchResult.currentPage < searchResult.nextPage) {
            searchJokes(searchResult.nextPage)
        }
    }
}
