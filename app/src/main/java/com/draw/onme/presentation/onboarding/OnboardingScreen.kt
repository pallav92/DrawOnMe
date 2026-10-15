package com.draw.onme.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.draw.onme.R
import com.draw.onme.ui.theme.DrawOnMeTheme

/**
 * Child-first onboarding screen featuring pre-reader visual pathways
 * and parent safety assurance.
 *
 * @param onSelectFreeScribble Callback when user selects the freehand notepad.
 * @param onSelectColoringBook Callback when user selects the coloring book studio.
 * @param onSelectStencils Callback when user selects the cartoon stencil studio.
 * @param onSelectFridge Callback when user selects the virtual fridge gallery.
 * @param modifier Optional modifier applied to the screen root.
 */
@Composable
fun OnboardingScreen(
    onSelectFreeScribble: () -> Unit,
    onSelectColoringBook: () -> Unit,
    onSelectStencils: () -> Unit,
    onSelectFridge: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            val isLandscape = maxWidth > maxHeight

            if (isLandscape) {
                OnboardingLandscapeContent(
                    onSelectFreeScribble = onSelectFreeScribble,
                    onSelectColoringBook = onSelectColoringBook,
                    onSelectStencils = onSelectStencils,
                    onSelectFridge = onSelectFridge
                )
            } else {
                OnboardingPortraitContent(
                    onSelectFreeScribble = onSelectFreeScribble,
                    onSelectColoringBook = onSelectColoringBook,
                    onSelectStencils = onSelectStencils,
                    onSelectFridge = onSelectFridge
                )
            }
        }
    }
}

@Composable
private fun AppBrandHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_splash_logo),
            contentDescription = "DrawOnMe Logo",
            modifier = Modifier.size(64.dp)
        )

        Column {
            Text(
                text = "DrawOnMe",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Little Artist Studio",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun OnboardingPortraitContent(
    onSelectFreeScribble: () -> Unit,
    onSelectColoringBook: () -> Unit,
    onSelectStencils: () -> Unit,
    onSelectFridge: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        AppBrandHeader()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ModeSelectionTile(
                    title = "Free Doodle",
                    badgeEmoji = "✏️",
                    containerColor = Color(0xFFFFF3E0),
                    contentColor = Color(0xFFE65100),
                    onClick = onSelectFreeScribble,
                    modifier = Modifier.weight(1f)
                )

                ModeSelectionTile(
                    title = "Coloring Book",
                    badgeEmoji = "🎨",
                    containerColor = Color(0xFFE1F5FE),
                    contentColor = Color(0xFF01579B),
                    onClick = onSelectColoringBook,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ModeSelectionTile(
                    title = "Art School",
                    badgeEmoji = "🐾",
                    containerColor = Color(0xFFEDE7F6),
                    contentColor = Color(0xFF4A148C),
                    onClick = onSelectStencils,
                    modifier = Modifier.weight(1f)
                )

                ModeSelectionTile(
                    title = "My Fridge",
                    badgeEmoji = "🖼️",
                    containerColor = Color(0xFFE0F2F1),
                    contentColor = Color(0xFF004D40),
                    onClick = onSelectFridge,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        ParentReassuranceBadge()

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun ParentReassuranceBadge(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "100% Offline • Child-Safe • Ad-Free",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun OnboardingLandscapeContent(
    onSelectFreeScribble: () -> Unit,
    onSelectColoringBook: () -> Unit,
    onSelectStencils: () -> Unit,
    onSelectFridge: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Column: Brand Header and Safety Badge
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppBrandHeader()
            ParentReassuranceBadge()
        }

        // Right Column: 2x2 Grid of Mode Tiles
        Column(
            modifier = Modifier
                .weight(1.5f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ModeSelectionTile(
                    title = "Free Doodle",
                    badgeEmoji = "✏️",
                    containerColor = Color(0xFFFFF3E0),
                    contentColor = Color(0xFFE65100),
                    onClick = onSelectFreeScribble,
                    modifier = Modifier.weight(1f)
                )

                ModeSelectionTile(
                    title = "Coloring Book",
                    badgeEmoji = "🎨",
                    containerColor = Color(0xFFE1F5FE),
                    contentColor = Color(0xFF01579B),
                    onClick = onSelectColoringBook,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ModeSelectionTile(
                    title = "Art School",
                    badgeEmoji = "🐾",
                    containerColor = Color(0xFFEDE7F6),
                    contentColor = Color(0xFF4A148C),
                    onClick = onSelectStencils,
                    modifier = Modifier.weight(1f)
                )

                ModeSelectionTile(
                    title = "My Fridge",
                    badgeEmoji = "🖼️",
                    containerColor = Color(0xFFE0F2F1),
                    contentColor = Color(0xFF004D40),
                    onClick = onSelectFridge,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ModeSelectionTile(
    title: String,
    badgeEmoji: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.90f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeEmoji,
                    fontSize = 34.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                ),
                color = contentColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    DrawOnMeTheme {
        OnboardingScreen(
            onSelectFreeScribble = {},
            onSelectColoringBook = {},
            onSelectStencils = {},
            onSelectFridge = {}
        )
    }
}
