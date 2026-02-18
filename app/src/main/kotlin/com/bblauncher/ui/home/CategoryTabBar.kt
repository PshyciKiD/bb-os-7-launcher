package com.bblauncher.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bblauncher.ui.theme.BBTheme
import com.bblauncher.viewmodel.Tab

/**
 * Horizontal tab bar: "Frequent" | "All" | "Favorites".
 * Long-press opens the icon pack picker.
 * In MVP only "All" is functional — the others are visible but do nothing special.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryTabBar(
    selectedTab: Tab,
    onTabSelected: (Tab) -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(BBTheme.tabBarHeight)
            .background(BBTheme.tabBarBackground)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Tab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .then(
                        if (isSelected) {
                            Modifier.background(BBTheme.tabActiveHighlight)
                        } else {
                            Modifier
                        },
                    )
                    .combinedClickable(
                        onClick = { onTabSelected(tab) },
                        onLongClick = onLongPress,
                    )
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = tab.name,
                    color = BBTheme.tabTextColor,
                    fontSize = BBTheme.tabTextSize,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                )
            }
        }
    }
}
