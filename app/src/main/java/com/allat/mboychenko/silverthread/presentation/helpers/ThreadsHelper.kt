package com.allat.mboychenko.silverthread.presentation.helpers

import android.os.Handler
import android.os.Looper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

fun runTaskOnComputation(task: () -> Unit): Disposable =
    Observable.fromCallable(task)
        .subscribeOn(Schedulers.computation())
        .subscribe()

fun runTaskOnComputation(task: () -> Unit, onComplete: () -> Unit): Disposable =
    Observable.fromCallable(task)
        .subscribeOn(Schedulers.computation())
        .subscribe({}, {}, onComplete)

fun <T> runTaskOnBackgroundWithResult(
    executorThread: ExecutorThread = ExecutorThread.COMPUTATION,
    task: () -> T,
    onNext: (result: T) -> Unit
): Disposable {
    val executor = when (executorThread) {
        ExecutorThread.COMPUTATION -> Schedulers.computation()
        ExecutorThread.IO -> Schedulers.io()
        else -> Schedulers.newThread()
    }
    return Observable.fromCallable(task)
        .subscribeOn(executor)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { onNext(it) }
}

fun <T> runTaskOnBackground(
    executorThread: ExecutorThread = ExecutorThread.IO,
    task: () -> T
): Disposable {
    val executor = when (executorThread) {
        ExecutorThread.COMPUTATION -> Schedulers.computation()
        ExecutorThread.IO -> Schedulers.io()
        else -> Schedulers.newThread()
    }
    return Observable.fromCallable(task)
        .subscribeOn(executor)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe()
}


fun executeOnMainThread(action: () -> Unit) {
    Handler(Looper.getMainLooper()).post {
        action()
    }
}

fun executeOnHandlerThread(handler: Handler, action: () -> Unit) {
    handler.post {
        action()
    }
}

enum class ExecutorThread {
    IO,
    NEW,
    COMPUTATION
}