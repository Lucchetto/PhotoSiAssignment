package com.photosi.assignment.section.countries

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.photosi.assignment.R
import com.photosi.assignment.domain.entity.CountryEntity
import com.photosi.assignment.domain.entity.RepoApiErrorEntity
import com.photosi.assignment.domain.entity.RepoApiResult
import com.photosi.assignment.domain.entity.Result
import com.photosi.assignment.navigation.AppRoute
import com.photosi.assignment.ui.component.ErrorStateScreen
import com.photosi.assignment.ui.component.FullScreenLoading
import com.photosi.assignment.ui.theme.PhotoSìAssignmentTheme
import com.photosi.assignment.ui.theme.spacing
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCountriesScreen(
    navController: NavController,
    viewModel: SelectCountriesViewModel
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

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
                searchQuery = it.searchQuery,
                onSearchQueryChange = viewModel::updateSearchQuery,
                countries = it.countries,
                onRetry = viewModel::reloadCountries,
                onCountrySelect = {
                    navController.navigate(AppRoute.UploadImages) {
                        popUpTo(AppRoute.SelectCountries) {
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = padding,
            )
        } ?: FullScreenLoading(modifier = Modifier.padding(padding))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CountriesList(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    countries: RepoApiResult<ImmutableList<CountryEntity>, Nothing>,
    onRetry: () -> Unit,
    onCountrySelect: (CountryEntity) -> Unit,
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
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        stickyHeader {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.padding(MaterialTheme.spacing.level4).fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.searchbar_placeholder)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResource(R.string.clear)
                            )
                        }
                    }
                } else {
                    null
                },
                singleLine = true,
            )
        }
        items(countries.value) {
            CountryItem(country = it, onClick = { onCountrySelect(it) })
        }
    }
}

val CountryEntity.flagUrl get() = "https://flagcdn.com/${isoAlpha2.lowercase()}.svg"

@Composable
private fun CountryItem(
    country: CountryEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) = Row(
    modifier = modifier
        .fillMaxSize()
        .clickable(onClick = onClick)
        .padding(MaterialTheme.spacing.level5),
    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.level4),
    verticalAlignment = Alignment.CenterVertically
) {
    AsyncImage(
        country.flagUrl,
        contentDescription = null,
        modifier = Modifier.size(42.dp)
    )
    Text(text = country.name)
}

private class CountriesListPreviewParamProvider : PreviewParameterProvider<RepoApiResult<ImmutableList<CountryEntity>, Nothing>> {
    override val values: Sequence<RepoApiResult<ImmutableList<CountryEntity>, Nothing>> = sequenceOf(
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
            ).let { list -> List(40) { list }.flatten() }.toImmutableList()
        ),
    )
}

@PreviewLightDark
@Composable
private fun CountriesListPreview(
    @PreviewParameter(CountriesListPreviewParamProvider::class) countries: RepoApiResult<ImmutableList<CountryEntity>, Nothing>
) = PhotoSìAssignmentTheme {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold {
        CountriesList(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            countries = countries,
            onRetry = {},
            onCountrySelect = {},
            modifier = Modifier.padding(it)
        )
    }
}
