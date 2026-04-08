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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun StatsScreen(modifier: Modifier = Modifier, onBack: () -> Unit, viewModel: StatsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val bg = MaterialTheme.colorScheme.background
    val onBg = MaterialTheme.colorScheme.onBackground

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bg)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp),
                colors = IconButtonDefaults.iconButtonColors(contentColor = onBg)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", Modifier.size(26.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Statistics", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = onBg)
        }

        HighlightRow(bestScore = uiState.bestScore, topTile = uiState.topTile)
        GameDetailsCard(uiState)
        TopScoresCard(scores = uiState.topScores)

        Spacer(modifier = Modifier.weight(1f))

        ResetButton(onClick = viewModel::resetStats)
    }
}

@Composable
private fun HighlightRow(bestScore: Int, topTile: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        HighlightCard(
            icon = Icons.Filled.EmojiEvents,
            label = "Best score",
            value = formatStat(bestScore),
            modifier = Modifier.weight(1f)
        )
        HighlightCard(
            icon = Icons.Filled.Star,
            label = "Top tile",
            value = formatStat(topTile),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun HighlightCard(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(primary)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, tint = onPrimary.copy(alpha = 0.7f), modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = onPrimary)
        Text(text = label, fontSize = 12.sp, color = onPrimary.copy(alpha = 0.7f))
    }
}

@Composable
private fun GameDetailsCard(stats: StatsUiState) {
    SectionCard {
        SectionTitle("Game details")
        Spacer(modifier = Modifier.height(12.dp))
        StatRow(label = "Games played", value = formatStat(stats.gamesPlayed))
        StatDivider()
        StatRow(label = "Wins", value = formatStat(stats.wins))
        StatDivider()
        StatRow(label = "Losses", value = formatStat(stats.losses))
    }
}

@Composable
private fun TopScoresCard(scores: List<Int>) {
    if (scores.isEmpty()) return

    SectionCard {
        SectionTitle("Top scores")
        Spacer(modifier = Modifier.height(12.dp))
        scores.forEachIndexed { index, score ->
            if (index > 0) StatDivider()
            StatRow(
                label = "#${index + 1}",
                value = formatStat(score),
                isBold = index == 0
            )
        }
    }
}

@Composable
private fun ResetButton(onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Icon(Icons.Default.DeleteForever, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reset Stats", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SectionCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    )
}

@Composable
private fun StatRow(label: String, value: String, isBold: Boolean = false) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val primary = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 15.sp, color = onSurface)
        Text(
            value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (isBold) primary else onSurface
        )
    }
}

@Composable
private fun StatDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

private fun formatStat(value: Int): String = "%,d".format(value).replace(',', ' ')
