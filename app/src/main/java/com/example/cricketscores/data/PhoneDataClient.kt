package com.example.cricketscores.data

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Helper used by the repository to request data from the phone when the
 * watch has no internet. It sends a message to the phone (path provided),
 * then waits (with timeout) for a response from the phone on the path "/response".
 *
 * Note: The phone side must reply on path "/response" (this matches the phone service you already use).
 */
class PhoneDataClient(private val context: Context) {

    private val TAG = "PhoneDataClient"

    /**
     * Sends [requestPath] to connected phone nodes and waits for a reply.
     *
     * Returns the raw String pushed by the phone or null on timeout / failure.
     */
    suspend fun requestFromPhone(requestPath: String, timeoutMs: Long = 8000L): String? {
        val messageClient = Wearable.getMessageClient(context)
        val responseDeferred = CompletableDeferred<String?>()

        val listener = MessageClient.OnMessageReceivedListener { event: MessageEvent ->
            // Phone replies with "/response"
            if (event.path == "$requestPath") {
                try {
                    val payload = String(event.data ?: byteArrayOf())
                    Log.d(TAG, "Got response from phone: ${payload.take(120)}")
                    if (!responseDeferred.isCompleted) responseDeferred.complete(payload)
                } catch (t: Throwable) {
                    Log.e(TAG, "Failed to read response payload", t)
                    if (!responseDeferred.isCompleted) responseDeferred.complete(null)
                }
            }
        }

        // Register listener
        messageClient.addListener(listener)

        try {
            val nodesTask = Wearable.getNodeClient(context).connectedNodes
            nodesTask.addOnSuccessListener { nodes ->
                if (nodes.isEmpty()) {
                    // no phone nodes
                    if (!responseDeferred.isCompleted) responseDeferred.complete(null)
                    return@addOnSuccessListener
                }
                nodes.forEach { node ->
                    Wearable.getMessageClient(context)
                        .sendMessage(node.id, requestPath, null)
                        .addOnSuccessListener {
                            Log.d(TAG, "Sent request to phone node: ${node.displayName} path=$requestPath")
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Failed to send request to phone node ${node.displayName}", e)
                        }
                }
            }.addOnFailureListener { e ->
                Log.e(TAG, "Failed to get connected nodes", e)
                if (!responseDeferred.isCompleted) responseDeferred.complete(null)
            }

            // Wait for response or timeout
            return withTimeoutOrNull(timeoutMs) {
                responseDeferred.await()
            }
        } finally {
            // Always remove listener
            try {
                messageClient.removeListener(listener)
            } catch (ignored: Exception) { }
        }
    }
}
