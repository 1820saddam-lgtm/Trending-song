package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.HotMagenta
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PremiumDialog(
  isCurrentlyPremium: Boolean,
  onDismiss: () -> Unit,
  onUpgradeConfirmed: () -> Unit
) {
  var selectedPlan by remember { mutableStateOf("INDIVIDUAL") }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = DarkNavyBg),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .border(1.dp, Brush.horizontalGradient(listOf(NeonViolet, ElectricCyan)), RoundedCornerShape(24.dp))
        .testTag("premium_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top close & badge
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(Brush.horizontalGradient(listOf(NeonViolet, HotMagenta)))
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = "VIP ACCESS",
              style = MaterialTheme.typography.labelSmall,
              color = TextPrimary,
              fontWeight = FontWeight.Bold
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(36.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = TextSecondary
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(NeonViolet, ElectricCyan)))
        ) {
          Icon(
            imageVector = Icons.Default.Diamond,
            contentDescription = "Diamond",
            tint = TextPrimary,
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = if (isCurrentlyPremium) "You are a TrendWave VIP!" else "Unlock TrendWave VIP",
          style = MaterialTheme.typography.headlineMedium,
          color = TextPrimary,
          textAlign = TextAlign.Center
        )

        Text(
          text = "Experience limitless music and video entertainment without any boundaries.",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Features list
        Column(
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          PremiumFeatureRow(title = "100% Ad-Free Listening & Video Watching")
          PremiumFeatureRow(title = "Ultra HD 320kbps Master Audio & 1080p Video")
          PremiumFeatureRow(title = "Download Unlimited Songs for Offline Mode")
          PremiumFeatureRow(title = "Unlimited Skips & Full Queue Management")
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (!isCurrentlyPremium) {
          // Plan Selector Cards
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            PlanCard(
              title = "Individual",
              price = "$3.99/mo",
              isSelected = selectedPlan == "INDIVIDUAL",
              modifier = Modifier.weight(1f)
            ) {
              selectedPlan = "INDIVIDUAL"
            }
            PlanCard(
              title = "Family (6)",
              price = "$6.99/mo",
              isSelected = selectedPlan == "FAMILY",
              modifier = Modifier.weight(1f)
            ) {
              selectedPlan = "FAMILY"
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          Button(
            onClick = {
              onUpgradeConfirmed()
              onDismiss()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(Brush.horizontalGradient(listOf(NeonViolet, ElectricCyan)))
              .testTag("upgrade_confirm_button")
          ) {
            Text(
              text = "Start 1-Month Free Trial",
              style = MaterialTheme.typography.titleSmall,
              color = TextPrimary,
              fontWeight = FontWeight.Bold
            )
          }
        } else {
          Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardElevated),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
          ) {
            Text("Keep Enjoying VIP Perks", color = ElectricCyan)
          }
        }
      }
    }
  }
}

@Composable
private fun PremiumFeatureRow(title: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.fillMaxWidth()
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .size(20.dp)
        .clip(CircleShape)
        .background(NeonViolet)
    ) {
      Icon(
        imageVector = Icons.Default.Check,
        contentDescription = null,
        tint = TextPrimary,
        modifier = Modifier.size(14.dp)
      )
    }
    Spacer(modifier = Modifier.width(10.dp))
    Text(
      text = title,
      style = MaterialTheme.typography.bodySmall,
      color = TextPrimary
    )
  }
}

@Composable
private fun PlanCard(
  title: String,
  price: String,
  isSelected: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) SurfaceCardElevated else SurfaceCard
    ),
    modifier = modifier
      .border(
        width = if (isSelected) 2.dp else 1.dp,
        color = if (isSelected) ElectricCyan else Color.Transparent,
        shape = RoundedCornerShape(14.dp)
      )
      .clickable(onClick = onClick)
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = if (isSelected) TextPrimary else TextSecondary
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = price,
        style = MaterialTheme.typography.titleSmall,
        color = if (isSelected) ElectricCyan else TextPrimary,
        fontWeight = FontWeight.Bold
      )
    }
  }
}
