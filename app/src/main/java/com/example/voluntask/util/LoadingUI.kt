package com.example.voluntask.util

import android.view.View

class LoadingUI(
    private val btn: View,
    private val loading: View,
    private val additionalItems: List<View>?
) {

    fun loadingToBtn(){
        btn.visibility = View.VISIBLE
        loading.visibility = View.GONE
        Hide(additionalItems)
    }

    fun btnToLoading(){
        btn.visibility = View.GONE
        loading.visibility = View.VISIBLE
        Show(additionalItems)
    }

    companion object {

        fun Hide(itemsToHide:List<View>?){
            itemsToHide?.forEach {
                it.visibility = View.GONE
            }
        }

        fun Show(itemsToShow: List<View>?){
            itemsToShow?.forEach {
                it.visibility = View.VISIBLE
            }
        }
    }
}