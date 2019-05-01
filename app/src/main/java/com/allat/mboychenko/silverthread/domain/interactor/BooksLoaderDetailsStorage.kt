package com.allat.mboychenko.silverthread.com.allat.mboychenko.silverthread.domain.interactor

interface BooksLoaderDetailsStorage {

    fun getBooksLoadingIds(): Map<String, Int>
    fun putBookLoadingId(url: String, id: Int)
    fun removeIdFromBookLoadings(id: Int)

    fun requestPermissionSaveData(key: String, value: String)
    fun requestPermissionRestoreData(key: String): String?
    fun requestPermissionRemoveData(key: String)

}