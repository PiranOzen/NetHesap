package com.example.nethesap.ui.main

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdManager {
    private var interstitialAd: InterstitialAd? = null
    private var clickCounter = 0
    private const val AD_THRESHOLD = 3 // Her 3 tıklamada bir reklam göster

    // Test Interstitial Ad Unit ID
    private const val INTERSTITIAL_AD_ID = "ca-app-pub-3940256099942544/1033173712"

    fun loadInterstitialAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    fun showInterstitialWithCounter(activity: Activity, onAdDismissed: () -> Unit) {
        clickCounter++
        
        if (clickCounter >= AD_THRESHOLD && interstitialAd != null) {
            clickCounter = 0 // Sayacı sıfırla
            interstitialAd?.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitialAd(activity)
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                    interstitialAd = null
                    onAdDismissed()
                }
            }
            interstitialAd?.show(activity)
        } else {
            // Reklam gösterilmediyse (eşik aşılmadı veya reklam hazır değilse) direkt devam et
            onAdDismissed()
            // Eğer reklam null ise tekrar yüklemeyi dene
            if (interstitialAd == null) {
                loadInterstitialAd(activity)
            }
        }
    }
}
