package com.pallav.drawonme.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pallav.drawonme.presentation.onboarding.components.RainbowSplashPad

/**
 * Child-first onboarding screen featuring an interactive rainbow splash pad,
 * pre-reader visual pathways, and parent safety assurance.
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // App Brand Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎨",
                        fontSize = 26.sp
                    )
                }

                Column {
                    Text(
                        text = "DrawOnMe",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Little Artist Studio",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive First Touch Rainbow Splash Pad
            RainbowSplashPad()

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

            // Parent Reassurance Badge
            Row(
                modifier = Modifier
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

            Spacer(modifier = Modifier.height(16.dp))
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
