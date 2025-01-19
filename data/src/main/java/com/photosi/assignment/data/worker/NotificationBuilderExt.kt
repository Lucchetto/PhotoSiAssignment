package com.photosi.assignment.data.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private fun notificationPendingIntent(
    context: Context,
    intent: Intent,
    flags: Int = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
): PendingIntent = PendingIntent.getActivity(context, 0, intent, flags)

internal fun NotificationCompat.Builder.setBigContent(string: String) =
    setStyle(
        NotificationCompat.BigTextStyle().bigText(string)
    ).setContentText(string)

internal fun NotificationCompat.Builder.setTitleAndTicker(string: String) =
    setTicker(string).setContentTitle(string)

internal fun NotificationCompat.Builder.launchLauncherActivityOnClick(
    context: Context
): NotificationCompat.Builder {
    val launcherIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)!!

    return setContentIntent(notificationPendingIntent(context, launcherIntent))
}