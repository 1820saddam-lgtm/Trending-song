package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.TrendWaveRepository
import com.example.model.TrendingFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("TrendWave", appName)
  }

  @Test
  fun `trendwave repository has catalog and supports operations`() {
    val repository = TrendWaveRepository()
    val tracks = repository.tracks.value
    val videos = repository.videos.value

    assertTrue("Tracks should not be empty", tracks.isNotEmpty())
    assertTrue("Videos should not be empty", videos.isNotEmpty())

    // Test filter
    val banglaTracks = repository.getTracksByFilter(TrendingFilter.BANGLADESH)
    assertTrue("Should have Bangladesh trending tracks", banglaTracks.isNotEmpty())

    // Test favorite toggle (trk_bn_2 is not in initial favorites)
    val testTrackId = "trk_bn_2"
    repository.toggleFavoriteTrack(testTrackId)
    assertTrue("Track should be added to favorites", repository.userProfile.value.favoriteTrackIds.contains(testTrackId))
    repository.toggleFavoriteTrack(testTrackId)
    assertTrue("Track should be removed from favorites", !repository.userProfile.value.favoriteTrackIds.contains(testTrackId))

    // Test custom playlist
    val playlist = repository.createCustomPlaylist("My Vibes", "Chill mix")
    assertNotNull(playlist)
    assertEquals("My Vibes", playlist.title)
  }
}
