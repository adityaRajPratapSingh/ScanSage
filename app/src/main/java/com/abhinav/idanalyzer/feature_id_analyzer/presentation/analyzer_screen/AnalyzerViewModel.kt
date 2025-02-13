package com.abhinav.idanalyzer.feature_id_analyzer.presentation.analyzer_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhinav.idanalyzer.core.util.Constants
import com.abhinav.idanalyzer.feature_id_analyzer.domain.IdAnalyzer
import com.abhinav.idanalyzer.feature_id_analyzer.domain.usecase.IdAnalyzerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyzerViewModel @Inject constructor(
    private val idAnalyzerUseCase: IdAnalyzerUseCase
): ViewModel() {

    private val _state = MutableStateFlow(AnalyzerState())
    val state = _state.asStateFlow()

    init {
        getData()
    }

    private fun getData(){
       idAnalyzerUseCase.getEntriesWithIdUseCase.invoke().onEach {list->
           _state.value = state.value.copy(
               listWithId = list
           )
       }.launchIn(viewModelScope)

        idAnalyzerUseCase.getEntriesWithoutIdUseCase.invoke().onEach {list->
            _state.value = state.value.copy(
                listWithoutId = list
            )
        }.launchIn(viewModelScope)
    }

    fun logOut(){
        viewModelScope.launch {
            App.create(Constants.APP_ID).currentUser?.logOut()
        }
    }

}

data class AnalyzerState(
    val listWithId: List<IdAnalyzer> = emptyList(),
    val listWithoutId: List<IdAnalyzer> = emptyList(),
)