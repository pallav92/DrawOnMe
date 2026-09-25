package com.draw.onme.presentation.coloring.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.draw.onme.data.repository.InMemoryColoringBookRepository
import com.draw.onme.domain.repository.ColoringBookRepository
import com.draw.onme.presentation.coloring.gallery.components.ColoringCard

/**
 * Screen displaying the Coloring Book album gallery.
 * Allows children to filter pages by themed categories, start a fresh blank canvas,
 * or pick a page to color.
 */
@Composable
fun ColoringGalleryScreen(
    onSelectPage: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    repository: ColoringBookRepository = remember { InMemoryColoringBookRepository() }
) {
    val categories = remember { listOf("All") + repository.getCategories() }
    var selectedCategory by remember { mutableStateOf("All") }

    val displayedPages = remember(selectedCategory) {
        if (selectedCategory == "All") {
            repository.getColoringPages()
        } else {
            repository.getPagesByCategory(selectedCategory)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header Row: Back button and Screen Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.92f),
                    tonalElevation = 2.dp,
                    shadowElevation = 3.dp
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Coloring Book 🎨",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Scrollable Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (cat in categories) {
                    CategoryFilterChip(
                        category = cat,
                        isSelected = cat == selectedCategory,
                        onSelect = { selectedCategory = it }
                    )
                }
            }

            // Grid of Coloring Pages
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = displayedPages,
                    key = { it.id },
                    contentType = { if (it.outlines.isEmpty()) "blank_canvas" else "template" }
                ) { page ->
                    val onCardClick = remember(page.id, onSelectPage) {
                        { onSelectPage(page.id) }
                    }
                    ColoringCard(
                        page = page,
                        onClick = onCardClick
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterChip(
    category: String,
    isSelected: Boolean,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val emoji = when (category) {
        InMemoryColoringBookRepository.CATEGORY_CREATE -> "🎨 "
        "Animals & Pets" -> "🐶 "
        "Vehicles & Adventures" -> "🚗 "
        "Fairy Tale & Fantasy" -> "🦄 "
        "Sweet Food & Nature" -> "🍓 "
        else -> "✨ "
    }

    FilterChip(
        selected = isSelected,
        onClick = { onSelect(category) },
        label = {
            Text(
                text = "$emoji$category",
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier
    )
}
