package com.pallav.drawonme.presentation.onboarding

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
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pallav.drawonme.R
import com.pallav.drawonme.presentation.onboarding.components.RainbowSplashPad
import com.pallav.drawonme.ui.theme.DrawOnMeTheme

/**
 * Child-first onboarding screen featuring an interactive rainbow splash pad,
 * pre-reader visual pathways, and parent safety assurance.
 *
 * Layout positioning:
 * - Bottom content (Magic Pad, pathway cards, reassurance badge) is anchored to the bottom.
 * - App brand header ("DrawOnMe") is dynamically centered in the space between the top
 *   of the screen and the top of the magic pad.
 * - Smooth vertical scrolling activates automatically on compact or landscape viewports.
 *
 * @param onSelectFreeScribble Callback when user selects the freehand notepad.
 * @param onSelectStencils Callback when user selects the cartoon stencil studio.
 * @param onSelectFridge Callback when user selects the virtual fridge gallery.
 * @param modifier Optional modifier applied to the screen root.
 */
@Composable
fun OnboardingScreen(
    onSelectFreeScribble: () -> Unit,
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
                    onSelectStencils = onSelectStencils,
                    onSelectFridge = onSelectFridge
                )
            } else {
                OnboardingLayout(
                    availableHeight = maxHeight,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    header = {
                        AppBrandHeader()
                    },
                    content = {
                        OnboardingBottomContent(
                            onSelectFreeScribble = onSelectFreeScribble,
                            onSelectStencils = onSelectStencils,
                            onSelectFridge = onSelectFridge,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                )
            }
        }
    }
}

/**
 * Custom two-slot layout that keeps the [content] (magic pad + cards) anchored at the bottom,
 * and centers the [header] ("DrawOnMe") vertically in the remaining space between the top edge
 * and the top of the magic pad.
 */
@Composable
private fun OnboardingLayout(
    availableHeight: Dp,
    header: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    minSpacing: Dp = 16.dp
) {
    Layout(
        contents = listOf(header, content),
        modifier = modifier
    ) { (headerMeasurables, contentMeasurables), constraints ->
        val headerConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val headerPlaceable = headerMeasurables.first().measure(headerConstraints)
        val contentConstraints = constraints.copy(minHeight = 0)
        val contentPlaceable = contentMeasurables.first().measure(contentConstraints)

        val availableHeightPx = availableHeight.roundToPx()
        val minSpacingPx = minSpacing.roundToPx()
        val neededHeight = headerPlaceable.height + minSpacingPx + contentPlaceable.height

        val totalHeight: Int
        val headerY: Int
        val contentY: Int

        if (neededHeight <= availableHeightPx) {
            // Viewport is tall enough: anchor content to bottom, center header in upper space
            totalHeight = availableHeightPx
            contentY = availableHeightPx - contentPlaceable.height
            // Available vertical space between top (0) and top of magic pad (contentY)
            val spaceAbovePad = contentY
            headerY = ((spaceAbovePad - headerPlaceable.height) / 2).coerceAtLeast(0)
        } else {
            // Compact screen / landscape: stack naturally with minimum spacing and scroll
            totalHeight = neededHeight
            headerY = 0
            contentY = headerPlaceable.height + minSpacingPx
        }

        val width = constraints.maxWidth

        layout(width, totalHeight) {
            val headerX = (width - headerPlaceable.width) / 2
            val contentX = (width - contentPlaceable.width) / 2

            headerPlaceable.placeRelative(headerX, headerY)
            contentPlaceable.placeRelative(contentX, contentY)
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
private fun OnboardingBottomContent(
    onSelectFreeScribble: () -> Unit,
    onSelectStencils: () -> Unit,
    onSelectFridge: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Big Interactive First Touch Rainbow Splash Pad
        RainbowSplashPad(
            height = 195.dp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Choose Your Adventure",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Card 1: Magic Doodle (Free Scribble)
        ModeSelectionCard(
            title = "Magic Doodle",
            subtitle = "Draw & scribble freely on an open notepad with vibrant colors and pens.",
            badgeEmoji = "✏️",
            icon = Icons.Default.Draw,
            containerColor = Color(0xFFFFF3E0), // Warm soft orange/amber
            contentColor = Color(0xFFE65100),
            onClick = onSelectFreeScribble
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Card 2: Learn to Draw (Geometric Stencils)
        ModeSelectionCard(
            title = "Learn to Draw",
            subtitle = "Follow fun stencils! Trace cars, houses, rockets, and cute animals step-by-step.",
            badgeEmoji = "✨",
            icon = Icons.Default.AutoAwesome,
            containerColor = Color(0xFFEDE7F6), // Soft lavender
            contentColor = Color(0xFF4A148C),
            onClick = onSelectStencils
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Card 3: My Fridge Door (Gallery)
        ModeSelectionCard(
            title = "My Fridge Door",
            subtitle = "See all your proud masterpieces pinned on the virtual refrigerator!",
            badgeEmoji = "🖼️",
            icon = Icons.Default.PhotoLibrary,
            containerColor = Color(0xFFE0F2F1), // Soft mint
            contentColor = Color(0xFF004D40),
            onClick = onSelectFridge
        )

        Spacer(modifier = Modifier.height(24.dp))

        ParentReassuranceBadge()
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
    onSelectStencils: () -> Unit,
    onSelectFridge: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Column: Brand Header, Compact Rainbow Pad, and Safety Badge
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppBrandHeader()

            RainbowSplashPad(
                height = 140.dp
            )

            ParentReassuranceBadge()
        }

        // Right Column: Mode Cards with smooth scrolling
        Column(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Choose Your Adventure",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            ModeSelectionCard(
                title = "Magic Doodle",
                subtitle = "Draw & scribble freely on an open notepad with vibrant colors and pens.",
                badgeEmoji = "✏️",
                icon = Icons.Default.Draw,
                containerColor = Color(0xFFFFF3E0),
                contentColor = Color(0xFFE65100),
                onClick = onSelectFreeScribble
            )

            ModeSelectionCard(
                title = "Learn to Draw",
                subtitle = "Follow fun stencils! Trace cars, houses, rockets, and cute animals step-by-step.",
                badgeEmoji = "✨",
                icon = Icons.Default.AutoAwesome,
                containerColor = Color(0xFFEDE7F6),
                contentColor = Color(0xFF4A148C),
                onClick = onSelectStencils
            )

            ModeSelectionCard(
                title = "My Fridge Door",
                subtitle = "See all your proud masterpieces pinned on the virtual refrigerator!",
                badgeEmoji = "🖼️",
                icon = Icons.Default.PhotoLibrary,
                containerColor = Color(0xFFE0F2F1),
                contentColor = Color(0xFF004D40),
                onClick = onSelectFridge
            )
        }
    }
}

@Composable
private fun ModeSelectionCard(
    title: String,
    subtitle: String,
    badgeEmoji: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeEmoji,
                    fontSize = 28.sp
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = contentColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.85f),
                    lineHeight = 16.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
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
            onSelectStencils = {},
            onSelectFridge = {}
        )
    }
}
