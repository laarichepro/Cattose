package br.com.cattose.app.feature.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import br.com.cattose.app.data.model.domain.CatDetails
import br.com.cattose.app.data.repository.CatRepository
import br.com.cattose.app.feature.detail.navigation.CAT_ID_ARG
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = mockk<CatRepository>()
    private val savedStateHandle = mockk<SavedStateHandle>()
    private lateinit var viewModel: DetailViewModel

    @Before
    fun setupMainDispatcher() {
        Dispatchers.setMain(dispatcher)
        every<String?> { savedStateHandle[CAT_ID_ARG] } returns "id"
    }

    @Test
    fun `given cats should emit success state`() = runTest {
        val cats = mockk<CatDetails>()
        coEvery {
            repository.getDetails("id")
        } returns flowOf(cats)

        viewModel = DetailViewModel(repository, savedStateHandle)

        viewModel.state.test {
            Truth.assertThat(awaitItem()).isEqualTo(DetailState.Loading)
            Truth.assertThat(awaitItem()).isEqualTo(DetailState.Success(cats))
        }
    }

    @Test
    fun `given error should emit error state`() = runTest {
        coEvery {
            repository.getDetails("id")
        } returns flow { throw Exception() }

        viewModel = DetailViewModel(repository, savedStateHandle)

        viewModel.state.test {
            Truth.assertThat(awaitItem()).isEqualTo(DetailState.Loading)
            Truth.assertThat(awaitItem()).isEqualTo(DetailState.Error)
        }
    }
}
