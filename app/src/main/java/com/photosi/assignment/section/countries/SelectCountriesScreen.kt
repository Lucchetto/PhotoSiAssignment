package com.photosi.assignment.section.countries

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.photosi.assignment.R
import com.photosi.assignment.domain.entity.CountryEntity
import com.photosi.assignment.domain.entity.RepoApiErrorEntity
import com.photosi.assignment.domain.entity.RepoApiResult
import com.photosi.assignment.domain.entity.Result
import com.photosi.assignment.ui.component.ErrorStateScreen
import com.photosi.assignment.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCountriesScreen(
    navController: NavController,
    viewModel: SelectCountriesViewModel
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.select_countries_screen_title)) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

        uiState?.let {
            CountriesList(
                countries = it.countries,
                onRetry = viewModel::reloadCountries,
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = padding
            )
        } ?: Box(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun CountriesList(
    countries: RepoApiResult<List<CountryEntity>, Nothing>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) = when (countries) {
    is Result.Failure -> {
        val illustration: ImageVector
        @StringRes val messageRes: Int

        when (countries.error) {
            is RepoApiErrorEntity.Custom,
            is RepoApiErrorEntity.Unknown -> {
                illustration = Icons.Outlined.ErrorOutline
                messageRes = R.string.select_countries_generic_error_message
            }
            is RepoApiErrorEntity.Network -> {
                illustration = Icons.Outlined.CloudOff
                messageRes = R.string.select_countries_network_error_message

            }
        }

        ErrorStateScreen(
            illustration = { Icon(illustration, contentDescription = null) },
            message = { Text(stringResource(messageRes)) },
            modifier = modifier,
            action = {
                Button(onClick = onRetry) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.retry)
                    )
                    Text(stringResource(R.string.retry))
                }
            },
            contentPadding = contentPadding
        )
    }
    is Result.Success -> LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding
    ) {
        items(countries.value) {
            CountryItem(country = it)
        }
    }
}

@Composable
private fun CountryItem(
    country: CountryEntity,
    modifier: Modifier = Modifier,
) = Box(
    modifier = modifier.padding(MaterialTheme.spacing.level5)
) {
    Text(text = country.name)
}

private class CountriesListPreviewParamProvider : PreviewParameterProvider<RepoApiResult<List<CountryEntity>, Nothing>> {
    override val values: Sequence<RepoApiResult<List<CountryEntity>, Nothing>> = sequenceOf(
        Result.Failure(RepoApiErrorEntity.Unknown(IllegalStateException())),
        Result.Failure(RepoApiErrorEntity.Network(IllegalStateException())),
        Result.Success(
            listOf(
                CountryEntity(
                    iso = 840,
                    isoAlpha2 = "US",
                    isoAlpha3 = "USA",
                    name = "United States",
                    phonePrefix = "+1",
                    phoneRegex = "\\+1\\d{10}"
                ),
                CountryEntity(
                    iso = 124,
                    isoAlpha2 = "CA",
                    isoAlpha3 = "CAN",
                    name = "Canada",
                    phonePrefix = "+1",
                    phoneRegex = "\\+1\\d{10}"
                ),
                CountryEntity(
                    iso = 36,
                    isoAlpha2 = "AU",
                    isoAlpha3 = "AUS",
                    name = "Australia",
                    phonePrefix = "+61",
                    phoneRegex = "\\+61\\d{9}"
                ),
                CountryEntity(
                    iso = 826,
                    isoAlpha2 = "GB",
                    isoAlpha3 = "GBR",
                    name = "United Kingdom",
                    phonePrefix = "+44",
                    phoneRegex = "\\+44\\d{10}"
                ),
                CountryEntity(
                    iso = 356,
                    isoAlpha2 = "IN",
                    isoAlpha3 = "IND",
                    name = "India",
                    phonePrefix = "+91",
                    phoneRegex = "\\+91\\d{10}"
                )
            ).let { list -> List(40) { list }.flatten() }
        ),
    )
}

@PreviewLightDark
@Composable
private fun CountriesListPreview(
    @PreviewParameter(CountriesListPreviewParamProvider::class) countries: RepoApiResult<List<CountryEntity>, Nothing>
) = MaterialTheme {
    Scaffold {
        CountriesList(countries = countries, onRetry = {}, modifier = Modifier.padding(it))
    }
}
