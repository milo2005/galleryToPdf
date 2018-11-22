package com.example.darfe.galleryapp.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class LifeDisposable(owner: LifecycleOwner): LifecycleObserver{

    private val dis:CompositeDisposable = CompositeDisposable()

    init {
        owner.lifecycle.addObserver(this)
    }


    infix fun add(disposable: Disposable){
        dis.add(disposable)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun clear(){
        dis.clear()
    }


}

fun <T> Single<T>.applySchedulers(): Single<T> = subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.applySchedulers(): Observable<T> = subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
