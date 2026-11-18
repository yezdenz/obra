package com.obra.studio.data.local

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import org.junit.Assert.*
import java.util.concurrent.TimeUnit

internal fun views(root: View): List<View> {
    val descendants = if (root is ViewGroup) (0 until root.childCount).flatMap { views(root.getChildAt(it)) }
        else emptyList()
    return listOf(root) + descendants
}
internal fun field(activity: Activity, label: String) = views(activity.window.decorView)
    .filterIsInstance<EditText>().single { it.contentDescription?.toString() == label }
internal fun click(activity: Activity, label: String) {
    val button = views(activity.window.decorView).filterIsInstance<TextView>().single { it.text.toString() == label }
    assertTrue("$label should be enabled", button.isEnabled)
    assertTrue(button.performClick())
}
internal fun <A : Activity> awaitView(scenario: ActivityScenario<A>, label: String) {
    awaitCondition("view $label") {
        var found = false
        scenario.onActivity { activity ->
            found = views(activity.window.decorView).filterIsInstance<TextView>()
                .any { it.text.toString() == label && it.isEnabled }
        }
        found
    }
}
internal fun awaitCondition(description: String, check: () -> Boolean) {
    val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(15)
    while (System.nanoTime() < deadline) {
        if (check()) return
        Thread.sleep(50)
    }
    fail("Timed out waiting for $description")
}
