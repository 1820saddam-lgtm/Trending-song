package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.model.AdminAnalytics
import com.example.model.MediaTrack
import com.example.model.MediaVideo
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.components.LanguageHelper
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.HotMagenta
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun ProfileAdminScreen(
  userProfile: UserProfile,
  analytics: AdminAnalytics,
  tracks: List<MediaTrack>,
  videos: List<MediaVideo>,
  onSetLanguage: (String) -> Unit,
  onSetAudioQuality: (String) -> Unit,
  onAuthenticate: (String, String, Boolean) -> Unit,
  onSwitchRole: (UserRole) -> Unit,
  onOpenPremium: () -> Unit,
  onAddTrack: (MediaTrack) -> Unit,
  onDeleteTrack: (String) -> Unit,
  onToggleFeatureTrack: (String) -> Unit,
  onAddVideo: (MediaVideo) -> Unit,
  onDeleteVideo: (String) -> Unit,
  onPostAnnouncement: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val language = userProfile.preferredLanguage
  var showLoginDialog by remember { mutableStateOf(false) }
  var showAddTrackDialog by remember { mutableStateOf(false) }
  var showAddVideoDialog by remember { mutableStateOf(false) }
  var announcementInput by remember { mutableStateOf("") }
  var adminTab by remember { mutableIntStateOf(0) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(DarkNavyBg),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // Header
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
      ) {
        Text(
          text = LanguageHelper.get("nav_profile", language),
          style = MaterialTheme.typography.headlineLarge,
          color = TextPrimary,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // User Profile Card
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
          .border(1.dp, SurfaceCardElevated, RoundedCornerShape(20.dp))
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(16.dp)
        ) {
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(CircleShape)
              .background(NeonViolet)
          ) {
            if (userProfile.avatarUrl.isNotBlank()) {
              AsyncImage(
                model = userProfile.avatarUrl,
                contentDescription = userProfile.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
              )
            } else {
              Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier
                  .align(Alignment.Center)
                  .size(36.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(16.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = userProfile.name,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(8.dp))
              if (userProfile.isPremium) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Brush.horizontalGradient(listOf(NeonViolet, ElectricCyan)))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "VIP",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }

            Text(
              text = userProfile.email,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "Role: ${userProfile.role.name}",
              style = MaterialTheme.typography.labelSmall,
              color = if (userProfile.role == UserRole.ADMIN) ElectricCyan else TextTertiary,
              fontWeight = FontWeight.Bold
            )
          }

          TextButton(
            onClick = { showLoginDialog = true },
            modifier = Modifier.testTag("profile_login_switch_button")
          ) {
            Text(
              text = if (userProfile.email == "guest@trendwave.io") "Sign In" else "Switch",
              color = ElectricCyan
            )
          }
        }
      }
    }

    // VIP Upgrade Banner
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardElevated),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .clickable(onClick = onOpenPremium)
          .border(1.dp, Brush.horizontalGradient(listOf(NeonViolet, HotMagenta)), RoundedCornerShape(16.dp))
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(14.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Diamond,
            contentDescription = null,
            tint = HotMagenta,
            modifier = Modifier.size(32.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = if (userProfile.isPremium) "TrendWave VIP Member" else "Upgrade to TrendWave VIP",
              style = MaterialTheme.typography.titleSmall,
              color = TextPrimary,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = if (userProfile.isPremium) "Ad-free • 320kbps Master Quality active" else "Listen ad-free with offline music downloads",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = ElectricCyan
          )
        }
      }
    }

    // App Preferences Section
    item {
      Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
          text = "PREFERENCES",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondary,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Language toggle
        PreferenceRow(
          icon = Icons.Default.Language,
          title = "App Language",
          subtitle = if (language == "bn") "বাংলা (Bengali)" else "English",
          onClick = {
            onSetLanguage(if (language == "bn") "en" else "bn")
          }
        )

        // Audio Quality
        PreferenceRow(
          icon = Icons.Default.Headphones,
          title = "Audio Streaming Quality",
          subtitle = userProfile.audioQuality,
          onClick = {
            val next = when (userProfile.audioQuality) {
              "Normal (128 kbps)" -> "High (256 kbps)"
              "High (256 kbps)" -> "Ultra HD (320 kbps)"
              else -> "Normal (128 kbps)"
            }
            onSetAudioQuality(next)
          }
        )

        // Storage & Cache
        PreferenceRow(
          icon = Icons.Default.Storage,
          title = "Offline Storage & Cache",
          subtitle = "${analytics.storageUsedMb} MB used (Smart Cache)",
          onClick = {}
        )
      }
    }

    // ADMIN PORTAL SECTION
    item {
      Spacer(modifier = Modifier.height(14.dp))
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
          .border(1.dp, if (userProfile.role == UserRole.ADMIN) ElectricCyan else SurfaceCardElevated, RoundedCornerShape(20.dp))
          .testTag("admin_portal_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = "Admin",
                tint = if (userProfile.role == UserRole.ADMIN) ElectricCyan else TextTertiary,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = LanguageHelper.get("admin_panel", language),
                  style = MaterialTheme.typography.titleMedium,
                  color = TextPrimary,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = if (userProfile.role == UserRole.ADMIN) "Authorized Administrator" else "Restricted Access",
                  style = MaterialTheme.typography.labelSmall,
                  color = if (userProfile.role == UserRole.ADMIN) ElectricCyan else TextTertiary
                )
              }
            }

            // Quick Role Switch / Unlock Button
            Button(
              onClick = {
                val nextRole = if (userProfile.role == UserRole.ADMIN) UserRole.USER else UserRole.ADMIN
                onSwitchRole(nextRole)
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = if (userProfile.role == UserRole.ADMIN) SurfaceCardElevated else NeonViolet
              ),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("admin_toggle_role_button")
            ) {
              Text(
                text = if (userProfile.role == UserRole.ADMIN) "Exit Admin" else "Admin Unlock",
                style = MaterialTheme.typography.labelSmall,
                color = TextPrimary
              )
            }
          }

          if (userProfile.role != UserRole.ADMIN) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "Sign in with admin credentials (admin@trendwave.com / admin123) or tap 'Admin Unlock' to view platform analytics, moderate content, and add songs/videos.",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          } else {
            // ADMIN DASHBOARD CONTENT
            Spacer(modifier = Modifier.height(16.dp))

            // Analytics Grid
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              AnalyticsCard(
                title = "Total Plays",
                value = "${analytics.totalPlays / 1000000}M+",
                modifier = Modifier.weight(1f)
              )
              AnalyticsCard(
                title = "Active Users",
                value = "${analytics.activeUsers / 1000}k",
                modifier = Modifier.weight(1f)
              )
              AnalyticsCard(
                title = "Revenue",
                value = "$${analytics.monthlyRevenueUsd.toInt()}",
                modifier = Modifier.weight(1f)
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Admin Sub-tabs
            TabRow(
              selectedTabIndex = adminTab,
              containerColor = SurfaceCardElevated,
              contentColor = TextPrimary,
              indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                  modifier = Modifier.tabIndicatorOffset(tabPositions[adminTab]),
                  color = ElectricCyan
                )
              }
            ) {
              Tab(
                selected = adminTab == 0,
                onClick = { adminTab = 0 },
                text = { Text("Songs (${tracks.size})", style = MaterialTheme.typography.labelSmall) }
              )
              Tab(
                selected = adminTab == 1,
                onClick = { adminTab = 1 },
                text = { Text("Videos (${videos.size})", style = MaterialTheme.typography.labelSmall) }
              )
              Tab(
                selected = adminTab == 2,
                onClick = { adminTab = 2 },
                text = { Text("Announcements", style = MaterialTheme.typography.labelSmall) }
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (adminTab) {
              0 -> {
                // Manage Songs
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text(
                    text = "Authorized Tracks",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                  )
                  Button(
                    onClick = { showAddTrackDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Song", style = MaterialTheme.typography.labelSmall)
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                tracks.take(5).forEach { track ->
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 4.dp)
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = track.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                      Text(
                        text = "${track.artist} • ${track.category}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                      )
                    }

                    // Feature toggle
                    IconButton(onClick = { onToggleFeatureTrack(track.id) }) {
                      Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Feature",
                        tint = if (track.isFeatured) ElectricCyan else TextTertiary,
                        modifier = Modifier.size(18.dp)
                      )
                    }

                    // Delete button
                    IconButton(onClick = { onDeleteTrack(track.id) }) {
                      Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = HotMagenta,
                        modifier = Modifier.size(18.dp)
                      )
                    }
                  }
                }
              }

              1 -> {
                // Manage Videos
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text(
                    text = "Authorized Videos",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                  )
                  Button(
                    onClick = { showAddVideoDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Video", style = MaterialTheme.typography.labelSmall)
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                videos.take(5).forEach { vid ->
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 4.dp)
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = vid.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                      Text(
                        text = "${vid.creator} • ${if (vid.isShort) "Short" else "16:9"} • ${vid.category}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                      )
                    }

                    IconButton(onClick = { onDeleteVideo(vid.id) }) {
                      Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = HotMagenta,
                        modifier = Modifier.size(18.dp)
                      )
                    }
                  }
                }
              }

              2 -> {
                // Manage Announcements
                Column {
                  Text(
                    text = "Broadcast App Announcement",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                  )
                  Spacer(modifier = Modifier.height(8.dp))
                  OutlinedTextField(
                    value = announcementInput,
                    onValueChange = { announcementInput = it },
                    placeholder = { Text("Type announcement for all listeners...", color = TextTertiary) },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                      focusedContainerColor = SurfaceCardElevated,
                      unfocusedContainerColor = SurfaceCardElevated,
                      focusedBorderColor = ElectricCyan,
                      unfocusedBorderColor = SurfaceCardElevated,
                      focusedTextColor = TextPrimary,
                      unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                  )
                  Spacer(modifier = Modifier.height(8.dp))
                  Button(
                    onClick = {
                      if (announcementInput.isNotBlank()) {
                        onPostAnnouncement(announcementInput)
                        announcementInput = ""
                      }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.align(Alignment.End)
                  ) {
                    Icon(Icons.Default.Campaign, null, tint = DarkNavyBg, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Broadcast Alert", color = DarkNavyBg, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Login / Switch Account Dialog
  if (showLoginDialog) {
    LoginDialog(
      onDismiss = { showLoginDialog = false },
      onLogin = { name, email, isGoogle ->
        onAuthenticate(name, email, isGoogle)
        showLoginDialog = false
      }
    )
  }

  // Add Song Dialog
  if (showAddTrackDialog) {
    AddTrackDialog(
      onDismiss = { showAddTrackDialog = false },
      onAdd = { track ->
        onAddTrack(track)
        showAddTrackDialog = false
      }
    )
  }

  // Add Video Dialog
  if (showAddVideoDialog) {
    AddVideoDialog(
      onDismiss = { showAddVideoDialog = false },
      onAdd = { video ->
        onAddVideo(video)
        showAddVideoDialog = false
      }
    )
  }
}

@Composable
fun AnalyticsCard(
  title: String,
  value: String,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceCardElevated),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = TextSecondary
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium,
        color = ElectricCyan,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@Composable
fun PreferenceRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
      .clickable(onClick = onClick)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(12.dp)
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(SurfaceCardElevated)
      ) {
        Icon(imageVector = icon, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(20.dp))
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(text = title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
        Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
      }
    }
  }
}

@Composable
fun LoginDialog(
  onDismiss: () -> Unit,
  onLogin: (String, String, Boolean) -> Unit
) {
  var email by remember { mutableStateOf("") }
  var name by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = DarkNavyBg),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .testTag("login_dialog")
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "TrendWave Account",
          style = MaterialTheme.typography.headlineMedium,
          color = TextPrimary,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Quick Google Sign-In button
        Button(
          onClick = { onLogin("Google Listener", "google.user@trendwave.io", true) },
          colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardElevated),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().height(46.dp)
        ) {
          Text("Continue with Google", color = TextPrimary)
        }

        // Quick Admin Login
        Spacer(modifier = Modifier.height(8.dp))
        Button(
          onClick = { onLogin("System Admin", "admin@trendwave.com", false) },
          colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().height(46.dp)
        ) {
          Text("Sign in as Administrator", color = TextPrimary, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(text = "— OR CUSTOM EMAIL —", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Display Name", color = TextSecondary) },
          singleLine = true,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = email,
          onValueChange = { email = it },
          label = { Text("Email Address", color = TextSecondary) },
          singleLine = true,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          horizontalArrangement = Arrangement.End,
          modifier = Modifier.fillMaxWidth()
        ) {
          TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              if (email.isNotBlank()) onLogin(name.ifBlank { "User" }, email, false)
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
          ) {
            Text("Confirm", color = DarkNavyBg, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun AddTrackDialog(
  onDismiss: () -> Unit,
  onAdd: (MediaTrack) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var artist by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Bengali") }
  var streamUrl by remember { mutableStateOf("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3") }
  var coverUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=400&q=80") }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = DarkNavyBg),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Text("Add New Song to Catalog", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Song Title", color = TextSecondary) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = artist,
          onValueChange = { artist = it },
          label = { Text("Artist Name", color = TextSecondary) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = category,
          onValueChange = { category = it },
          label = { Text("Category (Bengali, Hindi, English...)", color = TextSecondary) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
          TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              if (title.isNotBlank() && artist.isNotBlank()) {
                val newTrack = MediaTrack(
                  id = "trk_custom_${System.currentTimeMillis()}",
                  title = title,
                  artist = artist,
                  durationSeconds = 210,
                  streamUrl = streamUrl,
                  coverArtUrl = coverUrl,
                  category = category,
                  language = "bn",
                  playsCount = 1000L,
                  likesCount = 50L,
                  isFeatured = true
                )
                onAdd(newTrack)
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet)
          ) {
            Text("Publish Song")
          }
        }
      }
    }
  }
}

@Composable
fun AddVideoDialog(
  onDismiss: () -> Unit,
  onAdd: (MediaVideo) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var creator by remember { mutableStateOf("") }
  var isShort by remember { mutableStateOf(true) }
  var category by remember { mutableStateOf("Bengali") }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = DarkNavyBg),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Text("Add New Video", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Video Title", color = TextSecondary) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = creator,
          onValueChange = { creator = it },
          label = { Text("Creator Handle", color = TextSecondary) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("Vertical Short Video (9:16)", color = TextPrimary, style = MaterialTheme.typography.bodySmall)
          Spacer(modifier = Modifier.weight(1f))
          Switch(
            checked = isShort,
            onCheckedChange = { isShort = it },
            colors = SwitchDefaults.colors(checkedThumbColor = ElectricCyan)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
          TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              if (title.isNotBlank()) {
                val newVideo = MediaVideo(
                  id = "vid_custom_${System.currentTimeMillis()}",
                  title = title,
                  creator = creator.ifBlank { "@trendwave" },
                  durationSeconds = if (isShort) 45 else 240,
                  videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                  thumbnailUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=400&q=80",
                  category = category,
                  viewsCount = 1000L,
                  likesCount = 50L,
                  commentsCount = 2L,
                  description = title,
                  isShort = isShort
                )
                onAdd(newVideo)
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
          ) {
            Text("Publish Video", color = DarkNavyBg, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
