package com.client.xvideos.feature.redgifs;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import timber.log.Timber;

//https://api.redgifs.com/v2/gifs/obvioustroubledmantaray/hd.m3u8
//https://api.redgifs.com/v2/pins/easytightibisbill
//{
//        "error": {
//        "code": "NoAuthorizationData",
//        "message": "The authorization header is missing.",
//        "status": 401
//        }
//        }
//Tag  lilijunex s
