package com.example.rs_school_task_5.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException

class CatsPagingSource(
    private val catService: CatService
) : PagingSource<Int, Cat>() {
    override fun getRefreshKey(state: PagingState<Int, Cat>): Int? {

        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Cat> {
        try {
            val page = params.key ?: INITIAL_PAGE_NUMBER
            val pageSize = params.loadSize.coerceAtMost(MAX_PAGE_SIZE)
            val response = catService.getListOfCats(page, pageSize)

            if (response.isSuccessful) {
                val cats = checkNotNull(response.body()).map {
                    Cat(
                        it.id,
                        it.url,
                        it.width,
                        it.height
                    )
                }
                val nextKey = if (cats.size < pageSize) null else page + 1
                val prevKey = if (page == 1) null else page - 1
                return LoadResult.Page(cats, prevKey, nextKey)
            } else {
                return LoadResult.Error(HttpException(response))
            }
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    companion object {
        private const val INITIAL_PAGE_NUMBER = 1
        private const val MAX_PAGE_SIZE = 15
    }
}
