package com.android.movies.network.response

import com.google.gson.annotations.SerializedName

class ApiResponse<T> {
    @SerializedName("page")
    var page: Int? = null

    @SerializedName("results")
    var results: T? = null

    @SerializedName("total_results")
    var totalResults: Int? = null

    @SerializedName("total_pages")
    var totalPages: Int? = null


}