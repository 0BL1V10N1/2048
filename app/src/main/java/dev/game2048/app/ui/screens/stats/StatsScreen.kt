package dev.game2048.app.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.game2048.app.ui.theme.GameTitle
import dev.game2048.app.ui.theme.Tile2048

private val BackgroundColor = Color(0xFFFAF8EF)
private val CardColor = Color(0xFFEDE4DA)

@Composable
fun StatsScreen(modifier: Modifier = Modifier, onBack: () -> Unit, viewModel: StatsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize().background(BackgroundColor).padding(24.dp)) {
        BackButton(onBack)
        Spacer(modifier = Modifier.height(12.dp))
        StatsTitle()
        Spacer(modifier = Modifier.height(24.dp))
        HighlightRow(bestScore = uiState.bestScore, topTile = uiState.topTile)
        Spacer(modifier = Modifier.height(24.dp))
        DetailCard(uiState)
    }
}

@Composable
private fun BackButton(onBack: () -> Unit) {
    IconButton(
        onClick = onBack,
        modifier = Modifier.size(40.dp),
        colors = IconButtonDefaults.iconButtonColors(contentColor = GameTitle)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun StatsTitle() {
    Text(
        text = "Statistics",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = GameTitle
    )
}

@Composable
private fun HighlightRow(bestScore: Int, topTile: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        HighlightCard(label = "Best score", value = formatStat(bestScore), modifier = Modifier.weight(1f))
        HighlightCard(label = "Top tile", value = formatStat(topTile), modifier = Modifier.weight(1f))
    }
}

@Composable
private fun HighlightCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Tile2048)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(text = label, fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
private fun DetailCard(stats: StatsUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardColor)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        DetailRow(label = "Games played", value = formatStat(stats.gamesPlayed))
        HorizontalDivider(color = GameTitle.copy(alpha = 0.15f))
        DetailRow(label = "Wins", value = formatStat(stats.wins))
        DetailRow(label = "Losses", value = formatStat(stats.losses))
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 16.sp, color = GameTitle)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GameTitle)
    }
}

private fun formatStat(value: Int): String = "%,d".format(value).replace(',', ' ')
