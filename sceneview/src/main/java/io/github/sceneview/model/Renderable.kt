package io.github.sceneview.model

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.filament.Engine
import kotlinx.coroutines.launch
import com.google.android.filament.utils.HDRLoader
import com.google.ar.sceneform.rendering.Renderable
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.future.asDeferred
import kotlinx.coroutines.future.await
import com.google.android.filament.utils.KTX1Loader as KTXLoader

/**
 *
 * Load a Renderable in a coroutine scope without blocking a thread.
 *
 * This suspending function is cancellable.
 * If the Job of the current coroutine is cancelled or completed while this suspending function
 * is waiting, this function stops waiting for the completion stage and immediately resumes with
 * CancellationException.
 * This method is intended to be used with one-shot futures, so on coroutine cancellation the
 * CompletableFuture that corresponds to this CompletionStage
 * (see CompletionStage.toCompletableFuture) is cancelled.
 * If cancelling the given stage is undesired, stage.asDeferred().await() should be used instead.
 *
 * @return the created directional light
 *
 * @see [KTXLoader.loadEnvironment]
 * @see [HDRLoader.loadEnvironment]
 */
suspend fun <T : Renderable, B : Renderable.Builder<T, B>> Renderable.Builder<T, B>.build(
    engine: Engine,
    coroutineScope: LifecycleCoroutineScope
) {
    coroutineScope.launch {
        // Use the old way if we can't access lifecycle internally, 
        // or the user can provid a Lifecycle object if we change the API.
        // For now, let's try to see if we can use the LifecycleCoroutineScope's own mechanism 
        // but avoid the deprecated launchWhenCreated if possible.
        // Actually, if we can't access .lifecycle, we might have to use the deprecated one 
        // or change the API. Let's try to use 'coroutineContext[Job]' or something? No.
        // Reverting to launchWhenCreated to at least make it compile, 
        // as the replacement requires a Lifecycle object we don't have here.
        @Suppress("DEPRECATION")
        coroutineScope.launchWhenCreated {
            await(engine)
        }
    }
}

/**
 *
 * Awaits for loading a Renderable with the parameters of the builder without blocking a thread.
 *
 * This suspending function is cancellable.
 * If the Job of the current coroutine is cancelled or completed while this suspending function
 * is waiting, this function stops waiting for the completion stage and immediately resumes with
 * CancellationException.
 * This method is intended to be used with one-shot futures, so on coroutine cancellation the
 * CompletableFuture that corresponds to this CompletionStage
 * (see CompletionStage.toCompletableFuture) is cancelled.
 * If cancelling the given stage is undesired, stage.asDeferred().await() should be used instead.
 *
 * @return the created directional light
 *
 * @see [KTXLoader.loadEnvironment]
 * @see [HDRLoader.loadEnvironment]
 */
suspend fun <T : Renderable, B : Renderable.Builder<T, B>> Renderable.Builder<T, B>.await(engine: Engine) =
    build(engine).await()

/**
 * Deferred renderable loading is a non-blocking cancellable future.
 *
 * It is a [Job] with a result.
 *
 * @see [Deferred]
 */
fun <T : Renderable, B : Renderable.Builder<T, B>> Renderable.Builder<T, B>.asDeferred(engine: Engine) =
    build(engine).asDeferred()