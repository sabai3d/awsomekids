package com.baby.music;

import android.app.Activity;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdsManager {
    private static final String TAG = "main";
    private static InterstitialAd mInterstitialAd;
    private static AdsManager instance;
    private static Activity mActivity;
    private static RewardedAd mRewardedAd;


    static AdsManager getInstance(Activity activity) {
        mActivity = activity;
        if (null == instance)
            instance = new AdsManager();
        return instance;

    }


    void readyToPublish(String URL, boolean banner, boolean mrec, boolean interstitial, boolean rewarded) {

        loadAdsFromServer(URL, adUnit -> {
            if (banner)
                admobBanner(adUnit);
        }, adUnit -> {
            if (mrec)
                admobBannerMrec(adUnit);
        }, adUnit -> {
            if (interstitial)
                admobInterstitial(adUnit);
        }, adUnit -> {
            if (rewarded)
                admobRewardedAd(adUnit);
        });

    }


    private static void loadAdsFromServer(String URL, AdmobBanner banner, AdmobMrec mrec, AdmobInterstitial interstitial,
                                          AdmobRewarded rewarded) {

        mActivity.runOnUiThread(() -> {
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(mActivity);
            // Request a string response from the provided URL.
            JsonObjectRequest root = new JsonObjectRequest(URL, response -> {
                try {
                    JSONArray root1 = response.getJSONArray("com.Songs.ToyorElJaanah");
                    for (int i = 0; i < root1.length(); i++) {
                        JSONObject object = root1.getJSONObject(i);
                        // admob ads
                        String admob_banner = object.getString("admob_banner");
                        String admob_interstitial = object.getString("admob_interstitial");
                        String admob_rewarded = object.getString("admob_rewarded");
                        if (banner != null && interstitial != null && rewarded != null) {
                            banner.adUnit(admob_banner);
                            mrec.adUnit(admob_banner);
                            interstitial.adUnit(admob_interstitial);
                            rewarded.adUnit(admob_rewarded);

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                Log.d(TAG, "saw loadAdsFromServer: " + error.getMessage());
            });
            // Add the request to the RequestQueue.
            queue.add(root);
        });


    }


    private void admobBanner(String adUnit) {

        mActivity.runOnUiThread(() -> {

            MobileAds.initialize(mActivity, initializationStatus -> {
            });

            // ads adaptive size
            // Step 2 - Determine the screen width (less decorations) to use for the ad width.
            Display display = mActivity.getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);

            float widthPixels = outMetrics.widthPixels;
            float density = outMetrics.density;
            int adWidth = (int) (widthPixels / density);

            AdSize adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(mActivity, adWidth);
            //  finish

            // Initialize the Mobile Ads SDK.
            FrameLayout.LayoutParams params =
                    new FrameLayout
                            .LayoutParams(FrameLayout.LayoutParams
                            .MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM;

            AdRequest adRequest = new AdRequest.Builder().build();
            ViewGroup root = mActivity.findViewById(android.R.id.content);
            AdView adView = new AdView(mActivity);
            adView.setAdSize(adSize);
            adView.setAdListener(new AdListener() {

                @Override
                public void onAdFailedToLoad(LoadAdError adError) {
                    // Code to be executed when an ad request fails.
                    new Handler().postDelayed(() -> {
                        adView.loadAd(adRequest);
                    }, 2000);
                }


            });
            adView.setAdUnitId(adUnit);
            root.addView(adView, params);
            adView.loadAd(adRequest);

        });


    }

    private void admobBannerMrec(String adUnit) {

        mActivity.runOnUiThread(() -> {

            MobileAds.initialize(mActivity, initializationStatus -> {
            });

            AdSize adSize = new AdSize(300, 255);
            //  finish
            // Initialize the Mobile Ads SDK.
            FrameLayout.LayoutParams params =
                    new FrameLayout
                            .LayoutParams(FrameLayout.LayoutParams
                            .MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.TOP;

            AdRequest adRequest = new AdRequest.Builder().build();
            ViewGroup root = mActivity.findViewById(android.R.id.content);
            AdView adView = new AdView(mActivity);
            adView.setAdSize(adSize);
            adView.setAdListener(new AdListener() {

                @Override
                public void onAdFailedToLoad(LoadAdError adError) {
                    // Code to be executed when an ad request fails.
                    new Handler().postDelayed(() -> {
                        adView.loadAd(adRequest);
                    }, 2000);
                }


            });
            adView.setAdUnitId(adUnit);
            root.addView(adView, params);
            adView.loadAd(adRequest);

        });


    }


    private static void admobInterstitial(String adUnit) {

        mActivity.runOnUiThread(() -> {

            MobileAds.initialize(mActivity, initializationStatus -> {
            });
            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(mActivity, adUnit, adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            mInterstitialAd = interstitialAd;
                            admobInterstitial(adUnit);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            new Handler().postDelayed(() -> {
                                admobInterstitial(adUnit);
                            }, 2000);

                        }
                    });

        });

    }

    private static void admobRewardedAd(String adUnit) {

        mActivity.runOnUiThread(() -> {
            MobileAds.initialize(mActivity, initializationStatus -> {
            });
            AdRequest adRequest = new AdRequest.Builder().build();

            RewardedAd.load(mActivity, adUnit,
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            mRewardedAd = null;
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            mRewardedAd = rewardedAd;
                            Log.d(TAG, "Ad is Loaded");

                            mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    admobRewardedAd(adUnit);
                                }
                            });
                        }
                    });

        });

    }

    void admobShowRewarded() {
        if (mRewardedAd != null) {
            mRewardedAd.show(mActivity, rewardItem -> {
            });
        }
    }

    void admobShow() {
        if (mInterstitialAd != null)
            mInterstitialAd.show(mActivity);
    }


    void onBack() {
        if (mInterstitialAd != null)
            mInterstitialAd = null;
        if (mRewardedAd != null)
            mRewardedAd = null;
    }


    public interface AdmobBanner {
        void adUnit(String adUnit);
    }

    public interface AdmobInterstitial {
        void adUnit(String adUnit);
    }

    public interface AdmobRewarded {
        void adUnit(String adUnit);
    }

    public interface AdmobMrec {
        void adUnit(String adUnit);
    }

}
