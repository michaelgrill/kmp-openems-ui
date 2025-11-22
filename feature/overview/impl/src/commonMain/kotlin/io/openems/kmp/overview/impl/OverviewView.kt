package io.openems.kmp.overview.impl

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import io.openems.kmp.core.icon.Icons
import io.openems.kmp.core.icon.icon_arrow_drop_down
import io.openems.kmp.core.icon.icon_arrow_drop_up
import io.openems.kmp.core.icon.icon_search
import io.openems.kmp.core.icon.icon_sort
import io.openems.kmp.core.icon.icon_tune
import io.openems.kmp.core.icon.icon_warning
import io.openems.kmp.edge.api.model.Edge
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun OverviewView(
    edges: List<Edge> = emptyList(),
    onFilterChanged: (String) -> Unit = {},
    onNavigateToEdge: (edgeId: String) -> Unit = {},
) {
    Scaffold { pv ->
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(pv),
        ) {
            Filter(
                onFilterChanged = onFilterChanged,
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(edges) { edge ->
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .clickable {
                                onNavigateToEdge(edge.id)
                            }
                    ) {
                        Text(
                            text = edge.comment,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = edge.id,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    HorizontalDivider(
                        Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Filter(
    onFilterChanged: (String) -> Unit = {},
) {

    var searchString by remember { mutableStateOf("") }

    LaunchedEffect(searchString) {
        delay(500)
        onFilterChanged(searchString)
    }

    var showSearch by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }


    Column {
        Row(
            modifier = Modifier.padding(vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = {
                    showSearch = !showSearch
//                if (showSearch) {
//                    focusRequester.requestFocus()
//                } else {
//                    focusRequester.freeFocus()
//                }
                },
            ) {
                Icon(
                    painterResource(Icons.icon_search),
                    null,
                )
            }

            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier.horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                var expandedSort by remember { mutableStateOf(false) }
                Box {
                    FilterChip(
                        selected = false,
                        onClick = { expandedSort = true },
                        leadingIcon = {
                            Icon(
                                painterResource(Icons.icon_sort),
                                contentDescription = null
                            )
                        },
                        label = {
                            Text("Sortierung")
                        },
                        trailingIcon = {
                            Icon(
                                if (expandedSort) {
                                    painterResource(Icons.icon_arrow_drop_up)
                                } else {
                                    painterResource(Icons.icon_arrow_drop_down)
                                },
                                contentDescription = null
                            )
                        },
                    )
                    DropdownMenu(
                        expanded = expandedSort,
                        onDismissRequest = { expandedSort = false }
                    ) {

                        listOf(
                            "A - Z", "Z - A", "Ok - Fault", "Fault - Ok"
                        ).forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = { /* Do something... */ }
                            )
                        }
                    }
                }

                var expandedState by remember { mutableStateOf(false) }
                Box {
                    FilterChip(
                        selected = false,
                        onClick = { expandedState = true },
                        leadingIcon = {
                            Icon(
                                painterResource(Icons.icon_warning),
                                contentDescription = null
                            )
                        },
                        label = {
                            Text("State")
                        },
                        trailingIcon = {
                            Icon(
                                if (expandedState) {
                                    painterResource(Icons.icon_arrow_drop_up)
                                } else {
                                    painterResource(Icons.icon_arrow_drop_down)
                                },
                                contentDescription = null
                            )
                        },
                    )
                    DropdownMenu(
                        expanded = expandedState,
                        onDismissRequest = { expandedState = false }
                    ) {

                        listOf(
                            "Ok", "Info", "Warning", "Fault"
                        ).forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = { /* Do something... */ }
                            )
                        }
                    }
                }


                var expandedProductType by remember { mutableStateOf(false) }
                Box {
                    FilterChip(
                        selected = false,
                        onClick = { expandedProductType = true },
                        leadingIcon = {
                            Icon(
                                painterResource(Icons.icon_tune),
                                contentDescription = null
                            )
                        },
                        label = {
                            Text("Product type")
                        },
                    )
                }
            }

        }
        if (showSearch) {
            TextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = searchString,
                onValueChange = {
                    searchString = it
                },
            )
        }
    }
}

@Composable
fun HorizontalDividerWithText(
    modifier: Modifier = Modifier,
    text: String,
) {
    HorizontalDividerWithText(
        modifier = modifier,
        text = {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.outlineVariant,
                style = MaterialTheme.typography.titleSmall,
            )
        },
    )
}

@Composable
fun HorizontalDividerWithText(
    modifier: Modifier = Modifier,
    text: @Composable () -> Unit,
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier.verticalScroll(scrollState),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(Modifier.weight(1f))
        Spacer(Modifier.width(8.dp))
        text()
        Spacer(Modifier.width(8.dp))
        HorizontalDivider(Modifier.weight(1f))
    }
}